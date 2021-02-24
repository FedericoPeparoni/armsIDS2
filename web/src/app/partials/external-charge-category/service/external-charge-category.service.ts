import { CRUDService } from '../../../angular-ids-project/src/helpers/services/crud.service';
import { IExternalChargeCategory } from '../external-charge-category.interface';

export let endpoint = '/external-charge-categories';

export class ExternalChargeCategoryService extends CRUDService {

  protected restangular: restangular.IService;

  private _mod: Array<IExternalChargeCategory> = [{
    id: null,
    name: null,
    unique: null
  }];

  /** @ngInject */
  constructor(protected Restangular: restangular.IService) {
    super(Restangular, endpoint);
    this.model = angular.copy(this._mod);
  }


  /**
   * Return list of non-aviation charge categories
   */
  public getNonAviationCategories(): ng.IPromise<any> {
    return this.restangular.one(`${endpoint}/non-aviation`).get();
  }
}
