import http from 'k6/http';
import { check, group, sleep } from 'k6';
import { Rate, Trend } from 'k6/metrics';

const baseUrl = __ENV.BASE_URL || 'http://localhost:8080';
const mailhogUrl = __ENV.MAILHOG_URL || 'http://localhost:8025';
const createEvery = Number(__ENV.K6_CREATE_EVERY || 3);
const runId = __ENV.K6_RUN_ID || `${Date.now()}`;

const authSuccessRate = new Rate('realtorconnect_auth_success_rate');
const emailVerificationRate = new Rate('realtorconnect_email_verification_rate');
const bootstrapDuration = new Trend('realtorconnect_realtor_bootstrap_duration');
const realEstateCreateDuration = new Trend('realtorconnect_real_estate_create_duration');

export const options = {
  vus: Number(__ENV.K6_VUS || 500),
  duration: __ENV.K6_DURATION || '5m',
  thresholds: {
    http_req_failed: ['rate<0.05'],
    http_req_duration: ['p(95)<1000'],
    realtorconnect_auth_success_rate: ['rate>0.95'],
    realtorconnect_email_verification_rate: ['rate>0.95'],
    realtorconnect_real_estate_create_duration: ['p(95)<1500'],
  },
};

const jsonHeaders = {
  headers: {
    'Content-Type': 'application/json',
  },
};

let realtorContext;

export default function () {
  if (!realtorContext) {
    realtorContext = bootstrapRealtor();
  }

  group('public discovery', () => {
    const responses = http.batch([
      ['GET', `${baseUrl}/api/realtors?page=0&size=10`, null, { tags: { flow: 'realtor_search' } }],
      ['GET', `${baseUrl}/api/realtors/real-estates?page=0&size=10`, null, { tags: { flow: 'real_estate_search' } }],
      ['GET', `${baseUrl}/api/settings/real-estate`, null, { tags: { flow: 'settings_real_estate' } }],
    ]);

    check(responses[0], {
      'realtors search responded': (response) => response.status < 500,
    });
    check(responses[1], {
      'real estate search responded': (response) => response.status < 500,
    });
    check(responses[2], {
      'real estate settings responded': (response) => response.status < 500,
    });
  });

  group('authenticated realtor workspace', () => {
    const authHeaders = {
      headers: {
        Authorization: `Bearer ${realtorContext.token}`,
      },
    };

    const current = http.get(`${baseUrl}/api/auth/current`, {
      ...authHeaders,
      tags: { flow: 'current_user' },
    });

    check(current, {
      'current user resolved': (response) => response.status === 200,
    });

    const inventory = http.get(`${baseUrl}/api/realtors/${realtorContext.id}/real-estates/fulls?page=0&size=10`, {
      ...authHeaders,
      tags: { flow: 'realtor_inventory' },
    });

    check(inventory, {
      'realtor inventory resolved': (response) => response.status === 200,
    });

    if (__ITER % createEvery === 0) {
      const created = createRealEstate(realtorContext);
      if (created && created.id) {
        const markedCalled = http.put(`${baseUrl}/api/realtors/real-estates/${created.id}/mark-called`, null, {
          ...authHeaders,
          tags: { flow: 'real_estate_mark_called' },
        });

        check(markedCalled, {
          'real estate marked called': (response) => response.status === 200,
        });
      }
    }
  });

  sleep(1);
}

function bootstrapRealtor() {
  const started = Date.now();
  const suffix = `${runId}_${__VU}`;
  const username = `load_realtor_${suffix}`;
  const email = `${username}@mail.com`;
  const password = 'pass';

  const registerResponse = http.post(`${baseUrl}/api/auth/register/realtor`, JSON.stringify({
    name: `Load Realtor ${__VU}`,
    email,
    username,
    password,
    phone: phoneForVu(__VU),
    agency: 'Load Test Realty',
    agencySite: 'https://load-test.example.com',
  }), {
    ...jsonHeaders,
    tags: { flow: 'realtor_registration' },
  });

  check(registerResponse, {
    'realtor registered': (response) => response.status === 201 || response.status === 409,
  });

  const token = pollVerificationToken(email);
  const verified = token ? verifyEmail(token) : false;
  emailVerificationRate.add(verified);

  const loginResponse = http.post(`${baseUrl}/api/auth/login`, JSON.stringify({
    username,
    password,
  }), {
    ...jsonHeaders,
    tags: { flow: 'auth_login' },
  });

  const loggedIn = check(loginResponse, {
    'realtor logged in': (response) => response.status === 200,
  });

  authSuccessRate.add(loggedIn);
  bootstrapDuration.add(Date.now() - started);

  const loginBody = parseJson(loginResponse);
  return {
    id: loginBody.result.user.id,
    token: loginBody.result.token.authToken,
  };
}

function pollVerificationToken(email) {
  for (let attempt = 0; attempt < 20; attempt += 1) {
    const response = http.get(`${mailhogUrl}/api/v2/messages?limit=100`, {
      tags: { flow: 'mailhog_verification_lookup' },
    });

    if (response.status === 200) {
      const body = parseJson(response);
      const items = body.items || [];
      for (const item of items) {
        const recipients = item.To || [];
        const sentToTarget = recipients.some((recipient) => recipient.Mailbox && recipient.Domain
          && `${recipient.Mailbox}@${recipient.Domain}` === email);
        const content = item.Content && item.Content.Body ? item.Content.Body : '';
        const match = content.match(/verifyEmail\/([0-9a-fA-F-]{36})/);
        if (sentToTarget && match) {
          return match[1];
        }
      }
    }

    sleep(0.5);
  }

  return null;
}

function verifyEmail(token) {
  const response = http.get(`${baseUrl}/api/users/verifyEmail/${token}`, {
    tags: { flow: 'email_verification' },
  });

  return check(response, {
    'email verified': (res) => res.status === 200,
  });
}

function createRealEstate(context) {
  const started = Date.now();
  const response = http.post(`${baseUrl}/api/realtors/${context.id}/real-estates`, JSON.stringify(realEstatePayload()), {
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${context.token}`,
    },
    tags: { flow: 'real_estate_create' },
  });

  realEstateCreateDuration.add(Date.now() - started);

  const created = check(response, {
    'real estate created': (res) => res.status === 201,
  });

  if (!created) {
    return null;
  }

  return parseJson(response).result;
}

function realEstatePayload() {
  const suffix = `${runId}-${__VU}-${__ITER}`;
  return {
    name: `Load test apartment ${suffix}`,
    description: 'Private load test listing that exercises the realtor workspace write path.',
    price: 125000,
    owner: {
      name: 'Helen Owner',
      phone: '+380130495830',
      email: null,
    },
    location: {
      city: 'Odesa',
      district: 'Primorsky',
      residentialArea: 'Centre',
      street: 'Rishelievskaya',
      housingEstate: null,
      houseNumber: 11,
      block: null,
      apartmentNumber: 6,
      landmark: 'Opera house',
    },
    loggia: {
      type: 'LOGGIA',
      count: 1,
      glassed: false,
    },
    bathroom: {
      type: 'TOILET_BATH',
      count: 1,
      combined: true,
    },
    area: {
      total: 82,
      living: 60,
      kitchen: 22,
    },
    floor: 3,
    floorsInBuilding: 4,
    buildingType: 'APARTMENT',
    heatingType: 'CENTRALIZED',
    windowsType: 'METAL_PLASTIC',
    hotWaterType: 'CENTRALIZED',
    stateType: 'CAPITAL',
    announcementType: 'SALE',
    roomsCount: 4,
    ceilingHeight: 3.9,
    documents: null,
    private: true,
  };
}

function phoneForVu(vu) {
  const seed = hash(runId);
  const number = (seed * 1000 + vu) % 1000000000;
  return `+380${String(number).padStart(9, '0')}`;
}

function parseJson(response) {
  try {
    return response.json();
  } catch (error) {
    return {};
  }
}

function hash(value) {
  return String(value).split('').reduce((acc, char) => (
    ((acc * 31) + char.charCodeAt(0)) % 1000000
  ), 0);
}
