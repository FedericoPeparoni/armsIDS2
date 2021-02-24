import { BillingCentreManagementController } from './billing-centre-management.controller';

describe('controller BillingCentreManagementController', () => {

  let billingCentreManagementController: BillingCentreManagementController;
  let scope;

  beforeEach(angular.mock.module('armsWeb'));

  beforeEach(inject(($controller: angular.IControllerService, $rootScope: angular.IRootScopeService) => {
    scope = $rootScope.$new();
    billingCentreManagementController = $controller('BillingCentreManagementController', {$scope: scope});
  }));

  it('should be registered', inject(() => {
    expect(billingCentreManagementController).not.toEqual(null);
  }));

});
