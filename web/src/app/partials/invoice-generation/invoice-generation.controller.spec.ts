import { InvoiceGenerationController } from './invoice-generation.controller';

describe('controller InvoiceGenerationController', () => {

  let invoiceGenerationController: InvoiceGenerationController;
  let scope;

  beforeEach(angular.mock.module('armsWeb'));

  beforeEach(inject(($controller: angular.IControllerService, $rootScope: angular.IRootScopeService) => {
    scope = $rootScope.$new();
    invoiceGenerationController = $controller('InvoiceGenerationController', {$scope: scope});
  }));

  it('should be registered', inject(() => {
    expect(invoiceGenerationController).not.toEqual(null);
  }));

});
