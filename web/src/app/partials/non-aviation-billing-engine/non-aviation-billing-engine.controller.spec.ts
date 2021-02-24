import { NonAviationBillingEngineController } from './non-aviation-billing-engine.controller';

describe('controller NonAviationBillingEngineController', () => {

  let nonAviationBillingEngineController: NonAviationBillingEngineController;
  let scope;

  beforeEach(angular.mock.module('armsWeb'));

  beforeEach(inject(($controller: angular.IControllerService, $rootScope: angular.IRootScopeService) => {
    scope = $rootScope.$new();
    nonAviationBillingEngineController = $controller('NonAviationBillingEngineController', {$scope: scope});
  }));

  it('should be registered', inject(() => {
    expect(nonAviationBillingEngineController).not.toEqual(null);
  }));

  // model defaults
  describe('model defaults', () => {

    it('should fill the default month to the previous month', () => {
      let today = new Date();
      let month = today.getMonth();
      expect(nonAviationBillingEngineController.$scope.dateObject.getMonth()).toEqual(month - 1 >= 0 ? month - 1 : 11);
    });

    it('should not have any account selected by default', () => {
      expect(nonAviationBillingEngineController.$scope.editable.accountId).toBeNull();
    });

    it('should be the year corresponding with the previous month', () => {
      let today = new Date();
      today.setMonth(today.getMonth() - 1);
      expect(nonAviationBillingEngineController.$scope.dateObject.getFullYear()).toEqual(today.getFullYear());
    });
  });

});
