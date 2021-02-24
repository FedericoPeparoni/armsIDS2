import { ScFlightSchedulesController } from './sc-flight-schedules.controller';

describe('controller ScFlightSchedulesController', () => {

  let scFlightSchedulesController: ScFlightSchedulesController;
  let scope;

  beforeEach(angular.mock.module('armsWeb'));

  beforeEach(inject(($controller: angular.IControllerService, $rootScope: angular.IRootScopeService) => {
    scope = $rootScope.$new();
    scFlightSchedulesController = $controller('ScFlightSchedulesController', {$scope: scope});
  }));

  it('should be registered', inject(() => {
    expect(scFlightSchedulesController).not.toEqual(null);
  }));

});
