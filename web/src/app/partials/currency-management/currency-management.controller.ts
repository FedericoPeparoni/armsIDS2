// controllers
import { CRUDFormControllerUserService } from '../../angular-ids-project/src/helpers/controllers/crud-form-user-service/crud-form-user-service.controller';

// interfaces
import { ICurrency, ICurrencyInfo, ICurrencyScope, ICurrencySpring } from './currency-management.interface';
import { IExchangeRate } from '../currency-exchange-rates/currency-exchange-rates.interface';

// services
import { CurrencyManagementService } from './service/currency-management.service';
import { CurrencyExchangeRatesService } from '../currency-exchange-rates/service/currency-exchange-rates.service';
import { AccountsService } from '../accounts/service/accounts.service';
import { SystemConfigurationService } from '../system-configuration/service/system-configuration.service';
import { CustomDate } from '../../angular-ids-project/src/components/services/customDate/customDate.service';

// constants
import { SysConfigConstants } from '../system-configuration/system-configuration.constants';
import { IRestangularResponse } from '../../angular-ids-project/src/helpers/interfaces/restangularError.interface';

export class CurrencyManagementController extends CRUDFormControllerUserService {

  /* @ngInject */
  constructor(protected $scope: ICurrencyScope, private currencyManagementService: CurrencyManagementService,
      private currencyExchangeRatesService: CurrencyExchangeRatesService, private accountsService: AccountsService,
      private systemConfigurationService: SystemConfigurationService, private customDate: CustomDate) {
    // setup
    super($scope, currencyManagementService);
    super.setup();
    currencyExchangeRatesService.listAll().then((rates: Array<IExchangeRate>) => this.$scope.allExchangeRates = rates); // get all exchange rates

    this.$scope.customDate = this.customDate.returnDateFormatStr(false);

    $scope.$watchGroup(['currencyFilter', 'textSearch'], () => this.getFilterParameters());

    // functions
    $scope.reset = () => this.reset();
    $scope.refresh = () => this.refreshOverride();
    $scope.updateFromWeb = () => this.updateFromWeb(); // updates currency list, with new exchange rates from the web
    $scope.updateDisplay = (id: number) => currencyExchangeRatesService.listAll().then((rates: Array<IExchangeRate>) =>
      this.$scope.allExchangeRates = rates).then(() => this.displayCurrencyRate(id));

    $scope.getExchangeRates = (currencyId: number, queryString: string) => {
      currencyExchangeRatesService.getAllExchangeRatesByCurrencyId(currencyId, queryString)
        .then((data: IExchangeRate[]) => $scope.exchangeRates = data);
    };

    $scope.addExchangeRate = (currencyId: number, exchangeRate: IExchangeRate, startDate: string, endDate: string) =>
      this.addExchangeRate(currencyId, exchangeRate, startDate, endDate);

    $scope.updateExchangeRate = (exchangeRate: IExchangeRate, startDate: string, endDate: string) =>
      this.updateExchangeRate(exchangeRate, startDate, endDate);

    $scope.deleteExchangeRate = (exchangeRate: IExchangeRate) => this.deleteExchangeRate(exchangeRate);
    $scope.editExchangeRate = (exchangeRate: IExchangeRate) => this.editExchangeRate(exchangeRate);
    $scope.resetExchangeRate = () => this.resetExchangeRate();
    $scope.getActiveCurrencyList = () => this.getActiveCurrencyList();

    $scope.inverseExchange = this.systemConfigurationService.getBooleanFromValueByName(<any>SysConfigConstants.INVERSE_CURRENCY_RATE);

    // ansp currency
    $scope.anspCurrency = this.systemConfigurationService.getValueByName(<any>SysConfigConstants.ANSP_CURRENCY);
    $scope.isAnspCurrency = false;

    // set scope value for require external system identifier
    $scope.requireExternalSystemId = this.requireExternalSystemId();

    $scope.canDeleteOrDeactivateCurrency = () => this.canDeleteOrDeactivateCurrency();
  }

  protected edit(currency: ICurrency): void {
    this.$scope.editable = angular.copy(currency);
    this.resetExchangeRate();
    this.loadCurrencyInfo();
  }

  /**
   * We need to override this, because we really need the list of all currencies, not just the first page.
   * We need it to resolve currency IDs to currency codes.
   */
  protected list(data: Object = {}, queryString: string = ''): ng.IPromise<any> {
    const filterKey: string = 'filter';
    const textSearchKey: string = 'textSearch';
    data = (data || {});
    data[filterKey] = this.$scope.currencyFilter || 'active';
    data[textSearchKey] = this.$scope.textSearch;
    return super.list (data, queryString).then (() => {
      // load all currencies into a separate list after loading the current page
      this.loadAllCurrencies();
    });
  }

  protected reset(): void {
    this.resetExchangeRate();
    super.reset();
    this.$scope.isAnspCurrency = false;
    this.$scope.currencyInfo = null;
  }

  private displayCurrencyRate(currencyId: number): number {
    if (this.$scope.allExchangeRates) {
      for (let rate of this.$scope.allExchangeRates) {
        if (currencyId === rate.currency.id) {
          return this.toUSDInverse(rate.exchange_rate);
        }
      }
    }

    return null;
  }

  private getFilterParameters(): void {
    this.$scope.filterParameters = {
      filter: this.$scope.currencyFilter,
      textSearch: this.$scope.textSearch,
      page: this.$scope.pagination ? this.$scope.pagination.number : 0
    };
  }

  private refreshOverride(): void {
    this.getFilterParameters();

    super.list(this.$scope.filterParameters, this.$scope.getSortQueryString());
  }

  private resetExchangeRate(): void {
    this.$scope.exchangeRate = {
      id: null,
      currency: {
        id: null
      },
      currency_code: null,
      exchange_rate: null,
      exchange_rate_valid_from_date: null,
      exchange_rate_valid_to_date: null,
      target_currency: {
        id: this.$scope.editable ? this.$scope.editable.exchange_rate_target_currency_id : null
      }
    };

    if (typeof this.$scope.control !== 'undefined') {
      this.$scope.control.reset();
      this.editExchangeRate (this.$scope.exchangeRate);
    }
  }

  // exchange rate related methods that update the UI
  private editExchangeRate(rate: IExchangeRate): void {
    this.$scope.exchangeRate = angular.copy(rate);
    this.$scope.exchangeRate.exchange_rate = this.toUSDInverse(rate.exchange_rate);
    const targetCurrencyId = this.$scope.exchangeRate.target_currency.id;
    this.$scope.exchangeRate.target_currency = this.$scope.currencyList.find((element: ICurrency) => element.id === targetCurrencyId);

    if (rate.exchange_rate_valid_from_date) {
      this.$scope.control.setUTCStartDate(new Date(rate.exchange_rate_valid_from_date));
    }
    if (rate.exchange_rate_valid_to_date) {
      this.$scope.control.setUTCEndDate(new Date(rate.exchange_rate_valid_to_date));
    }
  }

  private addExchangeRate(currencyId: number, exchangeRate: IExchangeRate, startDate: string, endDate: string): void {
    exchangeRate.currency.id = currencyId;
    exchangeRate.exchange_rate_valid_from_date = startDate;
    exchangeRate.exchange_rate_valid_to_date = endDate;
    exchangeRate.exchange_rate = this.toUSDInverse(exchangeRate.exchange_rate);

    this.currencyExchangeRatesService.create(exchangeRate)
      .then(
        () => {
          this.$scope.getExchangeRates(currencyId);
          this.$scope.updateDisplay(currencyId);
          this.resetExchangeRate();
        },
        (error: IRestangularResponse) => this.setErrorResponse(error)
      )
    ;
  }

  private updateExchangeRate(exchangeRate: IExchangeRate, startDate: string, endDate: string): void {
    exchangeRate.exchange_rate_valid_from_date = startDate;
    exchangeRate.exchange_rate_valid_to_date = endDate;
    exchangeRate.exchange_rate = this.toUSDInverse(exchangeRate.exchange_rate);

    this.currencyExchangeRatesService.update(exchangeRate, exchangeRate.id)
      .then(
        () => {
          this.$scope.getExchangeRates(exchangeRate.currency.id);
          this.$scope.updateDisplay(exchangeRate.currency.id);
          this.resetExchangeRate();
        },
        (error: IRestangularResponse) => this.setErrorResponse(error)
      )
    ;
  }

  private deleteExchangeRate(exchangeRate: IExchangeRate): void {
    this.currencyExchangeRatesService.delete(exchangeRate.id)
      .then(() => this.$scope.getExchangeRates(exchangeRate.currency.id), (error: IRestangularResponse) => this.setErrorResponse(error))
      .then(() => this.$scope.updateDisplay(exchangeRate.currency.id), (error: IRestangularResponse) => this.setErrorResponse(error))
      .then(() => this.resetExchangeRate());
  }

  // makes api call to tell server to update currencies
  private updateFromWeb(): void {
    this.currencyExchangeRatesService.updateFromWeb()
      .then(() => this.currencyExchangeRatesService.listAll())
      .then((rates: Array<IExchangeRate>) => this.$scope.allExchangeRates = rates)
      .then(() => this.$scope.reset())
      .catch((error: IRestangularResponse) => this.setErrorResponse(error));
  }

  /**
   * Get required external system id flag from system configuration.
   *
   * @returns boolean true if required
   */
  private requireExternalSystemId(): boolean {
    return this.systemConfigurationService
      .getBooleanFromValueByName(<any>SysConfigConstants.REQUIRE_CURRENCY_EXTERNAL_SYSTEM_ID);
  }

  /**
   * Return inverse exchange rate
   * if inverseExchange parameter set
   * to true in system configuration
   *
   * @returns number
   */
  private toUSDInverse(rate: number): number {
    if (this.$scope.inverseExchange) {
      rate = 1 / rate;
    }
    return rate;
  }

  /**
   * Return the list of active currencies
   */
  private getActiveCurrencyList(): Array <ICurrency> {
    return this.$scope.currencyList ?
      this.$scope.currencyList.filter((currency: ICurrency) => currency && currency.active) :
      [];
  }

  /**
   * Load all currencies -- we need that to manage the drop-down list of exchange target currencies
   */
  private loadAllCurrencies(): void {
    this.currencyManagementService.listAll()
      .then(
        (resp: ICurrencySpring) => this.$scope.currencyList = resp.content,
        (error: IRestangularResponse) => this.setErrorResponse(error)
      );
  }

  /**
   * Load the extra info for the selected currency record
   */
  private loadCurrencyInfo(): void {
    this.$scope.isAnspCurrency = false;
    this.$scope.currencyInfo = null;
    if (this.$scope.editable && this.$scope.editable.id) {
      this.$scope.isAnspCurrency = this.$scope.editable.currency_code === this.$scope.anspCurrency;
      this.currencyManagementService.getCurrencyInfo (this.$scope.editable.id)
        .then ((currencyInfo: ICurrencyInfo) => {
          if (this.$scope.editable && currencyInfo && this.$scope.editable.id === currencyInfo.currency_id) {
            this.$scope.currencyInfo = currencyInfo;
          }
        })
      ;
    }
  }

  /**
   * Return true if current record can be deleted
   */
  private canDeleteOrDeactivateCurrency(): boolean {

    // if there's another currency that uses this currency as a target: return false
    if (this.$scope.currencyInfo && this.$scope.currencyInfo.used_as_exchange_target_by_another_active_currency) {
      return false;
    }

    // if there are accounts that reference this currency: return false
    if (this.$scope.currencyInfo && this.$scope.currencyInfo.ref_accounts.length) {
      return false;
    }

    // if this is an ANSP currency
    if (this.$scope.isAnspCurrency) {
      return false;
    }

    // allow deletion by default
    return true;
  }

}
