// interface
import { IAircraftType, IAircraftTypeMinimal } from '../aircraft-type-management.interface';

// service
import { CRUDService } from '../../../angular-ids-project/src/helpers/services/crud.service';

export let endpoint: string = 'aircraft-types';

export class AircraftTypeManagementService extends CRUDService {

  protected restangular: restangular.IService;

  private _mod: IAircraftType = {
    id: null,
    aircraft_type: null,
    aircraft_image: null,
    aircraft_name: null,
    manufacturer: null,
    wake_turbulence_category: null,
    maximum_takeoff_weight: null
  };

  /** @ngInject */
  constructor(protected Restangular: restangular.IService) {
    super(Restangular, endpoint);
    this.model = angular.copy(this._mod);
  }

  /**
   * Return list of account IDs and names ONLY -- to be used for forms (includes cash accounts)
   */
  public findAllMinimalReturn(): ng.IPromise<Array<IAircraftTypeMinimal>> {
    return this.restangular.one(`${endpoint}/allMinimal`).get();
  }

}
