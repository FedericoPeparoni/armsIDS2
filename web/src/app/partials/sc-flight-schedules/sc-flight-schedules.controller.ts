// controllers
import { CRUDFileUploadController } from '../../angular-ids-project/src/helpers/controllers/crud-file-handler/crud-file-handler.controller';

// interfaces
import { IFlightScheduleSpring, IFlightSchedule } from '../flight-schedule-management/flight-schedule-management.interface';
import { ISpringPageableParams } from '../../angular-ids-project/src/helpers/services/crud.service';
import { IRestangularResponse } from '../../angular-ids-project/src/helpers/interfaces/restangularError.interface';
import { IScFlightScheduleManagementScope } from './sc-flight-schedules.interface';

// services
import { ScFlightSchedulesService } from './service/sc-flight-schedules.service';
import { FlightScheduleManagementService } from '../flight-schedule-management/service/flight-schedule-management.service';
import { SystemConfigurationService } from '../system-configuration/service/system-configuration.service';

// constants
import { SysConfigConstants } from '../system-configuration/system-configuration.constants';
import { ScAccountManagementService } from '../sc-account-management/service/sc-account-management.service';

export class ScFlightSchedulesController extends CRUDFileUploadController {

  /* @ngInject */
  constructor(protected $scope: IScFlightScheduleManagementScope, protected $uibModal: ng.ui.bootstrap.IModalService,
    private scFlightSchedulesService: ScFlightSchedulesService, private flightScheduleManagementService: FlightScheduleManagementService,
    private systemConfigurationService: SystemConfigurationService, private scAccountManagementService: ScAccountManagementService,
    private $translate: angular.translate.ITranslateService) {

    super($scope, scFlightSchedulesService, $uibModal, 'File Name');
    super.setup({ refresh: false });

    this.getAccountsWithFlightSchedule();

    $scope.needAdminApproval = this.systemConfigurationService.getBooleanFromValueByName(<any>SysConfigConstants.REQUIRE_ADMIN_APPROVAL_FOR_SC_FLIGHT_SCHEDULES);

    // for daily schedule
    $scope.days = []; // initialize model for multiselect
    $scope.listOfDays = flightScheduleManagementService.getDaysOfWeek();
    $scope.addDaysToSchedule = () => this.addDaysToSchedule($scope.days);
    $scope.formatDailySchedules = (schedules: string, separator: string) => this.formatDailySchedules(schedules, separator);
    $scope.preUploadSchedules = (startDate: string, document: any) => this.preUploadSchedules(startDate, document);
    $scope.edit = (schedule: IFlightSchedule) => this.editOverride(schedule);
    $scope.setStatus = (item: IFlightSchedule) => this.setStatus(item);
    $scope.update = (editable: IFlightSchedule, id: number) => super.update(editable, id).then(() => this.getAccountsWithFlightSchedule());
    $scope.create = (editable: IFlightSchedule) => super.create(editable).then(() => this.getAccountsWithFlightSchedule());
    $scope.delete = (id: number) => super.delete(id).then(() => this.getAccountsWithFlightSchedule());

    $scope.reset = () => this.resetOverride();
    $scope.refreshOverride = () => this.refreshOverride();

    // override list, should only
    // call if accountId is present
    this.list = () => this.refreshOverride();

    // because refresh:false is
    // in super.setup, we need to
    // init the pagination object
    $scope.pagination = <ISpringPageableParams>{};

    // do not make a call for flight schedules until an account has been
    // selected in the dropdown service
    $scope.$watch('accountId', () => {
      this.refreshOverride();
    });
  }

  /**
   * Checks the CSV and warns the user
   * if any schedules will be set
   * to inactive
   *
   * @param  {string} startDate
   * @param  {any} document
   */
  private preUploadSchedules(startDate: string, document: any): void {
    this.scFlightSchedulesService.preUploadSchedules(startDate, document)
      .then((data: any) => {
        let changedSchedulesSet = new Set();
        let changedSchedulesArray = [];

        for (let item of data) {
          changedSchedulesSet.add(`${item.id} ${item.details}`);
        }

        changedSchedulesArray = Array.from(changedSchedulesSet);
        const warning = changedSchedulesArray.join('\n');

        // only create model if ther
        // is a warning to show
        if (warning && warning.length) {
          this.createWarningModel(warning);
        } else {
          super.upload('PUT', null, null, 'refreshOverride', { 'startDate': this.$scope.start.toISOString() })
            .then(() => this.getAccountsWithFlightSchedule());
        }
      },
      (error: IRestangularResponse) => {
        this.$scope.error = { error: error };
      });
  }

  /**
   * Modal to warn user of schedule
   * overriding
   */
  private createWarningModel(warning: string): void {
    let modal = this.$uibModal.open({
      templateUrl: 'app/partials/flight-schedule-management/schedule-warning.template.html',
      controller: ['$scope', ($scope: ng.IScope) => {
        $scope.warning = warning;
        $scope.close = () => modal.close();
        $scope.confirm = () => {
          modal.close();
          super.upload('PUT', null, null, 'refreshOverride', { 'startDate': this.$scope.start.toISOString() })
            .then(() => this.getAccountsWithFlightSchedule());
        };
      }]
    });
  }

  /**
   * Adds days of week selected from
   * multiselect to a comma separated
   * string of values
   *
   * @param  {Array<any>} days
   */
  private addDaysToSchedule(days: Array<any>): void {
    const dailySchedule = days.map((day: any) => {
      return day.value;
    });
    // send daily schedule as comma
    // separated string
    // eg. 1,5,7
    this.$scope.editable.daily_schedule = dailySchedule.join(',');
  }

  private getFilterParameters(): void {
    this.$scope.filterParameters = {
      accountId: this.$scope.accountId,
      search: this.$scope.search,
      page: this.$scope.pagination.number
    };
  }

  /**
   * Calls list with applied filters and create object for download
   *
   * @returns promise
   */
  private refreshOverride(): ng.IPromise<any> {
    this.getFilterParameters();
    if (this.$scope.accountId) {
      return super.list(this.$scope.filterParameters, this.$scope.getSortQueryString()).then((schedules: IFlightScheduleSpring) => {
        this.$scope.accountSchedules = schedules.content.map((schedule: IFlightSchedule) => {
          return {
            'flight_service_number': schedule.flight_service_number,
            'dep_ad': schedule.dep_ad,
            'dep_time': schedule.dep_time,
            'dest_ad': schedule.dest_ad,
            'dest_time': schedule.dest_time,
            'daily_schedule': this.formatDailySchedules(schedule.daily_schedule, '/')
          };
        });
      });
    }
  }

  // override reset to also
  // clear the multiselect
  private resetOverride(): void {
    this.$scope.days = [];
    super.reset();
  }

  // override edit to add the
  // days to the multiselect
  // upon clicking a record
  private editOverride(schedule: IFlightSchedule): void {
    const days = schedule.daily_schedule.split(',');
    this.$scope.days = []; // clear out current selections

    // match each sched number to the value
    // of the days in listOfDays
    for (let day of days) {
      for (let item of this.$scope.listOfDays) {
        if (day === item.value) {
          this.$scope.days.push(item);
        }
      }
    }
    super.edit(schedule);
  }

  /**
   * Format days from CSV for
   * data-grid display
   *
   * @param  {string} days
   * @returns string
   */
  private formatDailySchedules(days: string, separator: string): string {
    const arrOfDays = days.split(',');
    const daysInNumbers: string[] = [];

    for (let day of arrOfDays) {
      switch (day) {
        case '1': daysInNumbers.push(` ${this.$translate.instant('SUNDAY')}`); break;
        case '2': daysInNumbers.push(` ${this.$translate.instant('MONDAY')}`); break;
        case '3': daysInNumbers.push(` ${this.$translate.instant('TUESDAY')}`); break;
        case '4': daysInNumbers.push(` ${this.$translate.instant('WEDNESDAY')}`); break;
        case '5': daysInNumbers.push(` ${this.$translate.instant('THURSDAY')}`); break;
        case '6': daysInNumbers.push(` ${this.$translate.instant('FRIDAY')}`); break;
        case '7': daysInNumbers.push(` ${this.$translate.instant('SATURDAY')}`); break;
      };
    };

    return daysInNumbers.join(separator).trim();
  }

  private getAccountsWithFlightSchedule(): any {
    this.scAccountManagementService.getAccountsWithFlightSchedules().then((list: any) => {
      this.$scope.accountsWithFlightSchedules = list;
      if (!this.$scope.accountId && this.$scope.accountsWithFlightSchedules[0]) {
        this.$scope.accountId = this.$scope.accountsWithFlightSchedules[0].id;
      }
    });

  }

  private setStatus(item: any): string {
    if (item.sc_request_type === 'create') {
      return 'create pending';
    } else if (item.sc_request_type === 'update') {
      return 'update pending';
    } else if (item.sc_request_type === 'delete') {
      return 'delete pending';
    } else if (!item.sc_request_type) {
      return 'approved';
    } else {
      return '';
    }
  }

}
