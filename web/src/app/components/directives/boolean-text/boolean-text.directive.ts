/** @ngInject */
export function booleanText(): angular.IDirective {

  return {
    restrict: 'E',
    replace: true,
    templateUrl: 'app/components/directives/boolean-text/boolean-text.html',
    controller: BooleanTextController
  };
}

/** @ngInject */
class BooleanTextController {

  constructor($rootScope: angular.IRootScopeService, $scope: angular.IScope, $translate: angular.translate.ITranslateService) {
    this.setBooleanList($translate, $scope);

    $rootScope.$on('$translateChangeEnd', (resp: any) => {
      this.setBooleanList($translate, $scope);
    });
  }

  public setBooleanList($translate: angular.translate.ITranslateService, $scope: angular.IScope): void {
    $scope.booleanList = [
      { n: $translate.instant('True'), v: true },
      { n: $translate.instant('False'), v: false }
    ];
  }

}
