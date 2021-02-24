// controllers
import { CRUDFormControllerUserService } from '../../angular-ids-project/src/helpers/controllers/crud-form-user-service/crud-form-user-service.controller';

// interface
import { IGroupManagementScope } from './group-management.interface';
import { IPermissions } from '../permissions/permissions.interface';
import { IRole } from '../roles/roles.interface';

// service
import { PermissionsService } from '../permissions/service/permissions.service';
import { RolesService } from '../roles/service/roles.service';

export class GroupManagementController extends CRUDFormControllerUserService {
  /* @ngInject */
  constructor(protected $scope: IGroupManagementScope, private permissionsService: PermissionsService, private rolesService: RolesService) {
    super($scope, rolesService);
    super.setup({ refresh: false });

    // load groups without change password group
    super.list({ 'ignoreChangePassword': true });

    // load multipe select options
    this.updateRolesMayModifyList();
    permissionsService.listAll().then((listOfPermissions: Array<IPermissions>) => {
      $scope.selfCareOperatorPermissionsList = listOfPermissions.filter((permission: IPermissions) => permission.name === 'self_care_access');
      listOfPermissions = listOfPermissions.filter((permission: IPermissions) => permission.name !== 'self_care_access' && permission.name !== 'change_password');
      $scope.permissionsList = this.sortListOfPermissions(listOfPermissions);
    });

    // expose necessary methods to scope
    $scope.update = (role: IRole, id: number) => this.updateOverride(role, id); // override update method
    $scope.create = (role: IRole) => this.createOverride(role); // override create method
    $scope.delete = (id: number) => this.deleteOverride(id); // override create method
    $scope.refresh = () => this.refreshOverride();
    this.getFilterParameters();
  }

  private updateRolesMayModifyList(): void {
    this.rolesService.listAllWithoutChangePassword().then((roles: Array<IRole>) => {
      this.$scope.rolesMayModify = roles
        ? roles.filter((item: IRole) => item.name !== 'Administrator')
        : [];
     });
  }

  private sortListOfPermissions(listOfPermissions: Array<IPermissions>): Array<IPermissions> {
    listOfPermissions.sort((a: IPermissions, b: IPermissions) => {
      if (a.name < b.name) { return -1; }
      if (a.name > b.name) { return 1; }
      return 0;
    });
    return listOfPermissions;
  }

  private createOverride(role: IRole): void {
    super.create(role)
      .then((newRole: IRole) => this.updateRolesAndPermissions(newRole.id, role.permissions, role.owned_roles).then(() => {
        this.refreshOverride();
        this.updateRolesMayModifyList();
      }));
  }

  private updateOverride(role: IRole, id: number): void {
    super.update(role, id) // update role
      .then(() => this.updateRolesAndPermissions(id, role.permissions, role.owned_roles).then(() => {
        this.refreshOverride();
        this.updateRolesMayModifyList();
      }));
  }

  private deleteOverride(id: number): void {
    super.delete(id).then(() => {
      this.refreshOverride();
      this.updateRolesMayModifyList();
    });
  }

  // updates the permissions and owned roles for a single group (or role id)
  private updateRolesAndPermissions(roleID: number, permissions: Array<IPermissions>, ownedRoles: Array<IRole>): ng.IPromise<IRole> {
    return this.rolesService.updatedPermissionsOfRole(roleID, permissions).then(() => this.rolesService.updatedOwnedRolesOfRole(roleID, ownedRoles));
  }

  private getFilterParameters(): void {
    this.$scope.filterParameters = {
      search: this.$scope.textFilter,
      page: this.$scope.pagination ? this.$scope.pagination.number : 0,
      ignoreChangePassword: true
    };
  }

  private refreshOverride(): void {
    this.getFilterParameters();
    super.list(this.$scope.filterParameters, this.$scope.getSortQueryString());
  }
}
