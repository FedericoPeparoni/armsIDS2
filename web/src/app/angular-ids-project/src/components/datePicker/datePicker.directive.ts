/**
 * DatePicker
 *
 * What it does:
 *
 *  This gets the users SystemConfiguration:Date format from LocalStorage and puts it on the Angular Bootstrap UI datepicker (from parent)
 *
 * What it saves:
 *
 *  Requiring that every controller needs to have the LocalStorageService to get the SystemConfiguration:Date format
 */

// services
import { LocalStorageService } from '../services/localStorage/localStorage.service';

/** @ngInject */
export function datePicker(): angular.IDirective {

  return {
    restrict: 'E',
    template: `<input ng-model-options="{ timezone: 'UTC' }"/>`,
    controller: DatePickerController,
    controllerAs: 'DatePickerController',
    replace: true,
    link: linkFunc,
    require: '?ngModel'
  };

}

interface IDatePicker extends ng.IScope {
  dateFormat: string;
}

/** @ngInject */
export class DatePickerController {

  constructor(private $scope: IDatePicker) {
    $scope.dateFormat = LocalStorageService.get('SystemConfiguration:Date format');
  }

}

function linkFunc(scope: IDatePicker, elem: ng.IAugmentedJQuery, attrs: any, ctrl: ng.INgModelController): void {

  // this fires when the datepicker calendar is used or input value is manually changed
  // it is not called when the model changes outside the directive
  ctrl.$parsers.push((viewValue: Date) => {
    if (viewValue !== null && typeof viewValue === 'object') {
      viewValue.setUTCHours(0);
    }

    return viewValue;
  });

  // fires on model change, if it's a string, we change it to a date object
  scope.$watch(attrs.ngModel, (val: string | Date): void => {
    if (typeof val === 'string') {
      let dt = new Date(val);
      ctrl.$setViewValue(dt);
    } else {
      ctrl.$modelValue = new Date();
    }
  });

  // this only fires when the model is updated from outside the directive
  // this changes what is displayed in the UI, but changes here will not update the model or calendar popup
  ctrl.$formatters.push((modelValue: string): Date => {
    if (!modelValue) {
      return undefined;
    }

    return new Date(modelValue);
  });

}
