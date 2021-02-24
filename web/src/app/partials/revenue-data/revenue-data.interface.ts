// interface
import { IAerodrome } from '../aerodromes/aerodromes.interface';
import { IAccountMinimal } from '../accounts/accounts.interface';
import { IStartEndDates } from '../../angular-ids-project/src/components/dateRange/dateRange.interface';
import { IStaticType } from '../../angular-ids-project/src/helpers/interfaces/static-data-type.interface';

export interface IRevenueData {
  start_date: string;
  end_date: string;
  analysis_type: string;
  billing_centres: string;
  accounts: string;
  aerodromes: string;
  payment_mode: string;
  charge_class: string;
  charge_category: string;
  charge_type: string;
  temporal_group: string;
  group_by: string;
  sort: string;
  fiscal_year: boolean;
}

export interface IRevenueDataTemplate extends IRevenueData {
  id: number;
  name: string;
  value: string;
  chart_type: string;
}

export interface IRevenueResponse {
  date: string;
  ansp_sum: number;
  count: number;
  aerodrome_name: string;
  description: string;
  type: string;
  charge_class: string;
  subtype: string;
  usd_sum: number;
  account_name: string;
  name: string;
  is_paid: string;
  category: string;
}

export interface IRevenueDataScope extends ng.IScope {
  accountsList: Array<IAccountMinimal>;
  accountsModel: Array<IAccountMinimal>;
  aerodromesList: Array<IAerodrome>;
  aerodromesModel: Array<IAerodrome>;
  control: IStartEndDates;
  editable: IRevenueData;
  datapoints: Array<Object>;
  selected: Array<Object>;
  datacolumns: Array<Object>;
  datax: Object;
  addAerodromeToList: Function;
  addAccountToList: Function;
  display_value: string;
  ylabel: Object[];
  data: Array<IRevenueResponse>;
  displayValues: Array<any>;
  groupByValues: Array<IStaticType>;
  generate: Function;
}
