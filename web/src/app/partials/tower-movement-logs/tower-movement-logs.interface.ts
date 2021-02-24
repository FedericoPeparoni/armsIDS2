// interfaces
import { ICRUDScope } from '../../angular-ids-project/src/helpers/services/crud.interface';
import { IStartEndDates } from '../../angular-ids-project/src/components/dateRange/dateRange.interface';
import { ISpringPageableParams } from '../../angular-ids-project/src/helpers/services/crud.service';

export interface ITowerMovementLogType {
  id?: number;
  date_of_contact: string;
  flight_id: string;
  registration: string;
  aircraft_type: string;
  operator_name: string;
  departure_aerodrome: string;
  departure_contact_time: string;
  destination_aerodrome: string;
  destination_contact_time: string;
  route: string;
  flight_level: string;
  flight_crew: number;
  passengers: number;
  flight_category: string;
  day_of_flight: string;
  departure_time: string;
}

export interface ITowerMovementLogScope extends ICRUDScope {
  flightCategories: Function;
  pattern: string;
  search: string;
  control: IStartEndDates;
  pagination: ISpringPageableParams;
  editable: ITowerMovementLogType;
  getSortQueryString: () => string;
  refreshOverride: () => void;
  resolveAircraftType: (regNum: string, dateOfContact: Date) => void;
}
