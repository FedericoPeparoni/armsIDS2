// contants
import { SysConfigConstants } from '../../system-configuration/system-configuration.constants';

// interfaces
import { IAccount, IAccountEventMap, IAccountMinimal } from '../accounts.interface';

// services
import { CRUDService } from '../../../angular-ids-project/src/helpers/services/crud.service';
import { SystemConfigurationService } from '../../system-configuration/service/system-configuration.service';

export let endpoint: string = 'accounts';

export class AccountsService extends CRUDService {

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
    is_self_care: false,
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
    aircraft_parking_exemption: null,
    account_type: null,
    list_of_events_account_notified: null,
    iata_member: false,
    separate_pax_invoice: false,
    external_accounting_system_identifier: null,
    active: true,
    cash_account: false,
    approved_flight_school_indicator: false,
    account_users: [],
    nationality: null,
    whitelist_last_activity_date_time: null,
    whitelist_state: null,
    whitelist_inactivity_notice_sent_flag: null,
    whitelist_expiry_notice_sent_flag: null
  };

  /** @ngInject */
  constructor(protected Restangular: restangular.IService, protected systemConfigurationService: SystemConfigurationService) {
    super(Restangular, endpoint);
    this.model = angular.copy(this._mod);
  }

  /**
   * Return only credit accounts
   */
  public listAll(): ng.IPromise<any> {
    return super.list({}, `cash=false&size=-1`);
  }

  /**
   * Return all accounts (cash and credit)
   */
  public listAllWithCash(): ng.IPromise<any> {
    return super.listAll();
  }

  /**
   * Return all accounts (cash and credit) if credit PoS workflow configured,
   * otherwise only return credit accounts.
   *
   * US 93566 - allow cash account transaction creation
   */
  public listAllCreditWorkflow(): ng.IPromise<Array<IAccountMinimal>> {
    let posWorkflow: string = this.systemConfigurationService
      .getValueByName(<any>SysConfigConstants.POINT_OF_SALE_WORKFLOW);
    if (posWorkflow === 'credit') {
      return this.findAllMinimalReturn(true);
    } else {
      return this.findAllCreditMinimalActiveReturn();
    }
  }

  /**
   * Return list of account IDs and names ONLY -- to be used for forms (includes cash accounts)
   */
  public findAllMinimalReturn(onlyActive?: boolean): ng.IPromise<Array<IAccountMinimal>> {
    const active = onlyActive ? '?active=true' : '';
    return this.restangular.one(`${endpoint}/allMinimal${active}`).get();
  }

  /**
   * Return list of active account IDs and names ONLY -- to be used for forms (includes cash accounts)
   */
  public findAllActiveMinimalReturn(): ng.IPromise<Array<IAccountMinimal>> {
    return this.findAllMinimalReturn(true);
  }

  /**
   * Return list of account IDs and names ONLY for cash accounts
   */
  public findAllCashMinimalReturn(onlyActive?: boolean): ng.IPromise<Array<IAccountMinimal>> {
    const active = onlyActive ? '?active=true' : '';
    return this.restangular.one(`${endpoint}/cashMinimal${active}`).get();
  }

  /**
   * Return list of account IDs and names ONLY for credit accounts
   */
  public findAllCreditMinimalReturn(onlyActive?: boolean): ng.IPromise<Array<IAccountMinimal>> {
    const active = onlyActive ? '?active=true' : '';
    return this.restangular.one(`${endpoint}/creditMinimal${active}`).get();
  }

  /**
   * Return list of active account IDs and names ONLY for credit accounts
   */
  public findAllCreditMinimalActiveReturn(): ng.IPromise<Array<IAccountMinimal>> {
    return this.findAllCreditMinimalReturn(true);
  }

  /**
   * Return list of account IDs and names ONLY for active credit aviation accounts (Airline, General Aviation or Charter)
   */
  public findAllActiveCreditMinimalAviationReturn(accountType?: number): ng.IPromise<Array<IAccountMinimal>> {
    return this.restangular.one(`${endpoint}/activeCreditMinimalAviation`).customGET('', { accountType: accountType });
  }

  /**
   * Return list of account IDS and names ONLY for accounts with flight
   * schedules.
   */
  public findSchedMinimalReturn(): ng.IPromise<Array<IAccountMinimal>> {
    return this.restangular.one(`${endpoint}/schedMinimal`).get();
  }

  /**
   * Return list of customer notifications
   */
  public getCustomerNotifications(): ng.IPromise<any> {
    return this.restangular.one(`${endpoint}/getCustomerNotifications`).get();
  }

  public update(account: IAccount, id: number): ng.IPromise<void> {
    return super.update(account, id)
      .then(() => this.updateAccountCustomerEvent(id, account.list_of_events_account_notified));
  }

  public create(account: IAccount): ng.IPromise<void> {
    const events = account.list_of_events_account_notified;
    return super.create(account)
      .then((newAccount: IAccount) => this.updateAccountCustomerEvent(newAccount.id, events));
  }

  private updateAccountCustomerEvent(accountId: number, events: Array<IAccountEventMap>): ng.IPromise<void> {
    return this.restangular.one(`${endpoint}/${accountId}/account_events`).customPUT(events || []);
  }
}
