# Virtual Threads vs Platform Threads 성능 비교 벤치마크

Java 25의 Virtual Threads와 기존 Platform Threads의 성능을 다양한 워크로드(CPU-bound, I/O-bound, Mixed)에서 비교하는 벤치마크 애플리케이션입니다.

K6 부하 테스트와 Prometheus + Grafana + InfluxDB 모니터링 스택을 통해 두 스레드 모델의 처리량, 응답 시간, 리소스 사용량 차이를 시각적으로 확인할 수 있습니다.

---

## 기술 스택

| 구분 | 기술 |
|------|------|
| Language | Java 25 |
| Framework | Spring Boot 3.4.4 |
| Build | Gradle 9.1.0 (Kotlin DSL) |
| Database | H2 (In-Memory) |
| 부하 테스트 | K6 |
| 메트릭 수집 | Prometheus |
| 시각화 | Grafana |
| K6 결과 저장 | InfluxDB |
| 인프라 | Docker Compose |

---

## 프로젝트 구조

```
virtual-threads-benchmark/
├── build.gradle.kts                    # Gradle 빌드 설정 (Java 25, Spring Boot 3.4.4)
├── settings.gradle.kts                 # 프로젝트 이름 설정
├── docker-compose.yml                  # 모니터링 스택 (Prometheus, Grafana, InfluxDB, httpbin)
│
├── scripts/
│   ├── run-comparison.sh               # Platform ↔ Virtual 자동 비교 테스트 (전체 파이프라인)
│   ├── run-platform-threads.sh         # Platform Threads 단독 테스트
│   └── run-virtual-threads.sh          # Virtual Threads 단독 테스트
│
├── k6/
│   ├── lib/
│   │   └── helpers.js                  # 공용 유틸 (BASE_URL, checkResponse 등)
│   └── scripts/
│       ├── cpu-bound-test.js           # CPU 집약 워크로드 테스트
│       ├── io-heavy-test.js            # I/O 집약 워크로드 테스트 (ramp-up)
│       ├── mixed-workload-test.js      # 혼합 워크로드 테스트
│       └── full-comparison-test.js     # CPU + I/O + Mixed 동시 실행 통합 테스트
│
├── monitoring/
│   ├── prometheus/
│   │   └── prometheus.yml              # Prometheus 스크랩 설정
│   └── grafana/
│       └── provisioning/
│           ├── datasources/
│           │   └── datasources.yml     # Grafana 데이터소스 (Prometheus, InfluxDB)
│           └── dashboards/
│               └── dashboards.yml      # Grafana 대시보드 프로비저닝 설정
│
└── src/main/
    ├── java/com/perftest/vthreads/
    │   ├── VirtualThreadsBenchmarkApplication.java   # Spring Boot 메인 클래스
    │   ├── config/
    │   │   ├── ThreadPoolConfig.java                 # 스레드 풀 설정
    │   │   └── WebClientConfig.java                  # WebClient / RestClient 설정
    │   ├── controller/
    │   │   ├── CpuWorkloadController.java            # CPU 워크로드 API
    │   │   ├── IoWorkloadController.java             # I/O 워크로드 API
    │   │   ├── MixedWorkloadController.java          # 혼합 워크로드 API
    │   │   └── DiagnosticsController.java            # 스레드 진단 API
    │   ├── service/
    │   │   ├── CpuSimulationService.java             # CPU 시뮬레이션 (소수 계산, 해싱)
    │   │   ├── IoSimulationService.java              # I/O 시뮬레이션 (sleep, DB, HTTP)
    │   │   └── MixedWorkloadService.java             # 복합 워크로드 (DB → HTTP → CPU)
    │   ├── metrics/
    │   │   └── ThreadMetricsExporter.java            # JVM 스레드 메트릭 Prometheus 노출
    │   └── model/
    │       └── WorkloadResponse.java                 # 공통 응답 모델
    └── resources/
        ├── application.yml                           # 공통 설정
        ├── application-platform-threads.yml          # Platform Threads 프로파일
        └── application-virtual-threads.yml           # Virtual Threads 프로파일
```

---

## 사전 요구사항

- **Java 25** — 현재 프로젝트 toolchain 기준
- **Docker & Docker Compose** — 모니터링 스택 실행
- **K6** — 부하 테스트 도구 ([설치 가이드](https://k6.io/docs/get-started/installation/))

### 필요 포트

| 포트 | 서비스 |
|------|--------|
| 8080 | Spring Boot 애플리케이션 |
| 3000 | Grafana |
| 9090 | Prometheus |
| 8086 | InfluxDB |
| 8888 | httpbin (delay-service) |

---

## 빌드 및 실행

### 1. 빌드

```bash
./gradlew bootJar
```

빌드 결과물: `build/libs/virtual-threads-benchmark-0.0.1-SNAPSHOT.jar`

### 2. 모니터링 스택 시작

```bash
docker-compose up -d
```

Prometheus, Grafana, InfluxDB, httpbin(delay-service)이 시작됩니다.

### 3. 애플리케이션 실행

**Platform Threads 모드** (기존 방식, Tomcat 스레드 풀 최대 200개):

```bash
java -jar build/libs/virtual-threads-benchmark-0.0.1-SNAPSHOT.jar \
  --spring.profiles.active=platform-threads \
  -Djdk.tracePinnedThreads=short
```

**Virtual Threads 모드**:

```bash
java -jar build/libs/virtual-threads-benchmark-0.0.1-SNAPSHOT.jar \
  --spring.profiles.active=virtual-threads \
  -Djdk.tracePinnedThreads=short
```

로컬에서 `8080` 포트가 이미 사용 중이면 다음처럼 포트를 바꿔 실행합니다:

```bash
./gradlew bootRun --args='--spring.profiles.active=virtual-threads --server.port=18080 --app.self-base-url=http://localhost:18080'
```

### 4. 자동 비교 테스트

빌드 → 모니터링 스택 기동 → Platform Threads 테스트 → Virtual Threads 테스트를 자동으로 순차 실행합니다:

```bash
./scripts/run-comparison.sh
```

### 5. 개별 모드 테스트

```bash
# Platform Threads만 테스트
./scripts/run-platform-threads.sh

# Virtual Threads만 테스트
./scripts/run-virtual-threads.sh
```

---

## API 엔드포인트

### CPU 워크로드

| 메서드 | 경로 | 파라미터 | 설명 |
|--------|------|----------|------|
| GET | `/api/cpu/compute` | `iterations` (기본: 1000000) | 소수 계산 (trial division). CPU-bound이므로 Virtual Threads 이점 없음 |
| GET | `/api/cpu/hash` | `payload` (기본: hello), `rounds` (기본: 100) | SHA-256 반복 해싱 |

### I/O 워크로드

| 메서드 | 경로 | 파라미터 | 설명 |
|--------|------|----------|------|
| GET | `/api/io/sleep` | `ms` (기본: 500) | Thread.sleep으로 I/O 대기 시뮬레이션. Virtual Threads의 핵심 이점 확인 |
| GET | `/api/io/db-query` | `delayMs` (기본: 200) | H2 DB 쿼리 + sleep으로 느린 DB 쿼리 시뮬레이션 |
| GET | `/api/io/http-call` | `delayMs` (기본: 300) | 자기 자신의 `/api/io/sleep`을 호출하는 blocking HTTP 요청 |
| GET | `/api/io/fan-out` | `calls` (기본: 5), `delayMs` (기본: 200) | N개의 동시 HTTP 요청을 병렬 실행 |

### Mixed 워크로드

| 메서드 | 경로 | 파라미터 | 설명 |
|--------|------|----------|------|
| GET | `/api/mixed/realistic` | `dbDelayMs` (기본: 50), `httpDelayMs` (기본: 100), `cpuIterations` (기본: 10000) | DB I/O → HTTP I/O → CPU 계산 3단계 현실적 워크로드 |

### 진단

| 메서드 | 경로 | 설명 |
|--------|------|------|
| GET | `/api/diagnostics/thread-info` | 현재 요청을 처리하는 스레드 정보 (이름, isVirtual, ID 등) |
| GET | `/api/diagnostics/thread-count` | JVM 스레드 통계 (활성 스레드 수, 피크, 데드락 감지) |

### Virtual Thread 동작 추적

| 메서드 | 경로 | 파라미터 | 설명 |
|--------|------|----------|------|
| GET | `/api/virtual-threads/lifecycle` | `tasks` (기본: 3), `parkMs` (기본: 300), `monitorIntervalMs` (기본: 25), `cpuMs` (기본: 20) | 애플리케이션 코드에서 `Thread.ofVirtual()`로 virtual thread를 직접 만들고 `NEW → WAITING → TIMED_WAITING → TERMINATED` 상태 변화를 이벤트/샘플 타임라인으로 반환 |

예시:

```bash
curl "http://localhost:8080/api/virtual-threads/lifecycle?tasks=3&parkMs=500&monitorIntervalMs=25&cpuMs=20"
```

`18080` 포트로 실행했다면:

```bash
curl "http://localhost:18080/api/virtual-threads/lifecycle?tasks=3&parkMs=500&monitorIntervalMs=25&cpuMs=20"
```

응답에서 보면 좋은 필드:

| 필드 | 의미 |
|------|------|
| `springVirtualThreadsEnabled` | Spring Boot 요청 처리 스레드가 virtual thread 모드인지 여부 |
| `requestThread.virtual` | 이 API 요청을 처리한 스레드가 virtual thread인지 여부 |
| `events` | 각 virtual thread 내부에서 기록한 생성, 시작, 대기, 재개, 종료 이벤트 |
| `samples` | 외부 샘플러가 관찰한 각 virtual thread의 `Thread.State` |
| `summary.observedStates` | 이번 실행에서 실제 관찰된 Java thread 상태 목록 |

`/api/virtual-threads/lifecycle`은 Spring의 요청 처리 모드와 별개로 애플리케이션 코드에서 virtual thread를 직접 생성합니다. 따라서 platform profile로 실행해도 lab task는 virtual thread이고, virtual profile로 실행하면 요청 스레드까지 virtual thread인 차이를 함께 볼 수 있습니다.

### Actuator

| 메서드 | 경로 | 설명 |
|--------|------|------|
| GET | `/actuator/health` | 애플리케이션 헬스 체크 |
| GET | `/actuator/prometheus` | Prometheus 메트릭 엔드포인트 |

---

## K6 부하 테스트

### 테스트 스크립트

| 스크립트 | 워크로드 | 시나리오 | 설명 |
|----------|----------|----------|------|
| `cpu-bound-test.js` | CPU | 100 VUs, 2분 고정 | `/api/cpu/compute` 반복 호출 |
| `io-heavy-test.js` | I/O | 10→50→200→500→1000 VUs 점진 증가 | `/api/io/sleep` + `/api/io/fan-out` 호출 |
| `mixed-workload-test.js` | Mixed | 초당 10→200→50 요청 (ramping arrival rate) | `/api/mixed/realistic` 호출 |
| `full-comparison-test.js` | 전체 | CPU + I/O + Mixed 동시 실행 | 모든 워크로드를 동시에 실행하는 통합 테스트 |

### 실행 방법

```bash
# 개별 테스트 실행
k6 run -e TEST_MODE=virtual -e BASE_URL=http://localhost:8080 k6/scripts/io-heavy-test.js

# InfluxDB에 결과 저장 (Grafana에서 시각화)
k6 run --out influxdb=http://localhost:8086/k6 \
  -e TEST_MODE=virtual \
  -e BASE_URL=http://localhost:8080 \
  k6/scripts/io-heavy-test.js
```

### 환경 변수

| 변수 | 기본값 | 설명 |
|------|--------|------|
| `BASE_URL` | `http://localhost:8080` | 테스트 대상 애플리케이션 URL |
| `TEST_MODE` | `unknown` | 테스트 모드 태그 (`platform` 또는 `virtual`) — Grafana에서 필터링용 |

---

## 모니터링

### 접속 정보

| 서비스 | URL | 인증 |
|--------|-----|------|
| Grafana | http://localhost:3000 | admin / admin |
| Prometheus | http://localhost:9090 | - |
| InfluxDB | http://localhost:8086 | - (인증 비활성화) |
| H2 Console | http://localhost:8080/h2-console | sa / (빈 문자열) |

### Grafana 데이터소스

- **Prometheus** — Spring Boot Actuator 메트릭 (JVM, 스레드, HTTP 요청 등)
- **InfluxDB** — K6 부하 테스트 결과 (database: `k6`)

### 주요 모니터링 항목

- **JVM 스레드 수**: Platform Threads 모드에서는 동시 요청 수만큼 증가, Virtual Threads에서는 캐리어 스레드 수(= CPU 코어)로 유지
- **HTTP 요청 처리 시간**: p50, p95, p99 응답 시간 비교
- **처리량 (Throughput)**: 초당 처리 요청 수
- **에러율**: 스레드 풀 고갈로 인한 거부 비율

Grafana에서 `test_mode` 태그(`platform` / `virtual`)로 필터링하여 두 모드의 결과를 비교할 수 있습니다.

---

## 설정 파일

### application.yml (공통)

- 서버 포트: `8080`
- H2 인메모리 DB (`jdbc:h2:mem:testdb`)
- Actuator 엔드포인트: `health`, `info`, `prometheus`, `metrics` 노출
- Prometheus 메트릭 내보내기 활성화
- 자기 참조 URL: `app.self-base-url=http://localhost:8080`

### application-platform-threads.yml

```yaml
spring.threads.virtual.enabled: false   # Virtual Threads 비활성화
server.tomcat.threads.max: 200          # Tomcat 최대 스레드 200개
server.tomcat.threads.min-spare: 10     # 최소 유휴 스레드 10개
```

### application-virtual-threads.yml

```yaml
spring.threads.virtual.enabled: true    # Virtual Threads 활성화
```

Virtual Threads 모드에서는 Tomcat이 요청마다 새로운 Virtual Thread를 생성하므로 `max-threads` 설정이 무시됩니다.

---

## JVM 옵션

| 옵션 | 설명 |
|------|------|
| `-Djdk.tracePinnedThreads=short` | Virtual Thread가 캐리어 스레드에 고정(pinned)될 때 경고 로그 출력. `synchronized` 블록이나 네이티브 메서드에서 발생 |

---

## 핵심 관찰 포인트

1. **I/O-bound 워크로드**: Virtual Threads가 월등한 처리량을 보여줌 — 수천 개의 동시 요청에도 스레드 풀 고갈 없음
2. **CPU-bound 워크로드**: 두 모드 간 성능 차이 거의 없음 — 캐리어 스레드(= CPU 코어 수)가 병목
3. **Mixed 워크로드**: I/O 비중이 높을수록 Virtual Threads의 이점이 커짐
4. **스레드 수**: Platform Threads 모드의 `peakThreadCount`는 동시성에 비례하여 증가하지만, Virtual Threads 모드에서는 낮게 유지됨
