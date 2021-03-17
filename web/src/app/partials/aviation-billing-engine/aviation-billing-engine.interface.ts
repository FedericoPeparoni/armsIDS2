import { IAccount, IAccountMinimal } from '../accounts/accounts.interface';
import { IDateObject } from '../../angular-ids-project/src/helpers/interfaces/dateObject.interface';
import { IExtendableError } from '../../angular-ids-project/src/helpers/interfaces/restangularError.interface';
import { IFlightMovementCategory } from '../../partials/flight-movement-category/flight-movement-category.interface';

export interface IAviationBillingEngine {
  month: string;
  account_id_list: Array<number>;
  year: number;
  flights: string;
  incompleteFlights: Array<IIncompleteFlights>;
  iata_status: string;
  sort: string;
  userBillingCenterOnly: string;
  billing_interval: string;
  end_date: string;
  start_date: string;
  start_date_open: string;
  end_date_open: string;
  processStartDate: string;
  processEndDate: string;
  preview: number;
  endDateInclusive: string;
  iataInvoice?: boolean;
  flightCategory: number;
  account_type: number;
}

export interface IAviationBillingEngineSpring {
  content: Array<IAccount>;
}

export interface IIncompleteFlights {
  account_name: string;
  day_of_flight: string;
  departure_time: string;
  flight_id: string;
  flight_movement_id: number;
  issues: Array<string>;
  movement_type: string;
  registration: string;
  status: string;
}

export interface IAviationBillingEngineScope extends ng.IScope {
  invoiceByFmCategory: boolean;
  listOfAccounts: Array<IAccountMinimal>;
  selectedAccounts: Array<IAccountMinimal>;
  dateObject: IDateObject;
  addIdToList: Function;
  validate: Function;
  executeRecalculation: Function;
  editable: IAviationBillingEngine;
  cancelRecalculation: Function;
  recalculateJob: IJobStatus;
  reconcileJob: IJobStatus;
  recalculatePromise: ng.IPromise<any>;
  reconcilePromise: ng.IPromise<any>;
  clear: Function;
  error: IExtendableError;
  startingDay: number;
  setWeekStartDate: Function;
  setMonthlyDates: Function;
  setAnnuallyDates: Function;
  setPartiallyDates: Function;
  recalculate: any;
  reconcile: any;
  dateOptions: Object;
  dateOptionsAnnually: Object;
  dateOptionsPartially: Object;
  maxDate: string;
  startOfCurrentMonth: any;
  today: any;
  isBillingPeriodValid: boolean;
  executePSCR: Function;
  invoiceTypeChanged(invoiceType: string): void;
  preview: number;
  iataSupported: boolean;
  incompleteFlightSort(orderBy: string): Array<string>;
  flightCategories: Array<IFlightMovementCategory>;
  processing: boolean;
}

export interface IJobStatus {
  job_name: string;
  job_type: string;
  job_parameters: string;
  job_execution_status: string;
  start_time: string;
  stop_time: string;
  total_steps: number;
  steps_to_process: number;
  steps_completed: number;
  steps_aborted: number;
  variables: string;
  etc_time: string;
  seconds_left: string;
  rate: string;
  steps_est_time: string;
  message: string;
  flights: string;
}
