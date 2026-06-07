#!/bin/bash
# Generate 10,000 sample Java log lines including multiline stack traces
OUTPUT_FILE="$(dirname "$0")/app.log"
> "$OUTPUT_FILE"

LEVELS=("INFO" "WARN" "ERROR" "DEBUG")
CLASSES=("com.tmax.superoffice.SessionManager" "com.tmax.superoffice.DocumentService" "com.tmax.superoffice.LockServer" "com.tmax.webtob.RequestHandler" "com.tmax.superoffice.OperationManager")
MESSAGES_INFO=("세션 생성 완료 userId=%d documentId=%d" "문서 저장 성공 documentId=%d" "Lock 획득 성공 elementId=%d" "요청 처리 완료 duration=%dms" "Operation 메시지 전송 count=%d")
MESSAGES_WARN=("세션 만료 임박 userId=%d ttl=%ds" "Lock 대기 시간 초과 경고 elementId=%d waitTime=%dms" "메모리 사용량 높음 used=%dMB" "DB 커넥션 풀 부족 available=%d" "Kafka consumer lag 증가 lag=%d")
MESSAGES_ERROR=("NullPointerException 발생" "Connection refused: elasticsearch:9200" "Lock 획득 실패 timeout" "OOM: Java heap space" "DB 트랜잭션 데드락 감지")
STACK_TRACES=(
"java.lang.NullPointerException: Cannot invoke method on null object
\tat com.tmax.superoffice.SessionManager.getSession(SessionManager.java:142)
\tat com.tmax.superoffice.SessionManager.validateSession(SessionManager.java:98)
\tat com.tmax.superoffice.DocumentService.processRequest(DocumentService.java:67)
\tat com.tmax.superoffice.handler.RequestHandler.handle(RequestHandler.java:45)
\tat org.springframework.web.servlet.FrameworkServlet.service(FrameworkServlet.java:897)"
"java.util.concurrent.TimeoutException: Lock acquisition timeout after 30000ms
\tat com.tmax.superoffice.LockServer.acquireLock(LockServer.java:234)
\tat com.tmax.superoffice.LockServer.lockElement(LockServer.java:178)
\tat com.tmax.superoffice.OperationManager.executeWithLock(OperationManager.java:156)
\tat com.tmax.superoffice.DocumentService.updateElement(DocumentService.java:89)"
"org.elasticsearch.ElasticsearchException: Connection refused
\tat org.elasticsearch.client.RestClient.performRequest(RestClient.java:312)
\tat com.tmax.superoffice.monitoring.LogReporter.sendReport(LogReporter.java:78)
\tat com.tmax.superoffice.monitoring.CronJobScheduler.execute(CronJobScheduler.java:45)"
)

THREADS=("main" "http-nio-8080-exec-1" "http-nio-8080-exec-2" "kafka-consumer-1" "scheduler-1" "redis-lettuce-io-1" "pool-3-thread-1")

echo "로그 생성 시작..."
BASE_TS=$(date -j -f "%Y-%m-%d %H:%M:%S" "2024-09-30 09:00:00" "+%s" 2>/dev/null || date -d "2024-09-30 09:00:00" "+%s" 2>/dev/null || echo "1727676000")

for i in $(seq 1 10000); do
  TS=$((BASE_TS + i))
  FORMATTED_TS=$(date -r $TS "+%Y-%m-%d %H:%M:%S" 2>/dev/null || date -d "@$TS" "+%Y-%m-%d %H:%M:%S" 2>/dev/null || echo "2024-09-30 09:$(printf '%02d' $((i/60%60))):$(printf '%02d' $((i%60)))")
  MS=$(printf '%03d' $((RANDOM % 1000)))
  THREAD=${THREADS[$((RANDOM % ${#THREADS[@]}))]}
  CLASS=${CLASSES[$((RANDOM % ${#CLASSES[@]}))]}

  # 80% INFO, 10% WARN, 8% ERROR (with stack trace), 2% DEBUG
  RAND=$((RANDOM % 100))
  if [ $RAND -lt 80 ]; then
    MSG_TEMPLATE=${MESSAGES_INFO[$((RANDOM % ${#MESSAGES_INFO[@]}))]}
    printf -v MSG "$MSG_TEMPLATE" $((RANDOM % 10000)) $((RANDOM % 10000))
    echo "${FORMATTED_TS}.${MS} [${THREAD}] INFO  ${CLASS} - ${MSG}" >> "$OUTPUT_FILE"
  elif [ $RAND -lt 90 ]; then
    MSG_TEMPLATE=${MESSAGES_WARN[$((RANDOM % ${#MESSAGES_WARN[@]}))]}
    printf -v MSG "$MSG_TEMPLATE" $((RANDOM % 10000)) $((RANDOM % 10000))
    echo "${FORMATTED_TS}.${MS} [${THREAD}] WARN  ${CLASS} - ${MSG}" >> "$OUTPUT_FILE"
  elif [ $RAND -lt 98 ]; then
    MSG=${MESSAGES_ERROR[$((RANDOM % ${#MESSAGES_ERROR[@]}))]}
    TRACE=${STACK_TRACES[$((RANDOM % ${#STACK_TRACES[@]}))]}
    echo "${FORMATTED_TS}.${MS} [${THREAD}] ERROR ${CLASS} - ${MSG}" >> "$OUTPUT_FILE"
    echo -e "${TRACE}" >> "$OUTPUT_FILE"
  else
    echo "${FORMATTED_TS}.${MS} [${THREAD}] DEBUG ${CLASS} - Debug checkpoint i=${i} memory=$(( RANDOM % 1024 ))MB" >> "$OUTPUT_FILE"
  fi
done
echo "로그 생성 완료: $(wc -l < "$OUTPUT_FILE") lines -> $OUTPUT_FILE"
