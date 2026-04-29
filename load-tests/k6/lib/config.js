export const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
export const MAILHOG_URL = __ENV.MAILHOG_URL || 'http://localhost:8025';
export const RESULTS_DIR = __ENV.RESULTS_DIR || 'load-tests/results';
export const RUN_ID = __ENV.K6_RUN_ID || `${Date.now()}`;
export const RUN_STARTED_AT_UTC = __ENV.K6_RUN_STARTED_AT_UTC || new Date().toISOString();
export const TEST_PREFIX = __ENV.TEST_PREFIX || `loadtest_${RUN_ID}`;

export const DEFAULT_SLA = {
  overallP95Ms: envNumber('SLA_HTTP_P95_MS', 1000),
  overallP99Ms: envNumber('SLA_HTTP_P99_MS', 2000),
  publicP95Ms: envNumber('SLA_PUBLIC_P95_MS', 800),
  authP95Ms: envNumber('SLA_AUTH_P95_MS', 1000),
  writeP95Ms: envNumber('SLA_WRITE_P95_MS', 1500),
  failureRate: envNumber('SLA_HTTP_FAILED_RATE', 0.01),
  successRate: envNumber('SLA_SUCCESS_RATE', 0.95),
};

export function envNumber(name, defaultValue) {
  const value = Number(__ENV[name]);
  return Number.isFinite(value) ? value : defaultValue;
}

export function envString(name, defaultValue) {
  return __ENV[name] || defaultValue;
}

export function envBool(name, defaultValue = false) {
  if (__ENV[name] === undefined) {
    return defaultValue;
  }
  return ['1', 'true', 'yes', 'on'].includes(String(__ENV[name]).toLowerCase());
}

export function scenarioDuration(defaultValue) {
  return envString('DURATION', defaultValue);
}

export function targetRps(defaultValue) {
  return envNumber('TARGET_RPS', defaultValue);
}

export function preAllocatedVus(defaultValue) {
  return envNumber('PRE_ALLOCATED_VUS', defaultValue);
}

export function maxVus(defaultValue) {
  return envNumber('MAX_VUS', defaultValue);
}

export function arrivalRateScenario({
  rate = targetRps(25),
  duration = scenarioDuration('5m'),
  preAllocated = preAllocatedVus(100),
  max = maxVus(1000),
  exec = 'default',
  gracefulStop = '30s',
} = {}) {
  return {
    executor: 'constant-arrival-rate',
    rate,
    timeUnit: '1s',
    duration,
    preAllocatedVUs: preAllocated,
    maxVUs: max,
    exec,
    gracefulStop,
  };
}

export function rampingArrivalScenario({
  stages,
  preAllocated = preAllocatedVus(150),
  max = maxVus(1500),
  exec = 'default',
  gracefulStop = '30s',
} = {}) {
  return {
    executor: 'ramping-arrival-rate',
    startRate: envNumber('START_RPS', 1),
    timeUnit: '1s',
    preAllocatedVUs: preAllocated,
    maxVUs: max,
    stages,
    exec,
    gracefulStop,
  };
}

export function commonThresholds(extra = {}, {
  auth = true,
  email = true,
  write = true,
} = {}) {
  const thresholds = {
    http_req_failed: [`rate<${DEFAULT_SLA.failureRate}`],
    http_req_duration: [
      `p(95)<${DEFAULT_SLA.overallP95Ms}`,
      `p(99)<${DEFAULT_SLA.overallP99Ms}`,
    ],
    'http_req_duration{flow:public_browsing}': [`p(95)<${DEFAULT_SLA.publicP95Ms}`],
    realtorconnect_api_flow_success_rate: [`rate>${DEFAULT_SLA.successRate}`],
    realtorconnect_public_browsing_success_rate: [`rate>${DEFAULT_SLA.successRate}`],
    ...extra,
  };

  if (auth) {
    thresholds['http_req_duration{flow:auth}'] = [`p(95)<${DEFAULT_SLA.authP95Ms}`];
    thresholds.realtorconnect_auth_success_rate = [`rate>${DEFAULT_SLA.successRate}`];
  }

  if (email) {
    thresholds.realtorconnect_email_verification_rate = [`rate>${DEFAULT_SLA.successRate}`];
  }

  if (write) {
    thresholds['http_req_duration{operation:write}'] = [`p(95)<${DEFAULT_SLA.writeP95Ms}`];
    thresholds.realtorconnect_write_success_rate = [`rate>${DEFAULT_SLA.successRate}`];
    thresholds.realtorconnect_real_estate_creation_success_rate = [`rate>${DEFAULT_SLA.successRate}`];
  }

  return thresholds;
}

export function parseStages(defaultStages) {
  const raw = __ENV.STAGES;
  if (!raw) {
    return defaultStages;
  }

  return raw.split(',').map((stage) => {
    const [duration, target] = stage.split(':');
    return {
      duration,
      target: Number(target),
    };
  });
}
