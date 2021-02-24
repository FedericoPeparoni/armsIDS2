import { CatalogueServiceChargeController } from './catalogue-service-charge.controller';

describe('controller CatalogueServiceChargeController', () => {

  let catalogueServiceChargeController: CatalogueServiceChargeController;
  let scope;

  beforeEach(angular.mock.module('armsWeb'));

  beforeEach(inject(($controller: angular.IControllerService, $rootScope: angular.IRootScopeService) => {
    scope = $rootScope.$new();
    catalogueServiceChargeController = $controller('CatalogueServiceChargeController', {$scope: scope});
  }));

  it('should be registered', inject(() => {
    expect(catalogueServiceChargeController).not.toEqual(null);
  }));

});
