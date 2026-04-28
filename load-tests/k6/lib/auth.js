import { group } from 'k6';
import { authSuccessRate, emailVerificationSuccessRate, realtorBootstrapDuration } from './metrics.js';
import { realtorRegistrationPayload } from './data.js';
import { expectCreated, expectOk, hasAuthToken } from './checks.js';
import { authHeaders, get, parseJson, postJson } from './http.js';
import { pollVerificationToken } from './mailhog.js';

export function registerRealtor() {
  const payload = realtorRegistrationPayload();
  const response = postJson('/api/auth/register/realtor', payload, {
    service: 'user-service',
    endpoint: 'register_realtor',
    flow: 'auth',
    operation: 'write',
    method: 'POST',
  });

  const ok = expectCreated(response, 'realtor registration', (res) => (
    Number.isFinite(Number(res.json('result.id'))) && res.json('result.username') === payload.username
  ));

  return {
    ok,
    payload,
    response,
    realtor: ok ? parseJson(response).result : null,
  };
}

export function verifyRealtorEmail(email) {
  const token = pollVerificationToken(email);
  if (!token) {
    emailVerificationSuccessRate.add(false);
    return false;
  }

  const response = get(`/api/users/verifyEmail/${token}`, {
    service: 'user-service',
    endpoint: 'verify_email',
    flow: 'email_verification',
    operation: 'write',
    method: 'GET',
  });

  const ok = expectOk(response, 'email verification', (res) => res.json('result') === true);
  emailVerificationSuccessRate.add(ok);
  return ok;
}

export function login(username, password) {
  const response = postJson('/api/auth/login', {
    username,
    password,
  }, {
    service: 'user-service',
    endpoint: 'login',
    flow: 'auth',
    operation: 'write',
    method: 'POST',
  });

  const ok = expectOk(response, 'login', hasAuthToken);
  authSuccessRate.add(ok);

  if (!ok) {
    return {
      ok,
      response,
      token: null,
      user: null,
    };
  }

  const body = parseJson(response).result;
  return {
    ok,
    response,
    token: body.token.authToken,
    user: body.user,
  };
}

export function getCurrentUser(token, expectedUsername) {
  const response = get('/api/auth/current', {
    service: 'user-service',
    endpoint: 'current_user',
    flow: 'auth',
    operation: 'read',
    method: 'GET',
  }, {
    headers: authHeaders(token),
  });

  return expectOk(response, 'current user', (res) => res.json('result.username') === expectedUsername);
}

export function bootstrapRealtor() {
  const started = Date.now();
  let context = null;

  group('bootstrap realtor', () => {
    const registration = registerRealtor();
    if (!registration.ok) {
      return;
    }

    const verified = verifyRealtorEmail(registration.payload.email);
    if (!verified) {
      return;
    }

    const auth = login(registration.payload.username, registration.payload.password);
    if (!auth.ok) {
      return;
    }

    context = {
      id: auth.user.id,
      username: auth.user.username,
      token: auth.token,
    };

    getCurrentUser(context.token, context.username);
  });

  realtorBootstrapDuration.add(Date.now() - started);
  return context;
}

