[CmdletBinding()]
param(
    [ValidateSet("base", "ha", "rubrica")]
    [string]$Profile = "base",

    [string]$Namespace,

    [string]$KubectlContext = "docker-desktop",

    [string]$KustomizePath,

    [string]$RuntimeDirectory,

    [string]$FrontendPublicUrl,

    [switch]$SkipClusterChecks,

    [switch]$CaptureEvidence
)

$ErrorActionPreference = "Stop"
if ($PSVersionTable.PSVersion.Major -ge 7) {
    $PSNativeCommandUseErrorActionPreference = $true
}

$repoRoot = (Resolve-Path (Join-Path $PSScriptRoot "..")).Path
$dockerCliPath = "C:\Program Files\Docker\Docker\resources\bin\docker.exe"
$apiNodePort = if ($Profile -eq "rubrica") { "31081" } else { "30081" }
$frontendNodePort = "30080"
$grafanaNodePort = if ($Profile -eq "rubrica") { "31300" } else { "30300" }

if ([string]::IsNullOrWhiteSpace($Namespace)) {
    $Namespace = switch ($Profile) {
        "ha" { "devops-trabalho-ha" }
        "rubrica" { "devops-trabalho-rubrica" }
        default { "devops-trabalho" }
    }
}

if ([string]::IsNullOrWhiteSpace($KustomizePath)) {
    $KustomizePath = switch ($Profile) {
        "ha" { Join-Path $repoRoot "k8s\overlays\ha" }
        "rubrica" { Join-Path $repoRoot "k8s\overlays\rubrica" }
        default { Join-Path $repoRoot "k8s\base" }
    }
}

if ([string]::IsNullOrWhiteSpace($RuntimeDirectory)) {
    $RuntimeDirectory = switch ($Profile) {
        "ha" { Join-Path $repoRoot "k8s\overlays\ha\runtime" }
        "rubrica" { Join-Path $repoRoot "k8s\overlays\rubrica\runtime" }
        default { Join-Path $repoRoot "k8s\base\runtime" }
    }
}

$deployEnvPath = Join-Path $RuntimeDirectory "deploy.env"
$runtimeEnvPath = Join-Path $RuntimeDirectory "runtime.env"
$appConfigEnvPath = Join-Path $RuntimeDirectory "app-config.env"

function Invoke-Kubectl {
    param(
        [Parameter(Mandatory = $true)]
        [string[]]$Arguments,

        [int]$MaxAttempts = 5,

        [int]$RetryDelaySeconds = 10
    )

    for ($attempt = 1; $attempt -le $MaxAttempts; $attempt++) {
        $originalNativePreference = $null
        $originalErrorActionPreference = $ErrorActionPreference
        if ($PSVersionTable.PSVersion.Major -ge 7) {
            $originalNativePreference = $PSNativeCommandUseErrorActionPreference
            $PSNativeCommandUseErrorActionPreference = $false
        }

        try {
            $ErrorActionPreference = "Continue"
            $output = @(& kubectl @Arguments 2>&1 | ForEach-Object { "$_" })
        } finally {
            $ErrorActionPreference = $originalErrorActionPreference
            if ($PSVersionTable.PSVersion.Major -ge 7) {
                $PSNativeCommandUseErrorActionPreference = $originalNativePreference
            }
        }

        if ($LASTEXITCODE -eq 0) {
            if ($output.Count -gt 0) {
                $output | Out-Host
            }

            return $output
        }

        $message = ($output | Out-String).Trim()
        $isTransient = $message -match "TLS handshake timeout|Unable to connect to the server|i/o timeout|connection refused|EOF"

        if ($isTransient -and $attempt -lt $MaxAttempts) {
            Write-Warning "Falha transitoria do kubectl ao executar 'kubectl $($Arguments -join ' ')'. Nova tentativa em $RetryDelaySeconds segundos."
            Start-Sleep -Seconds $RetryDelaySeconds
            continue
        }

        throw "kubectl $($Arguments -join ' ') falhou: $message"
    }
}

foreach ($requiredFile in @($deployEnvPath, $runtimeEnvPath)) {
    if (-not (Test-Path $requiredFile)) {
        throw "Arquivo obrigatorio ausente: $requiredFile. Gere os arquivos com .\scripts\prepare-k8s-runtime.ps1 antes do deploy."
    }
}

if ($Profile -eq "ha" -and -not (Test-Path $appConfigEnvPath)) {
    throw "Arquivo obrigatorio ausente: $appConfigEnvPath. Gere os arquivos com .\scripts\prepare-k8s-runtime.ps1 -Profile ha antes do deploy."
}

if (-not $SkipClusterChecks) {
    $availableContexts = @(& kubectl config get-contexts -o name 2>$null)
    if (-not $availableContexts -or $availableContexts.Count -eq 0) {
        throw "Nenhum contexto kubectl foi encontrado. Configure o acesso ao cluster antes do deploy."
    }

    if ($availableContexts -contains $KubectlContext) {
        $currentContext = (& kubectl config current-context 2>$null).Trim()
        if ($currentContext -ne $KubectlContext) {
            Invoke-Kubectl -Arguments @("config", "use-context", $KubectlContext) | Out-Null
        }
    } elseif ([string]::IsNullOrWhiteSpace((& kubectl config current-context 2>$null))) {
        throw "O contexto '$KubectlContext' nao existe e o kubectl esta sem contexto atual."
    }

    $effectiveContext = (& kubectl config current-context 2>$null).Trim()
    if ($effectiveContext -eq "docker-desktop" -and (Test-Path $dockerCliPath)) {
        $kubernetesStatus = & $dockerCliPath desktop kubernetes status --format json | ConvertFrom-Json
        if ($kubernetesStatus.status -ne "running") {
            throw "Docker Desktop Kubernetes esta '$($kubernetesStatus.status)'. Habilite o Kubernetes no Docker Desktop e aguarde o cluster ficar em execucao."
        }
    }
}

if ($Profile -in @("base", "rubrica")) {
    foreach ($jobName in @("sqlserver-init", "flyway-migrate")) {
        Invoke-Kubectl -Arguments @("delete", "job", $jobName, "-n", $Namespace, "--ignore-not-found") | Out-Null
    }
}

Invoke-Kubectl -Arguments @("apply", "-k", $KustomizePath, "--validate=false") | Out-Null

if ($Profile -in @("base", "rubrica")) {
    foreach ($deploymentScale in @(
        @{ Name = "juliopedidoapi"; Replicas = "0" },
        @{ Name = "juliojubiladoapi"; Replicas = "0" }
    )) {
        Invoke-Kubectl -Arguments @("scale", "deployment/$($deploymentScale.Name)", "-n", $Namespace, "--replicas=$($deploymentScale.Replicas)") | Out-Null
    }

    foreach ($deploymentName in @("sqlserver", "elasticsearch")) {
        Invoke-Kubectl -Arguments @("rollout", "status", "deployment/$deploymentName", "-n", $Namespace, "--timeout=10m") | Out-Null
    }

    foreach ($jobName in @("sqlserver-init", "flyway-migrate")) {
        Invoke-Kubectl -Arguments @("wait", "--for=condition=complete", "job/$jobName", "-n", $Namespace, "--timeout=10m") | Out-Null
    }

    $deploymentRampUp = if ($Profile -eq "rubrica") {
        @(
            @{ Name = "juliopedidoapi"; Replicas = "1" },
            @{ Name = "juliojubiladoapi"; Replicas = "1" },
            @{ Name = "juliojubiladoapi"; Replicas = "4" }
        )
    } else {
        @(
            @{ Name = "juliopedidoapi"; Replicas = "1" },
            @{ Name = "juliopedidoapi"; Replicas = "2" },
            @{ Name = "juliojubiladoapi"; Replicas = "1" },
            @{ Name = "juliojubiladoapi"; Replicas = "4" }
        )
    }

    foreach ($deploymentScale in $deploymentRampUp) {
        Invoke-Kubectl -Arguments @("scale", "deployment/$($deploymentScale.Name)", "-n", $Namespace, "--replicas=$($deploymentScale.Replicas)") | Out-Null
        Invoke-Kubectl -Arguments @("rollout", "status", "deployment/$($deploymentScale.Name)", "-n", $Namespace, "--timeout=10m") | Out-Null
    }

    if ($Profile -eq "base") {
        Invoke-Kubectl -Arguments @("rollout", "status", "deployment/frontend", "-n", $Namespace, "--timeout=10m") | Out-Null
    }

    foreach ($deploymentName in @("kube-state-metrics", "prometheus", "grafana")) {
        Invoke-Kubectl -Arguments @("rollout", "status", "deployment/$deploymentName", "-n", $Namespace, "--timeout=10m") | Out-Null
    }

    Write-Host ""
    Write-Host "Recursos atuais no namespace ${Namespace}:"
    Invoke-Kubectl -Arguments @("get", "deploy,svc,pods,pdb,pvc", "-n", $Namespace) | Out-Null

    try {
        $apiHealth = Invoke-WebRequest -Uri "http://localhost:$apiNodePort/actuator/health/readiness" -UseBasicParsing -TimeoutSec 15
        Write-Host ""
        Write-Host "API NodePort respondeu com status $($apiHealth.StatusCode)."
    } catch {
        Write-Warning "Nao foi possivel validar a API via NodePort em http://localhost:$apiNodePort ainda."
    }

    try {
        $grafanaPage = Invoke-WebRequest -Uri "http://localhost:$grafanaNodePort/" -UseBasicParsing -TimeoutSec 15
        Write-Host "Grafana NodePort respondeu com status $($grafanaPage.StatusCode)."
    } catch {
        Write-Warning "Nao foi possivel validar o Grafana via NodePort em http://localhost:$grafanaNodePort ainda."
    }

    if ($Profile -eq "base") {
        try {
            $frontendPage = Invoke-WebRequest -Uri "http://localhost:$frontendNodePort/login" -UseBasicParsing -TimeoutSec 15
            Write-Host "Frontend NodePort respondeu com status $($frontendPage.StatusCode)."
        } catch {
            Write-Warning "Nao foi possivel validar o frontend via NodePort em http://localhost:$frontendNodePort ainda."
        }

        try {
            $frontendLoginBody = @{
                username = "admin"
                password = "admin123"
            } | ConvertTo-Json

            $frontendLogin = Invoke-RestMethod `
                -Method Post `
                -Uri "http://localhost:$frontendNodePort/api/auth/login" `
                -ContentType "application/json" `
                -Body $frontendLoginBody `
                -TimeoutSec 30

            if (-not $frontendLogin.accessToken) {
                throw "O proxy do frontend nao retornou accessToken no login."
            }

            $frontendHeaders = @{
                Authorization = "Bearer $($frontendLogin.accessToken)"
            }

            $frontendClientes = Invoke-RestMethod `
                -Method Get `
                -Uri "http://localhost:$frontendNodePort/api/clientes?page=0&size=1" `
                -Headers $frontendHeaders `
                -TimeoutSec 30

            if ($null -eq $frontendClientes.content) {
                throw "O proxy do frontend nao retornou o campo 'content' para /api/clientes."
            }

            Invoke-RestMethod `
                -Method Get `
                -Uri "http://localhost:$frontendNodePort/pedido-api/api/pedidos?page=0&size=1" `
                -Headers $frontendHeaders `
                -TimeoutSec 30 | Out-Null

            Write-Host "Proxy do frontend validado com sucesso para as duas APIs."
        } catch {
            Write-Warning "Nao foi possivel validar o proxy do frontend ponta a ponta: $($_.Exception.Message)"
        }
    }
} else {
    foreach ($deploymentName in @("frontend", "juliopedidoapi", "juliojubiladoapi")) {
        Invoke-Kubectl -Arguments @("rollout", "status", "deployment/$deploymentName", "-n", $Namespace, "--timeout=10m") | Out-Null
    }

    Write-Host ""
    Write-Host "Recursos atuais no namespace ${Namespace}:"
    Invoke-Kubectl -Arguments @("get", "deploy,svc,pods,pdb", "-n", $Namespace) | Out-Null

    if ([string]::IsNullOrWhiteSpace($FrontendPublicUrl)) {
        $effectiveContext = (& kubectl config current-context 2>$null).Trim()
        if ($effectiveContext -eq "docker-desktop") {
            $FrontendPublicUrl = "http://localhost:30080"
        }
    }

    if (-not [string]::IsNullOrWhiteSpace($FrontendPublicUrl)) {
        $FrontendPublicUrl = $FrontendPublicUrl.TrimEnd("/")

        try {
            $frontendPage = Invoke-WebRequest -Uri "$FrontendPublicUrl/login" -UseBasicParsing -TimeoutSec 20
            Write-Host "Frontend HA respondeu com status $($frontendPage.StatusCode)."

            $frontendLoginBody = @{
                username = "admin"
                password = "admin123"
            } | ConvertTo-Json

            $frontendLogin = Invoke-RestMethod `
                -Method Post `
                -Uri "$FrontendPublicUrl/api/auth/login" `
                -ContentType "application/json" `
                -Body $frontendLoginBody `
                -TimeoutSec 30

            if (-not $frontendLogin.accessToken) {
                throw "O frontend HA nao retornou accessToken no login."
            }

            Write-Host "Login via frontend HA validado com sucesso."
        } catch {
            Write-Warning "Nao foi possivel validar o endpoint publico do frontend HA: $($_.Exception.Message)"
        }
    } else {
        Write-Host "Informe -FrontendPublicUrl para validar o endpoint publico do frontend HA apos o rollout."
    }
}

if ($CaptureEvidence) {
    $evidenceArguments = @{
        Namespace = $Namespace
        Profile = $Profile
    }

    $evidencePath = & (Join-Path $PSScriptRoot "collect-k8s-evidence.ps1") @evidenceArguments
    Write-Host "Evidencias salvas em: $evidencePath"
}
