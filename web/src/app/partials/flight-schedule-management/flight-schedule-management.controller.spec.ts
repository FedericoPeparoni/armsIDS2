import { FlightScheduleManagementController } from './flight-schedule-management.controller';

describe('controller FlightScheduleManagementController', () => {

  let flightScheduleManagementController: FlightScheduleManagementController;
  let scope;

  beforeEach(angular.mock.module('armsWeb'));

  beforeEach(inject(($controller: angular.IControllerService, $rootScope: angular.IRootScopeService) => {
    scope = $rootScope.$new();
    flightScheduleManagementController = $controller('FlightScheduleManagementController', {$scope: scope});
  }));

  it('should be registered', inject(() => {
    expect(flightScheduleManagementController).not.toEqual(null);
  }));

});
