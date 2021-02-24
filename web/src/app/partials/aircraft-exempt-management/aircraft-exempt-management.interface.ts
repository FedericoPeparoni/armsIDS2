import { IAircraftType } from '../aircraft-type-management/aircraft-type-management.interface';
import { ISpringPageableParams } from '../../angular-ids-project/src/helpers/services/crud.service';

export interface IAircraftExemptType {
  id?: number;
  aircraft_type: number;
  enroute_fees_exempt: number;
  late_arrival_fees_exempt: number;
  late_departure_fees_exempt: number;
  parking_fees_exempt: number;
  flight_notes: string;
  approach_fees_exempt: number;
  aerodrome_fees_exempt: number;
  international_pax: number;
  domestic_pax: number;
  extended_hours_fees_exempt: number;
}

export interface IAircraftExemptManagementServiceScope {
  aircraftTypeList: Array<IAircraftType>;
  shouldShowCharge: Function;
  filterParameters: object;
  textFilter: string;
  pagination: ISpringPageableParams;
  refresh: Function;
  getSortQueryString: () => string;
}
