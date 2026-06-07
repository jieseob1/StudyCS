export const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
export const TEST_MODE = __ENV.TEST_MODE || 'unknown';

export function buildUrl(path) {
    return `${BASE_URL}${path}`;
}

export function checkResponse(res, name) {
    return {
        [`${name}: status 200`]: (r) => r.status === 200,
        [`${name}: no error`]: (r) => r.status < 400,
    };
}
