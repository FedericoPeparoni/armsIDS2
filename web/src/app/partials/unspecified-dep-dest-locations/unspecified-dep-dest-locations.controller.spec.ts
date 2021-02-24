import { UnspecifiedDepDestLocationsController } from './unspecified-dep-dest-locations.controller';

describe('controller UnspecifiedDepDestLocationsController', () => {

  let unspecifiedDepDestLocationsController: UnspecifiedDepDestLocationsController;
  let scope;

  beforeEach(angular.mock.module('armsWeb'));

  beforeEach(inject(($controller: angular.IControllerService, $rootScope: angular.IRootScopeService) => {
    scope = $rootScope.$new();
    unspecifiedDepDestLocationsController = $controller('UnspecifiedDepDestLocationsController', {$scope: scope});
  }));

  it('should be registered', inject(() => {
    expect(unspecifiedDepDestLocationsController).not.toEqual(null);
  }));

});
