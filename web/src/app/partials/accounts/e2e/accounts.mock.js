let accountNoId = {
  name: 'Air Canada3',
  alias: 'AC',
  aviation_billing_contact_person_name: 'John Smith',
  aviation_billing_phone_number: '1234567890',
  aviation_billing_mailing_address: '123 Yelp Str, Ottawa, ON, T56 0I8, Canada',
  aviation_billing_email_address: 'john@test.com',
  aviation_billing_sms_number: '1234567890',
  non_aviation_billing_contact_person_name: 'Alla Dyer',
  non_aviation_billing_phone_number: '1234554321',
  non_aviation_billing_mailing_address: '456 Carling Ave, Ottawa, ON, K2A 7T5, Canada',
  non_aviation_billing_email_address: 'alla@test.com',
  non_aviation_billing_sms_number: '1234554321',
  self_care_portal_user_name: 'test',
  self_care_portal_password: 'test',
  iata_code: 'K1',
  icao_code: 'I45',
  opr_identifier: 'test',
  payment_terms: 5,
  discount_structure: 6,
  tax_profile: 'test',
  percentage_of_passenger_fee_payable: 20,
  invoice_delivery_format: 'test',
  invoice_delivery_method: 'paper',
  invoice_currency: 'Test (TT)',
  monthly_overdue_penalty_rate: 2,
  notes: 'test',
  black_listed_indicator: true,
  black_listed_override: true,
  credit_limit: 5,
  aircraft_parking_exemption: 3,
  account_type: 'test',
  iata_member: true,
  separate_pax_invoice: true,
  external_accounting_system_identifier: 'test',
  active: true,
};

let type = {
  id: 1,
  name: 'Test'
};

let invoice_currency = {
  id: 123,
  currency_code: 'TT',
  currency_name: 'Test',
  country_code: 'TT',
  decimal_places: 2,
  allow_updated_from_web: null,
  symbol: 'TT',
  exchange_rate: 20,
  exchange_rate_valid_from_date: '2017-Jan-17',
  exchange_rate_valid_to_date: '2017-Feb-10',
  active: 'True'
};

let accountWithId = {
  "id": 1
};

Object.assign(accountWithId, accountNoId);

module.exports = [{
    request: {
      path: '/accounts',
      method: 'GET',
      params: {
        filter: 'active'
      }
    },
    response: {
      data: {content: [accountWithId]}
    }
  },
  {
    request: {
      path: '/accounts?filter=not-active',
      method: 'GET',
      data: {
        filter: 'not-active',
      }
    },
    response: {
      data: ''
    }
  },
  {
    request: {
      path: '/accounts?filter=active',
      method: 'GET',
      data: {
        filter: 'active',
      }
    },
    response: {
      data: {content: [accountWithId]}
    }
  },
  {
    request: {
      path: '/currencies',
      method: 'GET'
    },
    response: {
      data: {content: [invoice_currency]}
    }
  },
  {
    request: {
      path: '/account-types',
      method: 'GET'
    },
    response: {
      data: {content: [type]}
    }
  },
  {
    request: {
      path: '/accounts',
      method: 'POST',
      data: accountNoId
    },
    response: {
      data: {content: [accountWithId]}
    }
  },
  {
    request: {
      path: '/accounts',
      method: 'PUT',
      data: accountWithId
    },
    response: {
      data: {content: [accountWithId]}
    }
  },
  {
    request: {
      path: '/accounts/1',
      method: 'DELETE'
    },
    response: ''
  }
];
