[CmdletBinding()]
param(
    [string]$Namespace,

    [ValidateSet("base", "ha")]
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
    $Namespace = if ($Profile -eq "ha") { "devops-trabalho-ha" } else { "devops-trabalho" }
}

if ([string]::IsNullOrWhiteSpace($RuntimeDirectory)) {
    $RuntimeDirectory = if ($Profile -eq "ha") {
        Join-Path $repoRoot "k8s\overlays\ha\runtime"
    } else {
        Join-Path $repoRoot "k8s\base\runtime"
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
    "frontend-service.txt"         = @("get", "svc", "frontend", "-n", $Namespace, "-o", "yaml")
    "juliojubiladoapi-service.txt" = @("get", "svc", "juliojubiladoapi", "-n", $Namespace, "-o", "yaml")
}

if ($Profile -eq "base") {
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
    "- frontend-service.txt"
    "- juliojubiladoapi-service.txt"
    "- image-summary.txt"
)

if ($Profile -eq "base") {
    $checklistContent += @(
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
