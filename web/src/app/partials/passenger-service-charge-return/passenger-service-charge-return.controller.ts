// interfaces
import { IPassengerServiceChargeReturnScope, IPassengerServiceChargeReturn } from './passenger-service-charge-return.interface';

// controllers
import { CRUDFileUploadController } from '../../angular-ids-project/src/helpers/controllers/crud-file-handler/crud-file-handler.controller';

// services
import { PassengerServiceChargeReturnService } from './service/passenger-service-charge-return.service';
import { SaveLocalTemplateService } from '../../angular-ids-project/src/components/services/saveLocalTemplate/saveLocalTemplate.service';
import { SystemConfigurationService } from '../system-configuration/service/system-configuration.service';

// constants
import { SysConfigConstants } from '../system-configuration/system-configuration.constants';
import { IExtendableError, IError } from '../../angular-ids-project/src/helpers/interfaces/restangularError.interface';

export class PassengerServiceChargeReturnController extends CRUDFileUploadController {

  /* @ngInject */
  constructor(protected $scope: IPassengerServiceChargeReturnScope, private passengerServiceChargeReturnService: PassengerServiceChargeReturnService,
    protected $uibModal: ng.ui.bootstrap.IModalService, private saveLocalTemplateService: SaveLocalTemplateService,
    private systemConfigurationService: SystemConfigurationService) {
    super($scope, passengerServiceChargeReturnService, $uibModal, 'File Name');
    super.setup();

    $scope.uploadNew = (item: IPassengerServiceChargeReturn, id?: number) => this.uploadNew(item, id);

    this.saveLocalTemplate();
    $scope.$watchGroup(['search', 'chargeReturnFilter'], () => this.getFilterParameters());

    $scope.cargoDisplayUnits = systemConfigurationService.getValueByName(<any>SysConfigConstants.CARGO_DISPLAY_UNITS);
    $scope.extendedPassengerInformation = this.systemConfigurationService.getBooleanFromValueByName(<any>SysConfigConstants.EXTENDED_PSCR_PASSENGER_INFORMATION_SUPPORT);
    $scope.extendedCargoInformation = this.systemConfigurationService.getBooleanFromValueByName(<any>SysConfigConstants.EXTENDED_PSCR_CARGO_INFORMATION_SUPPORT);
    $scope.pageSize = this.systemConfigurationService.getValueByName(<any>SysConfigConstants.ROW_FOR_PAGE);

    $scope.edit = (item: IPassengerServiceChargeReturn) =>
      super.edit(systemConfigurationService.convertCargoProperty(item, 'loaded_goods,discharged_goods,loaded_mail,discharged_mail'));

    $scope.refresh = () => this.refreshOverride();
  }

  private saveLocalTemplate(): void {
    let filterPairs = [['search', 'search'], ['chargeReturnFilter', 'filter-orphan-returns']];
    this.saveLocalTemplateService.saveLocalTemplate(this.$scope, 'passenger-service-charge-return', filterPairs);
  }

  private uploadNew(item: IPassengerServiceChargeReturn, id?: number): void {
    const path = [];

    item.day_of_flight = new Date(item.day_of_flight).toISOString().replace('00.000Z', '00Z');
    item.document_filename = item.document_filename2;
    // sending id, instead of account, as part of multipart form
    if (item.account && item.account.id) {
      item.account_id = item.account.id;
    }

    if (id) {
      path.push(id);
    } else {
      path.push('new');
    }

    this.upload('PUT', path, item, null, null, true).then(() => {
      this.$scope.uploadJob = null;
      this.reset();
      this.refreshOverride();
    }).catch((error: IError) => {
      this.$scope.error = <IExtendableError>{};
      this.$scope.error.error = {data: error};
    });
  }

  private getFilterParameters(): void {
    this.$scope.filterParameters = {
      'search': this.$scope.search,
      'page': this.$scope.pagination ? this.$scope.pagination.number : 0,
      'filter-orphan-returns': this.$scope.chargeReturnFilter
    };
  }

  private refreshOverride(): void {
    this.getFilterParameters();
    super.list(this.$scope.filterParameters, this.$scope.getSortQueryString());
  }
}
