
// interfaces
import { ITuRateManagement } from '../tu-rate-management.interface';

// services
import { CRUDService } from '../../../angular-ids-project/src/helpers/services/crud.service';


export let endpoint: string = 'tu-rate';


export class TuRateManagementService extends CRUDService {

  protected restangular: restangular.IService;

  private _mod: ITuRateManagement = {
    id: null,
    fromManufactureYear: null,
    toManufactureYear: null,
    ars: 0,
    fromValidityYear: null,
    toValidityYear: null

  };

 /** @ngInject */
 constructor(protected Restangular: restangular.IService) {
  super(Restangular, endpoint);
  this.model = angular.copy(this._mod);
}

  /**
   * Get list of all unified TAx.
   */
  getList(): ng.IPromise<Array<ITuRateManagement>> {
    console.log(this.restangular.all(`${endpoint}/list`).getList());
    return this.restangular.all(`${endpoint}/list`).getList();

  }



}
