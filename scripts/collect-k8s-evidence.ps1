[CmdletBinding()]
param(
    [string]$Namespace,

    [ValidateSet("base", "ha", "rubrica")]
    [string]$Profile = "base",

    [string]$RuntimeDirectory,

    [string]$OutputDirectory
)

$ErrorActionPreference = "Stop"
if ($PSVersionTable.PSVersion.Major -ge 7) {
    $PSNativeCommandUseErrorActionPreference = $true
}

$repoRoot = (Resolve-Path (Join-Path $PSScriptRoot "..")).Path

if ([string]::IsNullOrWhiteSpace($Namespace)) {
    $Namespace = switch ($Profile) {
        "ha" { "devops-trabalho-ha" }
        "rubrica" { "devops-trabalho-rubrica" }
        default { "devops-trabalho" }
    }
}

if ([string]::IsNullOrWhiteSpace($RuntimeDirectory)) {
    $RuntimeDirectory = switch ($Profile) {
        "ha" { Join-Path $repoRoot "k8s\overlays\ha\runtime" }
        "rubrica" { Join-Path $repoRoot "k8s\overlays\rubrica\runtime" }
        default { Join-Path $repoRoot "k8s\base\runtime" }
    }
}

if ([string]::IsNullOrWhiteSpace($OutputDirectory)) {
    $OutputDirectory = Join-Path $repoRoot ("docs\evidencias\" + (Get-Date -Format "yyyyMMdd-HHmmss"))
}

New-Item -ItemType Directory -Force -Path $OutputDirectory | Out-Null

$deployEnvPath = Join-Path $RuntimeDirectory "deploy.env"
$runtimeEnvPath = Join-Path $RuntimeDirectory "runtime.env"
$appConfigEnvPath = Join-Path $RuntimeDirectory "app-config.env"

$kubectlOutputs = [ordered]@{
    "deploy-svc-pods-pdb.txt"      = @("get", "deploy,svc,pods,pdb", "-n", $Namespace)
    "namespace-events.txt"         = @("get", "events", "-n", $Namespace, "--sort-by=.lastTimestamp")
    "juliojubiladoapi-service.txt" = @("get", "svc", "juliojubiladoapi", "-n", $Namespace, "-o", "yaml")
}

if ($Profile -eq "base") {
    $kubectlOutputs["frontend-service.txt"] = @("get", "svc", "frontend", "-n", $Namespace, "-o", "yaml")
    $kubectlOutputs["persistent-volumes.txt"] = @("get", "pv")
    $kubectlOutputs["deploy-svc-pods-pdb-pvc.txt"] = @("get", "deploy,svc,pods,pdb,pvc", "-n", $Namespace)
    $kubectlOutputs["grafana-service.txt"] = @("get", "svc", "grafana", "-n", $Namespace, "-o", "yaml")
} elseif ($Profile -eq "rubrica") {
    $kubectlOutputs["persistent-volumes.txt"] = @("get", "pv")
    $kubectlOutputs["deploy-svc-pods-pdb-pvc.txt"] = @("get", "deploy,svc,pods,pdb,pvc", "-n", $Namespace)
    $kubectlOutputs["grafana-service.txt"] = @("get", "svc", "grafana", "-n", $Namespace, "-o", "yaml")
}

foreach ($entry in $kubectlOutputs.GetEnumerator()) {
    $outputPath = Join-Path $OutputDirectory $entry.Key
    & kubectl @($entry.Value) | Set-Content -Path $outputPath -Encoding utf8
}

if (Test-Path $deployEnvPath) {
    Copy-Item -Path $deployEnvPath -Destination (Join-Path $OutputDirectory "deploy.env") -Force
    Get-Content -Path $deployEnvPath | Set-Content -Path (Join-Path $OutputDirectory "image-summary.txt") -Encoding ascii
}

if (Test-Path $runtimeEnvPath) {
    $safeRuntime = Get-Content -Path $runtimeEnvPath | ForEach-Object {
        if ($_ -match "^[A-Z0-9_]+=") {
            $key = $_.Split("=", 2)[0]
            "$key=***"
        } else {
            $_
        }
    }
    $safeRuntime | Set-Content -Path (Join-Path $OutputDirectory "runtime.env.masked") -Encoding ascii
}

if (Test-Path $appConfigEnvPath) {
    Copy-Item -Path $appConfigEnvPath -Destination (Join-Path $OutputDirectory "app-config.env") -Force
}

$checklistContent = @(
    "# Evidencias da entrega"
    ""
    "Perfil auditado: $Profile"
    ""
    "Arquivos gerados automaticamente:"
    "- deploy-svc-pods-pdb.txt"
    "- namespace-events.txt"
    "- juliojubiladoapi-service.txt"
    "- image-summary.txt"
)

if ($Profile -eq "base") {
    $checklistContent += @(
        "- frontend-service.txt"
        "- persistent-volumes.txt"
        "- deploy-svc-pods-pdb-pvc.txt"
        "- grafana-service.txt"
        ""
        "Itens para capturar manualmente:"
        "- [ ] Print do frontend respondendo pelo NodePort"
        "- [ ] Print do Grafana antes do stress test"
        "- [ ] Print do Grafana durante o stress test"
        "- [ ] Print do pipeline concluido"
        "- [ ] Print da API principal respondendo via NodePort"
        "- [ ] Print dos PDBs e replicas em execucao"
        ""
        "URLs esperadas:"
        "- Frontend: http://localhost:30080"
        "- API principal: http://localhost:30081"
        "- Grafana: http://localhost:30300"
    )
} elseif ($Profile -eq "rubrica") {
    $checklistContent += @(
        "- persistent-volumes.txt"
        "- deploy-svc-pods-pdb-pvc.txt"
        "- grafana-service.txt"
        ""
        "Itens para capturar manualmente:"
        "- [ ] Print da API principal respondendo pelo NodePort"
        "- [ ] Print do Grafana antes do stress test"
        "- [ ] Print do Grafana durante o stress test"
        "- [ ] Print do Deployment da API principal com 4 replicas"
        "- [ ] Print do SQL Server com Service ClusterIP"
        "- [ ] Print do pipeline concluido"
        ""
        "URLs esperadas:"
        "- API principal: http://localhost:31081"
        "- Grafana: http://localhost:31300"
    )
} else {
    $checklistContent += @(
        "- app-config.env"
        ""
        "Itens para capturar manualmente:"
        "- [ ] Print do frontend HA respondendo pelo endpoint publico"
        "- [ ] Print dos Deployments com replicas prontas"
        "- [ ] Print dos PodDisruptionBudgets"
        "- [ ] Print da distribuicao dos pods por node"
        "- [ ] Print do rollout concluido"
        ""
        "Comandos sugeridos para a banca:"
        "- kubectl get deploy,svc,pods,pdb -n $Namespace"
        "- kubectl describe pdb frontend -n $Namespace"
        "- kubectl describe pdb juliopedidoapi -n $Namespace"
        "- kubectl describe pdb juliojubiladoapi -n $Namespace"
    )
}

Set-Content -Path (Join-Path $OutputDirectory "README.md") -Value $checklistContent -Encoding utf8

Write-Output $OutputDirectory
