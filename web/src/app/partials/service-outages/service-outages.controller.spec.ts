import { ServiceOutagesController } from './service-outages.controller';

describe('controller ServiceOutagesController', () => {

  let serviceOutagesController: ServiceOutagesController;
  let scope;

  beforeEach(angular.mock.module('armsWeb'));

  beforeEach(inject(($controller: angular.IControllerService, $rootScope: angular.IRootScopeService) => {
    scope = $rootScope.$new();
    serviceOutagesController = $controller('ServiceOutagesController', {$scope: scope});
  }));

  it('should be registered', inject(() => {
    expect(serviceOutagesController).not.toEqual(null);
  }));

});
