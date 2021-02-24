// interfaces
import { IExchangeRate } from '../currency-exchange-rates.interface';
import { IDateObject } from '../../../angular-ids-project/src/helpers/interfaces/dateObject.interface';

// services
import { CRUDService } from '../../../angular-ids-project/src/helpers/services/crud.service';

// endpoint
export let endpoint: string = 'currency-exchange-rates';

export class CurrencyExchangeRatesService extends CRUDService {

  protected restangular: restangular.IService;

  private _mod: IExchangeRate = {
    id: null,
    currency: {
      id: null
    },
    currency_code: null,
    exchange_rate: null,
    exchange_rate_valid_from_date: null,
    exchange_rate_valid_to_date: null,
    target_currency: {
      id: null,
      currency_code: null
    }
  };

  /** @ngInject */
  constructor(protected Restangular: restangular.IService) {
    super(Restangular, endpoint);
    this.model = angular.copy(this._mod);
  }

  public getExchangeRatesByCurrencyId(currencyId: number, page: number): ng.IPromise<any> {
    const pagination = {page};
    if (page) {
      pagination.page = --page; // the uib-pagination starts at `1` but Spring Framework starts at `0`, therefore we fake it
    }
    return this.restangular.one(`${endpoint}/for-currency/${currencyId}`).get(pagination);
  }

  public getAllExchangeRatesByCurrencyId(currencyId: number, queryString?: string): ng.IPromise<any> {
    const qs = queryString ? queryString : '';
    return this.restangular.one(`${endpoint}/all-currency/${currencyId}?${qs}`).get();
  }

  public getExchangeRateByCurrencyId(fromCurrenyId: number, toCurrencyId: number, datetime?: IDateObject): ng.IPromise<number> {
    return this.restangular.one(`${endpoint}/for-currency/${fromCurrenyId}/to/${toCurrencyId}`).get({ datetime: datetime || null });
  }

  public getExchangeAmount(fromAmount: number, exchangeRate: number, toDecimalPoints: number, inverse: boolean = false): ng.IPromise<number> {
    return this.restangular.one(`${endpoint}/for-exchange-rate/${exchangeRate}/from-amount/${fromAmount}/rounded-to/${toDecimalPoints}?inverse=${inverse}`).get();
  }

  public updateFromWeb(): ng.IPromise<any> {
    return this.restangular.one(`${endpoint}/update-all`).customPUT();
  }

  public getCurrentExchangeRatesByCurrencyCode(currencyCode: string): ng.IPromise<number> {
    return this.restangular.one(`${endpoint}/for-currency-code/${currencyCode}`).get();
  }

}
