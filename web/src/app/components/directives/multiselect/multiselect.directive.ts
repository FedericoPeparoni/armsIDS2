
/**
 * Multiselect directive to be reusable across all multiselect inputs
 *
 * Accepts params as follows:
 *
 * <multiselect id="id" name="name" extra-settings="{ ...settings }" options="itemsForSelect" model="targetModel" events="{ ...events } isRequired="true"></multiselect>
 *
 */

/** @ngInject */
export function multiselect(): angular.IDirective {

  return {
    restrict: 'E',
    templateUrl: 'app/components/directives/multiselect/multiselect.template.html',
    controllerAs: 'MultiSelectController',
    controller: MultiselectController,
    scope: {
      id: '@',
      name: '@',
      extraSettings: '=',
      options: '=',
      model: '=',
      events: '=',
      isRequired: '<',
      search: '@?',
      translatedValue: '@?',
      disabled: '='
    }
  };
}

/** @ngInject */
class MultiselectController {

  constructor($rootScope: angular.IRootScopeService, $scope: ng.IScope, $translate: angular.translate.ITranslateService, $compile: any) {
    let baseOptions;

    $scope.$watch('options', () => {
      baseOptions = angular.copy($scope.options);
      this.translateOptions(baseOptions, $scope.options, $scope.translatedValue, $translate);
    });
    ($translate as any).onReady(() => $scope.texts = this.translateOptions(baseOptions, $scope.options, $scope.translatedValue, $translate));
    $rootScope.$on('$translateChangeEnd', () => this.translateOptions(baseOptions, $scope.options, $scope.translatedValue, $translate));

    $scope.compile = $compile;
    $scope.translate = $translate;
    $scope.testval = true;

    $scope.settings = {
      enableSearch: true ? !$scope.search : false,
      searchField: $scope.extraSettings.displayProp,
      smartButtonMaxItems: 5,
      scrollable: true,
      buttonClasses: 'multiselect-dropdown btn btn-default',
      ...$scope.extraSettings
    };
  }

  public translateOptions(baseOptions: Array<any>, options: Array<any>, translatedValue: string, $translate: angular.translate.ITranslateService): void {
    if (baseOptions && translatedValue) {
      for (let i = 0, len = options.length; i < len; i++) {
        options[i][translatedValue] = $translate.instant(baseOptions[i][translatedValue]);
      };
    }
  }
}
