[CmdletBinding()]
param(
    [string]$BaseUrl = "http://localhost:30081",

    [string]$Username = "admin",

    [string]$Password = "admin123"
)

$ErrorActionPreference = "Stop"
if ($PSVersionTable.PSVersion.Major -ge 7) {
    $PSNativeCommandUseErrorActionPreference = $true
}

$k6Command = Get-Command k6 -ErrorAction SilentlyContinue
$dockerCommand = Get-Command docker -ErrorAction SilentlyContinue
$scriptPath = Join-Path $PSScriptRoot "api-stress-test.js"

if ($k6Command) {
    $env:BASE_URL = $BaseUrl
    $env:USERNAME = $Username
    $env:PASSWORD = $Password

    & $k6Command.Source run $scriptPath
    return
}

if ($dockerCommand) {
    $mountPath = $PSScriptRoot.Replace("\", "/")
    & $dockerCommand.Source run --rm `
        -e "BASE_URL=$BaseUrl" `
        -e "USERNAME=$Username" `
        -e "PASSWORD=$Password" `
        -v "${mountPath}:/scripts" `
        grafana/k6:0.49.0 run /scripts/api-stress-test.js
    return
}

throw "Nem o binario k6 nem o Docker estao disponiveis para executar o stress test."
