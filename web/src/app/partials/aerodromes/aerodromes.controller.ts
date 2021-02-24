// controllers
import { CRUDFormControllerUserService } from '../../angular-ids-project/src/helpers/controllers/crud-form-user-service/crud-form-user-service.controller';

// interfaces
import { IAerodromesScope } from './aerodromes.interface';
import { IAerodrome, IAerodromeServiceType } from './aerodromes.interface';

// services
import { AerodromesService } from './service/aerodromes.service';
import { SystemConfigurationService } from '../system-configuration/service/system-configuration.service';

// constants
import {SysConfigConstants} from '../system-configuration/system-configuration.constants';
import {CoordinateLabels, CoordinateValidation} from './service/aerodromes.constants';

// classes
import { Coordinates } from '../../angular-ids-project/src/components/convertCoordinates/convertCoordinates.class';

export class AerodromesController extends CRUDFormControllerUserService {

  /* @ngInject */
  constructor(protected $scope: IAerodromesScope,
              private aerodromesService: AerodromesService,
              private systemConfigurationService: SystemConfigurationService,
              private $filter: ng.IFilterService) {
    super($scope, aerodromesService);
    super.setup();
    $scope.checkIfDefaultSet = (aerodrome: IAerodrome) => $scope.hasDefault = this.checkIfDefaultSet(aerodrome);

    $scope.edit = (editable: IAerodrome) => this.editOverride(editable);

    // formats: DD.dddd or DD°MM′SS″L or DD MM SS L
    $scope.latitudeRegex = <any>CoordinateValidation.LATREGEX;
    $scope.longitudeRegex = <any>CoordinateValidation.LONREGEX;

    // get the current coordinate formant from system configurations
    $scope.format = systemConfigurationService.getValueByName(<any>SysConfigConstants.COORDINATE_FORMAT);

    // setup functions to get the lat/lon label
    $scope.getLatLabel = () => this.getLatLabel();
    $scope.getLonLabel = () => this.getLonLabel();

    $scope.refreshOverride = () => this.refreshOverride();
    this.getFilterParameters();

    // set scope value for require external system identifier
    $scope.requireExternalSystemId = this.requireExternalSystemId();

    aerodromesService.getAerodromeServiceTypes().then((serviceTypes: IAerodromeServiceType[]) => $scope.aerodromeServiceTypes = serviceTypes);
  }

  /**
   * @param  {IAerodrome} aerodrome
   * @returns boolean
   * This prevents selecting more than one aerodrome
   * as a default aerodrome for a billing centre
   */
  private checkIfDefaultSet(aerodrome: IAerodrome): boolean {
    for (let item of this.$scope.list) {
      if (item.billing_center.id === aerodrome.billing_center.id && item.is_default_billing_center && aerodrome.id === null) {
        this.$scope.editable.is_default_billing_center = false;
        return true;
      }
    }
    return false;
  }

  /**
   * This will display the coordinates in the form as
   * Degrees-Minutes-Seconds, provided the system
   * is configured to DMS
   *
   * @param  {IAerodrome} editable
   * @returns void
   */
  private editOverride(editable: IAerodrome): void {
    const coordinates = new Coordinates();
    let editableCopy = angular.copy(editable);

    if (this.$scope.format === <any>SysConfigConstants.DEGREES_MINUTES_SECONDS) {
      editableCopy.geometry.coordinates[0] = <any>coordinates.toLon(editableCopy.geometry.coordinates[0], 'dms', 0);
      editableCopy.geometry.coordinates[1] = <any>coordinates.toLat(editableCopy.geometry.coordinates[1], 'dms', 0);
    } else {
      // else display lon / lat
      // with 5 decimal points
      editableCopy.geometry.coordinates[0] = this.$filter('number')(editableCopy.geometry.coordinates[0], 5);
      editableCopy.geometry.coordinates[1] = this.$filter('number')(editableCopy.geometry.coordinates[1], 5);
    }
    super.edit(editableCopy);
  }

  /**
   * Get required external system id flag from system configuration.
   *
   * @returns boolean true if required
   */
  private requireExternalSystemId(): boolean {
    return this.systemConfigurationService
      .getBooleanFromValueByName(<any>SysConfigConstants.REQUIRE_AERODROME_EXTERNAL_SYSTEM_ID);
  }

  // return the latitude label,
  // based on the coordinate format
  private getLatLabel(): string {
    return this.$scope.format === <any>SysConfigConstants.DEGREES_MINUTES_SECONDS ?
      <any>CoordinateLabels.DMSLAT : <any>CoordinateLabels.DECIMALLAT;
  }

  // return the longitude label,
  // based on the coordinate formant
  private getLonLabel(): string {
    return this.$scope.format === <any>SysConfigConstants.DEGREES_MINUTES_SECONDS ?
      <any>CoordinateLabels.DMSLON : <any>CoordinateLabels.DECIMALLON;
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
