import { IPasswordChange } from '../password-change/password-change.interface'

export interface IUserInfoChange extends IPasswordChange {
    email: string;
    sms_number: string;
}

export interface IUserInfoChangeScope extends ng.IScope {
    updatePassword: Function;
    notificationText: string;
    editable: IUserInfoChange;
    login: string;
    error: any;
    passwordChangedMessage: boolean;
  }