import { ApplicationManagementController } from './application-management.controller';

describe('controller ApplicationManagementController', () => {

  let applicationManagementController: ApplicationManagementController;
  let scope;

  beforeEach(angular.mock.module('armsWeb'));

  beforeEach(inject(($controller: angular.IControllerService, $rootScope: angular.IRootScopeService) => {
    scope = $rootScope.$new();
    applicationManagementController = $controller('ApplicationManagementController', {$scope: scope});
  }));

  it('should be registered', inject(() => {
    expect(applicationManagementController).not.toEqual(null);
  }));

});
