#!/bin/bash

root_dir=$(pwd)

client_dir=$root_dir/TCPChatClient
server_dir=$root_dir/TCPChatServer
dist_dev_dir=$root_dir/dist

# Build all modules
cd "$client_dir" && mvn clean -U install
cd "$server_dir" && mvn clean -U install

# Create dist/dev directory if it doesn't exist
mkdir -p "$dist_dev_dir"

# Copy JAR artifacts to dist/ with version numbers
client_jar=$(ls "$client_dir/target"/TCPChatClient-*.jar | head -1)
server_jar=$(ls "$server_dir/target"/TCPChatServer-*.jar | head -1)

client_version=$(basename "$client_jar" | sed 's/TCPChatClient-//' | sed 's/.jar//')
server_version=$(basename "$server_jar" | sed 's/TCPChatServer-//' | sed 's/.jar//')

cp "$client_jar" "$dist_dev_dir/Client_$client_version.jar"
cp "$server_jar" "$dist_dev_dir/Server_$server_version.jar"