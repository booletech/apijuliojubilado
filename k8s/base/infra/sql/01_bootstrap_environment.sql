IF DB_ID(N'$(DB_NAME)') IS NULL
BEGIN
    DECLARE @createDatabaseSql nvarchar(max) =
        N'CREATE DATABASE ' + QUOTENAME(N'$(DB_NAME)');
    EXEC (@createDatabaseSql);
END;
GO

DECLARE @appLogin sysname = N'$(DB_APP_USERNAME)';
DECLARE @appPassword nvarchar(256) = N'$(DB_APP_PASSWORD)';
DECLARE @sql nvarchar(max);

IF EXISTS (SELECT 1 FROM sys.sql_logins WHERE name = @appLogin)
BEGIN
    SET @sql =
        N'ALTER LOGIN ' + QUOTENAME(@appLogin) +
        N' WITH PASSWORD = ' + QUOTENAME(@appPassword, '''') +
        N', CHECK_POLICY = OFF, CHECK_EXPIRATION = OFF;';
END
ELSE
BEGIN
    SET @sql =
        N'CREATE LOGIN ' + QUOTENAME(@appLogin) +
        N' WITH PASSWORD = ' + QUOTENAME(@appPassword, '''') +
        N', CHECK_POLICY = OFF, CHECK_EXPIRATION = OFF;';
END;

EXEC (@sql);
GO

DECLARE @migrationLogin sysname = N'$(DB_MIGRATION_USERNAME)';
DECLARE @migrationPassword nvarchar(256) = N'$(DB_MIGRATION_PASSWORD)';
DECLARE @sql nvarchar(max);

IF EXISTS (SELECT 1 FROM sys.sql_logins WHERE name = @migrationLogin)
BEGIN
    SET @sql =
        N'ALTER LOGIN ' + QUOTENAME(@migrationLogin) +
        N' WITH PASSWORD = ' + QUOTENAME(@migrationPassword, '''') +
        N', CHECK_POLICY = OFF, CHECK_EXPIRATION = OFF;';
END
ELSE
BEGIN
    SET @sql =
        N'CREATE LOGIN ' + QUOTENAME(@migrationLogin) +
        N' WITH PASSWORD = ' + QUOTENAME(@migrationPassword, '''') +
        N', CHECK_POLICY = OFF, CHECK_EXPIRATION = OFF;';
END;

EXEC (@sql);
GO

DECLARE @dbName sysname = N'$(DB_NAME)';
DECLARE @appUser sysname = N'$(DB_APP_USERNAME)';
DECLARE @appUserLiteral nvarchar(512) = REPLACE(@appUser, '''', '''''');
DECLARE @sql nvarchar(max);

SET @sql =
    N'USE ' + QUOTENAME(@dbName) + N';
IF NOT EXISTS (SELECT 1 FROM sys.database_principals WHERE name = N''' + @appUserLiteral + N''')
BEGIN
    CREATE USER ' + QUOTENAME(@appUser) + N' FOR LOGIN ' + QUOTENAME(@appUser) + N';
END;
IF NOT EXISTS (
    SELECT 1
    FROM sys.database_role_members drm
    JOIN sys.database_principals rp ON drm.role_principal_id = rp.principal_id
    JOIN sys.database_principals mp ON drm.member_principal_id = mp.principal_id
    WHERE rp.name = N''db_datareader'' AND mp.name = N''' + @appUserLiteral + N'''
)
BEGIN
    ALTER ROLE [db_datareader] ADD MEMBER ' + QUOTENAME(@appUser) + N';
END;
IF NOT EXISTS (
    SELECT 1
    FROM sys.database_role_members drm
    JOIN sys.database_principals rp ON drm.role_principal_id = rp.principal_id
    JOIN sys.database_principals mp ON drm.member_principal_id = mp.principal_id
    WHERE rp.name = N''db_datawriter'' AND mp.name = N''' + @appUserLiteral + N'''
)
BEGIN
    ALTER ROLE [db_datawriter] ADD MEMBER ' + QUOTENAME(@appUser) + N';
END;';

EXEC (@sql);
GO

DECLARE @dbName sysname = N'$(DB_NAME)';
DECLARE @migrationUser sysname = N'$(DB_MIGRATION_USERNAME)';
DECLARE @migrationUserLiteral nvarchar(512) = REPLACE(@migrationUser, '''', '''''');
DECLARE @sql nvarchar(max);

SET @sql =
    N'USE ' + QUOTENAME(@dbName) + N';
IF NOT EXISTS (SELECT 1 FROM sys.database_principals WHERE name = N''' + @migrationUserLiteral + N''')
BEGIN
    CREATE USER ' + QUOTENAME(@migrationUser) + N' FOR LOGIN ' + QUOTENAME(@migrationUser) + N';
END;
IF NOT EXISTS (
    SELECT 1
    FROM sys.database_role_members drm
    JOIN sys.database_principals rp ON drm.role_principal_id = rp.principal_id
    JOIN sys.database_principals mp ON drm.member_principal_id = mp.principal_id
    WHERE rp.name = N''db_owner'' AND mp.name = N''' + @migrationUserLiteral + N'''
)
BEGIN
    ALTER ROLE [db_owner] ADD MEMBER ' + QUOTENAME(@migrationUser) + N';
END;';

EXEC (@sql);
GO
