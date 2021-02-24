// interfaces
import { IAerodrome } from '../aerodromes/aerodromes.interface';
import { IStaticType } from '../../angular-ids-project/src/helpers/interfaces/static-data-type.interface';
import { ICRUDScope } from '../../angular-ids-project/src/helpers/services/crud.interface';
import { IStartEndDates } from '../../angular-ids-project/src/components/dateRange/dateRange.interface';
import { ISpringPageableParams } from '../../angular-ids-project/src/helpers/services/crud.service';

export interface IATCMovementLogType {
  id?: number;
  date_of_contact: string;
  registration: string;
  operator_identifier: string;
  route: string;
  flight_id: string;
  aircraft_type: string;
  departure_aerodrome: string;
  destination_aerodrome: string;
  fir_entry_point: string;
  fir_entry_time: string;
  fir_mid_point: string;
  fir_mid_time: string;
  fir_exit_point: string;
  fir_exit_time: string;
  flight_level: string;
  wake_turbulence: string;
  flight_category: string;
  flight_type: string;
  day_of_flight: string;
  departure_time: string;
}

export interface IATCMovementLogScope extends ICRUDScope {
  flightTypes: Array<IStaticType>;
  flightCategories: Array<IStaticType>;
  wakeTurbulence: Array<IStaticType>;
  search: string;
  control: IStartEndDates;
  pagination: ISpringPageableParams;
  editable: IATCMovementLogType;
  getSortQueryString: () => string;
  refreshOverride: () => void;
  resolveAircraftType: (regNum: string, dateOfContact: Date) => void;
}
