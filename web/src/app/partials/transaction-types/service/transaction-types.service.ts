import { CRUDService } from '../../../angular-ids-project/src/helpers/services/crud.service';
import { ITransactionType } from '../transaction-types.interface';

export let endpoint = '/transaction-types';

export class TransactionTypesService extends CRUDService {

  protected restangular: restangular.IService;

  private _mod: Array<ITransactionType> = [{
    id: null,
    name: null
  }];

  /** @ngInject */
  constructor(protected Restangular: restangular.IService) {
    super(Restangular, endpoint);
    this.model = angular.copy(this._mod);
  }

}
