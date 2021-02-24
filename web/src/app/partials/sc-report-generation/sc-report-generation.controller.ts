import { CRUDFormControllerUserService } from '../../angular-ids-project/src/helpers/controllers/crud-form-user-service/crud-form-user-service.controller';
import { ScReportGenerationService } from './service/sc-report-generation.service';
import { CustomDate } from '../../angular-ids-project/src/components/services/customDate/customDate.service';
import { ScAccountManagementService } from '../sc-account-management/service/sc-account-management.service';
import { IAccountMinimal } from '../accounts/accounts.interface';
import { ISCReportGeneration } from './sc-report-generation.interface';

export class ScReportGenerationController extends CRUDFormControllerUserService {

  /* @ngInject */
  constructor(protected $scope: ng.IScope, private scReportGenerationService: ScReportGenerationService, private customDate: CustomDate,
    private scAccountManagementService: ScAccountManagementService, private $timeout: ng.ITimeoutService) {
    super($scope, scReportGenerationService);

    this.$scope.customDate = this.customDate.returnDateFormatStr(false);

    // multiselect for accounts
    scAccountManagementService.getSCAccounts().then((data: IAccountMinimal) => $scope.accountsList = data);
    this.$scope.selectedAccounts = [];
    $scope.addAccountToList = () => this.addAccountToList($scope.selectedAccounts);

    $scope.setClear = (report: ISCReportGeneration) => this.setClear(report);
    $scope.returnFilteredDate = (date: Date) => this.returnFilteredDate(date);
    $scope.returnFilteredActiveStatus = (status: string) => this.returnFilteredActiveStatus(status);
    $scope.preview = (report: ISCReportGeneration) => this.preview(report);

    $scope.$watch('control', () => {
      if ($scope.control) {
        this.setDefaultDate();
      }
    });
  }

  protected setClear(report: ISCReportGeneration): void {
    this.setDefaultDate();
    this.$scope.selectedAccounts = [];
    this.$scope.accountIds = null;
    this.$scope.editable = this.scReportGenerationService.getModel();
    this.$scope.editable.report = report.report;
    this.$scope.reportResult = null;
  }

  protected preview(report: ISCReportGeneration): void {
    const params = {
      text: 'Preview',
      report: this.$scope.editable.report,
      startDate: this.returnFilteredDate(this.$scope.control.getUTCStartDate()),
      endDate: this.returnFilteredDate(this.$scope.control.getUTCEndDate())
    };
    this.scReportGenerationService.getPreview(params, this.$scope.accountIds).then((data: any) => {
      this.$scope.reportResult = data;
    });
  }

  protected setDefaultDate(): void {
    if (this.$scope.control) {

      const date = new Date();
      const startDate = new Date(date.getFullYear(), date.getMonth() - 1, 1);
      const endDate = new Date(date.getFullYear(), date.getMonth(), 0);

      this.$scope.control.setUTCStartDate(startDate);
      this.$scope.control.setUTCEndDate(endDate);
    }
  }

  /**
   * Adds accounts, selected
   * from the multiselect,
   * to a comma separated string
   *
   * @param  {Array<any>} selectedAccounts
   */
  private addAccountToList(selectedAccounts: Array<any>): void {
    const accounts = selectedAccounts.map((account: IAccountMinimal) => account.id);

    this.$scope.accountIds = accounts.join(',');
    this.$scope.accountIdsWithBrackets = accounts.length ? `{${this.$scope.accountIds}}` : '';
  }

  private returnFilteredDate(date: Date): string { // returns date string without milliseconds
    if (date === undefined || date === null) {
      return;
    }
    return `${date.toISOString().slice(0, 19)}Z`;
  }

  private returnFilteredActiveStatus(status: string): string {
    return status.length ? `{${status}}` : '';
  }

}
