// interface
import { IScQuerySubmission } from '../sc-query-submission.interface';

// service
import { CRUDService } from '../../../angular-ids-project/src/helpers/services/crud.service';

export let endpoint: string = 'query-submission';

export class ScQuerySubmissionService extends CRUDService {

  protected restangular: restangular.IService;

  private _mod: IScQuerySubmission = {
    sender_email: null,
    subject: null,
    message: null
  };

  /** @ngInject */
  constructor(protected Restangular: restangular.IService) {
    super(Restangular, endpoint);
    this.model = angular.copy(this._mod);
  }

  public getModel(): IScQuerySubmission {
    return angular.copy(this._mod);
  }

  public send(editable: IScQuerySubmission): ng.IPromise<any> {
    return this.restangular.all(this.endpoint).customPOST(editable);
  }
}
