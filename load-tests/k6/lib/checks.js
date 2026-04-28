import { strictCheck } from './http.js';

export function expectOk(response, name, fieldCheck = () => true) {
  return strictCheck(response, {
    [`${name} status is 200`]: (res) => res.status === 200,
    [`${name} response is success`]: (res) => bodySuccess(res),
    [`${name} response fields valid`]: fieldCheck,
  });
}

export function expectCreated(response, name, fieldCheck = () => true) {
  return strictCheck(response, {
    [`${name} status is 201`]: (res) => res.status === 201,
    [`${name} response is success`]: (res) => bodySuccess(res),
    [`${name} response fields valid`]: fieldCheck,
  });
}

export function expectNoContent(response, name) {
  return strictCheck(response, {
    [`${name} status is 204`]: (res) => res.status === 204,
  });
}

export function bodySuccess(response) {
  try {
    return response.json('success') === true;
  } catch (error) {
    return false;
  }
}

export function hasResultId(response) {
  try {
    return Number.isFinite(Number(response.json('result.id')));
  } catch (error) {
    return false;
  }
}

export function hasPageContent(response) {
  try {
    const content = response.json('result.content');
    return Array.isArray(content);
  } catch (error) {
    return false;
  }
}

export function hasAuthToken(response) {
  try {
    const token = response.json('result.token.authToken');
    return typeof token === 'string' && token.length > 20;
  } catch (error) {
    return false;
  }
}

