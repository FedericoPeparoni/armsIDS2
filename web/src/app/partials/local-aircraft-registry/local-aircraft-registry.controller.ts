// controllers
import { CRUDFileUploadController } from '../../angular-ids-project/src/helpers/controllers/crud-file-handler/crud-file-handler.controller';

// services
import { LocalAircraftRegistryService } from './service/local-aircraft-registry.service';
import { SystemConfigurationService } from '../system-configuration/service/system-configuration.service';
import { CustomDate } from '../../angular-ids-project/src/components/services/customDate/customDate.service';

// interfaces
import { ILocalAircraftRegistry, ILocalAircraftRegistryScope } from './local-aircraft-registry.interface';

// constants
import { SysConfigConstants } from '../system-configuration/system-configuration.constants';

export class LocalAircraftRegistryController extends CRUDFileUploadController {

  /* @ngInject */
  constructor(
    private localAircraftRegistryService: LocalAircraftRegistryService,
    private systemConfigurationService: SystemConfigurationService,
    protected $scope: ILocalAircraftRegistryScope,
    protected $uibModal: ng.ui.bootstrap.IModalService,
    private customDate: CustomDate
  ) {

    // setup
    super($scope, localAircraftRegistryService, $uibModal, 'File Name');
    super.setup();

    this.$scope.customDate = this.customDate.returnDateFormatStr(false);

    this.getFilterParameters();

    $scope.mtowUnitOfMeasure = systemConfigurationService.getValueByName(<any>SysConfigConstants.MTOW_UNIT_OF_MEASURE);
    $scope.organizationName = systemConfigurationService.getValueByName(<any>SysConfigConstants.ORGANIZATION);
    $scope.addDate = (localAircraftRegistry: ILocalAircraftRegistry) => this.addDate(localAircraftRegistry);

    $scope.create = (localAircraftRegistry: ILocalAircraftRegistry, renewal: string, expiry: string) => this.createOverride(localAircraftRegistry, renewal, expiry);
    $scope.convertMtowProperty = (localAircraftRegistry: ILocalAircraftRegistry) => {
      $scope.editable.mtow_weight = systemConfigurationService.convertMtowProperty(localAircraftRegistry, 'mtow_weight').mtow_weight;
    };
    $scope.edit = (localAircraftRegistry: ILocalAircraftRegistry) => { super.edit(systemConfigurationService.convertMtowProperty(localAircraftRegistry, 'mtow_weight')); };
    $scope.update = (localAircraftRegistry: ILocalAircraftRegistry, renewal: string, expiry: string) => this.updateOverride(localAircraftRegistry, renewal, expiry);
    $scope.refreshOverride = () => this.refreshOverride();
   }

  protected edit(localAircraftRegistry: ILocalAircraftRegistry): void {

    const d = new Date(localAircraftRegistry.coa_date_of_renewal);
    this.$scope.control.setUTCStartDate(d);

    let e = new Date(localAircraftRegistry.coa_date_of_expiry);
    this.$scope.control.setUTCEndDate(e);

    super.edit(localAircraftRegistry);
  }

  protected createOverride(localAircraftRegistry: ILocalAircraftRegistry, renewal: string, expiry: string): ng.IPromise<ILocalAircraftRegistry> {
    localAircraftRegistry.coa_date_of_renewal = renewal;
    localAircraftRegistry.coa_date_of_expiry = expiry;
    return super.create(localAircraftRegistry).then(() => this.$scope.control.reset());
  }

  protected updateOverride(localAircraftRegistry: ILocalAircraftRegistry, renewal: string, expiry: string): ng.IPromise<ILocalAircraftRegistry> {
    localAircraftRegistry.coa_date_of_renewal = renewal;
    localAircraftRegistry.coa_date_of_expiry = expiry;
    return super.update(localAircraftRegistry, localAircraftRegistry.id);
  }

  protected addDate(localAircraftRegistry: ILocalAircraftRegistry): void {

    if (localAircraftRegistry.coa_date_of_renewal !== null) {
      let d = new Date(localAircraftRegistry.coa_date_of_renewal);
      this.$scope.control.setUTCStartDate(d);
    }

    if (localAircraftRegistry.coa_date_of_expiry !== null) {
      let e = new Date(localAircraftRegistry.coa_date_of_expiry);
      this.$scope.control.setUTCEndDate(e);
    }
  }

  private getFilterParameters(): void {
    this.$scope.filterParameters = {
      search: this.$scope.search,
      page: this.$scope.pagination ? this.$scope.pagination.number : 0,
      filter: this.$scope.registrationFilter
    };
  }

  private refreshOverride(): void {
    this.getFilterParameters();
    super.list(this.$scope.filterParameters, this.$scope.getSortQueryString());
  }
}
