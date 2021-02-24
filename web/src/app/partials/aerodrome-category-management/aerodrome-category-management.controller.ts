// controllers
import { CRUDFormControllerUserService } from '../../angular-ids-project/src/helpers/controllers/crud-form-user-service/crud-form-user-service.controller';

// services
import { AerodromeCategoryManagementService } from './service/aerodrome-category-management.service';

export class AerodromeCategoryManagementController extends CRUDFormControllerUserService {

  /* @ngInject */
  constructor(protected $scope: ng.IScope, private aerodromeCategoryManagementService: AerodromeCategoryManagementService) {
    super($scope, aerodromeCategoryManagementService);
    super.setup();
    $scope.refreshOverride = () => this.refreshOverride();
    this.getFilterParameters();
  }

  private getFilterParameters(): void {
    this.$scope.filterParameters = {
      search: this.$scope.textFilter,
      page: this.$scope.pagination ? this.$scope.pagination.number : 0
    };
  }

  private refreshOverride(): void {
    this.getFilterParameters();
    super.list(this.$scope.filterParameters, this.$scope.getSortQueryString());
  }

}
