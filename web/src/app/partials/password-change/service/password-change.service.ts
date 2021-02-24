// services
import { CRUDService } from '../../../angular-ids-project/src/helpers/services/crud.service';
import { OAuthService } from '../../../components/services/oauth/oauth.service';

// interfaces
import { IPasswordChange } from '../password-change.interface';

export let endpoint = 'users/change-pwd';

export class PasswordChangeService extends CRUDService {

  protected restangular: restangular.IService;

  private passwordChange: IPasswordChange = {
    old_password: '',
    new_password: null
  };

  /** @ngInject */
  constructor(protected Restangular: restangular.IService, private $uibModal: ng.ui.bootstrap.IModalService, private oAuthService: OAuthService) {
    super(Restangular, endpoint);
    this.model = this.passwordChange;
  }

  public updatePassword(passwordChange: IPasswordChange): ng.IPromise<void> {
    return this.restangular.one(`${endpoint}`).customPUT(passwordChange);
  }

  /**
   * Modal to tell user that the password is changed successfully
   */
  public passwordChangedMessage(logout: boolean): void {
    const modal = this.$uibModal.open({
      templateUrl: 'app/partials/password-change/password-change.template.html',
      controller: ['$scope', ($scope: ng.IScope) => {
        $scope.logout = logout;
        $scope.confirm = () => {
          modal.close();
          if (logout) {
            this.oAuthService.logout();
          }
        };
      }]
    });
  }
}
