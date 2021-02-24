/** @ngInject */
export function summaryUpload(): angular.IDirective {
    return {
        restrict: 'E',
        templateUrl: 'app/components/directives/summary-upload/summary-upload.directive.html',
        scope: {
            uploadJob: '='
        }
    };
  }
