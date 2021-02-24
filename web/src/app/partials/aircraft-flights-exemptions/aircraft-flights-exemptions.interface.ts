import { ISpringPageableParams } from '../../angular-ids-project/src/helpers/services/crud.service';

export interface IAircraftFlightsExemptions {
  id?: number;
  aircraft_registration: string;
  flight_id: string;
  enroute_fees_exempt: number;
  approach_fees_exempt: number;
  aerodrome_fees_exempt: number;
  late_arrival_fees_exempt: number;
  late_departure_fees_exempt: number;
  parking_fees_exempt: number;
  international_pax: number;
  domestic_pax: number;
  extended_hours: number;
  flight_notes: string;
  exemption_start_date: string;
  exemption_end_date: string;
}

export interface IAircraftFlightsExemptionsScope {
  shouldShowCharge: Function;
  refreshOverride: Function;
  createOverride: Function;
  updateOverride: Function;
  edit: Function;
  control: any;
  editable: IAircraftFlightsExemptions;
  textFilter: string;
  pagination: ISpringPageableParams;
  getSortQueryString: () => string;
  customDate: string;
  filterParameters: object;
}
