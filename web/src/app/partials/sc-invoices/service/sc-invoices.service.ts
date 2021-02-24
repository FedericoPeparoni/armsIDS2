// interfaces
import { IInvoice } from '../../invoices/invoices.interface';

// services
import { CRUDFileUploadService } from '../../../angular-ids-project/src/helpers/services/crud-file-handler.service';

// endpoint
export let endpoint: string = 'sc-billing-ledgers';

export class ScInvoicesService extends CRUDFileUploadService {

  protected restangular: restangular.IService;

  private _mod: IInvoice = {
    id: null,
    account: null,
    invoice_period_or_date: null,
    invoice_type: null,
    invoice_state_type: null,
    payment_due_date: null,
    user: null,
    invoice_document: null,
    invoice_number: null,
    invoice_amount: null,
    invoice_currency: null,
    invoice_exchange_to_usd: null,
    invoice_date_of_issue: null,
    payment_amount: null,
    payment_currency: null,
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
}

