import { IAccount, IAccountMinimal } from '../../accounts/accounts.interface';
import { CRUDService } from '../../../angular-ids-project/src/helpers/services/crud.service';

export let endpoint: string = 'sc-account-management';

export class ScAccountManagementService extends CRUDService {

  protected restangular: restangular.IService;

  private _mod: IAccount = {
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
    is_self_care: true,
    account_users: [],
    iata_code: null,
    icao_code: null,
    opr_identifier: null,
    payment_terms: null,
    discount_structure: null,
    tax_profile: null,
    percentage_of_passenger_fee_payable: null,
    invoice_delivery_format: null,
    invoice_delivery_method: null,
    invoice_currency: null,
    monthly_overdue_penalty_rate: null,
    notes: null,
    black_listed_indicator: false,
    black_listed_override: false,
    credit_limit: null,
    aircraft_parking_exemption: 0,
    account_type: null,
    account_type_discount: null,
    list_of_events_account_notified: null,
    iata_member: false,
    separate_pax_invoice: false,
    external_accounting_system_identifier: null,
    active: true,
    cash_account: true,
    approved_flight_school_indicator: false,
    nationality: null,
    whitelist_last_activity_date_time: null,
    whitelist_state: null,
    whitelist_inactivity_notice_sent_flag: null,
    whitelist_expiry_notice_sent_flag: null
  };

  /** @ngInject */
  constructor(protected Restangular: restangular.IService) {
    super(Restangular, endpoint);
    this.model = angular.copy(this._mod);
  }

  public getSCAccounts(onlyActive?: boolean): ng.IPromise<IAccountMinimal> {
    const active = onlyActive ? '?active=true' : '';
    return this.restangular.one(`${endpoint}/approved-only${active}`).get();
  }

  public getSCActiveAccounts(): ng.IPromise<IAccountMinimal> {
    return this.getSCAccounts(true);
  }

  public getSCWhiteListAccounts(): ng.IPromise<IAccountMinimal> {
    return this.restangular.one(`${endpoint}/cash-whitelist`).get();
  }

  public getAccountsWithFlightSchedules(): ng.IPromise<IAccountMinimal> {
    return this.restangular.one(`${endpoint}/accounts-with-flight-schedules`).get();
  }
}
