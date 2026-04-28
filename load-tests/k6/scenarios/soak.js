import { getCurrentUser } from '../lib/auth.js';
import { commonThresholds } from '../lib/config.js';
import { createRealEstateFlow, publicCapacityRequest, realtorInventoryFlow } from '../lib/flows.js';
import { soakScenario } from '../lib/scenarios.js';
import { summaryHandler } from '../lib/summary.js';
import { bootstrapContext } from './shared-workload.js';

export const options = {
  scenarios: {
    soak: soakScenario(),
  },
  thresholds: commonThresholds(),
};

export default function () {
  const roll = Math.random();

  if (roll < 0.75) {
    publicCapacityRequest();
    return;
  }

  const context = bootstrapContext();

  if (roll < 0.85) {
    if (context) {
      getCurrentUser(context.token, context.username);
    }
    return;
  }

  if (roll < 0.95) {
    realtorInventoryFlow(context);
    return;
  }

  createRealEstateFlow(context);
}

export const handleSummary = summaryHandler('soak');

