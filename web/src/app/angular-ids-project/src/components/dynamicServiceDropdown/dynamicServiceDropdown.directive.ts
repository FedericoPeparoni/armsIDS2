/**
 * Dynamic Service Dropdown
 *
 * Using a string, the directive will bind a service which will keep it encapsulated.  This removes having the controller having to hold onto it
 *
 * todo: add ability to use the multi select dropdown
 */

import { IDynamicServiceDropdown } from './dynamicServiceDropdown.interface';
/** @ngInject */
export function dynamicServiceDropdown(): angular.IDirective {

  return {
    restrict: 'E',
    replace: true,
    templateUrl: 'app/angular-ids-project/src/components/dynamicServiceDropdown/dynamicServiceDropdown.html',
    controller: DynamicServiceDropdown,
    link: linkFunc,
    scope: {
      method: '@',
      isRequired: '<',
      service: '@',
      allowEmpty: '@',
      queryString: '@?', // if used, appends a string to the end of the endpoint URL
      disabled: '<?',
      sortedBy: '@?', // string; ex. aircraft_name
      selected: '@',
      field: '@',
      object: '@',
      listFilter: '=?',
      selectedValue: '=?',
      model: '@'
    },
    require: 'ngModel'
  };
}

function linkFunc(scope: IDynamicServiceDropdown, elem: any, attrs: any, ctrl: any): void {
  scope.dropdownType = attrs.dropdownType;
  scope.ngOptions = attrs.options;
  scope.ngModel = ctrl;
  scope.class = attrs.class;
  scope.allowEmpty = attrs.allowEmpty;

  scope.$watch(() => { // watcher to update the controller bind with `ngModel`
    return ctrl.$viewValue;
  }, (newValue: string) => {
    ctrl.$setViewValue(newValue);
    ctrl.$render();
  });
}

/** @ngInject */
export class DynamicServiceDropdown {

  constructor($scope: IDynamicServiceDropdown, $injector: ng.auto.IInjectorService) {

    if ($scope.service.substring(0, 2) !== '::') {
      console.error(`Please add '::' to the front of the 'service' binding, otherwise this won't work`);
      return;
    }

    let serviceString = $scope.service.substring(2);
    let service: any = $injector.get(serviceString);

    let method = $scope.method ? $scope.method : 'listAll'; // unless specified we call the `listAll` method

    const sortBy = (list: any) => {
      // sort array of objects by key/value from param
      const content =  list.content.sort((a: any, b: any) => a[$scope.sortedBy].localeCompare(b[$scope.sortedBy]));

      return {...list, content};
    };

    if ($scope.queryString) { // appends a string to the end of the URL
      let serviceWithParams = angular.copy(service);
      serviceWithParams.endpoint += `?${$scope.queryString}`;
      service = serviceWithParams;
    }

    let serviceCall = service[method]();

    if (typeof serviceCall.then !== 'undefined') { // determine if is a promise or not, promise if AJAX call
      serviceCall.then((data: Array<any>) => {
        $scope.list = $scope.sortedBy ? sortBy(data) : data;

        if ($scope.selected && $scope.list.content && $scope.field) {
          if ($scope.object) {
            $scope.$parent[$scope.object][$scope.model] = $scope.selectedValue = $scope.list.content.find((item: any) => item[$scope.field] === $scope.selected);
          } else {
            $scope.$parent[$scope.model] = $scope.selectedValue = $scope.list.content.find((item: any) => item[$scope.field] === $scope.selected);
          }
        };

      });
    } else {
      $scope.list = $scope.sortedBy ? sortBy(serviceCall) : serviceCall; // is just a regular array/object
    }

  }

}
