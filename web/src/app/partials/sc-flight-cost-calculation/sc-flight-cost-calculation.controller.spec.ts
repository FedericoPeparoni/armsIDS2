import { ScFlightCostCalculationController } from './sc-flight-cost-calculation.controller';

describe('controller ScFlightCostCalculationController', () => {

  let scFlightCostCalculationController: ScFlightCostCalculationController;
  let scope;

  beforeEach(angular.mock.module('armsWeb'));

  beforeEach(inject(($controller: angular.IControllerService, $rootScope: angular.IRootScopeService) => {
    scope = $rootScope.$new();
    scFlightCostCalculationController = $controller('ScFlightCostCalculationController', {$scope: scope});
  }));

  it('should be registered', inject(() => {
    expect(scFlightCostCalculationController).not.toEqual(null);
  }));

});
