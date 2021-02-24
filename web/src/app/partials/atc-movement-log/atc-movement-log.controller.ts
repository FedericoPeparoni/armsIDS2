// controllers
import { CRUDFileUploadController } from '../../angular-ids-project/src/helpers/controllers/crud-file-handler/crud-file-handler.controller';

// interfaces
import { IAircraftType } from '../aircraft-type-management/aircraft-type-management.interface';
import { IATCMovementLogScope } from './atc-movement-log.interface';

// services
import { AircraftRegistrationService } from '../aircraft-registration/service/aircraft-registration.service';
import { AtcMovementLogService } from './service/atc-movement-log.service';
import { SaveLocalTemplateService } from '../../angular-ids-project/src/components/services/saveLocalTemplate/saveLocalTemplate.service';
import { CustomDate } from '../../angular-ids-project/src/components/services/customDate/customDate.service';

export class AtcMovementLogController extends CRUDFileUploadController {

  /* @ngInject */
  constructor(protected $scope: IATCMovementLogScope, private atcMovementLogService: AtcMovementLogService,
    protected aircraftRegistrationService: AircraftRegistrationService, protected $uibModal: ng.ui.bootstrap.IModalService,
    private saveLocalTemplateService: SaveLocalTemplateService, private customDate: CustomDate
  ) {

    super($scope, atcMovementLogService, $uibModal, 'File Name');
    super.setup();

    this.$scope.customDate = this.customDate.returnDateFormatStr(false);

    $scope.flightTypes = this.service.listFlightTypes();
    $scope.flightCategories  = this.service.listFlightCategories();

    $scope.refreshOverride = () => this.refreshOverride();

    this.saveLocalTemplate();

    $scope.$watchGroup(['search', 'control'], () => this.getFilterParameters());

    $scope.resolveAircraftType = (regNum: string, dateOfContact: Date) => this.resolveAircraftType(regNum, dateOfContact);
  }

  private saveLocalTemplate(): void {
    let filterPairs = [['search', 'search']];
    this.saveLocalTemplateService.saveLocalTemplate(this.$scope, 'atc-movement-log', filterPairs);
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
      'search': this.$scope.search,
      'start': startDate,
      'end': endDate,
      'page': this.$scope.pagination ? this.$scope.pagination.number : 0
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

  /**
   * Find aircraft type by registration number and date of contact.
   * 
   * @param regNum registration number for movement log
   * @param dateOfContact date of contact for movement log
   */
  private resolveAircraftType(regNum: string, dateOfContact: Date): void {
    this.aircraftRegistrationService.getAircraftType(regNum, dateOfContact)
      .then((aircraftType: IAircraftType) => {
        if (aircraftType) {
          this.$scope.editable.aircraft_type = aircraftType.aircraft_type;
        }
      });
  }
}
