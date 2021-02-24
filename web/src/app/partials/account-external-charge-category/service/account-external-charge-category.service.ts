// interfaces
import { IAccountExternalChargeCategory } from '../account-external-charge-category.interface';

// services
import { CRUDService } from '../../../angular-ids-project/src/helpers/services/crud.service';

// endpoint
export let endpoint: string = 'accounts-external-charge-categories';

export class AccountExternalChargeCategoryService extends CRUDService {

  protected restangular: restangular.IService;

  private _mod: IAccountExternalChargeCategory = {
    id: null,
    account: null,
    external_charge_category: null,
    external_system_identifier: null
  };

  /** @ngInject */
  constructor(protected Restangular: restangular.IService) {
    super(Restangular, endpoint);
    this.model = angular.copy(this._mod);
  }

  public get modelOverride(): IAccountExternalChargeCategory { return angular.copy(this._mod); };

  public getByAccountId(accountId: number, externalChargeCategoryId?: number): ng.IPromise<any> {
    let params: object = {
      externalChargeCategoryId: externalChargeCategoryId
    };
    return this.restangular.one(`${endpoint}/for-account/${accountId}`).get(params);
  }
}
