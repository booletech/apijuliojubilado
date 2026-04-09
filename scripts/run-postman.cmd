@echo off
setlocal

set "JUBILADO_BASE_URL=%~1"
set "PEDIDO_BASE_URL=%~2"

if "%JUBILADO_BASE_URL%"=="" set "JUBILADO_BASE_URL=http://localhost:8081"
if "%PEDIDO_BASE_URL%"=="" set "PEDIDO_BASE_URL=http://localhost:8080"

set "REPORT_DIR=reports"
set "HTML_REPORT=%REPORT_DIR%\\postman-report.html"
set "JSON_REPORT=%REPORT_DIR%\\postman-report.json"

if not exist "%REPORT_DIR%" mkdir "%REPORT_DIR%"

echo Rodando colecao: docs/JulioApis.postman_collection.json
echo Jubilado API: %JUBILADO_BASE_URL%
echo Pedido API:   %PEDIDO_BASE_URL%

npx newman run docs/JulioApis.postman_collection.json ^
  --env-var "jubilado_base_url=%JUBILADO_BASE_URL%" ^
  --env-var "pedido_base_url=%PEDIDO_BASE_URL%" ^
  --reporters "cli,html,json" ^
  --reporter-html-export "%HTML_REPORT%" ^
  --reporter-json-export "%JSON_REPORT%"
