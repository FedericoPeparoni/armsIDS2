// interfaces
import { IBankAccount } from '../../../partials/bank-account-management/bank-account-management.interface';
import { ICurrency, ICurrencySpring } from '../../../partials/currency-management/currency-management.interface';
import { IPaymentScope, IPayment } from './payment.interface';
import { IRestangularResponse } from '../../../angular-ids-project/src/helpers/interfaces/restangularError.interface';

// services
import { CurrencyExchangeRatesService } from '../../../partials/currency-exchange-rates/service/currency-exchange-rates.service';
import { CurrencyManagementService } from '../../../partials/currency-management/service/currency-management.service';
import { HelperService } from '../../../angular-ids-project/src/components/services/helpers/helpers.service';
import { TransactionsService } from '../../../partials/transactions/service/transactions.service';
import { SystemConfigurationService } from '../../../partials/system-configuration/service/system-configuration.service';
import { AccountsService } from '../../../partials/accounts/service/accounts.service';
import { OrganizationService } from '../../../partials/organization/service/organization.service';
import { BankAccountManagementService } from '../../../partials/bank-account-management/service/bank-account-management.service';

// constants
import { SysConfigConstants } from '../../../partials/system-configuration/system-configuration.constants';
import { IAccount } from '../../../partials/accounts/accounts.interface';

/** @ngInject */
export class PaymentController {

  private _exchangeTimeout: ng.IPromise<any>;

  constructor(private $scope: IPaymentScope, private $timeout: ng.ITimeoutService, private currencyExchangeRatesService: CurrencyExchangeRatesService,
    private currencyManagementService: CurrencyManagementService, private transactionsService: TransactionsService,
    private systemConfigurationService: SystemConfigurationService, private accountsService: AccountsService, private organizationService: OrganizationService,
    private bankAccountManagementService: BankAccountManagementService) {
    $scope.ctrl = $scope;

    $scope.activeOrg = this.organizationService.active();
    $scope.workflow = systemConfigurationService.getValueByName(<any>SysConfigConstants.POINT_OF_SALE_WORKFLOW);

    // expose necessary methods to scope
    $scope.filterPaymentMechanism = (mechanism: string) => this.filterPaymentMechanism(mechanism);
    $scope.updateExchangeRate = (fromCurrency: ICurrency, toCurrency: ICurrency) => this.updateExchangeRate(fromCurrency, toCurrency);
    $scope.updatePaymentAmount = () => this.updatePaymentAmount();

    // default exchange rate to null and add watch to update from payment value
    // this is here to limit exchange rate in view to 5 decimal places
    $scope.paymentExchangeRate = null;
    $scope.$watch('payment.payment_exchange_rate', (value: number) => this.updatePaymentExchangeRate(value));

    // get full list of currencies, only do this once
    this.currencyManagementService.listAll().then((data: ICurrencySpring) => this.$scope.fullCurrencyList = data.content);

    // get invoice currencies
    $scope.$watch('invoiceCurrency', (value: ICurrency) => this.updateCurrencyList(value));

    $scope.$watch('accountId', (accountId: number) => {
      if (accountId) {
        this.accountsService.get(accountId).then((data: IAccount) => {
          this.$scope.cashAccount = data.cash_account;
        });
      }
    });

    $scope.$watch('invoiceAmount', (value: number) => {
      $scope.payment.amount = value;
      this.updateExchangeRate($scope.payment.payment_currency, $scope.payment.currency);
    });

    $scope.inverseExchange = this.systemConfigurationService.getBooleanFromValueByName(<any>SysConfigConstants.INVERSE_CURRENCY_RATE);

    // define scope payment mechanism functions
    this.$scope.isPaymentMechanism = (...mechanisms: Array<string>) => this.isPaymentMechanism(mechanisms);

    // define scope bank account properties and functions
    this.$scope.getBankAccountLabel = (name: string, number: string) => this.bankAccountManagementService.getLabel(name, number);
    this.$scope.setPaymentBankAccount = (bankAccount: IBankAccount) => this.setPaymentBankAccount(bankAccount);

    // get bank account support and define in scope
    this.bankAccountManagementService.getIsSupported().then((isSupported: boolean) => this.$scope.isBankAccountSupported = isSupported);

    // define on change event functions
    this.$scope.onPaymentMechanismChange = (payment: IPayment) => this.onPaymentMechanismChange(payment);
  }

  /**
   * Update invoice currency with the currency that user selected
   *
   * @param invoiceCurrency invoiceCurrency
   */
  private updateCurrencyList(invoiceCurrency: ICurrency): void {

    // update invoice currency for the chosen account
    if (invoiceCurrency) {
        this.$scope.payment.currency = invoiceCurrency;
        const paymentCurrency = invoiceCurrency;
        const { currency_name, currency_code } = paymentCurrency;
        this.$scope.paymentCurrency = `${currency_name} (${currency_code})`;
    }
  }

  /**
   * Update payment exchange rate by even rounding supplied value.
   *
   * @param value exchange rate value
   */
  private updatePaymentExchangeRate(value: number): void {

    // even round exchange rate to 5 decimal places of value
    if (value) {
      this.$scope.paymentExchangeRate = HelperService.evenRound(value, 5).toFixed(5);
    } else {
      this.$scope.paymentExchangeRate = null;
    };

    // determine if inverse needs to be shown
    if (this.$scope.paymentExchangeRate && this.$scope.inverseExchange) {
      this.$scope.paymentExchangeRate = (1 / parseFloat(this.$scope.paymentExchangeRate)).toString();
    }
  }

  /**
   * Calcualte exchange amount. Rounding decimal points is defined by supplied `currency`.
   *
   * @param amount amount to exchange
   * @param exchangeRate exchange rate
   * @param currency currency to use for formatting
   * @param inverse inverse exchange rate (divide instead of multiple)
   */
  private calculateAmount(amount: number, exchangeRate: number, currency: ICurrency, inverse: boolean = false): number {

    // if amount, exchange and currency are null, return null
    if (amount === null || exchangeRate === null || currency === null) {
      return null;
    }

    // calculate exchange from amount and exchange rate (inverse if true)
    var exchange: number;
    if (inverse) {
      exchange = amount / exchangeRate;
    } else {
      exchange = amount * exchangeRate;
    }

    // value should be even rounded to currency decimal points
    return HelperService.evenRound(exchange, currency.decimal_places || 2);
  }

  /**
   * Calculate exchange rate from payment currency into local currency. This method will
   * set currency fields to invalid if no exchange rate is found.
   *
   * @param fromCurrency payment currency
   * @param toCurrency local currency
   */
  private updateExchangeRate(fromCurrency: ICurrency, toCurrency: ICurrency): void {
    // assert that scope payment exists
    if (!this.$scope.payment) {
      return;
    }

    // if fromCurrency exists, toCurrency null and fromCurrency in currencyList
    // set toCurrency to fromCurrency for better user experience
    if (fromCurrency && !toCurrency && this.$scope.currencyList.filter((currency: ICurrency) => currency.id === fromCurrency.id).length > 0) {
      toCurrency = fromCurrency;
      this.$scope.payment.currency = fromCurrency;
    }

    // if currency values are empty, return
    // else find exchange rate between currencies
    if (!fromCurrency || !toCurrency) {
      this.$scope.payment.payment_exchange_rate = null;
      this.updatePaymentAmount();
    } else {
      this.currencyExchangeRatesService.getExchangeRateByCurrencyId(fromCurrency.id, toCurrency.id)
        .then((data: number) => {
          this.$scope.payment.payment_exchange_rate = data;
          this.$scope.form.currency.$setValidity('valid_exchange_rate', true);
          this.$scope.form.paymentCurrency.$setValidity('valid_exchange_rate', true);
        }).catch((error: IRestangularResponse) => {
          this.$scope.payment.payment_exchange_rate = null;
          this.$scope.form.currency.$setValidity('valid_exchange_rate', false);
          this.$scope.form.paymentCurrency.$setValidity('valid_exchange_rate', false);
          this.$scope.error = { error: error };
        }).finally(() => {
          this.updatePaymentAmount();
        });
    }
  }

  /**
   * Update payment amount from local amount and exchange rate.
   */
  private updatePaymentAmount(): void {

    // if exchange call to service already pending, cancel and send another
    // use one timeout for both local and payment amounts so that if user
    // switches input controls and updates before result is returned, their new value
    // isn't overwritten
    if (this._exchangeTimeout) {
      this.$timeout.cancel(this._exchangeTimeout);
    }

    // get all values from editable form
    var amount: number = this.$scope.payment.amount;
    var rate: number = this.$scope.payment.payment_exchange_rate;
    var currency: ICurrency = this.$scope.payment.payment_currency;
    var inverse: boolean = true;

    // if amount, exchange and currency are null, return
    // else calculate exchange in browser (emulated banker's rounding)
    // and after custom debounce of 800 milliseconds, get calcualtion from service (so that banker's rounding is consistent)
    if (!amount && amount !== 0 || !rate || !currency) {
      this.$scope.payment.payment_amount = null;
    } else {

      // initially get amount from emulated banker's rounding in browser
      // this will provide immediate feedback to the user
      this.$scope.payment.payment_amount = this.calculateAmount(amount, rate, currency, inverse);

      // send call to service to get calcualted exchange amount
      // wait 800 milliseconds before sending
      this._exchangeTimeout = this.$timeout(() => {
        this.currencyExchangeRatesService.getExchangeAmount(amount, rate, currency.decimal_places || 2, inverse)
          .then((data: number) => {
            this.$scope.payment.payment_amount = data;
          }).catch((Error: IRestangularResponse) => {
            // fallback to manual calculations
            this.$scope.payment.payment_amount = this.calculateAmount(amount, rate, currency, inverse);
          });
      }, 800);
    }
  }

  /**
   * Filter out adjustment payment mechanisms for point-of-sale interface.
   *
   * @param mechanism payment mechanism
   */
  private filterPaymentMechanism(mechanism: string): boolean {
    return mechanism !== 'adjustment';
  }

  /**
   * Verify if any provided payment mechanisms are selected.
   * 
   * @param mechanisms payment mechanisms to verify
   */
  private isPaymentMechanism(mechanisms: Array<string>): boolean {

    // mechanisms parameter is required, return false if not valid
    if (!mechanisms) {
      return false;
    }

    let mechanism: string = this.$scope.payment
      ? this.$scope.payment.payment_mechanism : null;

    return mechanisms.indexOf(mechanism) > -1;
  }

  /**
   * Set payment transaction's bank account name, number, externalId from
   * provided bank account object.
   * 
   * @param bankAccount bank account info to use
   */
  private setPaymentBankAccount(bankAccount: IBankAccount): void {
    if (bankAccount) {
      this.$scope.payment.bank_account_name = bankAccount.name;
      this.$scope.payment.bank_account_number = bankAccount.number;
      this.$scope.payment.bank_account_external_accounting_system_id = bankAccount.external_accounting_system_id;
    } else {
      this.$scope.payment.bank_account_name = null;
      this.$scope.payment.bank_account_number = null;
      this.$scope.payment.bank_account_external_accounting_system_id = null;
    }
  }

  /**
   * On payment mechanism change event, updated payment properties.
   * 
   * @param payment editable payment
   */
  private onPaymentMechanismChange(payment: IPayment): void {

    // set payment reference number to 'N/A' if payment mechanism is cash
    payment.payment_reference_number = this.isPaymentMechanism(['cash'])
      ? 'N/A' : null;

    // on payment mechanism other than cheque or wire, clear bank account
    if (!this.isPaymentMechanism(['cheque', 'wire'])) {
      (<any>payment).bank_account = null; // type <any> as bank_account is not an expected IPayment property
      payment.bank_account_name = null;
      payment.bank_account_number = null;
      payment.bank_account_external_accounting_system_id = null;
    }
  }
}
