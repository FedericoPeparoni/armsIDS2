// displays box with error and error description

/** @ngInject */
export function errorHandling(): angular.IDirective {

  return {
    restrict: 'E',
    templateUrl: 'app/angular-ids-project/src/components/errorHandling/errorHandling.html',
    replace: true,
    scope: {
      error: '='
    }
  };

}
