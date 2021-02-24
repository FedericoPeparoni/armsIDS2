import { PassengerRevenueReconciliationController } from './passenger-revenue-reconciliation.controller';

describe('controller PassengerRevenueReconciliationController', () => {

  let passengerRevenueReconciliationController: PassengerRevenueReconciliationController;
  let scope;

  beforeEach(angular.mock.module('armsWeb'));

  beforeEach(inject(($controller: angular.IControllerService, $rootScope: angular.IRootScopeService) => {
    scope = $rootScope.$new();
    passengerRevenueReconciliationController = $controller('PassengerRevenueReconciliationController', {$scope: scope});
  }));

  it('controller should be registered', inject(() => {
    expect(passengerRevenueReconciliationController).not.toEqual(null);
  }));
});
