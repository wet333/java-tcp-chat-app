# Get the current directory
$root_dir = Get-Location

$client_dir = Join-Path $root_dir "TCPChatClient"
$server_dir = Join-Path $root_dir "TCPChatServer"
$dist_dir = Join-Path $root_dir "dist"

# Create the dist directory if it doesn't exist
if (!(Test-Path -Path $dist_dir)) {
    New-Item -ItemType Directory -Path $dist_dir
}

# Build the client and server
Set-Location $client_dir
mvn clean -U install

Set-Location $server_dir
mvn clean -U install

# Copy the .jar files to the /dist folder
Copy-Item -Path (Join-Path $client_dir "target\*.jar") -Destination $dist_dir -Force
Copy-Item -Path (Join-Path $server_dir "target\*.jar") -Destination $dist_dir -Force

# Start the server
Set-Location (Join-Path $server_dir "target")
java -jar "TCPChatServer-1.0-SNAPSHOT.jar"

cd ..
