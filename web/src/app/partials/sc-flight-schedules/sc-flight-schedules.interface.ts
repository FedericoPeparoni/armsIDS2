// intefaces
import { ISpringPageableParams } from '../../angular-ids-project/src/helpers/services/crud.service';
import { IStaticType } from '../../angular-ids-project/src/helpers/interfaces/static-data-type.interface';
import { IAccount } from '../accounts/accounts.interface';
import { IFlightSchedule } from '../flight-schedule-management/flight-schedule-management.interface';
import { IExtendableError } from '../../angular-ids-project/src/helpers/interfaces/restangularError.interface'

export interface IScFlightScheduleManagementScope {
  search: string;
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
  error: IExtendableError;
  needAdminApproval: boolean;
  setStatus: Function;
  accountsWithFlightSchedules: Array<IAccount>;
  update: Function;
  create: Function;
  delete: Function;
  $watch: any;
  filterParameters: object;
  textFilter: string;
}
