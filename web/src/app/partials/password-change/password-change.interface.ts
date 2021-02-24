export interface IPasswordChange {
  old_password: string;
  new_password: string;
}

export interface IPasswordChangeScope extends ng.IScope {
  updatePassword: Function;
  notificationText: string;
  forcePasswordChange: boolean;
  editable: IPasswordChange;
  email: string;
  login: string;
  error: any;
  passwordChangedMessage: boolean;
}
