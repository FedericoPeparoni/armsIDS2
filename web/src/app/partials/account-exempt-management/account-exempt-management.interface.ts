import { IAccount } from '../accounts/accounts.interface';

export interface IAccountExemptManagement {
  id?: number;
  account_id: number;
  account_name: string;
  enroute: number;
  parking: number;
  approach_fees_exempt: number;
  aerodrome_fees_exempt: number;
  late_arrival: number;
  late_departure: number;
  international_pax: number;
  domestic_pax: number;
  extended_hours: number;
  flight_notes: string;
}

export interface IAccountExemptManagementScope extends ng.IScope {
  accountList: Array<IAccount>;
  getAccountNameByID: Function;
}
