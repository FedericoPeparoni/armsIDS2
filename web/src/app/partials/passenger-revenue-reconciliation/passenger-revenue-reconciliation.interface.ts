import { IExtendableError } from '../../angular-ids-project/src/helpers/interfaces/restangularError.interface';

export interface IPassengerRevenueReconciliationScope {
  executePSCR: (start_date: Date, end_date: Date) => void;
  reconcilePSCRJob: boolean;
  reconcileResponse: IPassengerRevenueReconciliationResponse;
  dom_pay: number;
  itl_pay: number;
  domPaxCurrency: string | number;
  intlPaxCurrency: string | number;
  error: IExtendableError;
  customDate: string;
}

export interface IPassengerRevenueReconciliation {
  start_date: string;
  end_date: string;
  dom_pay: number;
  itl_pay: number;
}

export interface IPassengerRevenueReconciliationResponse {
  count_fm: number;
  count_pscr: number;
  dom_warning: boolean;
  itl_warning: boolean;
  total_dom_collected: number;
  total_itl_collected: number;
  total_dom_fees: number;
  total_itl_fees: number;
}
