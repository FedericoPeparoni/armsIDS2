// interface
import { IRecurringCharge } from '../recurring-charges.interface';

// service
import { CRUDService } from '../../../angular-ids-project/src/helpers/services/crud.service';

export let endpoint: string = 'recurring-charges';

export class RecurringChargesService extends CRUDService {

  protected restangular: restangular.IService;

  private _mod: IRecurringCharge = {
    id: null,
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
    account: {
      id: null,
      name: null,
      alias: null,
      aviation_billing_contact_person_name: null,
      aviation_billing_phone_number: null,
      aviation_billing_mailing_address: null,
      aviation_billing_email_address: null,
      aviation_billing_sms_number: null,
      non_aviation_billing_contact_person_name: null,
      non_aviation_billing_phone_number: null,
      non_aviation_billing_mailing_address: null,
      non_aviation_billing_email_address: null,
      non_aviation_billing_sms_number: null,
      account_users: [],
      is_self_care: null,
      iata_code: null,
      icao_code: null,
      opr_identifier: null,
      payment_terms: null,
      discount_structure: null,
      tax_profile: null,
      percentage_of_passenger_fee_payable: null,
      invoice_delivery_format: null,
      invoice_delivery_method: null,
      invoice_currency: {
        id: null,
        currency_code: null,
        currency_name: null,
        country_code: null,
        decimal_places: null,
        allow_updated_from_web: null,
        symbol: null,
        active: null,
        external_accounting_system_identifier: null,
        exchange_rate_target_currency_id: null
      },
      monthly_overdue_penalty_rate: null,
      notes: null,
      black_listed_indicator: null,
      black_listed_override: null,
      credit_limit: null,
      aircraft_parking_exemption: null,
      account_type: null,
      account_type_discount: null,
      list_of_events_account_notified: null,
      iata_member: null,
      separate_pax_invoice: null,
      external_accounting_system_identifier: null,
      active: null,
      cash_account: null,
      approved_flight_school_indicator: null,
      nationality: null,
      whitelist_last_activity_date_time: null,
      whitelist_state: null,
      whitelist_inactivity_notice_sent_flag: null,
      whitelist_expiry_notice_sent_flag: null
    },
    start_date: null,
    end_date: null
  };

  /** @ngInject */
  constructor(protected Restangular: restangular.IService) {
    super(Restangular, endpoint);
    this.model = angular.copy(this._mod);
  }

  getByAccountId(accountId: number, filter: string): ng.IPromise<IRecurringCharge[]> {
    return this.restangular.one(`${endpoint}/${accountId}`).get({ filter });
  }

}

