// controllers
import { CRUDFormControllerUserService } from '../../angular-ids-project/src/helpers/controllers/crud-form-user-service/crud-form-user-service.controller';

// services
import { NominalRoutesService } from './service/nominal-routes.service';

// interfaces
import { INominalroutesScope } from './nominal-routes.interface';

export class NominalRoutesController extends CRUDFormControllerUserService {

  /* @ngInject */
  constructor(protected $scope: INominalroutesScope, private nominalRoutesService: NominalRoutesService, $translate: angular.translate.ITranslateService) {
    super($scope, nominalRoutesService);
    super.setup();

    $scope.routeTypes = nominalRoutesService.listRouteTypes();
    this.getFilterParameters();
    $scope.refresh = () => this.refreshOverride();
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
