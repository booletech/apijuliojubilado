param(
  [string]$BaseUrl = "http://localhost:30081",
  [string]$Username = "admin",
  [string]$Password = "admin123"
)

$healthUrl = "$BaseUrl/actuator/health/readiness"
$loginUrl = "$BaseUrl/auth/login"
$clientesUrl = "$BaseUrl/api/clientes?page=0&size=1"

Write-Host "Verificando readiness em $healthUrl"
$health = Invoke-RestMethod -Method Get -Uri $healthUrl -TimeoutSec 30
if ($health.status -ne "UP") {
  throw "Readiness retornou status '$($health.status)'"
}

Write-Host "Autenticando em $loginUrl"
$loginBody = @{
  username = $Username
  password = $Password
} | ConvertTo-Json

$login = Invoke-RestMethod `
  -Method Post `
  -Uri $loginUrl `
  -ContentType "application/json" `
  -Body $loginBody `
  -TimeoutSec 30

if (-not $login.accessToken) {
  throw "Login nao retornou accessToken"
}

Write-Host "Chamando endpoint autenticado em $clientesUrl"
$headers = @{
  Authorization = "Bearer $($login.accessToken)"
}

$clientes = Invoke-RestMethod -Method Get -Uri $clientesUrl -Headers $headers -TimeoutSec 30
if ($null -eq $clientes.content) {
  throw "Resposta de /api/clientes nao contem o campo 'content'"
}

Write-Host "Smoke test concluido com sucesso."
