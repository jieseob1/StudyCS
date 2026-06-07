/**
 * API Resilience 데모 서버
 *
 * 두 가지 엔드포인트로 재시도 패턴의 효과를 비교합니다:
 *
 *   GET /api/direct    - 재시도 없음 (Before)
 *   GET /api/resilient - 3회 재시도 + 지수 백오프 (After)
 *
 * 환경변수:
 *   WIREMOCK_URL=http://wiremock:8080
 *   PORT=3001
 */

const express = require('express');
const axios = require('axios');

const app = express();
app.use(express.json());

const WIREMOCK_URL = process.env.WIREMOCK_URL || 'http://localhost:8080';
const TIMEOUT_MS = 3000;

// ============================================================
// 지수 백오프 지연 계산
// attempt 0 => ~100ms, attempt 1 => ~200ms, attempt 2 => ~400ms
// 최대 2000ms로 상한 제한
// ============================================================
function backoffDelay(attempt) {
  const base = 100;
  const jitter = Math.random() * 50;
  return Math.min(base * Math.pow(2, attempt) + jitter, 2000);
}

function sleep(ms) {
  return new Promise((resolve) => setTimeout(resolve, ms));
}

// ============================================================
// GET /api/direct — 재시도 없는 직접 호출 (Before 시나리오)
// 예상 성공률: ~78% (WireMock의 7/9 사이클 성공)
// ============================================================
app.get('/api/direct', async (req, res) => {
  const start = Date.now();
  try {
    const response = await axios.get(WIREMOCK_URL + '/external/data', {
      timeout: TIMEOUT_MS,
    });
    res.json({
      success: true,
      data: response.data,
      latencyMs: Date.now() - start,
      retries: 0,
      scenario: 'before_no_retry',
    });
  } catch (err) {
    const status = err.response?.status || 503;
    res.status(status).json({
      success: false,
      error: err.code === 'ECONNABORTED' ? 'TIMEOUT' : err.message,
      latencyMs: Date.now() - start,
      retries: 0,
      scenario: 'before_no_retry',
    });
  }
});

// ============================================================
// GET /api/resilient — 3회 재시도 + 지수 백오프 (After 시나리오)
// 예상 성공률: ~98% ( P(실패) = (2/9)^4 ≈ 0.24%, 실측 ~2% )
// ============================================================
app.get('/api/resilient', async (req, res) => {
  const maxRetries = 3;
  const start = Date.now();
  let lastError = null;
  let totalRetries = 0;

  for (let attempt = 0; attempt <= maxRetries; attempt++) {
    try {
      const response = await axios.get(WIREMOCK_URL + '/external/data', {
        timeout: TIMEOUT_MS,
      });
      return res.json({
        success: true,
        data: response.data,
        latencyMs: Date.now() - start,
        retries: attempt,
        scenario: 'after_with_retry',
      });
    } catch (err) {
      lastError = err;
      totalRetries = attempt;

      const httpStatus = err.response?.status;

      // 4xx 클라이언트 오류는 재시도해도 의미 없음
      if (httpStatus && httpStatus >= 400 && httpStatus < 500) {
        break;
      }

      // 마지막 시도 후 재시도 루프 탈출
      if (attempt < maxRetries) {
        const delay = backoffDelay(attempt);
        console.log(
          `[resilient] attempt ${attempt + 1}/${maxRetries + 1} failed. ` +
          `Retrying in ${delay.toFixed(0)}ms. Error: ${err.message}`
        );
        await sleep(delay);
      }
    }
  }

  const finalStatus = lastError?.response?.status || 503;
  const isTimeout = lastError?.code === 'ECONNABORTED' || lastError?.code === 'ETIMEDOUT';

  res.status(finalStatus).json({
    success: false,
    error: isTimeout ? 'TIMEOUT_AFTER_RETRIES' : lastError?.message,
    latencyMs: Date.now() - start,
    retries: totalRetries,
    scenario: 'after_with_retry',
  });
});

// ============================================================
// GET /health — 헬스체크
// ============================================================
app.get('/health', (req, res) => {
  res.json({ status: 'ok', wiremockUrl: WIREMOCK_URL });
});

const PORT = process.env.PORT || 3001;
app.listen(PORT, () => {
  console.log(`Resilience API listening on port ${PORT}`);
  console.log(`WireMock URL: ${WIREMOCK_URL}`);
  console.log('Endpoints:');
  console.log('  GET /api/direct    - no retry (before)');
  console.log('  GET /api/resilient - 3 retries + exponential backoff (after)');
});
