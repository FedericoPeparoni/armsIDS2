import { RecurringChargesController } from './recurring-charges.controller';

describe('controller RecurringChargesController', () => {

  let recurringChargesController: RecurringChargesController;
  let scope;

  beforeEach(angular.mock.module('armsWeb'));

  beforeEach(inject(($controller: angular.IControllerService, $rootScope: angular.IRootScopeService) => {
    scope = $rootScope.$new();
    recurringChargesController = $controller('RecurringChargesController', {$scope: scope});
  }));

  it('should be registered', inject(() => {
    expect(recurringChargesController).not.toEqual(null);
  }));

});
