[CmdletBinding()]
param(
    [ValidateSet("base", "ha")]
    [string]$Profile = "base",

    [Parameter(Mandatory = $true)]
    [string]$FrontendImage,

    [Parameter(Mandatory = $true)]
    [string]$JulioJubiladoApiImage,

    [Parameter(Mandatory = $true)]
    [string]$JulioPedidoApiImage,

    [string]$SqlServerSaPassword,

    [string]$DbAppUsername,

    [string]$DbAppPassword,

    [string]$DbMigrationUsername,

    [string]$DbMigrationPassword,

    [string]$JwtSecret,

    [string]$GrafanaAdminUser = "admin",

    [string]$GrafanaAdminPassword,

    [string]$RuntimeDirectory,

    [string]$SpringProfilesActive,

    [string]$DbHost,

    [string]$DbPort = "1433",

    [string]$DbName,

    [string]$DbEncrypt,

    [string]$DbTrustServerCertificate,

    [string]$JulioPedidoApiUrl = "http://juliopedidoapi:8080",

    [string]$JulioPedidoApiServiceSubject = "service-juliopedidoapi",

    [string]$ElasticsearchUrl,

    [string]$ElasticsearchPedidoIndex = "pedido",

    [string]$JwtIssuer = "JulioApis",

    [string]$JwtExpirationMinutes = "60",

    [string]$AppCorsAllowedOrigins,

    [string]$AppSeedDefaultUsers,

    [string]$ViaCepApiUrl = "https://viacep.com.br/",

    [string]$FipeApiUrl = "https://fipe.parallelum.com.br/api/v2",

    [string]$PedidoApiAdminUsername = "admin",

    [string]$PedidoApiSupervisorUsername = "supervisor",

    [string]$PedidoApiFuncionarioUsername = "funcionario",

    [string]$PedidoApiTotemUsername = "totem",

    [string]$JavaToolOptions
)

$ErrorActionPreference = "Stop"

$repoRoot = (Resolve-Path (Join-Path $PSScriptRoot "..")).Path

if ([string]::IsNullOrWhiteSpace($RuntimeDirectory)) {
    $RuntimeDirectory = if ($Profile -eq "ha") {
        Join-Path $repoRoot "k8s\overlays\ha\runtime"
    } else {
        Join-Path $repoRoot "k8s\base\runtime"
    }
}

if ([string]::IsNullOrWhiteSpace($DbAppUsername)) {
    $DbAppUsername = if ($Profile -eq "ha") { "julio_prod_app" } else { "julio_dev_app" }
}

if ([string]::IsNullOrWhiteSpace($DbMigrationUsername)) {
    $DbMigrationUsername = if ($Profile -eq "ha") { "julio_prod_migrator" } else { "julio_dev_migrator" }
}

if ([string]::IsNullOrWhiteSpace($SpringProfilesActive)) {
    $SpringProfilesActive = if ($Profile -eq "ha") { "prod" } else { "dev" }
}

if ([string]::IsNullOrWhiteSpace($DbName)) {
    $DbName = if ($Profile -eq "ha") { "julio_prod" } else { "julio_dev" }
}

if ([string]::IsNullOrWhiteSpace($DbEncrypt)) {
    $DbEncrypt = "true"
}

if ([string]::IsNullOrWhiteSpace($DbTrustServerCertificate)) {
    $DbTrustServerCertificate = if ($Profile -eq "ha") { "false" } else { "true" }
}

if ([string]::IsNullOrWhiteSpace($AppCorsAllowedOrigins)) {
    $AppCorsAllowedOrigins = if ($Profile -eq "ha") {
        "https://julio.example.com"
    } else {
        "http://localhost:5173,http://127.0.0.1:5173,http://localhost:30080,http://127.0.0.1:30080"
    }
}

if ([string]::IsNullOrWhiteSpace($AppSeedDefaultUsers)) {
    $AppSeedDefaultUsers = if ($Profile -eq "ha") { "false" } else { "true" }
}

if ([string]::IsNullOrWhiteSpace($JavaToolOptions)) {
    $JavaToolOptions = if ($Profile -eq "ha") { "-Xms128m -Xmx192m" } else { "-Xms128m -Xmx192m" }
}

if ([string]::IsNullOrWhiteSpace($DbAppPassword)) {
    throw "Parametro obrigatorio ausente: DbAppPassword"
}

if ([string]::IsNullOrWhiteSpace($JwtSecret)) {
    throw "Parametro obrigatorio ausente: JwtSecret"
}

if ($Profile -eq "base") {
    foreach ($requiredPair in @(
        @{ Name = "SqlServerSaPassword"; Value = $SqlServerSaPassword },
        @{ Name = "DbMigrationPassword"; Value = $DbMigrationPassword },
        @{ Name = "GrafanaAdminPassword"; Value = $GrafanaAdminPassword }
    )) {
        if ([string]::IsNullOrWhiteSpace($requiredPair.Value)) {
            throw "Parametro obrigatorio ausente para o perfil base: $($requiredPair.Name)"
        }
    }
} else {
    foreach ($requiredPair in @(
        @{ Name = "DbHost"; Value = $DbHost },
        @{ Name = "ElasticsearchUrl"; Value = $ElasticsearchUrl }
    )) {
        if ([string]::IsNullOrWhiteSpace($requiredPair.Value)) {
            throw "Parametro obrigatorio ausente para o perfil ha: $($requiredPair.Name)"
        }
    }
}

New-Item -ItemType Directory -Force -Path $RuntimeDirectory | Out-Null

$deployEnvPath = Join-Path $RuntimeDirectory "deploy.env"
$runtimeEnvPath = Join-Path $RuntimeDirectory "runtime.env"
$appConfigEnvPath = Join-Path $RuntimeDirectory "app-config.env"

$deployEnvContent = @(
    "FRONTEND_IMAGE=$FrontendImage"
    "JULIOJUBILADOAPI_IMAGE=$JulioJubiladoApiImage"
    "JULIOPEDIDOAPI_IMAGE=$JulioPedidoApiImage"
)

$runtimeEnvContent = [System.Collections.Generic.List[string]]::new()
$runtimeEnvContent.Add("DB_APP_USERNAME=$DbAppUsername")
$runtimeEnvContent.Add("DB_APP_PASSWORD=$DbAppPassword")
$runtimeEnvContent.Add("JWT_SECRET=$JwtSecret")

if ($Profile -eq "base") {
    $runtimeEnvContent.Add("SQLSERVER_SA_PASSWORD=$SqlServerSaPassword")
    $runtimeEnvContent.Add("DB_MIGRATION_USERNAME=$DbMigrationUsername")
    $runtimeEnvContent.Add("DB_MIGRATION_PASSWORD=$DbMigrationPassword")
    $runtimeEnvContent.Add("GRAFANA_ADMIN_USER=$GrafanaAdminUser")
    $runtimeEnvContent.Add("GRAFANA_ADMIN_PASSWORD=$GrafanaAdminPassword")
} elseif (-not [string]::IsNullOrWhiteSpace($DbMigrationPassword)) {
    $runtimeEnvContent.Add("DB_MIGRATION_USERNAME=$DbMigrationUsername")
    $runtimeEnvContent.Add("DB_MIGRATION_PASSWORD=$DbMigrationPassword")
}

Set-Content -Path $deployEnvPath -Value $deployEnvContent -Encoding ascii
Set-Content -Path $runtimeEnvPath -Value $runtimeEnvContent -Encoding ascii

if ($Profile -eq "ha") {
    $appConfigContent = @(
        "SPRING_PROFILES_ACTIVE=$SpringProfilesActive"
        "DB_HOST=$DbHost"
        "DB_PORT=$DbPort"
        "DB_NAME=$DbName"
        "DB_ENCRYPT=$DbEncrypt"
        "DB_TRUST_SERVER_CERTIFICATE=$DbTrustServerCertificate"
        "JULIOPEDIDOAPI_URL=$JulioPedidoApiUrl"
        "JULIOPEDIDOAPI_SERVICE_SUBJECT=$JulioPedidoApiServiceSubject"
        "ELASTICSEARCH_URL=$ElasticsearchUrl"
        "ELASTICSEARCH_PEDIDO_INDEX=$ElasticsearchPedidoIndex"
        "JWT_ISSUER=$JwtIssuer"
        "JWT_EXPIRATION_MINUTES=$JwtExpirationMinutes"
        "APP_CORS_ALLOWED_ORIGINS=$AppCorsAllowedOrigins"
        "APP_SEED_DEFAULT_USERS=$AppSeedDefaultUsers"
        "VIACEP_API_URL=$ViaCepApiUrl"
        "FIPE_API_URL=$FipeApiUrl"
        "PEDIDO_API_ADMIN_USERNAME=$PedidoApiAdminUsername"
        "PEDIDO_API_SUPERVISOR_USERNAME=$PedidoApiSupervisorUsername"
        "PEDIDO_API_FUNCIONARIO_USERNAME=$PedidoApiFuncionarioUsername"
        "PEDIDO_API_TOTEM_USERNAME=$PedidoApiTotemUsername"
        "JAVA_TOOL_OPTIONS=$JavaToolOptions"
    )

    Set-Content -Path $appConfigEnvPath -Value $appConfigContent -Encoding ascii
    Write-Output "app-config.env => $appConfigEnvPath"
}

Write-Output "deploy.env => $deployEnvPath"
Write-Output "runtime.env => $runtimeEnvPath"
