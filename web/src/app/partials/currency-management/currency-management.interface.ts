import { IStartEndDates } from '../../angular-ids-project/src/components/dateRange/dateRange.interface';
import { ICountry } from '../country-management/country-management.interface';
import { IExchangeRate, IExchangeRateSpring } from '../currency-exchange-rates/currency-exchange-rates.interface';
import { ISpringPageableParams } from '../../angular-ids-project/src/helpers/services/crud.service';
import { IExtendableError } from '../../angular-ids-project/src/helpers/interfaces/restangularError.interface';

export interface ICurrencySpring extends ISpringPageableParams {
  content: Array<ICurrency>;
}

export interface ICurrency {
  id?: number;
  currency_code: string;
  currency_name: string;
  country_code: ICountry;
  decimal_places: number;
  symbol: string;
  active: boolean;
  allow_updated_from_web: boolean;
  external_accounting_system_identifier: string;
  exchange_rate_target_currency_id: number;
}

/**
 * Additional read-only information about a currency record --
 * provided by back-end
 */
export interface ICurrencyInfo {
  currency_id: number;
  ref_accounts: Array<string>;
  ref_account_total: number;
  used_as_exchange_target_by_another_active_currency: boolean;
}

export interface ICurrencyScope extends ng.IScope {
  control: IStartEndDates;
  currencyFilter: string;
  textSearch: string;
  editable: ICurrency;
  updateFromWeb: () => void;
  reset: () => void;
  edit: (currency: ICurrency) => void;
  getExchangeRates: Function;
  resetExchangeRate: Function;
  addExchangeRate: (id: number, exchangeRate: IExchangeRate, startDate: string, endDate: string) => void;
  updateExchangeRate: (exchangeRate: IExchangeRate, startDate: string, endDate: string) => void;
  deleteExchangeRate: (rate: IExchangeRate) => void;
  editExchangeRate: (rate: IExchangeRate) => void;
  exchangeRate: any;
  exchangeRates: any;
  allExchangeRates: Array<IExchangeRate>;
  error: IExtendableError;
  $watch: any;
  anspCurrency: string | number;
  isAnspCurrency: boolean;
  requireExternalSystemId: boolean;
  currencyList: Array<ICurrency>;
  getActiveCurrencyList: () => Array<ICurrency>;
  loadCurrencyInfo: () => void;
  currencyInfo: ICurrencyInfo;
  canDeleteOrDeactivateCurrency: () => boolean;
}
