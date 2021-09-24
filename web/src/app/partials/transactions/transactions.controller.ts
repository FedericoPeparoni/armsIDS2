// enums
import { CrudFileHandlerPropertyType } from '../../angular-ids-project/src/helpers/controllers/crud-file-handler/crud-file-handler-type';

// controllers
import { CRUDFileUploadController } from '../../angular-ids-project/src/helpers/controllers/crud-file-handler/crud-file-handler.controller';

// interfaces
import { IRestangularResponse } from '../../angular-ids-project/src/helpers/interfaces/restangularError.interface';
import { ITransactionsScope, ITransaction, IAdjustmentCharge, IChargeTypeScope, ITransactionExportSupport } from './transactions.interface';
import { ICurrency, ICurrencySpring } from '../currency-management/currency-management.interface';
import { IInvoice, IInvoiceSpring } from '../invoices/invoices.interface';
import { ICatalogueServiceChargeTypeSpring, ICatalogueServiceChargeType } from '../catalogue-service-charge/catalogue-service-charge.interface';
import { IFlightMovement } from '../flight-movement-management/flight-movement-management.interface';
import { IInvoiceLineItem } from '../line-item/line-item.interface';
import { IBankAccount } from '../bank-account-management/bank-account-management.interface';
import {IAircraftRegistration} from '../aircraft-registration/aircraft-registration.interface';

// services
import { TransactionsService } from './service/transactions.service';
import { InvoicesService } from '../invoices/service/invoices.service';
import { CurrencyManagementService } from '../currency-management/service/currency-management.service';
import { CurrencyExchangeRatesService } from '../currency-exchange-rates/service/currency-exchange-rates.service';
import { HelperService } from '../../angular-ids-project/src/components/services/helpers/helpers.service';
import { CatalogueServiceChargeService } from '../catalogue-service-charge/service/catalogue-service-charge.service';
import { SystemConfigurationService } from '../system-configuration/service/system-configuration.service';
import { CustomDate } from '../../angular-ids-project/src/components/services/customDate/customDate.service';
import { SysConfigBoolean } from '../../angular-ids-project/src/components/services/sysConfigBoolean/sysConfigBoolean.service';
import { FlightMovementManagementService } from '../flight-movement-management/service/flight-movement-management.service';
import { ChargesAdjustmentType, ChargeAdjustmentsService } from '../charge-adjustments/service/charge-adjustments.service';
import { OrganizationService } from '../organization/service/organization.service';
import { BankAccountManagementService } from '../bank-account-management/service/bank-account-management.service';
import { AircraftRegistrationService } from '../aircraft-registration/service/aircraft-registration.service';

// constants
import { SysConfigConstants } from '../system-configuration/system-configuration.constants';
import { ITransactionType } from '../transaction-types/transaction-types.interface';
import { TransactionTypesService } from '../transaction-types/service/transaction-types.service';
import { UnifiedTaxManagementService } from '../unified-tax-management/service/unified-tax-management.service';



export class TransactionsController extends CRUDFileUploadController {

  private _exchangeTimeout: ng.IPromise<any>;

  private exportSupport: ITransactionExportSupport = { credit_notes: false, payments: false };

  private exportSupportMechanism: Object = {};

  /* @ngInject */
  constructor(protected $scope: ITransactionsScope, private transactionsService: TransactionsService,
    private aircraftRegistrationService: AircraftRegistrationService,
    private currencyManagementService: CurrencyManagementService, private invoicesService: InvoicesService,
    private currencyExchangeRatesService: CurrencyExchangeRatesService, private $timeout: ng.ITimeoutService,
    private catalogueServiceChargeService: CatalogueServiceChargeService, private systemConfigurationService: SystemConfigurationService,
    private customDate: CustomDate, private sysConfigBoolean: SysConfigBoolean, private $filter: ng.IFilterService,
    private flightMovementManagementService: FlightMovementManagementService, private chargeAdjustmentsService: ChargeAdjustmentsService,
    private organizationService: OrganizationService, private bankAccountManagementService: BankAccountManagementService,private unifiedTaxManagementService: UnifiedTaxManagementService,
    private $state: angular.ui.IStateService, private transactionTypesService: TransactionTypesService, private $q: angular.IQService) {
    super($scope, transactionsService, null, 'Supporting Document', CrudFileHandlerPropertyType.Part);
    super.setup();

    $scope.pattern = '.jpg, .gif, .png, .pdf';
    $scope.fileUploadRequired = false;
    $scope.fileUploadDisabled = true;
    this.getFilterParameters();

    $scope.inverseExchange = sysConfigBoolean.parse(this.systemConfigurationService.getValueByName(<any>SysConfigConstants.INVERSE_CURRENCY_RATE));
    $scope.customDate = this.customDate.returnDateFormatStr(false);

    $scope.edit = (item: ITransaction) => this.editTransaction(item);
    $scope.refreshList = () => this.refreshList();
    $scope.refreshInvoiceList = (accountId: number, currencyId: number, page?: number) => this.refreshInvoiceList(accountId, currencyId, page);
    $scope.refreshAndHideCreateWizard = (transaction: ITransaction) => this.refreshAndHideCreateWizard(transaction);
    $scope.hideCreateWizard = (transaction: ITransaction) => this.hideCreateWizard(transaction);
    $scope.showChargeWizard = (editable: ITransaction) => this.showChargeWizard(editable);
    $scope.showCreateWizard = (editable: ITransaction) => this.showCreateWizard(editable);
    $scope.showExcessWizard = (editable: ITransaction) => this.showExcessWizard(editable);

    $scope.activeOrg = this.organizationService.active();
    $scope.taspLabel = systemConfigurationService.getValueByName(<any>SysConfigConstants.TASP_FEES_LABEL);


    $scope.selectedInvoiceIds = [];
    $scope.charges = [];
    $scope.calculatePayments = (id: number, amount: number) => this.calculatePayments(id, amount);
    $scope.create = (transaction: ITransaction) => this.create(transaction);
    $scope.reset = () => this.reset();

    $scope.showPaymentMechanism = ((item: string) => $scope.transactionFromInvoice ? item !== 'adjustment' : item);

    $scope.getCurrencyListByAccountId = (id: number) => this.service.getCurrencyListByAccountId(id).then((data: Array<ICurrency>) =>
      $scope.currencyList = data);

    $scope.getInvoicesByTransactionId = (id: number) => this.service.getInvoicesByTransactionId(id).then((data: IInvoice) =>
      this.$scope.listOfInvoices = data);



    $scope.getTransactionPaymentsByTransactionId = (id: number) => this.service.getTransactionPaymentsByTransactionId(id).then((data: IInvoice) =>
      this.$scope.listOfTransactionPayments = data);

    $scope.updateExchangeRate = (transaction: ITransaction, prioritizePayment?: boolean) => this.updateExchangeRate(transaction, prioritizePayment);
    $scope.updateLocalAmount = () => this.updateLocalAmount();
    $scope.updatePaymentAmount = () => this.updatePaymentAmount();


    // default exchange rate to null and add watch to update from editable value
    // this is here to limit exchange rate in view to 5 decimal places
    $scope.paymentExchangeRate = null;
    $scope.$watch('editable.payment_exchange_rate', (value: number) => this.updatePaymentExchangeRate(value));
    $scope.invoiceChecked = false;
    $scope.excessToBePaid = false;
    $scope.addCharges = (charge: IAdjustmentCharge) => this.addCharges(charge);
    $scope.editCharges = (charge: IAdjustmentCharge, index: number) => this.editCharges(charge, index);
    $scope.resetCharges = () => this.resetCharges();
    $scope.updateCharges = (charge: IAdjustmentCharge) => this.updateCharges(charge);
    $scope.deleteCharges = () => this.deleteCharges();
    $scope.getCheckbox = (index: number, id: number, checked: boolean, amountOwed: number) => this.getCheckbox(index, id, checked, amountOwed);
    $scope.getInvoiceAdjusted = (id: number) => this.getInvoiceAdjusted(id);
    $scope.createNote = (transaction: ITransaction, charges: IAdjustmentCharge) => this.createNote(transaction, charges);
    $scope.disableCreateNote = (charges: Array<IAdjustmentCharge>) => this.disableCreateNote(charges);
    $scope.getTotal = () => this.getTotal();
    $scope.cancelCharges = () => { $scope.charges = []; this.resetCharges(); };
    $scope.setExternalSystemIdentifiers = (chargeDescription: string) => this.setExternalSystemIdentifiers(chargeDescription);

    $scope.charge = {
      transaction_id: null,
      date: null,
      aerodrome: null,
      flight_id: null,
      charge_description: null,
      other_description: null,
      charge_amount: null,
      registration_number: null
    };


    $scope.aircraftRegistration = {
      registration_number: null,
      registration_start_date: null,
      registration_expiry_date: null,
      mtow_override: null,
      country_override: null,
      account: null,
      aircraft_type: null,
      country_of_registration: null,
      created_by_self_care: null,
      coa_expiry_date: null,
      coa_issue_date: null,
      aircraft_service_date: null,
      is_local: null,
      aircraft_scope : null,
    };

    $scope.exportSupport = false;
    $scope.exportInProcess = false;
    $scope.selectedItems = {};

    $scope.exportAllTransactions = () => this.exportAllTransactions();
    $scope.exportSelectedTransactions = (selected: any) => this.exportSelectedTransactions(selected);
    $scope.isExported = (transaction: ITransaction) => this.isExported(transaction);
    $scope.isExportSupport = (transaction: ITransaction) => this.isExportSupport(transaction);
    $scope.isSelectedItems = (selected: any) => this.isSelectedItems(selected);
    $scope.setExportSupportMechanism = (mechanism: string) => this.setExportSupportMechanism(mechanism);
    $scope.getUnifiedTaxChargesByRegistrationNumberAndByBillingLedgerId = (aircraftRegistrationId: number,billingLedgerId: number) =>this.unifiedTaxManagementService.getUnifiedTaxChargesByAircraftRegistationNumberAndBillingLedgerdId(aircraftRegistrationId,billingLedgerId);


    $scope.setFlightId = (item: IFlightMovement) => {
      const {
        flight_id,
        date_of_flight,
        dep_time,
        item18_reg_num,
        dep_ad, dest_ad,
        enroute_charges,
        aerodrome_charges,
        approach_charges,
        late_arrival_charges,
        late_departure_charges,
        tasp_charge,
        domestic_passenger_charges,
        international_passenger_charges,
        parking_charges
      } = item;

      const regNum = item18_reg_num ? item18_reg_num : '-';
      const dateOfFlight = this.$filter('dateConverter')(date_of_flight);

      $scope.charge.flight_id = `${flight_id}/${dateOfFlight}/${dep_time}/${regNum}/${dep_ad}-${dest_ad}`;
      $scope.chargeFlight = item;

      if (this.$scope.editable.payment_mechanism === 'adjustment' && this.$scope.editable.transaction_type.name === 'credit' && this.$scope.invoiceAdjusted) {
        // reselect chargeTypes
         this.getInvoiceAdjusted(this.$scope.invoiceAdjusted.id);

        // filter chargeTypes based on flight movement's charges
        const showEnrouteCharges = !!enroute_charges;
        const showLandingCharges = !!aerodrome_charges || !!approach_charges || !!late_arrival_charges || !!late_departure_charges || !!tasp_charge;
        const showTaspCharges = !!tasp_charge;
        const showPassengerCharges = !!domestic_passenger_charges || !!international_passenger_charges;
        const showParkingCharges = !!parking_charges;

        if (!showEnrouteCharges) {
          this.$scope.chargeTypes = this.$scope.chargeTypes.filter((chargeType: IChargeTypeScope) => !chargeType.description.toLowerCase().includes('enroute'));
        }

        if (!showLandingCharges) {
          this.$scope.chargeTypes = this.$scope.chargeTypes.filter((chargeType: IChargeTypeScope) => !chargeType.description.toLowerCase().includes('landing'));
        }

        if (!showTaspCharges) {
          this.$scope.chargeTypes = this.$scope.chargeTypes.filter((chargeType: IChargeTypeScope) => !chargeType.description.toLowerCase().includes(this.$scope.taspLabel.toLowerCase()));
        }

        if (!showPassengerCharges) {
          this.$scope.chargeTypes = this.$scope.chargeTypes.filter((chargeType: IChargeTypeScope) => !chargeType.description.toLowerCase().includes('passenger'));
        }

        if (!showParkingCharges) {
          this.$scope.chargeTypes = this.$scope.chargeTypes.filter((chargeType: IChargeTypeScope) => !chargeType.description.toLowerCase().includes('parking'));
        }
      }

    };



    $scope.setAircraftRegistration = (item : IAircraftRegistration) => {
        $scope.aircraftRegistration =  item ;
        $scope.charge.registration_number = item.registration_number;
        let unifiedTaxCharges;
        this.unifiedTaxManagementService.getUnifiedTaxChargesByAircraftRegistationNumberAndBillingLedgerdId(item.id,this.$scope.adjustmentInvoiceId).then((x) =>{
        unifiedTaxCharges = x;
        $scope.charge.charge_amount = unifiedTaxCharges.amount;
    });

       }

    $scope.setAerodrome = (item: IInvoiceLineItem) => {
      $scope.charge.aerodrome = item.aerodrome.aerodrome_name;
      $scope.charge.charge_description = item.service_charge_catalogue.description;
      this.setExternalSystemIdentifiers($scope.charge.charge_description);
    };

    $scope.getLineItemsByInvoiceId = (invoiceId: number) => this.getLineItemsByInvoiceId(invoiceId);
    $scope.getFlightMovementsByInvoiceId = (invoiceId: number) => this.getFlightMovementsByInvoiceId(invoiceId);
    $scope.getAircraftRegistrationByBillingLedgerId = (invoiceId: number) => this.getAircraftRegistrationByBillingLedgerId(invoiceId);

    // define scope payment mechanism functions
    $scope.isPaymentMechanism = (...mechanisms: Array<string>) => this.isPaymentMechanism(mechanisms);

    // define scope bank account properties and functions
    $scope.getBankAccountLabel = (name: string, number: string) => this.bankAccountManagementService.getLabel(name, number);
    $scope.setEditableBankAccount = (bankAccount: IBankAccount) => this.setEditableBankAccount(bankAccount);

    // define scope payment date properties
    $scope.isPaymentDateSupported = this.systemConfigurationService
      .getBooleanFromValueByName(<any>SysConfigConstants.BACKDATE_PAYMENT_ALLOWED);

    // define on change event functions
    $scope.onPaymentMechanismChange = (editable: ITransaction) => this.onPaymentMechanismChange(editable);
    $scope.onTransactionTypeChange = (editable: ITransaction) => this.onTransactionTypeChange(editable);

    // initialize all data requests and trigger on load method once complete
    this.initialize();
  }




  protected create(transaction: ITransaction): ng.IPromise<any> {

    // clone so any changes made to transaction is not flashed to the view
    let data: ITransaction = angular.copy(transaction);

    // change amount values to negative for the backend
    if (this.$scope.transactionFromInvoice) {
      data.amount = -Math.abs(data.amount);
      data.payment_amount = -Math.abs(data.payment_amount);
    }

    // disable form to prevent multiple clicks
    this.$scope.isDisabled = true;

    // use upload method as multiplePart type is used to upload optional file
    // always undo disabling of form after promise complete
    return super.upload('POST', null, data, 'refreshAndHideCreateWizard').finally(() => {
      this.$scope.isDisabled = false;
      this.$scope.transactionFromInvoice = false;
    });
  }

  protected reset(): void {
    super.reset();
    this.resetInvoices();
    this.$scope.currencyList = this.$scope.fullCurrencyList;
    this.$scope.transactionFromInvoice = false;
  }

  private resetInvoices(): void { // used to clear selected invoices upon create / after reset
    this.$scope.amounts = null;
    this.$scope.selectedInvoiceIds = [];
    this.$scope.isDisabled = false;
  }

  /**
   * Populate transaction form using provided transaction parameter genearted from an invoice.
   * This is used on the Billing -> Invoices view to pay a selected invoice.
   *
   * @param transaction transaction to populate
   */
  private setTransactionFromInvoice(transaction: ITransaction): void {
    this.$scope.toggle = true;
    this.$scope.transactionFromInvoice = true;
    this.$scope.editable = this.transactionsService.getModel();
    Object.keys(this.$scope.editable).forEach((item: any) => this.$scope.editable[item] = transaction[item] ? transaction[item] : this.$scope.editable[item]);
    this.transactionTypesService.listAll().then((types: Array<ITransactionType>) =>
      this.$scope.editable.transaction_type = types.find((type: ITransactionType) => type.name === 'credit'));
    this.$scope.editable.payment_mechanism = this.$scope.paymentMechanisms.find((mechanism: string) => mechanism === 'cash');
    this.onPaymentMechanismChange(this.$scope.editable);
    this.updateExchangeRate(this.$scope.editable, true);
  }

  /**
   *
   * This takes all payable invoices and calculates
   * the total amount owed.
   *
   * Additionally, if an invoice amount is exactly
   * equal to the transaction amount added by the user,
   * that invoice will be automatically selected for payment.
   *
   * @param  {Array<IInvoice>} invoices
   * @returns void
   */
  private addInvoices(invoices: Array<IInvoice>): void {
    // if populating a list of unpaid invoices
    // to apply the excess adjustment amount,
    // remove the already adjusted invoice
    if (this.$scope.excessToBePaid) { // adjustment
      invoices = invoices.filter((invoice: IInvoice) => {
        // if adjustment invoice, correct pagenation settings
        if (invoice.id === this.$scope.adjustmentInvoiceId) {
          this.$scope.data.number_of_elements--;
          this.$scope.data.total_elements--;
          this.$scope.data.total_items--;
          return false;
        } else {
          return true;
        }
      });

      // set the excess amount to the total of the charges
      // added, minus the amount owed from the invoice
      // selected for adjustment
      this.$scope.editable.amount = this.$scope.total - this.$scope.amountOwed;

      // for adjustment, exchange rate is always 1
      this.$scope.editable.payment_exchange_rate = 1;
    }

    this.$scope.invoices = invoices;
    let exactMatchId: number = null;

    for (let invoice of this.$scope.invoices) {

      if (Math.abs(this.$scope.editable.amount) === invoice.amount_owing && invoice.amount_owing !== 0 &&
        exactMatchId === null && this.$scope.selectedInvoiceIds.length === 0) {
        exactMatchId = invoice.id;
        break;
      }
    }

    if (this.$scope.editable.transaction_type.name === 'credit') {
      this.$scope.editable.amount = -Math.abs(this.$scope.editable.amount); // convert amount to a negative for the back-end
      this.$scope.editable.payment_amount = -Math.abs(this.$scope.editable.payment_amount); // convert amount to a negative for the back-end
    }

    // if exact match between amount and invoice, calculate for that invoice
    if (exactMatchId) {
      this.calculatePayments(exactMatchId, this.$scope.editable.amount);
    }
  }

  private calculatePayments(id: number, amount: number): void {
    let ids = this.$scope.selectedInvoiceIds;
    let index = ids.indexOf(id);
    index === -1 ? ids.push(id) : ids.splice(ids.indexOf(id), 1); // add/remove id from array
    this.$scope.editable.billing_ledger_ids = ids; // ids of selected invoices

    this.validateSelectedInvoice(ids, id, amount, index === -1);
  }

  private validateSelectedInvoice(ids: Array<number>, id: number, amount: number, subtract: boolean): void {
    if (ids.length > 0) {
      this.service.validateSelectedInvoice(this.$scope.editable).then((resp: IInvoice) => {
        this.$scope.amounts = resp;
        this.updateAmountAvailable(amount, subtract);
      }).catch((response: IRestangularResponse) => this.rejectSelectedInvoice(response, id));
    } else {
      this.$scope.amounts = null;
      this.updateAmountAvailable(amount, subtract);
    }
  }

  private rejectSelectedInvoice(response: IRestangularResponse, id: number): void {

    // handle rejection response appropriately
    this.setErrorResponse(response);

    // remove id from selected billing ledger ids if exists
    let ids: Array<number> = this.$scope.editable.billing_ledger_ids;
    let index: number = ids.indexOf(id);
    if (index !== -1) {
      ids.splice(index, 1);
    }

    // remove id from amounts if exists, must be undefined
    if (this.$scope.amounts && this.$scope.amounts[id] !== undefined) {
      delete(this.$scope.amounts[id]);
    }

    // uncheck invoice if selected
    for (let invoice of this.$scope.invoices) {
      if (invoice.id === id) {
        (<any>invoice).checked = false; // type <any> as checked is not an expected IInvoice property
      }
    }
  }

  private updateAmountAvailable(amount: number, subtract: boolean): void {
    this.$scope.amountAvailable += (subtract ? -Math.abs(amount) : Math.abs(amount));
  }

  /**
   * Update payment exchange rate by even rounding supplied value.
   *
   * @param value exchange rate value
   */
  private updatePaymentExchangeRate(value: number): void {

    // even round exchange rate to 5 decimal places of value
    if (value) {
      if (this.$scope.inverseExchange) {
        this.$scope.paymentExchangeRate = HelperService.evenRound((1 / value), 5).toFixed(5);
      } else {
        this.$scope.paymentExchangeRate = HelperService.evenRound(value, 5).toFixed(5);
      }
    } else {
      this.$scope.paymentExchangeRate = null;
    };
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
   * Update exchange rate from payment currency into local currency. This method will
   * set currency fields to invalid if no exchange rate is found.
   *
   * @param transaction transaction to update exchange rate
   * @param prioritizePayment priority payment value over local value when updating
   */
  private updateExchangeRate(transaction: ITransaction, prioritizePayment: boolean = true): void {
    if (this.$scope.transactionFromInvoice) {
      prioritizePayment = false;
    }

    // validate that provided transaction is defined as it is required
    // return immediately if transaction is not defined
    if (!transaction) {
      return;
    }

    // define from and to currencies based on provided transaction
    let fromCurrency: ICurrency = transaction.payment_currency;
    let toCurrency: ICurrency = transaction.currency;

    // if fromCurrency exists, toCurrency null and fromCurrency in currencyList
    // set toCurrency to fromCurrency for better user experience
    if (fromCurrency && !toCurrency && this.$scope.currencyList.filter((currency: ICurrency) => currency.id === fromCurrency.id).length > 0) {
      toCurrency = fromCurrency;
      this.$scope.editable.currency = fromCurrency;
    }

    // if currency values are empty, return
    // else find exchange rate between currencies
    if (!fromCurrency || !fromCurrency.id || !toCurrency || !toCurrency.id) {
      this.$scope.editable.payment_exchange_rate = null;
      prioritizePayment ? this.updateLocalAmount() : this.updatePaymentAmount();
    } else {
      this.currencyExchangeRatesService.getExchangeRateByCurrencyId(fromCurrency.id, toCurrency.id, transaction.payment_date || null)
        .then((data: number) => {
          this.$scope.editable.payment_exchange_rate = data;
          this.$scope.form.currency.$setValidity('valid_exchange_rate', true);
          this.$scope.form.paymentCurrency.$setValidity('valid_exchange_rate', true);
        }).catch((error: IRestangularResponse) => {
          this.$scope.editable.payment_exchange_rate = null;
          this.$scope.form.currency.$setValidity('valid_exchange_rate', false);
          this.$scope.form.paymentCurrency.$setValidity('valid_exchange_rate', false);
          this.$scope.error = { error };
        }).finally(() => {
          prioritizePayment ? this.updateLocalAmount() : this.updatePaymentAmount();
        });
    }
  }

  /**
   * Update local amount from payment amount and exchange rate.
   */
  private updateLocalAmount(): void {

    // if exchange call to service already pending, cancel and send another
    // use one timeout for both local and payment amounts so that if user
    // switches input controls and updates before result is returned, their new value
    // isn't overwritten
    if (this._exchangeTimeout) {
      this.$timeout.cancel(this._exchangeTimeout);
    }

    // get all values from editable form
    var amount: number = this.$scope.editable.payment_amount;
    var rate: number = this.$scope.editable.payment_exchange_rate;
    var currency: ICurrency = this.$scope.editable.currency;
    var inverse: boolean = false;

    // if amount, exchange and currency are null, return
    // else calculate exchange in browser (emulated banker's rounding)
    // and after custom debounce of 800 milliseconds, get calcualtion from service (so that banker's rounding is consistent)
    if (!amount && amount !== 0 || !rate || !currency) {
      this.$scope.editable.amount = null;
    } else {

      // initially get amount from emulated banker's rounding in browser
      // this will provide immediate feedback to the user
      this.$scope.editable.amount = this.calculateAmount(amount, rate, currency, inverse);

      // send call to service to get calcualted exchange amount
      // wait 800 milliseconds before sending
      this._exchangeTimeout = this.$timeout(() => {
        this.currencyExchangeRatesService.getExchangeAmount(amount, rate, currency.decimal_places || 2, inverse)
          .then((data: number) => {
            this.$scope.editable.amount = data;
          }).catch((Error: IRestangularResponse) => {
            // fallback to manual calculations
            this.$scope.editable.amount = this.calculateAmount(amount, rate, currency, inverse);
          });
      }, 800);
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
    var amount: number = this.$scope.editable.amount;
    var rate: number = this.$scope.editable.payment_exchange_rate;
    var currency: ICurrency = this.$scope.editable.payment_currency;
    var inverse: boolean = true;

    // if amount, exchange and currency are null, return
    // else calculate exchange in browser (emulated banker's rounding)
    // and after custom debounce of 800 milliseconds, get calcualtion from service (so that banker's rounding is consistent)
    if (!amount && amount !== 0 || !rate || !currency) {
      this.$scope.editable.payment_amount = null;
    } else {

      // initially get amount from emulated banker's rounding in browser
      // this will provide immediate feedback to the user
      this.$scope.editable.payment_amount = this.calculateAmount(amount, rate, currency, inverse);

      // send call to service to get calcualted exchange amount
      // wait 800 milliseconds before sending
      this._exchangeTimeout = this.$timeout(() => {
        this.currencyExchangeRatesService.getExchangeAmount(amount, rate, currency.decimal_places || 2, inverse)
          .then((data: number) => {
            this.$scope.editable.payment_amount = data;
          }).catch((Error: IRestangularResponse) => {
            // fallback to manual calculations
            this.$scope.editable.payment_amount = this.calculateAmount(amount, rate, currency, inverse);
          });
      }, 800);
    }
  }

  /**
   * This runs when clicking a checkbox to select
   * an invoice for adjustment
   * @param  {number} position - index of selected invoice for adjustment
   * @param  {number} id - id of invoice to adjust
   * @param  {boolean} checked - used to indicate of an invoice has been selected
   * @param  {number} amountOwed - amount owed on selected invoice
   * @returns void
   */
  private getCheckbox(position: number, id: number, checked: boolean, amountOwed: number): void {
    // user may only select one invoice to adjust
    // after, if there is excess credit, the user
    // may select any number of 'unpaid' invoices to pay
    this.$scope.editable.billing_ledger_ids = [];
    this.$scope.amountOwed = amountOwed;
    angular.forEach(this.$scope.invoices, (subscription: any, index: number): void => {
      if (position !== index) {
        subscription.checked = false;
      }
    });

    // capture invoice to be adjusted
    this.$scope.adjustmentInvoiceId = id;
    this.$scope.invoiceChecked = checked;
  }

  private addCharges(charge: IAdjustmentCharge): void {

    // resolve flight movement external charge values
    this.chargeAdjustmentsService.resolveFlightAdjustmentExternal(charge, this.$scope.chargeFlight, this.$scope.invoiceAdjusted.invoice_type);

    // store charge flight for editing purposes
    this.$scope.chargeFlights = this.$scope.chargeFlights || [];
    this.$scope.chargeFlights.push(angular.copy(this.$scope.chargeFlight || {}));

    let copyCharge = angular.copy(charge);
    this.$scope.charges.push(copyCharge);

    // get charge type from scope using description supplied
    let chargeType = this.$scope.chargeTypes
      ? this.$scope.chargeTypes.find((item: IChargeTypeScope) => item.external_accounting_system_identifier && item.description === charge.charge_description)
      : null;

    // limit subsequent charge types to similiar externalChargeCategory
    if (chargeType && chargeType.external_charge_category && this.$scope.invoiceAdjusted && this.$scope.invoiceAdjusted.invoice_type === 'non-aviation') {
      this.$scope.chargeTypes = this.$scope.catalogueCharges.filter((item: IChargeTypeScope) =>
        item.external_charge_category && item.external_charge_category.id === chargeType.external_charge_category.id);
    } else if (this.$scope.invoiceAdjusted && this.$scope.invoiceAdjusted.invoice_type === 'non-aviation') {
      this.$scope.chargeTypes = this.$scope.catalogueCharges;
    }

    this.resetCharges();
  }

  /**
   * For adjustment transactions,
   * Clears the add charges form
   * @returns void
   */
  private resetCharges(): void {
    this.$scope.charge = {
      date: null,
      aerodrome: null,
      flight_id: null,
      charge_description: null,
      other_description: null,
      charge_amount: null,
      transaction_id: null,
      external_accounting_system_identifier: null
    };
    this.$scope.chargeIndex = undefined;
    this.$scope.chargeFlight = undefined;
  }

  private editCharges(data: IAdjustmentCharge, index: number): void {
    this.$scope.charge = angular.copy(data);
    this.$scope.chargeFlight = angular.copy(this.$scope.chargeFlights[index]);
    this.$scope.chargeIndex = index;
  }

  /**
   * For adjustment transactions
   * Updates charges
   * @param  {IAdjustmentCharge} charge
   * @returns void
   */
  private updateCharges(charge: IAdjustmentCharge): void {

    // resolve flight movement external charge values
    this.chargeAdjustmentsService.resolveFlightAdjustmentExternal(charge, this.$scope.chargeFlight, this.$scope.invoiceAdjusted.invoice_type);

    for (let item in this.$scope.charges) {
      if (this.$scope.chargeIndex === parseInt(item, 10)) {
        this.$scope.charges[parseInt(item, 10)] = angular.copy(charge);
      }
    }
    this.resetCharges();
  }

  /**
   * For adjustment transactions
   * Deletes charges
   * @returns void
   */
  private deleteCharges(): void {
    this.$scope.charges.splice(this.$scope.chargeIndex, 1);
    this.$scope.chargeFlights.splice(this.$scope.chargeIndex, 1);

    // reset catalogue charges if no charges left
    if (this.$scope.charges.length < 1 && this.$scope.invoiceAdjusted && this.$scope.invoiceAdjusted.invoice_type === 'non-aviation') {
      this.$scope.chargeTypes = this.$scope.catalogueCharges;
    }

    this.resetCharges();
  }

  /**
   * For adjustment transactions, this sets the
   * different available charge types, depending
   * on the type of invoice
   *
   * @param  {number} id
   * @returns void
   */
  private getInvoiceAdjusted(id: number): void {
    for (let invoice of this.$scope.invoices) {
      if (invoice.id === id) {
        this.$scope.invoiceAdjusted = invoice;
        switch (invoice.invoice_type) {
          case 'aviation-iata':
            this.$scope.chargeTypes = this.chargeAdjustmentsService.iataChargeTypes();
            break;
          case 'aviation-noniata':
            this.$scope.chargeTypes = this.chargeAdjustmentsService.nonIataChargeTypes();
            break;
            case 'unified-tax':
              this.$scope.chargeTypes = this.chargeAdjustmentsService.unifiedTaxChargeTypes();
            break;
          case 'non-aviation':
            this.$scope.chargeTypes = this.$scope.catalogueCharges;
            break;
          case 'debit-note':
            this.$scope.chargeTypes = [{ description: <any> ChargesAdjustmentType.OTHER_CHARGES }];
            break;

          default:
            this.$scope.chargeTypes = [];
        }
      }
    }
  }

  /**
   * Returns the total amount of all
   * the added adjustment charges
   * @returns number
   */
  private getTotal(): number {
    this.$scope.total = 0;
    for (let i = 0; i < this.$scope.charges.length; i++) {
      let item = this.$scope.charges[i];
      this.$scope.total += (item.charge_amount);
    }

    // perform rounding due to floating point percision issue in JavaScript
    let percision: number = this.$scope.editable && this.$scope.editable.currency
      ? this.$scope.editable.currency.decimal_places : 2;
    this.$scope.total = HelperService.evenRound(this.$scope.total, percision);

    return this.$scope.total;
  }

  /**
   * Adds adjustment charges to the editable
   * object and creates a credit or debit note
   *
   * @param  {ITransaction} transaction
   * @param  {IAdjustmentCharge} charges
   * @returns ng
   */
  private createNote(transaction: ITransaction, charges: IAdjustmentCharge): ng.IPromise<any> {
    this.$scope.isDisabledCreateNote = true;
    this.$scope.editable.charges_adjustment = [];
    for (let item of angular.copy(this.$scope.charges)) {
      // the charge amount must be equal to the local amount
      // as the local amount is a negative, here we convert
      // the charge amounts to negatives
      if (this.$scope.editable.transaction_type.name === 'credit') {
        item.charge_amount = -Math.abs(item.charge_amount);
      }

      if (item.other_description !== null) {
        item.charge_description = item.other_description;
      }
      this.$scope.editable.charges_adjustment.push(item);
    }

    // here we add the id of the invoice to be adjusted
    // to the beginning of the array. This must be done
    // only *after* unpaid invoices (if any) are selected
    // for payment with surplus credit charges
    if (!this.$scope.editable.billing_ledger_ids.includes(this.$scope.adjustmentInvoiceId)) {
      this.$scope.editable.billing_ledger_ids.unshift(this.$scope.adjustmentInvoiceId);
    }

    // set the amount and payment_amount
    // to the added total of the adjustment charges
    // currently there are no exchange rate conversions
    // applied to adjustement charges, the invoice
    // must be paid in the invoice currency
    this.$scope.editable.amount = this.getTotal();
    this.$scope.editable.payment_amount = this.getTotal();

    // no ability to adjust in other other currencies,
    // set the payment_currency to the local currency
    this.$scope.editable.payment_currency = this.$scope.editable.currency;

    // for adjustment, exchange rate is always 1
    this.$scope.editable.payment_exchange_rate = 1;

    // if crediting, convert local/payment amounts to
    // a negative, to reduce the balance (owed)
    if (this.$scope.editable.transaction_type.name === 'credit') {
      this.$scope.editable.amount = -Math.abs(this.$scope.editable.amount);
      this.$scope.editable.payment_amount = -Math.abs(this.$scope.editable.payment_amount);
    }

    // create transaction and return promise
    return this.create(transaction).then(() => this.$scope.isDisabledCreateNote = false);
  }

  /**
   * Set scope charge external system identifiers from selected
   * charge type.
   *
   * @param chargeDescription description of charge adjustement
   */
  private setExternalSystemIdentifiers(chargeDescription: string): void {

    // get charge type from scope using description supplied
    let chargeType = this.$scope.chargeTypes
      ? this.$scope.chargeTypes.find((item: IChargeTypeScope) => item.description === chargeDescription)
      : null;

    // set by charge type found with external account system identifier if exists
    this.$scope.charge.external_accounting_system_identifier = chargeType && chargeType.external_accounting_system_identifier
      ? chargeType.external_accounting_system_identifier
      : null;

    // set by charge type found with external charge category if exsits
    this.$scope.charge.external_charge_category_name = chargeType && chargeType.external_charge_category
      ? chargeType.external_charge_category.name
      : null;
  }

  private getFilterParameters(): void {
    let startDate: string;
    let endDate: string;

    if (this.$scope.control && this.$scope.control.getUTCStartDate()) {
      startDate = this.$scope.control.getUTCStartDate().toISOString().substr(0, 10);
    }

    if (this.$scope.control && this.$scope.control.getUTCEndDate()) {
      endDate = this.$scope.control.getUTCEndDate().toISOString().substr(0, 10);
    }

    this.$scope.filterParameters = {
      searchFilter: this.$scope.textFilter,
      exportedFilter: this.$scope.exportedFilter === true ? false : null, // since "non-exported" only set false when checked
      page: this.$scope.pagination ? this.$scope.pagination.number : 0,
      startDate: startDate,
      endDate: endDate,
      account: this.$scope.accountFilter
    };
  }

  /**
   * Refresh data list in scope.
   */
  private refreshList(): ng.IPromise<any> {
    this.getFilterParameters();
    return this.$scope.refresh(this.$scope.filterParameters, this.$scope.getSortQueryString());
  }

  /**
   * Determine if any transaction items are selected.
   *
   * @param selectedItems transaction id items selected
   */
  private isSelectedItems(selectedItems: any): boolean {
    return Object.keys(selectedItems)
      .filter((key: string) => selectedItems[key])
      .length > 0;
  }

  /**
   * Export all non-exported transactions.
   */
  private exportAllTransactions(): void {
    this.$scope.exportInProcess = true;
    this.transactionsService.exportAllTransactions()
      .then((response: any) => this.handleExportSuccess(response))
      .catch((response: IRestangularResponse) => this.handleExportError(response))
      .finally(() => this.handleExportFinished(this.$scope.selectedItems));
  }

  /**
   * Export a list of transaction ids.
   *
   * @param selectedItems transaction ids to export
   */
  private exportSelectedTransactions(selectedItems: any): void {
    this.$scope.exportInProcess = true;

    const ids: Array<number> = Object.keys(selectedItems)
      .filter((key: string) => selectedItems[key])
      .map(Number);

    this.transactionsService.exportSelectedTransactions(ids)
      .then((response: any) => this.handleExportSuccess(response))
      .catch((response: IRestangularResponse) => this.handleExportError(response))
      .finally(() => this.handleExportFinished(selectedItems));
  }

  /**
   * Clear any error messages away and display success message.
   */
  private handleExportSuccess(response: any): void {
    this.$scope.exportSuccess = true;
    this.$scope.error = null;
  }

  /**
   * Clear success message and display error message without subtitle.
   *
   * @param response error response
   */
  private handleExportError(response: IRestangularResponse): void {
    this.$scope.exportSuccess = false;
    response.data.hide_subtitle = true;
    this.setErrorResponse(response);
  }

  /**
   * Reset view and makr export in process as false.
   *
   * @param selectedItems items to uncheck
   */
  private handleExportFinished(selectedItems: any): void {

    // deselect ALL items
    if (selectedItems) {
      Object.keys(selectedItems).forEach((key: string) => this.$scope.selectedItems[key] = false);
    }

    // reset view list
    this.$scope.exportInProcess = false;
    this.refreshList();
  }

  private isExported(transaction: ITransaction): boolean {

    let result: boolean = false;

    if (this.exportSupportMechanism[transaction.payment_mechanism] === true) {
      result = transaction.payment_mechanism === 'adjustment'
        ? this.isExportedAdjustment(transaction)
        : this.isExportedPayment(transaction);
    }

    return result;
  }

  private isExportedAdjustment(transaction: ITransaction): boolean {

    let result: boolean = false;

    if (this.exportSupport.credit_notes === true && this.exportSupport.payments === true) {
      result = transaction.exported && transaction.payments_exported;
    } else if (this.exportSupport.credit_notes === true) {
      result = transaction.exported;
    } else if (this.exportSupport.payments === true) {
      result = transaction.payments_exported;
    }

    return result;
  }

  private isExportedPayment(transaction: ITransaction): boolean {
    let result: boolean = false;

    if (this.exportSupport.payments === true) {
      result = transaction.payments_exported;
    }

    return result;
  }

  /**
   * Validate if transaction is available for exporting.
   *
   * @param transaction transaction item to validate
   */
  private isExportSupport(transaction: ITransaction): boolean {

    const exportSupport: boolean = transaction.payment_mechanism === 'adjustment'
      ? this.exportSupport.credit_notes || this.exportSupport.payments
      : this.exportSupport.payments;

    // supported for export if credit type, export supported, and mechanism is supported
    const result: boolean = transaction.transaction_type.name === 'credit' && exportSupport &&
      this.exportSupportMechanism[transaction.payment_mechanism];

    // make sure item is not already selected if support is false
    if (!result && this.$scope.selectedItems[transaction.id]) {
      this.$scope.selectedItems[transaction.id] = false;
    }

    return result;
  }

  /**
   * Set export support for transaction mechansim and cache for reuse. Should
   * reset on page change/reload.
   *
   * @param mechanism transaction mechansim support to set
   */
  private setExportSupportMechanism(mechanism: string): void {
    if (this.exportSupportMechanism[mechanism] !== false && this.exportSupportMechanism[mechanism] !== true) {
      this.exportSupportMechanism[mechanism] = false;
      this.transactionsService.exportSupportMechanism(mechanism)
        .then((result: boolean) => this.exportSupportMechanism[mechanism] = result);
    }
  }

  /**
   * Refresh list and then hide create wizard.
   */
  private refreshAndHideCreateWizard(result: ITransaction): ng.IPromise<any> {
    return this.refreshList().then(() => this.hideCreateWizard(result));
  }

  /**
   * Hide and reset transaction creation wizard.
   */
  private hideCreateWizard(result: ITransaction): void {
    this.$scope.invoiceAdjustedTextFilter = null;

    this.$scope.excessToBePaid = false;
    this.$scope.invoiceChecked = false;

    this.$scope.showInvoices = false;
    this.$scope.showCharges = false;

    this.$scope.cancelCharges();
    this.$scope.reset();
    this.$scope.form.$setUntouched();

    if (result && result.interest_invoice_error) {
      this.$scope.interestInvoiceError = result.interest_invoice_error;
    }
  }

  /**
   * Show transaction charge wizard for adjustments.
   *
   * @param editable transaction to create
   */
  private showChargeWizard(editable: ITransaction): void {
    this.$scope.showCharges = true;
  }

  /**
   * Show transcation creation wizard.
   *
   * @param editable transaction to create
   */
  private showCreateWizard(editable: ITransaction): void {
    if (editable.payment_mechanism === 'adjustment') {
      this.getAllInvoicesByAccountIdAndCurrency(editable.account.id, editable.currency.id);
    } else {
      this.getUnpaidInvoicesByAccountIdAndCurrency(editable.account.id, editable.currency.id);
    }

    this.getTotalAmount(editable.account.id, editable.currency.id);
    this.$scope.amountAvailable = Math.abs(editable.amount) || 0;

    this.$scope.showInvoices = true;
    this.$scope.showCharges = false;
  }

  /**
   * Show transaction excess wizard for adjustments.
   *
   * @param editable adjustment transaction with excess
   */
  private showExcessWizard(editable: ITransaction): void {
    this.getUnpaidInvoicesByAccountIdAndCurrency(editable.account.id, editable.currency.id, true);

    this.$scope.amountAvailable = Math.abs(this.$scope.total - this.$scope.amountOwed) || 0;

    this.$scope.showInvoices = true;
    this.$scope.showCharges = false;
    this.$scope.invoiceAdjustedTextFilter = null;
  }

  /**
   * Get all unpaid invoices by account and currency.
   *
   * @param accountId account id
   * @param currencyId currency id
   * @param excessTobePaid excess to be paid
   * @param page page number
   */
  private getUnpaidInvoicesByAccountIdAndCurrency(accountId: number, currencyId: number, excessTobePaid?: boolean, page?: number, sort?: string): ng.IPromise<IInvoiceSpring> {
    return this.invoicesService.getUnpaidInvoices(accountId, currencyId, page, sort)
      .then((data: IInvoiceSpring) => {
        this.$scope.excessToBePaid = excessTobePaid || false;
        this.$scope.data = data; this.addInvoices(data.content);
        return data;
      });
  }

  /**
   * Get all invoices by account id and currency.
   *
   * @param accountId acount id
   * @param currencyId currency id
   * @param page page number
   */
  private getAllInvoicesByAccountIdAndCurrency(accountId: number, currencyId: number, page?: number, sort?: string): ng.IPromise<IInvoiceSpring> {
    return this.invoicesService.getAllInvoicesByAccountIdAndCurrency(accountId, currencyId, page, sort)
      .then((data: IInvoiceSpring) => {
        this.$scope.data = data;
        this.addInvoices(data.content);
        return data;
      });
  }

  /**
   * Refresh invoice list depending create wizard editable state.
   *
   * @param accountId account id
   * @param currencyId currency id
   * @param page page number
   */
  private refreshInvoiceList(accountId: number, currencyId: number, page?: number): ng.IPromise<IInvoiceSpring> | void {
    if (this.$scope.editable.payment_mechanism === 'adjustment' && !this.$scope.excessToBePaid) {
      this.$scope.invoiceChecked = false;
      return this.getAllInvoicesByAccountIdAndCurrency(accountId, currencyId, page, this.$scope.getInvoiceListSortQueryString());
    } else if (this.$scope.editable.payment_mechanism !== 'adjustment' || this.$scope.excessToBePaid) {
      return this.getUnpaidInvoicesByAccountIdAndCurrency(accountId, currencyId, this.$scope.excessToBePaid, page, this.$scope.getInvoiceListSortQueryString());
    } else {
      return null;
    }
  }

  /**
   * Get total amount owned amount from all invoices.
   *
   * @param accountId account id
   * @param currencyId currency id
   */
  private getTotalAmount(accountId: number, currencyId: number): void {
    this.invoicesService.getTotalAmountForInvoices(accountId, currencyId)
      .then((amount: number) => this.$scope.totalAmountForAllInvoices = amount);
  }

  // for aviation invoices, returns a list of flight movements by related invoice id
  private getFlightMovementsByInvoiceId(invoiceId: number): ng.IPromise<Array<IFlightMovement>> {
    return this.flightMovementManagementService.findAllFlightMovementsListByAssociatedInvoiceId(invoiceId)
    .then((listFlightMovements: Array<IFlightMovement>) => this.$scope.listFlightMovements = listFlightMovements);
  }

  // for non-aviation invoices, returns associated line items by related invoice id
  private getLineItemsByInvoiceId(invoiceId: number): ng.IPromise<Array<IInvoiceLineItem>> {
    return this.invoicesService.getLineItemsByInvoiceId(invoiceId)
      .then((lineItems: Array<IInvoiceLineItem>) => this.$scope.lineItems = lineItems);
  }

  // for unified-tax invoices
 private getAircraftRegistrationByBillingLedgerId(invoiceId: number): ng.IPromise<Array<IAircraftRegistration>> {
   this.$scope.billingLedgedIdSelected = invoiceId;
    return this.aircraftRegistrationService.getAircraftRegistrationByBillingLedgerId(invoiceId)
    .then((listAircraftRegstration: Array<IAircraftRegistration>) => this.$scope.listAircraftRegstration = listAircraftRegstration);
     }




  /**
   * Edit override for transactions.
   */
  private editTransaction(item: ITransaction): void {
    super.edit(item);

    // force editable amounts to absolute value
    this.$scope.editable.amount = Math.abs(item.amount);
    this.$scope.editable.payment_amount = Math.abs(item.payment_amount);

    this.$scope.listOfApprovals = item.transaction_approvals;

    // define crud file upload file name to suppporting document name if exists
    this.$scope.editable.document_filename = item.supporting_document_name;

    // get all invoices related to transaction
    this.$scope.getInvoicesByTransactionId(item.id);

    // get all transaction payments related to transaction
    this.$scope.getTransactionPaymentsByTransactionId(item.id);
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

    let mechanism: string = this.$scope.editable
      ? this.$scope.editable.payment_mechanism : null;

    return mechanisms.indexOf(mechanism) > -1;
  }

  /**
   * Set editable transaction's bank account name, number, externalId from
   * provided bank account object.
   *
   * @param bankAccount bank account info to use
   */
  private setEditableBankAccount(bankAccount: IBankAccount): void {
    if (bankAccount) {
      this.$scope.editable.bank_account_name = bankAccount.name;
      this.$scope.editable.bank_account_number = bankAccount.number;
      this.$scope.editable.bank_account_external_accounting_system_id = bankAccount.external_accounting_system_id;
    } else {
      this.$scope.editable.bank_account_name = null;
      this.$scope.editable.bank_account_number = null;
      this.$scope.editable.bank_account_external_accounting_system_id = null;
    }
  }

  /**
   * On payment mechanism change event, updated editable transaction properties.
   *
   * @param editable editable transaction
   */
  private onPaymentMechanismChange(editable: ITransaction): void {

    // set payment reference number to 'N/A' if payment mechanism is cash or adjustment
    editable.payment_reference_number = this.isPaymentMechanism(['adjustment', 'cash'])
      ? 'N/A' : editable.payment_reference_number !== 'N/A' ? editable.payment_reference_number : null;

    // on payment amount adjustment, updated amounts and currecy related properties
    if (this.isPaymentMechanism(['adjustment'])) {
      editable.amount = null;
      editable.payment_amount = null;
      editable.payment_currency = null;
      editable.payment_exchange_rate = null;
    }

    // set file upload disbaled status based on payment mechanism
    this.$scope.fileUploadDisabled = !(this.isPaymentMechanism(['adjustment']) && editable.transaction_type.name === 'credit');

    // on payment mechanism other than cheque or wire, clear bank account and payment date
    if (!this.isPaymentMechanism(['cheque', 'wire'])) {
      (<any>editable).bank_account = null; // type <any> as bank_account is not an expected ITransaction property
      editable.bank_account_name = null;
      editable.bank_account_number = null;
      editable.bank_account_external_accounting_system_id = null;
      editable.payment_date = null;
    }
  }

  /**
   * On transaction type change event, updated editable transaction properties
   *
   * @param editable editable transaction
   */
  private onTransactionTypeChange(editable: ITransaction): void {

    // set payment reference number to 'N/A' if transaction type is 'debit' or
    // transaction type is 'credit' and payment mechanism is 'adjustment' or 'cash'
    editable.payment_reference_number = editable.transaction_type.name === 'debit' ||
    editable.transaction_type.name === 'credit' && this.isPaymentMechanism(['adjustment', 'cash'])
     ? 'N/A' : null;

    editable.payment_mechanism = editable.transaction_type.name === 'debit' ? 'adjustment' : editable.payment_mechanism;

    // set file upload disbaled status based on payment mechanism
    this.$scope.fileUploadDisabled = !(this.isPaymentMechanism(['adjustment']) && editable.transaction_type.name === 'credit');

  }

  /**
   * Request and load bank acounts into scope.
   */
  private loadBankAccounts(): angular.IPromise<any> {
    // get bank account support and define in scope
    return this.bankAccountManagementService.getIsSupported()
      .then((isSupported: boolean) => this.$scope.isBankAccountSupported = isSupported);
  }

  /**
   * Request and load catalogue service charges into scope.
   */
  private loadCatagolueCharges(): angular.IPromise<any> {
    this.$scope.catalogueCharges = [];
    return this.catalogueServiceChargeService.listAll().then((catalogue: ICatalogueServiceChargeTypeSpring) => {
      catalogue.content.forEach((item: ICatalogueServiceChargeType) => {
        this.$scope.catalogueCharges.push({
          description: item.description,
          external_accounting_system_identifier: item.external_accounting_system_identifier,
          external_charge_category: item.external_charge_category
        });
      });
      this.$scope.catalogueCharges.push({ description: 'All charges' });
    });
  }

  /**
   * Request and load currencies into scope.
   */
  private loadCurrencies(): angular.IPromise<any> {
    // initially display all currencies until an account is selected
    return this.currencyManagementService.listAll()
      .then((data: ICurrencySpring) => {
        this.$scope.fullCurrencyList = data.content;
        this.$scope.currencyList = data.content;
      });
  }

  /**
   * Set export support for transaction credit note and payment types. Should be set on
   * page load.
   */
  private loadExportSupport(): angular.IPromise<any> {
    return this.transactionsService.exportSupport().then((response: ITransactionExportSupport) => {
      this.exportSupport = response;
      this.$scope.exportSupport = this.exportSupport.credit_notes === true || this.exportSupport.payments === true;
    });
  }

  /**
   * Request and load payment mechanisms into scope.
   */
  private loadPaymentMechanisms(): angular.IPromise<any> {
    return this.transactionsService.getPaymentMechanismList()
      .then((data: Array<string>) => this.$scope.paymentMechanisms = data);
  }

  /**
   * Initialize all data requests and trigger on load method.
   */
  private initialize(): angular.IPromise<any> {
    return this.$q.all([
      this.loadPaymentMechanisms(), this.loadCurrencies(), this.loadCatagolueCharges(),
      this.loadExportSupport(), this.loadBankAccounts()
    ]).then(() => this.onLoad());
  }



  /**
   * Called after all data requests have been loaded into the scope.
   */
  private onLoad(): void {

    // if transactions was called from Invoices
    if (this.$state.params.transaction) {
      this.setTransactionFromInvoice(this.$state.params.transaction);
    }
  }

  private disableCreateNote(charges: Array<IAdjustmentCharge>): boolean {
    return !charges.length || (this.$scope.amountOwed === 0 && this.$scope.editable.transaction_type.name === 'credit') ||
      (this.$scope.total > this.$scope.amountOwed && this.$scope.editable.transaction_type.name === 'credit') || this.$scope.isDisabledCreateNote;
  }
}
