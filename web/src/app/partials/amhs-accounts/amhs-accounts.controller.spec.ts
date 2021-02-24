import { AMHSAccountsController } from './amhs-accounts.controller';

describe('controller AMHSAccountsController', () => {

  let amhsAccountsController: AMHSAccountsController;
  let scope;

  beforeEach(angular.mock.module('armsWeb'));

  beforeEach(inject(($controller: angular.IControllerService, $rootScope: angular.IRootScopeService) => {
    scope = $rootScope.$new();
    amhsAccountsController = $controller('AMHSAccountsController', {$scope: scope});
  }));

  it('should be registered', inject(() => {
    expect(amhsAccountsController).not.toEqual(null);
  }));

});
