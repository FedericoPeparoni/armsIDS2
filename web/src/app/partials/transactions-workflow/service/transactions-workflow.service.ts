/**
 * Required Interfaces
 */
import { ITransactionWorkflow } from '../transactions-workflow.interface';

/**
 * Required Services
 */
import { CRUDService } from '../../../angular-ids-project/src/helpers/services/crud.service';

/**
 * Base API endpoint for this service
 */
export let endpoint: string = 'approval-workflow';

/**
 * Supports the Transaction Approval Workflow controller
 */
export class TransactionsWorkflowService extends CRUDService {

  protected restangular: restangular.IService;

  private _mod: ITransactionWorkflow = {
    id: null,
    level: null,
    delete: false,
    approval_name: null,
    approval_group: null,
    status_type: null,
    threshold_amount: null,
    threshold_currency: null,
    approval_under_level: null,
    approval_over_level: null,
    rejected_level: null,
    version: 0,
    approval_document_required: false
  };

  /** @ngInject */
  constructor(protected Restangular: restangular.IService, private $translate: angular.translate.ITranslateService) {
    super(Restangular, endpoint);
    this.model = angular.copy(this._mod);
  }

  /**
   * Return translation supported list of status_types (required by API)
   */
  public getStatusTypes(): Array<string> {
    return [
      'INITIAL',
      'INTERMEDIATE',
      'FINAL',
    ];
  }

  /**
   * Return a copy of the model with all fields set to their initial value (as defined in _mod above)
   */
  public getModel(): ITransactionWorkflow {
    return angular.copy(this._mod);
  }

  /**
   * Return a list of all existing workflow steps
   */
  public getAllWorkflowLevels(): ng.IPromise<any> {
    return this.list();
  }

  /**
   * Creates a new step when some workflow steps already exist (overrides create in CRUD service which uses PUT)
   */
  public createNew(obj: ITransactionWorkflow, id: number): ng.IPromise<any> {
    return this.restangular.all(`${this.endpoint}/${id}`).customPOST(obj);
  }

}
