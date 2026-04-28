import { arrivalRateScenario, envNumber, parseStages, rampingArrivalScenario, scenarioDuration, targetRps } from './config.js';

export function smokeScenario() {
  return {
    executor: 'shared-iterations',
    vus: envNumber('SMOKE_VUS', 1),
    iterations: envNumber('SMOKE_ITERATIONS', 1),
    maxDuration: __ENV.SMOKE_MAX_DURATION || '2m',
  };
}

export function publicCapacityScenario() {
  return arrivalRateScenario({
    rate: targetRps(50),
    duration: scenarioDuration('10m'),
    preAllocated: envNumber('PRE_ALLOCATED_VUS', 100),
    max: envNumber('MAX_VUS', 1000),
  });
}

export function mixedCapacityScenario() {
  return arrivalRateScenario({
    rate: targetRps(50),
    duration: scenarioDuration('10m'),
    preAllocated: envNumber('PRE_ALLOCATED_VUS', 150),
    max: envNumber('MAX_VUS', 1500),
  });
}

export function stressScenario() {
  return rampingArrivalScenario({
    stages: parseStages([
      { duration: '1m', target: 25 },
      { duration: '2m', target: 50 },
      { duration: '2m', target: 100 },
      { duration: '2m', target: 200 },
      { duration: '2m', target: 300 },
      { duration: '2m', target: 400 },
      { duration: '2m', target: 500 },
      { duration: '1m', target: 0 },
    ]),
    preAllocated: envNumber('PRE_ALLOCATED_VUS', 200),
    max: envNumber('MAX_VUS', 2000),
  });
}

export function spikeScenario() {
  const base = envNumber('BASE_RPS', 50);
  const spike = envNumber('SPIKE_RPS', 400);
  return rampingArrivalScenario({
    stages: parseStages([
      { duration: envDuration('BASE_DURATION', '30s'), target: base },
      { duration: envDuration('SPIKE_RAMP_DURATION', '5s'), target: spike },
      { duration: envDuration('SPIKE_HOLD_DURATION', '3m'), target: spike },
      { duration: envDuration('RECOVERY_RAMP_DURATION', '5s'), target: base },
      { duration: envDuration('RECOVERY_DURATION', '1m'), target: base },
      { duration: '30s', target: 0 },
    ]),
    preAllocated: envNumber('PRE_ALLOCATED_VUS', 200),
    max: envNumber('MAX_VUS', 2000),
  });
}

export function soakScenario() {
  return arrivalRateScenario({
    rate: targetRps(100),
    duration: scenarioDuration('1h'),
    preAllocated: envNumber('PRE_ALLOCATED_VUS', 200),
    max: envNumber('MAX_VUS', 1500),
  });
}

function envDuration(name, defaultValue) {
  return __ENV[name] || defaultValue;
}
