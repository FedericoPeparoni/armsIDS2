import { IAccountMinimal } from '../accounts/accounts.interface';
import { IStartEndDates } from '../../angular-ids-project/src/components/dateRange/dateRange.interface';
import { IStaticType } from '../../angular-ids-project/src/helpers/interfaces/static-data-type.interface';

export interface IReportGeneration {
  report: string;
  used_defined: boolean;
  used_undefined: boolean;
  todate: string;
  overdue_interval: string;
  invoices: boolean;
  credit_type: boolean;
  summary_interval: string;
  filter_type: number;
  account_ids: string;
  account_new_page: boolean;
  group_by_account: boolean;
  group_by_aerodrome: boolean;
  group_by_owner: boolean;
  credit_notes: boolean;
  debit_notes: boolean;
  status: string;
  include_null_accounts: boolean;
  fiscal_year: boolean;
  report_month: string;
}

export interface ReportGenerationScope extends ng.IScope {
  reportGeneration: Array<IReportGeneration>;
  addAccountToList: Function;
  accountIds: string;
  accountsList: Array<IAccountMinimal>;
  selectedAccounts: Array<IAccountMinimal>;
  setClear: Function;
  start: Date;
  end: Date;
  editable: IReportGeneration;
  control: IStartEndDates;
  getFilterType: Function;
  setToDate: Function;
  returnFilteredDate: Function;
  returnEndOfMonth: Function;
  missingInfo: Array<any>;
  listOfMissingInfo: Array<IStaticType>
  formatMissingInfo: Function;
  aircraftRegistrations: Array<IStaticType>;
  listOfAirRegs: Array<IStaticType>;
  formatAircraftRegistrations: Function;
  billingErrors: string;
  aircraftRegs: string;
  CAAB: boolean;
  DC_ANSP: boolean;
  EANA: boolean;
  INAC: boolean;
  KCAA: boolean;
  ZACL: boolean;
  TTCAA: boolean;
  report_format: string;
}
