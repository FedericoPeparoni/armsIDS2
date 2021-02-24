// controllers
import { CRUDFormControllerUserService } from '../../angular-ids-project/src/helpers/controllers/crud-form-user-service/crud-form-user-service.controller';

// interfaces
import { IAircraftType } from './aircraft-type-management.interface';

// services
import { SystemConfigurationService } from '../system-configuration/service/system-configuration.service';
import { AircraftTypeManagementService } from './service/aircraft-type-management.service';

// constants
import { SysConfigConstants } from '../system-configuration/system-configuration.constants';

export class AircraftTypeManagementController extends CRUDFormControllerUserService {

  /* @ngInject */
  constructor(protected $scope: ng.IScope, private aircraftTypeManagementService: AircraftTypeManagementService,
    private systemConfigurationService: SystemConfigurationService) {
    super($scope, aircraftTypeManagementService);
    super.setup();
    $scope.mtowUnitOfMeasure = systemConfigurationService.getValueByName(<any>SysConfigConstants.MTOW_UNIT_OF_MEASURE);
    $scope.edit = (item: IAircraftType) => super.edit(systemConfigurationService.convertMtowProperty(item, 'maximum_takeoff_weight'));
    $scope.refreshOverride = () => this.refreshOverride();
    this.getFilterParameters();
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
