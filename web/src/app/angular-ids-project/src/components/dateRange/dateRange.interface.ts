export interface ICalendarObject {
  open: boolean;
  date: Date;
  time;
  minDate?: Date;
  maxDate?: Date;
  initDate?: Date;
}

export interface IStartEndDates extends ng.IScope {
  showTimepickers: boolean;
  start: ICalendarObject;
  end: ICalendarObject;
  control: IStartEndDates;
  getUTCStartDate: () => Date;
  getUTCEndDate: () => Date;
  setUTCStartDate: (dateObject: Date) => void;
  setUTCEndDate: (dateObject: Date) => void;
  reset: Function;
  timeChanged: Function;
  dateChanged: Function;
  startEndAdjust: boolean;
  dateOptions: Object;
  dateFormat: string;
  dateValidate: Function;
  dateRangeForm: IDateRangeForm;
  endName: string;
  startName: string;
}

export interface IDateRangeForm {
  end: ng.INgModelController;
  $setPristine: Function;
}
