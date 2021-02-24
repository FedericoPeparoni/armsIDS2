import { ISystemConfiguration } from '../../../../partials/system-configuration/system-configuration.interface';

export interface IPasswordScope extends ng.IScope {
  checkPasswords: Function;
  password: string;
  password1: string;
  password2: string;
  modelCtrl: {
    $setValidity: Function;
    $setUntouched: Function;
    password: ng.INgModelController;
    passwordConfirm: ng.INgModelController;
  };
  required: boolean;
  configurations: Array<ISystemConfiguration>;
  minLength: number;
}

export interface IPasswordValidation {
  required: boolean;
  minlength: boolean;
  confirm: boolean;
  lowercase: boolean;
  uppercase: boolean;
  numeric: boolean;
  special: boolean;
}
