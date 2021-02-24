export interface IScLoginScope extends ng.IScope {
  warning: boolean;
  error: number;
  timeoutDelay: number;
  submit(user: string, password: string): void;
  passwordRecoveryForm: boolean;
}
