import { IDownloadScope } from './download.interface';

/** @ngInject */
export function download(): angular.IDirective {

  return {
    restrict: 'E',
    templateUrl: 'app/angular-ids-project/src/components/download/download.html',
    link: linkFunc,
    scope: {
      url: '='
    },
    replace: true,
    transclude: true
  };

}

function linkFunc(scope: IDownloadScope, elem: any, attrs: any, ctrl: any): void {
  scope.types = ['pdf', 'csv'];
  scope.notImplementedTypes = typeof attrs.notImplementedTypes !== 'undefined' ? attrs.notImplementedTypes.split(',') : [];
}
