import { FlightStatusExemptionsController } from './flight-status-exemptions.controller';

describe('controller FlightStatusExemptionsController', () => {

  let flightStatusExemptionsController: FlightStatusExemptionsController;
  let scope;

  beforeEach(angular.mock.module('armsWeb'));

  beforeEach(inject(($controller: angular.IControllerService, $rootScope: angular.IRootScopeService) => {
    scope = $rootScope.$new();
    flightStatusExemptionsController = $controller('FlightStatusExemptionsController', {$scope: scope});
  }));

  it('should be registered', inject(() => {
    expect(flightStatusExemptionsController).not.toEqual(null);
  }));

});
