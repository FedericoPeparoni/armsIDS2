// interface
import { IAerodrome } from '../aerodromes/aerodromes.interface';
import { IAircraftType } from '../aircraft-type-management/aircraft-type-management.interface';
import { IStartEndDates } from '../../angular-ids-project/src/components/dateRange/dateRange.interface';
import { IMtowType } from '../mtow/mtow.interface';
import { IBillingCentre } from '../billing-centre-management/billing-centre-management.interface';
import { IAccountMinimal } from '../accounts/accounts.interface';
import { IStaticType } from '../../angular-ids-project/src/helpers/interfaces/static-data-type.interface';
import { IExtendableError } from '../../angular-ids-project/src/helpers/interfaces/restangularError.interface';

export interface IAirTrafficData {
  start_date: string;
  end_date: string
  aerodromes: string;
  aircraft_types: string;
  mtow_categories: string;
  billing_centres: string;
  accounts: string;
  temporal_group: string;
  flight_types: Array<string>;
  flight_scopes: Array<string>;
  flight_categories: Array<string>;
  flight_rules: Array<string>;
  flight_levels: string;
  routes: string;
  sort: string;
  group_by: string;
  fiscal_year: boolean;
}

export interface IAirTrafficDataTemplate extends IAirTrafficData {
  id: number;
  name: string;
  value: string;
  revenue_category: string;
  chart_type: string;
  mtow_factor_class: string;
}

export interface IAirTrafficResponse {
  aircraft_type: string;
  bill_aerodromes: string;
  count: number;
  date: string;
  flight_categorty: string;
  flight_levels: string;
  flight_rules: string;
  flight_scope: string;
  flight_type: string;
  movement_type: string;
  mtow_category: number;
  name: string;
  route: string;
  sum_aerodrome_charges: number;
  sum_approach_charges: number;
  sum_domestic_passenger_charges: number;
  sum_enroute_chargers: number;
  sum_international_passenger_charges: number;
  sum_late_arrival_charges: number;
  sum_late_departure_charges: number;
  sum_passengers_chargeable_domestic: number;
  sum_passengers_chargeable_intern: number;
  sum_tasp_charge: number;
  sum_total_charges: number;
}

export interface IAirTrafficDataScope extends ng.IScope {
  aerodromesList: Array<IAerodrome>;
  aerodromesModel: Array<IAerodrome>;
  aircraftTypeList: Array<IAircraftType>;
  aircraftTypeModel: Array<IAircraftType>;
  billingCentresList: Array<IBillingCentre>;
  billingCentresModel: Array<IBillingCentre>;
  accountsList: Array<IAccountMinimal>;
  accountsModel: Array<IAccountMinimal>;
  mtowList: Array<IMtowType>;
  mtowModel: Array<IMtowType>;
  routeList: Array<Object>;
  routeModel: Array<string>;
  flightLevelList: Array<Object>;
  flightLevelModel: Array<string>;
  sortList: Array<IStaticType>;
  groupByModel: Array<IStaticType>;
  control: IStartEndDates;
  generate: Function;
  editable: IAirTrafficData;
  datapoints: Array<Object>;
  selected: Array<Object>;
  datacolumns: Array<Object>;
  data: IAirTrafficResponse[]; // used for exporting to csv/pdf
  datax: Object;
  addAerodromeToList: Function;
  addAircraftToList: Function;
  addBillingCentreToList: Function;
  addMTOWToList: Function;
  addRouteToList: Function;
  addAccountToList: Function;
  addFlightLevelToList: Function;
  addGroupToList: Function;
  createSortList: Function;
  addSortToList: Function;
  revenue_category: string;
  display_value: string;
  ylabel: Array<Object>;
  displayValues: Array<any>;
  groupByValues: Array<IStaticType>;
  list: Array<string>;
  noData: boolean;
  error: IExtendableError;
  cashAccounts: IAccountMinimal;
  newSortIndexes: Array<Object>;
  name: string;
  showMTOWList: Function;

  chartHeight: number;
}
