/** @ngInject */
export function uploadFileFormat(): angular.IDirective {

  return {
    restrict: 'E',
    controller: UploadFileFormatController,
    controllerAs: 'UploadFileFormatController',
    templateUrl: 'app/angular-ids-project/src/components/uploadFileFormat/uploadFileFormat.html',
    replace: true,
    scope: {
      template: '@',
      mtow: '@',
      format: '@'
    }
  };

}

/** @ngInject */
export class UploadFileFormatController {
  public constructor(private $scope: ng.IScope, private $uibModal: any) {
    const mtow = $scope.mtow;
    const format = $scope.format;
    $scope.showFileFormat = () => {
      const modal = $uibModal.open({
        templateUrl: $scope.template,
        controller: ['$scope', ($scope: ng.IScope) => {
          $scope.mtow = mtow;
          $scope.format = format;
          $scope.confirm = () => {
            modal.close();
          };
        }]
      });
    };
  }
}


