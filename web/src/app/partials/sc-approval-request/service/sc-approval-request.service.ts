// interface
import { ISCApprovalRequest } from '../sc-approval-request.interface';

// service
import { CRUDService } from '../../../angular-ids-project/src/helpers/services/crud.service';

export let endpoint: string = 'self-care-approval-request';

export class ScApprovalRequestService extends CRUDService {

  protected restangular: restangular.IService;

  private _mod: ISCApprovalRequest = {
    id: null,
    account: null,
    user: null,
    request_type: null,
    request_dataset: null,
    object_id: null,
    request_text: null,
    status: null,
    responders_name: null,
    response_date: null,
    response_text: null,
    created_at: null
  };

  /** @ngInject */
  constructor(protected Restangular: restangular.IService) {
    super(Restangular, endpoint);
    this.model = angular.copy(this._mod);
  }

  public approve(item: ISCApprovalRequest, id: number): ng.IPromise<void> {
    return this.restangular.one(`${endpoint}/approve/${id}`).customPUT(item);
  }

  public approveSelected(itemsId: Array<number>, text: string): ng.IPromise<void> {
    return this.restangular.one(`${endpoint}/bulk-approve/${text}`).customPUT(itemsId);
  }

  public reject(item: ISCApprovalRequest, id: number): ng.IPromise<void> {
    return this.restangular.one(`${endpoint}/reject/${id}`).customPUT(item);
  }

  public rejectSelected(itemsId: Array<number>, text: string): ng.IPromise<void> {
    return this.restangular.one(`${endpoint}/bulk-reject/${text}`).customPUT(itemsId);
  }
}


