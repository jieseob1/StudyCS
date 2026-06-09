#!/usr/bin/env bash
set -euo pipefail

cd "$(dirname "$0")"

rm -rf out
mkdir -p out

javac -d out $(find src test -name '*.java')

java -cp out studycs.javalab.StudyCsLabTest
java -cp out studycs.javalab.Main
