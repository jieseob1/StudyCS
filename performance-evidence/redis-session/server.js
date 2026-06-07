/**
 * Redis 세션 관리 REST API 브리지
 *
 * k6는 HTTP로만 통신하므로 Redis HSET/HGET/DEL 명령을
 * HTTP 엔드포인트로 노출합니다.
 *
 * 실행: node server.js
 * 환경변수:
 *   REDIS_URL=redis://localhost:6379
 *   PORT=3000
 */

const express = require('express');
const { createClient } = require('redis');

const app = express();
app.use(express.json());

const redisClient = createClient({ url: process.env.REDIS_URL || 'redis://localhost:6379' });
redisClient.on('error', (err) => console.error('Redis client error:', err));
redisClient.connect().catch(console.error);

// ============================================================
// POST /sessions — 세션 생성 (Redis HSET)
// ============================================================
app.post('/sessions', async (req, res) => {
  const start = Date.now();
  try {
    const { sessionId, userId, data } = req.body;
    const key = 'session:' + sessionId;

    await redisClient.hSet(key, {
      userId: String(userId),
      createdAt: String(Date.now()),
      lastAccessedAt: String(Date.now()),
      data: JSON.stringify(data || {}),
    });

    // TTL 1시간
    await redisClient.expire(key, 3600);

    res.status(201).json({
      sessionId,
      created: true,
      latencyMs: Date.now() - start,
    });
  } catch (err) {
    console.error('session create error:', err.message);
    res.status(500).json({ error: err.message });
  }
});

// ============================================================
// GET /sessions/:sessionId — 세션 조회 (Redis HGETALL)
// ============================================================
app.get('/sessions/:sessionId', async (req, res) => {
  const start = Date.now();
  try {
    const key = 'session:' + req.params.sessionId;
    const session = await redisClient.hGetAll(key);

    if (!session || Object.keys(session).length === 0) {
      return res.status(404).json({ error: 'Session not found' });
    }

    // 마지막 접근 시간 갱신
    await redisClient.hSet(key, 'lastAccessedAt', String(Date.now()));

    res.json({ ...session, latencyMs: Date.now() - start });
  } catch (err) {
    console.error('session read error:', err.message);
    res.status(500).json({ error: err.message });
  }
});

// ============================================================
// DELETE /sessions/:sessionId — 세션 삭제 (Redis DEL)
// ============================================================
app.delete('/sessions/:sessionId', async (req, res) => {
  const start = Date.now();
  try {
    const key = 'session:' + req.params.sessionId;
    const deleted = await redisClient.del(key);

    res.json({
      deleted: deleted === 1,
      latencyMs: Date.now() - start,
    });
  } catch (err) {
    console.error('session delete error:', err.message);
    res.status(500).json({ error: err.message });
  }
});

// ============================================================
// GET /health — 헬스체크
// ============================================================
app.get('/health', async (req, res) => {
  try {
    await redisClient.ping();
    res.json({ status: 'ok', redis: 'connected' });
  } catch (err) {
    res.status(503).json({ status: 'error', redis: err.message });
  }
});

// ============================================================
// GET /stats — Redis 통계 (성능 모니터링용)
// ============================================================
app.get('/stats', async (req, res) => {
  try {
    const info = await redisClient.info('stats');
    const memory = await redisClient.info('memory');
    const keyspace = await redisClient.info('keyspace');
    res.json({ info, memory, keyspace });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
  console.log(`Session API listening on port ${PORT}`);
  console.log(`Redis URL: ${process.env.REDIS_URL || 'redis://localhost:6379'}`);
});
