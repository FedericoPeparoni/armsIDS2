let currencyNoId = {
  country_code: "Test",
  active: true,
  currency_code: "TTT",
  currency_name: "Test TTT",
  decimal_places: 4,
  symbol: "$",
};

let exchangeRateNoId = {
  currency: {
    id: 1,
  },
  currency_code: 'AAA',
  exchange_rate: 0.80,
  exchange_rate_valid_from_date: '2017-01-01',
  exchange_rate_valid_to_date: '2017-01-01',
  target_currency: {
    id: 1,
  },
};

let exchangeWithId = {
    id: 1
}

let country = {
  id: 1,
  country_code: "TTT",
  country_name: "Test"
};

let currencyWithId = {
  id: 1
};

Object.assign(currencyWithId, currencyNoId);
Object.assign(exchangeWithId, exchangeRateNoId);

module.exports = [{
    request: {
      path: '/countries',
      method: 'GET'
    },
    response: {
      data: {
        content: [country]
      }
    }
  },
  {
    request: {
      path: '/currencies',
      method: 'GET'
    },
    response: {
      data: {
        content: [currencyWithId]
      }
    }
  },
  {
    request: {
      path: '/currencies',
      method: 'POST',
      data: currencyNoId
    },
    response: {
      data: {
        content: [currencyWithId]
      }
    }
  },
  {
    request: {
      path: '/currencies',
      method: 'PUT',
      data: currencyWithId
    },
    response: {
      data: {
        content: [currencyWithId]
      }
    }
  },
  {
    request: {
      path: '/currencies/1',
      method: 'DELETE'
    },
    response: ''
  },
  {
    request: {
      path: '/currency-exchange-rates/for-currency/1',
      method: 'GET'
    },
    response: {
      data: {
        content: [exchangeWithId]
      }
    }
  },
  {
    request: {
      path: '/currency-exchange-rates',
      method: 'POST',
      data: exchangeRateNoId
    },
    response: {
      data: {
        content: [exchangeWithId]
      }
    }
  },
  {
    request: {
      path: '/currency-exchange-rates',
      method: 'PUT',
      data: exchangeWithId
    },
    response: {
      data: {
        content: [exchangeWithId]
      }
    }
  },
  {
    request: {
      path: '/currency-exchange-rates/1',
      method: 'DELETE'
    },
    response: ''
  }
];
