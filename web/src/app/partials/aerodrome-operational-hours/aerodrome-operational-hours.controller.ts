// controllers
import { CRUDFormControllerUserService } from '../../angular-ids-project/src/helpers/controllers/crud-form-user-service/crud-form-user-service.controller';

// interfaces
import { IAerodromeOperationalHoursScope, IAerodromeOperationalHours } from './aerodrome-operational-hours.interface';

// services
import { AerodromeOperationalHoursService } from './service/aerodrome-operational-hours.service';

export class AerodromeOperationalHoursController extends CRUDFormControllerUserService {

  /* @ngInject */
  constructor(protected $scope: IAerodromeOperationalHoursScope, private aerodromeOperationalHoursService: AerodromeOperationalHoursService) {
    super($scope, aerodromeOperationalHoursService);
    super.setup();

    $scope.showTimeErrorMessage = [];
    $scope.fromTimeIsAfterEndHours = [];
    $scope.timeIsAlreadyExist = [];
    $scope.showDateErrorMessage = [];
    $scope.dateIsAlreadyExist = [];

    $scope.timeFormatErrorMessage = 'Please enter valid hours (01-23) and minutes (01-59), each range should be separated by a semicolon';
    $scope.timeIsAlreadyExistErrorMessage = 'Time is overlapping';
    $scope.timeStartEndErrorMessage = 'From hours should be before end hours';
    $scope.dateFormatErrorMessage = 'Please enter valid month (01-12) and date (01-31), each range should be separated by a semicolon';
    $scope.dateIsAlreadyExistErrorMessage = 'Duplicate dates';

    this.getFilterParameters();

    $scope.$watch('list', () => this.setCopyFromAerodromeList());
    $scope.copyDataFromAerodrome = (aerodrome: string) => this.copyDataFromAerodrome(aerodrome);
    $scope.timeValidate = (time: string, fieldName: string, editableFieldName: string) => this.timeValidate(time, fieldName, editableFieldName);
    $scope.dateValidate = (date: string, fieldName: string, editableFieldName: string) => this.dateValidate(date, fieldName, editableFieldName);
    $scope.create = (editable: IAerodromeOperationalHours) => this.createOverride(editable);
    $scope.update = (editable: IAerodromeOperationalHours, id: number) => this.updateOverride(editable, id);
    $scope.delete = (id: number) => this.deleteOverride(id);
    $scope.edit = (editable: IAerodromeOperationalHours) => this.editOverride(editable);
    $scope.refresh = () => this.refreshOverride();
    $scope.reset = () => this.resetOverride();
  }

  private createOverride(editable: IAerodromeOperationalHours): void {
    super.create(editable).then(() => this.resetOverride());
  }

  private updateOverride(editable: IAerodromeOperationalHours, id: number): void {
    super.update(editable, id).then(() => this.resetOverride());
  }

  private deleteOverride(id: number): void {
    super.delete(id).then(() => this.resetOverride());
  }

  private editOverride(editable: IAerodromeOperationalHours): void {
    this.resetOverride();
    super.edit(editable);
  }

  private resetErrors(): void {
    const errors = ['showTimeErrorMessage',
                    'fromTimeIsAfterEndHours',
                    'timeIsAlreadyExist',
                    'showDateErrorMessage',
                    'dateIsAlreadyExist'];

    errors.forEach((element: string) => {
      if (this.$scope[element]) {
        for (let item in this.$scope[element]) {
          if (this.$scope[element].hasOwnProperty(item)) {
            this.$scope[element][item] = null;
          }
        }
      }
    });
  }

  private setCopyFromAerodromeList(): void {
    this.$scope.copyFromAerodromeList = [];
    if (this.$scope.list) {
      this.$scope.list.forEach((item: IAerodromeOperationalHours) => this.$scope.copyFromAerodromeList.push(item.aerodrome));
    }
  }

  private copyDataFromAerodrome(aerodrome: string): void {
    const item = this.$scope.list.find((aerodromeOperationalHours: IAerodromeOperationalHours) =>
      aerodromeOperationalHours.aerodrome.aerodrome_name === aerodrome);

    const keys = Object.keys(this.$scope.editable);
    keys.forEach((key: string) => {
      if (key !== 'id' && key !== 'aerodrome') {
        this.$scope.editable[key] = item[key];
      }
    });
  }

  private dateValidate(date: string, fieldName: string, editableFieldName: string): void {
    if (date) {
      const array = date.split(';').filter((element: string) => element).sort();
      if (this.checkIfDateValid(array, fieldName)) {
        this.setDateFieldError(fieldName, true, false);
        this.$scope.editable[editableFieldName] = array.join(';');
      }
    } else {
      this.setDateFieldError(fieldName, true, false);
    }
  }

  private checkIfDateValid(array: any, fieldName: string): boolean {
    for (let index in array) {
      if (array.hasOwnProperty(index)) {
        if (this.checkIfDateIsAlreadyExist(array, array[index], fieldName, parseInt(index, 10))) {
          if (!moment(array[index], 'MM/DD', true).isValid() && array[index] !== '02/29') {
            this.$scope.form[fieldName].$setValidity('valid', false);
            this.$scope.showDateErrorMessage[fieldName] = true;
            return false;
          }
        } else {
          return false;
        }
      }
    }
    return true;
  }

  private checkIfDateIsAlreadyExist(array: any, date: string, fieldName: string, index: number): boolean {
    if (array.includes(date) && array.indexOf(date) !== index) {
      this.$scope.form[fieldName].$setValidity('valid', false);
      this.$scope.dateIsAlreadyExist[fieldName] = true;
      this.$scope.showDateErrorMessage[fieldName] = false;
      return false;
    }
    return true;
  }

  private timeValidate(time: string, fieldName: string, editableFieldName: string): void {
    if (time) {
      const array = time.split(';').filter((element: string) => element).sort();
      if (this.checkIfTimeValid(array, fieldName) && this.checkIfTimeOverlapping(array, fieldName)) {
        this.setTimeFieldError(fieldName, true, false);
        this.$scope.editable[editableFieldName] = array.join(';');
      }
    } else {
      this.setTimeFieldError(fieldName, true, false);
    }
  }

  private checkIfTimeValid(array: any, fieldName: string): boolean {
    for (let element of array) {
      const hours = element.split('-');
      const startTime = moment(hours[0], 'HHmm', true);
      const endTime = moment(hours[1], 'HHmm', true);

      if (startTime.isValid() && endTime.isValid() && hours[0] !== '2400' && hours[1] !== '2400' && hours.length === 2) {
        if (!startTime.isBefore(endTime)) {
          this.$scope.form[fieldName].$setValidity('valid', false);
          this.$scope.fromTimeIsAfterEndHours[fieldName] = true;
          return false;
        }
      } else {
        this.$scope.form[fieldName].$setValidity('valid', false);
        this.$scope.showTimeErrorMessage[fieldName] = true;
        return false;
      }
    }
    return true;
  }

  private checkIfTimeOverlapping(array: any, fieldName: string): boolean {
    for (let element in array) {
      if (array.hasOwnProperty(element)) {
        if (!this.checkIfTimeIsAlreadyExist(array, array[element], parseInt(element, 10))) {
          this.$scope.form[fieldName].$setValidity('valid', false);
          this.$scope.timeIsAlreadyExist[fieldName] = true;
          return false;
        }
      }
    }
    return true;
  }

  private checkIfTimeIsAlreadyExist(array: any, time: string, index: number): boolean {
    const exist = this.includesTime(array, time, index);
    if (exist) {
      return false;
    }
    return true;
  }

  private includesTime(array: any, time: string, index: number): boolean {
    const hours = time.split('-');
    const startTime = moment(hours[0], 'HHmm', true);
    const endTime = moment(hours[1], 'HHmm', true);

    return array
    .filter((element: string, elementIndex: number) => elementIndex !== index)
    .some((element: string) => {
      const existingHours = element.split('-');
      const existingStartTime = moment(existingHours[0], 'HHmm', true);
      const existingEndTime = moment(existingHours[1], 'HHmm', true);

      return startTime.isBetween(existingStartTime, existingEndTime) || endTime.isBetween(existingStartTime, existingEndTime) ||
        existingStartTime.isBetween(startTime, endTime) || existingEndTime.isBetween(startTime, endTime) ||
        startTime.isSame(existingStartTime) || startTime.isSame(existingEndTime) ||
        endTime.isSame(existingStartTime) || endTime.isSame(existingEndTime);
    });
  }

  private setTimeFieldError(fieldName: string, valid: boolean, error: boolean): void {
    this.$scope.form[fieldName].$setValidity('valid', valid);
    this.$scope.showTimeErrorMessage[fieldName] = error;
    this.$scope.fromTimeIsAfterEndHours[fieldName] = error;
    this.$scope.timeIsAlreadyExist[fieldName] = error;
  }

  private setDateFieldError(fieldName: string, valid: boolean, error: boolean): void {
    this.$scope.form[fieldName].$setValidity('valid', valid);
    this.$scope.showDateErrorMessage[fieldName] = error;
    this.$scope.dateIsAlreadyExist[fieldName] = error;
  }

  private getFilterParameters(): void {
    this.$scope.filterParameters = {
      search: this.$scope.search,
      page: this.$scope.pagination ? this.$scope.pagination.number : 0
    };
  }

  private refreshOverride(): void {
    this.getFilterParameters();
    super.list(this.$scope.filterParameters, this.$scope.getSortQueryString());
  }

  private resetOverride(): void {
    super.reset();
    this.resetErrors();
    this.$scope.copyFromAerodrome = null;
  }
}
