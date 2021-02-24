// interfaces
import { ILocalAircraftRegistry } from '../local-aircraft-registry.interface';

// services
import { CRUDFileUploadService } from '../../../angular-ids-project/src/helpers/services/crud-file-handler.service';

export const endpoint: string = 'local-aircraft-registries';

export class LocalAircraftRegistryService extends CRUDFileUploadService {

  protected restangular: restangular.IService;

  private _mod: ILocalAircraftRegistry = {
    id: null,
    registration_number: null,
    owner_name: null,
    analysis_type: null,
    mtow_weight: null,
    coa_date_of_renewal: null,
    coa_date_of_expiry: null
  };

  /** @ngInject */
  constructor(protected Restangular: restangular.IService) {
    super(Restangular, endpoint);
    this.model = angular.copy(this._mod);
  }
}
