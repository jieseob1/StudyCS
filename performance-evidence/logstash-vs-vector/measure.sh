#!/usr/bin/env bash
# ============================================================
# Logstash vs Vector 메모리/CPU 사용량 측정 스크립트
#
# 실행 방법:
#   chmod +x measure.sh
#   ./measure.sh
#
# 출력:
#   - results.csv: 시계열 측정 데이터
#   - 콘솔: 최종 비교 요약
#
# 측정 항목:
#   - 메모리 사용량 (MiB)
#   - CPU 사용률 (%)
#   - 5분간 30회 샘플링 (10초 간격)
# ============================================================

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
RESULTS_FILE="${SCRIPT_DIR}/results.csv"
COMPOSE_FILE="${SCRIPT_DIR}/docker-compose.yml"

# 색상 코드
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color
BOLD='\033[1m'

log_info()  { echo -e "${BLUE}[INFO]${NC}  $*"; }
log_ok()    { echo -e "${GREEN}[OK]${NC}    $*"; }
log_warn()  { echo -e "${YELLOW}[WARN]${NC}  $*"; }
log_error() { echo -e "${RED}[ERROR]${NC} $*"; }
log_step()  { echo -e "\n${CYAN}${BOLD}==> $*${NC}"; }

# ============================================================
# 사전 확인
# ============================================================
check_prerequisites() {
    log_step "사전 요구사항 확인"

    if ! command -v docker &>/dev/null; then
        log_error "Docker가 설치되어 있지 않습니다."
        exit 1
    fi
    log_ok "Docker: $(docker --version | cut -d' ' -f3 | tr -d ',')"

    if ! docker info &>/dev/null; then
        log_error "Docker 데몬이 실행되지 않고 있습니다."
        exit 1
    fi
    log_ok "Docker 데몬 실행 중"

    if ! command -v docker compose &>/dev/null && ! command -v docker-compose &>/dev/null; then
        log_error "docker compose 또는 docker-compose가 필요합니다."
        exit 1
    fi
    log_ok "docker-compose 사용 가능"

    if ! command -v bc &>/dev/null; then
        log_warn "bc가 없어 통계 계산이 제한될 수 있습니다."
    fi
}

# docker compose 명령 선택 (v1/v2 호환)
compose_cmd() {
    if command -v docker compose &>/dev/null; then
        docker compose -f "${COMPOSE_FILE}" "$@"
    else
        docker-compose -f "${COMPOSE_FILE}" "$@"
    fi
}

# ============================================================
# CSV 헤더 초기화
# ============================================================
init_results() {
    echo "timestamp,container,memory_usage_mib,memory_limit_mib,memory_percent,cpu_percent" > "${RESULTS_FILE}"
    log_ok "결과 파일 초기화: ${RESULTS_FILE}"
}

# ============================================================
# 컨테이너 메모리/CPU 측정 (단일 샘플)
# ============================================================
measure_once() {
    local ts
    ts="$(date '+%Y-%m-%d %H:%M:%S')"

    # docker stats: 단일 샘플 (-no-stream)
    # --format: 필요한 필드만 추출
    while IFS="|" read -r name mem_usage mem_limit mem_pct cpu_pct; do
        # 컨테이너 이름에서 perf-logstash 또는 perf-vector만 필터
        if [[ "${name}" == "perf-logstash" || "${name}" == "perf-vector" ]]; then
            # 단위 변환: "256MiB" → 256, "1.2GiB" → 1228.8
            mem_mib=$(convert_to_mib "${mem_usage}")
            limit_mib=$(convert_to_mib "${mem_limit}")
            # % 뒤 기호 제거
            mem_pct_clean=$(echo "${mem_pct}" | tr -d '%')
            cpu_pct_clean=$(echo "${cpu_pct}" | tr -d '%')

            echo "${ts},${name},${mem_mib},${limit_mib},${mem_pct_clean},${cpu_pct_clean}" >> "${RESULTS_FILE}"
            printf "  %-20s MEM: %6.1f MiB  CPU: %5.2f%%\n" \
                "${name}" "${mem_mib}" "${cpu_pct_clean}"
        fi
    done < <(docker stats --no-stream --format "{{.Name}}|{{.MemUsage}}|{{.MemPerc}}|{{.CPUPerc}}" 2>/dev/null \
        | awk -F'|' '{
            split($2, a, " / ");
            print $1 "|" a[1] "|" a[2] "|" $3 "|" $4
        }')
}

# 메모리 값을 MiB로 변환
convert_to_mib() {
    local val="$1"
    # 숫자와 단위 분리
    local num unit
    num=$(echo "${val}" | sed 's/[^0-9.]//g')
    unit=$(echo "${val}" | sed 's/[0-9.]//g' | tr '[:lower:]' '[:upper:]')

    case "${unit}" in
        GIB|GB) echo "${num} * 1024" | bc -l 2>/dev/null || echo "0" ;;
        MIB|MB) echo "${num}" ;;
        KIB|KB) echo "${num} / 1024" | bc -l 2>/dev/null || echo "0" ;;
        *)      echo "${num}" ;;
    esac
}

# ============================================================
# 컨테이너 기동 대기
# ============================================================
wait_for_startup() {
    local timeout=120
    local elapsed=0

    log_step "컨테이너 기동 대기 (최대 ${timeout}초)"
    log_info "Elasticsearch 헬스체크 통과 대기 중..."

    while [[ ${elapsed} -lt ${timeout} ]]; do
        if docker inspect --format='{{.State.Health.Status}}' perf-elasticsearch 2>/dev/null | grep -q "healthy"; then
            log_ok "Elasticsearch 준비 완료 (${elapsed}초 소요)"
            break
        fi
        printf "\r  대기 중... %d/%d초" "${elapsed}" "${timeout}"
        sleep 5
        elapsed=$((elapsed + 5))
    done

    if [[ ${elapsed} -ge ${timeout} ]]; then
        log_warn "Elasticsearch 헬스체크 타임아웃. 계속 진행합니다."
    fi

    log_info "Logstash/Vector 안정화 대기 (30초)..."
    sleep 30
    log_ok "안정화 완료. 측정 시작"
}

# ============================================================
# 메인 측정 루프 (5분, 10초 간격 = 30회)
# ============================================================
run_measurement() {
    local duration=300  # 5분
    local interval=10   # 10초 간격
    local samples=$((duration / interval))
    local elapsed=0

    log_step "성능 측정 시작 (${duration}초, ${interval}초 간격, 총 ${samples}회)"
    echo ""

    local sample_num=1
    while [[ ${elapsed} -lt ${duration} ]]; do
        echo -e "${YELLOW}[샘플 ${sample_num}/${samples}]${NC} $(date '+%H:%M:%S')"
        measure_once
        sample_num=$((sample_num + 1))
        elapsed=$((elapsed + interval))
        [[ ${elapsed} -lt ${duration} ]] && sleep ${interval}
    done

    log_ok "측정 완료. 총 ${samples}개 샘플 수집"
}

# ============================================================
# 결과 분석 및 요약 출력
# ============================================================
print_summary() {
    log_step "측정 결과 요약"

    if [[ ! -f "${RESULTS_FILE}" ]]; then
        log_error "결과 파일이 없습니다: ${RESULTS_FILE}"
        return
    fi

    echo ""
    echo -e "${BOLD}┌─────────────────────────────────────────────────────────────┐${NC}"
    echo -e "${BOLD}│           Logstash vs Vector 성능 비교 결과                  │${NC}"
    echo -e "${BOLD}└─────────────────────────────────────────────────────────────┘${NC}"
    echo ""

    # Python으로 통계 계산 (bc보다 정확)
    if command -v python3 &>/dev/null; then
        python3 << PYEOF
import csv
import statistics

data = {"perf-logstash": {"mem": [], "cpu": []}, "perf-vector": {"mem": [], "cpu": []}}

with open("${RESULTS_FILE}") as f:
    reader = csv.DictReader(f)
    for row in reader:
        container = row["container"]
        if container in data:
            try:
                data[container]["mem"].append(float(row["memory_usage_mib"]))
                data[container]["cpu"].append(float(row["cpu_percent"]))
            except (ValueError, KeyError):
                pass

print(f"{'컨테이너':<22} {'평균 MEM':>12} {'최대 MEM':>12} {'평균 CPU':>12} {'최대 CPU':>12}")
print("-" * 72)

results = {}
for container, metrics in data.items():
    if not metrics["mem"]:
        print(f"{container:<22} 데이터 없음")
        continue
    avg_mem = statistics.mean(metrics["mem"])
    max_mem = max(metrics["mem"])
    avg_cpu = statistics.mean(metrics["cpu"])
    max_cpu = max(metrics["cpu"])
    results[container] = {"avg_mem": avg_mem, "max_mem": max_mem, "avg_cpu": avg_cpu, "max_cpu": max_cpu}
    print(f"{container:<22} {avg_mem:>10.1f}MB {max_mem:>10.1f}MB {avg_cpu:>10.2f}% {max_cpu:>10.2f}%")

print()
if "perf-logstash" in results and "perf-vector" in results:
    ls = results["perf-logstash"]
    vc = results["perf-vector"]
    mem_reduction = (1 - vc["avg_mem"] / ls["avg_mem"]) * 100 if ls["avg_mem"] > 0 else 0
    cpu_reduction = (1 - vc["avg_cpu"] / ls["avg_cpu"]) * 100 if ls["avg_cpu"] > 0 else 0
    print(f"[결론] Vector의 메모리 사용량: Logstash 대비 {mem_reduction:.1f}% 감소")
    print(f"[결론] Vector의 CPU 사용률:    Logstash 대비 {cpu_reduction:.1f}% 감소")
    print()
    print(f"이력서 작성 근거: 'Logstash → Vector 전환으로 메모리 {mem_reduction:.0f}% 절감'")
PYEOF
    else
        log_warn "python3가 없어 상세 통계를 계산할 수 없습니다."
        log_info "결과 파일을 직접 확인하세요: ${RESULTS_FILE}"
    fi

    echo ""
    log_info "원시 데이터: ${RESULTS_FILE}"
    log_info "그래프 생성: python3 -c \"import pandas as pd; import matplotlib.pyplot as plt; ...\""
}

# ============================================================
# 클린업
# ============================================================
cleanup() {
    local exit_code=$?
    if [[ ${exit_code} -ne 0 ]]; then
        log_warn "비정상 종료 감지. 컨테이너를 정리합니다..."
        compose_cmd down --remove-orphans 2>/dev/null || true
    fi
    exit ${exit_code}
}
trap cleanup EXIT INT TERM

# ============================================================
# 메인 실행 흐름
# ============================================================
main() {
    echo ""
    echo -e "${BOLD}${CYAN}=================================================${NC}"
    echo -e "${BOLD}${CYAN}  Logstash vs Vector 성능 비교 측정 도구         ${NC}"
    echo -e "${BOLD}${CYAN}=================================================${NC}"
    echo ""

    check_prerequisites
    init_results

    log_step "Docker Compose 환경 시작"
    compose_cmd up -d --build
    log_ok "컨테이너 기동 명령 완료"

    wait_for_startup
    run_measurement
    print_summary

    echo ""
    log_step "측정 완료"
    log_info "컨테이너를 유지합니다. 종료하려면: docker compose -f ${COMPOSE_FILE} down"
    echo ""
}

main "$@"
