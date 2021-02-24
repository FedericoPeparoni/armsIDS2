import { AircraftExemptManagementController } from './aircraft-exempt-management.controller';

describe('controller AircraftExemptManagementController', () => {

  let aircraftExemptManagementController: AircraftExemptManagementController;
  let scope;

  beforeEach(angular.mock.module('armsWeb'));

  beforeEach(inject(($controller: angular.IControllerService, $rootScope: angular.IRootScopeService) => {
    scope = $rootScope.$new();
    aircraftExemptManagementController = $controller('AircraftExemptManagementController', {$scope: scope});
  }));

  it('should be registered', inject(() => {
    expect(aircraftExemptManagementController).not.toEqual(null);
  }));

});
