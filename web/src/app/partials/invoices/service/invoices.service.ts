// interfaces
import { IInvoice } from '../invoices.interface';

// services
import { CRUDFileUploadService } from '../../../angular-ids-project/src/helpers/services/crud-file-handler.service';

// endpoint
// note: formerly known as billing-ledgers
export let endpoint: string = 'billing-ledgers';

export class InvoicesService extends CRUDFileUploadService {

  protected restangular: restangular.IService;

  private _mod: IInvoice = {
    id: null,
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
    invoice_period_or_date: null,
    invoice_type: null,
    invoice_state_type: null,
    payment_due_date: null,
    user: {
      email: null,
      id: null,
      login: null,
      job_title: null,
      name: null,
      permissions: [],
      roles: [],
      contact_information: null,
      sms_number: null,
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
      language: null,
      is_selfcare_user: null,
      force_password_change: null,
      registration_status: null
    },
    invoice_document: null,
    invoice_number: null,
    invoice_amount: null,
    invoice_currency: {
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
    invoice_exchange_to_usd: null,
    invoice_date_of_issue: null,
    payment_amount: null,
    payment_currency: {
      id: null,
      currency_code: null,
      currency_name: null,
      country_code: {
        id: 1,
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
    payment_exchange_to_usd: null,
    payment_date: null,
    exported: null,
    amount_owing: null,
    proforma: null,
    point_of_sale: null
  };
  /** @ngInject */
  constructor(protected Restangular: restangular.IService, $http: ng.IHttpService) {
    super(Restangular, endpoint);
    this.model = angular.copy(this._mod);
  }

  public getModel(): IInvoice {
    return this._mod;
  }

  public insertInvoicesTransaction(invoice: IInvoice): ng.IPromise<any> {
    return this.restangular.all(`${endpoint}`).post(invoice);
  }

  public updateInvoiceStatus(id: number, invoiceStatus: string): ng.IPromise<IInvoice> {
    return this.restangular.one(`${endpoint}/${id}/${invoiceStatus}`).put();
  }

  public voidPublishedInvoice(id: number): ng.IPromise<IInvoice> {
    return this.restangular.one(`${endpoint}/${id}/void-published`).put();
  }

  public getUnpaidInvoices(accountId: number, currencyId: number, page?: number, sort?: string): ng.IPromise<any> {
    const pageSort = sort ? `&${sort}` : '';
    const pageNum = page ? page - 1 : 0;
    return this.restangular.one(`${endpoint}/getUnpaidBillingLedgersByAccountAndCurrency?accountId=${accountId}&currencyId=${currencyId}&page=${pageNum}${pageSort}`).get()
      .then((response: any) => {
        response.number++;
        return response;
      });
  }

  public getAllInvoicesByAccountIdAndCurrency(accountId: number, currencyId: number, page?: number, sort?: string): ng.IPromise<any> {
    const pageSort = sort ? `&${sort}` : '';
    const pageNum = page ? page - 1 : 0;
    return this.restangular.one(`${endpoint}/getAllBillingLedgersByAccountAndCurrency?accountId=${accountId}&currencyId=${currencyId}&page=${pageNum}${pageSort}`).get().
      then((response: any) => {
        response.number++;
        return response;
      });
  }

  public getTotalAmountForInvoices(accountId: number, currencyId: number): ng.IPromise<any> {
    return this.restangular.one(`${endpoint}/getTotalAmountForInvoicesByAccountIdAndCurrency?accountId=${accountId}&currencyId=${currencyId}`).get();
  }

  public getLineItemsByInvoiceId(invoiceId: number): ng.IPromise<any> {
    return this.restangular.one(`${endpoint}/getLineItemsByInvoiceId/${invoiceId}`).get();
  }

  public exportSupport(): ng.IPromise<boolean> {
    return this.restangular.one(`${endpoint}/export-support`).get();
  }

  public exportSupportType(type: string): ng.IPromise<boolean> {
    return this.restangular.one(`${endpoint}/export-support/${type}`).get();
  }

  public exportAllInvoices(): ng.IPromise<any> {
    return this.restangular.all(`${endpoint}/export-all`).post({});
  }

  public exportSelectedInvoices(ids: Array<number>): ng.IPromise<any> {
    return this.restangular.all(`${endpoint}/export`).post(ids);
  }
}
