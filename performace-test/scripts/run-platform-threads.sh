#!/bin/bash
set -e

echo "=========================================="
echo "  Virtual Threads Benchmark"
echo "  Mode: PLATFORM THREADS (Traditional)"
echo "=========================================="

# Build the application
echo "[1/3] Building application..."
cd "$(dirname "$0")/.."
./gradlew bootJar -q

# Start the application
echo "[2/3] Starting Spring Boot with Platform Threads..."
java -jar build/libs/virtual-threads-benchmark-0.0.1-SNAPSHOT.jar \
  --spring.profiles.active=platform-threads \
  -Djdk.tracePinnedThreads=short &
APP_PID=$!

# Wait for app to be ready
echo "Waiting for application to start..."
for i in $(seq 1 30); do
  if curl -s http://localhost:8080/actuator/health | grep -q '"UP"'; then
    echo "Application started successfully!"
    break
  fi
  if [ $i -eq 30 ]; then
    echo "Application failed to start!"
    kill $APP_PID 2>/dev/null
    exit 1
  fi
  sleep 1
done

# Run k6 test
echo "[3/3] Running k6 I/O Heavy Test..."
k6 run --out influxdb=http://localhost:8086/k6 \
  -e TEST_MODE=platform \
  -e BASE_URL=http://localhost:8080 \
  k6/scripts/io-heavy-test.js

echo "Test complete. Stopping application..."
kill $APP_PID 2>/dev/null
wait $APP_PID 2>/dev/null
echo "Done!"
