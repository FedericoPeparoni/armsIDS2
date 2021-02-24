// interfaces
import { ICurrency, ICurrencyInfo } from '../currency-management.interface';

// services
import { CRUDService } from '../../../angular-ids-project/src/helpers/services/crud.service';

// endpoint
export let endpoint: string = 'currencies';

export class CurrencyManagementService extends CRUDService {

  protected restangular: restangular.IService;

  private _mod: ICurrency = {
    id: null,
    currency_code: null,
    currency_name: null,
    country_code: null,
    decimal_places: null,
    allow_updated_from_web: false, // the html control is not 'required', but at the same time may never be null
    symbol: null,
    active: null,
    external_accounting_system_identifier: null,
    exchange_rate_target_currency_id: null
  };

  /** @ngInject */
  constructor(protected Restangular: restangular.IService) {
    super(Restangular, endpoint);
    this.model = angular.copy(this._mod);
  }

  /**
   * Returns page with USD and ANSP currency
   */
  public getANSPCurrencyAndUSD(): ng.IPromise<ICurrency[]> {
    return this.restangular.one(`${endpoint}/ansp`).get();
  }

  /**
   * Returns list with USD and ANSP currency
   */
  public getListCurrencyANSPAndUSD(): ng.IPromise<ICurrency[]> {
    return this.restangular.one(`${endpoint}/ansp-usd-list`).get();
  }

  public getCurrencyInfo(id: number): ng.IPromise<ICurrencyInfo> {
    return this.restangular.one (`${endpoint}/${id}/info`).get();
  }

  public getByCurrencyCode(code: string): ng.IPromise<ICurrency> {
    return this.restangular.one(`${endpoint}/currency-code/${code}`).get();
  }

  public getActiveCurrency(): ng.IPromise<ICurrency[]> {
    return this.restangular.one(`${endpoint}/active`).get();
  }
}
