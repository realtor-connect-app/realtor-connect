import { Counter, Rate, Trend } from 'k6/metrics';

export const apiFlowSuccessRate = new Rate('realtorconnect_api_flow_success_rate');
export const publicBrowsingSuccessRate = new Rate('realtorconnect_public_browsing_success_rate');
export const authSuccessRate = new Rate('realtorconnect_auth_success_rate');
export const emailVerificationSuccessRate = new Rate('realtorconnect_email_verification_rate');
export const writeSuccessRate = new Rate('realtorconnect_write_success_rate');
export const realEstateCreationSuccessRate = new Rate('realtorconnect_real_estate_creation_success_rate');

export const realtorBootstrapDuration = new Trend('realtorconnect_realtor_bootstrap_duration', true);
export const realEstateCreationDuration = new Trend('realtorconnect_real_estate_creation_duration', true);
export const publicBrowsingDuration = new Trend('realtorconnect_public_browsing_duration', true);

export const realEstateCreatedCounter = new Counter('realtorconnect_real_estates_created_total');
export const markCalledCounter = new Counter('realtorconnect_real_estates_marked_called_total');

