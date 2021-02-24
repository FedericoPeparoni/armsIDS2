// controllers
import { CRUDFormController } from '../../angular-ids-project/src/helpers/controllers/crud-form/crud-form.controller';

// interfaces
import { ISystemSummaryScope, ISystemSummary } from './system-summary.interface';

// services
import { SystemSummaryService } from './service/system-summary.service';
import { SystemConfigurationService } from '../system-configuration/service/system-configuration.service';

export class SystemSummaryController extends CRUDFormController {

  /* @ngInject */
  constructor(protected $scope: ISystemSummaryScope, private systemSummaryService: SystemSummaryService, private systemConfigurationService: SystemConfigurationService) {
    super(systemSummaryService);
    this.setup();
    $scope.shouldShowCharge = (chargeType: string) => systemConfigurationService.shouldShowCharge(chargeType);

    $scope.formatName = (name: string) => {
      if (name) {
        return name.replace(/_/g, ' ').toLocaleLowerCase();
      }
      return;
    };
  }

  protected list(): ng.IPromise<void> {
    return super.list().then((data: ISystemSummary) => {
      this.$scope.data = data;
      this.$scope.list = Object.keys(data).map((k: string) => data[k]);
    });
  }
}

