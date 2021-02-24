// controllers
import { CRUDFormControllerUserService } from '../../angular-ids-project/src/helpers/controllers/crud-form-user-service/crud-form-user-service.controller';

// interfaces
import { IAccount, IAccountSpring, IAccountsScope, IAccountEventMap, INotificationList } from './accounts.interface';
import { IStaticType } from '../../angular-ids-project/src/helpers/interfaces/static-data-type.interface';
import { IType } from '../types/types.interface';

// services
import { AccountExternalChargeCategoryService } from '../account-external-charge-category/service/account-external-charge-category.service';
import { AccountsService } from './service/accounts.service';
import { SystemConfigurationService } from '../system-configuration/service/system-configuration.service';
import { UsersService } from '../users/service/users.service';
import { TypesService } from '../types/service/types.service';
import { CurrencyManagementService } from '../currency-management/service/currency-management.service';
import { LocalStorageService } from '../../angular-ids-project/src/components/services/localStorage/localStorage.service';

// constants
import { SysConfigConstants } from '../system-configuration/system-configuration.constants';
import { IUser, IUserSpring } from '../users/users.interface';
import { IAccountExternalChargeCategory } from '../account-external-charge-category/account-external-charge-category.interface';
import { IRestangularResponse } from '../../angular-ids-project/src/helpers/interfaces/restangularError.interface';
import { ICurrency } from '../currency-management/currency-management.interface';

export class AccountsController extends CRUDFormControllerUserService {

  private EMAIL: string = 'Email';
  private SMS: string = 'SMS text message';

  /* @ngInject */
  constructor(protected $scope: IAccountsScope, private $state: angular.ui.IStateService, private accountsService: AccountsService,
    private usersService: UsersService, private systemConfigurationService: SystemConfigurationService, $timeout: ng.ITimeoutService,
    private $translate: angular.translate.ITranslateService, private accountExternalChargeCategoryService: AccountExternalChargeCategoryService,
    private typesService: TypesService, private currencyManagementService: CurrencyManagementService, private $filter: ng.IFilterService) {

    super($scope, accountsService);
    super.setup({ refresh: false });
    super.list({}, `filter=true&cash=false`);

    $scope.refreshOverride = () => this.refreshOverride();
    $scope.setAccountType = () => this.setAccountType();

    $scope.$watch('filter', () => this.getFilterParameters());

    $scope.whitelistingEnabled = LocalStorageService.getBooleanFromValueByName(`SystemConfiguration:${SysConfigConstants.WL_ENABLED}`);

    this.currencyManagementService.getByCurrencyCode('USD').then((currency: ICurrency) => {
      $scope.usdCurrency = this.$scope.editable.invoice_currency = currency;
    });

    this.typesService.listAll().then((types: Array<IType>) => {
      this.$scope.generalAviation = types.find((element: IType) => element.name === 'GeneralAviation');
      this.$scope.airline = types.find((element: IType) => element.name === 'Airline');
      this.setDefaultFields();
    });

    // if account was called from Invoices
    if ($state.params.accountId) {
      $timeout(() => { this.$scope.toggle = true; });
      accountsService.listAllWithCash().then((data: IAccountSpring) => this.findAccount(data.content, $state.params.accountId));
    }

    // if account is created via accounts interface, cash_account is set to false,
    // if account is created via point-of-sale interface, cash_account is set to true
    $scope.$on('$locationChangeStart', function (event: Object, current: string, previous: string): void {
      if (previous.includes('/invoice-generation/')) {
        $scope.editable.cash_account = true;
      }
    });

    $scope.notificationsTypeList = [{ id: 1, name: $translate.instant(this.EMAIL), value: this.EMAIL }, { id: 2, name: $translate.instant(this.SMS), value: this.SMS }];

    $scope.notificationsList = [];
    $scope.editable.list_of_events_account_notified = [];

    accountsService.getCustomerNotifications().then((data: Array<any>) => {
      data.forEach((element: any) => {
        $scope.notificationsList.push({ id: element[0], name: element[1], model: [] });
        this.setListOfEventsAccountNotified(element[0], false, false);
        $scope.emptyAccountEventList = $scope.editable.list_of_events_account_notified;
      });
    });

    $scope.selfCareUserList = [];

    usersService.listAll().then((users: IUserSpring) => {
      users.content.forEach((user: IUser) => {
        if (user.is_selfcare_user && user.registration_status) {
          $scope.selfCareUserList.push(user);
        }
      });
    });

    $scope.addNotificationsToList = () => this.addNotificationsToList();
    $scope.getAviationInfo = () => this.getAviationInfo();
    $scope.getAllNotifications = () => this.getAllNotifications();
    $scope.setEmpty = () => this.setEmpty();

    $scope.checkCreditLimit = () => this.checkCreditLimit();

    // set scope value for require external system identifier
    $scope.requireExternalSystemId = this.requireExternalSystemId();

    $scope.shouldShowCharge = (chargeType: string) => systemConfigurationService.shouldShowCharge(chargeType);

    $scope.editOverride = (item: IAccount) => this.editOverride(item);
    $scope.editExternal = (item: IAccountExternalChargeCategory) => this.editExternal(item);
    $scope.createExternal = (item: IAccountExternalChargeCategory) => this.createExternal(item);
    $scope.updateExternal = (item: IAccountExternalChargeCategory, id: number) => this.updateExternal(item, id);
    $scope.deleteExternal = (id: number) => this.deleteExternal(id);
    $scope.resetExternal = () => this.resetExternal();
  }

  protected create(account: IAccount): ng.IPromise<void> {
    return super.create(account).then((createdAccount: IAccount) => {
      if (this.$state && this.$state.params.after) { // go back to previous page (if state params exist)
        this.$state.go(this.$state.params.after.create.goTo.state, {
          accountId: createdAccount.id
        });
      }
    });
  }

  protected reset(): void {
    this.$scope.resetPass = {}; // to reset pass2
    super.reset();
    this.resetExternal();
    this.setDefaultFields();
  }

  private setDefaultFields(): void {
    this.checkCreditLimit();
    this.setAccountType();
    this.$scope.editable.payment_terms = <number>this.systemConfigurationService.getValueByName(<any>SysConfigConstants.DEFAULT_ACCOUNT_PAYMENT_TERMS);
    this.$scope.editable.monthly_overdue_penalty_rate = <number>this.systemConfigurationService.getValueByName(<any>SysConfigConstants.DEFAULT_ACCOUNT_MONTHLY_PENALTY);
    this.$scope.editable.aircraft_parking_exemption = <number>this.systemConfigurationService.getValueByName(<any>SysConfigConstants.DEFAULT_ACCOUNT_PARKING_EXEMPTION);
    this.$scope.anspCurrency = <string>this.systemConfigurationService.getValueByName(<any>SysConfigConstants.ANSP_CURRENCY);
    this.$scope.editable.invoice_delivery_format = 'pdf';
    this.$scope.editable.invoice_delivery_method = 'email';
    if (this.$scope.usdCurrency) {
      this.$scope.editable.invoice_currency = this.$scope.usdCurrency;
    }
  }

  private checkCreditLimit(): void {
    if (this.$scope.editable.cash_account === true) {
      this.$scope.editable.credit_limit = 0;
      this.$scope.minCreditLimit = 0;
      this.$scope.maxCreditLimit = 0;
    } else {
      this.$scope.editable.credit_limit = <number>this.systemConfigurationService.getValueByName(<any>SysConfigConstants.DEFAULT_ACCOUNT_CREDIT_LIMIT);
      this.$scope.minCreditLimit = <number>this.systemConfigurationService.getValueByName(<any>SysConfigConstants.DEFAULT_ACCOUNT_MIN_CREDIT_NOTE);
      this.$scope.maxCreditLimit = <number>this.systemConfigurationService.getValueByName(<any>SysConfigConstants.DEFAULT_ACCOUNT_MAX_CREDIT_NOTE);
    }
  }

  private getFilterParameters(): void {
    this.$scope.filterParameters = {
      filter: this.$scope.filter,
      cash: this.$scope.accountTypeFilter,
      search: this.$scope.textFilter,
      page: this.$scope.pagination ? this.$scope.pagination.number : 0,
      selfCareAccounts: this.$scope.selfCareAccounts,
      nationality: this.$scope.nationality
    };
  }

  private refreshOverride(): ng.IPromise<IAccountSpring> {
    let request: ng.IPromise<IAccountSpring>;
    this.getFilterParameters();

    switch (this.$scope.accountFilter) {
      case 'outstanding':
      case 'overdue':
        this.$scope.filterParameters.invoices = this.$scope.accountFilter;
        this.$scope.accFilter = angular.copy(this.$scope.filterParameters); // used as a param for the export directive
        break;
      case 'credit-limit':
        this.$scope.filterParameters.credit = true;
        this.$scope.accFilter = angular.copy(this.$scope.filterParameters);
        break;
      case null || undefined || '':
        this.$scope.accFilter = angular.copy(this.$scope.filterParameters);
        this.$scope.accFilter.credit = '';
        this.$scope.accFilter.invoices = '';
    }

    request = super.list(this.$scope.filterParameters, this.$scope.getSortQueryString());

    return request;
  }

  // returns the selected account from Invoices
  private findAccount(accountList: Array<IAccount>, params: string): void {
    for (let i = 0; i < accountList.length; i++) {
      if (accountList[i].id === parseInt(params, 10)) {
        this.$scope.editable = accountList[i];
        break;
      }
    }
  }

  private getAviationInfo(): void {
    this.$scope.editable.non_aviation_billing_contact_person_name = this.$scope.editable.aviation_billing_contact_person_name;
    this.$scope.editable.non_aviation_billing_phone_number = this.$scope.editable.aviation_billing_phone_number;
    this.$scope.editable.non_aviation_billing_mailing_address = this.$scope.editable.aviation_billing_mailing_address;
    this.$scope.editable.non_aviation_billing_email_address = this.$scope.editable.aviation_billing_email_address;
    this.$scope.editable.non_aviation_billing_sms_number = this.$scope.editable.aviation_billing_sms_number;
  }

  private getAllNotifications(): void {
    this.setEmpty();
    if (this.$scope.editable.list_of_events_account_notified !== null && this.$scope.editable.list_of_events_account_notified.length > 0) {
      this.$scope.notificationsList.forEach((element: INotificationList) => {
        element.model = [];
        let event: Array<IAccountEventMap> = this.$scope.editable.list_of_events_account_notified.filter((item: IAccountEventMap) => (item.notification_event_type === element.id));
        if (event[0].notification_email) {
          element.model.push({ id: 1, name: this.EMAIL, value: this.EMAIL });
        }
        if (event[0].notification_sms) {
          element.model.push({ id: 2, name: this.SMS, value: this.SMS });
        }
      });
    } else {
      this.$scope.editable.list_of_events_account_notified = this.$scope.emptyAccountEventList;
    }
  }

  private addNotificationsToList(): void {
    this.$scope.editable.list_of_events_account_notified = [];
    this.$scope.notificationsList.forEach((element: INotificationList) => {
      let email = false;
      let sms = false;
      if (element.model.length > 0) {
        element.model.forEach((el: IStaticType) => {
          if (el.name.includes(this.SMS)) {
            sms = true;
          }
          if (el.name.includes(this.EMAIL)) {
            email = true;
          }
        });
      }
      this.setListOfEventsAccountNotified(element.id, email, sms);
    });
  }

  private setListOfEventsAccountNotified(notificationId: number, notificationEmail: boolean, notificationSms: boolean): void {
    this.$scope.editable.list_of_events_account_notified.push({
      notification_event_type: notificationId,
      notification_email: notificationEmail,
      notification_sms: notificationSms
    });
  }

  private setEmpty(): void {
    this.$scope.notificationsList.forEach((element: INotificationList) => {
      element.model = [];
    });
  }

  /**
   * Get required external system id flag from system configuration.
   *
   * @returns boolean true if required
   */
  private requireExternalSystemId(): boolean {
    return this.systemConfigurationService
      .getBooleanFromValueByName(<any>SysConfigConstants.REQUIRE_ACCOUNT_EXTERNAL_SYSTEM_ID);
  }

  private editOverride(item: IAccount): void {
    super.edit(item);
    this.resetExternal();
    this.$scope.whitelistLastActivityDateTime = this.$filter('dateConverter')(item.whitelist_last_activity_date_time, 'HH:mm');
    this.$scope.whitelistState = this.$translate.instant(item.whitelist_state.toLowerCase());
    this.$scope.editableExternal.account = item;

    this.accountExternalChargeCategoryService.getByAccountId(item.id)
      .then((response: Array<IAccountExternalChargeCategory>) => this.$scope.listExternal = response);
  }

  private editExternal(item: IAccountExternalChargeCategory): void {
    this.$scope.error = null;
    this.$scope.editableExternal = angular.copy(item);
  }

  private createExternal(item: IAccountExternalChargeCategory): ng.IPromise<any> {
    return this.accountExternalChargeCategoryService.create(item)
      .then((newObject: Object) => this.resetExternal(), (error: IRestangularResponse) => this.setErrorResponse(error));
  }

  private updateExternal(item: IAccountExternalChargeCategory, id: number): ng.IPromise<any> {
    return this.accountExternalChargeCategoryService.update(item, id)
      .then(() => this.resetExternal(), (error: IRestangularResponse) => this.setErrorResponse(error));
  }

  private deleteExternal(id: number): ng.IPromise<any> {
    return this.accountExternalChargeCategoryService.delete(id)
      .then(() => this.resetExternal(), (error: IRestangularResponse) => this.setErrorResponse(error));
  }

  private refreshExternal(): void {
    if (this.$scope.editable !== null && this.$scope.editable.id !== null) {
      this.accountExternalChargeCategoryService.getByAccountId(this.$scope.editable.id)
        .then((response: Array<IAccountExternalChargeCategory>) => this.$scope.listExternal = response);
    } else {
      this.$scope.listExternal = null;
    }
  }

  private resetExternal(): void {
    this.$scope.error = null;
    this.$scope.editableExternal = angular.copy(this.accountExternalChargeCategoryService.modelOverride);
    this.$scope.editableExternal.account = this.$scope.editable !== null && this.$scope.editable.id !== null ? this.$scope.editable : null;
    this.$scope.whitelistLastActivityDateTime = null;
    this.$scope.whitelistState = null;
    this.$scope.whitelistInactivityNoticeSent = null;
    this.$scope.whitelistExpiryNoticeSent = null;
    this.refreshExternal();
    if (this.$scope.formExternal) {
      this.$scope.formExternal.$setUntouched();
    }
  }

  private setAccountType(): void {
    if (this.$scope.editable.icao_code && this.$scope.airline) {
      this.$scope.editable.account_type = this.$scope.airline;
    } else if (!this.$scope.editable.icao_code && this.$scope.generalAviation) {
      this.$scope.editable.account_type = this.$scope.generalAviation;
    }
  }

}
