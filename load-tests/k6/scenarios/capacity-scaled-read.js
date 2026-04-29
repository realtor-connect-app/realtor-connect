import { commonThresholds, DEFAULT_SLA } from '../lib/config.js';
import { scaledServicesReadRequest } from '../lib/flows.js';
import { publicCapacityScenario } from '../lib/scenarios.js';
import { summaryHandler } from '../lib/summary.js';

export const options = {
  scenarios: {
    scaled_read_capacity: publicCapacityScenario(),
  },
  thresholds: commonThresholds({
    'http_req_duration{flow:scaled_services_read}': [`p(95)<${DEFAULT_SLA.publicP95Ms}`],
  }, {
    auth: false,
    email: false,
    write: false,
  }),
};

export default function () {
  scaledServicesReadRequest();
}

export const handleSummary = summaryHandler('capacity-scaled-read');
