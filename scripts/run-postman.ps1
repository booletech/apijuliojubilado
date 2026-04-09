param(
  [string]$JubiladoBaseUrl = "http://localhost:8081",
  [string]$PedidoBaseUrl = "http://localhost:8080"
)

$collection = "docs/JulioApis.postman_collection.json"
$reportDir = "reports"
$htmlReport = Join-Path $reportDir "postman-report.html"
$jsonReport = Join-Path $reportDir "postman-report.json"

if (-not (Test-Path $reportDir)) {
  New-Item -ItemType Directory -Path $reportDir | Out-Null
}

Write-Host "Rodando coleção: $collection"
Write-Host "Jubilado API: $JubiladoBaseUrl"
Write-Host "Pedido API:   $PedidoBaseUrl"

npx newman run $collection `
  --env-var "jubilado_base_url=$JubiladoBaseUrl" `
  --env-var "pedido_base_url=$PedidoBaseUrl" `
  --reporters "cli,html,json" `
  --reporter-html-export $htmlReport `
  --reporter-json-export $jsonReport
