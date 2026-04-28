import { group } from 'k6';
import { commonThresholds } from '../lib/config.js';
import { bootstrapRealtor, getCurrentUser } from '../lib/auth.js';
import { createRealEstateFlow, markRealEstateCalledFlow, publicBrowsingFlow, realtorInventoryFlow } from '../lib/flows.js';
import { smokeScenario } from '../lib/scenarios.js';
import { summaryHandler } from '../lib/summary.js';

export const options = {
  scenarios: {
    smoke: smokeScenario(),
  },
  thresholds: commonThresholds(),
};

export default function () {
  let context = null;
  let estate = null;

  group('smoke public discovery', () => {
    publicBrowsingFlow();
  });

  group('smoke auth and private realtor flow', () => {
    context = bootstrapRealtor();
    if (!context) {
      return;
    }

    getCurrentUser(context.token, context.username);
    realtorInventoryFlow(context);
    estate = createRealEstateFlow(context, { markCalled: false });
    if (estate) {
      markRealEstateCalledFlow(context, estate.id);
    }
  });
}

export const handleSummary = summaryHandler('smoke');

