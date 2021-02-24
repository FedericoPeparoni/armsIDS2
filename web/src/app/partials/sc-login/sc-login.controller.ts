// service
import { ScLoginService } from './service/sc-login.service';
import { UsersService } from '../users/service/users.service';

// interfaces
import { IRestangularResponse } from '../../angular-ids-project/src/helpers/interfaces/restangularError.interface';

// constants
import { LOGIN_ERROR } from '../../login/login.constants';

declare let bowser: any;

export class ScLoginController {

  private interval: ng.IPromise<any>;
  private failedAttempts: number = 0;

  /* @ngInject */
  constructor(private $scope: ng.IScope, private scLoginService: ScLoginService, private $state: angular.ui.IStateService,
    private $interval: ng.IIntervalService, private $window: ng.IWindowService, private usersService: UsersService) {

    $scope.passwordRecoveryForm = () => { $scope.recoveryForm = true; $scope.error = null; };
    $scope.closeRecoveryForm = () => $scope.recoveryForm = false;
    $scope.showLogin = () => { $scope.recoveryForm = false; $scope.tempPaswordSent = false; $scope.error = null; };
    $scope.recoverPassword = (email: string) => usersService.recoverPassword(email).then(() => {
      $scope.error = null;
      $scope.tempPaswordSent = true;
    },
      (error: IRestangularResponse) => {
        $scope.error = { error: error.data };
      });

    this.interval = null;

    $scope.offlineServerError = $scope.language && $scope.language.selected && $scope.language.selected.code
      ? LOGIN_ERROR.SERVER[$scope.language.selected.code]
      : LOGIN_ERROR.SERVER.en;

    $scope.timeoutDelay = 0;
    $scope.warning = false;
    $scope.submit = (username: string, password: string) => this.submit(username, password);

    if (!bowser.chrome && !bowser.firefox && !bowser.msedge) {
      $scope.browserWarning = true;
    } else if (bowser.chrome && bowser.version < 52 || bowser.firefox && bowser.version < 47) {
      $scope.browserWarning = true;
    }

  }

  private submit(username: string, password: string): void {
    if (this.$scope.timeoutDelay > 0) {
      return; // prevents spamming
    }

    this.$scope.warning = false;
    this.$scope.error = null;

    this.scLoginService.login(username, password).then(() => {
      this.$state.go('main.sc-home-page', {}, { reload: true });

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
      };
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
