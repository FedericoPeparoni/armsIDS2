// interfaces
import { ITransaction, ITransactionExportSupport } from '../transactions.interface';

// service
import { CRUDFileUploadService } from '../../../angular-ids-project/src/helpers/services/crud-file-handler.service';

export let endpoint: string = 'transactions';

export class TransactionsService extends CRUDFileUploadService {

  protected restangular: restangular.IService;

  private _mod: ITransaction = {
    id: null,
    account: null,
    transaction_date_time: null,
    description: null,
    transaction_type: null,
    amount: null,
    currency: null,
    payment_mechanism: null,
    payment_reference_number: null,
    exported: false,
    billing_ledger_ids: null,
    exchange_rate_to_usd: null,
    exchange_rate_to_ansp: null,
    balance: null,
    payment_amount: null,
    payment_currency: null,
    payment_exchange_rate: null,
    charges_adjustment: null,
    kra_receipt_number: null,
    kra_clerk_name: null,
    payments_exported: null,
    has_approval_document: null,
    approval_document_name: null,
    approval_document_type: null,
    has_supporting_document: null,
    supporting_document_name: null,
    supporting_document_type: null,
    payment_date: null,
    bank_account_name: null,
    bank_account_number: null,
    bank_account_external_accounting_system_id: null,
    interest_invoice_error: null,
    transaction_approvals: null
  };

  /** @ngInject */
  constructor(protected Restangular: restangular.IService) {
    super(Restangular, endpoint);
    this.model = angular.copy(this._mod);
  }

  getPaymentMechanismList(): ng.IPromise<any> {
    return this.restangular.one(`${endpoint}/getPaymentMechanismList`).get();
  }

  getCurrencyListByAccountId(id: number): ng.IPromise<any> {
    return this.restangular.one(`${endpoint}/getCurrencyListByAccountId/${id}`).get();
  }

  validateSelectedInvoice(transaction: ITransaction): ng.IPromise<any> {
    return this.restangular.all(`${endpoint}/calculateAmountForTransactionPayments`).post(transaction);
  }

  getTransactionByInvoiceId(invoiceId: number): ng.IPromise<ITransaction[]> {
    return this.restangular.one(`${endpoint}/getTransactionByBillingLedgerId/${invoiceId}`).get();
  }

  getTransactionPaymentsByInvoiceId(invoiceId: number): ng.IPromise<ITransaction[]> {
    return this.restangular.one(`${endpoint}/getTransactionPaymentsByBillingLedgerId/${invoiceId}`).get();
  }

  getInvoicesByTransactionId(transactionId: number): ng.IPromise<any> {
    return this.restangular.one(`${endpoint}/getBillingLedgersByTransactionId/${transactionId}`).get();
  }

  getTransactionPaymentsByTransactionId(transactionId: number): ng.IPromise<any> {
    return this.restangular.one(`${endpoint}/getTransactionPaymentsByTransactionId/${transactionId}`).get();
  }

  public getModel(): ITransaction {
    return this._mod;
  }

  public exportSupport(): ng.IPromise<ITransactionExportSupport> {
    return this.restangular.one(`${endpoint}/export-support`).get();
  }

  public exportSupportMechanism(mechanism: string): ng.IPromise<boolean> {
    return this.restangular.one(`${endpoint}/export-support/${mechanism}`).get();
  }

  public exportAllTransactions(): ng.IPromise<boolean> {
    return this.restangular.all(`${endpoint}/export-all`).post({});
  }

  public exportSelectedTransactions(ids: Array<number>): ng.IPromise<boolean> {
    return this.restangular.all(`${endpoint}/export`).post(ids);
  }
}
