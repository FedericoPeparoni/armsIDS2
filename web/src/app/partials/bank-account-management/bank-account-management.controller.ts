// controllers
import { CRUDFormControllerUserService } from '../../angular-ids-project/src/helpers/controllers/crud-form-user-service/crud-form-user-service.controller';

// interfaces
import { IBankAccountScope } from './bank-account-management.interface';

// services
import { BankAccountManagementService } from './service/bank-account-management.service';
import { SystemConfigurationService } from '../system-configuration/service/system-configuration.service';
import { SysConfigConstants } from '../system-configuration/system-configuration.constants';

export class BankAccountManagementController extends CRUDFormControllerUserService {

  /* @ngInject */
  constructor(protected $scope: IBankAccountScope, protected bankAccountManagementService: BankAccountManagementService,
    protected systemConfigurationService: SystemConfigurationService) {
    super($scope, bankAccountManagementService);
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

    // add scope boolean flag for external identifier requirement
    this.$scope.requireExternalSystemId = this.systemConfigurationService
      .getBooleanFromValueByName(<any>SysConfigConstants.REQUIRE_BANK_ACCOUNT_EXTERNAL_SYSTEM_ID);
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
