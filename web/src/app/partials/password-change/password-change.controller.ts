// controllers
import { CRUDFormControllerUserService } from '../../angular-ids-project/src/helpers/controllers/crud-form-user-service/crud-form-user-service.controller';

// interface
import { IPasswordChange, IPasswordChangeScope } from './password-change.interface';
import { IRestangularResponse } from '../../angular-ids-project/src/helpers/interfaces/restangularError.interface';

// service
import { UsersService } from '../users/service/users.service';
import { PasswordChangeService } from './service/password-change.service';
import { IUser } from '../users/users.interface';

export class PasswordChangeController extends CRUDFormControllerUserService {

  /* @ngInject */
  constructor(
    protected $scope: IPasswordChangeScope,
    private usersService: UsersService,
    private passwordChangeService: PasswordChangeService,
    private $window: ng.IWindowService
    ) {

    super($scope, passwordChangeService);
    super.setup({ refresh: false });

    $scope.notificationText = 'Enter your current password, type a new password, and then type in again to confirm it. </br> After saving, you might need to re-enter your user name and password and sign in again.';

    $scope.forcePasswordChange = JSON.parse(this.$window.localStorage.getItem('forcePasswordChange'));

    this.usersService.current.then((user: IUser) => {
      const {email, login} = user;
      $scope.email = email;
      $scope.login = login;
      $scope.editable.new_password = null;
    });

    $scope.updatePassword = (changePassword: IPasswordChange) => {
      passwordChangeService.updatePassword(changePassword)
        .then(() => this.passwordChangeService.passwordChangedMessage(true))
        .catch((error: IRestangularResponse) => this.$scope.error = { error });
    };
  }
}

