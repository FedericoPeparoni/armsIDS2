import { AccountsController } from './accounts.controller';

describe('controller annual tonne', () => {

  let accountsController: AccountsController;
  let scope;

  beforeEach(angular.mock.module('armsWeb'));

  beforeEach(inject(($controller: angular.IControllerService, $rootScope: angular.IRootScopeService) => {
    scope = $rootScope.$new();
    accountsController = $controller('AccountsController', {$scope: scope});
  }));

  it('should be registered', inject(() => {
    expect(accountsController).not.toEqual(null);
  }));

});

