import { RESULTS_DIR, RUN_ID } from './config.js';

export function summaryHandler(scenarioName) {
  return function handleSummary(data) {
    const base = `${RESULTS_DIR}/${scenarioName}-${RUN_ID}`;
    return {
      stdout: humanSummary(data, scenarioName),
      [`${base}.json`]: JSON.stringify(data, null, 2),
      [`${base}-summary.txt`]: humanSummary(data, scenarioName),
    };
  };
}

function humanSummary(data, scenarioName) {
  const metrics = data.metrics || {};
  const lines = [
    `Scenario: ${scenarioName}`,
    `Run ID: ${RUN_ID}`,
    '',
    `HTTP requests: ${value(metrics.http_reqs, 'count')}`,
    `Request rate: ${rate(metrics.http_reqs)}`,
    `HTTP failures: ${rate(metrics.http_req_failed)}`,
    `Dropped iterations: ${value(metrics.dropped_iterations, 'count')}`,
    `p50 latency: ${percentile(metrics.http_req_duration, 'p(50)', 'med')} ms`,
    `p90 latency: ${percentile(metrics.http_req_duration, 'p(90)')} ms`,
    `p95 latency: ${percentile(metrics.http_req_duration, 'p(95)')} ms`,
    `p99 latency: ${percentile(metrics.http_req_duration, 'p(99)')} ms`,
    '',
    `Public browsing success: ${rate(metrics.realtorconnect_public_browsing_success_rate)}`,
    `Auth success: ${rate(metrics.realtorconnect_auth_success_rate)}`,
    `Email verification success: ${rate(metrics.realtorconnect_email_verification_rate)}`,
    `Write success: ${rate(metrics.realtorconnect_write_success_rate)}`,
    `Real estate creation success: ${rate(metrics.realtorconnect_real_estate_creation_success_rate)}`,
    `Real estates created: ${value(metrics.realtorconnect_real_estates_created_total, 'count')}`,
  ];

  return `${lines.join('\n')}\n`;
}

function value(metric, key) {
  if (!metric || metric.values === undefined || metric.values[key] === undefined) {
    return 'n/a';
  }
  return String(metric.values[key]);
}

function percentile(metric, key, fallbackKey) {
  if (!metric || !metric.values) {
    return 'n/a';
  }
  const metricValue = metric.values[key] !== undefined ? metric.values[key] : metric.values[fallbackKey];
  return metricValue === undefined ? 'n/a' : Number(metricValue).toFixed(2);
}

function rate(metric) {
  if (!metric || !metric.values) {
    return 'n/a';
  }
  const value = metric.values.rate !== undefined ? metric.values.rate : metric.values.rate_per_sec;
  return value === undefined ? 'n/a' : Number(value).toFixed(4);
}
