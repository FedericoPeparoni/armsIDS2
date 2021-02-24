let transactionNoId = {
  account: {
    id: 1,
    kra_receipt_number: 'receipt',
    kra_clerk_name: 'clerk',
    name: 'test account',
    alias: null,
    account_type: {
      id: 1,
      name: 'Aviation'
    },
    invoice_delivery_format: null,
    invoice_delivery_method: null,
    invoice_currency: {
      id: 4,
      currency_code: 'AFN',
      currency_name: 'afn',
      decimal_places: 2,
      allow_updated_from_web: false,
      exchange_rate_to_usd: 1,
      active: null
    }
  },
  transaction_date_time: new Date(),
  description: 'test description',
  transaction_type: {
    id: 1,
    name: 'credit'
  },
  amount: 10000,
  currency: {
    id: 4,
    currency_code: 'AFN',
    currency_name: 'afn',
    decimal_places: 2,
    exchange_rate_to_usd: 1,
  },
  payment_mechanism: 'cash',
  payment_reference_number: 123,
  exported: false,
  billing_ledger_ids: null,
  exchange_rate_to_usd: null,
  exchange_rate_to_ansp: null,
  balance: null
}

let transactionWithId = {
  id: 1
};

let account = {
  id: 1,
  name: 'test account',
  alias: null,
  account_type: {
    id: 1,
    name: 'Aviation'
  },
  invoice_delivery_format: null,
  invoice_delivery_method: null,
  invoice_currency: {
    id: 4,
    currency_code: 'AFN',
    currency_name: 'afn',
    decimal_places: 2,
    allow_updated_from_web: false,
    exchange_rate_to_usd: 1,
    active: null
  }
};

let invoice = {
  id: 1,
  account: {
    id: 1,
    name: 'test account',
    account_type: {
      id: 1,
      name: 'Airline'
    },
    invoice_currency: {
      id: 4,
      currency_code: 'AFN',
      currency_name: 'Afghan Afghani',
      country_code: {
        id: 4,
        country_code: 'AFG',
        country_name: 'Afghanistan'
      },
      decimal_places: 2,
      symbol: '؋',
      exchange_rate_to_usd: 57.392301,
      exchange_rate_valid_from_date: null,
      exchange_rate_valid_to_date: null,
      active: false,
      allow_updated_from_web: true
    },
    type: {
      id: 1,
      name: 'aviation'
    },
    credit_limit: 10000.0
  },
  aviation: true,
  invoice_state_type: 'published',
  invoice_amount: 100.0,
  invoice_currency: {
    id: 4,
    currency_code: 'AFN',
    currency_name: 'Afghan Afghani',
    country_code: {
      id: 4,
      country_code: 'AFG',
      country_name: 'Afghanistan'
    },
    decimal_places: 2,
    symbol: '؋',
    exchange_rate_to_usd: 57.392301,
    exchange_rate_valid_from_date: null,
    exchange_rate_valid_to_date: null,
    active: false,
    allow_updated_from_web: true
  },
  invoice_exchange_to_usd: 1.0,
  invoice_exchange_to_ansp: 1.0,
  payment_amount: 300.0,
  payment_currency: {
    id: 4,
    currency_code: 'AFN',
    currency_name: 'Afghan Afghani',
    country_code: {
      id: 4,
      country_code: 'AFG',
      country_name: 'Afghanistan'
    },
    decimal_places: 2,
    symbol: '؋',
    exchange_rate_to_usd: 57.392301,
    exchange_rate_valid_from_date: null,
    exchange_rate_valid_to_date: null,
  },
  payment_exchange_to_usd: 1.0,
  exported: false,
  billing_ledger_type: null,
  amount_owing: 0.73
}

Object.assign(transactionWithId, transactionNoId)

module.exports = [{
    request: {
      path: '/transactions',
      method: 'GET'
    },
    response: {
      data: {content: [transactionWithId]}
    }
  },
  {
    request: {
      path: '/billing-ledgers',
      method: 'GET',
      data: {
        accountId: 1,
        currencyId: 4
      }
    },
    response: {
      data: {content: [invoice]}
    }
  },
  {
    request: {
      path: '/billing-ledgers/getUnpaidBillingLedgersByAccountAndCurrency?accountId=1&currencyId=4',
      method: 'GET',
      data: {
        accountId: 1,
        currencyId: 4
      }
    },
    response: {
      data: {content: [invoice]}
    }
  },
  {
    request: {
      path: '/accounts',
      method: 'GET'
    },
    response: {
      data: {content:[account]}
    }
  },
  {
    request: {
      path: '/transactions',
      method: 'POST',
      data: transactionNoId
    },
    response: {
      data: {content: [transactionWithId]}
    }
  },
  {
    request: {
      path: '/transactions',
      method: 'PUT',
      data: transactionWithId
    },
    response: {
      data: {content: [transactionWithId]}
    }
  },
  {
    request: {
      path: '/transactions/1',
      method: 'DELETE'
    },
    response: ''
  }
];
