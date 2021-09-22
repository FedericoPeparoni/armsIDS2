// constants
import { MapFlyToEntityType } from '../map/map.constants';

// controllers
import { CRUDFormControllerUserService } from '../../angular-ids-project/src/helpers/controllers/crud-form-user-service/crud-form-user-service.controller';

// interfaces
import { IFlightMovementScope, IFlightMovement } from './flight-movement-management.interface';
import { IRouteSegment, IRouteSegmentSpring } from '../route-segments/route-segments.interface';
import { IAircraftType, IAircraftTypeMinimal } from '../aircraft-type-management/aircraft-type-management.interface';
import { IRestangularResponse } from '../../angular-ids-project/src/helpers/interfaces/restangularError.interface';
import { IFlightMovementCategory } from '../../partials/flight-movement-category/flight-movement-category.interface';

// services
import { FlightMovementManagementService } from './service/flight-movement-management.service';
import { RouteSegmentsService } from '../route-segments/service/route-segments.service';
import { AviationBillingEngineService } from '../aviation-billing-engine/service/aviation-billing-engine.service';
import { AircraftTypeManagementService } from '../aircraft-type-management/service/aircraft-type-management.service';
import { AircraftUnspecifiedManagementService } from '../aircraft-unspecified-management/service/aircraft-unspecified-management.service';
import { SaveLocalTemplateService } from '../../angular-ids-project/src/components/services/saveLocalTemplate/saveLocalTemplate.service';
import { SystemConfigurationService } from '../system-configuration/service/system-configuration.service';
import { ReportsService } from '../reports/service/reports.service';
import { LocalStorageService } from '../../angular-ids-project/src/components/services/localStorage/localStorage.service';
import { CustomDate } from '../../angular-ids-project/src/components/services/customDate/customDate.service';
import { ServerDatetimeService } from '../../angular-ids-project/src/components/server-datetime/server-datetime.service';
import { FlightMovementCategoryService } from '../../partials/flight-movement-category/service/flight-movement-category.service';
import { FlightType } from '../flight-types/service/flight-types.service';

// constants
import { OrganizationName } from '../../partials/organization/organization.constants';
import { SysConfigConstants } from '../../partials/system-configuration/system-configuration.constants';

// classes
import { Calculation } from '../recalculate-reconcile/calculation';

export class FlightMovementManagementController extends CRUDFormControllerUserService {

  // used to save previous filter and sort state when disabling and reenabling
  private previousFilterSort: any = {};

  /* @ngInject */
  constructor(protected $scope: IFlightMovementScope, private $rootScope: ng.IRootScopeService, private routeSegmentsService: RouteSegmentsService,
    private flightMovementManagementService: FlightMovementManagementService, private aviationBillingEngineService: AviationBillingEngineService,
    private aircraftTypeManagementService: AircraftTypeManagementService, private aircraftUnspecifiedManagementService: AircraftUnspecifiedManagementService,
    private $interval: ng.IIntervalService, private saveLocalTemplateService: SaveLocalTemplateService, $uibModal: any,
    private systemConfigurationService: SystemConfigurationService, private reportsService: ReportsService, $translate: angular.translate.ITranslateService,
    private customDate: CustomDate, private $timeout: ng.ITimeoutService, private serverDatetimeService: ServerDatetimeService,
    private flightMovementCategoryService: FlightMovementCategoryService
  ) {
    super($scope, flightMovementManagementService);
    super.setup({refresh: false});

    this.$scope.customDate = this.customDate.returnDateFormatStr(false);

    $scope.invoiceByFMCategory = this.systemConfigurationService.getBooleanFromValueByName(<any>SysConfigConstants.INVOICE_FM_CATEGORY);
    if ($scope.invoiceByFMCategory) {
      $scope.notification_text = 'Note: Cannot generate an invoice when the selected flights are ' +
    'from multiple accounts, are not pending, or have different flight movement categories';
    } else {
      $scope.notification_text = 'Note: Cannot generate an invoice when the selected flights are ' +
    'from multiple accounts or are not pending';
    }

    $scope.notification_text_mark_as_paid = 'Note: Some of the selected Flight Movements have charges associated. Mark as Paid not allowed.'


    // duplicate|missing flight properties, functions
    $scope.showDuplicateMissing = false;
    $scope.showAllFlights = true;
    $scope.locateDuplicateMissingFlights = (enable: boolean) => this.locateDuplicateMissingFlights(enable);

    flightMovementCategoryService.listAll().then((resp: Array<IFlightMovementCategory>) => {
      $scope.flightCategories = resp;
    });

    // actual dep|dest ad properties, functions, events
    /*
      Use local template incase sort saved, need this in constructor as saveLocalTemplate.service
      will trigger cannedSort watch on tableSort.directive which checks the sort attribute of each
      th element. Even though this is set correctly by saveLocalTemplate.service, tableSort.directive
      is unaware of the change as no digest has finished to update the sort attribute by the time the
      cannedSort watcher is fired.
    */
    $scope.showActualDepDestAd = this.localTemplateQueryStringActualAd();
    $scope.showActualDepAd = $scope.showActualDepDestAd;
    $scope.showActualDestAd = $scope.showActualDepDestAd;
    $scope.$on('table-sort-item-value-changed', (event: angular.IAngularEvent, newValue: string, oldValue: string, isSorted: string) =>
      this.tableSortItemValueChanged(event, newValue, oldValue, isSorted));

    $scope.refreshOverride = (reset?: boolean, sort?: string) => this.refreshOverride(reset, sort);

    $scope.selectedFlightMovements = [];
    $scope.getFlightType = (flightType: string) => FlightType[flightType];
    $scope.showFlightMovementOnMap = (flightMovement: IFlightMovement) => this.showFlightMovementOnMap(flightMovement);
    $scope.checkboxesSelected = () => this.getIdsFromCheckboxes(); // checks if any flightmovements selected and then enables / disables buttons
    $scope.allowInvoice = () => this.allowInvoice(this.getIdsFromCheckboxes());
    $scope.markManualInputs = (flightMovement: IFlightMovement) => this.markManualInputs(flightMovement);
    $scope.addFlightMovementToPlan = () => this.addFlightMovementToPlan(this.getIdsFromCheckboxes()); // get flight movement

    $scope.countSelected = () => {
      let flights = this.getIdsFromCheckboxes();
     let filterFlights =  this.$scope.list.filter((item: IFlightMovement) =>flights.indexOf(item.id)> -1);
          let flagDisableButton = false;
            if(filterFlights.filter((item: IFlightMovement) =>item.status == 'PENDING').filter((item: IFlightMovement)=>item.enroute_charges == 0).length > 0 ){
              flagDisableButton = true;
            }
             else {
              flagDisableButton = false;
             }

      $scope.flagDisableButton = flagDisableButton;
      $scope.numberOfSelectedMovements = $scope.selectedRecords = flights.length;
      $scope.isAllowedInvoice = this.allowInvoice(flights);
      $scope.notAllowedRecalculate = this.allowInvoiceRecalculation(flights);
      $scope.allFlightsSelected = this.areAllFlightsSelected();
    };

    $scope.numberOfSelectedMovements = 0;

    $scope.numberOfDaysOfFlightData = systemConfigurationService.getValueByName(<any>SysConfigConstants.NUMBER_OF_DAYS_OF_FLIGHT_DATA) as number;
    $scope.distanceUnitOfMeasure = systemConfigurationService.getValueByName(<any>SysConfigConstants.DISTANCE_UNIT_OF_MEASURE) as string;
    $scope.isKCAA = systemConfigurationService.getValueByName(<any>SysConfigConstants.ORGANIZATION) === OrganizationName.KCAA;
    $scope.translateResolutionErrors = (resolutionErrors: any) => {
      if (resolutionErrors) {
        return resolutionErrors.map((rE: string) => $translate.instant(rE));
      }
    };

    aircraftTypeManagementService.findAllMinimalReturn().then((data: Array<IAircraftTypeMinimal>) => this.updateAircraftTypeList(data));

    $scope.modal = $uibModal;
    $scope.open = () => this.open();

    $scope.$on('$destroy', (): ng.IAngularEvent => $rootScope.$broadcast('map.toggle', 'Layers Off', []));

    const recalculate = new Calculation('recalculateJob', 'executeSpecificRecalculation', 'cancelSpecificRecalculation', 'getStatusForSpecificRecalculate', null, $scope, aviationBillingEngineService, $interval, serverDatetimeService);
    const reconcile = new Calculation('reconcileJob', 'executeSpecificReconciliation', 'cancelSpecificReconciliation', 'getStatusForSpecificReconciliation', null, $scope, aviationBillingEngineService, $interval, serverDatetimeService);

    // specific recalculate methods
    $scope.executeRecalculation = () => {
      recalculate.executeCalculation(this.getIdsFromCheckboxes().join(','));
      this.resetSelectedFlightMovements();
    };

    $scope.cancelRecalculation = () => recalculate.cancelCalculation();
    recalculate.checkStatusOnPageLoad();

    // specific reconcile methods
    $scope.executeReconciliation = () => reconcile.executeCalculation(this.getIdsFromCheckboxes().join(','));
    $scope.cancelReconciliation = () => reconcile.cancelCalculation();
    $scope.generateInvoice = (accountId: number, flightIdList: Array<number>) => this.generateInvoice(accountId, flightIdList);

    reconcile.checkStatusOnPageLoad();

    // override edit to get sys config distance
    $scope.edit = (fm: IFlightMovement) => this.editOverride(fm);

    $scope.shouldShowCharge = (chargeType: string) => systemConfigurationService.shouldShowCharge(chargeType);
    $scope.incompleteReasons = flightMovementManagementService.getAllIncompleteReasons();

    $scope.fmGenerateError = 0;

    // $timeout called without a delay specified waits for the next digest cycle before firing callback
    $timeout(() => this.saveLocalTemplate());

    $scope.deleteOverride = (id: number, reason: string) => flightMovementManagementService.deleteOverride(id, reason)
      .then(() => this.refreshOverride())
      .catch((error: IRestangularResponse) => {
        this.$scope.error = { error };
      });

    $scope.updateOverride = (flightMovement: IFlightMovement, id: number, undeletionReason: string) => this.updateOverride(flightMovement, id, undeletionReason);

    $scope.showWarning = (actionOne: string, actionTwo: string) => this.$scope.error = flightMovementManagementService.showWarning(actionOne, actionTwo);

    $scope.resetFilters = () => this.resetFilters();

    // use to initialise pagination display
    $scope.selectedRecords = 0;

    $scope.toggleAllFlights = () => this.toggleAllFlights();

    // selected flights should be reset whenever filters are changed
    $scope.$watchGroup([
      'search',
      'flightCategoryFilter',
      'flightMovementStatusFilter',
      'incompleteFlightReason',
      'accountTypeFilter',
      'invoiceStatusFilter',
      'showDuplicateMissing',
      'showActualDepDestAd',
      'control.start.date',
      'control.end.date',
      'showAllFlights',
      'fmStatusFilter'],
      () => {
        this.resetSelectedFlightMovements();
        this.getFilterParameters();
      });

    $scope.$watch('list', () => {
      if ($scope.list) {
        $scope.currentFlightIds = $scope.list.map((flight: IFlightMovement) => flight.id);
      }
    });

    // watch for tasp_charge_currency is set and apply it to the scope variable, remove watch once complete
    let setDefaultTaspCurrency = $scope.$watch('editable.tasp_charge_currency', () => {
      if ($scope.editable.tasp_charge_currency) {
        $scope.defaultTaspCurrency = $scope.editable.tasp_charge_currency;
        setDefaultTaspCurrency();
      }
    });

    $scope.resetSelectedFlightMovements = () => this.resetSelectedFlightMovements();
    this.setDefaultFields();

    $scope.markZeroFlightCostsAsPaid = systemConfigurationService.getValueByName(<any>SysConfigConstants.MARK_ZERO_FLIGHT_COSTS_AS_PAID) as string;
    $scope.markAsPaid = (flightIdList: Array<number>) => this.markAsPaid(flightIdList);
  }

  protected create(flightMovement: IFlightMovement): ng.IPromise<any> {
    return super.create(this.trackManualInputs(flightMovement));
  }

  protected updateOverride(flightMovement: IFlightMovement, id: number, undeletionReason: string): ng.IPromise<any> {
    const originalStatusNotes = flightMovement.status_notes;
    if (undeletionReason) {
      const reason = `${flightMovement.status_notes ? flightMovement.status_notes : ''} Undeleted: ${undeletionReason}; `;
      flightMovement.status_notes = reason.substring(0, Math.min(reason.length, 300));
    }

    return super.update(this.trackManualInputs(flightMovement), id)
      .catch((error: IRestangularResponse) => {
        this.$scope.error = { error };
        flightMovement.status_notes = originalStatusNotes;
    });
  }

  protected reset(): void {
    this.$scope.manual = {};
    super.reset();
    this.setDefaultFields();
  }

  /**
   * Override edit to show the distance based on the system config value.
   *
   * @param fm flight movement editing
   */
  private editOverride(fm: IFlightMovement): void {
    this.$scope.showErrorOnlyOnForm = false;
    const flightMovement = this.systemConfigurationService.convertDistanceProperty(angular.copy(fm), 'user_crossing_distance', this.$scope.distanceUnitOfMeasure);
    super.edit(flightMovement);

    this.$timeout(() => {
      if (flightMovement.source !== 'manual') {
        for (let i in this.$scope.form) {
          if (this.$scope.form[i] && this.$scope.form[i].$name && !this.$scope.form[i].$valid) {
              this.$scope.form[i].$setDirty(); // sets the input as dirty, for scss reasons
              this.$scope.form[i].$setTouched(); // sets the input as touched, for scss reasons
              this.$scope.showErrorOnlyOnForm = true;
          }
        }
      }
    });

  }

  /**
   * Define current Data, Filter, and QueryString for use the next time the page is loaded.
   */
  private saveLocalTemplate(): void {
    let filterPairs = [['search', 'search'], ['flightCategoryFilter', 'type'], ['flightMovementStatusFilter', 'status'],
    ['incompleteFlightReason', 'issue'], ['accountTypeFilter', 'iata'], ['invoiceStatusFilter', 'invoice'],
    ['showDuplicateMissing', 'duplicatesOrMissing'], ['showAllFlights', 'showAllFlights'],
    ['showActualDepDestAd', 'showActualDepDest'], ['fmStatusFilter', 'fmStatusFilter']];

    if (!LocalStorageService.get('flight-movement-managementFilters')) {
      // reset the filters to their default settings
      this.resetFilters();
    } else {
      this.$scope.list = [];
      }

    this.saveLocalTemplateService.saveLocalTemplate(this.$scope, 'flight-movement-management', filterPairs);
  }

  /**
   * Return true if actualDepAd or actualDestAd exists in local QueryString template.
   *
   * @returns true if exists in local QueryString
   */
  private localTemplateQueryStringActualAd(): boolean {
    const queryString: string = LocalStorageService.get('flight-movement-managementQueryString');
    return queryString && (queryString.indexOf('actualDepAd') !== -1 || queryString.indexOf('actualDestAd') !== -1);
  }

  private trackManualInputs(editableFlightMovement: IFlightMovement): IFlightMovement {
    let trackableFields = this.service.getTrackableFields();
    let originalFlightMovement: IFlightMovement;
    let changedFieldsArray: Array<string>;

    for (let flightMovement of this.$scope.list) { // finds the original flight movement
      if (flightMovement.id === editableFlightMovement.id) {
        originalFlightMovement = Object.assign({}, flightMovement);
      }
    }

    if (originalFlightMovement === undefined || originalFlightMovement === null) { // flight movement is being created manually
      editableFlightMovement.manually_changed_fields = '';

      for (let key in editableFlightMovement) {
        if (editableFlightMovement[key] !== null && editableFlightMovement[key] !== '') {
          if (trackableFields.includes(key)) {
            editableFlightMovement.manually_changed_fields += `${key},`;
          }
        }
      }
      editableFlightMovement.manually_changed_fields = editableFlightMovement.manually_changed_fields.slice(0, -1);
      return editableFlightMovement;
    }


    // check NON manually created flights
    changedFieldsArray = this.trackNonManualFlights(originalFlightMovement, editableFlightMovement);

    for (let changedField of changedFieldsArray) { // remove fields that are not trackable
      if (!trackableFields.includes(changedField)) {
        changedFieldsArray.splice(changedFieldsArray.indexOf(changedField), 1);
      }
    }

    if (editableFlightMovement.manually_changed_fields) { // if there are previously changed fields, concat with a set to keep them unique
      let set = new Set([...editableFlightMovement.manually_changed_fields.split(','), ...changedFieldsArray]);
      editableFlightMovement.manually_changed_fields = Array.from(set).join(',');
    } else {
      editableFlightMovement.manually_changed_fields = changedFieldsArray.join(',');
    }

    return editableFlightMovement;
  }

  private markManualInputs({ manually_changed_fields }: IFlightMovement): void {
    let manuallyChangedFields = {};
    let fieldsArray = [];

    if (manually_changed_fields) {
      fieldsArray = manually_changed_fields.split(',');
      for (let field of fieldsArray) {
        manuallyChangedFields[field] = true;
      }
    }

    this.$scope.manual = manuallyChangedFields;
  }

  // tracks modified inputs for non-manual flights
  private trackNonManualFlights(originalFlightMovement: IFlightMovement, editableFlightMovement: IFlightMovement): Array<string> {
    const diff: Object = {};
    for (let key in originalFlightMovement) { // compares diffences, adds to diff object
      if (originalFlightMovement[key] === null && editableFlightMovement[key] === '') { // for not manualy created - check for string type that was changed but that removed (not updated)
        editableFlightMovement[key] = null;
      } else if (originalFlightMovement[key] !== editableFlightMovement[key] && editableFlightMovement[key] !== undefined) {

        // we must make sure that DOF compares with the same time zone
        if (key === 'date_of_flight') {
          if (originalFlightMovement[key].slice(0, 10) !== editableFlightMovement[key].toISOString().slice(0, 10)) {
            diff[key] = editableFlightMovement[key];
          } else {
            continue;
          }
        }
        diff[key] = editableFlightMovement[key];
      }
    }

    return Object.keys(diff);
  }

  /**
   * All selected flights must have a status of pending
   * All selected flights must belong to the same account
   * All selected flights must have same fm category if INVOICE_FM_CATEGORY = 't'
   * @param  {Array<number>} ids
   * @returns boolean
   */
  private allowInvoice(ids: any): boolean {
    const accountSet = new Set();
    const categorySet = new Set();
    let output: boolean = true;

    this.$scope.fmGenerateError = 0;
    this.$scope.fmFlightCategory = null;

    for (let i = 0; i < this.$scope.list.length; i++) {
      if (ids.includes(this.$scope.list[i].id)) {
        if (this.$scope.list[i].status === 'PENDING') {
          accountSet.add(this.$scope.list[i].associated_account_id);
        } else {
          this.$scope.fmGenerateError = 2;

          return false;
        };

        categorySet.add(this.$scope.list[i].flightmovement_category_name);
      };
    };

    if (this.$scope.invoiceByFMCategory && categorySet.size > 1) {
      this.$scope.fmGenerateError = 3;

      output = false;
    };

    if (accountSet.size > 1) {
      if (this.$scope.fmGenerateError === 0) {
        this.$scope.fmGenerateError = 1;
      };

      output = false;
    } else {
      this.$scope.accountId = <number>accountSet.values().next().value;
    };

    // check if 'Invoice by flight movement category' in sys config set to true and
    // flights have a 'pending' status and
    // belong to the same account with the same flight category
    if (this.$scope.invoiceByFMCategory && output && categorySet.size === 1) {
      this.getFmFlightCategory(categorySet.values().next().value.toString());
    }

    return output;
  }

  private getFmFlightCategory(name: string): void {
    const flightCategory = this.$scope.flightCategories.find((element: IFlightMovementCategory) => element.name === name);
    this.$scope.fmFlightCategory = flightCategory ? flightCategory.id : null;
  }

  /**
   * All selected flights must not have a status of DELETED
   * @param  {Array<number>} ids
   * @returns boolean
   */
  private allowInvoiceRecalculation(ids: any): boolean {
    return this.$scope.list
      .filter((flight: IFlightMovement) => ids.includes(flight.id))
      .some((flight: IFlightMovement) => flight.status === 'DELETED');
  }

  private addFlightMovementToPlan(ids: Array<Number>): void {
    let flightMovement: IFlightMovement;
    for (let flight of this.$scope.list) {
      if (ids[0] === flight.id) {
        flightMovement = flight;
        break;
      }
    }

    this.$scope.editable = flightMovement;

    let segmentType = this.findType(flightMovement);

    this.routeSegmentsService.getRouteSegmentsByTypeAndFlightMovementId(flightMovement.id, segmentType) // get segments for movement
      .then((data: IRouteSegmentSpring) => this.$scope.specificRouteSegments = this.parseRouteSegments(data.content, flightMovement), // parse segments
      (error: IRestangularResponse) => this.$scope.specificRouteSegments = []); // this ensures that previous segments will be cleared if there is an error
  }

  private findType(flightMovement: IFlightMovement): string {
    let source: string = flightMovement.source.toUpperCase();
    let type: string = 'SCHED'; // default segment type for manual, aftn, amhs, and spatia flight movements
    this.$scope.dynamicRoute = flightMovement.fpl_route; // default sched route

    if (source === 'RADAR-SUMMARY') {
      type = 'RADAR';
      this.$scope.dynamicRoute = flightMovement.radar_route_text;
    } else if (source === 'ATC-LOG') {
      type = 'ATC';
      this.$scope.dynamicRoute = flightMovement.atc_log_route_text;
    } else if (source === 'TOWER-LOG') {
      type = 'TOWER';
      this.$scope.dynamicRoute = flightMovement.tower_log_route_text;
    }

    return type;
  }

  private open(): void {
    this.$scope.modal.open({
      templateUrl: 'app/partials/flight-movement-management/flight-plan.template.html',
      controller: FlightMovementManagementController,
      windowClass: 'app-modal-window', // css for modal width
      scope: this.$scope,
      appendTo: angular.element(document.querySelector('#flight-plan-modal-holder'))
    });
  }

  private getIdsFromCheckboxes(): Array<number> {
    let keys: string[] = Object.keys(this.$scope.selectedFlightMovements);
    return keys.filter((key: string) => { return this.$scope.selectedFlightMovements[key]; }).map(Number);

  }

  private showFlightMovementOnMap(flightMovement: IFlightMovement): void { // broadcasts a single airspace to be shown on map
    this.$rootScope.$broadcast('map.flyToEntity', MapFlyToEntityType.SINGLE_FLIGHT_MOVEMENT, [flightMovement]);
  }

  private parseRouteSegments(segments: Array<IRouteSegment>, flightMovement: IFlightMovement): Array<IRouteSegment> {
    let flightRouteDestination = flightMovement.dest_ad.toUpperCase() === 'ZZZZ' ? flightMovement.item18_dest : flightMovement.dest_ad;
    let distances: Array<number> = [];
    let costs: Array<number> = [];

    for (let segment of segments) {
      distances.push(segment.segment_length);
      costs.push(segment.segment_cost);
    }

    segments.sort((a: IRouteSegment, b: IRouteSegment) => { return a.segment_number - b.segment_number; }); // sort by segment number ascending

    if (segments.length > 0) {
      segments[segments.length - 1].destination = flightRouteDestination; // add final destination
      for (let i = 0, j = 1; j < segments.length; j++ , i++) {
        segments[i].destination = segments[j].segment_start_label; // add starting label as destination of previous segment
      }
    }

    this.$scope.totalDistance = distances.reduce((a: number, b: number) => a + b, 0);
    this.$scope.totalCost = costs.reduce((a: number, b: number) => a + b, 0);

    return segments;
  }

  private getFilterParameters(reset?: boolean): void {

    // default to true for legacy support when not params provided
    reset = reset === false ? false : true;

    // force start and end date to UTC ISO string
    let startDate: string;
    if (this.$scope.control && this.$scope.control.getUTCStartDate()) {
      startDate = this.$scope.control.getUTCStartDate().toISOString().substr(0, 10);
    }

    let endDate: string;
    if (this.$scope.control && this.$scope.control.getUTCEndDate()) {
      endDate = this.$scope.control.getUTCEndDate().toISOString().substr(0, 10);
    }

    // not sure why we want to reset on refresh.. but remains for legecy support
    if (reset) {
      this.reset();
    }

    this.$scope.filterParameters = {
      'search': this.$scope.search,
      'type': this.$scope.flightCategoryFilter,
      'status': this.$scope.flightMovementStatusFilter,
      'issue': this.$scope.incompleteFlightReason,
      'iata': this.$scope.accountTypeFilter,
      'invoice': this.$scope.invoiceStatusFilter,
      'start': startDate,
      'end': endDate,
      'page': this.$scope.pagination ? this.$scope.pagination.number : 0,
      'duplicatesOrMissing': this.$scope.showDuplicateMissing,
      'showActualDepDest': this.$scope.showActualDepDestAd,
      'showAllFlights': this.$scope.showAllFlights,
      'fmStatusFilter': this.$scope.fmStatusFilter
    };
  }

  /**
   * Calls list with applied filters and sort.
   *
   * @param reset default to true for legecy support
   * @param sort default to scope's getSortQueryString
   */
  private refreshOverride(reset?: boolean, sort?: string): angular.IPromise<any> {

    this.getFilterParameters(reset);

    // default sort to user selected columns if undefined, null, or empty
    if (!sort) {
      sort = this.$scope.getSortQueryString ? this.$scope.getSortQueryString() : '';
    }

    return super.list(this.$scope.filterParameters, sort).then((data: any) => {

      // this is done to prevent flashing of ZZZZ/'actual' aerodrome value when
      // sorting by departure or destination aerodromes
      this.$scope.showActualDepAd = this.$scope.showActualDepDestAd;
      this.$scope.showActualDestAd = this.$scope.showActualDepDestAd;
      this.$scope.allFlightsSelected = this.areAllFlightsSelected();

      // continue promise chaining
      return data;
    });
  }

  /**
   * Generate an Aviation invoice with the status "new"
   */
  private generateInvoice(accountId: number, flightIdList: Array<number>): void {
    this.reportsService.generateAviationInvoiceFM(accountId, flightIdList, this.$scope.fmFlightCategory).then(() => { this.refreshOverride(); this.$scope.invoiceCreated = true; })
      .catch((error: IRestangularResponse) => this.$scope.error = { error });
  }

  /**
   * Disables appropriate filters and fixes sort option if enable is true else reset
   * showAllFlights to true. Finally refresh data table list.
   *
   * @param enable locate duplicate or missing function
   */
  private locateDuplicateMissingFlights(enable: boolean): void {

    if (enable) {

      // save current filter state before clearing and disabling affected fields
      this.previousFilterSort.invoiceStatusFilter = this.$scope.invoiceStatusFilter;
      this.previousFilterSort.flightCategoryFilter = this.$scope.flightCategoryFilter;
      this.previousFilterSort.flightMovementStatusFilter = this.$scope.flightMovementStatusFilter;
      this.previousFilterSort.incompleteFlightReason = this.$scope.incompleteFlightReason;
      this.previousFilterSort.fmStatusFilter = this.$scope.fmStatusFilter;
      this.previousFilterSort.showActualDepDestAd = this.$scope.showActualDepDestAd;

      // clear filter field since they will be disabled in view
      this.$scope.invoiceStatusFilter = null;
      this.$scope.flightCategoryFilter = null;
      this.$scope.flightMovementStatusFilter = null;
      this.$scope.incompleteFlightReason = null;
      this.$scope.fmStatusFilter = null;

      // this is required so that the find duplicate/missing flights feature can work with THRU plans
      this.$scope.showActualDepDestAd = true;

      // set sort to locate duplicate/missing flights (Aircraft)
      // this is overwritten on backend when duplicatesOrMissing param true
      this.$scope.cannedSort = 'sort=item18RegNum,asc&sort=dateOfFlight,asc&sort=depTime,asc';

    } else {

      // resort affect filterfields to previous state
      this.$scope.invoiceStatusFilter = this.previousFilterSort.invoiceStatusFilter;
      this.$scope.flightCategoryFilter = this.previousFilterSort.flightCategoryFilter;
      this.$scope.flightMovementStatusFilter = this.previousFilterSort.flightMovementStatusFilter;
      this.$scope.incompleteFlightReason = this.previousFilterSort.incompleteFlightReason;
      this.$scope.fmStatusFilter = this.previousFilterSort.fmStatusFilter;
      this.$scope.showActualDepDestAd = this.previousFilterSort.showActualDepDestAd;

      // always return cannedSort value back to default
      this.$scope.cannedSort = null;

      // when locate duplicate/missing flights disabled,
      // showAllFlights is redundent and should always be true
      this.$scope.showAllFlights = true;
    }

    this.refreshOverride(false, this.$scope.cannedSort);
  }

  /**
   * When table sort item value changes, updated show actual dep|dest ad flags accordingly.
   *
   * @param event angular event
   * @param newValue new table sort value
   * @param oldValue old table sort value
   * @param isSorted true if table sorted by value
   */
  private tableSortItemValueChanged(event: angular.IAngularEvent, newValue: string, oldValue: string, isSorted: string): void {
    // only set flags immeditality if isSorted is false, otherwise refreshOverride() will handle it when promise returned
    // this is done to prevent column value "flickering" table list refresh required after a table sort value change
    if (!isSorted && (newValue === 'depAd' || newValue === 'actualDepAd')) {
      this.$scope.showActualDepAd = this.$scope.showActualDepDestAd;
    } else if (!isSorted && (newValue === 'destAd' || newValue === 'actualDestAd')) {
      this.$scope.showActualDestAd = this.$scope.showActualDepDestAd;
    }
  }

  /**
   * Update view model with list of aircraft types.
   *
   * @param aircraftTypes list of aircraft types
   */
  private updateAircraftTypeList(aircraftTypes: Array<IAircraftTypeMinimal>): void {
    this.$scope.aircraftTypesList = aircraftTypes;

    // add 'ZZZZ' as a placeholder for item18
    this.$scope.aircraftTypesList.push(<IAircraftType>{
      aircraft_type: 'ZZZZ'
    });
  }

  /**
   * Reset the filters to their default settings:
   * flight movement status - active
   * date filter - the last N days depending on system setting
   */
  private resetFilters(): void {
    this.$scope.search = null;
    this.$scope.flightCategoryFilter = null;
    this.$scope.flightMovementStatusFilter = 'ACTIVE';
    this.$scope.incompleteFlightReason = null;
    this.$scope.accountTypeFilter = null;
    this.$scope.invoiceStatusFilter = null;
    this.$scope.showDuplicateMissing = null;
    this.$scope.showActualDepDestAd = null;
    this.$scope.cannedSort = null;

    this.resetSelectedFlightMovements();

    if (this.$scope.numberOfDaysOfFlightData && this.$scope.control) {
      const startDateFilter = new Date(moment().subtract(this.$scope.numberOfDaysOfFlightData - 1, 'days').format('l'));
      const endDateFilter = new Date(Date.now());

      this.$scope.control.setUTCStartDate(startDateFilter);
      this.$scope.control.setUTCEndDate(endDateFilter);
    }

    this.refreshOverride();
  }

  // select or deselect all flights in page
  private toggleAllFlights(): void {
    const allSelectedInverse = !this.areAllFlightsSelected();

    this.$scope.currentFlightIds.map((id: number) => this.$scope.selectedFlightMovements[id] = allSelectedInverse);

    this.$scope.allFlightsSelected = allSelectedInverse;
    this.$scope.countSelected();
  }

  private areAllFlightsSelected(): boolean {
    return this.$scope.currentFlightIds
      && this.$scope.currentFlightIds.every((id: number) => this.$scope.selectedFlightMovements[id]);
  }

  // deselect selected flights
  private resetSelectedFlightMovements(): void {
    this.$scope.selectedFlightMovements = [];
    this.$scope.numberOfSelectedMovements = this.$scope.selectedRecords = 0;
  }

  private setDefaultFields(): void {
    this.$scope.editable.date_of_flight = moment.utc().startOf('day').toDate();
    if (this.$scope.defaultTaspCurrency && !this.$scope.editable.tasp_charge_currency) {
      this.$scope.editable.tasp_charge_currency = this.$scope.defaultTaspCurrency;
    }
  }

  /**
   * Mark flight movements as paid, only PENDING zero cost flights will be processed.
   *
   * @param flightIdList list of flight movements to mark as paid
   */
  private markAsPaid(flightIdList: Array<number>): void {
    this.flightMovementManagementService.markAsPaid(flightIdList)
      .then(() => { this.refreshOverride(); this.$scope.markedAsPaid = true; })
      .catch((error: IRestangularResponse) => this.$scope.error = { error });
  }



}
