/** @ngInject */
export function paginationDisplay(): angular.IDirective {

  return {
    restrict: 'E',
    replace: true,
    templateUrl: 'app/components/directives/pagination-display/pagination-display.html'
  };
}
