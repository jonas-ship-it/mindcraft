#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "${ROOT_DIR}"

if [[ -x "./gradlew" ]]; then
  ./gradlew build
elif command -v gradle >/dev/null 2>&1; then
  gradle build
else
  echo "Gradle not found. Install Gradle or add the Gradle wrapper (./gradlew)." >&2
  exit 1
fi
