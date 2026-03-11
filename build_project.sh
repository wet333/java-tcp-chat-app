#!/bin/bash

set -e
root_dir=$(pwd)

client_dir=$root_dir/TCPChatClient
server_dir=$root_dir/TCPChatServer
dist_dev_dir=$root_dir/dist

# ── Step 1: Generate TLS keystores ──────────────────────────────────────────
"$root_dir/generate_keys.sh"

# ── Step 2: Build Commons + compile everything ───────────────────────────────
cd "$root_dir" && mvn clean install -DskipTests --no-transfer-progress -q

# ── Step 3: Build DEV JARs ───────────────────────────────────────────────────
echo "[Build] Packaging Server (DEV)..."
mvn package -pl TCPChatServer -Pdev -DskipTests --no-transfer-progress -q

echo "[Build] Packaging Client (DEV)..."
mvn package -pl TCPChatClient -Pdev -DskipTests --no-transfer-progress -q

# ── Step 4: Build PROD JARs (only if PROD keystores exist) ───────────────────
BUILD_PROD=false
if [ -d "$dist_dev_dir/tls/prod" ] && [ -f "$dist_dev_dir/tls/prod/server.keystore.jks" ]; then
    BUILD_PROD=true
fi

if [ "$BUILD_PROD" = true ]; then
    echo "[Build] Packaging Server (PROD)..."
    mvn package -pl TCPChatServer -Pprod -DskipTests --no-transfer-progress -q

    echo "[Build] Packaging Client (PROD)..."
    mvn package -pl TCPChatClient -Pprod -DskipTests --no-transfer-progress -q
else
    echo "[Build] No PROD keystores found — skipping PROD JARs. (Add PROD_TLS_* to tls.env to enable)"
fi

# ── Step 5: Copy JARs to dist/ ───────────────────────────────────────────────
mkdir -p "$dist_dev_dir"

# Remove old JARs before copying new ones
rm -f "$dist_dev_dir"/*.jar

client_jar=$(ls "$client_dir/target"/TCPChatClient-*.jar 2>/dev/null | grep -v original | head -1)
server_jar=$(ls "$server_dir/target"/TCPChatServer-*.jar 2>/dev/null | grep -v original | head -1)

client_version=$(basename "$client_jar" | sed 's/TCPChatClient-//' | sed 's/.jar//')
server_version=$(basename "$server_jar" | sed 's/TCPChatServer-//' | sed 's/.jar//')

# The last build that ran was DEV (or PROD if enabled), so we need to rebuild to get both.
# Copy whichever is currently in target — they were last built as DEV.
# Re-package to get clean DEV JARs in target before copying.
mvn package -pl TCPChatServer -Pdev -DskipTests --no-transfer-progress -q
cp "$server_jar" "$dist_dev_dir/Server_DEV_${server_version}.jar"

mvn package -pl TCPChatClient -Pdev -DskipTests --no-transfer-progress -q
cp "$client_jar" "$dist_dev_dir/Client_DEV_${client_version}.jar"

if [ "$BUILD_PROD" = true ]; then
    mvn clean package -pl TCPChatServer -Pprod -DskipTests --no-transfer-progress -q
    cp "$server_jar" "$dist_dev_dir/Server_PROD_${server_version}.jar"
    cp "$dist_dev_dir/tls/prod/server.keystore.jks" "$dist_dev_dir/server.keystore.jks"

    mvn clean package -pl TCPChatClient -Pprod -DskipTests --no-transfer-progress -q
    cp "$client_jar" "$dist_dev_dir/Client_PROD_${client_version}.jar"
fi

echo ""
echo "[Build] Done. Output in dist/:"
ls -1 "$dist_dev_dir"/*.jar
echo ""
echo "To run DEV server (from dist/):"
echo "  cp tls/dev/server.keystore.jks ./ && java -jar Server_DEV_${server_version}.jar"
echo ""
echo "To run DEV client (from dist/):"
echo "  java -jar Client_DEV_${client_version}.jar"
echo ""
if [ "$BUILD_PROD" = true ]; then
echo "To run PROD server (from dist/):"
echo "  TLS_KS_PASSWORD=<password> java -jar Server_PROD_${server_version}.jar"
echo "  (server.keystore.jks has been copied to dist/ automatically)"
fi
