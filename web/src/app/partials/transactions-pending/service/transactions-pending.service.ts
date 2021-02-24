/**
 * Required Interfaces
 */
import { ITransactionPending } from '../transactions-pending.interface';

/**
 * Required Services
 */
import { CRUDFileUploadService } from '../../../angular-ids-project/src/helpers/services/crud-file-handler.service';

/**
 * Base API endpoint for this service
 */
export let endpoint: string = 'pending-transactions';

/**
 * Supports the Pending Transactions controller
 */
export class TransactionsPendingService extends CRUDFileUploadService {

  protected restangular: restangular.IService;

  private _mod: ITransactionPending = {
    account: null,
    can_approve: null,
    can_reject: null,
    current_approval_level: null,
    description: null,
    detailed_invoices: null,
    exchange_rate_to_ansp: null,
    exchange_rate_to_usd: null,
    exported: null,
    local_amount: null,
    local_currency: null,
    payment_amount: null,
    payment_currency: null,
    payment_exchange_rate: null,
    payment_mechanism: null,
    payment_reference_number: null,
    pending_charge_adjustments: null,
    previous_approval_level: null,
    related_invoices: null,
    transaction_date_time: null,
    transaction_type: null,
    version: null,
    document: null,
    document_filename: null,
    has_approval_document: null,
    approval_document_name: null,
    approval_document_type: null,
    has_supporting_document: null,
    supporting_document_name: null,
    supporting_document_type: null,
    pending_transaction_approvals: null
  };

  /** @ngInject */
  constructor(protected Restangular: restangular.IService) {
    super(Restangular, endpoint);
    this.model = angular.copy(this._mod);
  }

  /**
   * Get details of selected pending transaction
   * @param {number} id - id of the pending transaction
   */
  public getOne(id: number): ng.IPromise<any> {
    return this.get(id);
  }

  /**
   * Approve pending transaction, backend determines next steps
   * @param {number} id - id of the pending transaction
   */
  public approve(id: number, fd?: FormData, notes?: string): ng.IPromise<any> {
    let path = notes ? `${this.endpoint}/approve/${id}/${notes}` : `${this.endpoint}/approve/${id}`;
    const req = this.restangular.one(path);

    if (fd) {
      return req.withHttpConfig({ 'transformRequest': angular.identity }).customPUT(fd, undefined, undefined, { 'Content-Type': undefined });
    } else {
      return req.customPUT();
    }
  }

  /**
   * Reject pending transaction, backend determines next steps
   * @param {number} id - id of the pending transaction
   */
  public reject(id: number, notes?: string): ng.IPromise<any> {
    return this.restangular.one(`${this.endpoint}/${id}/reject`).customPUT(notes).then((resp: any) => {
      return resp;
    });
  }

  public showWarning(): any {
    const warning: any = {
      data: {
        error: `Notes is required`,
        error_description: `Notes is required for rejecting a pending transaction`
      }
    };
    return { error: warning };
  }

}
