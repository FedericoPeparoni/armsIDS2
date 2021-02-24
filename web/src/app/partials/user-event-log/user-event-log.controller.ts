// controllers
import { CRUDFormControllerUserService } from '../../angular-ids-project/src/helpers/controllers/crud-form-user-service/crud-form-user-service.controller';

// services
import { UserEventLogService } from './service/user-event-log.service';
import { CustomDate } from '../../angular-ids-project/src/components/services/customDate/customDate.service';

export class UserEventLogController extends CRUDFormControllerUserService {

  /* @ngInject */
  constructor(protected $scope: ng.IScope, private userEventLogService: UserEventLogService, private customDate: CustomDate) {
    super($scope, userEventLogService);
    super.setup();

    $scope.refreshOverride = () => this.refreshOverride();
    this.$scope.customDate = this.customDate.returnDateFormatStr(false);
    this.getFilterParameters();
  }

  private getFilterParameters(): void {
    this.$scope.filterParameters = {
      search: this.$scope.search,
      page: this.$scope.pagination ? this.$scope.pagination.number : 0
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
