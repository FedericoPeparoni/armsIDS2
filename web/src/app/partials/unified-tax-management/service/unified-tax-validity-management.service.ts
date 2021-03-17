
// interfaces
import {IUnifiedTaxManagement, IValidity} from '../unified-tax-managment.interface';

// services
import { CRUDService } from '../../../angular-ids-project/src/helpers/services/crud.service';

export let endpoint: string = 'unified-tax-validities'

export class UnifiedTaxValidityManagementService extends CRUDService {

  protected restangular: restangular.IService;

  private _mod: IValidity = {
    id: null,
    from_validity_year: null,
    to_validity_year: null,
  };

 /** @ngInject */
 constructor(protected Restangular: restangular.IService) {
  super(Restangular, endpoint);
  this.model = angular.copy(this._mod);
}

  /**
   * Get list of all unified TAx.
   */
  getList(): ng.IPromise<Array<IValidity>> {
    console.log(this.restangular.all(`${endpoint}/list`).getList());
    return this.restangular.all(`${endpoint}/list`).getList();

  }





}
