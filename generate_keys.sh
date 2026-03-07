#!/bin/bash
# generate_keys.sh
# Generates TLS keystores for DEV (localhost) and optionally PROD environments.
# Output goes to dist/tls/dev/ and dist/tls/prod/.
# Run this before building the project, or let build_project.sh call it automatically.

set -e
ROOT_DIR=$(dirname "$(realpath "$0")")
TLS_DIR="$ROOT_DIR/dist/tls"

# ── Load tls.env if present ──────────────────────────────────────────────────
if [ -f "$ROOT_DIR/tls.env" ]; then
    echo "[TLS] Loading tls.env..."
    set -a
    # shellcheck disable=SC1091
    source "$ROOT_DIR/tls.env"
    set +a
fi

# ── DEV defaults (used if vars are not set) ──────────────────────────────────
DEV_TLS_PASSWORD="${DEV_TLS_PASSWORD:-chatapp_dev_tls}"
DEV_TLS_VALIDITY="${DEV_TLS_VALIDITY:-3650}"
DEV_DNAME="CN=localhost, OU=Dev, O=ChatApp, L=Local, ST=Dev, C=AR"

# ── Generate DEV keystores ───────────────────────────────────────────────────
DEV_DIR="$TLS_DIR/dev"
DEV_KS="$DEV_DIR/server.keystore.jks"
DEV_TS="$DEV_DIR/client.truststore.jks"

mkdir -p "$DEV_DIR"

if [ -f "$DEV_KS" ] && [ -f "$DEV_TS" ]; then
    echo "[TLS] DEV keystores already exist, skipping. (delete dist/tls/dev/ to regenerate)"
else
    echo "[TLS] Generating DEV certificate (CN=localhost, validity=${DEV_TLS_VALIDITY} days)..."
    CERT_TMP="$DEV_DIR/_tmp_server.crt"

    keytool -genkeypair \
        -alias chatserver \
        -keyalg RSA -keysize 2048 \
        -validity "$DEV_TLS_VALIDITY" \
        -keystore "$DEV_KS" \
        -storepass "$DEV_TLS_PASSWORD" \
        -keypass "$DEV_TLS_PASSWORD" \
        -dname "$DEV_DNAME" \
        -noprompt 2>/dev/null

    keytool -exportcert \
        -alias chatserver \
        -keystore "$DEV_KS" \
        -storepass "$DEV_TLS_PASSWORD" \
        -file "$CERT_TMP" -rfc 2>/dev/null

    keytool -importcert \
        -alias chatserver \
        -file "$CERT_TMP" \
        -keystore "$DEV_TS" \
        -storepass "$DEV_TLS_PASSWORD" \
        -noprompt 2>/dev/null

    rm "$CERT_TMP"

    # tls.properties bundled inside the client JAR
    echo "truststore.password=$DEV_TLS_PASSWORD" > "$DEV_DIR/tls.properties"

    echo "[TLS] DEV keystores generated in dist/tls/dev/"
fi

# ── Generate PROD keystores (only if all PROD vars are defined) ──────────────
PROD_REQUIRED_VARS=(PROD_TLS_CN PROD_TLS_OU PROD_TLS_O PROD_TLS_L PROD_TLS_ST PROD_TLS_C PROD_TLS_PASSWORD PROD_TLS_VALIDITY)
PROD_CONFIGURED=true
for VAR in "${PROD_REQUIRED_VARS[@]}"; do
    if [ -z "${!VAR}" ]; then
        PROD_CONFIGURED=false
        break
    fi
done

if [ "$PROD_CONFIGURED" = false ]; then
    echo "[TLS] PROD vars not fully defined in tls.env — skipping PROD keystores."
    echo "[TLS] Copy tls.env.example to tls.env and fill in PROD_TLS_* to enable PROD builds."
    exit 0
fi

PROD_DIR="$TLS_DIR/prod"
PROD_KS="$PROD_DIR/server.keystore.jks"
PROD_TS="$PROD_DIR/client.truststore.jks"

mkdir -p "$PROD_DIR"

if [ -f "$PROD_KS" ] && [ -f "$PROD_TS" ]; then
    echo "[TLS] PROD keystores already exist, skipping. (delete dist/tls/prod/ to regenerate)"
else
    PROD_DNAME="CN=${PROD_TLS_CN}, OU=${PROD_TLS_OU}, O=${PROD_TLS_O}, L=${PROD_TLS_L}, ST=${PROD_TLS_ST}, C=${PROD_TLS_C}"
    echo "[TLS] Generating PROD certificate (CN=${PROD_TLS_CN}, validity=${PROD_TLS_VALIDITY} days)..."
    CERT_TMP="$PROD_DIR/_tmp_server.crt"

    keytool -genkeypair \
        -alias chatserver \
        -keyalg RSA -keysize 2048 \
        -validity "$PROD_TLS_VALIDITY" \
        -keystore "$PROD_KS" \
        -storepass "$PROD_TLS_PASSWORD" \
        -keypass "$PROD_TLS_PASSWORD" \
        -dname "$PROD_DNAME" \
        -noprompt 2>/dev/null

    keytool -exportcert \
        -alias chatserver \
        -keystore "$PROD_KS" \
        -storepass "$PROD_TLS_PASSWORD" \
        -file "$CERT_TMP" -rfc 2>/dev/null

    keytool -importcert \
        -alias chatserver \
        -file "$CERT_TMP" \
        -keystore "$PROD_TS" \
        -storepass "$PROD_TLS_PASSWORD" \
        -noprompt 2>/dev/null

    rm "$CERT_TMP"

    # tls.properties bundled inside the client JAR
    echo "truststore.password=$PROD_TLS_PASSWORD" > "$PROD_DIR/tls.properties"

    echo "[TLS] PROD keystores generated in dist/tls/prod/"
fi
