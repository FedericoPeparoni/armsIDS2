import { EnrouteAirNavigationChargesManagementController } from './enroute-air-navigation-charges-management.controller';

describe('controller EnrouteAirNavigationChargesManagementController', () => {

  let enrouteAirNavigationChargesManagementController: EnrouteAirNavigationChargesManagementController;
  let scope;

  beforeEach(angular.mock.module('armsWeb'));

  beforeEach(inject(($controller: angular.IControllerService, $rootScope: angular.IRootScopeService) => {
    scope = $rootScope.$new();
    enrouteAirNavigationChargesManagementController = $controller('EnrouteAirNavigationChargesManagementController', {$scope: scope});
  }));

  it('should be registered', inject(() => {
    expect(enrouteAirNavigationChargesManagementController).not.toEqual(null);
  }));

});
