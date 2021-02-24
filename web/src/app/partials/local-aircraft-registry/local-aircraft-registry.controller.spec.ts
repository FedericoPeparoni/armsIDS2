import { LocalAircraftRegistryController } from './local-aircraft-registry.controller';

describe('controller LocalAircraftRegistryController', () => {

  let localAircraftRegistryController: LocalAircraftRegistryController;
  let scope;

  beforeEach(angular.mock.module('armsWeb'));

  beforeEach(inject(($controller: angular.IControllerService, $rootScope: angular.IRootScopeService) => {
    scope = $rootScope.$new();
    localAircraftRegistryController = $controller('LocalAircraftRegistryController', {$scope: scope});
  }));

  it('should be registered', inject(() => {
    expect(localAircraftRegistryController).not.toEqual(null);
  }));

});
