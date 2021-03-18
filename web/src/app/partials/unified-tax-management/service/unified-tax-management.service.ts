
// interfaces
import { IUnifiedTaxManagement } from '../unified-tax-managment.interface';

// services
import { CRUDService, ISpringPageableParams } from '../../../angular-ids-project/src/helpers/services/crud.service';


export let endpoint: string = 'unified-taxes';

export class UnifiedTaxManagementService extends CRUDService {

  protected restangular: restangular.IService;

  private _mod: IUnifiedTaxManagement = {
    id: null,
    from_manufacture_year: null,
    to_manufacture_year: null,
    rate: null,
    validity: null,
    requireExternalSystemId:null
  };

 /** @ngInject */
 constructor(protected Restangular: restangular.IService) {
  super(Restangular, endpoint);
  this.model = angular.copy(this._mod);
}

  /**
   * Get list of all unified TAx.
   */
  getList(): ng.IPromise<Array<IUnifiedTaxManagement>> {
    console.log(this.restangular.all(`${endpoint}/list`).getList());
    return this.restangular.all(`${endpoint}/list`).getList();
  }

  public getListByValidityId(validityId: number): ng.IPromise<any> {
    
    return this.restangular.all(`${endpoint}/validity/${validityId}/list`).getList();
  }





}
