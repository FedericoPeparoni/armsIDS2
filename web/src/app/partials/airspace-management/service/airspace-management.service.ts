// interfaces
import { IAirspace } from '../airspace-management.interface';

// service
import { CRUDService } from '../../../angular-ids-project/src/helpers/services/crud.service';

export let endpoint: string = 'airspaces';

export class AirspaceManagementService extends CRUDService {

  protected restangular: restangular.IService;

  private _mod: IAirspace = {
    airspace_name: null,
    airspace_type: null,
    airspace_full_name: null,
    airspace_included: null,
    airspace_ceiling: null,
    id: null
  };

  /** @ngInject */
  constructor(protected Restangular: restangular.IService) {
    super(Restangular, endpoint);
    this.model = angular.copy(this._mod);
  }

  protected getAirspacesFromNavDB(): ng.IPromise<any> { // returns airspaces from navdb
    return this.restangular.one(`${endpoint}/fromnavdb`).get();
  }

  public create(id: number): ng.IPromise<any> {
    return this.restangular.one(`${endpoint}/fromnavdb/${id}`).customPOST();
  }

}
