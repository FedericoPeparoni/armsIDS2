// constants
import { SysConfigConstants } from '../system-configuration/system-configuration.constants';

// controllers
import { CRUDFormControllerUserService } from '../../angular-ids-project/src/helpers/controllers/crud-form-user-service/crud-form-user-service.controller';

// interface
import { IUser, IUserScope } from './users.interface';
import { IRole } from '../roles/roles.interface';

// service
import { UsersService } from './service/users.service';
import { RolesService } from '../roles/service/roles.service';
import { SystemConfigurationService } from '../system-configuration/service/system-configuration.service';
import { BillingCentreManagementService } from '../billing-centre-management/service/billing-centre-management.service';
import { IBillingCentreSpring, IBillingCentre } from '../billing-centre-management/billing-centre-management.interface';

export class UsersController extends CRUDFormControllerUserService {

  /* @ngInject */
  constructor(protected $scope: IUserScope, private $state: any, private usersService: UsersService, private rolesService: RolesService,
    private systemConfigurationService: SystemConfigurationService, private billingCentreManagementService: BillingCentreManagementService) {
    super($scope, usersService);
    super.setup({ refresh: false });
    super.list({}, `selfCareUser=false`);

    // set default scope values
    $scope.setZeroLength = false;

    // expose necessary methods to scope
    $scope.resetPassword = (setZeroLength?: boolean) => this.resetPassword(setZeroLength);

    // watch for editable password change and if empty, assume setZeroLength true
    $scope.$watch('editable.password', (newValue: string) => {
      $scope.setZeroLength = newValue === '';
    });

    $scope.$watch('userType', () => this.getFilterParameters());

    $scope.reset = () => this.resetOverride();
    $scope.create = (editable: IUser) => this.createOverride(editable);
    $scope.update = (editable: IUser, id: number) => this.updateOverride(editable, id);
    $scope.delete = (id: number) => this.deleteOverride(id);
    $scope.refresh = () => this.refreshOverride();

    // get roles and bind to scope
    rolesService.listAllWithoutChangePassword().then((roles: Array<IRole>) => {
      $scope.rolesList = roles;
      $scope.rolesListForNotSelfCare = $scope.rolesList.filter(
        (item: IRole) => typeof item !== 'undefined' && item.name && item.name.toLowerCase() !== 'self-care operators');
    });

    // set default billing centre
    billingCentreManagementService.listAll().then((billingCentres: IBillingCentreSpring) => {
      $scope.editable.billing_center = $scope.defaultBillingCentre = billingCentres.content.find((bc: IBillingCentre) => bc.hq);
    });

    // get minlength system configuration value and update scope
    $scope.minLength = parseInt(<string> systemConfigurationService.getValueByName(<any>SysConfigConstants.PASSWORD_MINIMUM_LENGTH), 10);

    // get single user if state has a user id
    if (typeof $state.params.id !== 'undefined') { // edit single user by URL: /user/1
      this.service.get($state.params.id).then((user: IUser) => this.edit(user));
    }

    $scope.checkSelfCareUser = () => {
        if ($scope.editable.is_selfcare_user) {
          $scope.editable.billing_center = null;
          $scope.editable.roles = [];
          $scope.rolesList.forEach((element: IRole) => {
            if (typeof element !== 'undefined' && element.name && element.name.toLowerCase() === 'self-care operators') {
              $scope.editable.roles.push(element);
            }
          });
          if ($scope.editable.password === '') {
            $scope.editable.password = null;
          }
        } else {
          $scope.editable.roles = [];
        }
      };
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
      this.$scope.editable.password = '';
    } else {
      delete(this.$scope.editable.password);
    }

    // used to call password directive resetPass method
    // this will clean password fields and validatin flags/messages
    this.$scope.resetPass = new Date().getTime();
  }

  private resetOverride(): void {
    this.resetPassword();
    super.reset();
    this.$scope.editable.billing_center = this.$scope.defaultBillingCentre;
  }

  private updateOverride(editable: IUser, id: number): void {
    super.update(editable, id).then(() => this.resetOverride());
  }

  private deleteOverride(id: number): void {
    super.delete(id).then(() => this.resetOverride());
  }

  private createOverride(editable: IUser): void {
    super.create(editable).then(() => this.resetOverride());
  }

  private getFilterParameters(): void {
    this.$scope.filterParameters = {
      selfCareUser: this.$scope.userType,
      search: this.$scope.textFilter,
      page: this.$scope.pagination ? this.$scope.pagination.number : 0
    };
  }

  private refreshOverride(): void {
    this.getFilterParameters();
    super.list(this.$scope.filterParameters, this.$scope.getSortQueryString(), 'users');
  }
}
