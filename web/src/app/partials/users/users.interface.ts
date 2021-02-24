// interfaces
import { ICRUDScope } from '../../angular-ids-project/src/helpers/services/crud.interface';
import { IRole } from '../roles/roles.interface';
import { IBillingCentre } from '../billing-centre-management/billing-centre-management.interface';

export interface IUser {
  email: string;
  id: number;
  login: string;
  name: string;
  permissions: Array<string>;
  roles: Array<IRole>;
  contact_information: string;
  sms_number: string;
  billing_center: IBillingCentre;
  job_title: string;
  language: string;
  is_selfcare_user: boolean;
  force_password_change: boolean;
  registration_status: boolean;
}

export interface IUserSpring {
  content: Array<IUser>;
}

export interface IUserLight {
  id: number;
  name: string;
}

export interface IUserScope extends ICRUDScope {
  editable: {
    is_selfcare_user: boolean;
    password: string;
    billing_center: IBillingCentre;
    roles: Array<IRole>;
  };
  minLength: number;
  rolesList: Array<IRole>;
  setZeroLength: boolean;
  resetPassword(setZeroLength?: boolean): void;
}
