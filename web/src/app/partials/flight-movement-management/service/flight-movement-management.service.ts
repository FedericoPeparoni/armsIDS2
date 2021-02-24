// contants
import { SysConfigConstants } from '../../system-configuration/system-configuration.constants';

// interfaces
import { IFlightMovement, IFlightMovementSpring } from '../flight-movement-management.interface';

// services
import { CRUDService } from '../../../angular-ids-project/src/helpers/services/crud.service';
import { IFlightMovementFilters } from '../flight-movement-management.interface';
import { LocalStorageService } from '../../../angular-ids-project/src/components/services/localStorage/localStorage.service';
import { SystemConfigurationService } from '../../system-configuration/service/system-configuration.service';
import { IStaticType } from '../../../angular-ids-project/src/helpers/interfaces/static-data-type.interface';

// endpoint
export let endpoint: string = 'flightmovements';

export class FlightMovementManagementService extends CRUDService {

  protected restangular: restangular.IService;

  private _mod: IFlightMovement = {
    id: null,
    actual_departure_time: null,
    actual_mtow: null,
    aerodrome_charges: null,
    approach_charges: null,
    aircraft_type: null,
    arrival_ad: null,
    arrival_time: null,
    associated_aircraft: null,
    associated_account: null,
    associated_account_black_listed_indicator: null,
    associated_account_black_listed_override: null,
    associated_account_id: null,
    average_mass_factor: null,
    atc_crossing_distance: null,
    atc_crossing_distance_cost: null,
    crew_members: null,
    date_of_flight: null,
    delta_flight: null,
    dep_ad: null,
    dep_time: null,
    dest_ad: null,
    domestic_passenger_charges: null,
    enroute_charges: null,
    enroute_charges_basis: null,
    enroute_charges_status: null,
    enroute_invoice_id: null,
    entry_time: null,
    exempt_approach_charges: null,
    exempt_dep_charges: null,
    exempt_domestic_passenger_charges: null,
    exempt_enroute_charges: null,
    exempt_international_passenger_charges: null,
    exempt_parking_charges: null,
    exempt_late_departure_charges: null,
    exempt_late_arrival_charges: null,
    exempt_aerodrome_charges: null,
    exit_time: null,
    flight_id: null,
    flight_level: null,
    flight_notes: null,
    flight_rules: null,
    flight_type: null,
    flight_category_nationality: null,
    flight_category_scope: null,
    flight_category_type: null,
    flightmovement_category_name: null,
    fpl_crossing_cost: null,
    fpl_crossing_distance: null,
    fpl_route: null,
    initial_fpl_data: null,
    international_passenger_charges: null,
    item18_dep: null,
    item18_dest: null,
    item18_reg_num: null,
    item18_rmk: null,
    item18_status: null,
    item18_aircraft_type: null,
    item18_operator: null,
    late_arrival_charges: null,
    late_departure_charges: null,
    manually_changed_fields: null,
    movement_type: null,
    nominal_crossing_cost: null,
    nominal_crossing_distance: null,
    other_charges_status: null,
    other_info: null,
    other_invoice_id: null,
    parking_charges: null,
    parking_time: null,
    passenger_charges_status: null,
    passenger_invoice_id: null,
    passengers_chargeable_domestic: null,
    passengers_chargeable_intern: null,
    passengers_joining_adult: null,
    passengers_transit_adult: null,
    passengers_child: null,
    prepaid_amount: null,
    radar_crossing_cost: null,
    radar_crossing_distance: null,
    resolution_errors: null,
    source: 'manual',
    spatia_fpl_object_id: null,
    status: null,
    total_charges: null,
    tower_crossing_distance: null,
    tower_crossing_distance_cost: null,
    user_crossing_distance: null,
    user_crossing_distance_cost: null,
    wake_turb: null,
    estimated_elapsed_time: null,
    radar_route_text: null,
    atc_log_route_text: null,
    tower_log_route_text: null,
    tasp_charge: null,
    cruising_speed_or_mach_number: null,
    adhoc_charge_required: false,
    flightCategory: null,
    enroute_invoice_currency: null,
    enroute_result_currency: null,
    arriving_pax_domestic_airport: null,
    landing_pax_domestic_airport: null,
    transfer_pax_domestic_airport: null,
    departing_pax_domestic_airport: null,
    arriving_child_domestic_airport: null,
    landing_child_domestic_airport: null,
    transfer_child_domestic_airport: null,
    departing_child_domestic_airport: null,
    exempt_arriving_pax_domestic_airport: null,
    exempt_landing_pax_domestic_airport: null,
    exempt_transfer_pax_domestic_airport: null,
    exempt_departing_pax_domestic_airport: null,
    loaded_goods: null,
    discharged_goods: null,
    loaded_mail: null,
    discharged_mail: null,
    status_notes: null,
    extended_hours_surcharge_currency: null,
    extended_hours_surcharge: null,
    tasp_charge_currency: null
  };

  /** @ngInject */
  constructor(
    protected Restangular: restangular.IService, protected $q: angular.IQService,
    protected systemConfigurationService: SystemConfigurationService
  ) {
    super(Restangular, endpoint);
    this.model = angular.copy(this._mod);
  }

  public getAccountFlightMovements(accountId: number): ng.IPromise<any> {
    return this.restangular.all(`${endpoint}/account/${accountId}`).getList();
  }

  // recalculates one or many flight movements
  public recalculate(flightMovements: Array<number>): ng.IPromise<any> {
    return this.restangular.all(`${endpoint}/recalculate`).customPUT(flightMovements);
  }

  // reconciles one or many flight movements
  public reconcile(flightMovements: Array<number>): ng.IPromise<any> {
    return this.restangular.all(`${endpoint}/reconcile`).customPUT(flightMovements);
  }

  // generates invoices for flight movements
  public generateInvoice(flightMovements: Array<number>): ng.IPromise<any> {
    return this.restangular.all(`${endpoint}/generate-invoices`).customPOST(flightMovements);
  }

  // tests to see if a flight movement is complete
  public validate(flightMovement: IFlightMovement): ng.IPromise<any> {
    return this.restangular.all(`${endpoint}/validate`).customPOST(flightMovement);
  }

  // this has to be done because if filters are used, it is a different endpoint
  public list(params?: IFlightMovementFilters, queryString?: string): ng.IPromise<any> {
    if (params &&
        (typeof params.fmStatusFilter !== 'undefined' || typeof params.search !== 'undefined' || typeof params.type !== 'undefined' || typeof params.status !== 'undefined' || typeof params.invoice !== 'undefined'
        || typeof params.issue !== 'undefined' || typeof params.iata !== 'undefined' || (typeof params.start !== 'undefined' && typeof params.end !== 'undefined'))) {

      if (params.search === '') { // for both endpoints they accept `search`, if `search` is empty, just remove it otherwise can cause issues
        delete params.search;
      }

      this.endpoint = `${endpoint}/filters`; // to use filters, it is a different URL endpoint
    }

    return super.list(params, queryString)
      .then((data: Array<IFlightMovement>) => { this.endpoint = endpoint; return data; })
      .catch(() => this.endpoint = endpoint); // always revert endpoint
  }

  // returns an aircraft type by the last registration number in the database (if it exists)
  public getAircraftTypeByLatestRegistrationNumber(registrationNumber: string): ng.IPromise<any> {
    return this.restangular.one(`${endpoint}/aircraftTypeByLatestRegistrationNumber/${registrationNumber}`).get();
  }

  // returns flight movements that are related to the invoice (or `billing_ledger` which it's currently called in the database)
  public findAllFlightMovementsByAssociatedInvoiceId(invoiceId: number, page?: number, size?: number): ng.IPromise<IFlightMovementSpring> {

    // request parameters, always pass 'size' parameter
    const params: any = {
      size: size || LocalStorageService.get(`SystemConfiguration:${SysConfigConstants.ROW_FOR_PAGE}`) || 20
    };

    // add page parameter if supplied
    // the uib-pagination starts at `1` but Spring Framework starts at `0`, therefore we fake it
    if (page && page > 0) {
      params.page = page - 1;
    }

    return this.restangular.one(`${endpoint}/invoices/${invoiceId}`).get(params);
  }

  // returns flight movements that are related to the invoice (or `billing_ledger` which it's currently called in the database)
  public findAllFlightMovementsListByAssociatedInvoiceId(invoiceId: number): ng.IPromise<Array<IFlightMovement>> {
    return this.restangular.one(`${endpoint}/list/invoices/${invoiceId}`).get();
  }

  public getTrackableFields(): Array<string> {
    return ['flight_id', 'item18_reg_num', 'date_of_flight', 'dep_time', 'dep_ad',
      'aircraft_type', 'dest_ad', 'associated_account_id', 'flight_type', 'item18_status',
      'item18_rmk', 'item18_dep', 'item18_dest', 'item18_aircraft_type', 'route', 'arrival_ad', 'arrival_time',
      'parking_time', 'passengers_chargeable_intern', 'passengers_chargeable_domestic', 'passengers_child',
      'elapsed_time', 'cruising_speed_or_mach_number', 'flight_level'];
  }

  // returns distinct dep_ad and dest_ad
  public getDistinctRoutes(): ng.IPromise<any> {
    return this.restangular.one(`${endpoint}/distinctRoutes/`).get();
  }

  // returns distinct flight levels
  public getDistinctFlightLevels(): ng.IPromise<any> {
    return this.restangular.one(`${endpoint}/distinctFlightLevels/`).get();
  }

  // returns distinct registration numbers
  public getDistinctRegNum(): ng.IPromise<any> {
    return this.restangular.one(`${endpoint}/distinctRegNumbers/`).get();
  }

  // when a user deletes a flight movement, the record is not physically deleting but modifying the flight movement status
  public deleteOverride(id: number, reason: string): ng.IPromise<any> {
    return this.restangular.one(`${endpoint}/delete/${id}`).customPUT(reason);
  }

  public showWarning(actionOne: string, actionTwo: string): object {
    const warning: any = {
      data: {
        error: `${actionOne} Reason is required`,
        error_description: `${actionOne} Reason is required for ${actionTwo} a flight movement`
      }
    };
    return { error: warning };
  }

  // gets an account based on FLIGHT_ID
  public getAccountFromIdentifier(identifier: string): ng.IPromise<any> {
    if (identifier) {
      return this.restangular.one(`${endpoint}/accountByIdentifier/${identifier}`).get();
    } else {
      return this.$q.reject('identifier prefix cannot be null or empty');
    }
  }

  // get an account based on item18 operator
  public getAccountFromOperator(operator: string): ng.IPromise<any> {
    return operator
      ? this.restangular.one(`${endpoint}/accountByOperator/${operator}`).get()
      : this.$q.reject('operator cannot be null or empty');
  }

  // gets valid aircraft registration based on REGISTRATION_NUMBER
  public getAircraftRegistrationFromRegNumber(regNumber: string, flightDate: string): ng.IPromise<any> {
    if (regNumber && flightDate) {
      return this.restangular.one(`${endpoint}/aircraftRegistrationByRegNumber/${regNumber}/${flightDate}`).get();
    } else {
      return this.$q.reject('regNumber and flightDate cannot be null or empty');
    }
  }

  /**
   * Mark flight movements as paid, only PENDING zero cost flights will be processed.
   * 
   * @param flightIdList list of flight movements to mark as paid
   */
  public markAsPaid(flightIdList: Array<number>): ng.IPromise<any> {
    return this.Restangular.one(`${endpoint}/mark-as-paid`).customPOST(flightIdList);
  }

  /**
   * Returns all incomplete flight movement reasons. MISSING_PARKING_TIME is only
   * included if system configuration item 'CALCULATE_PARKING_CHARGES' is enabled.
   */
  public getAllIncompleteReasons(): Array<IStaticType> {

    // add all default incomplete reasons
    // ids values must be unique but are only used for ngRepeat trackBy and can be 'number' value
    let result: Array<IStaticType> = [
      { id: 1, name: 'Missing MTOW', value: 'MISSING_MTOW' },
      { id: 2, name: 'No Associated Account', value: 'NO_ASSOCIATED_ACCOUNT' },
      { id: 3, name: 'Unknown Aircraft Type', value: 'UNKNOWN_AIRCRAFT_TYPE' },
      { id: 4, name: 'Zero Length Billable Track', value: 'ZERO_LENGTH_BILLABLE_TRACK' },
      { id: 5, name: 'Passenger Service Charge Incomplete', value: 'MISSING_PASSENGER_SERVICE_CHARGE_RETURN' },
      { id: 6, name: 'Radar Summary Incomplete', value: 'RADAR_SUMMARY_MISSING' },
      { id: 7, name: 'Tower Log Incomplete', value: 'TOWER_LOG_MISSING' },
      { id: 8, name: 'ATC Log Incomplete', value: 'ATC_LOG_MISSING' },
      { id: 9, name: 'Flight Plan Incomplete', value: 'FLIGHT_PLAN_MISSING' },
      { id: 10, name: 'Passenger Domestic Data Incomplete', value: 'MISSING_PASSENGER_DOMESTIC_DATA' },
      { id: 11, name: 'Passenger International Data Incomplete', value: 'MISSING_PASSENGER_INTERNATIONAL_DATA' },
      { id: 12, name: 'Unknown Departure Aerodrome', value: 'UNKNOWN_DEP_AD' },
      { id: 13, name: 'Unknown Destination Aerodrome', value: 'UNKNOWN_DEST_AD' },
      { id: 14, name: 'Expired Or Missing CoA For Small Aircraft', value: 'EXPIRED_OR_MISSING_COA_FOR_SMALL_AIRCRAFT' },
      { id: 15, name: 'Invalid Time Format', value: 'INVALID_TIME_FORMAT' },
      { id: 16, name: 'Invalid Flight Level', value: 'INVALID_FLIGHT_LEVEL' }
    ];

    // only add parking charge if 'CALCULATE_PARKING_CHARGES' is enabled in system configuration
    if (this.systemConfigurationService.shouldShowCharge('parking')) {
      result.push({
        id: 16, name: 'Missing Parking Time', value: 'MISSING_PARKING_TIME'
      });
    }

    return result;
  }
}
