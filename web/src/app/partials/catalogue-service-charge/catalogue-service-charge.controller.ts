// controllers
import { CRUDFormControllerUserService } from '../../angular-ids-project/src/helpers/controllers/crud-form-user-service/crud-form-user-service.controller';

// interfaces
import { ICatalogueServiceChargeScope } from './catalogue-service-charge.interface';
import { SystemConfigurationService } from '../system-configuration/service/system-configuration.service';
import { SysConfigConstants } from '../system-configuration/system-configuration.constants';

// services
import { CatalogueServiceChargeService } from './service/catalogue-service-charge.service';
import { InvoiceTemplateManagementService } from '../invoice-template-management/service/invoice-template-management.service';
import { CurrencyManagementService } from '../currency-management/service/currency-management.service';
import { ICurrency } from '../currency-management/currency-management.interface';

export class CatalogueServiceChargeController extends CRUDFormControllerUserService {

  /* @ngInject */
  constructor(protected $scope: ICatalogueServiceChargeScope,
              private catalogueServiceChargeService: CatalogueServiceChargeService,
              private invoiceTemplateManagementService: InvoiceTemplateManagementService,
              private currencyManagementService: CurrencyManagementService,
              private systemConfigurationService: SystemConfigurationService) {
    super($scope, catalogueServiceChargeService);
    super.setup({ filter: true });

    $scope.basisList = catalogueServiceChargeService.getBasisList();
    $scope.categoryList = catalogueServiceChargeService.listCategories();
    $scope.externalDatabaseList = [
      {
        name: 'AATIS',
        value: 'AATIS'
      },
      {
        name: 'EAIP',
        value: 'EAIP'
      }
    ];

    // set scope value for require external system identifier
    $scope.requireExternalSystemId = this.requireExternalSystemId();
    $scope.refresh = () => this.refreshOverride();
    this.getFilterParameters();

    // get the ANSP currency
    currencyManagementService.getANSPCurrencyAndUSD().then((currencies: ICurrency[]) => {
      $scope.currencies = currencies;
    });
  }

  /**
   * Get required external system id flag from system configuration.
   *
   * @returns boolean true if required
   */
  private requireExternalSystemId(): boolean {
    return this.systemConfigurationService
      .getBooleanFromValueByName(<any>SysConfigConstants.REQUIRE_SERVICE_CHARGE_CATALOGUE_EXTERNAL_SYSTEM_ID);
  }

  private getFilterParameters(): void {
    this.$scope.filterParameters = {
      textFilter: this.$scope.textFilter,
      page: this.$scope.pagination ? this.$scope.pagination.number : 0
    };
  }

  /**
   * Refresh data list in scope.
   */
  private refreshOverride(): ng.IPromise<any> {
    this.getFilterParameters();
    return super.list(this.$scope.filterParameters, this.$scope.getSortQueryString());
  }
}
