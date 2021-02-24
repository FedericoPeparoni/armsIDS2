let flightStatusExemptionsNoId = {
  'flight_item_type': 'Item 18 - Statuses',
  'flight_item_value': 'Navaid Calibration',
  'enroute_fees_are_exempt': true,
  'late_arrival_fees_exempt': true,
  'late_departure_fees_exempt': true,
  'parking_fees_are_exempt': true,
  'adult_passenger_fees_exempt': true,
  'child_passenger_fees_exempt': true,
  'approach_fees_exempt': true,
  'aerodrome_fees_exempt': true,
  'flight_notes': 'test notes'
};

let flightStatusExemptionsWithId = {
  'id': 2
};

Object.assign(flightStatusExemptionsWithId, flightStatusExemptionsNoId);

module.exports = [{
  request: {
    path: '/exempt-flight-status',
    method: 'GET'
  },
  response: {
    data: {content: [flightStatusExemptionsWithId]}
  }
},
  {
    request: {
      path: '/exempt-flight-status',
      method: 'POST',
      data: flightStatusExemptionsNoId
    },
    response: {
      data: {content: [flightStatusExemptionsWithId]}
    }
  },
  {
    request: {
      path: '/exempt-flight-status',
      method: 'PUT',
      data: flightStatusExemptionsWithId
    },
    response: {
      data: {content: [flightStatusExemptionsWithId]}
    }
  },
  {
    request: {
      path: '/exempt-flight-status/2',
      method: 'DELETE'
    },
    response: ''
  }
];
