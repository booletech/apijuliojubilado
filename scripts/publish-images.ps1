[CmdletBinding()]
param(
    [Parameter(Mandatory = $true)]
    [string]$DockerHubUsername,

    [string]$DockerHubToken,

    [string]$JulioJubiladoApiRepository = "juliojubiladoapi",

    [string]$JulioPedidoApiRepository = "juliopedidoapi",

    [string]$FrontendRepository = "juliojubiladofrontend",

    [string]$FrontendApiBaseUrl = "/api",

    [string]$FrontendPedidoApiBaseUrl = "/pedido-api",

    [string]$Tag,

    [switch]$NoPush,

    [switch]$SkipLatestTag
)

$ErrorActionPreference = "Stop"
if ($PSVersionTable.PSVersion.Major -ge 7) {
    $PSNativeCommandUseErrorActionPreference = $true
}

$repoRoot = (Resolve-Path (Join-Path $PSScriptRoot "..")).Path
$dockerCommand = Get-Command docker -ErrorAction Stop

if ([string]::IsNullOrWhiteSpace($Tag)) {
    try {
        $gitCommand = Get-Command git -ErrorAction Stop
        $Tag = (& $gitCommand.Source rev-parse --short HEAD).Trim()
    } catch {
        $Tag = Get-Date -Format "yyyyMMddHHmmss"
    }
}

$julioRepo = "$DockerHubUsername/$JulioJubiladoApiRepository"
$pedidoRepo = "$DockerHubUsername/$JulioPedidoApiRepository"
$frontendRepo = "$DockerHubUsername/$FrontendRepository"
$julioVersionTag = "${julioRepo}:$Tag"
$pedidoVersionTag = "${pedidoRepo}:$Tag"
$frontendVersionTag = "${frontendRepo}:$Tag"

$julioTags = @($julioVersionTag)
$pedidoTags = @($pedidoVersionTag)
$frontendTags = @($frontendVersionTag)

if (-not $SkipLatestTag) {
    $julioTags += "${julioRepo}:latest"
    $pedidoTags += "${pedidoRepo}:latest"
    $frontendTags += "${frontendRepo}:latest"
}

if (-not $NoPush -and -not [string]::IsNullOrWhiteSpace($DockerHubToken)) {
    $DockerHubToken | & $dockerCommand.Source login --username $DockerHubUsername --password-stdin
}

Write-Host "Building JulioJubiladoapi image..."
$julioBuildArgs = @("build")
foreach ($tagValue in $julioTags) {
    $julioBuildArgs += @("-t", $tagValue)
}
$julioBuildArgs += (Join-Path $repoRoot "JulioJubiladoapi")
& $dockerCommand.Source @julioBuildArgs

Write-Host "Building juliopedidoapi image..."
$pedidoBuildArgs = @("build")
foreach ($tagValue in $pedidoTags) {
    $pedidoBuildArgs += @("-t", $tagValue)
}
$pedidoBuildArgs += (Join-Path $repoRoot "juliopedidoapi")
& $dockerCommand.Source @pedidoBuildArgs

Write-Host "Building frontend image..."
$frontendBuildArgs = @("build")
foreach ($tagValue in $frontendTags) {
    $frontendBuildArgs += @("-t", $tagValue)
}
$frontendBuildArgs += @(
    "--build-arg", "VITE_API_BASE_URL=$FrontendApiBaseUrl",
    "--build-arg", "VITE_PEDIDO_API_BASE_URL=$FrontendPedidoApiBaseUrl"
)
$frontendBuildArgs += (Join-Path $repoRoot "frontend")
& $dockerCommand.Source @frontendBuildArgs

if (-not $NoPush) {
    foreach ($tagValue in $julioTags) {
        & $dockerCommand.Source push $tagValue
    }

    foreach ($tagValue in $pedidoTags) {
        & $dockerCommand.Source push $tagValue
    }

    foreach ($tagValue in $frontendTags) {
        & $dockerCommand.Source push $tagValue
    }
}

Write-Output "FRONTEND_IMAGE=docker.io/$frontendVersionTag"
Write-Output "JULIOJUBILADOAPI_IMAGE=docker.io/$julioVersionTag"
Write-Output "JULIOPEDIDOAPI_IMAGE=docker.io/$pedidoVersionTag"
