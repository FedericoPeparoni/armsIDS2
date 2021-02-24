import { IAccount } from '../accounts/accounts.interface';
import { ICatalogueServiceChargeType } from '../catalogue-service-charge/catalogue-service-charge.interface';
import { IStartEndDates } from '../../angular-ids-project/src/components/dateRange/dateRange.interface';
import { ISpringPageableParams } from '../../angular-ids-project/src/helpers/services/crud.service';

export interface IRecurringCharge {
  id: number,
  service_charge_catalogue: ICatalogueServiceChargeType,
  account: IAccount,
  start_date: string,
  end_date: string
}

export interface IRecurringChargeScope {
  control: IStartEndDates;
  update(recurringCharge: IRecurringCharge, startDate: string, endDate: string);
  create(recurringCharge: IRecurringCharge, startDate: string, endDate: string);
  catalogueServiceChargeListFiltered: ICatalogueServiceChargeType[];
  customDate: string;
  filterParameters: object;
  accountFilter: string;
  statusFilter: string;
  textFilter: string;
  pagination: ISpringPageableParams;
  getSortQueryString: () => string;
  refresh: Function;
}
