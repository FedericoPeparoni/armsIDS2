export class ScHomePageController  {

  /* @ngInject */
  constructor(protected $scope: ng.IScope) {
    $scope.loginFromBilling = JSON.parse(window.localStorage.getItem('loginFromBilling'));
  }
}
