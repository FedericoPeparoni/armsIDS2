import { InvoicesController } from './invoices.controller';

describe('controller InvoicesController', () => {

  let invoicesController: InvoicesController;
  let scope;

  beforeEach(angular.mock.module('armsWeb'));

  beforeEach(inject(($controller: angular.IControllerService, $rootScope: angular.IRootScopeService) => {
    scope = $rootScope.$new();
    invoicesController = $controller('InvoicesController', {$scope: scope});
  }));

  it('should be registered', inject(() => {
    expect(invoicesController).not.toEqual(null);
  }));

});
