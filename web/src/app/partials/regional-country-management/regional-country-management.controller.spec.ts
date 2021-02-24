import { RegionalCountryManagementController } from './regional-country-management.controller';

describe('controller RegionalCountryManagementController', () => {

  let regionalCountryManagementController: RegionalCountryManagementController;
  let scope;

  beforeEach(angular.mock.module('armsWeb'));

  beforeEach(inject(($controller: angular.IControllerService, $rootScope: angular.IRootScopeService) => {
    scope = $rootScope.$new();
    regionalCountryManagementController = $controller('RegionalCountryManagementController', {$scope: scope});
  }));

  it('should be registered', inject(() => {
    expect(regionalCountryManagementController).not.toEqual(null);
  }));

});
