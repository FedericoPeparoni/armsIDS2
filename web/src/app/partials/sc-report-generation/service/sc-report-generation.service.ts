import { CRUDService } from '../../../angular-ids-project/src/helpers/services/crud.service';
import { ISCReportGeneration } from '../sc-report-generation.interface';

// endpoint
export let endpoint: string = 'sc-report-generation';

export class ScReportGenerationService extends CRUDService {

  protected restangular: restangular.IService;

  private _mod: ISCReportGeneration = {
    report: null,
    startDate: null,
    endDate: null,
    account_ids: null,
    group_by_account: false,
    account_status: null
  };

  /** @ngInject */
  constructor(protected Restangular: restangular.IService) {
    super(Restangular, endpoint);
    this.model = angular.copy(this._mod);
  }

  public getModel(): ISCReportGeneration {
    return angular.copy(this._mod);
  }

  public getPreview(params: object, body: Array<number>): ng.IPromise<any> {
    return this.restangular.one(`${endpoint}`).customPOST(body, null, params);
  }

}
