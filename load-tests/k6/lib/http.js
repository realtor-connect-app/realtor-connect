import http from 'k6/http';
import { check } from 'k6';
import { BASE_URL } from './config.js';
import { apiFlowSuccessRate } from './metrics.js';

export function apiUrl(path) {
  return `${BASE_URL}${path}`;
}

export function get(path, tags = {}, params = {}) {
  return http.get(apiUrl(path), requestParams(tags, params));
}

export function postJson(path, body, tags = {}, params = {}) {
  return http.post(apiUrl(path), JSON.stringify(body), requestParams(tags, params, {
    'Content-Type': 'application/json',
  }));
}

export function put(path, tags = {}, params = {}) {
  return http.put(apiUrl(path), null, requestParams(tags, params));
}

export function del(path, tags = {}, params = {}) {
  return http.del(apiUrl(path), null, requestParams(tags, params));
}

export function authHeaders(token) {
  return {
    Authorization: `Bearer ${token}`,
  };
}

export function requestParams(tags = {}, params = {}, headers = {}) {
  return {
    ...params,
    headers: {
      ...(params.headers || {}),
      ...headers,
    },
    tags: {
      ...(params.tags || {}),
      ...tags,
    },
  };
}

export function strictCheck(response, checks) {
  const safeChecks = {};
  Object.keys(checks).forEach((name) => {
    safeChecks[name] = (res) => {
      try {
        return Boolean(res) && checks[name](res);
      } catch (error) {
        return false;
      }
    };
  });

  const passed = check(response, safeChecks);
  apiFlowSuccessRate.add(passed);
  return passed;
}

export function parseJson(response) {
  try {
    return response.json();
  } catch (error) {
    return {};
  }
}

export function result(response) {
  return parseJson(response).result;
}

export function content(response) {
  const responseResult = result(response);
  return responseResult && responseResult.content ? responseResult.content : [];
}

export function query(params) {
  const parts = [];
  Object.keys(params).forEach((key) => {
    const value = params[key];
    if (value !== undefined && value !== null && value !== '') {
      parts.push(`${encodeURIComponent(key)}=${encodeURIComponent(value)}`);
    }
  });
  return parts.length ? `?${parts.join('&')}` : '';
}
