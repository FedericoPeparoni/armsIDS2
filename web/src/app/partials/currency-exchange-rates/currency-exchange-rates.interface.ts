import { ISpringPageableParams } from '../../angular-ids-project/src/helpers/services/crud.service';

export interface IExchangeRate {
  id: number;
  currency: {
      id: number;
  },
  currency_code: string;
  exchange_rate: number;
  exchange_rate_valid_from_date: string;
  exchange_rate_valid_to_date: string;
  target_currency: {
    id: number;
    currency_code: string;
  };
}

export interface IExchangeRateSpring extends ISpringPageableParams {
  content: Array<IExchangeRate>;
}
