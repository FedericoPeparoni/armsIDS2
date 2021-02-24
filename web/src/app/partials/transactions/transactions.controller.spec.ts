import { TransactionsController } from './transactions.controller';

describe('controller TransactionsController', () => {

  let transactionsController: TransactionsController;
  let scope;

  beforeEach(angular.mock.module('armsWeb'));

  beforeEach(inject(($controller: angular.IControllerService, $rootScope: angular.IRootScopeService) => {
    scope = $rootScope.$new();
    transactionsController = $controller('TransactionsController', {$scope: scope});
  }));

  it('should be registered', inject(() => {
    expect(transactionsController).not.toEqual(null);
  }));

});
