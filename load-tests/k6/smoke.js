import http from 'k6/http';
import { check, sleep } from 'k6';

const baseUrl = __ENV.BASE_URL || 'http://localhost:8080';

export const options = {
  vus: Number(__ENV.K6_VUS || 10),
  duration: __ENV.K6_DURATION || '5m',
  thresholds: {
    http_req_failed: ['rate<0.05'],
    http_req_duration: ['p(95)<1000'],
  },
};

export default function () {
  const responses = http.batch([
    ['GET', `${baseUrl}/api/realtors?page=0&size=10`],
    ['GET', `${baseUrl}/api/realtors/real-estates?page=0&size=10`],
    ['GET', `${baseUrl}/api/settings/real-estate`],
  ]);

  check(responses[0], {
    'realtors endpoint responded': (response) => response.status < 500,
  });
  check(responses[1], {
    'real estates endpoint responded': (response) => response.status < 500,
  });
  check(responses[2], {
    'settings endpoint responded': (response) => response.status < 500,
  });

  sleep(1);
}
