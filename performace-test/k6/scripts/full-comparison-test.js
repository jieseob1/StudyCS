import http from 'k6/http';
import { check, sleep } from 'k6';
import { Trend, Rate } from 'k6/metrics';
import { buildUrl, checkResponse, TEST_MODE } from '../lib/helpers.js';

// Custom metrics per workload type
const ioDuration = new Trend('io_heavy_duration', true);
const cpuDuration = new Trend('cpu_bound_duration', true);
const mixedDuration = new Trend('mixed_workload_duration', true);
const errorRate = new Rate('error_rate');

export const options = {
    scenarios: {
        io_heavy: {
            executor: 'ramping-vus',
            stages: [
                { duration: '1m',  target: 500 },
                { duration: '2m',  target: 500 },
                { duration: '30s', target: 0 },
            ],
            exec: 'ioHeavy',
            tags: { workload: 'io' },
        },
        cpu_bound: {
            executor: 'constant-vus',
            vus: 50,
            duration: '3m30s',
            exec: 'cpuBound',
            tags: { workload: 'cpu' },
        },
        mixed: {
            executor: 'ramping-arrival-rate',
            startRate: 5,
            timeUnit: '1s',
            preAllocatedVUs: 100,
            maxVUs: 500,
            stages: [
                { duration: '1m',  target: 100 },
                { duration: '2m',  target: 100 },
                { duration: '30s', target: 0 },
            ],
            exec: 'mixedWork',
            tags: { workload: 'mixed' },
        },
    },
    thresholds: {
        'http_req_duration{workload:io}':    ['p(95)<5000'],
        'http_req_duration{workload:cpu}':   ['p(95)<30000'],
        'http_req_duration{workload:mixed}': ['p(95)<10000'],
        error_rate: ['rate<0.10'],
    },
    tags: {
        test_mode: TEST_MODE,
    },
};

export function ioHeavy() {
    // Sleep endpoint
    const sleepRes = http.get(buildUrl('/api/io/sleep?ms=500'), {
        tags: { endpoint: 'sleep', workload: 'io' },
    });

    const sleepPassed = check(sleepRes, {
        ...checkResponse(sleepRes, 'io-sleep'),
        'io-sleep: within timeout': (r) => r.timings.duration < 5000,
    });

    ioDuration.add(sleepRes.timings.duration);
    errorRate.add(!sleepPassed);

    // Fan-out endpoint
    const fanOutRes = http.get(buildUrl('/api/io/fan-out?calls=5&delayMs=200'), {
        tags: { endpoint: 'fan-out', workload: 'io' },
    });

    const fanOutPassed = check(fanOutRes, {
        ...checkResponse(fanOutRes, 'io-fan-out'),
    });

    ioDuration.add(fanOutRes.timings.duration);
    errorRate.add(!fanOutPassed);

    sleep(0.5);
}

export function cpuBound() {
    const res = http.get(buildUrl('/api/cpu/compute?iterations=1000000'), {
        tags: { endpoint: 'cpu-compute', workload: 'cpu' },
        timeout: '60s',
    });

    const passed = check(res, {
        ...checkResponse(res, 'cpu-compute'),
    });

    cpuDuration.add(res.timings.duration);
    errorRate.add(!passed);

    sleep(0.1);
}

export function mixedWork() {
    const res = http.get(
        buildUrl('/api/mixed/realistic?dbDelayMs=50&httpDelayMs=100&cpuIterations=10000'),
        {
            tags: { endpoint: 'mixed-realistic', workload: 'mixed' },
            timeout: '30s',
        }
    );

    const passed = check(res, {
        ...checkResponse(res, 'mixed-realistic'),
    });

    mixedDuration.add(res.timings.duration);
    errorRate.add(!passed);
}
