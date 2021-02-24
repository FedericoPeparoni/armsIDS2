// controllers
import { CRUDFormControllerUserService } from '../../angular-ids-project/src/helpers/controllers/crud-form-user-service/crud-form-user-service.controller';

// services
import { AircraftExemptManagementService } from './service/aircraft-exempt-management.service';

// interfaces
import { IAircraftExemptManagementServiceScope } from './aircraft-exempt-management.interface';
import { SystemConfigurationService } from '../system-configuration/service/system-configuration.service';

export class AircraftExemptManagementController extends CRUDFormControllerUserService {

  /* @ngInject */
  constructor(
    protected $scope: IAircraftExemptManagementServiceScope,
    private aircraftExemptManagementService: AircraftExemptManagementService,
    private systemConfigurationService: SystemConfigurationService
  ) {
    super($scope, aircraftExemptManagementService);
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
