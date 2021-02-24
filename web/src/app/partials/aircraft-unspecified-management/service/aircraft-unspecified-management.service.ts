// interface
import { IAircraftType } from '../aircraft-unspecified-management.interface';

// service
import { CRUDService } from '../../../angular-ids-project/src/helpers/services/crud.service';

export let endpoint: string = 'unspecified-aircraft-types';

export class AircraftUnspecifiedManagementService extends CRUDService {

  protected restangular: restangular.IService;

  private _mod: IAircraftType = {
    id: null,
    text_identifier: null,
    aircraft_type: null,
    name: null,
    status: null,
    mtow: null
  };

  /** @ngInject */
  constructor(protected Restangular: restangular.IService) {
    super(Restangular, endpoint);
    this.model = angular.copy(this._mod);
  }

}
