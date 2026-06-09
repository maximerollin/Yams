#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

RUN_VALIDATE="${RUN_VALIDATE:-0}"
SCREENSHOT_JAVA_HOME="${SCREENSHOT_JAVA_HOME:-}"
DEFAULT_JBR_HOME="$HOME/.gradle/jdks/jetbrains_s_r_o_-21-aarch64-os_x.2/jbrsdk_jcef-21.0.8-osx-aarch64-b1038.68/Contents/Home"

if [[ -z "$SCREENSHOT_JAVA_HOME" && -d "$DEFAULT_JBR_HOME" ]]; then
  SCREENSHOT_JAVA_HOME="$DEFAULT_JBR_HOME"
fi

GRADLE_ARGS=()
if [[ -n "$SCREENSHOT_JAVA_HOME" ]]; then
  GRADLE_ARGS+=("-Dorg.gradle.java.home=$SCREENSHOT_JAVA_HOME")
fi

SOURCE_DIR="screenshots/build/outputs/screenshotTest-results/preview/debug/rendered/io/github/maximerollin/yams/screenshots/StoreScreenshotsKt"
DEST_DIR="screenshot"
TEST_REFERENCE_DIR="screenshots/src/screenshotTestDebug/reference/io/github/maximerollin/yams/screenshots/StoreScreenshotsKt"
TEST_SCREENSHOT_DIRS=("$SOURCE_DIR" "$TEST_REFERENCE_DIR")
LANGUAGES=(de en es fr it)
DEVICES=(phone tablet)

camel_to_kebab() {
  local input="$1"
  local output=""
  local previous=""
  local char=""

  for ((i = 0; i < ${#input}; i++)); do
    char="${input:i:1}"
    if [[ "$char" =~ [A-Z] && "$previous" =~ [a-z0-9] ]]; then
      output+="-"
    fi
    output+="$char"
    previous="$char"
  done

  printf '%s' "$output" | tr '[:upper:]' '[:lower:]'
}

delete_test_screenshot_pngs() {
  local deleted=0
  local dir=""
  local file=""

  for dir in "${TEST_SCREENSHOT_DIRS[@]}"; do
    if [[ -d "$dir" ]]; then
      while IFS= read -r -d '' file; do
        rm -- "$file"
        ((deleted += 1))
      done < <(find "$dir" -type f -name '*.png' -print0)
    fi
  done

  if ((deleted > 0)); then
    echo "Deleted $deleted PNG files from screenshot test directories."
  else
    echo "No PNG files found in screenshot test directories."
  fi
}

for device in "${DEVICES[@]}"; do
  for language in "${LANGUAGES[@]}"; do
    mkdir -p "$DEST_DIR/$device/$language"
  done
done

find "$DEST_DIR" -type f -name '*.png' -delete
if [[ -d "$SOURCE_DIR" ]]; then
  find "$SOURCE_DIR" -type f -name '*.png' -delete
fi

echo "Generating Compose preview screenshots..."
./gradlew :screenshots:updateDebugScreenshotTest "${GRADLE_ARGS[@]}"

if [[ ! -d "$SOURCE_DIR" ]]; then
  echo "Screenshot output directory not found: $SOURCE_DIR" >&2
  exit 1
fi

shopt -s nullglob
copied=0
invalid=0

for file in "$SOURCE_DIR"/*.png; do
  base_name="$(basename "$file")"

  if [[ "$base_name" =~ ^(.+)_(phone|tablet)-([a-z]{2})_[0-9a-f]+_0\.png$ ]]; then
    byte_size="$(wc -c < "$file" | tr -d '[:space:]')"
    if ((byte_size <= 1024)); then
      echo "Invalid screenshot output, file is too small: $file" >&2
      ((invalid += 1))
      continue
    fi

    screen="${BASH_REMATCH[1]}"
    device="${BASH_REMATCH[2]}"
    language="${BASH_REMATCH[3]}"
    output_name="$(camel_to_kebab "$screen").png"

    cp "$file" "$DEST_DIR/$device/$language/$output_name"
    ((copied += 1))
  fi
done

if ((invalid > 0)); then
  echo "Found $invalid invalid screenshots. Aborting copy validation." >&2
  exit 1
fi

if ((copied == 0)); then
  echo "No screenshots were copied from $SOURCE_DIR." >&2
  exit 1
fi

echo "Copied $copied screenshots into $DEST_DIR/."

if [[ "$RUN_VALIDATE" == "1" ]]; then
  echo "Validating screenshot references..."
  ./gradlew :screenshots:validateDebugScreenshotTest "${GRADLE_ARGS[@]}"
else
  echo "Skipping screenshot validation. Set RUN_VALIDATE=1 to enable it."
fi

delete_test_screenshot_pngs

echo "Done."
