import { FlightMovementManagementController } from './flight-movement-management.controller';

describe('controller FlightMovementManagementController', () => {

  let flightMovementManagementController: FlightMovementManagementController;
  let scope;

  beforeEach(angular.mock.module('armsWeb'));

  beforeEach(inject(($controller: angular.IControllerService, $rootScope: angular.IRootScopeService) => {
    scope = $rootScope.$new();
    flightMovementManagementController = $controller('FlightMovementManagementController', {$scope: scope});
  }));

  it('should be registered', inject(() => {
    expect(flightMovementManagementController).not.toEqual(null);
  }));

});
