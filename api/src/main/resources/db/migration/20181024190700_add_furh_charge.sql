-- add inac furh charge currency
INSERT INTO currencies(
        currency_code,
        currency_name,
        country_code,
        decimal_places,
        symbol, active,
        created_by,
        allow_updated_from_web
    ) VALUES (
        'VFC',
        'Venezuelan FURH Charge',
        country_id('Venezuela'),
        2,
        'FURH',
        false,
        'system',
        false
    );

-- add inac furh charge specific columns
ALTER TABLE flight_movements
    ADD COLUMN furh_charge_legs integer;

ALTER TABLE flight_movements
    ADD COLUMN furh_charge_rate double precision;

ALTER TABLE flight_movements
    ADD COLUMN furh_charge_amount double precision;

-- add translations for new FURH charge labels
SELECT add_languages_by_json('[
  {
    "token": "Unique Factor of Human Resource",
    "code": "en",
    "val": "Unique Factor of Human Resource",
    "created_by": "system",
    "part": "backend"
  },
  {
    "token": "Unique Factor of Human Resource",
    "code": "es",
    "val": "Factor Unico de Recurso Humano",
    "created_by": "system",
    "part": "backend"
  },
  {
    "token": "Missing applicable exchange rates for currency {{fromCurrencyCode}} to {{toCurrencyCode}} on {{date}}",
    "code": "en",
    "val": "Missing applicable exchange rates for currency {{fromCurrencyCode}} to {{toCurrencyCode}} on {{date}}",
    "created_by": "system",
    "part": "backend"
  },
  {
    "token": "Missing applicable exchange rates for currency {{fromCurrencyCode}} to {{toCurrencyCode}} on {{date}}",
    "code": "es",
    "val": "Faltan los tipos de cambio aplicables para la moneda {{fromCurrencyCode}} a {{toCurrencyCode}} en {{date}}",
    "created_by": "system",
    "part": "backend"
  },
  {
    "token": "Missing applicable exchange rates for currency {{currencyCode}} on {{date}}",
    "code": "en",
    "val": "Missing applicable exchange rates for currency {{currencyCode}} on {{date}}",
    "created_by": "system",
    "part": "backend"
  },
  {
    "token": "Missing applicable exchange rates for currency {{currencyCode}} on {{date}}",
    "code": "es",
    "val": "Faltan los tipos de cambio aplicables a la moneda {{currencyCode}} el {{date}}",
    "created_by": "system",
    "part": "backend"
  },
  {
    "token": "FURH Legs",
    "code": "en",
    "val": "FURH Legs",
    "created_by": "system",
    "part": "frontend"
  },
  {
    "token": "FURH Legs",
    "code": "es",
    "val": "Piernas FURH",
    "created_by": "system",
    "part": "frontend"
  },
  {
    "token": "FURH Rates",
    "code": "en",
    "val": "FURH Rates",
    "created_by": "system",
    "part": "frontend"
  },
  {
    "token": "FURH Rates",
    "code": "es",
    "val": "Tarifas FURH",
    "created_by": "system",
    "part": "frontend"
  },
  {
    "token": "FURH Charges",
    "code": "en",
    "val": "FURH Charges",
    "created_by": "system",
    "part": "frontend"
  },
  {
    "token": "FURH Charges",
    "code": "es",
    "val": "Cargos FURH",
    "created_by": "system",
    "part": "frontend"
  }
]');
