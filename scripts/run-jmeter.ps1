[CmdletBinding()]
param(
    [string]$BaseUrl = "http://localhost:30081",

    [string]$Username = "admin",

    [string]$Password = "admin123",

    [int]$Threads = 20,

    [int]$RampUp = 30,

    [int]$Loops = 20
)

$ErrorActionPreference = "Stop"
if ($PSVersionTable.PSVersion.Major -ge 7) {
    $PSNativeCommandUseErrorActionPreference = $true
}

$repoRoot = (Resolve-Path (Join-Path $PSScriptRoot "..")).Path
$testPlan = Join-Path $repoRoot "jmeter\julio-devops-test-plan.jmx"
$resultsDir = Join-Path $repoRoot "reports\jmeter"
$resultsFile = Join-Path $resultsDir "results.jtl"
$dashboardDir = Join-Path $resultsDir "dashboard"

New-Item -ItemType Directory -Force -Path $resultsDir | Out-Null
if (Test-Path $resultsFile) {
    Remove-Item -LiteralPath $resultsFile -Force
}
if (Test-Path $dashboardDir) {
    Remove-Item -LiteralPath $dashboardDir -Recurse -Force
}

$jmeterCommand = Get-Command jmeter -ErrorAction SilentlyContinue
if ($jmeterCommand) {
    $jmeterArgs = @(
        "-n"
        "-t", $testPlan
        "-l", $resultsFile
        "-e"
        "-o", $dashboardDir
        "-JBASE_URL=$BaseUrl"
        "-JUSERNAME=$Username"
        "-JPASSWORD=$Password"
        "-JTHREADS=$Threads"
        "-JRAMP_UP=$RampUp"
        "-JLOOPS=$Loops"
    )
    & $jmeterCommand.Source `
        @jmeterArgs
    return
}

$dockerCommand = Get-Command docker -ErrorAction SilentlyContinue
if ($dockerCommand) {
    $repoMount = $repoRoot.Replace("\", "/")
    $dockerArgs = @(
        "run"
        "--rm"
        "-v", "${repoMount}:/test"
        "justb4/jmeter:5.5"
        "-n"
        "-t", "/test/jmeter/julio-devops-test-plan.jmx"
        "-l", "/test/reports/jmeter/results.jtl"
        "-e"
        "-o", "/test/reports/jmeter/dashboard"
        "-JBASE_URL=$BaseUrl"
        "-JUSERNAME=$Username"
        "-JPASSWORD=$Password"
        "-JTHREADS=$Threads"
        "-JRAMP_UP=$RampUp"
        "-JLOOPS=$Loops"
    )
    & $dockerCommand.Source `
        @dockerArgs
    return
}

throw "Nem o JMeter local nem o Docker estao disponiveis para executar o teste."
