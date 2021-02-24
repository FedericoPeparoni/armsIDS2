// controllers
import { CRUDFormControllerUserService } from '../../angular-ids-project/src/helpers/controllers/crud-form-user-service/crud-form-user-service.controller';

// services
import { AircraftUnspecifiedManagementService } from './service/aircraft-unspecified-management.service';
import { AircraftTypeManagementService } from '../aircraft-type-management/service/aircraft-type-management.service';
import { SystemConfigurationService } from '../system-configuration/service/system-configuration.service';

// interface
import { IAircraftTypeSpring, IAircraftType } from '../aircraft-type-management/aircraft-type-management.interface';

// constants
import { SysConfigConstants } from '../system-configuration/system-configuration.constants';

export class AircraftUnspecifiedManagementController extends CRUDFormControllerUserService {

  /* @ngInject */
  constructor(protected $scope: ng.IScope,
    private aircraftUnspecifiedManagementService: AircraftUnspecifiedManagementService,
    private aircraftTypeManagementService: AircraftTypeManagementService,
    private systemConfigurationService: SystemConfigurationService) {

    // define and setup super class
    super($scope, aircraftUnspecifiedManagementService);
    super.setup();

    // expose necessary method to scope
    $scope.edit = (item: IAircraftType) => { super.edit(systemConfigurationService.convertMtowProperty(item, 'mtow')); };
    $scope.refresh = () => this.refresh();
    this.getFilterParameters();
    $scope.getMTOWByAircraftType = (ac: string) => this.getMTOWByAircraftType(ac);

    // expose necessary fields to scope
    $scope.mtowUnitOfMeasure = systemConfigurationService.getValueByName(<any>SysConfigConstants.MTOW_UNIT_OF_MEASURE);
  }

  /**
   * This will add appropriate filters from scope
   * for parent refresh parameters.
   */
  protected refresh(): angular.IPromise<void> {
    this.getFilterParameters();
    return super.list(this.$scope.filterParameters, this.$scope.getSortQueryString());
  }

  private getFilterParameters(): void {
    this.$scope.filterParameters = {
      search: this.$scope.textFilter,
      page: this.$scope.pagination ? this.$scope.pagination.number : 0
    };
  }

  private getMTOWByAircraftType(ac: string): void {
    if (ac !== undefined) {
      this.aircraftTypeManagementService.listAll().then((aircraftType: IAircraftTypeSpring) => {
          for (let i = 0; i < aircraftType.content.length; i++) { // we have to search to see if we can find the right aircraft type
            if (ac === aircraftType.content[i].aircraft_type) {
              this.$scope.editable.mtow = aircraftType.content[i].maximum_takeoff_weight; // update the mtow field
              break;
            } else {
              this.$scope.editable.mtow = null;
            }
          }
      });
    } else {
        this.$scope.editable.mtow = null;
      }
  }
}
