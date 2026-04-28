import { RUN_ID, TEST_PREFIX } from './config.js';

const cities = ['Odesa', 'Kyiv', 'Lviv', 'Dnipro', 'Kharkiv'];
const districts = ['Primorsky', 'Shevchenkivskyi', 'Halytskyi', 'Sobornyi', 'Kyivskyi'];
const residentialAreas = ['Centre', 'Arcadia', 'Pechersk', 'Sykhiv', 'Nagornyi'];
const streets = ['Rishelievskaya', 'Deribasivska', 'Khreshchatyk', 'Horodotska', 'Sumska'];
const buildingTypes = ['APARTMENT', 'STUDIO_FLAT', 'HOUSE', 'ROOM'];
const states = ['CAPITAL', 'LIVING', 'EUROPEAN_STYLE_RENOVATION', 'FROM_DEVELOPER'];
const announcements = ['SALE', 'DAILY_RENT', 'LONG_RENT'];
const realtorNames = ['Load Realtor', 'Research Realtor', 'Capacity Agent', 'Experiment Broker'];

export function uniqueSuffix() {
  return `${RUN_ID}_${__VU}_${__ITER}_${randomInt(100000, 999999)}`;
}

export function randomInt(min, max) {
  return Math.floor(Math.random() * (max - min + 1)) + min;
}

export function pick(values) {
  return values[randomInt(0, values.length - 1)];
}

export function realtorRegistrationPayload() {
  const suffix = uniqueSuffix();
  const username = `lt_${shortRunId()}_${__VU}_${__ITER}_${randomInt(1000, 9999)}`;
  return {
    name: `${pick(realtorNames)} ${suffix}`,
    email: `${username}@mail.com`,
    username,
    password: 'pass',
    phone: phoneForSuffix(suffix),
    agency: `${TEST_PREFIX} Realty`,
    agencySite: 'https://load-test.example.com',
  };
}

function shortRunId() {
  return String(RUN_ID).slice(-8);
}

export function realEstatePayload({ prefix = TEST_PREFIX, isPrivate = true } = {}) {
  const suffix = uniqueSuffix();
  const totalArea = randomInt(70, 160);
  const kitchenArea = randomInt(8, 30);
  const livingArea = totalArea - kitchenArea - randomInt(8, 20);

  return {
    name: `${prefix} apartment ${suffix}`,
    description: 'Load test listing for distributed throughput and latency experiment.',
    price: randomInt(45000, 350000),
    owner: {
      name: `${prefix} Owner`,
      phone: '+380130495830',
      email: null,
    },
    location: {
      city: pick(cities),
      district: pick(districts),
      residentialArea: pick(residentialAreas),
      street: pick(streets),
      housingEstate: null,
      houseNumber: randomInt(1, 180),
      block: null,
      apartmentNumber: randomInt(1, 240),
      landmark: 'Load test landmark',
    },
    loggia: {
      type: 'LOGGIA',
      count: randomInt(1, 2),
      glassed: Math.random() > 0.5,
    },
    bathroom: {
      type: 'TOILET_BATH',
      count: 1,
      combined: Math.random() > 0.5,
    },
    area: {
      total: totalArea,
      living: livingArea,
      kitchen: kitchenArea,
    },
    floor: randomInt(1, 16),
    floorsInBuilding: randomInt(16, 24),
    buildingType: pick(buildingTypes),
    heatingType: 'CENTRALIZED',
    windowsType: 'METAL_PLASTIC',
    hotWaterType: 'CENTRALIZED',
    stateType: pick(states),
    announcementType: pick(announcements),
    roomsCount: randomInt(1, 5),
    ceilingHeight: 2.7 + (randomInt(0, 8) / 10),
    documents: null,
    private: isPrivate,
  };
}

export function realtorSearchQuery() {
  const variants = [
    { page: randomInt(0, 5), size: pick([5, 10, 15, 20]) },
    { page: randomInt(0, 5), size: pick([5, 10, 15, 20]), name: 'Realtor' },
    { page: randomInt(0, 5), size: pick([5, 10, 15, 20]), agency: 'agency' },
  ];
  return pick(variants);
}

export function realEstateSearchQuery() {
  const variants = [
    { page: randomInt(0, 8), size: pick([5, 10, 15, 20]) },
    { page: randomInt(0, 8), size: pick([5, 10, 15, 20]), city: pick(cities) },
    { page: randomInt(0, 8), size: pick([5, 10, 15, 20]), minPrice: 30000, maxPrice: 400000 },
    { page: randomInt(0, 8), size: pick([5, 10, 15, 20]), buildingType: pick(buildingTypes) },
    { page: randomInt(0, 8), size: pick([5, 10, 15, 20]), announcementType: pick(announcements) },
    { page: randomInt(0, 8), size: pick([5, 10, 15, 20]), roomsCount: randomInt(1, 5) },
  ];
  return pick(variants);
}

function phoneForSuffix(suffix) {
  const number = hash(suffix) % 1000000000;
  return `+380${String(number).padStart(9, '0')}`;
}

function hash(value) {
  return String(value).split('').reduce((acc, char) => (
    ((acc * 31) + char.charCodeAt(0)) % 1000000000
  ), 0);
}
