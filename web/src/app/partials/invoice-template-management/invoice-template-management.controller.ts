// controllers
import { CRUDFileUploadController } from '../../angular-ids-project/src/helpers/controllers/crud-file-handler/crud-file-handler.controller';

// services
import { InvoiceTemplateManagementService } from './service/invoice-template-management.service';

// interface
import { IInvoiceTemplateManagementScope } from './invoice-template-management.interface';

export class InvoiceTemplateManagementController extends CRUDFileUploadController {

  /* @ngInject */
  constructor(protected $scope: IInvoiceTemplateManagementScope, private invoiceTemplateManagementService: InvoiceTemplateManagementService, protected $uibModal: ng.ui.bootstrap.IModalService) {
    super($scope, invoiceTemplateManagementService, $uibModal);
    super.setup();

    $scope.pattern = '.rptdesign'; // currently only accepts .rptdesign file types
    $scope.resetTemplate = (invoiceCategory: string) => invoiceTemplateManagementService.resetTemplate(invoiceCategory)
      .then(() => this.list())
      .then(() => super.reset());

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
