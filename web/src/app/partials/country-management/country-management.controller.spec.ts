import { CountryManagementController } from './country-management.controller';

describe('controller CountryManagementController', () => {

  let countryManagementController: CountryManagementController;
  let scope;

  beforeEach(angular.mock.module('armsWeb'));

  beforeEach(inject(($controller: angular.IControllerService, $rootScope: angular.IRootScopeService) => {
    scope = $rootScope.$new();
    countryManagementController = $controller('CountryManagementController', {$scope: scope});
  }));

  it('should be registered', inject(() => {
    expect(countryManagementController).not.toEqual(null);
  }));

});
