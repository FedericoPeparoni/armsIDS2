// controllers
import { CRUDFormControllerUserService } from '../../angular-ids-project/src/helpers/controllers/crud-form-user-service/crud-form-user-service.controller';

// services
import { AccountsService } from '../accounts/service/accounts.service';
import { AerodromesService } from '../aerodromes/service/aerodromes.service';

// interface
import { IReportGeneration, ReportGenerationScope } from './report-generation.interface';
import { IAccountMinimal } from '../accounts/accounts.interface';
import { IAerodromeSpring, IAerodrome } from '../aerodromes/aerodromes.interface';
import { ICurrency } from '../currency-management/currency-management.interface';
import { IUser } from '../users/users.interface';

// services
import { ReportGenerationService } from './service/report-generation.service';
import { FlightMovementManagementService } from '../flight-movement-management/service/flight-movement-management.service';
import { CustomDate } from '../../angular-ids-project/src/components/services/customDate/customDate.service';
import { CurrencyManagementService } from '../currency-management/service/currency-management.service';
import { LocalStorageService } from '../../angular-ids-project/src/components/services/localStorage/localStorage.service';
import { UsersService } from '../users/service/users.service';

// constants
import { OrganizationService } from '../organization/service/organization.service';
import { IActiveOrganization } from '../organization/organization.interface';
import { SysConfigConstants } from '../system-configuration/system-configuration.constants';

const reportsWithDatePicker = [
  'accountStatementReport',
  'revenueLostToExemptionsReport',
  'summarisedInvoiceTotalsReport',
  'aircraftTypesReport',
  'creditAndDebitNotesReport',
  'aircraftRegistrationReport',
  'missingBillingInformationReport',
  'aircraftRegistrationTrackingReport',
  'kcaaAccountStatementReport',
  'kcaaPassengerReport',
  'passengerReport',
  'revenueReport',
  'unifiedTaxReport'
];

export class ReportGenerationController extends CRUDFormControllerUserService {

  /**
   * Active organization flag based on system configuration setting.
   */
  private activeOrg: IActiveOrganization;

  /* @ngInject */
  constructor(public $scope: ReportGenerationScope,
    private reportGenerationService: ReportGenerationService,
    private accountsService: AccountsService,
    private aerodromesService: AerodromesService,
    private flightMovementManagementService: FlightMovementManagementService,
    private $timeout: ng.ITimeoutService, private customDate: CustomDate,
    private organizationService: OrganizationService,
    private currencyManagementService: CurrencyManagementService,
    private usersService: UsersService) {
    super($scope, reportGenerationService);

    // retrieve active organization flags from system configuration settings
    this.activeOrg = this.organizationService.active();

    // set shortcut organization flags to show different reports
    $scope.CAAB = this.activeOrg.isCaab;
    $scope.DC_ANSP = this.activeOrg.isDcAnsp;
    $scope.EANA = this.activeOrg.isEana;
    $scope.INAC = this.activeOrg.isInac;
    $scope.KCAA = this.activeOrg.isKcaa;
    $scope.ZACL = this.activeOrg.isZacl;
    $scope.TTCAA = this.activeOrg.isTTCAA;

    // select initial report
    $timeout(() => { $scope.editable.report = $scope.CAAB ? 'accountStatusReport' : 'kcaaDebtorReport'; });

    this.$scope.customDate = this.customDate.returnDateFormatStr(false);

    // multiselect for accounts
    accountsService.findAllCreditMinimalReturn().then((data: Array<IAccountMinimal>) => $scope.accountsList = data);
    accountsService.findAllMinimalReturn().then((data: Array<IAccountMinimal>) => $scope.accountsListWithCash = data);
    $scope.selectedAccounts = [];
    $scope.addAccountToList = () => this.addAccountToList($scope.selectedAccounts);

    // multiselect for aerodromes
    aerodromesService.listAll().then((data: IAerodromeSpring) => {
      $scope.aerodromesList = data.content;
      const zzzzAd: any = { aerodrome_name: 'ZZZZ' };
      $scope.aerodromesList.push(zzzzAd);
    });

    usersService.current.then((user: IUser) => $scope.userName = user.name);

    $scope.selectedAerodromes = [];
    $scope.addAerodromeToList = () => this.addAerodromeToList($scope.selectedAerodromes);

    $scope.setClear = (report: IReportGeneration) => this.setClear(report);
    $scope.getFilterType = (report: IReportGeneration) => this.getFilterType(report);
    $scope.returnFilteredDate = (date: Date) => this.returnFilteredDate(date);
    $scope.returnEndOfMonth = (date: Date) => this.returnEndOfMonth(date);

    // multiselect for missing billing errors
    $scope.missingInfo = []; // initialize model
    $scope.listOfMissingInfo = reportGenerationService.getMissingInfoErrors();
    $scope.formatMissingInfo = () => this.formatMissingInfo($scope.missingInfo);

    // multiselect for aircraft registrations
    $scope.aircraftRegistrations = []; // initialize model

    flightMovementManagementService.getDistinctRegNum().then((registrations: any) => {
      const regs = registrations.map((element: string, index: number) => { return { id: index, name: element }; });

      $scope.listOfAirRegs = this.reduceAirRegs(regs);
    });

    // if accounts or aircraft reg numbers are not selected, need to be send as empty string that means 'all'
    $scope.accountIdsWithBrackets = '';
    $scope.aircraftRegs = '';

    this.currencyManagementService.getListCurrencyANSPAndUSD().then((currencies: ICurrency[]) => {
      $scope.currenciesList = currencies;
    });

    $scope.formatAircraftRegistrations = () => this.formatAircraftRegistrations($scope.aircraftRegistrations);
    $scope.getFiscalYear = () => this.getFiscalYear();

    $scope.requiresDatePicker = (reportType: string) => reportsWithDatePicker.find((report: string) => report === reportType);

    $scope.report_format = 'pdf';

    this.setDefaultDate();
  }

  public setDefaultDate(): void {
    if (this.$scope.control) {

      const date = new Date();
      const startDate = new Date(date.getFullYear(), date.getMonth() - 1, 1);
      const endDate = new Date(date.getFullYear(), date.getMonth(), 0);

      this.$scope.control.setUTCStartDate(startDate);
      this.$scope.control.setUTCEndDate(endDate);
    }
  }

  protected getFilterType(report: IReportGeneration): number {
    if (report.used_defined && !report.used_undefined) {
      report.filter_type = 3;
    } else if (!report.used_defined && report.used_undefined) {
      report.filter_type = 2;
    } else if (report.used_defined && report.used_undefined) {
      report.filter_type = 1;
    }

    return report.filter_type;
  }

  protected setClear(report: IReportGeneration): void {
    this.setDefaultDate();
    this.$scope.selectedAccounts = [];
    this.$scope.missingInfo = [];
    this.$scope.editable = this.reportGenerationService.getModel();
    this.$scope.editable.report = report.report;
    this.$scope.editable.account_new_page = false;
  }

  private returnFilteredDate(date: Date): string { // returns date string without milliseconds
    if (date === undefined || date === null) {
      return;
    }
    return `${date.toISOString().slice(0, 19)}Z`;
  }

  private returnEndOfMonth(date: Date): string {
    if (date === undefined || date === null) {
      return;
    }
    const endDate = new Date(date.getFullYear(), date.getMonth() + 2, 0);
    return `${endDate.toISOString().slice(0, 11)}23:59:59Z`;
  }

  /**
   * Adds resolution errors selected from
   * multiselect to a comma separated
   * string of values
   *
   * @param  {Array<any>} missingInfo
   */
  private formatMissingInfo(missingInfo: Array<any>): void {
    const resolutionErrors = missingInfo.map((resolutionError: any) => {
      return resolutionError.value;
    });
    // send resolution
    // errors as comma
    // separated string
    // eg. atc-log,tower-log
    this.$scope.billingErrors = `{${resolutionErrors.join(',')}}`;
  }

  /**
   * Adds aircraft registrations,
   * selected from the multiselect,
   * to a comma separated string
   *
   * @param  {Array<any>} regs
   */
  private formatAircraftRegistrations(regs: Array<any>): void {
    const registrations = regs.map((registration: any) => {
      return registration.name;
    });

    this.$scope.aircraftRegs = registrations.length ? `{${registrations.join(',')}}` : '';
  }

  /**
   * Remove invalid registrations from the list
   * These invalid registrations contains special characters that break BIRT SQL
   *
   * @param  {Array<any>} regs
   * @return {Array<any>} regs
   */
  private reduceAirRegs(regs: Array<any>): Array<any> {
    const regex: RegExp = /[^A-Za-z0-9]+/g;

    return regs.reduce(((result: any, reg: any) => {
      if (reg && reg.name && !reg.name.match(regex)) {
        result.push(reg);
      };

      return result;
    }), []);
  }

  /**
   * Adds accounts, selected
   * from the multiselect,
   * to a comma separated string
   *
   * @param  {Array<any>} selectedAccounts
   */
  private addAccountToList(selectedAccounts: Array<any>): void {
    const accounts = selectedAccounts.map((account: IAccountMinimal) => account.id);
    // send account ids as
    // comma separated string
    // eg. 1,2,3
    this.$scope.accountIds = accounts.join(',');
    this.$scope.accountIdsWithBrackets = accounts.length ? `{${this.$scope.accountIds}}` : '';

    // if report is aircraft reg tracking
    // and accounts are selected,
    // group by account is default true
    if (this.$scope.editable.report === 'aircraftRegistrationTrackingReport') {
      this.$scope.editable.group_by_account = selectedAccounts.length ? true : false;
    }
  }

  /**
   * Adds aerodromes, selected
   * from the multiselect, to a comma separated string
   * @param  {Array<any>} selectedAerodromes
   */
  private addAerodromeToList(selectedAerodromes: Array<any>): void {
    const aerodromes = selectedAerodromes.map((aerodrome: IAerodrome) => aerodrome.aerodrome_name);

    this.$scope.aerodromesIds = aerodromes.join(',');
    this.$scope.aerodromeIdsWithBrackets = aerodromes.length ? `{${this.$scope.aerodromesIds}}` : '';
  }

  private getFiscalYear(): void {
    if (this.$scope.editable.fiscal_year) {
      const fiscal = LocalStorageService.get(`SystemConfiguration:${SysConfigConstants.FIRST_DAY_OF_FISCAL_YEAR}`).replace('/', '-');
      const year = new Date().getFullYear();
      this.$scope.fiscalYear = `${year}-${fiscal}`;
    } else {
      this.$scope.fiscalYear = null;
    }
  }
}
