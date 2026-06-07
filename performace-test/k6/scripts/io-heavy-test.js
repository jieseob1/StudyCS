import http from 'k6/http';
import { check, sleep } from 'k6';
import { Trend, Rate } from 'k6/metrics';
import { buildUrl, checkResponse, TEST_MODE } from '../lib/helpers.js';

// Custom metrics
const sleepEndpointDuration = new Trend('sleep_endpoint_duration', true);
const fanOutEndpointDuration = new Trend('fan_out_endpoint_duration', true);
const errorRate = new Rate('error_rate');

export const options = {
    executor: 'ramping-vus',
    stages: [
        { duration: '30s', target: 10 },
        { duration: '1m',  target: 50 },
        { duration: '1m',  target: 200 },
        { duration: '1m',  target: 500 },
        { duration: '1m',  target: 1000 },
        { duration: '30s', target: 0 },
    ],
    thresholds: {
        http_req_duration: ['p(95)<5000'],
        error_rate: ['rate<0.10'],
    },
    tags: {
        test_mode: TEST_MODE,
    },
};

export default function () {
    // Step 1: Sleep endpoint - primary I/O bound test demonstrating virtual thread advantage
    const sleepRes = http.get(buildUrl('/api/io/sleep?ms=500'), {
        tags: { endpoint: 'sleep' },
    });

    const sleepChecks = check(sleepRes, {
        ...checkResponse(sleepRes, 'sleep'),
        'sleep: duration reasonable': (r) => r.timings.duration < 5000,
    });

    sleepEndpointDuration.add(sleepRes.timings.duration);
    errorRate.add(!sleepChecks);

    // Step 2: Fan-out endpoint - concurrent outbound I/O calls
    const fanOutRes = http.get(buildUrl('/api/io/fan-out?calls=5&delayMs=200'), {
        tags: { endpoint: 'fan-out' },
    });

    const fanOutChecks = check(fanOutRes, {
        ...checkResponse(fanOutRes, 'fan-out'),
    });

    fanOutEndpointDuration.add(fanOutRes.timings.duration);
    errorRate.add(!fanOutChecks);

    // Pacing to avoid overwhelming the server on ramp-up
    sleep(0.5);
}
