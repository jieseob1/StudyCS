import http from 'k6/http';
import { check, sleep } from 'k6';
import { Trend, Rate } from 'k6/metrics';
import { buildUrl, checkResponse, TEST_MODE } from '../lib/helpers.js';

// Custom metrics
const mixedDuration = new Trend('mixed_workload_duration', true);
const errorRate = new Rate('error_rate');

export const options = {
    scenarios: {
        mixed_workload: {
            executor: 'ramping-arrival-rate',
            startRate: 10,
            timeUnit: '1s',
            preAllocatedVUs: 200,
            maxVUs: 2000,
            stages: [
                { duration: '1m', target: 50 },
                { duration: '2m', target: 200 },
                { duration: '1m', target: 50 },
            ],
        },
    },
    thresholds: {
        http_req_duration: ['p(95)<10000'],
        error_rate: ['rate<0.10'],
    },
    tags: {
        test_mode: TEST_MODE,
        workload: 'mixed',
    },
};

export default function () {
    const res = http.get(
        buildUrl('/api/mixed/realistic?dbDelayMs=50&httpDelayMs=100&cpuIterations=10000'),
        {
            tags: { endpoint: 'mixed-realistic' },
            timeout: '30s',
        }
    );

    const passed = check(res, {
        ...checkResponse(res, 'mixed-realistic'),
    });

    mixedDuration.add(res.timings.duration);
    errorRate.add(!passed);
}
