import http from 'k6/http';
import { check, sleep } from 'k6';
import { Trend, Rate } from 'k6/metrics';
import { buildUrl, checkResponse, TEST_MODE } from '../lib/helpers.js';

// Custom metrics
const cpuDuration = new Trend('cpu_compute_duration', true);
const errorRate = new Rate('error_rate');

export const options = {
    scenarios: {
        cpu_bound: {
            executor: 'constant-vus',
            vus: 100,
            duration: '2m',
        },
    },
    thresholds: {
        http_req_duration: ['p(95)<30000'],
        error_rate: ['rate<0.05'],
    },
    tags: {
        test_mode: TEST_MODE,
        workload: 'cpu',
    },
};

export default function () {
    const res = http.get(buildUrl('/api/cpu/compute?iterations=1000000'), {
        tags: { endpoint: 'cpu-compute' },
        timeout: '60s',
    });

    const passed = check(res, {
        ...checkResponse(res, 'cpu-compute'),
    });

    cpuDuration.add(res.timings.duration);
    errorRate.add(!passed);

    // Light pacing - CPU work is inherently throttled by compute time
    sleep(0.1);
}
