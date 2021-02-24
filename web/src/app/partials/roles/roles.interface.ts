import { IPermissions } from '../permissions/permissions.interface';

export interface IRole {
  id?: number;
  name: string;
  permissions: Array<IPermissions>;
  notification_mechanism: string;
  owned_roles: Array<IRole>;
  max_credit_note_amount_approval_limit: number;
  max_debit_note_amount_approval_limit: number;
}

export interface IRoleData extends IRole {
  content: Array<IRole>;
}
