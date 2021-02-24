// controllers
import { CRUDFileUploadController } from '../../angular-ids-project/src/helpers/controllers/crud-file-handler/crud-file-handler.controller';

// interfaces
import { IAirNavigationChargeScope } from './air-navigation-charges.interface';

// services
import { AirNavigationChargesService } from './service/air-navigation-charges.service';
import { AerodromeCategoryManagementService } from '../aerodrome-category-management/service/aerodrome-category-management.service';
import { SystemConfigurationService } from '../system-configuration/service/system-configuration.service';

export class AirNavigationChargesController extends CRUDFileUploadController {

  /* @ngInject */
  constructor(protected $scope: IAirNavigationChargeScope, private airNavigationChargesService: AirNavigationChargesService,
    private systemConfigurationService: SystemConfigurationService, protected $uibModal: ng.ui.bootstrap.IModalService,
    protected aerodromeCategoryManagementService: AerodromeCategoryManagementService) {
    super($scope, airNavigationChargesService, $uibModal, 'File Name');

    $scope.pattern = '.xls, .xlsx';

    $scope.chargeTypes = airNavigationChargesService.listChargeTypes();
    $scope.shouldShowCharge = (chargeType: string) => systemConfigurationService.shouldShowCharge(chargeType);
    $scope.refreshOverride = () => this.refreshOverride();
    this.getFilterParameters();

    aerodromeCategoryManagementService.listAll().then((resp: any) => {
      $scope.aerodromeCategoryList = resp.content;
      super.setup();
    });

  }

  protected refreshOverride(): angular.IPromise<void> {
    this.getFilterParameters();
    return super.list(this.$scope.filterParameters, this.$scope.getSortQueryString());
  }

  private getFilterParameters(): void {
    this.$scope.filterParameters = {
      textFilter: this.$scope.textFilter,
      page: this.$scope.pagination ? this.$scope.pagination.number : 0
    };
  }

}
