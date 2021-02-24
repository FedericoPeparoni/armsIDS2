// intefaces
import { ISpringPageableParams } from '../../angular-ids-project/src/helpers/services/crud.service';
import { IStaticType } from '../../angular-ids-project/src/helpers/interfaces/static-data-type.interface';
import { IAccount, IAccountMinimal } from '../accounts/accounts.interface';

export interface IFlightScheduleManagementScope {
  textFilter: string;
  pagination: ISpringPageableParams;
  refreshOverride: () => void;
  getSortQueryString: () => string;
  preUploadSchedules: (startDate: string, document: any) => void;
  getDay: (charater: string) => string;
  mtowUnitOfMeasure: string | number;
  accountSchedules: Array<IFlightSchedule>;
  editable: IFlightSchedule;
  addDaysToSchedule: Function;
  formatDailySchedules: Function;
  edit: Function;
  reset: Function;
  days: Array<IStaticType>;
  start: Date;
  listOfDays: Array<IStaticType>;
  accountId: number;
  clearFilters: Function;
  $watch: any;
  customDate: any;
  accountsWithFlightSchedules: Array<IAccountMinimal>;
  update: Function;
  create: Function;
  delete: Function;
  pattern: string;
  filterParameters: object;
}

export interface IFlightSchedule {
  id?: number;
  account?: IAccount;
  flight_service_number: string;
  dep_ad: string;
  dep_time: string;
  dest_ad: string;
  dest_time: string;
  daily_schedule: string;
  self_care?: boolean;
  active_indicator?: string;
  start_date?: Date;
  end_date?: Date;
  missing_flight_movements?: Array<IMissingOrUnexpectedFlight>
  unexpected_flights?: Array<IMissingOrUnexpectedFlight>
}

export interface IMissingOrUnexpectedFlight {
  flight_id: number,
  date_of_flight: string
}

export interface IFlightScheduleSpring {
  content: Array<IFlightSchedule>
}
