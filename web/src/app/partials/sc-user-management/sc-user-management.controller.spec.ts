import { ScUserManagementController } from './sc-user-management.controller';

describe('controller ScUserManagementController', () => {

  let scUserManagementController: ScUserManagementController;
  let scope;

  beforeEach(angular.mock.module('armsWeb'));

  beforeEach(inject(($controller: angular.IControllerService, $rootScope: angular.IRootScopeService) => {
    scope = $rootScope.$new();
    scUserManagementController = $controller('ScUserManagementController', {$scope: scope});
  }));

  it('should be registered', inject(() => {
    expect(scUserManagementController).not.toEqual(null);
  }));

});
