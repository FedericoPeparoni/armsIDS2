// services
import { CRUDService } from '../../../angular-ids-project/src/helpers/services/crud.service';

// endpoint
export let endpoint: string = 'invoice-state-types';

export class InvoiceStateTypeService extends CRUDService {

  protected restangular: restangular.IService;

  private _mod: Array<string> = [];

  /** @ngInject */
  constructor(protected Restangular: restangular.IService) {
    super(Restangular, endpoint);
    this.model = angular.copy(this._mod);
  }

}
