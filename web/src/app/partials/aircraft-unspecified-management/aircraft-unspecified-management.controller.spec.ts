import { AircraftUnspecifiedManagementController } from './aircraft-unspecified-management.controller';

describe('controller AircraftUnspecifiedManagementController', () => {

  let aircraftUnspecifiedManagementController: AircraftUnspecifiedManagementController;
  let scope;

  beforeEach(angular.mock.module('armsWeb'));

  beforeEach(inject(($controller: angular.IControllerService, $rootScope: angular.IRootScopeService) => {
    scope = $rootScope.$new();
    aircraftUnspecifiedManagementController = $controller('AircraftUnspecifiedManagementController', {$scope: scope});
  }));

  it('should be registered', inject(() => {
    expect(aircraftUnspecifiedManagementController).not.toEqual(null);
  }));

});
