// controllers
import { CRUDFormControllerUserService } from '../../angular-ids-project/src/helpers/controllers/crud-form-user-service/crud-form-user-service.controller';

// services
import { MtowService } from './service/mtow.service';
import { SystemConfigurationService } from '../system-configuration/service/system-configuration.service';

// interfaces
import { IMtowType } from './mtow.interface';

// constants
import { SysConfigConstants } from '../system-configuration/system-configuration.constants';

export class MtowController extends CRUDFormControllerUserService {

  /* @ngInject */
  constructor(protected $scope: ng.IScope, private mtowService: MtowService, private systemConfigurationService: SystemConfigurationService) {
    super($scope, mtowService);
    super.setup({ filter: 'DOMESTIC' });

    $scope.$watch('factorClassFilter.value', () => this.getFilterParameters());


    // configuration for MTOW Factor Class (t/f)
    const mtowSystemConfigValue = systemConfigurationService.getValueByName(<any>SysConfigConstants.USE_MTOW_FACTOR_CLASS);
    $scope.useMTOWFactorClass = mtowSystemConfigValue === SysConfigConstants.SYSTEM_CONFIG_TRUE;

    // configuration for MTOW Units (tons/kg)
    $scope.mtowUnitOfMeasure = systemConfigurationService.getValueByName(<any>SysConfigConstants.MTOW_UNIT_OF_MEASURE);
    $scope.edit = (item: IMtowType) => { super.edit(systemConfigurationService.convertMtowProperty(item, 'upper_limit')); };

    $scope.factorClass = this.service.listMtowFactorClass();
    $scope.refreshOverride = () => this.refreshOverride();

    $scope.factorClassFilter = {
      value: 'DOMESTIC'
    };
  }

  protected refreshOverride(): angular.IPromise<void> {
    this.getFilterParameters();
    return super.list(this.$scope.filterParameters, this.$scope.getSortQueryString());
  }

  private getFilterParameters(): void {
    this.$scope.filterParameters = {
      filter: this.$scope.factorClassFilter.value,
      page: this.$scope.pagination ? this.$scope.pagination.number : 0
    };
  }
}
