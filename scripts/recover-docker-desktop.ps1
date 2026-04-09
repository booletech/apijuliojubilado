$ErrorActionPreference = 'Stop'

param(
    [string]$StatusPath = (Join-Path $PSScriptRoot '..\docker-recovery-status.json'),
    [int]$StartupWaitSeconds = 30
)

function Invoke-BestEffort {
    param(
        [scriptblock]$Action
    )

    try {
        & $Action
    } catch {
        Write-Warning $_
    }
}

Invoke-BestEffort { wsl.exe --shutdown }

$processNames = @(
    'Docker Desktop.exe',
    'com.docker.backend.exe',
    'com.docker.build.exe',
    'wsl.exe'
)

foreach ($processName in $processNames) {
    Invoke-BestEffort { taskkill /F /IM $processName /T | Out-Null }
}

Start-Sleep -Seconds 3

Invoke-BestEffort { Start-Service -Name vmcompute }
Invoke-BestEffort { Start-Service -Name com.docker.service }

Start-Sleep -Seconds 3

Start-Process 'C:\Program Files\Docker\Docker\Docker Desktop.exe'

Start-Sleep -Seconds $StartupWaitSeconds

$statusDirectory = Split-Path -Parent $StatusPath
if ($statusDirectory -and -not (Test-Path $statusDirectory)) {
    New-Item -ItemType Directory -Path $statusDirectory -Force | Out-Null
}

$services = Get-Service -Name com.docker.service, vmcompute, WSLService, hns |
    Select-Object Name, Status

$processes = Get-Process 'Docker Desktop', 'com.docker.backend', 'com.docker.build' -ErrorAction SilentlyContinue |
    Select-Object ProcessName, Id

[pscustomobject]@{
    Timestamp = (Get-Date).ToString('s')
    Services = $services
    Processes = $processes
} | ConvertTo-Json -Depth 4 | Set-Content -Encoding UTF8 $StatusPath
