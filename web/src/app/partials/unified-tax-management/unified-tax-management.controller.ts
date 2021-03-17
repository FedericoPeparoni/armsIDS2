 // controllers
import { CRUDFormControllerUserService } from '../../angular-ids-project/src/helpers/controllers/crud-form-user-service/crud-form-user-service.controller';

 //interfaces
import { ITuRateManagementScope } from './unified-tax-managment.interface';

 //services
import { UnifiedTaxManagementService } from './service/unified-tax-management.service';
import { SystemConfigurationService } from '../system-configuration/service/system-configuration.service';
import { SysConfigConstants } from '../system-configuration/system-configuration.constants';
import { CustomDate } from '../../angular-ids-project/src/components/services/customDate/customDate.service';
export class UnifiedTaxManagementController extends CRUDFormControllerUserService {

  /* @ngInject */
  constructor(protected $scope: ITuRateManagementScope, protected unifiedTaxManagementService: UnifiedTaxManagementService,
    protected systemConfigurationService: SystemConfigurationService,private customDate: CustomDate) {
    super($scope, unifiedTaxManagementService);
    this.setup();
    this.getFilterParameters();
  }


  /**
  * Setup method override, add scope functions and properties.
  */
  protected setup(): void {
    this.$scope.customDate = this.customDate.returnDateFormatStr(false);


    // call parent setup method, required to override parent scope functions and properties
    super.setup();

    // override refresh scope method with refresh override above
    this.$scope.refresh = () => this.refresh();



     // add scope boolean flag for external identifier requirement
     this.$scope.requireExternalSystemId = this.systemConfigurationService
     .getBooleanFromValueByName(<any>SysConfigConstants.REQUIRE_UNIFIED_TAX_EXTERNAL_SYSTEM_ID);
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
