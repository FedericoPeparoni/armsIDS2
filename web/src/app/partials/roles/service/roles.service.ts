// interfaces
import { IRole, IRoleData } from '../roles.interface';
import { IPermissions } from '../../permissions/permissions.interface';

// services
import { CRUDService } from '../../../angular-ids-project/src/helpers/services/crud.service';

export let endpoint = '/roles';

export class RolesService extends CRUDService {

  protected restangular: restangular.IService;

  private _mod: IRole = {
    id: null,
    name: null,
    permissions: [],
    owned_roles: [],
    max_credit_note_amount_approval_limit: null,
    max_debit_note_amount_approval_limit: null,
    notification_mechanism: null
  };

  /** @ngInject */
  constructor(protected Restangular: restangular.IService) {
    super(Restangular, endpoint);
    this.model = angular.copy(this._mod);
  }

  /**
   * Return all roles without 'Change the password'
   */
  public listAllWithoutChangePassword(): ng.IPromise<any> {
    return super.listAll(<any>{ 'ignoreChangePassword': true })
      .then((data: IRoleData) => data.content);
  }

  public updatedPermissionsOfRole(roleID: number, listOfPermissions: Array<IPermissions>): ng.IPromise<IRole> {
    return this.restangular.one(`${endpoint}/${roleID}/permissions`).customPUT(listOfPermissions);
  }

  public updatedOwnedRolesOfRole(roleID: number, listOfOwnedRoles: Array<IRole>): ng.IPromise<IRole> {
    return this.restangular.one(`${endpoint}/${roleID}/owned-roles`).customPUT(listOfOwnedRoles);
  }
}
