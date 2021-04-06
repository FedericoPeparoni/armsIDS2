// controllers
import { CRUDFormControllerUserService } from '../../angular-ids-project/src/helpers/controllers/crud-form-user-service/crud-form-user-service.controller';

// interfaces
import { IAviationBillingEngineScope, IIncompleteFlights } from './aviation-billing-engine.interface';
import { IAccountMinimal } from '../accounts/accounts.interface';
import { ISystemConfigurationSpring } from '../system-configuration/system-configuration.interface';
import { IType } from '../types/types.interface';

// services
import { AviationBillingEngineService } from './service/aviation-billing-engine.service';
import { AccountsService } from '../accounts/service/accounts.service';
import { SystemConfigurationService } from '../system-configuration/service/system-configuration.service';
import { ReportsService } from '../reports/service/reports.service';
import { SysConfigBoolean } from '../../angular-ids-project/src/components/services/sysConfigBoolean/sysConfigBoolean.service';
import { ServerDatetimeService } from '../../angular-ids-project/src/components/server-datetime/server-datetime.service';
import { TypesService } from '../types/service/types.service';
import { CustomDate } from '../../angular-ids-project/src/components/services/customDate/customDate.service';

// constants
import { SysConfigConstants } from '../system-configuration/system-configuration.constants';

// classes
import { Calculation } from '../recalculate-reconcile/calculation';
import { IRestangularResponse } from '../../angular-ids-project/src/helpers/interfaces/restangularError.interface';


export class AviationBillingEngineController extends CRUDFormControllerUserService {



  /* @ngInject */
  constructor(public $scope: IAviationBillingEngineScope, private aviationBillingEngineService: AviationBillingEngineService,
    private customDate: CustomDate,
    private accountsService: AccountsService, private systemConfigurationService: SystemConfigurationService, private $interval: angular.IIntervalService,
    private reportsService: ReportsService, $translate: angular.translate.ITranslateService, private sysConfigBoolean: SysConfigBoolean,
    private serverDatetimeService: ServerDatetimeService, private typesService: TypesService) {

    // setup service and generate scope
    super($scope, aviationBillingEngineService);
    super.setup({ refresh: false });

    // default settings
    $scope.processing = false;
    $scope.selectedAccounts = [];
    $scope.today = () => moment();
    $scope.startOfCurrentMonth = $scope.today().startOf('month');
    $scope.customDate = this.customDate.returnDateFormatStr(false);
    this.$scope.progressBarValue = 0;

    this.setDefaultDate();
    this.setBillingInterval();


    // get required system configurations
    $scope.invoiceByFmCategory = sysConfigBoolean.parse(this.systemConfigurationService.getValueByName(<any>SysConfigConstants.INVOICE_FM_CATEGORY));
    $scope.iataSupported = sysConfigBoolean.parse(systemConfigurationService.getValueByName(<any>SysConfigConstants.IATA_INVOICE_SUPPORT));

    this.typesService.listAll().then((types: Array<IType>) => {
      const allAccounts: any = { id: 'all', name: 'All Accounts' };
      $scope.accountTypes = types.filter((type: IType) =>
        type.name === 'Airline' || type.name === 'GeneralAviation' || type.name === 'Charter' || type.name === 'Unified Tax');
      $scope.accountTypes.unshift(allAccounts);
      this.updateAccounts($scope.editable.account_type);
    });

    if ($scope.iataSupported) {
      $scope.editable.iata_status = 'iata';
    } else {
      $scope.editable.iata_status = 'non-iata';
    }

    // functions accessible by the template
    $scope.clear = () => this.clear();
    $scope.validate = () => this.validate();
    $scope.addIdToList = () => this.addIdToList();
    $scope.closeHeader = () => this.closeHeader();
    $scope.setMonthlyDates = () => this.setMonthlyDates();
    $scope.setAnnuallyDates = () => this.setAnnuallyDates();
    $scope.setPartiallyDates = () => this.setPartiallyDates();
    $scope.cancelGeneration = () => this.cancelGeneration();
    $scope.setWeekStartDate = () => this.setWeekStartDate();
    $scope.setBillingInterval = () => this.setBillingInterval();
    $scope.cancelRecalculation = () => this.cancelRecalculation();
    $scope.executeRecalculation = () => this.executeRecalculation();
    $scope.executeGeneration = (preview: number) => this.executeGeneration(preview);
    $scope.setDefaultFlights = (iataStatus: string) => this.setDefaultFlights(iataStatus);
    $scope.incompleteFlightSort = (orderBy: string) => this.incompleteFlightSort(orderBy);
    $scope.updateAccounts = (accountType: number) => this.updateAccounts(accountType);
    $scope.setIntervalPeriodForUnifiedTax = (accountType: number) => this.setIntervalPeriodForUnifiedTax(accountType);
    $scope.setFlightMovementCategory = (accountType: number) => this.setFlightMovementCategory(accountType);

    $scope.getProgressBarValue = (accountsTotal: number, accountNumber: number, flightsTotal: number, flightNumber: number) =>
      this.getProgressBarValue(accountsTotal, accountNumber, flightsTotal, flightNumber);

    // calculation class initialization
    $scope.recalculate = new Calculation('recalculateJob', 'executeRecalulation', 'cancelRecalculation', 'getStatusForRecalulations',
      null, $scope, aviationBillingEngineService, $interval, serverDatetimeService);
    $scope.generate = new Calculation('generateJob', 'executeGeneration', 'cancelGeneration', 'getStatusForGeneration', 'downloadInvoice',
      $scope, aviationBillingEngineService, $interval, serverDatetimeService);

    // check to see if any async processes already running
    this.checkRecalculationStatusOnPageLoad();
    this.checkGenerationStatusOnPageLoad();

    // listeners
    $scope.$watchGroup(['dateObject', 'editable.end_date', 'editable.billing_interval', 'editable.iata_status'], () =>
      $scope.isBillingPeriodValid = this.validateBillingPeriod());
  }

  private
  updateAccounts(accountType: number): void {
    if (typeof accountType === 'string') {
      accountType = null;
    }
    // get required external data from the backend
    this.accountsService.findAllActiveCreditMinimalAviationReturn(accountType).then((accounts: Array<IAccountMinimal>) => this.$scope.listOfAccounts = accounts);
  }

  // close the incomplete flights content box
  private closeHeader(): void {
    this.$scope.editable.incompleteFlights = null;
    this.$scope.recalculateJob = null;
    this.$scope.generateJob = null;
    this.$scope.preview = null;
  }

  // check to see if calculation async already in progress
  private checkRecalculationStatusOnPageLoad(): void {
    this.$scope.recalculate.checkStatusOnPageLoad().then((data: boolean) => {
      if (!this.$scope.processing) {
        this.$scope.processing = data;
      }
    });
  }

  // begin a recalculation async task
  private executeRecalculation(): void {
    this.$scope.processing = true;

    this.setProcessDates();

    // if accounts are selected, recalculate selected only
    this.$scope.editable.account_id_list = this.$scope.selectedAccounts && this.$scope.selectedAccounts.length > 0
      ? this.$scope.selectedAccounts.map((account: IAccountMinimal) => account.id)
      : null;

    this.$scope.recalculate.executeCalculation(this.$scope.editable);
  }

  // cancel a recalculation async task
  private cancelRecalculation(): void {
    this.$scope.recalculate.cancelCalculation();
  }

  // check to see if generation async already in progress
  private checkGenerationStatusOnPageLoad(): void {
    this.$scope.generate.checkStatusOnPageLoad().then((data: boolean) => {
      if (!this.$scope.processing) {
        this.$scope.processing = data;
      };
    });
  }

  // start a generate or preview invoice async task
  private executeGeneration(preview: number): void {
    this.$scope.processing = true;
    this.$scope.editable.preview = preview;

    this.setProcessDates();

    this.$scope.editable.iataInvoice = this.$scope.editable.iata_status === 'iata';

    // apply all account ids if non selected (only applies to IATA as buttons disabled for non-iata)
    // otherwise apply only selected account ids
    if (!this.$scope.selectedAccounts || this.$scope.selectedAccounts.length === 0) {
      this.$scope.editable.account_id_list = this.$scope.listOfAccounts.map((account: IAccountMinimal) => account.id);
    } else {
      this.$scope.editable.account_id_list = this.$scope.selectedAccounts.map((account: IAccountMinimal) => account.id);
    }

    this.$scope.generate.executeCalculation(this.$scope.editable);
  }

  // cancel a generate or preview invoice async task
  private cancelGeneration(): void {
    this.$scope.generateJob.job_execution_status = 'CANCELED';

    this.$scope.generate.cancelCalculation();
  };

  /**
   * Set default flight flag based on iata_status
   */
  private setDefaultFlights(iataStatus: string): void {
    this.$scope.editable.userBillingCenterOnly = iataStatus === 'iata' ? 'false' : 'true';
  }


  /**
   * Create list of selected accounts ids
   */
  private addIdToList(): number[] {
    const idList = this.$scope.selectedAccounts.map((account: IAccountMinimal) => {
      return account.id;
    });

    return this.$scope.editable.account_id_list = idList;
  }

  /**
   * Clear the preview from the screen
   */
  private clear(): boolean {
    angular.element(document.querySelector('#preview')).html('');

    return true;
  }

  /**
   * Validate billing period
   */
  private validateBillingPeriod(): boolean {
    if (this.$scope.editable.billing_interval === 'WEEKLY') {

      let end = moment(this.$scope.editable.end_date);
      let yyyy = end.year();
      let mm = this.str_pad(end.month() + 1);
      let dd;
      if (end.utcOffset() < 0) {
        dd = this.str_pad(end.date() + 1);
      } else {
        dd = this.str_pad(end.date());
      }
      let end_date = moment().format(`${yyyy}-${mm}-${dd}[T]00:00:00.000[Z]`);

      let today = moment();
      yyyy = today.year();
      mm = this.str_pad(today.month() + 1);
      dd = this.str_pad(today.date());
      let today_date = moment().format(`${yyyy}-${mm}-${dd}[T]00:00:00.000[Z]`);

      return moment(today_date).isAfter(moment(end_date));
    } else {
      return this.$scope.startOfCurrentMonth.isAfter(this.$scope.dateObject);
    }
  }

  /**
   * Set start date for weekly invoice
   */
  private setWeekStartDate(): void {
    this.$scope.editable.start_date = moment(this.$scope.editable.end_date).subtract(6, 'days').toISOString();

  }

  /**
   * Show flights for the selected perion where flight status is PENDING, INCOMPLETE or CANCELED
   */
  private validate(): void {
    this.$scope.processing = true;

    this.setProcessDates();

    // if accounts are selected, validate selected only
    this.$scope.editable.account_id_list = this.$scope.selectedAccounts && this.$scope.selectedAccounts.length > 0
      ? this.$scope.selectedAccounts.map((account: IAccountMinimal) => account.id)
      : null;

    const editable = this.$scope.editable;
    const processStartDate = this.$scope.editable.processStartDate;
    const processEndDate = this.$scope.editable.processEndDate;

    this.aviationBillingEngineService.validate(editable, processStartDate, processEndDate).then((data: Array<IIncompleteFlights>) => {
      this.$scope.processing = false;
      this.$scope.editable.incompleteFlights = data;
      this.$scope.editable.flights = `${data.length}`;
    }).catch((error: IRestangularResponse) => {
      this.$scope.processing = false;

      this.setErrorResponse(error);
    });
  }

  /**
   * Incomplete Flight sort order by editable sort field value.
   *
   * @param orderBy editable.sort value
   */
  private incompleteFlightSort(orderBy: string): Array<string> {
    let sort: Array<string>;
    switch (orderBy) {
      case 'dateTime,account':
        sort = ['-day_of_flight', '-departure_time', '+account_name', '+flight_id'];
        break;
      case 'account,dateTime':
        sort = ['+account_name', '-day_of_flight', '-departure_time', '+flight_id'];
        break;
      default:
        sort = ['-day_of_flight', '-departure_time', '+flight_id'];
    }
    return sort;
  }

  /**
   * Set dates for validate/recalculate/recalculate methods
   */
  private setProcessDates(): void {
    if (this.$scope.editable.billing_interval === 'MONTHLY') {
      this.$scope.editable.processStartDate = this.firstDateOfMonth();
      this.$scope.editable.processEndDate = this.lastDateOfMonth();
      this.$scope.editable.endDateInclusive = this.lastDateOfMonth();
    }
    else if (this.$scope.editable.billing_interval === 'ANNUALLY') {
      this.$scope.editable.processStartDate = this.firstDateOfYear();
      this.$scope.editable.processEndDate = this.lastDateOfYear();
      this.$scope.editable.endDateInclusive = this.lastDateOfYear();
    }
    else if (this.$scope.editable.billing_interval === 'PARTIALLY') {
      this.$scope.editable.processStartDate = this.firstDateOfMonth();
      this.$scope.editable.processEndDate = this.lastDateOfYear();
      this.$scope.editable.endDateInclusive = this.lastDateOfYear();
    }

    else {
      this.$scope.editable.processStartDate = new Date(this.$scope.editable.start_date).toISOString();
      this.$scope.editable.processEndDate = new Date(this.$scope.editable.end_date).toISOString();
      this.$scope.editable.endDateInclusive = new Date(this.$scope.editable.end_date).toISOString();
    }
  }

  /**
   * Set start/end dates for monthly,annually,partially interval for previewing and generating
   */
  private setMonthlyDates(): void {
    if (this.$scope.editable.billing_interval === 'MONTHLY') {
      this.$scope.editable.start_date = this.firstDateOfMonth();
      this.$scope.editable.end_date = this.lastDateOfMonth();

    }
    else if (this.$scope.editable.billing_interval === 'ANNUALLY') {
      this.setAnnuallyDates();
    }
    else if (this.$scope.editable.billing_interval === 'PARTIALLY') {
      this.setPartiallyDates();
      this.setDefaultPartiallyDate();
    }
    else {
      this.setWeeklyDefaultDate();
    }
  }
  /**
   * Set start/end dates for annually interval for previewing and generating
   */
  private setAnnuallyDates(): void {
    this.$scope.editable.start_date = this.firstDateOfYear();
    this.$scope.editable.end_date = this.lastDateOfYear();
  }
  /**
   * Set start/end dates for partially interval for previewing and generating
   */
  private setPartiallyDates(): void {
    this.$scope.editable.start_date = this.firstDateOfMonth();
    this.$scope.editable.end_date = this.lastDateOfYear();
  }



  /**
   * Return the first date of the dateObject (selected month)
   */
  private firstDateOfMonth(): string {
    let d = angular.copy(this.$scope.dateObject);
    let yyyy = d.getFullYear();
    let mm = this.str_pad(d.getMonth() + 1);

    return moment().format(`${yyyy}-${mm}-01[T]00:00:00.000[Z]`);
  }

  /**
   * Return the last date of the dateObject (selected month)
   */
  private lastDateOfMonth(): string {
    let d = new Date(this.$scope.dateObject.getFullYear(), this.$scope.dateObject.getMonth() + 1, 0);
    let yyyy = d.getFullYear();
    let mm = this.str_pad(d.getMonth() + 1);
    let dd = this.str_pad(d.getDate());

    return moment().format(`${yyyy}-${mm}-${dd}[T]00:00:00.000[Z]`);
  }

  /**
    * Return the first date of the dateObject (selected annually)
    */

  private firstDateOfYear(): string {
    let d = angular.copy(this.$scope.dateObject);
    let yyyy = d.getFullYear();

    return moment().format(`${yyyy}-01-01[T]00:00:00.000[Z]`);

  }

  /**
   * Return the last date of the dateObject (selected annually)
   */
  private lastDateOfYear(): string {
    let d = angular.copy(this.$scope.dateObject);
    let yyyy = d.getFullYear();
    return moment().format(`${yyyy}-12-31[T]23:59:59.999[Z]`);
  }



  /**
   * Set default date for monthly billing interval
   */
  private setDefaultDate(): void {
    let today = new Date();
    let month = today.getMonth();
    this.$scope.dateObject = new Date(today.getFullYear(), month - 1);

    console.log(this.$scope.dateObject);

  }


  /**
    * Set default date for partially billing interval
    */

  private setDefaultPartiallyDate(): void {
    let today = new Date();
    let month = today.getMonth();
    this.$scope.dateObject = new Date(today.getFullYear(), month);

    console.log(this.$scope.dateObject);

  }


  /**
   * Set default date for weekly billing interval
   */
  private setWeeklyDefaultDate(): void {
    if (this.$scope.startingDay !== undefined) {
      let diff = moment().day() - this.$scope.startingDay;
      let end = moment().subtract(diff + 1 + (diff < 0 ? 7 : 0), 'days');
      let yyyy = end.year();
      let mm = this.str_pad(end.month() + 1);
      let dd = this.str_pad(end.date());
      this.$scope.editable.end_date = moment().format(`${yyyy}-${mm}-${dd}[T]00:00:00.000[Z]`);

      this.setWeekStartDate();

      this.$scope.maxDate = moment().add(6, 'days').toISOString();
      this.dateOptions();
    }
  }

  /**
   * Set default billing interval from SystemConfiguration 'Default billing period...' and 'First day of week...'
   */
  private setBillingInterval(): void {
    let firstDay;
    this.systemConfigurationService.listAll().then((data: ISystemConfigurationSpring) => {
      if (this.$scope.editable.iata_status === 'iata') {
        this.$scope.editable.billing_interval = this.getSystemConfigurationItem(data, <any>SysConfigConstants.DEFAULT_BILLING_PERIOD_IATA).toUpperCase();
        firstDay = this.getSystemConfigurationItem(data, <any>SysConfigConstants.FIRST_DAY_OF_WEEK_IATA);
      } else {
        this.$scope.editable.billing_interval = this.getSystemConfigurationItem(data, <any>SysConfigConstants.DEFAULT_BILLING_PERIOD_NON_IATA).toUpperCase();
        firstDay = this.getSystemConfigurationItem(data, <any>SysConfigConstants.FIRST_DAY_OF_WEEK_NON_IATA);
      }

      const weekDays = ['Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday'];
      for (let day of weekDays) {
        if (firstDay === day) {
          this.$scope.startingDay = weekDays.indexOf(day);
        }
      }
      if (this.$scope.editable.billing_interval === 'MONTHLY') {
        this.setMonthlyDates();
      }
      else if (this.$scope.editable.billing_interval === 'ANNUALLY') {
        this.setAnnuallyDates();
      }
      else if (this.$scope.editable.billing_interval === 'PARTIALLY') {
        this.setPartiallyDates();
      }
      else {
        this.setWeeklyDefaultDate();
      }
      this.dateOptions();
      this.dateOptionsAnnually();
      this.dateOptionsPartially();
    });
  }

  /**
   * Set billing interval default in accountType UnifiedTax
   */
  private setIntervalPeriodForUnifiedTax(accountType: number): void  {
    if (accountType === 8){
      this.$scope.editable.billing_interval = 'ANNUALLY';
    }
  }


/**
   * Set Flight Movement Category default in accountType UnifiedTax
   */
  private setFlightMovementCategory(accountType: number): void  {
    if (accountType === 8){
      this.$scope.editable.flightCategory = null ;
    }
  }




  /**
   * Set date options
   */
  private dateOptions(): void {
  this.$scope.dateOptions = {
    startingDay: 0,
    showWeeks: false,
    maxDate: new Date(this.$scope.maxDate),
    dateDisabled: (data: any): boolean => {
      let date = data.date,
        mode = data.mode;
      let startingDay = this.$scope.startingDay === 0 ? 6 : this.$scope.startingDay - 1;
      return mode === 'day' && (date.getDay() !== startingDay);
    }
  };
}
  /**
    * Set date options Annually
    */

  private dateOptionsAnnually(): void {
  this.$scope.dateOptionsAnnually = {
    maxDate: new Date(this.$scope.maxDate),
    minMode: 'year',
    dateDisabled: (data: any): boolean => {
      let date = data.date,
        mode = data.mode;
      let nextYear = (moment().year() + 1);
      return mode === 'year' && (date.getFullYear() > nextYear );
    }

  };
}

  /**
     * Set date options Partially
     */

  private dateOptionsPartially(): void {
  this.$scope.dateOptionsPartially = {
    minMode: 'month',
    dateDisabled: (data: any): boolean => {
      let date = data.date,
        mode = data.mode;
        let currentYear = moment().year();
      return mode === 'month' && (date.getFullYear() > currentYear);

    }
  };
}

  /**
     * Set date options Open
     */
   private dateOptionsOpen(): void {
  this.$scope.dateOptionsAnnually = {
    minMode: 'day',
  };
}


  /**
   * @param  {ISystemConfigurationSpring} data
   * @returns default or current value for itemName parameter
   */
  private getSystemConfigurationItem(data: ISystemConfigurationSpring, itemName: string): string {
  for (let item of data.content) {
    if (item.item_name === itemName) {
      if (typeof (item.current_value) === 'string') {
        return item.current_value;
      } else {
        return item.default_value;
      }
    }
  }
}

  /**
   * Prepends `0` to a
   * single digit number
   *
   * @param  {number} n
   * @returns string
   */
  private str_pad(n: number): string {
  return String('0' + n).slice(-2);
}

  private getProgressBarValue(accountsTotal: any, accountNumber: any, flightsTotal: any, flightNumber: any): number {
  if (accountsTotal && flightsTotal && flightNumber) {
    if (accountsTotal === accountNumber) {
      this.$scope.progressBarValue = accountsTotal - 1 + flightNumber / flightsTotal;
    } else {
      this.$scope.progressBarValue = accountNumber;
    }
  }
  return this.$scope.progressBarValue;
}
}
function maxDate(maxDate: any) {
  throw new Error('Function not implemented.');

}

