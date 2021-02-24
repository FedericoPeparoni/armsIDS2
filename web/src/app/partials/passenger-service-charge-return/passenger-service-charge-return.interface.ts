// services
import { ISpringPageableParams } from '../../angular-ids-project/src/helpers/services/crud.service';

// interfaces
import { IStartEndDates } from '../../angular-ids-project/src/components/dateRange/dateRange.interface';
import { IAccount } from '../accounts/accounts.interface';
import { IExtendableError } from '../../angular-ids-project/src/helpers/interfaces/restangularError.interface';

export interface IPassengerServiceChargeReturn {
  id?: number;
  flight_id: string;
  day_of_flight: string;
  departure_time: string;
  transit_passengers: number;
  joining_passengers: number;
  children: number;
  chargeable_itl_passengers: number;
  chargeable_domestic_passengers: number;
  document_filename: string;
  document_filename2: string;
  mime_type: string;
  document: any;
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
  account: IAccount;
  account_id: number;
}

export interface IPassengerServiceChargeReturnScope {
  pattern: string;
  executeReconciliation: Function;
  refresh: Function;
  edit: Function;
  control: IStartEndDates;
  dom_pay: number;
  itl_pay: number;
  category_name: string;
  uploadNew: Function;
  editable: IPassengerServiceChargeReturn;
  uploadJob: any;
  extendedPassengerInformation: boolean;
  extendedCargoInformation: boolean;
  cargoDisplayUnits: string;
  maxCargo: number;
  pagination: ISpringPageableParams;
  getSortQueryString: Function;
  search: string;
  chargeReturnFilter: string;
  filterParameters: object;
  $watchGroup: any;
  error: IExtendableError;
  pageSize: number;
}
