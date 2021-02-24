// constants
import { SysConfigConstants } from '../../../../partials/system-configuration/system-configuration.constants';

// interfaces
import { RecaptchaScope } from './recaptcha.interface';

// services
import { RecaptchaService } from './service/recaptcha.service';
import { LocalStorageService } from '../services/localStorage/localStorage.service';
import { SelfCareHelper } from '../services/selfCareHelper/selfCareHelper.service';

/** @ngInject */
export function recaptcha(): angular.IDirective {

  return {
    restrict: 'E',
    templateUrl: 'app/angular-ids-project/src/components/recaptcha/recaptcha.html',
    replace: true,
    controller: RecaptchaController,
    controllerAs: 'RecaptchaController'
  };
}

/** @ngInject */
export class RecaptchaController {

  constructor(private $scope: RecaptchaScope, private recaptchaService: RecaptchaService, private selfCareHelper: SelfCareHelper) {

    // intialize recaptcha scope properties that are dependent on system configuration setting
    this.initialize();

    // always set public key from recaptcha service if available
    this.$scope.publicKey = this.recaptchaService.publicKey;

    // get the widgetId when the recaptcha instance is created via onCreate callback
    this.$scope.onWidgetCreate = (widgetId: number) => this.$scope.widgetId = widgetId;

    this.$scope.setResponse = (response: any) => this.$scope.showButton = true;
    this.$scope.recaptchaExpire = () => this.recaptchaService.reload(this.$scope.widgetId);
  }

  private async initialize(): Promise<any> {

    // test to see if captcha configurations are available
    // if not download them and add to localstorage
    this.$scope.requireCaptcha = LocalStorageService.get(<any>SysConfigConstants.REQUIRE_CAPTCHA);
    if (!this.$scope.requireCaptcha) {
      this.$scope.requireCaptcha = await this.selfCareHelper.fetchConfig(<any>SysConfigConstants.REQUIRE_CAPTCHA)
        .then(() => LocalStorageService.get(<any>SysConfigConstants.REQUIRE_CAPTCHA));
    }

    if (this.$scope.requireCaptcha) {
      this.$scope.loginFromBilling = JSON.parse(window.localStorage.getItem('loginFromBilling'));
      this.$scope.showButton = this.$scope.loginFromBilling === false;
    } else {
      this.$scope.showButton = true;
    }
  }
}
