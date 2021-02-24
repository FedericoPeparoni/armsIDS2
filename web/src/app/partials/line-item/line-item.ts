// there's no service, but this is the object
import { IInvoiceLineItem } from './line-item.interface';

export const LineItem: IInvoiceLineItem = {
  aerodrome: {
    id: null,
    aerodrome_name: null,
    extended_aerodrome_name: null,
    aixm_flag: null,
    is_default_billing_center: null,
    external_accounting_system_identifier: null,
    geometry: {
      type: null,
      coordinates: [
        null,
        null
      ]
    },
    aerodrome_category: {
      id: null,
      category_name: null,
      international_passenger_fee_adult: null,
      international_passenger_fee_child: null,
      domestic_passenger_fee_adult: null,
      domestic_passenger_fee_child: null,
      international_fees_currency: {
        id: null,
        currency_code: null,
        currency_name: null,
        country_code: {
          id: null,
          country_code: null,
          country_name: null,
          aircraft_registration_prefixes: null,
          aerodrome_prefixes: null
        },
        decimal_places: null,
        symbol: null,
        active: null,
        allow_updated_from_web: null,
        external_accounting_system_identifier: null,
        exchange_rate_target_currency_id: null
      },
      domestic_fees_currency: {
        id: null,
        currency_code: null,
        currency_name: null,
        country_code: {
          id: null,
          country_code: null,
          country_name: null,
          aircraft_registration_prefixes: null,
          aerodrome_prefixes: null
        },
        decimal_places: null,
        symbol: null,
        active: null,
        allow_updated_from_web: null,
        external_accounting_system_identifier: null,
        exchange_rate_target_currency_id: null
      }
    },
    billing_center: {
      aerodromes: [],
      hq: null,
      id: null,
      invoice_sequence_number: null,
      name: null,
      prefix_invoice_number: null,
      prefix_receipt_number: null,
      receipt_sequence_number: null,
      users: [],
      external_accounting_system_identifier: null,
      iata_invoice_sequence_number: null,
      receipt_cheque_sequence_number: null,
      receipt_wire_sequence_number: null
    },
    aerodrome_services: null
  },
  service_charge_catalogue: {
    id: null,
    charge_class: null,
    category: null,
    type: null,
    subtype: null,
    description: null,
    charge_basis: null,
    minimum_amount: null,
    maximum_amount: null,
    amount: null,
    invoice_category: null,
    external_accounting_system_identifier: null,
    external_charge_category: null,
    currency: null
  },
  recurring_charge: null,
  account_external_system_identifier: null,
  amount: null,
  user_unit_amount: null,
  user_markup_amount: null,
  user_price: null,
  user_electricity_meter_reading: null,
  user_electricity_charge_type: null,
  user_water_meter_reading: null,
  user_discount_percentage: null,
  user_town: {
    id: null,
    town_or_village_name: null,
    water_utility_schedule: null,
    residential_electricity_utility_schedule: null,
    commercial_electricity_utility_schedule: null
  },
  price_per_unit: null,
  any_aerodrome: null,
  invoice_permit: {
    invoice_permit_number: null,
    external_database_for_charge: null,
    adhoc_total_fee_payment_amount: null
  },
  requisition: {
    req_number: null,
    external_database_for_charge: null,
    req_currency: null,
    req_total_amount: null,
    req_ar_id: null,
    req_country_id: null,
    req_id: null,
    req_maninfo_id: null
  }
};
