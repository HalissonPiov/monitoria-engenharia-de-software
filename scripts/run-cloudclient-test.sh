#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
JUNIT_JAR="$ROOT_DIR/lib/junit-platform-console-standalone-1.10.2.jar"
OUT_DIR="$ROOT_DIR/build"

if [[ ! -f "$JUNIT_JAR" ]]; then
  echo "JAR do JUnit 5 nao encontrado em: $JUNIT_JAR"
  echo "Baixe o arquivo junit-platform-console-standalone-1.10.2.jar para a pasta lib/."
  exit 1
fi

rm -rf "$OUT_DIR"
mkdir -p "$OUT_DIR"

# Compila as classes originais e o teste. 
# O -sourcepath avisa ao compilador para procurar dependências (como a classe PublishConfig) na pasta refactor.
javac -cp "$JUNIT_JAR" -d "$OUT_DIR" -sourcepath "$ROOT_DIR/src:$ROOT_DIR/src/exercises/activity1/refactor" \
  "$ROOT_DIR/src/exercises/activity1/KuraException.java" \
  "$ROOT_DIR/src/exercises/activity1/CloudClient.java" \
  "$ROOT_DIR/src/exercises/activity1/CloudClientImpl.java" \
  "$ROOT_DIR/src/exercises/activity1/CloudClientTest.java"

java -jar "$JUNIT_JAR" execute --class-path "$OUT_DIR" --select-class CloudClientTest