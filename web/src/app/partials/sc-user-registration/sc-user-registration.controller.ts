// constants
import { SysConfigConstants } from '../../partials/system-configuration/system-configuration.constants';

// controllers
import { CRUDFormControllerUserService } from '../../angular-ids-project/src/helpers/controllers/crud-form-user-service/crud-form-user-service.controller';

// interfaces
import { IRestangularResponse } from '../../angular-ids-project/src/helpers/interfaces/restangularError.interface';
import { IScUserRegistration } from './sc-user-registration.interface';
import { ISystemConfiguration } from '../../partials/system-configuration/system-configuration.interface';

// services
import { LocalStorageService } from '../../angular-ids-project/src/components/services/localStorage/localStorage.service';
import { RecaptchaService } from '../../angular-ids-project/src/components/recaptcha/service/recaptcha.service';
import { ScUserRegistrationService } from './service/sc-user-registration.service';
import { SelfCareHelper } from '../../angular-ids-project/src/components/services/selfCareHelper/selfCareHelper.service';

export class ScUserRegistrationController extends CRUDFormControllerUserService {

  private PASSWORD_MINIMUM_LENGTH: string = `SystemConfiguration:${SysConfigConstants.PASSWORD_MINIMUM_LENGTH}`;
  private REQUIRE_EMAIL_VERIFICATION: string = SysConfigConstants.REQUIRE_EMAIL_VERIFICATION.toString();

  /* @ngInject */
  constructor(protected $scope: ng.IScope, scUserRegistrationService: ScUserRegistrationService, private recaptchaService: RecaptchaService,
    private $location: ng.ILocationService, private selfCareHelper: SelfCareHelper) {
    super($scope, scUserRegistrationService);
    super.setup({ refresh: false });

    if (!$scope.requireEmail) {
      this.selfCareHelper.fetchConfig(this.REQUIRE_EMAIL_VERIFICATION).then((resp: any) => {
        $scope.requireEmail = LocalStorageService.get(this.REQUIRE_EMAIL_VERIFICATION);
      });
    };

    // initialization
    $scope.showButton = null;
    $scope.processing = null;

    // test to see if password configurations are available
    // if not download them and add to localstorage
    if (!LocalStorageService.get(this.PASSWORD_MINIMUM_LENGTH)) {
      this.getPasswordSettings();
    }

    // functions exposed to the template
    $scope.createOverride = (editable: IScUserRegistration) => this.createOverride(editable);
    $scope.resetPassword = () => this.resetPassword();
    $scope.activate = () => this.activate();
  }

  // used to call password directive resetPass method
  // this will clean password fields and validatin flags/messages
  private resetPassword(): void {
    this.$scope.resetPass = new Date().getTime();

    this.recaptchaService.reload();
    this.$scope.showButton = null;
  }

  private activate(): void {
    if (this.$location.search().key) {
      this.service.activate(this.$location.search().key).then((response: IRestangularResponse) => {
        if (response) {
          this.$scope.activated = true;
        } else {
          this.$scope.activated = false;
        }
      }, (error: IRestangularResponse) => {
        this.$scope.error = { error: error };
      });
    };
  }

  private createOverride(editable: IScUserRegistration): void {
    editable.url = this.$location.absUrl();

    this.$scope.processing = true;

    this.service.create(editable).then(() => {
      this.$scope.userCreated = true;
      this.$scope.processing = false;

      this.reset();

      this.$scope.resetPassword();
    }, (error: IRestangularResponse) => {
      this.$scope.processing = false;

      this.$scope.error = { error: error };
    });
  }

  private getPasswordSettings(): void {
    this.service.getSettings().then((resp: ISystemConfiguration[]) => {
      for (let i = 0, len = resp.length; i < len; i++) {
        LocalStorageService.set(`SystemConfiguration:${resp[i].item_name}`, resp[i].current_value);
      };
    });
  }

}
