// controllers
import { CRUDFormControllerUserService } from '../../angular-ids-project/src/helpers/controllers/crud-form-user-service/crud-form-user-service.controller';

// interfaces
import { IInterestRate, IInterestRateScope } from './interest-rates.interface';

// services
import { InterestRatesService } from './service/interest-rates.service';
import { CustomDate } from '../../angular-ids-project/src/components/services/customDate/customDate.service';

export class InterestRatesController extends CRUDFormControllerUserService {

  /* @ngInject */
  constructor(protected $scope: IInterestRateScope, private interestRatesService: InterestRatesService, private customDate: CustomDate) {
    super($scope, interestRatesService);
    super.setup();

    this.$scope.customDate = this.customDate.returnDateFormatStr(false);
    $scope.refresh = () => this.refreshOverride();
    $scope.create = (editable: IInterestRate) => this.createOverride(editable);
    $scope.update = (editable: IInterestRate, id: number) => this.updateOverride(editable, id);
    this.getFilterParameters();
  }

  private getFilterParameters(): void {
    this.$scope.filterParameters = {
      search: this.$scope.search,
      page: this.$scope.pagination ? this.$scope.pagination.number : 0
    };
  }

  private refreshOverride(): void {
    this.getFilterParameters();
    super.list(this.$scope.filterParameters, this.$scope.getSortQueryString());
  }

  private createOverride(editable: IInterestRate): void {
    this.setEditable(editable);
    super.create(editable).then(() => this.$scope.control ? this.$scope.control.reset() : '');
  }

  private updateOverride(editable: IInterestRate, id: number): void {
    this.setEditable(editable);
    super.update(editable, id).then(() => this.$scope.control ? this.$scope.control.reset() : '');
  }

  private setEditable(editable: IInterestRate): void {

    const item = editable;
    const { default_interest_specification,
            default_interest_application,
            default_foreign_interest_specified_percentage,
            default_national_interest_specified_percentage,
            punitive_interest_specification,
            punitive_interest_application,
            punitive_interest_specified_percentage } = item;

    editable.default_foreign_interest_applied_percentage =
      this.getAppliedPercentage(default_interest_specification, default_interest_application, default_foreign_interest_specified_percentage);

    editable.default_national_interest_applied_percentage =
      this.getAppliedPercentage(default_interest_specification, default_interest_application, default_national_interest_specified_percentage);

    editable.punitive_interest_applied_percentage =
      this.getAppliedPercentage(punitive_interest_specification, punitive_interest_application, punitive_interest_specified_percentage);
  }

  private getAppliedPercentage(specification: string, application: string, percent: number): number {
    if (application === 'MONTHLY') {
      switch (specification) {
        case 'YEARLY': return percent / 12;
        case 'MONTHLY': return percent;
        case 'DAILY': return percent * 30;
      }
    }

    if (application === 'DAILY') {
      switch (specification) {
        case 'YEARLY': return percent / 365;
        case 'MONTHLY': return percent / 30;
        case 'DAILY': return percent;
      }
    }
    return null;
  }
}
