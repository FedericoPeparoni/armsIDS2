// interfaces
import { IRouteSegment } from '../route-segments/route-segments.interface';
import { ISpringPageableParams } from '../../angular-ids-project/src/helpers/services/crud.service';
import { ICRUDScope } from '../../angular-ids-project/src/helpers/services/crud.interface';
import { IAircraftTypeMinimal } from '../aircraft-type-management/aircraft-type-management.interface';
import { IFlightMovementCategory } from '../../partials/flight-movement-category/flight-movement-category.interface';
import { ICurrency } from '../../partials/currency-management/currency-management.interface';
import { IStaticType } from '../../angular-ids-project/src/helpers/interfaces/static-data-type.interface';

export interface IFlightMovementFilters extends ISpringPageableParams {
  status: string;
  issue: string;
  type: string;
  start: string;
  end: string;
  search: string;
  iata: string;
  invoice: string;
  fmStatusFilter: string;
}

export interface IFlightMovementSpring {
   content: Array<IFlightMovement>;
   number: number;
}

export interface IFlightMovement {
  id: number;
  actual_departure_time: number;
  actual_mtow: number;
  aircraft_type: string;
  aerodrome_charges: number;
  approach_charges: number;
  arrival_ad: string;
  arrival_time: number;
  associated_aircraft: string;
  associated_account: string;
  associated_account_black_listed_indicator: boolean;
  associated_account_black_listed_override: boolean;
  associated_account_id: number;
  average_mass_factor: number;
  atc_crossing_distance: number;
  atc_crossing_distance_cost: number;
  crew_members: number;
  date_of_flight: any;
  delta_flight: boolean;
  dep_ad: string;
  dep_time: string;
  dest_ad: string;
  domestic_passenger_charges: number;
  enroute_charges: number;
  enroute_charges_basis: string;
  enroute_charges_status: string;
  enroute_invoice_id: number;
  entry_time: string;
  exempt_aerodrome_charges: number;
  exempt_approach_charges: number;
  exempt_dep_charges: number;
  exempt_domestic_passenger_charges: number;
  exempt_enroute_charges: number;
  exempt_international_passenger_charges: number;
  exempt_late_departure_charges: number;
  exempt_late_arrival_charges: number;
  exempt_parking_charges: number;
  exit_time: string;
  flight_id: string;
  flight_level: string;
  flight_notes: string;
  flight_rules: string;
  flight_type: string;
  flight_category_nationality: string;
  flight_category_scope: string;
  flight_category_type: string;
  flightmovement_category_name: string;
  fpl_crossing_cost: number;
  fpl_crossing_distance: number;
  fpl_route: string;
  initial_fpl_data: string;
  international_passenger_charges: number;
  item18_dep: string;
  item18_dest: string;
  item18_reg_num: string;
  item18_rmk: string;
  item18_status: string;
  item18_aircraft_type: string;
  item18_operator: string;
  late_arrival_charges: number;
  late_departure_charges: number;
  manually_changed_fields: string;
  movement_type: string;
  nominal_crossing_cost: number;
  nominal_crossing_distance: number;
  other_charges_status: string;
  other_info: string;
  other_invoice_id: number;
  parking_charges: number;
  parking_time: number;
  passenger_charges_status: string;
  passenger_invoice_id: number;
  passengers_chargeable_domestic: number;
  passengers_chargeable_intern: number;
  passengers_joining_adult: number;
  passengers_transit_adult: number;
  passengers_child: number;
  prepaid_amount: number;
  radar_crossing_cost: number;
  radar_crossing_distance: number;
  resolution_errors: Array<string>;
  source: string;
  spatia_fpl_object_id: number;
  status: string;
  total_charges: number;
  tower_crossing_distance: number;
  tower_crossing_distance_cost: number;
  user_crossing_distance: number;
  user_crossing_distance_cost: number;
  wake_turb: string;
  estimated_elapsed_time: string;
  radar_route_text: string;
  atc_log_route_text: string;
  tower_log_route_text: string;
  tasp_charge: number;
  cruising_speed_or_mach_number: string;
  adhoc_charge_required: boolean;
  flightCategory: number;
  enroute_invoice_currency: ICurrency;
  enroute_result_currency: ICurrency;
  arriving_pax_domestic_airport: number;
  landing_pax_domestic_airport: number;
  transfer_pax_domestic_airport: number;
  departing_pax_domestic_airport: number;
  arriving_child_domestic_airport: number;
  landing_child_domestic_airport: number;
  transfer_child_domestic_airport: number;
  departing_child_domestic_airport: number;
  exempt_arriving_pax_domestic_airport: number;
  exempt_landing_pax_domestic_airport: number;
  exempt_transfer_pax_domestic_airport: number;
  exempt_departing_pax_domestic_airport: number;
  loaded_goods: number;
  discharged_goods: number;
  loaded_mail: number;
  discharged_mail: number;
  status_notes: string;
  extended_hours_surcharge_currency: ICurrency;
  extended_hours_surcharge: number;
  tasp_charge_currency: ICurrency;
}

export interface IFlightMovementScope extends ICRUDScope {
  showFlightMovementOnMap: (flightMovement: IFlightMovement) => void;
  reconcileAll: (ids: Array<number>) => void;
  recalculateAll: (ids: Array<number>) => void;
  allowInvoice: (ids: Array<number>) => boolean;
  checkboxesSelected: () => void;
  selectedFlightMovements: Array<boolean>;
  open: () => void;
  countSelected: () => void;
  modal: any; // todo: change to ng.ui.bootstrap.IModalService once ui-angular-bootstrap typings issue resolved
  manual: Object;
  markManualInputs: (flightMovement: IFlightMovement) => void;
  getRouteSegments: (IFlightMovement: IFlightMovement) => any;
  addFlightMovementToPlan: (ids: Array<Number>) => void;
  allRouteSegments: Array<IRouteSegment>;
  specificRouteSegments: Array<IRouteSegment>;
  totalDistance: number;
  totalCost: number;
  isAllowedInvoice: boolean;
  numberOfSelectedMovements: number;
  dynamicRoute: string;
  accountId: number;
  refreshOverride: (reset?: boolean, sort?: string) => angular.IPromise<any>;
  distanceUnitOfMeasure: string;
  showDuplicateMissing: boolean;
  showAllFlights: boolean;
  showActualDepDestAd: boolean;
  showActualDepAd: boolean;
  showActualDestAd: boolean;
  fmStatusFilter: string;
  locateDuplicateMissingFlights: (enable: boolean) => void;
  toggleAllFlights: () => void;
  aircraftTypesList: Array<IAircraftTypeMinimal>;
  editable: IFlightMovement;
  isKCAA: boolean;
  fmGenerateError: number;
  flightCategories: Array<IFlightMovementCategory>;
  shouldShowCharge: (chargeType: string) => boolean;
  markZeroFlightCostsAsPaid: string;
  markedAsPaid: boolean;
  incompleteReasons: Array<IStaticType>;
}