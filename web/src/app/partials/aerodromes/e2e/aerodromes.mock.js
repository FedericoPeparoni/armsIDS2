let aerodromeNoId = {
  aerodrome_name: "TEST",
  aerodrome_category: {
    id: 2,
    category_name: "Test AC",
    international_passenger_fee_adult: 0.02
  },
  aixm_flag: false,
  billing_center: {
    aerodromes: [],
    id: 1,
    invoice_sequence_number: 3,
    name: "Test Billing Centre",
    prefix_invoice_number: "as",
    users: []
  },
  geometry: {
    type: "Point",
    coordinates: [3, 3]
  },
  is_default_billing_center: false
};

let aerodromeWithId = {
  "id": 1
};

let billingCentre = {
  aerodromes: [],
  id: 1,
  invoice_sequence_number: 3,
  name: "Test Billing Centre",
  prefix_invoice_number: "as",
  users: []
}

let aerodrome_category = {
  category_name: "Test AC",
  domestic_passenger_fee_adult: 0.02,
  domestic_passenger_fee_child: 0.02,
  id: 2,
  international_passenger_fee_adult: 0.02,
  international_passenger_fee_child: 0.02
}

Object.assign(aerodromeWithId, aerodromeNoId);

module.exports = [{
    request: {
      path: '/aerodromes',
      method: 'GET'
    },
    response: {
      data: { content: [aerodromeWithId] }
    }
  },
  {
    request: {
      path: '/billing-centers',
      method: 'GET'
    },
    response: {
      data: { content: [billingCentre] }
    }
  },
  {
    request: {
      path: '/aerodromecategories',
      method: 'GET'
    },
    response: {
      data: { content: [aerodrome_category] }
    }
  },
  {
    request: {
      path: '/aerodromes',
      method: 'POST',
      data: aerodromeNoId
    },
    response: {
      data: [
        { content: [aerodromeWithId] }
      ]
    }
  },
  {
    request: {
      path: '/aerodromes',
      method: 'PUT',
      data: aerodromeWithId
    },
    response: {
      data: [
        { content: [aerodromeWithId] }
      ]
    }
  },
  {
    request: {
      path: '/aerodromes/1',
      method: 'DELETE'
    },
    response: ''
  }
];
