#!/bin/bash
# version-bump.sh
# Semi-automatic version bumping for the tcp-chat Maven multi-module project.
#
# Usage:
#   ./version-bump.sh                          # interactive: detect changed modules
#   ./version-bump.sh --release                # strip -SNAPSHOT from changed modules
#   ./version-bump.sh --commons 0.2.0-SNAPSHOT # set specific module versions directly
#   ./version-bump.sh --server  0.2.0-SNAPSHOT
#   ./version-bump.sh --client  0.2.0-SNAPSHOT
#   (flags can be combined)

set -euo pipefail

# ── colours ────────────────────────────────────────────────────────────────────
RED='\033[0;31m'; GREEN='\033[0;32m'; YELLOW='\033[1;33m'
CYAN='\033[0;36m'; BOLD='\033[1m'; NC='\033[0m'

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# ── helpers ────────────────────────────────────────────────────────────────────

check_python() {
    if ! command -v python3 &>/dev/null; then
        echo -e "${RED}Error: python3 is required but not found.${NC}"
        echo "Install it with:  sudo apt install python3  (Debian/Ubuntu/WSL)"
        exit 1
    fi
}

# Read the module-level <version> from a pom.xml (the one AFTER </parent>).
# For the root pom (no <parent> block) it reads the first <version>.
get_version() {
    local pom="$1"
    python3 - "$pom" <<'EOF'
import sys, re

with open(sys.argv[1]) as f:
    content = f.read()

# If there is a <parent> block, skip it and find the next <version>
parent_end = content.find('</parent>')
search_from = parent_end + len('</parent>') if parent_end != -1 else 0

m = re.search(r'<version>([^<]+)</version>', content[search_from:])
if m:
    print(m.group(1).strip())
else:
    print('')
EOF
}

# Replace the module-level <version> in a pom.xml (the one AFTER </parent>).
set_module_version() {
    local pom="$1"
    local new_ver="$2"
    python3 - "$pom" "$new_ver" <<'EOF'
import sys, re

pom_path, new_version = sys.argv[1], sys.argv[2]

with open(pom_path) as f:
    content = f.read()

parent_end = content.find('</parent>')
if parent_end != -1:
    anchor = parent_end + len('</parent>')
    pre   = content[:anchor]
    rest  = content[anchor:]
    rest  = re.sub(r'<version>[^<]+</version>', f'<version>{new_version}</version>', rest, count=1)
    updated = pre + rest
else:
    # root pom: replace the first <version>
    updated = re.sub(r'<version>[^<]+</version>', f'<version>{new_version}</version>', content, count=1)

with open(pom_path, 'w') as f:
    f.write(updated)
EOF
}

# Replace the <tcpchatcommons.version> property in the root pom.xml.
set_commons_property() {
    local new_ver="$1"
    python3 - "$ROOT_DIR/pom.xml" "$new_ver" <<'EOF'
import sys, re

pom_path, new_version = sys.argv[1], sys.argv[2]

with open(pom_path) as f:
    content = f.read()

updated = re.sub(
    r'<tcpchatcommons\.version>[^<]+</tcpchatcommons\.version>',
    f'<tcpchatcommons.version>{new_version}</tcpchatcommons.version>',
    content
)

with open(pom_path, 'w') as f:
    f.write(updated)
EOF
}

# Validate semver-like format: digits.digits.digits[-anything]
validate_version() {
    local ver="$1"
    if [[ ! "$ver" =~ ^[0-9]+\.[0-9]+\.[0-9]+(-[A-Za-z0-9._-]+)?$ ]]; then
        echo -e "${RED}Error: versión inválida '${ver}'. Formato esperado: MAJOR.MINOR.PATCH o MAJOR.MINOR.PATCH-SUFFIX${NC}"
        return 1
    fi
}

# Compute the next version given a bump type.
bump_version() {
    local version="$1"
    local bump_type="$2"

    # Strip any -SUFFIX (e.g. -SNAPSHOT) to work on numbers
    local base="${version%%-*}"
    IFS='.' read -r major minor patch <<< "$base"

    case "$bump_type" in
        patch)   echo "$major.$minor.$((patch + 1))-SNAPSHOT" ;;
        minor)   echo "$major.$((minor + 1)).0-SNAPSHOT" ;;
        major)   echo "$((major + 1)).0.0-SNAPSHOT" ;;
        release) echo "$major.$minor.$patch" ;;
        *)       return 1 ;;
    esac
}

# ── change detection ───────────────────────────────────────────────────────────

detect_changed_modules() {
    local diff_output=""

    # Try origin/master first; fall back to HEAD~1 if no remote
    if git rev-parse --verify origin/master &>/dev/null; then
        diff_output=$(git diff origin/master --name-only 2>/dev/null || true)
        # Also include staged/unstaged local changes not yet in origin
        local local_changes
        local_changes=$(git diff HEAD --name-only 2>/dev/null || true)
        diff_output=$(printf '%s\n%s' "$diff_output" "$local_changes")
    elif git rev-parse HEAD~1 &>/dev/null 2>&1; then
        echo -e "${YELLOW}Aviso: no se encontró origin/master. Usando HEAD~1 como base.${NC}"
        diff_output=$(git diff HEAD~1 --name-only 2>/dev/null || true)
    else
        echo -e "${YELLOW}Aviso: no hay historial suficiente. Mostrando archivos modificados localmente.${NC}"
        diff_output=$(git status --short | awk '{print $2}' || true)
    fi

    CHANGED_COMMONS=false
    CHANGED_SERVER=false
    CHANGED_CLIENT=false

    echo "$diff_output" | grep -q '^TCPChatCommons/' && CHANGED_COMMONS=true || true
    echo "$diff_output" | grep -q '^TCPChatServer/'  && CHANGED_SERVER=true  || true
    echo "$diff_output" | grep -q '^TCPChatClient/'  && CHANGED_CLIENT=true  || true
}

# ── interactive bump for a single module ──────────────────────────────────────

interactive_bump() {
    local module="$1"
    local pom="$2"
    local current
    current=$(get_version "$pom")

    echo ""
    echo -e "${BOLD}[${module}]${NC} versión actual: ${CYAN}${current}${NC}"

    local new_ver=""
    while true; do
        printf "  Tipo de bump [patch/minor/major/skip/custom]: "
        read -r choice </dev/tty

        case "$choice" in
            patch|minor|major|release)
                new_ver=$(bump_version "$current" "$choice")
                break ;;
            skip)
                echo -e "  ${YELLOW}→ sin cambios${NC}"
                return 0 ;;
            custom)
                printf "  Nueva versión: "
                read -r new_ver </dev/tty
                if validate_version "$new_ver"; then
                    break
                fi ;;
            *)
                echo -e "  ${RED}Opción inválida. Usá: patch, minor, major, skip o custom.${NC}" ;;
        esac
    done

    echo -e "  ${GREEN}→ nueva versión: ${new_ver}${NC}"
    # Store result in global vars (bash doesn't have return values for strings)
    BUMP_RESULT_VERSION="$new_ver"
    BUMP_RESULT_MODULE="$module"
}

# ── print summary ──────────────────────────────────────────────────────────────

print_summary() {
    echo ""
    echo -e "${BOLD}Resumen:${NC}"
    printf "  %-16s %s\n" "TCPChatCommons"  "${SUMMARY_COMMONS}"
    printf "  %-16s %s\n" "TCPChatServer"   "${SUMMARY_SERVER}"
    printf "  %-16s %s\n" "TCPChatClient"   "${SUMMARY_CLIENT}"
    echo ""
}

# ── apply changes ──────────────────────────────────────────────────────────────

apply_version() {
    local module="$1"
    local pom="$2"
    local old_ver="$3"
    local new_ver="$4"

    set_module_version "$pom" "$new_ver"

    if [[ "$module" == "TCPChatCommons" ]]; then
        set_commons_property "$new_ver"
    fi

    echo -e "  ${GREEN}✓${NC} ${pom#$ROOT_DIR/}"
}

# ── argument parsing ───────────────────────────────────────────────────────────

RELEASE_MODE=false
ARG_COMMONS=""
ARG_SERVER=""
ARG_CLIENT=""

while [[ $# -gt 0 ]]; do
    case "$1" in
        --release)     RELEASE_MODE=true;  shift ;;
        --commons)     ARG_COMMONS="$2";   shift 2 ;;
        --server)      ARG_SERVER="$2";    shift 2 ;;
        --client)      ARG_CLIENT="$2";    shift 2 ;;
        -h|--help)
            cat <<'HELP'
version-bump.sh — Semi-automatic version bumping for the tcp-chat Maven project.

Usage:
  ./version-bump.sh                          # interactive: detect changed modules
  ./version-bump.sh --release                # strip -SNAPSHOT from changed modules
  ./version-bump.sh --commons 0.2.0-SNAPSHOT # set specific module versions directly
  ./version-bump.sh --server  0.2.0-SNAPSHOT
  ./version-bump.sh --client  0.2.0-SNAPSHOT
  (flags can be combined)
HELP
            exit 0 ;;
        *)
            echo -e "${RED}Flag desconocido: $1${NC}"; exit 1 ;;
    esac
done

# ── main ───────────────────────────────────────────────────────────────────────

check_python

POM_COMMONS="$ROOT_DIR/TCPChatCommons/pom.xml"
POM_SERVER="$ROOT_DIR/TCPChatServer/pom.xml"
POM_CLIENT="$ROOT_DIR/TCPChatClient/pom.xml"

VER_COMMONS=$(get_version "$POM_COMMONS")
VER_SERVER=$(get_version "$POM_SERVER")
VER_CLIENT=$(get_version "$POM_CLIENT")

SUMMARY_COMMONS="${VER_COMMONS} (sin cambios)"
SUMMARY_SERVER="${VER_SERVER} (sin cambios)"
SUMMARY_CLIENT="${VER_CLIENT} (sin cambios)"

CHANGES_APPLIED=false

# ── mode: explicit --set args ──────────────────────────────────────────────────

if [[ -n "$ARG_COMMONS" || -n "$ARG_SERVER" || -n "$ARG_CLIENT" ]]; then

    echo -e "${BOLD}Aplicando versiones explícitas...${NC}"

    if [[ -n "$ARG_COMMONS" ]]; then
        validate_version "$ARG_COMMONS"
        apply_version "TCPChatCommons" "$POM_COMMONS" "$VER_COMMONS" "$ARG_COMMONS"
        SUMMARY_COMMONS="${VER_COMMONS} → ${ARG_COMMONS}"
        CHANGES_APPLIED=true
    fi
    if [[ -n "$ARG_SERVER" ]]; then
        validate_version "$ARG_SERVER"
        apply_version "TCPChatServer" "$POM_SERVER" "$VER_SERVER" "$ARG_SERVER"
        SUMMARY_SERVER="${VER_SERVER} → ${ARG_SERVER}"
        CHANGES_APPLIED=true
    fi
    if [[ -n "$ARG_CLIENT" ]]; then
        validate_version "$ARG_CLIENT"
        apply_version "TCPChatClient" "$POM_CLIENT" "$VER_CLIENT" "$ARG_CLIENT"
        SUMMARY_CLIENT="${VER_CLIENT} → ${ARG_CLIENT}"
        CHANGES_APPLIED=true
    fi

    print_summary
    exit 0
fi

# ── mode: detect changes + interactive / --release ────────────────────────────

echo -e "${BOLD}Detectando cambios vs origin/master...${NC}"
detect_changed_modules

# Display detection results
for mod in TCPChatCommons TCPChatServer TCPChatClient; do
    var="CHANGED_${mod#TCPChat}"
    # Normalize: CHANGED_COMMONS, CHANGED_SERVER, CHANGED_CLIENT
    case "$mod" in
        TCPChatCommons) flag=$CHANGED_COMMONS ;;
        TCPChatServer)  flag=$CHANGED_SERVER  ;;
        TCPChatClient)  flag=$CHANGED_CLIENT  ;;
    esac

    if [[ "$flag" == true ]]; then
        echo -e "  ${GREEN}✓${NC} ${mod}  — cambios detectados"
    else
        echo -e "  ${YELLOW}–${NC} ${mod}  — sin cambios"
    fi
done

NONE_CHANGED=true
[[ $CHANGED_COMMONS == true || $CHANGED_SERVER == true || $CHANGED_CLIENT == true ]] && NONE_CHANGED=false

if [[ "$NONE_CHANGED" == true ]]; then
    echo ""
    echo -e "${YELLOW}No se detectaron cambios en ningún módulo.${NC}"
    echo "Podés forzar un bump con: ./version-bump.sh --server <versión>"
    exit 0
fi

# For each changed module: either release-strip or ask interactively
process_module() {
    local mod="$1"
    local pom="$2"
    local current="$3"
    local summary_var="$4"

    if [[ "$RELEASE_MODE" == true ]]; then
        local new_ver
        new_ver=$(bump_version "$current" release)
        echo ""
        echo -e "${BOLD}[${mod}]${NC} ${CYAN}${current}${NC} → ${GREEN}${new_ver}${NC} (release)"
        apply_version "$mod" "$pom" "$current" "$new_ver"
        eval "$summary_var=\"${current} → ${new_ver}\""
        CHANGES_APPLIED=true
    else
        BUMP_RESULT_VERSION=""
        interactive_bump "$mod" "$pom"
        if [[ -n "$BUMP_RESULT_VERSION" ]]; then
            echo -e "  Aplicando..."
            apply_version "$mod" "$pom" "$current" "$BUMP_RESULT_VERSION"
            eval "$summary_var=\"${current} → ${BUMP_RESULT_VERSION}\""
            CHANGES_APPLIED=true
        fi
    fi
}

[[ $CHANGED_COMMONS == true ]] && process_module "TCPChatCommons" "$POM_COMMONS" "$VER_COMMONS" "SUMMARY_COMMONS"
[[ $CHANGED_SERVER  == true ]] && process_module "TCPChatServer"  "$POM_SERVER"  "$VER_SERVER"  "SUMMARY_SERVER"
[[ $CHANGED_CLIENT  == true ]] && process_module "TCPChatClient"  "$POM_CLIENT"  "$VER_CLIENT"  "SUMMARY_CLIENT"

if [[ "$CHANGES_APPLIED" == true ]]; then
    print_summary
    echo -e "${GREEN}Listo. Verificá con: mvn validate${NC}"
else
    echo ""
    echo -e "${YELLOW}No se aplicaron cambios.${NC}"
fi
