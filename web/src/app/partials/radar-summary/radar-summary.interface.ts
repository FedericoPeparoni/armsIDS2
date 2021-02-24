// interfaces
import { ISpringPageableParams } from '../../angular-ids-project/src/helpers/services/crud.service';
import { IStartEndDates } from '../../angular-ids-project/src/components/dateRange/dateRange.interface';

export interface IRadarSummaryType {
  id?: number;
  date: string;
  flight_identifier: string;
  day_of_flight: string;
  departure_time: string;
  registration: string;
  aircraft_type: string;
  departure_aero_drome: string;
  destination_aero_drome: string;
  route: string;
  fir_entry_point : string;
  fir_entry_time: string;
  fir_entry_flight_level?: string;
  fir_exit_point: string;
  fir_exit_time: string;
  fir_exit_flight_level?: string;
  flight_rule: string;
  flight_travel_category: string;
  cruising_speed?: string;
  flight_level?: string;
  wake_turb?: string;
  entry_coordinate: string;
  exit_coordinate: string;
}

export interface IRadarSummaryScope {
  filterParameters: { 'search': string; 'start': string; 'end': string; 'page': number; };
  format: string | number;
  pattern?: string;
  search: string;
  control: IStartEndDates;
  pagination: ISpringPageableParams;
  editable: IRadarSummaryType;
  refreshOverride: () => void;
  getSortQueryString: () => string;
  customDate: string;
  $watchGroup: any;
  flightLevelRequired: boolean;
}

export interface IRadarSummaryFilters extends ISpringPageableParams {
  start: string;
  end: string;
  search: string;
}
