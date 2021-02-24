import { ITitleScope } from './title.interface';

/** @ngInject */
export function title(): angular.IDirective {

  return {
    restrict: 'E',
    template: '{{ prefix }} {{ divider }} {{ title | translate }} {{ suffix }}',
    controller: TitleController,
    controllerAs: 'TitleController',
    scope: {},
    link: linkFunc,
    transclude: true
  };

}

function linkFunc(scope: ITitleScope, elem: any, attrs: any): void {
  scope.suffix = typeof attrs.suffix !== 'undefined' ? attrs.suffix : '';
  scope.prefix = typeof attrs.prefix !== 'undefined' ? attrs.prefix : '';
}

/** @ngInject */
export class TitleController {
  public constructor(private $scope: ITitleScope, $translate: angular.translate.ITranslateService) {
    $scope.$on('$stateChangeStart', (event: any, toState: any) => {
      // title must be translated alone for matching
      $scope.title = toState.title;
      $scope.divider = toState.title ? ' - ' : '';
    });
  }
}
