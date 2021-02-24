// services
import { CRUDService } from '../../../angular-ids-project/src/helpers/services/crud.service';
import { IType } from '../types.interface';

// endpoint
export let endpoint: string = '/account-types';

export class TypesService extends CRUDService {

  protected restangular: restangular.IService;

  private _mod: IType = {
    id: null,
    name: null
  };

  /** @ngInject */
  constructor(protected Restangular: restangular.IService) {
    super(Restangular, endpoint);
    this.model = angular.copy(this._mod);
  }

}
