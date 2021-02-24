import { ScInvoicesController } from './sc-invoices.controller';

describe('controller ScInvoicesController', () => {

  let scInvoicesController: ScInvoicesController;
  let scope;

  beforeEach(angular.mock.module('armsWeb'));

  beforeEach(inject(($controller: angular.IControllerService, $rootScope: angular.IRootScopeService) => {
    scope = $rootScope.$new();
    scInvoicesController = $controller('ScInvoicesController', {$scope: scope});
  }));

  it('should be registered', inject(() => {
    expect(scInvoicesController).not.toEqual(null);
  }));

});
