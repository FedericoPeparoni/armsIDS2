 // controllers
import { CRUDFormControllerUserService } from '../../angular-ids-project/src/helpers/controllers/crud-form-user-service/crud-form-user-service.controller';

 //interfaces
import { ITuRateManagementScope } from './tu-rate-management.interface';

 //services
import { TuRateManagementService } from './service/tu-rate-management.service';
import { SystemConfigurationService } from '../system-configuration/service/system-configuration.service';


export class TuRateManagementController extends CRUDFormControllerUserService {

  /* @ngInject */
  constructor(protected $scope: ITuRateManagementScope, protected tuRateManagementService: TuRateManagementService,
    protected systemConfigurationService: SystemConfigurationService) {
    super($scope, tuRateManagementService);
    this.setup();
    this.getFilterParameters();
  }


  /**
  * Setup method override, add scope functions and properties.
  */
  protected setup(): void {

    // call parent setup method, required to override parent scope functions and properties
    super.setup();

    // override refresh scope method with refresh override above
    this.$scope.refresh = () => this.refresh();

  }

  /**
   * Refresh method override, adds scope filters, pagination, sort query.
   */
  protected refresh(): angular.IPromise<void> {
    this.getFilterParameters();
    return super.list(this.$scope.filterParameters, this.$scope.getSortQueryString());
  }

  private getFilterParameters(): void {
    this.$scope.filterParameters = {
      search: this.$scope.search,
      page: this.$scope.pagination ? this.$scope.pagination.number : 0
    };
  }






}
