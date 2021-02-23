$db = 'abms'
$user = 'abms'
$pass = 'abms'

$pgpass = $args[0]

while (!$pgpass) {
    $pgpass = Read-Host "Please enter the password for the 'postgres' user"
}

$env:PGPASSWORD = $pgpass

Write-Host "Dropping database '$db' (if exists)"
dropdb -U postgres $db

Write-Host "Creating database '$db'"
createdb -U postgres $db

Write-Host "Creating user '$user' with password '$pass'"
psql -U postgres -c "create user $user with password '$pass';" $db

Write-Host "Granting all privileges to user $user on $db"
psql -U postgres -c "grant all privileges on database ""$db"" to $user;" $db

Write-Host "Creating schema $user"
psql -U postgres -d abms -c "create schema abms authorization abms;"

Write-Host "Creating extension postgis"
psql -U postgres -d abms -c "create extension if not exists postgis schema public;"

Write-Host "Complete!"
