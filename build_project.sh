#!/bin/bash

root_dir=$(pwd)

client_dir=$root_dir/TCPChatClient
server_dir=$root_dir/TCPChatServer
protocol_dir=$root_dir/TCPChatProtocol

cd "$protocol_dir" && mvn clean install
cd "$client_dir" && mvn clean install
cd "$server_dir" && mvn clean install