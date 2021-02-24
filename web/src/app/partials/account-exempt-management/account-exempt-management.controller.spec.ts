import { AccountExemptManagementController } from './account-exempt-management.controller';

describe('controller AccountExemptManagementController', () => {

  let accountExemptManagementController: AccountExemptManagementController;
  let scope;

  beforeEach(angular.mock.module('armsWeb'));

  beforeEach(inject(($controller: angular.IControllerService, $rootScope: angular.IRootScopeService) => {
    scope = $rootScope.$new();
    accountExemptManagementController = $controller('AccountExemptManagementController', {$scope: scope});
  }));

  it('should be registered', inject(() => {
    expect(accountExemptManagementController).not.toEqual(null);
  }));

});
