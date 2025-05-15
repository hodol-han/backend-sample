#!/bin/bash

set -euo pipefail
set -o errexit

GRADLE_WRAPPER="${GRADLE_WRAPPER:-./gradlew}"

if [[ $# -eq 0 ]]; then
  echo "Usage: $0 <file1> <file2> ..."
  exit 1
fi

for file in "$@"; do
  if [[ ! -f "$file" ]]; then
    echo "Skipping invalid file: $file"
    continue
  fi

  echo "Formatting: $file"

  # 'spotlessIdeHook' applies formatting to specific files as a workaround.
  # See: https://jdriven.com/blog/2020/11/Formatting-in-pre-commit-hook
  "$GRADLE_WRAPPER" \
    spotlessApply \
    -PspotlessIdeHook="$(realpath "$file")" \
    --quiet
done
