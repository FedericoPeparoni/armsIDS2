// interface
import { IDownloadOAuth2 } from './download-oauth2.interface';
import { DownloadOauth2 } from './download-oauth2.class';

import ITranscludeFunction = angular.ITranscludeFunction;
import IDirectivePrePost = angular.IDirectivePrePost;

/**
 * Download directive
 *
 * Note: will display `fa-floppy-o` icon, unless there is text inside the element, which remove the icon
 */
/** @ngInject */
export function downloadOauth2(): angular.IDirective {

  return {
    restrict: 'E',
    templateUrl: 'app/components/directives/download-oauth2/download-oauth2.html',
    scope: {
      bodyParams: '<?', // body parameters
      bodyParamsFn: '=?', // body parameters as a function, takes precedence over bodyParams
      url: '@',
      params: '=?',
      disable: '<?',
      error: '=',
      classUsed: '@?',
      requestMethod: '@?', // http method, defaults to GET
      callbackSuccessFn: '&?', // if specified, runs after successful response
      callbackErrorFn: '&?', // if specified, runs after unsuccessful response
      hideIcon: '<?' // force hiding of default save icon
    },
    replace: true,
    controller: DownloadOAuth2Controller,
    transclude: true,
    compile: (elem: any, attrs: ng.IAttributes, transcludeFn: ITranscludeFunction): IDirectivePrePost => () => {
      transcludeFn(elem, (clone: ng.IAugmentedJQuery) => {
        if (clone.text().trim().length) {
          elem.find('i').remove(); // remove the icon to just have text
        }
      });
    }
  };
}

/** @ngInject */
export class DownloadOAuth2Controller {

  constructor($scope: IDownloadOAuth2, Restangular: restangular.IService) {
    const downloadOAuth2 = new DownloadOauth2($scope, Restangular);

    $scope.requestMethod = $scope.requestMethod || 'GET';
    $scope.classUsed = $scope.classUsed === undefined ? 'btn btn-primary btn-sm btn-download' : $scope.classUsed;

    $scope.generate = () => downloadOAuth2.generate();
  }
}
