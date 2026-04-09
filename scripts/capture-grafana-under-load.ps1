[CmdletBinding()]
param(
    [ValidateSet("base", "rubrica")]
    [string]$Profile = "rubrica",

    [string]$Namespace,

    [string]$ApiBaseUrl,

    [string]$GrafanaUrl,

    [string]$OutputDirectory,

    [int]$WarmupSeconds = 45,

    [switch]$UsePortForward
)

$ErrorActionPreference = "Stop"
if ($PSVersionTable.PSVersion.Major -ge 7) {
    $PSNativeCommandUseErrorActionPreference = $true
}

$repoRoot = (Resolve-Path (Join-Path $PSScriptRoot "..")).Path

if ([string]::IsNullOrWhiteSpace($Namespace)) {
    $Namespace = if ($Profile -eq "rubrica") { "devops-trabalho-rubrica" } else { "devops-trabalho" }
}

if ([string]::IsNullOrWhiteSpace($ApiBaseUrl)) {
    $ApiBaseUrl = if ($Profile -eq "rubrica") { "http://localhost:31081" } else { "http://localhost:30081" }
}

if ([string]::IsNullOrWhiteSpace($GrafanaUrl)) {
    $GrafanaUrl = if ($Profile -eq "rubrica") { "http://localhost:31300" } else { "http://localhost:30300" }
}

if ([string]::IsNullOrWhiteSpace($OutputDirectory)) {
    $OutputDirectory = Join-Path $repoRoot ("docs\evidencias\" + (Get-Date -Format "yyyyMMdd-HHmmss"))
}

New-Item -ItemType Directory -Force -Path $OutputDirectory | Out-Null

$edgeCandidates = @(
    "C:\Program Files (x86)\Microsoft\Edge\Application\msedge.exe"
    "C:\Program Files\Microsoft\Edge\Application\msedge.exe"
)
$edgePath = $edgeCandidates | Where-Object { Test-Path $_ } | Select-Object -First 1
if (-not $edgePath) {
    throw "Nao foi encontrado o Microsoft Edge para gerar a captura do dashboard."
}

$apiTargetUrl = $ApiBaseUrl.TrimEnd("/")
$grafanaTargetUrl = $GrafanaUrl.TrimEnd("/")
$apiPortForwardProcess = $null
$portForwardProcess = $null
$stressProcess = $null

try {
    if ($UsePortForward) {
        $apiTargetUrl = "http://127.0.0.1:33081"
        $grafanaTargetUrl = "http://127.0.0.1:33000"
        $apiPortForwardArgs = @(
            "-NoProfile"
            "-Command"
            "kubectl port-forward svc/juliojubiladoapi 33081:8081 -n $Namespace"
        )
        $apiPortForwardProcess = Start-Process `
            -FilePath "powershell.exe" `
            -ArgumentList $apiPortForwardArgs `
            -PassThru `
            -WindowStyle Hidden

        $portForwardArgs = @(
            "-NoProfile"
            "-Command"
            "kubectl port-forward svc/grafana 33000:3000 -n $Namespace"
        )
        $portForwardProcess = Start-Process `
            -FilePath "powershell.exe" `
            -ArgumentList $portForwardArgs `
            -PassThru `
            -WindowStyle Hidden

        $apiReady = $false
        foreach ($attempt in 1..20) {
            try {
                Invoke-WebRequest `
                    -Uri "$apiTargetUrl/actuator/health/readiness" `
                    -UseBasicParsing `
                    -TimeoutSec 5 | Out-Null
                $apiReady = $true
                break
            } catch {
                Start-Sleep -Seconds 2
            }
        }
        if (-not $apiReady) {
            throw "Nao foi possivel estabelecer o port-forward da API em $apiTargetUrl."
        }

        $grafanaReady = $false
        foreach ($attempt in 1..20) {
            try {
                Invoke-WebRequest `
                    -Uri $grafanaTargetUrl `
                    -UseBasicParsing `
                    -TimeoutSec 5 | Out-Null
                $grafanaReady = $true
                break
            } catch {
                Start-Sleep -Seconds 2
            }
        }
        if (-not $grafanaReady) {
            throw "Nao foi possivel estabelecer o port-forward do Grafana em $grafanaTargetUrl."
        }
    }

    $stressArgs = @(
        "-ExecutionPolicy", "Bypass",
        "-File", (Join-Path $PSScriptRoot "run-stress-test.ps1"),
        "-BaseUrl", $apiTargetUrl
    )

    $stressProcess = Start-Process `
        -FilePath "powershell.exe" `
        -ArgumentList $stressArgs `
        -PassThru `
        -WindowStyle Hidden

    Start-Sleep -Seconds $WarmupSeconds

    $screenshotPath = Join-Path $OutputDirectory "grafana-under-load.png"
    $edgeArgs = @(
        "--headless",
        "--disable-gpu",
        "--hide-scrollbars",
        "--window-size=1600,1200",
        "--virtual-time-budget=10000",
        "--screenshot=$screenshotPath",
        $grafanaTargetUrl
    )

    & $edgePath @edgeArgs | Out-Null

    if (-not (Test-Path $screenshotPath)) {
        throw "A captura do dashboard nao foi gerada em $screenshotPath."
    }

    Write-Output $screenshotPath
} finally {
    foreach ($process in @($stressProcess, $portForwardProcess, $apiPortForwardProcess)) {
        if ($null -ne $process) {
            try {
                if (-not $process.HasExited) {
                    Stop-Process -Id $process.Id -Force
                }
            } catch {
            }
        }
    }
}
