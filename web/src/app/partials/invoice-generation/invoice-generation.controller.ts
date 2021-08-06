// interface
import { InvoiceGenerationScope, IAviationInvoice, IChargeItemForm } from './invoice-generation.interface';
import { ICatalogueServiceChargeType } from '../catalogue-service-charge/catalogue-service-charge.interface';
import { IFlightMovement, IFlightMovementSpring } from '../flight-movement-management/flight-movement-management.interface';
import { IRestangularResponse } from '../../angular-ids-project/src/helpers/interfaces/restangularError.interface';
import { IAccount, IAccountMinimal } from '../accounts/accounts.interface';
import { IAerodrome } from '../aerodromes/aerodromes.interface';
import { IInvoiceLineItem } from '../line-item/line-item.interface';
import { IAircraftTypeMinimal } from '../aircraft-type-management/aircraft-type-management.interface';
import { IFlightMovementCategory } from '../../partials/flight-movement-category/flight-movement-category.interface';
import { INonAviationInvoicePayload } from '../reports/repots.interface';

// services
import { ReportsService } from '../reports/service/reports.service';
import { AccountsService } from '../accounts/service/accounts.service';
import { AerodromesService } from '../aerodromes/service/aerodromes.service';
import { SystemConfigurationService } from '../system-configuration/service/system-configuration.service';
import { AircraftTypeManagementService } from '../aircraft-type-management/service/aircraft-type-management.service';
import { PluginsService } from '../plugins/service/plugins.service';
import { FlightMovementCategoryService } from '../../partials/flight-movement-category/service/flight-movement-category.service';
import { SysConfigBoolean } from '../../angular-ids-project/src/components/services/sysConfigBoolean/sysConfigBoolean.service';
import { CurrencyManagementService } from '../currency-management/service/currency-management.service';
import { LocalStorageService } from '../../angular-ids-project/src/components/services/localStorage/localStorage.service';

// objects
import { LineItem } from '../line-item/line-item';
import { IExternalChargeCategory } from '../external-charge-category/external-charge-category.interface';

// constants
import { SysConfigConstants } from '../system-configuration/system-configuration.constants';
import { ExternalDatabaseInputService } from '../../components/directives/external-database-input/external-database-input.service';
import { IAdhocFee } from '../../components/directives/external-database-input/external-database-input.directive';
import { ICurrency } from '../currency-management/currency-management.interface';

// lodash
import { sortBy } from 'lodash';

export class InvoiceGenerationController {

  private catalogueServiceChargeTypes: Array<ICatalogueServiceChargeType>;

  /* @ngInject */
  constructor (private $scope: InvoiceGenerationScope, private $uibModal: ng.ui.bootstrap.IModalService, private $state: ng.ui.IStateService,
    private aerodromesService: AerodromesService, private accountsService: AccountsService, private systemConfigurationService: SystemConfigurationService,
    private reportsService: ReportsService, private $translate: angular.translate.ITranslateService, private aircraftTypeManagementService: AircraftTypeManagementService,
    private externalDatabaseInputService: ExternalDatabaseInputService, private pluginsService: PluginsService, private flightMovementCategoryService: FlightMovementCategoryService,
    private sysConfigBoolean: SysConfigBoolean, private currencyManagementService: CurrencyManagementService) {

    $scope.workflow = systemConfigurationService.getValueByName(<any>SysConfigConstants.POINT_OF_SALE_WORKFLOW);
    $scope.showProforma = LocalStorageService.getBooleanFromValueByName(`SystemConfiguration:${SysConfigConstants.PROFORMA_INVOICE_SUPPORT}`);

    aircraftTypeManagementService.findAllMinimalReturn().then((data: Array<IAircraftTypeMinimal>) => this.$scope.aircraftTypesList = data);

    $scope.invoiceByFmCategory = sysConfigBoolean.parse(this.systemConfigurationService.getValueByName(<any>SysConfigConstants.INVOICE_FM_CATEGORY) || 'f');
    // $scope.showDisclaimer = () => this.showDisclaimer();
    $scope.generateButtonIsDisabled = () => this.generateButtonIsDisabled();
    $scope.invoiceByFMCategory = this.systemConfigurationService.getBooleanFromValueByName(<any>SysConfigConstants.INVOICE_FM_CATEGORY);
    if ($scope.invoiceByFMCategory) {
      $scope.notification_text = 'Note: Cannot generate an invoice when the selected flights are not pending' 
    
    } 
    



    if ($scope.invoiceByFmCategory) {
      flightMovementCategoryService.getFlightMovementCategoryForAviationInvoice().then((resp: Array<IFlightMovementCategory>) => {
        $scope.flightCategories = resp;
        $scope.editable = {
          flightCategory: resp[0].id
        };
      });
    }

    // options
    $scope.saleTypes = [
      {
        n: 'Aviation', v: 'aviation'
      },
      {
        n: 'General', v: 'general'
      }
    ];

    // generic
    $scope.createAccount = () => $state.go('main.accounts', { // go to accounts but come back after create
      after: {
        create: {
          goTo: {
            state: 'main.invoice-generation',
            params: null
          }
        }
      }
    });

    // generic
    $scope.createAircraftRegistration = () => $state.go('main.aircraft-registration', { // go to aircraft registration but come back after create
      after: {
        create: {
          goTo: {
            state: 'main.invoice-generation',
            params: null
          }
        }
      }
    });

    // watch for account change and get invoice currency for selected account
    // this is required as we only pull the minified account list for account dropdown selection
    $scope.$watch('account', (account: IAccount) => this.updateAccountCurrencyCode(account));

    // if coming back from `account` page after creating an account, will pre-populate the form
    if ($state.params.accountId && !isNaN($state.params.accountId)) {
      accountsService.get($state.params.accountId).then((account: IAccount) => $scope.account = account);
    }

    $scope.defaultAccountSelection = LocalStorageService.get(`SystemConfiguration:${SysConfigConstants.POINT_OF_SALE_DEFAULT_ACCOUNT_SELECTION}`);

    $scope.$watch('defaultAccountSelection', (accountSelection: string) => {
      if (accountSelection === 'Credit Accounts') {
        $scope.accounts_type = 'credit';
      } else if (accountSelection === 'Cash Accounts') {
        $scope.accounts_type = 'cash';
      } else {
        $scope.accounts_type = 'all';
      }
    });

    accountsService.findAllCashMinimalReturn(true).then((accounts: Array<IAccountMinimal>) => {
      $scope.accountsListCash = accounts;

      accountsService.findAllCreditMinimalReturn(true).then((accounts: Array<IAccountMinimal>) => {
        $scope.accountsListCredit = accounts;

        $scope.accountsListAll = $scope.accountsListCash.concat($scope.accountsListCredit);
        $scope.accountsListAll = sortBy($scope.accountsListAll, (item: IAccountMinimal) => {
            return item.name.toLowerCase();
        });

        this.getAccounts();
      });
    });

    $scope.getAccounts = () => this.getAccounts();
    $scope.removePreview = () => this.removePreview();
    $scope.recalculateInvoiceAmount = () => this.recalculateInvoiceAmount();

    // aviation related
    $scope.getNonInvoicedFlightMovementsByAccount = (account: number, typeOfSale: string, userBillingCenterOnly: boolean, flightCategory: number, page: number, queryString: string) =>
      this.getNonInvoicedFlightMovementsByAccount(account, typeOfSale, userBillingCenterOnly, flightCategory, page, queryString);

    $scope.previewAviationInvoiceJSON = (accountId: number, flightIdList: number[], flightMovementList: IFlightMovement[], flightCategory: number, invoiceCurrency: string) =>
      this.previewAviationInvoiceJSON(accountId, flightIdList, flightMovementList, flightCategory, invoiceCurrency);

    $scope.createFlightMovement = (account: IAccount) => this.createFlightMovement(account);
    $scope.editFlightMovement = (account: IAccount, flightMovement: IFlightMovement, index: number) => this.editFlightMovement(account, flightMovement, index);
    $scope.removeFlightMovement = (index: number) => $scope.tempFlightMovementList.splice(index, 1);
    $scope.removeError = () => $scope.error = null;
    $scope.flightToggleSelection = (id: number) => this.flightToggleSelection(id);
    $scope.generateSuccess = () => this.generateSuccess();
    $scope.generateError = () => this.generateError();
    $scope.generateAviationInvoicePaySuccess = () => this.generateAviationInvoicePaySuccess();
    $scope.setEmptyObjects = () => this.setEmptyObjects();
    $scope.selectedFlights = [];
    $scope.results = [];
    $scope.tempFlightMovementList = [];
    $scope.modal = $uibModal;
    $scope.payment = {};
    $scope.aviationInvoices = {};

    // getting USD currency
    this.currencyManagementService.getByCurrencyCode('USD').then((currency: ICurrency) => {
      $scope.usdCurrency = $scope.defaultTaspCurrency = currency;
    });

    // getting ANSP currency
    let anspCode = this.systemConfigurationService.getValueByName(<any>SysConfigConstants.ANSP_CURRENCY);
    if (anspCode) {
      this.currencyManagementService.getByCurrencyCode(anspCode).then((currency: ICurrency) => {
        $scope.anspCurrency = currency;
      });

    }

    // non-aviation related
    reportsService.getApplicablePosServiceCharges().then((response: ICatalogueServiceChargeType[]) => { this.catalogueServiceChargeTypes = response; this.$scope.serviceChargeList = response; this.getCategories(response); });
    aerodromesService.listAll().then((response: IAerodrome[]) => $scope.aerodromesList = response);
    $scope.chargeProps = { categories: [], types: [], descriptions: [] };
    $scope.getTypes = (category: string) => this.getTypes(category);
    $scope.getDescriptions = (type: string) => this.getDescriptions(type);
    $scope.addChargeItem = (description: string) => this.addChargeItem(description);
    $scope.lineItemError = [];
    $scope.lineItems = [];
    $scope.calculatedAmount = [];
    $scope.previewNonAviationInvoiceJSON = (accountId: number, lineItems: IInvoiceLineItem[], invoiceCurrency: string) =>
      this.previewNonAviationInvoiceJSON(accountId, lineItems, invoiceCurrency);

    $scope.nonAviationInvoices = {};
    $scope.removeChargeItem = (index: number) => this.$scope.lineItems.splice(index, 1);
    $scope.generateNonAviSuccess = () => this.generateNonAviSuccess();
    $scope.generateNonAviError = () => this.generateNonAviError();
    $scope.generateNonAviationInvoicePaySuccess = () => this.generateNonAviationInvoicePaySuccess();

    $scope.generateNonAviationInvoice = (accountId: number, preview: number, format: string, lineItems: IInvoiceLineItem[], invoiceCurrency: string) =>
      this.generateNonAviationInvoice(accountId, preview, format, lineItems, invoiceCurrency);

    $scope.generateAviationInvoice = (accountId: number, flightIdList: number[], format: string, preview: number,
      tempFlightMovementList: IFlightMovement[], flightCategory: number, invoiceCurrency: string) =>
      this.generateAviationInvoice(accountId, flightIdList, format, preview, tempFlightMovementList, flightCategory, invoiceCurrency);

    // reset charge props if line items is less then 0
    $scope.$watch('lineItems.length', (newLineItems: Array<IInvoiceLineItem>) => {
      $scope.nonAviationInvoices = {};
      if (this.catalogueServiceChargeTypes && (!newLineItems || newLineItems.length < 1)) {
        this.$scope.serviceChargeList = this.catalogueServiceChargeTypes;
        this.resetChargeProps(null);
      }
    });

    $scope.$on('lineItem amount changed', () => {
      $scope.nonAviationInvoices = {};
    });

    $scope.clearChargeItemForm = (chargeItemForm: IChargeItemForm) => this.clearChargeItemForm(chargeItemForm);

    $scope.isGenerateDisabled = (isPayment: boolean, isItemsValid: boolean, isPaymentValid?: boolean) => this.isGenerateDisabled(isPayment, isItemsValid, isPaymentValid);
    $scope.lineItemsValid = false;

    // adhoc fee related
    $scope.organizationName = systemConfigurationService.getValueByName(<any>SysConfigConstants.ORGANIZATION);
    $scope.approachLabel = systemConfigurationService.getValueByName(<any>SysConfigConstants.APPROACH_FEES_LABEL);
    pluginsService.getPluginEnabled('kcaa', 'aatis').then((isEnabled: boolean) => {
      if (isEnabled && systemConfigurationService.getBooleanFromValueByName (<any>SysConfigConstants.CALCULATE_TASP_CHARGES)) {
        $scope.isAATISEnabled = true;
      } else {
        $scope.isAATISEnabled = false;
      }
    });
    $scope.taspLabel = systemConfigurationService.getValueByName(<any>SysConfigConstants.TASP_FEES_LABEL);
    $scope.adhocFees = [];
    $scope.addAdhocFeeToList = (newAdhocFee: IAdhocFee) => this.addAdhocFeeToList(newAdhocFee);
    $scope.$watchGroup(['selectedFlights.length', 'adhocFees.length'], () => this.findFlightsMissingAdhocFee());
    $scope.$watch('tempFlightMovementList', () => this.findFlightsMissingAdhocFee(), true);

    // click generate and pay button from 'fake' button that includes popup functionality
    $scope.clickGenerateAndPay = () => setTimeout(() => document.getElementById('generate-pay-button').click());

    // click preview button from 'fake' button that includes popup functionality
    $scope.clickPreview = () => setTimeout(() => document.getElementById('preview-button').click());

    // used by the flight creation form
    $scope.showPassengerCounts = systemConfigurationService.getBooleanFromValueByName (<any>SysConfigConstants.INCLUDE_PASSENGER_CHARGES_ON_INVOICE);

    $scope.shouldShowCharge = (chargeType: string) => systemConfigurationService.shouldShowCharge(chargeType);
    $scope.nonAviationInvoicePayload = (parms: any): INonAviationInvoicePayload => reportsService.nonAviationInvoicePayload(parms.line_items, parms.payment);
  }

  private getAccounts(): void {
    if (this.$scope.accounts_type === 'cash') {
      this.$scope.accountsList = this.$scope.accountsListCash;
    } else if (this.$scope.accounts_type === 'credit') {
      this.$scope.accountsList = this.$scope.accountsListCredit;
    } else {
      this.$scope.accountsList = this.$scope.accountsListAll;
    }
  }

  // aviation Related methods
  private createFlightMovement(account: IAccount): void {
    this.openFlightPlanModal(account);
  }

  private editFlightMovement(account: IAccount, flightMovement: IFlightMovement, index: number): void {
    let disableTaspFees = false;
    if (this.$scope.selectedAdhocFees) {
      disableTaspFees = this.$scope.selectedAdhocFees.some((item: any) => item.flight_movement.id === flightMovement.id);
    }
    this.openFlightPlanModal(account, angular.copy(flightMovement), index, disableTaspFees);
  }

  private setEmptyObjects(): void {
    this.$scope.results = [];

    if (this.$scope.aviationInvoices) {
      this.$scope.aviationInvoices.generating = false;
      this.$scope.aviationInvoices.generatePay = false;
      this.$scope.aviationInvoices.preview = null;
    }
    this.$scope.tempFlightMovementList = [];
    this.$scope.invoiceCreatedAndPaid = false;
    this.$scope.lineItems = [];
    this.$scope.selectedFlights = [];
    this.$scope.invoiceCreated = null;
    this.$scope.currenciesList = [];
    this.$scope.invoiceCurrency = null;
  }

  // create/edit a flight plan
  private openFlightPlanModal(account: IAccount = null, flightMovement: IFlightMovement = null, index: number = null, disableTaspFees: boolean = false): void {

    const modal = this.$scope.modal.open({
      templateUrl: 'app/partials/invoice-generation/invoice-generation.flight-modal.html',
      scope: this.$scope,
      controller: ['$scope', '$timeout', 'reportsService', function (scope: angular.IScope, $timeout: angular.ITimeoutService, reportsService: ReportsService): void {

        scope.error = null;

        modal.rendered.then(function (): void { // needs to be called after rendering is done, so form controller can be setup
          $timeout(() => { // allows the directives inside to finish rendering

            if (flightMovement && !isNaN(index)) { // whether to edit a flight movement or not
              scope.editable = flightMovement;
              scope.index = index;
              scope.editable.disableTaspFees = disableTaspFees;
            } else {
              scope.editable = { source: 'manual' };
              scope.editable.associated_account_id = account.id; // pre-selects the account
              scope.editable.date_of_flight = moment.utc().startOf('day').toISOString(); // pre-selects the date of flight
              if (!scope.editable.tasp_charge_currency) {
                scope.editable.tasp_charge_currency = scope.defaultTaspCurrency;
              }
              scope.index = null;
            }

            function validateFlightMovement(flightMovement: IFlightMovement): ng.IPromise<void> { // returns whether a flight movement is valid or not
              scope.flightMovementButtonDisabled = true;
              return reportsService.validateFlight(account.id, flightMovement, scope.$parent.tempFlightMovementList);
            }

            scope.addFlightMovement = (flightMovement: IFlightMovement) => validateFlightMovement(flightMovement).then((validatedFlightMovement: any) => {
                // shows warning if FM has mismatching billing center
                scope.$parent.tempFlightMovementList.push(validatedFlightMovement);
                scope.flightMovementButtonDisabled = false;
                modal.close();
              },
              (error: IRestangularResponse) => {
                scope.error = { error };
                scope.flightMovementButtonDisabled = false;
            });

            scope.updateFlightMovement = (flightMovement: IFlightMovement, index: number) => validateFlightMovement(flightMovement).then((validatedFlightMovement: any) => {
              // shows warning if FM has mismatching billing center
              scope.$parent.tempFlightMovementList[index] = validatedFlightMovement;
              modal.close();
            },
              (error: IRestangularResponse) => {
                scope.error = { error };
                scope.flightMovementButtonDisabled = false;
            });

            scope.manual = {};
            scope.flightForm.$setPristine();
            scope.flightForm.$setUntouched();
          }, 0);
        });
      }],
      appendTo: angular.element(document.querySelector('#flight-modal-holder'))
    });
  }

  private reset(): void {
    // general
    this.$scope.account = null;
    this.$scope.accountCurrencyCode = null;
    this.$scope.typeOfSaleSelection = null;
    this.$scope.invoiceCurrency = null;

    this.$scope.paymentReset = new Date(); // resets payment

    // non-aviation
    this.$scope.nonAviationInvoices.preview = null;
    this.$scope.nonAviationInvoices.totalCurrency = null;
  }

  // generate a non-aviation invoice with the status "new"
  private generateNonAviationInvoice(accountId: number, preview: number, format: string, lineItems: IInvoiceLineItem[], invoiceCurrency: string): void {
    this.$scope.proforma = this.$scope.typeOfSaleSelection === 'general-proforma';
    this.reportsService.generateNonAviationInvoicePOS(accountId, preview, format, lineItems, invoiceCurrency, this.$scope.proforma).then(() => {
      this.generateNonAviSuccess();
    },
      (error: IRestangularResponse) => { this.$scope.error = { error }; this.generateNonAviError(); }
    );
  }

  // generate an aviation invoice with the status "new"
  private generateAviationInvoice(accountId: number, flightIdList: number[], format: string, preview: number,
    tempFlightMovementList: IFlightMovement[], flightCategory: number, invoiceCurrency: string): void {

    this.findSelectedAdhocFees();

    this.reportsService.generateAviationInvoice(accountId, flightIdList, this.$scope.selectedAdhocFees, format, preview,
      tempFlightMovementList, flightCategory, invoiceCurrency).then(() => {
      this.generateSuccess();
    },
      (error: IRestangularResponse) => { this.$scope.error = { error }; this.generateError(); }
    );

  }

  private getNonInvoicedFlightMovementsByAccount(account: number, typeOfSale: string, userBillingCenterOnly: boolean, flightCategory: number, page: number = 1, queryString: string): void {
    this.$scope.results = [];
    if (typeOfSale === 'aviation' && account) {
      this.reportsService.getNonInvoicedFlightMovementsByAccount(account, userBillingCenterOnly, page - 1, queryString, flightCategory).then((response: IFlightMovementSpring) => {
        response.number++;
        this.$scope.flightMovementList = response;
      },
        (error: IRestangularResponse) => this.$scope.error = { error }
      );
    }
  }

  private flightToggleSelection(id: number): void {
    const idx = this.$scope.selectedFlights.indexOf(id);

    if (idx > -1) {
      this.$scope.selectedFlights.splice(idx, 1);
    } else {
      this.$scope.selectedFlights.push(id);
    }

    this.$scope.aviationInvoices.preview = null;
    this.removePreview();

    this.findSelectedAdhocFees();
  }

  private removePreview(): void {
    this.$scope.$broadcast('hidePreview');
  }

  // returns JSON of Aviation Invoice
  private previewAviationInvoiceJSON(accountId: number, flightIdList: number[], flightMovementList: IFlightMovement[], flightCategory: number, invoiceCurrency: string): ng.IPromise<IAviationInvoice> {

    this.findSelectedAdhocFees();
    return this.reportsService
      .aviationInvoice(accountId, flightIdList, flightMovementList, this.$scope.selectedAdhocFees, flightCategory, 1, 'json', invoiceCurrency)
      .then((data: IAviationInvoice) => this.$scope.aviationInvoices.preview = data, (error: IRestangularResponse) => this.$scope.error = { error });
  }

  private enableButtons(): void {
    this.$scope.aviationInvoices.generating = false;
    this.$scope.aviationInvoices.generatePay = false;
    this.$scope.nonAviationInvoices.generating = false;
    this.$scope.nonAviationInvoices.generatePay = false;
  }

  private generateSuccess(): void { // callback for successful invoice generation
    this.$scope.invoiceCreated = true;
    this.reset();
    this.enableButtons();
  }

  private generateAviationInvoicePaySuccess(): void { // callback for successful invoice generation & pay
    this.generateSuccess();
    this.$scope.invoiceCreatedAndPaid = true;
  }

  private generateError(): void { // callback for unsuccessful generation or unsuccessful generation & pay
    this.enableButtons();
  }

  // returns JSON of Non-Aviation Invoice
  private previewNonAviationInvoiceJSON(accountId: number, lineItems: IInvoiceLineItem[], invoiceCurrency: string): ng.IPromise<any> {
    this.$scope.nonAviationInvoices.preview = null;
    this.$scope.nonAviationInvoices.totalCurrency = null;
    this.$scope.proforma = this.$scope.typeOfSaleSelection === 'general-proforma';
    return this.reportsService.nonAviationInvoice(accountId, lineItems, 1, 'json', invoiceCurrency, this.$scope.proforma).then((data: any) => {
      if (data.global.invoice_currency_code === data.global.invoice_currency_ansp_code) {
        this.$scope.nonAviationInvoices.preview = data.global.total_amount_ansp;
      } else {
        this.$scope.nonAviationInvoices.preview = data.global.total_amount;
      };

      this.$scope.nonAviationInvoices.totalCurrency = data.global.invoice_currency_code;
    }, (error: IRestangularResponse) => this.$scope.error = { error });
  }

  private generateNonAviSuccess(): void { // callback for successful non aviation invoice generation
    this.$scope.invoiceCreated = true;
    this.enableButtons();
    this.reset();
  }

  private generateNonAviationInvoicePaySuccess(): void { // callback for successful non aviation invoice generation & pay
    this.generateSuccess();
    this.$scope.invoiceCreatedAndPaid = true;
  }

  private generateNonAviError(): void { // callback for unsuccessful non avi generation or unsuccessful non avi generation & pay
    this.enableButtons();
  }

  /**
   * Creates an array of unique categories from the
   * service charge catalogue
   *
   * @param  {ICatalogueServiceChargeType[]} serviceChargeList
   * @returns void
   */
  private getCategories(serviceChargeList: ICatalogueServiceChargeType[]): void {
    const categories = serviceChargeList.map((item: ICatalogueServiceChargeType) => { return this.toTitleCase(item.category); });
    this.$scope.chargeProps.categories = Array.from(new Set(categories));
  }

  /**
   * Creates an array of unique types associated
   * with the selected category
   *
   * @param  {string} category
   * @returns void
   */
  private getTypes(category: string): void {
    const types = this.$scope.serviceChargeList
      .filter((item: ICatalogueServiceChargeType) => { return this.toTitleCase(item.category) === this.toTitleCase(category); })
      .map((item: ICatalogueServiceChargeType) => { return this.toTitleCase(item.type); });

    this.$scope.chargeProps.types = Array.from(new Set(types));
  }

  /**
   * Creates an array of unique descriptions associated
   * with the selected type
   *
   * @param  {string} type
   * @returns void
   */
  private getDescriptions(type: string): void {
    const descriptions = this.$scope.serviceChargeList
      .filter((item: ICatalogueServiceChargeType) => { return this.toTitleCase(item.type) === this.toTitleCase(type); })
      .map((item: ICatalogueServiceChargeType) => { return item.description; });

    this.$scope.chargeProps.descriptions = Array.from(new Set(descriptions));
  }

  /**
   * Finds the service charge item by the unique description
   * and adds it to the line items table
   *
   * @param  {string} description - Mandatory / Unique
   * @returns void
   */
  private addChargeItem(description: string): void {
    if (!description) { return; };

    const chargeItem = this.$scope.serviceChargeList.find((item: ICatalogueServiceChargeType) => item.description === description);

    this.$scope.lineItems.push(angular.copy(LineItem)); // push new line item
    this.$scope.lineItems[this.$scope.lineItems.length - 1].service_charge_catalogue = chargeItem; // update line item service charge

    // limit charge props to only of the same external charge category
    this.resetChargeProps(chargeItem.external_charge_category);
  }

  /**
   * This prevents duplicate categories/types created
   * by minor differences in capitalization or whitespace.
   *
   * eg:
   * "Trade Concessions" and "Trade concessions"
   * "Regulatory Charges" and "Regulatory  Charges"
   *
   * @param  {string} str
   * @returns string
   */
  private toTitleCase(str: string): string {
    if (!str) { return str; };
    const wordsToIgnore: any = ['and', 'a', 'or', 'for', 'in'];
    const wordsInString = str.split(' ').filter(String); // filters out empty "" strings
    const parsedWords = wordsInString.map((word: string) => {
      return wordsToIgnore.includes(word) ? word.trim() : word.replace(/\w\S*/g, (txt: string) => {
        return txt.charAt(0).toUpperCase() + txt.substr(1).toLowerCase();
      }).trim();
    });
    return parsedWords.join(' ');
  }

  /**
   * Limit selectable charge categories based on external charge category provided.
   * 
   * @param externalChargeCateogry category to limit list, null if no limitation
   */
  private resetChargeProps(externalChargeCateogry: IExternalChargeCategory): void {

    // set external charge category and limit service charge list
    if (externalChargeCateogry) {
      this.$scope.serviceChargeList = this.catalogueServiceChargeTypes.filter((item: ICatalogueServiceChargeType) =>
        item.external_charge_category !== null && item.external_charge_category.id === externalChargeCateogry.id);
    } else {
      this.$scope.serviceChargeList = this.catalogueServiceChargeTypes;
    }

    // update selectable dropdown list
    this.getCategories(this.$scope.serviceChargeList);
    this.getTypes(this.$scope.form.chargeItemForm.category);
    this.getDescriptions(this.$scope.form.chargeItemForm.type);
  }

  /**
   * Clear general charge item form selection.
   */
  private clearChargeItemForm(chargeItemForm: IChargeItemForm): void {
    chargeItemForm.category = '';
    chargeItemForm.type = '';
    chargeItemForm.description = '';
    chargeItemForm.$setPristine();
    chargeItemForm.$setUntouched();
    this.$scope.chargeProps.types = [];
    this.$scope.chargeProps.descriptions = [];
  }

  private isGenerateDisabled(isPayment: boolean, isItemsValid: boolean, isPaymentValid?: boolean): boolean {
    // return true if no account or sale selection
    if (!this.$scope.typeOfSaleSelection || !this.$scope.account) {
      return true;
    }

    // generic conditions
    let isDisabled: boolean = !isItemsValid;

    // sale type specific conditions
    if (this.$scope.typeOfSaleSelection === 'aviation') {
      isDisabled = isDisabled
        || this.$scope.aviationInvoices.generating
        || this.$scope.aviationInvoices.generatePay;
    } else if (this.$scope.typeOfSaleSelection === 'general') {
      isDisabled = isDisabled
        || this.$scope.nonAviationInvoices.generating
        || this.$scope.nonAviationInvoices.generatePay;
    }

    // payment specific conditions
    if (isPayment) {
      isDisabled = isDisabled
        || !isPaymentValid
        || this.$scope.workflow === 'credit';
    } else {
      isDisabled = isDisabled
        || this.$scope.workflow === 'cash'
        || (this.$scope.workflow === 'mixed' && this.$scope.account.cash_account);
    } 
    if(!isDisabled) {
     return  this. generateButtonIsDisabled();    
    }
    return isDisabled;
  }

  /*
   * Add adhoc fee returned from external database.
   * Adhoc fee is unique to flight movement and must have non-null charge amount
   */
  private addAdhocFeeToList(newAdhocFee: IAdhocFee): void {
    // remove adhoc fee if it already exists for flight
    this.$scope.adhocFees = this.$scope.adhocFees.filter((adhocFee: IAdhocFee) => adhocFee.flight_movement.id !== newAdhocFee.flight_movement.id);

    // add new adhoc fee to list if it is valid and has non-null charge amount
    if (newAdhocFee.adhoc_total_fee_payment_amount !== null) {
      this.$scope.adhocFees.push(newAdhocFee);
    }

    this.findSelectedAdhocFees();
  }

  // filter adhoc fees by those that belong to a selected flight
  private findSelectedAdhocFees(): void {
    this.$scope.selectedAdhocFees = this.$scope.adhocFees.filter((adhocFee: IAdhocFee) => (this.$scope.selectedFlights as any).includes(adhocFee.flight_movement.id)
      || (this.$scope.tempFlightMovementList as any).includes(adhocFee.flight_movement));
  }

  // used to check if any selected flights still require an adhoc fee
  private findFlightsMissingAdhocFee(): void {
    const flightMovements = this.$scope.flightMovementList ? this.$scope.flightMovementList.content : null;
    const selectedAdhocFeeFlightIds = this.$scope.adhocFees.map((adhocFee: IAdhocFee) => adhocFee.flight_movement.id);
    this.$scope.flightsRequiringAdhocFee = [];

    if (flightMovements && this.$scope.selectedFlights.length) {
      this.$scope.flightsRequiringAdhocFee = this.$scope.selectedFlights.reduce((acc: any, selectedFlightId: number) => {
        const flight = flightMovements.find((flight: IFlightMovement) => flight.id === selectedFlightId);

        if (flight && flight.adhoc_charge_required && !(selectedAdhocFeeFlightIds as any).includes(flight.id)) {
          acc.push(selectedFlightId);
        }

        return acc;
      }, []);
    }

    if (this.$scope.tempFlightMovementList.length) {
      const flights = this.$scope.tempFlightMovementList.filter((item: IFlightMovement) =>
      item.adhoc_charge_required && !(selectedAdhocFeeFlightIds as any).includes(item.id)).map((item: IFlightMovement) => item.id);

      this.$scope.flightsRequiringAdhocFee = this.$scope.flightsRequiringAdhocFee.concat(flights);

    }
  }

  private updateAccountCurrencyCode(account: IAccount): void {
    if (account && account.id) {
      this.accountsService.get(account.id)
        .then((account: IAccount) => {
          this.$scope.accountCurrencyCode = account.invoice_currency.currency_code;
          this.setDefaultCurrency(account.invoice_currency);
        });
    } else {
      this.$scope.accountCurrencyCode = null;
    }
  }

  private setDefaultCurrency(accountCurrency: ICurrency): void {
    this.$scope.currenciesList = [];
    this.$scope.currenciesList.push(accountCurrency);
    // default invoice currency is account currency
    this.$scope.invoiceCurrency = accountCurrency;
    if (accountCurrency.currency_code !== this.$scope.anspCurrency.currency_code) {
      this.$scope.currenciesList.push(this.$scope.anspCurrency);
    }

    if (accountCurrency.currency_code !== this.$scope.usdCurrency.currency_code) {
      this.$scope.currenciesList.push(this.$scope.usdCurrency);
    }
  }

  private recalculateInvoiceAmount(): void {
    this.$scope.accountCurrencyCode = this.$scope.invoiceCurrency.currency_code;
  }


  private generateButtonIsDisabled(): boolean {
    if (this.$scope.flightMovementList == null) return true;
    const flightMovementIncomplete = this.$scope.flightMovementList.content.filter((item: IFlightMovement) =>
    item.status == 'INCOMPLETE').filter((item: IFlightMovement) =>this.$scope.selectedFlights.indexOf(item.id)> -1)
 
   return flightMovementIncomplete.length != 0;
  } 

}
