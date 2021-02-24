import { IPermissions } from '../permissions/permissions.interface';
import { IRole } from '../roles/roles.interface';

export interface IGroupManagementScope extends ng.IScope {
  editable: IRole;
  list: Array<IRole>;
  rolesMayModify: Array<IRole>;
  permissionsList: Array<IPermissions>;
  create: Function;
  edit: Function;
  update: Function;
  delete: Function;
  reset: Function;
}
