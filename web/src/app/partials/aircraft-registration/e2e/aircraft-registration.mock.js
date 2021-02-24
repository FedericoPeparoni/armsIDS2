let aircraftRegistrationWithNoId = {
  mtow_override: 165.35,
  registration_expiry_date: "2017-01-18T23:59:59Z",
  registration_number: "CD456",
  registration_start_date: "2017-01-04T00:00:00Z",
  account: {
    name: "Mock Account",
    id: 1
  },
  aircraft_type: {
    aircraft_name: "A300",
    aircraft_type: "A30B",
    id: 4,
    manufacturer: "AIRBUS",
    maximum_takeoff_weight: 181.88
  },
  country_of_registration: {
    country_code: "AFG",
    country_name: "Afghanistan",
    id: 4
  }
};

let aircraftRegistrationWithId = {
  id: 1
};

let account = {
  id: 1,
  name: 'Mock Account'
};

let aircraftType = {
  aircraft_name: "A300",
  aircraft_type: "A30B",
  id: 4,
  manufacturer: "AIRBUS",
  maximum_takeoff_weight: 181.88
};

let country = {
  country_code: "AFG",
  country_name: "Afghanistan",
  id: 4
};

Object.assign(aircraftRegistrationWithId, aircraftRegistrationWithNoId);

module.exports = [{
    request: {
      path: '/accounts',
      method: 'GET'
    },
    response: {
      data: {content: [account]}
    }
  },
  {
    request: {
      path: '/aircraft-types',
      method: 'GET'
    },
    response: {
      data: {content: [aircraftType]}
    }
  },
  {
    request: {
      path: '/countries',
      method: 'GET'
    },
    response: {
      data: {content: [country]}
    }
  },
  {
    request: {
      path: '/aircraft-registrations',
      method: 'GET'
    },
    response: {
      data: {content: [aircraftRegistrationWithId]}
    }
  },
  {
    request: {
      path: '/aircraft-registrations',
      method: 'POST',
      data: aircraftRegistrationWithNoId
    },
    response: {
      data: {content: [aircraftRegistrationWithId]}
    }
  },
  {
    request: {
      path: '/aircraft-registrations',
      method: 'PUT',
      data: aircraftRegistrationWithId
    },
    response: {
      data: {content: [aircraftRegistrationWithId]}
    }
  },
  {
    request: {
      path: '/aircraft-registrations/1',
      method: 'DELETE'
    },
    response: ''
  }
];