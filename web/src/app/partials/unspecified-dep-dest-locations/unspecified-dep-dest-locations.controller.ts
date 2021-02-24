// controllers
import { CRUDFormControllerUserService } from '../../angular-ids-project/src/helpers/controllers/crud-form-user-service/crud-form-user-service.controller';

// interfaces
import { IUnspecifiedDepartureLocationScope, IUnspecifiedDepartureLocationType } from './unspecified-dep-dest-locations.interface';

// services
import { UnspecifiedDepDestLocationsService } from './service/unspecified-dep-dest-locations.service';
import { SystemConfigurationService } from '../system-configuration/service/system-configuration.service';

// constants
import { SysConfigConstants } from '../system-configuration/system-configuration.constants';
import {CoordinateLabels, CoordinateValidation} from '../aerodromes/service/aerodromes.constants';

// classes
import { Coordinates } from '../../angular-ids-project/src/components/convertCoordinates/convertCoordinates.class';

export class UnspecifiedDepDestLocationsController extends CRUDFormControllerUserService {

  /* @ngInject */
  constructor(protected $scope: IUnspecifiedDepartureLocationScope,
    private unspecifiedDepDestLocationsService: UnspecifiedDepDestLocationsService,
    private systemConfigurationService: SystemConfigurationService,
    private $filter: ng.IFilterService, $translate: angular.translate.ITranslateService) {

    // define and setup super class
    super($scope, unspecifiedDepDestLocationsService);
    super.setup();

    $scope.country = this.systemConfigurationService.getValueByName(<any>SysConfigConstants.ANSP_COUNTRY_CODE);

    // expose necessary method to scope
    $scope.edit = (editable: IUnspecifiedDepartureLocationType) => this.editOverride(editable);
    $scope.refresh = () => this.refresh();
    $scope.reset = () => this.reset();
    this.getFilterParameters();

    // translate
    $scope.booleanList = [
      { n: $translate.instant('True'), v: true },
      { n: $translate.instant('False'), v: false }
    ];

    // formats: DD.dddd or DD°MM′SS″L or DD MM SS L
    $scope.latitudeRegex = <any>CoordinateValidation.LATREGEX;
    $scope.longitudeRegex = <any>CoordinateValidation.LONREGEX;

    // get the current coordinate formant from system configurations
    $scope.format = systemConfigurationService.getValueByName(<any>SysConfigConstants.COORDINATE_FORMAT);

    // setup functions to get the lat/lon label
    $scope.getLatLabel = () => this.getLatLabel();
    $scope.getLonLabel = () => this.getLonLabel();
  }

  protected reset(): void {
    super.reset();
    this.$scope.editable.country_code = this.$scope.defaultCounty;
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

  /**
   * This will display the coordinates in the form as
   * Degrees-Minutes-Seconds, provided the system
   * is configured to DMS
   *
   * @param  {IUnspecifiedDepartureLocationType} editable
   * @returns void
   */
  private editOverride(editable: IUnspecifiedDepartureLocationType): void {
    const coordinates = new Coordinates();
    let editableCopy = angular.copy(editable);

    if (this.$scope.format === <any>SysConfigConstants.DEGREES_MINUTES_SECONDS) {
      editableCopy.latitude = <any>coordinates.toLat(editableCopy.latitude, 'dms', 0);
      editableCopy.longitude = <any>coordinates.toLon(editableCopy.longitude, 'dms', 0);
    } else {
      // else display lon / lat
      // with 5 decimal points
      editableCopy.latitude = this.$filter('number')(editableCopy.latitude, 5);
      editableCopy.longitude = this.$filter('number')(editableCopy.longitude, 5);
    }
    super.edit(editableCopy);
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
}
