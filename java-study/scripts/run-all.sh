#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
BUILD_DIR="$ROOT_DIR/build/classes"
SOURCES_FILE="$ROOT_DIR/build/sources.txt"

rm -rf "$ROOT_DIR/build"
mkdir -p "$BUILD_DIR"

SOURCE_DIRS=()
if [[ -d "$ROOT_DIR/src/main/java" ]]; then
  SOURCE_DIRS+=("$ROOT_DIR/src/main/java")
fi
if [[ -d "$ROOT_DIR/src/test/java" ]]; then
  SOURCE_DIRS+=("$ROOT_DIR/src/test/java")
fi

if [[ ${#SOURCE_DIRS[@]} -eq 0 ]]; then
  echo "No Java source directories found." >&2
  exit 1
fi

find "${SOURCE_DIRS[@]}" -name "*.java" | sort > "$SOURCES_FILE"

if [[ ! -s "$SOURCES_FILE" ]]; then
  echo "No Java source files found." >&2
  exit 1
fi

javac -encoding UTF-8 -d "$BUILD_DIR" @"$SOURCES_FILE"

echo "== Assertions =="
java -cp "$BUILD_DIR" com.studycs.JavaStudyAssertions

echo
echo "== Data structure demo =="
java -cp "$BUILD_DIR" com.studycs.datastructures.DataStructureDemo

echo
echo "== Algorithm pattern demo =="
java -cp "$BUILD_DIR" com.studycs.algorithms.AlgorithmPatternDemo
