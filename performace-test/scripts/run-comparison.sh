#!/bin/bash
set -e

PROJECT_ROOT="$(cd "$(dirname "$0")/.." && pwd)"
JAR_PATH="${PROJECT_ROOT}/build/libs/virtual-threads-benchmark-0.0.1-SNAPSHOT.jar"
APP_URL="http://localhost:8080"
INFLUXDB_URL="http://localhost:8086/k6"

# ─────────────────────────────────────────────
# Helpers
# ─────────────────────────────────────────────

log() { echo "[$(date '+%H:%M:%S')] $*"; }

wait_for_app() {
    local pid=$1
    log "Waiting for application (PID ${pid}) to be ready..."
    for i in $(seq 1 60); do
        if curl -s "${APP_URL}/actuator/health" | grep -q '"UP"'; then
            log "Application is ready."
            return 0
        fi
        if ! kill -0 "${pid}" 2>/dev/null; then
            log "ERROR: Application process exited unexpectedly."
            return 1
        fi
        sleep 1
    done
    log "ERROR: Application did not start within 60 seconds."
    return 1
}

stop_app() {
    local pid=$1
    if kill -0 "${pid}" 2>/dev/null; then
        log "Stopping application (PID ${pid})..."
        kill "${pid}" 2>/dev/null || true
        wait "${pid}" 2>/dev/null || true
        log "Application stopped."
    fi
}

# ─────────────────────────────────────────────
# 1. Prerequisites check
# ─────────────────────────────────────────────

echo ""
echo "=========================================="
echo "  Virtual Threads Full Comparison"
echo "=========================================="
echo ""
log "Checking prerequisites..."

check_cmd() {
    if ! command -v "$1" &>/dev/null; then
        log "ERROR: '$1' is not installed or not on PATH."
        exit 1
    fi
    log "  [ok] $1 found: $(command -v "$1")"
}

check_cmd java
check_cmd k6
check_cmd docker

JAVA_VERSION=$(java -version 2>&1 | head -1)
log "  Java: ${JAVA_VERSION}"
log "Prerequisites satisfied."

# ─────────────────────────────────────────────
# 2. Start monitoring stack
# ─────────────────────────────────────────────

log "Starting monitoring stack (Prometheus + Grafana + InfluxDB)..."
cd "${PROJECT_ROOT}"
docker compose up -d

log "Waiting for monitoring services to be ready..."
# InfluxDB readiness
for i in $(seq 1 30); do
    if curl -s "http://localhost:8086/ping" | grep -q "influxdb" || \
       curl -s -o /dev/null -w "%{http_code}" "http://localhost:8086/ping" | grep -q "204"; then
        log "InfluxDB is ready."
        break
    fi
    if [ $i -eq 30 ]; then
        log "WARNING: InfluxDB may not be ready - proceeding anyway."
    fi
    sleep 2
done

# Grafana readiness
for i in $(seq 1 30); do
    if curl -s "http://localhost:3000/api/health" | grep -q '"ok"'; then
        log "Grafana is ready."
        break
    fi
    if [ $i -eq 30 ]; then
        log "WARNING: Grafana may not be ready - proceeding anyway."
    fi
    sleep 2
done

# ─────────────────────────────────────────────
# 3. Build the application (once, shared jar)
# ─────────────────────────────────────────────

log "Building application jar..."
cd "${PROJECT_ROOT}"
./gradlew bootJar -q
log "Build complete: ${JAR_PATH}"

# ─────────────────────────────────────────────
# 4. Platform threads test
# ─────────────────────────────────────────────

echo ""
echo "------------------------------------------"
echo "  PHASE 1: Platform Threads Test"
echo "------------------------------------------"

log "Starting Spring Boot with Platform Threads..."
java -jar "${JAR_PATH}" \
    --spring.profiles.active=platform-threads \
    -Djdk.tracePinnedThreads=short &
PLATFORM_PID=$!

if ! wait_for_app "${PLATFORM_PID}"; then
    stop_app "${PLATFORM_PID}"
    exit 1
fi

log "Running k6 I/O heavy test (platform threads)..."
k6 run \
    --out "influxdb=${INFLUXDB_URL}" \
    -e TEST_MODE=platform \
    -e BASE_URL="${APP_URL}" \
    "${PROJECT_ROOT}/k6/scripts/io-heavy-test.js"

stop_app "${PLATFORM_PID}"

# ─────────────────────────────────────────────
# 5. Cooldown between test runs
# ─────────────────────────────────────────────

log "Cooldown period (10 seconds) between tests..."
sleep 10

# ─────────────────────────────────────────────
# 6. Virtual threads test
# ─────────────────────────────────────────────

echo ""
echo "------------------------------------------"
echo "  PHASE 2: Virtual Threads Test"
echo "------------------------------------------"

log "Starting Spring Boot with Virtual Threads..."
java -jar "${JAR_PATH}" \
    --spring.profiles.active=virtual-threads \
    -Djdk.tracePinnedThreads=short &
VIRTUAL_PID=$!

if ! wait_for_app "${VIRTUAL_PID}"; then
    stop_app "${VIRTUAL_PID}"
    exit 1
fi

log "Running k6 I/O heavy test (virtual threads)..."
k6 run \
    --out "influxdb=${INFLUXDB_URL}" \
    -e TEST_MODE=virtual \
    -e BASE_URL="${APP_URL}" \
    "${PROJECT_ROOT}/k6/scripts/io-heavy-test.js"

stop_app "${VIRTUAL_PID}"

# ─────────────────────────────────────────────
# 7. Summary
# ─────────────────────────────────────────────

echo ""
echo "=========================================="
echo "  Benchmark Complete"
echo "=========================================="
echo ""
echo "  Both test runs have finished."
echo "  Results are stored in InfluxDB and"
echo "  can be visualised in Grafana."
echo ""
echo "  Grafana:    http://localhost:3000"
echo "    user:     admin"
echo "    password: admin"
echo ""
echo "  Filter by the 'test_mode' tag to compare:"
echo "    test_mode=platform  (traditional threads)"
echo "    test_mode=virtual   (Java virtual threads)"
echo ""
echo "  InfluxDB:   http://localhost:8086"
echo "    database: k6"
echo "=========================================="
