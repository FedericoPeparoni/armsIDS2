import { ScTransactionsController } from './sc-transactions.controller';

describe('controller ScTransactionsController', () => {

  let scTransactionsController: ScTransactionsController;
  let scope;

  beforeEach(angular.mock.module('armsWeb'));

  beforeEach(inject(($controller: angular.IControllerService, $rootScope: angular.IRootScopeService) => {
    scope = $rootScope.$new();
    scTransactionsController = $controller('ScTransactionsController', {$scope: scope});
  }));

  it('should be registered', inject(() => {
    expect(scTransactionsController).not.toEqual(null);
  }));

});
