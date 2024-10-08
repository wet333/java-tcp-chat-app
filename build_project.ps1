# Get the current directory
$root_dir = Get-Location

$client_dir = Join-Path $root_dir "TCPChatClient"
$server_dir = Join-Path $root_dir "TCPChatServer"

Set-Location $client_dir
mvn clean -U install

Set-Location $server_dir
mvn clean -U install

# Start the server
Set-Location (Join-Path $server_dir "target")
java -jar "TCPChatServer-1.0-SNAPSHOT.jar"

cd ..