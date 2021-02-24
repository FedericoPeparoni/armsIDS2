// interface
import { ILoginScope } from './login.interface';
import { IRestangularResponse } from '../angular-ids-project/src/helpers/interfaces/restangularError.interface';

// service
import { OAuthService } from '../components/services/oauth/oauth.service';
import { LoginService } from './service/login.service';
import { LocaleSwitcherService } from '../angular-ids-project/src/helpers/services/localeSwitcher.service';
import { UsersService } from '../partials/users/service/users.service';
import { LocalStorageService } from '../angular-ids-project/src/components/services/localStorage/localStorage.service';

// constants
import { SysConfigConstants } from '../partials/system-configuration/system-configuration.constants';
import { LOGIN_ERROR } from './login.constants';

export class LoginController {

  private failedAttempts: number = 0;
  private interval: ng.IPromise<any>;

  /* @ngInject */
  constructor(
    private $rootScope: ng.IRootScopeService,
    private $scope: ILoginScope,
    private $state: angular.ui.IStateService,
    private $interval: ng.IIntervalService,
    private loginService: LoginService,
    private localeSwitcherService: LocaleSwitcherService,
    private usersService: UsersService,
    private oAuthService: OAuthService
  ) {

    // redirect to welcome page if the user is already logged in
    this.loginService.redirectIfLoggedIn();

    // log out a self care user from billing and vice-versa
    // and redirect if accessing pages on incorrect url
    this.oAuthService.validate();

    $scope.language = this.$rootScope.language;

    this.interval = null;

    $scope.timeoutDelay = 0;
    $scope.warning = false;
    $scope.submit = (username: string, password: string) => this.submit(username, password);
    $scope.changeLanguage = (lang: any) => this.localeSwitcherService.changeLanguage(lang);

    $scope.offlineServerError = $scope.language && $scope.language.selected && $scope.language.selected.code
      ? LOGIN_ERROR.SERVER[$scope.language.selected.code]
      : LOGIN_ERROR.SERVER.en;

    $scope.passwordRecoveryForm = () => {
      $scope.recoveryForm = true;
      $scope.error = null;
    };
    $scope.closeRecoveryForm = () => {
      $scope.recoveryForm = false;
      $scope.error = null;
    };

    $scope.showLogin = () => {
      $scope.recoveryForm = false;
      $scope.tempPaswordSent = false;
      $scope.error = null;
    };

    $scope.setFrontendLanguage = (lang: any) => localeSwitcherService.setFrontendLanguage(lang);
    $scope.updateBackendLanguage = (lang: any) => localeSwitcherService.updateBackendLanguage(lang);

    $scope.recoverPassword = (email: string) => this.usersService.recoverPassword(email).then(() => {
      $scope.tempPaswordSent = true;
    },
    (error: IRestangularResponse) => {
      $scope.error = { error: error.data };
      $scope.errorDescription = $scope.error
        && $scope.error.error
        && $scope.error.error.error_description !== 'null'
        && $scope.error.error.error_description.length
        ? $scope.error.error.error_description
        : 'Service not available. Please contact the system administrator';
    });
  }

  private submit(username: string, password: string): void {
    if (this.$scope.timeoutDelay > 0) {
      return; // prevents spamming
    }

    this.$scope.warning = false;
    this.$scope.error = null;

    this.loginService.login(username, password).then(() => {
      const lang: any = LocalStorageService.get(`SystemConfiguration:${SysConfigConstants.LANGUAGE_SELECTION}`);

      this.$state.go('main.welcome-page');

      this.localeSwitcherService.updateBackendLanguage(lang);
    }, (data: IRestangularResponse) => {
      this.$scope.error = data.status;

      if (data.status === 400 || data.status === 401) { // bad credentials
        if (this.failedAttempts > 2) {
          this.countdown(5 * this.failedAttempts);
          this.$scope.error = null;
        } else {
          this.$scope.warning = true;
        }

        this.failedAttempts++;
      }
    });
  }

  private countdown(time: number): void {
    this.$scope.timeoutDelay = time;

    this.interval = this.$interval(() => {
      this.$scope.timeoutDelay--;

      if (this.$scope.timeoutDelay <= 0) {
        this.$interval.cancel(this.interval);
      }
    }, 1000);
  }

}
