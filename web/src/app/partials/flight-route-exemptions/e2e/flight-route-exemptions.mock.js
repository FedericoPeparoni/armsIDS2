let exemptionNoId = {
  departure_aerodrome_id: 1.0,
  destination_aerodrome_id: 1.0,
  exemption_in_either_direction: true,
  enroute_fees_are_exempt: true,
  approach_fees_are_exempt: true,
  aerodrome_fees_are_exempt: true,
  late_arrival_fees_are_exempt: true,
  late_departure_fees_are_exempt: true,
  parking_fees_are_exempt: true,
  adult_passenger_fees_are_exempt: true,
  child_passenger_fees_are_exempt: true,
  flight_notes: "notes"
};

let exemptionWithId = {
  id: 1
};

for (let item in exemptionNoId) {
  if (exemptionNoId.hasOwnProperty(item)) {
    exemptionWithId[item] = exemptionNoId[item];
  }
}


module.exports = [
  {
    request: {
      path: '/exempt-flight-routes',
      method: 'GET'
    },
    response: {
      data: {content: [exemptionWithId]}
    }
  },
  {
    request: {
      path: '/exempt-flight-routes',
      method: 'POST',
      data: exemptionNoId
    },
    response: {
      data: {content: [exemptionWithId]}
    }
  },
  {
    request: {
      path: '/exempt-flight-routes/1',
      method: 'PUT',
      data: exemptionWithId
    },
    response: {
      data: {content: [exemptionWithId]}
    }
  },
  {
    request: {
      path: '/exempt-flight-routes/1',
      method: 'DELETE'
    },
    response: ''
  }
];
