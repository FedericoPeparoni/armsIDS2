import { ScAccountManagementController } from './sc-account-management.controller';

describe('controller ScAccountManagementController', () => {

  let scAccountManagementController: ScAccountManagementController;
  let scope;

  beforeEach(angular.mock.module('armsWeb'));

  beforeEach(inject(($controller: angular.IControllerService, $rootScope: angular.IRootScopeService) => {
    scope = $rootScope.$new();
    scAccountManagementController = $controller('ScAccountManagementController', {$scope: scope});
  }));

  it('should be registered', inject(() => {
    expect(scAccountManagementController).not.toEqual(null);
  }));

});
