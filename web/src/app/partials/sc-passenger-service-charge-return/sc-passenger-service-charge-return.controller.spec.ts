import { ScPassengerServiceChargeReturnController } from './sc-passenger-service-charge-return.controller';

describe('controller ScPassengerServiceChargeReturnController', () => {

  let passengerServiceChargeReturnController: ScPassengerServiceChargeReturnController;
  let scope;

  beforeEach(angular.mock.module('armsWeb'));

  beforeEach(inject(($controller: angular.IControllerService, $rootScope: angular.IRootScopeService) => {
    scope = $rootScope.$new();
    passengerServiceChargeReturnController = $controller('PassengerServiceChargeReturnController', {$scope: scope});
  }));

  it('should be registered', inject(() => {
    expect(passengerServiceChargeReturnController).not.toEqual(null);
  }));

});
