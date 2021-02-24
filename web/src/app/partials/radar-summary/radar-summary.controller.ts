// interfaces
import { IRadarSummaryScope } from './radar-summary.interface';

// controllers
import { CRUDFileUploadController } from '../../angular-ids-project/src/helpers/controllers/crud-file-handler/crud-file-handler.controller';

// services
import { RadarSummaryService } from './service/radar-summary.service';
import { SystemConfigurationService } from '../system-configuration/service/system-configuration.service';
import { SaveLocalTemplateService } from '../../angular-ids-project/src/components/services/saveLocalTemplate/saveLocalTemplate.service';
import { CustomDate } from '../../angular-ids-project/src/components/services/customDate/customDate.service';

// constants
import { SysConfigConstants } from '../system-configuration/system-configuration.constants';

export class RadarSummaryController extends CRUDFileUploadController {

  private eurocatFormat: string = 'EUROCAT-A';
  private intelcanFormat: string = 'INTELCAN-A';

  /* @ngInject */
  constructor(protected $scope: IRadarSummaryScope, private radarSummaryService: RadarSummaryService, private saveLocalTemplateService: SaveLocalTemplateService,
    protected $uibModal: ng.ui.bootstrap.IModalService, protected systemConfigurationService: SystemConfigurationService, private customDate: CustomDate) {
    super($scope, radarSummaryService, $uibModal, 'File Name');
    super.setup();

    $scope.$watchGroup(['search', 'control'], () => this.getFilterParameters());

    $scope.format = systemConfigurationService.getValueByName(<any>SysConfigConstants.RADAR_SUMMARY_FORMAT);
    $scope.flightLevelRequired = systemConfigurationService.getBooleanFromValueByName(<any>SysConfigConstants.VALIDATE_FLIGHT_LEVEL_AIRSPACE);

    this.$scope.customDate = this.customDate.returnDateFormatStr(false);

    // overwrite default '.csv' file upload pattern based on radar format
    if ($scope.format === this.eurocatFormat) {
      $scope.pattern = '.print';
    } else if ($scope.format === this.intelcanFormat) {
      $scope.pattern = null;
    }

    this.saveLocalTemplate();
    $scope.refreshOverride = () => this.refreshOverride();
  }

  private saveLocalTemplate(): void {
    let filterPairs = [['search', 'search']];
    this.saveLocalTemplateService.saveLocalTemplate(this.$scope, 'radar-summary', filterPairs);
  }

  private getFilterParameters(): void {
    let startDate: string;
    let endDate: string;

    if (this.$scope.control && this.$scope.control.getUTCStartDate()) {
      startDate = this.$scope.control.getUTCStartDate().toISOString().substr(0, 10);
    }

    if (this.$scope.control && this.$scope.control.getUTCEndDate()) {
      endDate = this.$scope.control.getUTCEndDate().toISOString().substr(0, 10);
    }

    this.$scope.filterParameters = {
      'search': this.$scope.search,
      'start': startDate,
      'end': endDate,
      'page': this.$scope.pagination ? this.$scope.pagination.number : 0
    };
  }

  /**
   * Calls list with applied filters
   *
   * @returns void
   */
  private refreshOverride(): void {
    this.getFilterParameters();
    super.list(this.$scope.filterParameters, this.$scope.getSortQueryString());
  }
}
