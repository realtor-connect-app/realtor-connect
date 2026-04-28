import { getCurrentUser } from '../lib/auth.js';
import { commonThresholds } from '../lib/config.js';
import { createRealEstateFlow, publicCapacityRequest, realtorInventoryFlow } from '../lib/flows.js';
import { stressScenario } from '../lib/scenarios.js';
import { summaryHandler } from '../lib/summary.js';
import { bootstrapContext } from './shared-workload.js';

export const options = {
  scenarios: {
    stress: stressScenario(),
  },
  thresholds: commonThresholds({
    dropped_iterations: ['count<100'],
  }),
};

export default function () {
  const roll = Math.random();

  if (roll < 0.70) {
    publicCapacityRequest();
    return;
  }

  const context = bootstrapContext();

  if (roll < 0.80) {
    if (context) {
      getCurrentUser(context.token, context.username);
    }
    return;
  }

  if (roll < 0.90) {
    realtorInventoryFlow(context);
    return;
  }

  createRealEstateFlow(context);
}

export const handleSummary = summaryHandler('stress');

