import { AircraftTypeManagementController } from './aircraft-type-management.controller';

describe('controller AircraftTypeManagementController', () => {

  let aircraftTypeManagementController: AircraftTypeManagementController;
  let scope;

  beforeEach(angular.mock.module('armsWeb'));

  beforeEach(inject(($controller: angular.IControllerService, $rootScope: angular.IRootScopeService) => {
    scope = $rootScope.$new();
    aircraftTypeManagementController = $controller('AircraftTypeManagementController', {$scope: scope});
  }));

  it('should be registered', inject(() => {
    expect(aircraftTypeManagementController).not.toEqual(null);
  }));

});
