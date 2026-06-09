#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

DRY_RUN=0
FORCE=0
TARGET_DIRS=(screenshot screenshots)

usage() {
  cat <<'EOF'
Usage: ./scripts/clear-store-screenshots.sh [--dry-run] [--force]

Deletes every PNG under:
  - screenshot/
  - screenshots/

Options:
  --dry-run   List PNG files without deleting them.
  --force     Delete without asking for confirmation.
  --help      Show this help.
EOF
}

while [[ $# -gt 0 ]]; do
  case "$1" in
    --dry-run)
      DRY_RUN=1
      shift
      ;;
    --force)
      FORCE=1
      shift
      ;;
    --help|-h)
      usage
      exit 0
      ;;
    *)
      echo "Unknown option: $1" >&2
      usage >&2
      exit 1
      ;;
  esac
done

png_files=()

for target_dir in "${TARGET_DIRS[@]}"; do
  if [[ -d "$target_dir" ]]; then
    while IFS= read -r -d '' file; do
      png_files+=("$file")
    done < <(find "$target_dir" -type f -name '*.png' -print0)
  fi
done

if [[ "${#png_files[@]}" -eq 0 ]]; then
  echo "No PNG files found in screenshot/ or screenshots/."
  exit 0
fi

if [[ "$DRY_RUN" -eq 1 ]]; then
  printf '%s\n' "${png_files[@]}"
  echo
  echo "Dry run: ${#png_files[@]} PNG files would be deleted."
  exit 0
fi

echo "This will delete ${#png_files[@]} PNG files under screenshot/ and screenshots/."

if [[ "$FORCE" -ne 1 ]]; then
  read -r -p "Continue? [y/N] " answer
  if [[ ! "$answer" =~ ^[Yy]$ ]]; then
    echo "Aborted."
    exit 0
  fi
fi

for file in "${png_files[@]}"; do
  rm -- "$file"
done

echo "Deleted ${#png_files[@]} PNG files."
