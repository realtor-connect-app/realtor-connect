import http from 'k6/http';
import { group } from 'k6';
import { expectCreated, expectOk, hasPageContent } from './checks.js';
import { envNumber } from './config.js';
import { realEstatePayload, realEstateSearchQuery, realtorSearchQuery } from './data.js';
import { apiUrl, authHeaders, content, get, parseJson, postJson, put, query } from './http.js';
import {
  markCalledCounter,
  publicBrowsingDuration,
  publicBrowsingSuccessRate,
  realEstateCreatedCounter,
  realEstateCreationDuration,
  realEstateCreationSuccessRate,
  writeSuccessRate,
} from './metrics.js';

const knownPublicRealEstateIds = [];

export function publicBrowsingFlow({ includeDetails = true } = {}) {
  const started = Date.now();
  let allOk = true;

  group('public browsing', () => {
    const realtorResponse = get(`/api/realtors${query(realtorSearchQuery())}`, {
      service: 'user-service',
      endpoint: 'list_realtors',
      flow: 'public_browsing',
      operation: 'read',
      method: 'GET',
    });
    allOk = expectOk(realtorResponse, 'list realtors', hasPageContent) && allOk;

    const realEstateResponse = get(`/api/realtors/real-estates${query(realEstateSearchQuery())}`, {
      service: 'real-estate-service',
      endpoint: 'list_real_estates',
      flow: 'public_browsing',
      operation: 'read',
      method: 'GET',
    });
    allOk = expectOk(realEstateResponse, 'list real estates', hasPageContent) && allOk;

    if (includeDetails) {
      const estates = content(realEstateResponse);
      if (estates.length > 0) {
        const estate = estates[Math.floor(Math.random() * estates.length)];
        const detailResponse = get(`/api/realtors/real-estates/${estate.id}`, {
          service: 'real-estate-service',
          endpoint: 'real_estate_details',
          flow: 'public_browsing',
          operation: 'read',
          method: 'GET',
        });
        allOk = expectOk(detailResponse, 'real estate details', (res) => Number(res.json('result.id')) === Number(estate.id)) && allOk;
      }
    }

    const settingsResponse = get(settingsPath(), {
      service: 'operational-service',
      endpoint: 'settings',
      flow: 'public_browsing',
      operation: 'read',
      method: 'GET',
    });
    allOk = expectOk(settingsResponse, 'settings') && allOk;
  });

  publicBrowsingSuccessRate.add(allOk);
  publicBrowsingDuration.add(Date.now() - started);
  return allOk;
}

export function publicBrowsingSingleRequest() {
  const choice = Math.random();

  if (choice < 0.35) {
    const response = get(`/api/realtors${query(realtorSearchQuery())}`, {
      service: 'user-service',
      endpoint: 'list_realtors',
      flow: 'public_browsing',
      operation: 'read',
      method: 'GET',
    });
    const ok = expectOk(response, 'list realtors', hasPageContent);
    publicBrowsingSuccessRate.add(ok);
    return ok;
  }

  if (choice < 0.75) {
    const response = get(`/api/realtors/real-estates${query(realEstateSearchQuery())}`, {
      service: 'real-estate-service',
      endpoint: 'list_real_estates',
      flow: 'public_browsing',
      operation: 'read',
      method: 'GET',
    });
    const ok = expectOk(response, 'list real estates', hasPageContent);
    publicBrowsingSuccessRate.add(ok);
    return ok;
  }

  const response = get(settingsPath(), {
    service: 'operational-service',
    endpoint: 'settings',
    flow: 'public_browsing',
    operation: 'read',
    method: 'GET',
  });
  const ok = expectOk(response, 'settings');
  publicBrowsingSuccessRate.add(ok);
  return ok;
}

export function publicCapacityRequest() {
  const choice = Math.random();

  if (choice < 0.25) {
    const response = get(`/api/realtors${query(realtorSearchQuery())}`, {
      service: 'user-service',
      endpoint: 'list_realtors',
      flow: 'public_browsing',
      operation: 'read',
      method: 'GET',
    });
    const ok = expectOk(response, 'list realtors', hasPageContent);
    publicBrowsingSuccessRate.add(ok);
    return ok;
  }

  if (choice < 0.70 || knownPublicRealEstateIds.length === 0) {
    const response = get(`/api/realtors/real-estates${query(realEstateSearchQuery())}`, {
      service: 'real-estate-service',
      endpoint: 'list_real_estates',
      flow: 'public_browsing',
      operation: 'read',
      method: 'GET',
    });

    const ok = expectOk(response, 'list real estates', hasPageContent);
    rememberPublicRealEstateIds(response);
    publicBrowsingSuccessRate.add(ok);
    return ok;
  }

  if (choice < 0.85) {
    const realEstateId = knownPublicRealEstateIds[Math.floor(Math.random() * knownPublicRealEstateIds.length)];
    const response = get(`/api/realtors/real-estates/${realEstateId}`, {
      service: 'real-estate-service',
      endpoint: 'real_estate_details',
      flow: 'public_browsing',
      operation: 'read',
      method: 'GET',
    });
    const ok = expectOk(response, 'real estate details', (res) => Number(res.json('result.id')) === Number(realEstateId));
    publicBrowsingSuccessRate.add(ok);
    return ok;
  }

  return publicBrowsingSingleRequest();
}

export function scaledServicesReadRequest() {
  const userReadPercent = envNumber('USER_READ_PERCENT', 35);
  const realEstateListPercent = envNumber('REAL_ESTATE_LIST_PERCENT', 50);
  const choice = Math.random() * 100;

  if (choice < userReadPercent) {
    const response = get(`/api/realtors${query(realtorSearchQuery())}`, {
      service: 'user-service',
      endpoint: 'list_realtors',
      flow: 'scaled_services_read',
      operation: 'read',
      method: 'GET',
    });
    const ok = expectOk(response, 'list realtors', hasPageContent);
    publicBrowsingSuccessRate.add(ok);
    return ok;
  }

  if (choice < userReadPercent + realEstateListPercent || knownPublicRealEstateIds.length === 0) {
    const response = get(`/api/realtors/real-estates${query(realEstateSearchQuery())}`, {
      service: 'real-estate-service',
      endpoint: 'list_real_estates',
      flow: 'scaled_services_read',
      operation: 'read',
      method: 'GET',
    });

    const ok = expectOk(response, 'list real estates', hasPageContent);
    rememberPublicRealEstateIds(response);
    publicBrowsingSuccessRate.add(ok);
    return ok;
  }

  const realEstateId = knownPublicRealEstateIds[Math.floor(Math.random() * knownPublicRealEstateIds.length)];
  const response = get(`/api/realtors/real-estates/${realEstateId}`, {
    service: 'real-estate-service',
    endpoint: 'real_estate_details',
    flow: 'scaled_services_read',
    operation: 'read',
    method: 'GET',
  });
  const ok = expectOk(response, 'real estate details', (res) => Number(res.json('result.id')) === Number(realEstateId));
  publicBrowsingSuccessRate.add(ok);
  return ok;
}

export function realtorInventoryFlow(context) {
  if (!context) {
    return false;
  }

  const response = get(`/api/realtors/${context.id}/real-estates/fulls${query({ page: 0, size: 15 })}`, {
    service: 'real-estate-service',
    endpoint: 'realtor_inventory',
    flow: 'private_inventory',
    operation: 'read',
    method: 'GET',
  }, {
    headers: authHeaders(context.token),
  });

  return expectOk(response, 'realtor inventory', hasPageContent);
}

export function createRealEstateFlow(context, { markCalled = true } = {}) {
  if (!context) {
    writeSuccessRate.add(false);
    realEstateCreationSuccessRate.add(false);
    return null;
  }

  const started = Date.now();
  const response = postJson(`/api/realtors/${context.id}/real-estates`, realEstatePayload({ isPrivate: true }), {
    service: 'real-estate-service',
    endpoint: 'create_real_estate',
    flow: 'real_estate_write',
    operation: 'write',
    method: 'POST',
  }, {
    headers: authHeaders(context.token),
  });

  realEstateCreationDuration.add(Date.now() - started);

  const created = expectCreated(response, 'create real estate', (res) => Number.isFinite(Number(res.json('result.id'))));
  writeSuccessRate.add(created);
  realEstateCreationSuccessRate.add(created);

  if (!created) {
    return null;
  }

  realEstateCreatedCounter.add(1);
  const estate = parseJson(response).result;

  if (markCalled) {
    markRealEstateCalledFlow(context, estate.id);
  }

  return estate;
}

export function markRealEstateCalledFlow(context, realEstateId) {
  if (!context || !realEstateId) {
    writeSuccessRate.add(false);
    return false;
  }

  const response = put(`/api/realtors/real-estates/${realEstateId}/mark-called`, {
    service: 'real-estate-service',
    endpoint: 'mark_real_estate_called',
    flow: 'real_estate_write',
    operation: 'write',
    method: 'PUT',
  }, {
    headers: authHeaders(context.token),
  });

  const ok = expectOk(response, 'mark real estate called', (res) => res.json('result') === true);
  writeSuccessRate.add(ok);
  if (ok) {
    markCalledCounter.add(1);
  }
  return ok;
}

export function cleanupRealEstate(context, realEstateId) {
  if (!context || !realEstateId) {
    return false;
  }

  const response = http.del(apiUrl(`/api/realtors/real-estates/${realEstateId}`), null, {
    headers: authHeaders(context.token),
    tags: {
      service: 'real-estate-service',
      endpoint: 'delete_real_estate',
      flow: 'cleanup',
      operation: 'write',
      method: 'DELETE',
    },
  });

  return response.status === 204;
}

function settingsPath() {
  const paths = ['/api/settings/real-estate', '/api/settings/realtor', '/api/settings/user', '/api/settings/file'];
  return paths[Math.floor(Math.random() * paths.length)];
}

function rememberPublicRealEstateIds(response) {
  content(response)
    .map((estate) => estate.id)
    .filter((id) => id !== undefined && id !== null)
    .forEach((id) => {
      if (!knownPublicRealEstateIds.includes(id)) {
        knownPublicRealEstateIds.push(id);
      }
    });

  if (knownPublicRealEstateIds.length > 500) {
    knownPublicRealEstateIds.splice(0, knownPublicRealEstateIds.length - 500);
  }
}
