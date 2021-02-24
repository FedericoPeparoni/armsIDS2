let aerodromeCategoryWithNoId = {
  aerodromes: [],
  category_name: "Test 2",
  created_at: "2017-01-16T12:42:30Z",
  created_by: "admin",
  domestic_passenger_fee_adult: 0.03,
  domestic_passenger_fee_child: 0.04,
  id: 1,
  international_passenger_fee_adult: 0.01,
  international_passenger_fee_child: 0.02,
  ldp_billing_formulas: [],
  updated_at: null,
  updated_by: null
};

let aerodrome = {
  aerodrome_name: "Test Aerodrome",
  aixm_flag: true,
  geometry: {
    type: "Point",
    coordinates: [88, 18]
  },
  id: 1
};

let aerodromeCategoryWithId = {
  id: 1
};

aerodromeCategoryWithId = Object.assign(aerodromeCategoryWithId, aerodromeCategoryWithNoId);

module.exports = [
  {
    request: {
      path: '/aerodromes',
      method: 'GET'
    },
    response: {
      data: [
        {content: [aerodrome]}
      ]
    }
  },
  {
    request: {
      path: '/aerodromecategories',
      method: 'PUT',
      data: aerodromeCategoryWithId
    },
    response: {
      data: {content: [aerodromeCategoryWithId]}
    }
  },
  {
    request: {
      path: '/aerodromecategories',
      method: 'DELETE',
      data: aerodromeCategoryWithId.id
    },
    response: {
      data: [
        ''
      ]
    }
  },
];