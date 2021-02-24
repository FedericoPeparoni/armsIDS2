let towerNoId = {
  date_of_contact: '2017-Feb-11',
  flight_id: '100',
  registration: 'AFMN',
  aircraft_type: '100D',
  operator_name: 'FXBR',
  departure_aerodrome: 'Ottawa',
  departure_contact_time: '1234',
  destination_aerodrome: 'Ottawa',
  destination_contact_time: '1234',
  route: 'ABDGDGD',
  flight_level: '15000',
  flight_crew: 14,
  passengers: 200,
  flight_category: 'sch',
  day_of_flight: '2017-Feb-11',
  departure_time: '1234'
};

let towerWithId = {
  "id": 1
};

let aerodrome = {
    aerodrome_name: "Ottawa",
    aerodrome_category: "Test",
    aixm_flag: false,
    billing_center: "Test Billing Centre",
    geometry: {
        type: "Point",
        coordinates: [4, 4]
    },
    id: 1,
    is_default_billing_center: false
};

let aircraftType = {
    aircraft_image: null,
    aircraft_name: 'sputnik',
    aircraft_type: 'S123',
    id: 1,
    manufacturer: 'test',
    maximum_takeoff_weight: 2.4,
    wake_turbulence_category: {
        id: 1,
        name: 'L'
    }
};

Object.assign(towerWithId, towerNoId);

module.exports = [
  {
        request: {
            path: '/aerodromes',
            method: 'GET'
        },
        response: {
            data: { content: [aerodrome] }
        }
    },
  {
      request: {
          path: '/aircraft-types',
          method: 'GET'
      },
      response: {
          data: { content: [aircraftType] }
      }
  },
  {
    request: {
      path: '/tower-movement-log',
      method: 'GET'
    },
    response: {
      data: {content: [towerWithId]}
    }
  },
  {
    request: {
      path: '/tower-movement-log',
      method: 'POST',
      data: towerNoId
    },
    response: {
      data: {content: [towerWithId]}
    }
  },
  {
    request: {
      path: '/tower-movement-log',
      method: 'PUT',
      data: towerWithId
    },
    response: {
      data: {content: [towerWithId]}
    }
  },
  {
    request: {
      path: '/tower-movement-log/1',
      method: 'DELETE'
    },
    response: ''
  }
];
