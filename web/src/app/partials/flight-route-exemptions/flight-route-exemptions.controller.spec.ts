import { FlightRouteExemptionsController } from './flight-route-exemptions.controller';

describe('controller FlightRouteExemptionsController', () => {

  let flightRouteExemptionsController: FlightRouteExemptionsController;
  let scope;

  beforeEach(angular.mock.module('armsWeb'));

  beforeEach(inject(($controller: angular.IControllerService, $rootScope: angular.IRootScopeService) => {
    scope = $rootScope.$new();
    flightRouteExemptionsController = $controller('FlightRouteExemptionsController', {$scope: scope});
  }));

  it('should be registered', inject(() => {
    expect(flightRouteExemptionsController).not.toEqual(null);
  }));

});
