// interfaces
import { ITransaction } from '../../transactions/transactions.interface';

// service
import { CRUDService } from '../../../angular-ids-project/src/helpers/services/crud.service';

export let endpoint: string = 'sc-transactions';

export class ScTransactionsService extends CRUDService {

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
      charges_adjustment: [],
      kra_receipt_number: null,
      kra_clerk_name: null,
      payments_exported: null,
      has_approval_document: null,
      approval_document_name: null,
      approval_document_type: null,
      has_supporting_document: null,
      supporting_document_name: null,
      supporting_document_type: null,
      interest_invoice_error: null,
      transaction_approvals: null
  };

  /** @ngInject */
  constructor(protected Restangular: restangular.IService) {
    super(Restangular, endpoint);
    this.model = angular.copy(this._mod);
  }

  public getModel(): ITransaction {
    return this._mod;
  }

  getInvoicesByTransactionId(transactionId: number): ng.IPromise<any> {
    return this.restangular.one(`${endpoint}/getBillingLedgersByTransactionId/${transactionId}`).get();
  }
}

