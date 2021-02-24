let scheduleWithNoId = {
  schedule_id: 1,
  schedule_type: 'WATER',
  minimum_charge: 700,
  utilities_range_bracket: [{
    id: 1,
    schedule_id: 1,
    range_top_end: 500,
    unit_price: 500
  }],
  utilities_water_towns_and_village: [{
    id: 1,
    town_or_village_name: 'Tinseltown',
    water_utility_schedule: 1
  }]
};

let scheduleWithId = {
  id: 1
};

let rangeWithNoId = {
  schedule_id: 1,
  range_top_end: 500,
  unit_price: 500
};

let rangeWithId = {
  id: 2
}

Object.assign(scheduleWithId, scheduleWithNoId);
Object.assign(rangeWithId, rangeWithNoId);

module.exports = [{
    request: {
      path: '/utilities-schedules',
      method: 'GET'
    },
    response: {
      data: {content: [scheduleWithId]}
    }
  },
  {
    request: {
      path: '/utilities-schedules',
      method: 'POST',
      data: scheduleWithNoId
    },
    response: {
      data: {content: [scheduleWithId]}
    }
  },
  {
    request: {
      path: '/utilities-schedules',
      method: 'PUT',
      data: scheduleWithId
    },
    response: {
      data: {content: [scheduleWithId]}
    }
  },
  {
    request: {
      path: '/utilities-schedules/1',
      method: 'DELETE'
    },
    response: ''
  },
  {
    request: {
      path: '/range-brackets',
      method: 'GET'
    },
    response: {
      data: {content: [rangeWithId]}
    }
  },
  {
    request: {
      path: '/range-brackets',
      method: 'POST',
      data: rangeWithNoId
    },
    response: {
      data: {content: [rangeWithId]}
    }
  },
  {
    request: {
      path: '/range-brackets',
      method: 'PUT',
      data: rangeWithId
    },
    response: {
      data: {content: [rangeWithId]}
    }
  },
  {
    request: {
      path: '/range-brackets/1',
      method: 'DELETE'
    },
    response: ''
  }
];