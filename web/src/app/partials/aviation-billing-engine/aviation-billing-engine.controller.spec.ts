import { AviationBillingEngineController } from './aviation-billing-engine.controller';

describe('controller AviationBillingEngineController', () => {

  let aviationBillingEngineController: AviationBillingEngineController;
  let scope;

  beforeEach(angular.mock.module('armsWeb'));

  beforeEach(inject(($controller: angular.IControllerService, $rootScope: angular.IRootScopeService) => {
    scope = $rootScope.$new();
    aviationBillingEngineController = $controller('AviationBillingEngineController', {$scope: scope});
  }));

  it('controller should be registered', inject(() => {
    expect(aviationBillingEngineController).not.toEqual(null);
  }));

  // model defaults
  describe('model defaults', () => {

    it('should fill the default month to the previous month', () => {
      let today = new Date();
      let month = today.getMonth();
      expect(aviationBillingEngineController.$scope.dateObject.getMonth()).toEqual(month - 1 >= 0 ? month - 1 : 11);
    });

    it('should not have any accounts selected by default', () => {
      expect(aviationBillingEngineController.$scope.editable.account_id_list).toBeNull();
    });


    it('should be the year corresponding with the previous month', () => {
      let today = new Date();
      today.setMonth(today.getMonth() - 1);
      expect(aviationBillingEngineController.$scope.dateObject.getFullYear()).toEqual(today.getFullYear());
    });
  });
});
