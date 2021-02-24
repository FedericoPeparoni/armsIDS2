// constants
import { SysConfigConstants } from '../system-configuration/system-configuration.constants';

// controllers
import { CRUDFormControllerUserService } from '../../angular-ids-project/src/helpers/controllers/crud-form-user-service/crud-form-user-service.controller';

// interface
import { IUser } from '../users/users.interface';
import { IRestangularResponse } from '../../angular-ids-project/src/helpers/interfaces/restangularError.interface';
import { IUserInfoChange, IUserInfoChangeScope } from './current-user.interface';

// service
import { UsersService } from '../users/service/users.service';
import { SystemConfigurationService } from '../system-configuration/service/system-configuration.service';
import { PasswordChangeService } from '../password-change/service/password-change.service';

export class CurrentUserController extends CRUDFormControllerUserService {

  /* @ngInject */
  constructor(protected $scope: IUserInfoChangeScope, private usersService: UsersService,
    private systemConfigurationService: SystemConfigurationService, private passwordChangeService: PasswordChangeService) {

    super($scope, passwordChangeService);
    super.setup({ refresh: false });

    this.usersService.current.then((user: IUser) => {
      const {email, login, sms_number} = user;
      $scope.login = login;
      $scope.editable.email = email;
      $scope.editable.sms_number = sms_number;
      $scope.editable.new_password = null;
    });

    // expose necessary methods to scope
    $scope.resetPassword = (setZeroLength?: boolean) => this.resetPassword(setZeroLength);

    // get minlength system configuration value and update scope
    $scope.passwordMinLength = parseInt(<string> systemConfigurationService.getValueByName(<any>SysConfigConstants.PASSWORD_MINIMUM_LENGTH), 10);

    $scope.updatePassword = (changePassword: IUserInfoChange) => usersService.updatePassword(changePassword)
      .then((response: any) => {
        if (response) {
          this.passwordChangeService.passwordChangedMessage(false);
        }
        $scope.editable.old_password = '';
        $scope.resetPassword();
      }).catch((error: IRestangularResponse) => this.$scope.error = { error });
  }

  /**
   * Reset password values, validation and flags.
   *
   * @param setZeroLength true to set password to zero length
   */
  private resetPassword(setZeroLength?: boolean): void {
    setZeroLength = setZeroLength || false;

    // set password to an empty string if isZeroLength
    // else set to undefined to symbolize an untouched/unset value
    if (setZeroLength) {
      this.$scope.editable.new_password = '';
    } else {
      delete(this.$scope.editable.new_password);
    }

    // used to call password directive resetPass method
    // this will clean password fields and validatin flags/messages
    this.$scope.resetPass = new Date().getTime();
  }
}
