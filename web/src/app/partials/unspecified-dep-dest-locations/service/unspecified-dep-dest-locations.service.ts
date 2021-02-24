// interface
import { IUnspecifiedDepartureLocationType } from '../unspecified-dep-dest-locations.interface';

// service
import { CRUDService } from '../../../angular-ids-project/src/helpers/services/crud.service';

export let endpoint: string = 'unspecified-departure-destination-locations';

/* @ngInject */
export class UnspecifiedDepDestLocationsService extends CRUDService {

  private _mod: IUnspecifiedDepartureLocationType = {
    id: null,
    text_identifier: null,
    name: null,
    maintained: null,
    aerodrome_identifier: null,
    latitude: null,
    longitude: null,
    status: null
  };

  constructor(protected Restangular: restangular.IService, private $q: ng.IQService) {
    super(Restangular, endpoint);
    this.model = angular.copy(this._mod);
  }
}
