let countryNoId = {
  country: {
    id: 3,
    country_code: 'AAA',
    country_name: 'Hibernia'
  }
};

let country = {
  id: 4,
  country_code: "AFG",
  country_name: "Afghanistan"
};

let countryWithId = {
  id: 1
};

Object.assign(countryWithId, countryNoId);

module.exports = [{
    request: {
      path: '/regional-countries',
      method: 'GET'
    },
    response: {
      data: {content: [countryWithId]}
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
      path: '/regional-countries',
      method: 'PUT',
      data: countryWithId
    },
    response: {
      data: {content: [countryWithId]}
    }
  }
];
