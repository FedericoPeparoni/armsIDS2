import { PluginsController } from './plugins.controller';

describe('controller PluginsController', () => {

  let pluginsController: PluginsController;
  let scope;

  beforeEach(angular.mock.module('armsWeb'));

  beforeEach(inject(($controller: angular.IControllerService, $rootScope: angular.IRootScopeService) => {
    scope = $rootScope.$new();
    pluginsController = $controller('PluginsController', {$scope: scope});
  }));

  it('should be registered', inject(() => {
    expect(pluginsController).not.toEqual(null);
  }));

});
