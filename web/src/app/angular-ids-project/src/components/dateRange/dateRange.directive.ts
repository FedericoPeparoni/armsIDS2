// interfaces
import { IStartEndDates, ICalendarObject } from './dateRange.interface';

/**
 * startEndAdjust will convert the start and end dates to the start/end of the month
 * NOTE: even without this it'll convert start/end date hours/mins/secs to the start/end of the day
 */

/** @ngInject */
export function dateRange(): angular.IDirective {

  return {
    restrict: 'E',
    templateUrl: 'app/angular-ids-project/src/components/dateRange/dateRange.html',
    replace: true,
    controller: DateRangeController,
    controllerAs: 'DateRangeController',
    scope: {
      control: '=', // returns controller
      isRequired: '=', // required fields
      format: '@?', // format
      dateOptions: '=?', // date-options
      startEndAdjust: '=?', // changes dates to start of month/end of month
      endName: '@?',
      startName: '@?'
    }
  };
}

/** @ngInject */
export class DateRangeController {

  private dateModel: ICalendarObject = {
    open: false,
    date: null,
    time: null,
    minDate: null,
    maxDate: null
  };

  constructor(public $scope: IStartEndDates) {
    $scope.getUTCStartDate = () => this.getUTCStartDate();
    $scope.getUTCEndDate = () => this.getUTCEndDate();
    $scope.setUTCStartDate = (dateObject: Date) => this.setUTCStartDate(dateObject);
    $scope.setUTCEndDate = (dateObject: Date) => this.setUTCEndDate(dateObject);
    $scope.dateChanged = (type: string, date: Date) => this.dateChanged(type, date);
    $scope.reset = () => this.reset();
    $scope.dateValidate = () => this.dateValidate();
    $scope.control = $scope;
    this.reset(); // initial set up

    $scope.startName = $scope.startName === undefined ? 'Start Date' : $scope.startName;
    $scope.endName = $scope.endName === undefined ? 'End Date' : $scope.endName;
  }

  public reset(): void {
    this.$scope.start = angular.copy(this.dateModel);
    this.$scope.end = angular.copy(this.dateModel);
    if (this.$scope.dateRangeForm && this.getUTCStartDate() !== undefined && this.getUTCEndDate() !== undefined) {
      this.$scope.dateRangeForm.$setPristine();
    }
  }

  public setUTCStartDate(date: Date): void {
    if (date.getTime !== undefined) {
      date = this.convertStartDate(date);
      this.$scope.start.date = date;
      this.setEndMinDate(date);
      this.dateValidate();
    }
  }

  public setUTCEndDate(date: Date): void {
    if (date.getTime !== undefined) {
      date = this.convertEndDate(date);
      this.$scope.end.date = date;
      this.setStartMaxDate(date);
      this.dateValidate();
    }
  }

  public getUTCEndDate(): Date {
    return this.$scope.end.date;
  }

  public getUTCStartDate(): Date {
    return this.$scope.start.date;
  }

  private dateValidate(): void {
    if (this.getUTCStartDate() !== undefined && this.getUTCEndDate() !== undefined) {
      if (this.getUTCStartDate() > this.getUTCEndDate()) {
        this.$scope.dateRangeForm.end.$setTouched(); // in case the end date has not been modified since loading, this will allow it to be highlighted
        this.$scope.dateRangeForm.end.$setDirty(); // in case the end date has not been modified since loading, this will allow it to be highlighted
        this.$scope.dateRangeForm.end.$setValidity('valid', false);
      } else {
        this.$scope.dateRangeForm.end.$setValidity('valid', true);
      }
    }
  }

  // fires when the datePickers inside are changed manually
  private dateChanged(type: string, date: Date): void {
    if (date === undefined || date === null) {
      return;
    }

    switch (type) {
      case 'start':
        date = this.convertStartDate(date);
        this.setUTCStartDate(date);
        break;
      case 'end':
        date = this.convertEndDate(date);
        this.setUTCEndDate(date);
        break;
    }

  }

  private setStartMaxDate(date: Date): void {
    this.$scope.start.maxDate = date;
  }

  private setEndMinDate(date: Date): void {
    this.$scope.end.minDate = date;
  }

  private convertStartDate(date: Date): Date {
    if (this.$scope.startEndAdjust) { // converts the date to the start of the month
      date.setUTCDate(1);
    }
    date.setUTCHours(0);
    date.setMinutes(0);
    date.setSeconds(0);
    return date;
  }

  private convertEndDate(date: Date): Date {
    if (this.$scope.startEndAdjust) { // converts the date to the end of the month
      const lastDate = new Date(date.getUTCFullYear(), date.getUTCMonth() + 1, 0, 23, 59, 59);
      date.setUTCDate(lastDate.getDate());
    }
    date.setUTCHours(23);
    date.setMinutes(59);
    date.setSeconds(59);
    return date;
  }
}
