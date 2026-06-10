#!/usr/bin/env bash
set -euo pipefail

BASE_URL="${BASE_URL:-http://localhost:8080}"
CONCURRENCY="${CONCURRENCY:-20}"
DELAY_MS="${DELAY_MS:-1000}"
WEBCLIENT_DELAY_MS="${WEBCLIENT_DELAY_MS:-300}"
WEBCLIENT_IDS="${WEBCLIENT_IDS:-1,2,3,4,5}"
POOL_DELAY_MS="${POOL_DELAY_MS:-300}"
DB_DELAY_MS="${DB_DELAY_MS:-200}"

run_concurrent() {
  local label="$1"
  local path="$2"

  echo
  echo "== ${label}"
  echo "GET ${BASE_URL}${path}"
  echo "concurrency=${CONCURRENCY}"

  local started_at
  started_at=$(date +%s)

  for i in $(seq 1 "${CONCURRENCY}"); do
    curl -s -o /dev/null \
      -H "X-Request-Id: ${label// /-}-${i}" \
      -w "request=${i} status=%{http_code} total=%{time_total}s\n" \
      "${BASE_URL}${path}" &
  done
  wait

  local ended_at
  ended_at=$(date +%s)
  echo "wall-clock=$((ended_at - started_at))s"
}

run_once() {
  local label="$1"
  local path="$2"

  echo
  echo "== ${label}"
  echo "GET ${BASE_URL}${path}"
  curl -s \
    -H "X-Request-Id: ${label// /-}" \
    -w "\nstatus=%{http_code} total=%{time_total}s\n" \
    "${BASE_URL}${path}"
  echo
}

run_concurrent "blocking sleep" "/demo/blocking/sleep?ms=${DELAY_MS}"
run_concurrent "non blocking delay" "/demo/non-blocking/delay?ms=${DELAY_MS}"
run_concurrent "blocking offload boundedElastic" "/demo/blocking/offload?ms=${DELAY_MS}"
run_concurrent "blocking offload dispatchers io" "/demo/blocking/offload?ms=${DELAY_MS}&mode=io"

run_once "webclient sequential" "/demo/webclient/sequential?ids=${WEBCLIENT_IDS}&delayMs=${WEBCLIENT_DELAY_MS}"
run_once "webclient parallel" "/demo/webclient/parallel?ids=${WEBCLIENT_IDS}&delayMs=${WEBCLIENT_DELAY_MS}"

run_once "scheduler subscribeOn" "/demo/reactor/scheduler/subscribe-on"
run_once "scheduler publishOn" "/demo/reactor/scheduler/publish-on"
run_once "operator fusion" "/demo/reactor/fusion"

run_once "netty pool one connection" "/demo/netty/connection-pool?connections=1&ids=${WEBCLIENT_IDS}&delayMs=${POOL_DELAY_MS}"
run_once "netty pool four connections" "/demo/netty/connection-pool?connections=4&ids=${WEBCLIENT_IDS}&delayMs=${POOL_DELAY_MS}"

run_once "jdbc bad" "/demo/db/jdbc/bad?ms=${DB_DELAY_MS}"
run_once "jdbc offload" "/demo/db/jdbc/offload?ms=${DB_DELAY_MS}"
run_once "r2dbc" "/demo/db/r2dbc?ms=${DB_DELAY_MS}"

run_once "structured concurrency" "/demo/coroutine/structured"
run_once "mdc broken" "/demo/context/mdc/broken"
run_once "mdc fixed" "/demo/context/mdc/fixed"

echo
echo "서버 로그에서 reactor-http-nio, boundedElastic, DefaultDispatcher-worker, ForkJoinPool, requestId를 같이 확인하세요."
