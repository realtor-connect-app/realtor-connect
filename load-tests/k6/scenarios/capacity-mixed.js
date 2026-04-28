import { getCurrentUser } from '../lib/auth.js';
import { commonThresholds, envNumber } from '../lib/config.js';
import { createRealEstateFlow, publicCapacityRequest, realtorInventoryFlow } from '../lib/flows.js';
import { mixedCapacityScenario } from '../lib/scenarios.js';
import { summaryHandler } from '../lib/summary.js';
import { bootstrapContext } from './shared-workload.js';

export const options = {
  scenarios: {
    mixed_capacity: mixedCapacityScenario(),
  },
  thresholds: commonThresholds(),
};

const publicPercent = envNumber('PUBLIC_BROWSING_PERCENT', 70);
const authPercent = envNumber('AUTH_PERCENT', 10);
const inventoryPercent = envNumber('INVENTORY_PERCENT', 10);

export default function () {
  const roll = Math.random() * 100;

  if (roll < publicPercent) {
    publicCapacityRequest();
    return;
  }

  const context = bootstrapContext();

  if (roll < publicPercent + authPercent) {
    if (context) {
      getCurrentUser(context.token, context.username);
    }
    return;
  }

  if (roll < publicPercent + authPercent + inventoryPercent) {
    realtorInventoryFlow(context);
    return;
  }

  createRealEstateFlow(context);
}

export const handleSummary = summaryHandler('capacity-mixed');

