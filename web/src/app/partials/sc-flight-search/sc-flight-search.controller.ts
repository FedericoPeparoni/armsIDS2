// controllers
import { CRUDFormControllerUserService } from '../../angular-ids-project/src/helpers/controllers/crud-form-user-service/crud-form-user-service.controller';

// services
import { ScFlightSearchService } from './service/sc-flight-search.service';
import { CustomDate } from '../../angular-ids-project/src/components/services/customDate/customDate.service';

export class ScFlightSearchController extends CRUDFormControllerUserService {

  /* @ngInject */
  constructor(protected $scope: ng.IScope, private scFlightSearchService: ScFlightSearchService, private customDate: CustomDate, private $filter: ng.IFilterService) {
    super($scope, scFlightSearchService);
    super.setup();

    this.getFilterParameters();
    $scope.refreshOverride = () => this.refreshOverride();

    $scope.customDate = this.customDate.returnDateFormatStr(false);

    $scope.$watch('list', () => {
      this.getAccountBalance();
    });
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
      flightId: this.$scope.flightIdFilter,
      icaoCode: this.$scope.icaoCodeFilter,
      account: this.$scope.accountFilter,
      startDate: startDate,
      endDate: endDate,
      page: this.$scope.pagination ? this.$scope.pagination.number : 0
    };
  }

  private refreshOverride(): ng.IPromise<any> {
    this.getFilterParameters();
    return this.$scope.refresh(this.$scope.filterParameters, this.$scope.getSortQueryString());
  }

  private getAccountBalance(): void {
    this.$scope.accountBalance = null;
    if (this.$scope.accountFilter && this.$scope.list.length) {
      this.$scope.accountBalance = this.$filter('number')(this.$scope.list[0].associated_account_usd_balance, 2);
    }
  }

}
