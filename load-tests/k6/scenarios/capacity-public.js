import { commonThresholds } from '../lib/config.js';
import { publicCapacityRequest } from '../lib/flows.js';
import { publicCapacityScenario } from '../lib/scenarios.js';
import { summaryHandler } from '../lib/summary.js';

export const options = {
  scenarios: {
    public_capacity: publicCapacityScenario(),
  },
  thresholds: commonThresholds({}, {
    auth: false,
    email: false,
    write: false,
  }),
};

export default function () {
  publicCapacityRequest();
}

export const handleSummary = summaryHandler('capacity-public');
