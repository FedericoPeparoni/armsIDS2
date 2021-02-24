// interfaces
import { IBankAccount } from '../bank-account-management.interface';

// services
import { CRUDService } from '../../../angular-ids-project/src/helpers/services/crud.service';

export let endpoint: string = 'bank-accounts';

export class BankAccountManagementService extends CRUDService {

  protected restangular: restangular.IService;

  private _mod: IBankAccount = {
    id: null,
    name: null,
    number: null,
    version: 0,
    currency: null,
    external_accounting_system_id: null
  };

  /** @ngInject */
  constructor(protected Restangular: restangular.IService) {
    super(Restangular, endpoint);
    this.model = angular.copy(this._mod);
  }

  /**
   * Get list of all bank accounts.
   */
  getList(): ng.IPromise<Array<IBankAccount>> {
    return this.restangular.all(`${endpoint}/list`).getList();
  }

  /**
   * Get bank account support.
   */
  getIsSupported(): ng.IPromise<boolean> {
    return this.restangular.one(`${endpoint}/is-supported`).get();
  }

  /**
   * Format bank account as label, `name (number)`.
   * 
   * @param bankAccount bank acount into to use
   */
  getLabel(name: string, number: string): string {
    return name && number
      ? `${name} (${number})`
      : name || number || '';
  }
}
