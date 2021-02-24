// controllers
import { CRUDFormControllerUserService } from '../../angular-ids-project/src/helpers/controllers/crud-form-user-service/crud-form-user-service.controller';

// interface
import { IAccountExemptManagementScope } from './account-exempt-management.interface';

// services
import { AccountExemptManagementService } from './service/account-exempt-management.service';
import { SystemConfigurationService } from '../system-configuration/service/system-configuration.service';

export class AccountExemptManagementController extends CRUDFormControllerUserService {

  /* @ngInject */
  constructor(protected $scope: IAccountExemptManagementScope, private accountExemptManagementService: AccountExemptManagementService,
    private systemConfigurationService: SystemConfigurationService) {
    super($scope, accountExemptManagementService);
    super.setup();

    $scope.shouldShowCharge = (chargeType: string) => systemConfigurationService.shouldShowCharge(chargeType);
    this.getFilterParameters();
    $scope.refresh = () => this.refreshOverride();
  }

  private getFilterParameters(): void {
    this.$scope.filterParameters = {
      searchFilter: this.$scope.textFilter,
      page: this.$scope.pagination ? this.$scope.pagination.number : 0
    };
  }

  private refreshOverride(): void {
    this.getFilterParameters();
    super.list(this.$scope.filterParameters, this.$scope.getSortQueryString());
  }
}
