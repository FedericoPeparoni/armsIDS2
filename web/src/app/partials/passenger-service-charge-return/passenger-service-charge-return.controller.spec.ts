import { PassengerServiceChargeReturnController } from './passenger-service-charge-return.controller';

describe('controller PassengerServiceChargeReturnController', () => {

  let passengerServiceChargeReturnController: PassengerServiceChargeReturnController;
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
