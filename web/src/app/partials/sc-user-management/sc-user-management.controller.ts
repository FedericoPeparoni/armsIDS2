// controllers
import { CRUDFormControllerUserService } from '../../angular-ids-project/src/helpers/controllers/crud-form-user-service/crud-form-user-service.controller';

// services
import { ScUserManagementService } from './service/sc-user-management.service';

export class ScUserManagementController extends CRUDFormControllerUserService {

  /* @ngInject */
  constructor(protected $scope: ng.IScope, scUserManagementService: ScUserManagementService) {
    super($scope, scUserManagementService);
    super.setup({ refresh: false });
    super.list({}, `selfCareUser=true`);

    // used to call password directive resetPass method
    // this will clean password fields and validatin flags/messages
    $scope.resetPassword = () => this.$scope.resetPass = new Date().getTime();
    $scope.refreshOverride = () => this.refreshOverride();
    this.getFilterParameters();
  }

  private getFilterParameters(): void {
    this.$scope.filterParameters = {
      search: this.$scope.textFilter,
      selfCareUser: true,
      page: this.$scope.pagination ? this.$scope.pagination.number : 0
    };
  }

  private refreshOverride(): ng.IPromise<any> {
    this.getFilterParameters();
    return this.$scope.refresh(this.$scope.filterParameters, this.$scope.getSortQueryString());
  }

}
