// controllers
import { CRUDFileUploadController } from '../../angular-ids-project/src/helpers/controllers/crud-file-handler/crud-file-handler.controller';

// services
import { ReportTemplatesService } from './service/report-templates.service';

// interface
import { IIReportTemplateScope } from './report-templates.interface';

export class ReportTemplatesController extends CRUDFileUploadController {

  /* @ngInject */
  constructor(protected $scope: IIReportTemplateScope, private reportTemplatesService: ReportTemplatesService, protected $uibModal: ng.ui.bootstrap.IModalService) {
    super($scope, reportTemplatesService, $uibModal);
    super.setup();
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
