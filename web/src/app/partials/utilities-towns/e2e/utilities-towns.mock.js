let townWithNoId = {
  town_or_village_name: 'Tinseltown',
  water_utility_schedule: {
    minimum_charge: 5,
    schedule_id: 1,
    schedule_type: "WATER"
  },
  electricity_utility_schedule: {
    minimum_charge: 777,
    schedule_id: 2,
    schedule_type: "ELECTRIC"
  }
};

let townWithId = {
  id: 1
};

let schedule = [{
minimum_charge: 5,
schedule_id: 1,
schedule_type: "WATER",
},
{minimum_charge: 777,
schedule_id: 2,
schedule_type: "ELECTRICITY"}]

Object.assign(townWithId, townWithNoId);

module.exports = [{
    request: {
      path: '/utilities-towns-and-villages',
      method: 'GET'
    },
    response: {
      data: {content:[townWithId]}
    }
  },
  {
    request: {
      path: '/utilities-schedules',
      method: 'GET'
    },
    response: {
      data: {content:schedule}
    }
  },
  {
    request: {
      path: '/utilities-towns-and-villages',
      method: 'POST',
      data: townWithNoId
    },
    response: {
      data: {content:[townWithId]}
    }
  },
  {
    request: {
      path: '/utilities-towns-and-villages',
      method: 'PUT',
      data: townWithId
    },
    response: {
      data: {content:[townWithId]}
    }
  },
  {
    request: {
      path: '/utilities-towns-and-villages/1',
      method: 'DELETE'
    },
    response: ''
  }];
