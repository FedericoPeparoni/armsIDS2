import { InvoiceTemplateManagementController } from './invoice-template-management.controller';

describe('controller InvoiceTemplateManagementController', () => {

  let invoiceTemplateManagementController: InvoiceTemplateManagementController;
  let scope;

  beforeEach(angular.mock.module('armsWeb'));

  beforeEach(inject(($controller: angular.IControllerService, $rootScope: angular.IRootScopeService) => {
    scope = $rootScope.$new();
    invoiceTemplateManagementController = $controller('InvoiceTemplateManagementController', {$scope: scope});
  }));

  it('should be registered', inject(() => {
    expect(invoiceTemplateManagementController).not.toEqual(null);
  }));

});
