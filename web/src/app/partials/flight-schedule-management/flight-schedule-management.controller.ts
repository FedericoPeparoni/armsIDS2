// controllers
import { CRUDFileUploadController } from '../../angular-ids-project/src/helpers/controllers/crud-file-handler/crud-file-handler.controller';

// interfaces
import { IFlightScheduleManagementScope, IFlightScheduleSpring, IFlightSchedule } from './flight-schedule-management.interface';

// services
import { FlightScheduleManagementService } from './service/flight-schedule-management.service';
import { AccountsService } from '../accounts/service/accounts.service';
import { IAccountMinimal } from '../accounts/accounts.interface';

export class FlightScheduleManagementController extends CRUDFileUploadController {

  /* @ngInject */
  constructor(protected $scope: IFlightScheduleManagementScope, protected $uibModal: ng.ui.bootstrap.IModalService,
    private flightScheduleManagementService: FlightScheduleManagementService, private accountsService: AccountsService,
    private $translate: angular.translate.ITranslateService) {
    super($scope, flightScheduleManagementService, $uibModal, 'File Name');
    super.setup({ refresh: false });

    this.getAccountsWithFlightSchedule();

    // functions exposed to the template
    $scope.preUploadSchedules = (startDate: string, document: any) => this.preUploadSchedules(startDate, document);
    $scope.formatDailySchedules = (schedules: string, separator: string) => this.formatDailySchedules(schedules, separator);
    $scope.addDaysToSchedule = () => this.addDaysToSchedule($scope.days);
    $scope.refreshOverride = () => this.refreshOverride();
    $scope.clearFilters = () => this.clearFilters();
    $scope.edit = (schedule: IFlightSchedule) => this.editOverride(schedule);
    $scope.update = (editable: IFlightSchedule, id: number) => super.update(editable, id).then(() => this.getAccountsWithFlightSchedule());
    $scope.create = (editable: IFlightSchedule) => super.create(editable).then(() => this.getAccountsWithFlightSchedule());
    $scope.delete = (id: number) => super.delete(id).then(() => this.getAccountsWithFlightSchedule());

    // get formatted list of days for multiselect
    $scope.listOfDays = flightScheduleManagementService.getDaysOfWeek();

    // default objects required because super.setup() is called without refresh
    $scope.pagination = {};
    $scope.days = [];

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
    this.flightScheduleManagementService.preUploadSchedules(startDate, document).then((data: any) => {
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
    });
  }

  /**
   * Override edit to add the
   * days to the multiselect
   * upon clicking a record
   */
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
    // send daily schedule as comma separated string (eg. 1,5,7)
    // empty array of daily schedules will be set to null
    this.$scope.editable.daily_schedule = dailySchedule.length > 0
      ? dailySchedule.join(',') : null;
  }

  private getFilterParameters(): void {
    this.$scope.filterParameters = {
      'accountId': this.$scope.accountId,
      'search': this.$scope.textFilter,
      'page': this.$scope.pagination.number
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

  /**
   * Clears filters
   *
   * @returns void
   */
  private clearFilters(): void {
    this.reset();

    this.$scope.textFilter = null;
    this.$scope.days = [];

    this.$scope.editable = this.flightScheduleManagementService.getModel();
  }

  private getAccountsWithFlightSchedule(): any {
    this.accountsService.findSchedMinimalReturn().then((list: Array<IAccountMinimal>) => {
      this.$scope.accountsWithFlightSchedules = list;
      if (!this.$scope.accountId && this.$scope.accountsWithFlightSchedules[0]) {
        this.$scope.accountId = this.$scope.accountsWithFlightSchedules[0].id;
      }
    });
  }
}
