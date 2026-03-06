#!/usr/bin/env bash
set -euo pipefail

APP_ID="${APP_ID:-Yams}"
FORCE=0

if [[ "${1:-}" == "--force" ]]; then
  FORCE=1
fi

case "$(uname -s)" in
  Darwin)
    FILES_DIR="$HOME/Library/Application Support/$APP_ID"
    CACHE_DIR="$HOME/Library/Caches/$APP_ID"
    ;;
  Linux)
    FILES_DIR="${XDG_DATA_HOME:-$HOME/.local/share}/$APP_ID"
    CACHE_DIR="${XDG_CACHE_HOME:-$HOME/.cache}/$APP_ID"
    ;;
  MINGW*|MSYS*|CYGWIN*)
    FILES_DIR="${APPDATA:-$HOME/AppData/Roaming}/$APP_ID"
    CACHE_DIR="${LOCALAPPDATA:-$HOME/AppData/Local}/$APP_ID/Cache"
    ;;
  *)
    echo "Unsupported OS: $(uname -s)"
    exit 1
    ;;
esac

echo "Full app reset for '$APP_ID' will remove:"
echo "  - $FILES_DIR"
echo "  - $CACHE_DIR"
echo
echo "Close the app before running this script."

if [[ "$FORCE" -ne 1 ]]; then
  read -r -p "Continue? [y/N] " answer
  if [[ ! "$answer" =~ ^[Yy]$ ]]; then
    echo "Aborted."
    exit 0
  fi
fi

rm -rf "$FILES_DIR" "$CACHE_DIR"
echo "Done. App data removed."
