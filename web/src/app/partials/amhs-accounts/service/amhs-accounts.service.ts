// interface
import { IAMHSAccount } from '../amhs-accounts.interface';

// service
import { CRUDService } from '../../../angular-ids-project/src/helpers/services/crud.service';

export let endpoint: string = 'amhs-accounts';

export class AMHSAccountsService extends CRUDService {

  protected restangular: restangular.IService;

  private _mod: IAMHSAccount = {
    id: null,
    active: null,
    addr: null,
    descr: null,
    passwd: null,
    allow_mta_conn: null,
    svc_hold_for_delivery: null
  };

  /** @ngInject */
  constructor(protected Restangular: restangular.IService) {
    super(Restangular, endpoint);
    this.model = angular.copy(this._mod);
  }
}
