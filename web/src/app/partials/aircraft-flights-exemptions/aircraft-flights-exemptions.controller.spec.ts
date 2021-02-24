import { AircraftFlightsExemptionsController } from './aircraft-flights-exemptions.controller';

describe('controller AircraftFlightsExemptionsController', () => {

  let aircraftFlightsExemptionsController: AircraftFlightsExemptionsController;
  let scope;

  beforeEach(angular.mock.module('armsWeb'));

  beforeEach(inject(($controller: angular.IControllerService, $rootScope: angular.IRootScopeService) => {
    scope = $rootScope.$new();
    aircraftFlightsExemptionsController = $controller('AircraftFlightsExemptionsController', {$scope: scope});
  }));

  it('should be registered', inject(() => {
    expect(aircraftFlightsExemptionsController).not.toEqual(null);
  }));

});
