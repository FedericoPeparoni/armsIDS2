import { CachedEventsController } from './cached-events.controller';

describe('controller CachedEventsController', () => {

  let cachedEventsController: CachedEventsController;
  let scope;

  beforeEach(angular.mock.module('armsWeb'));

  beforeEach(inject(($controller: angular.IControllerService, $rootScope: angular.IRootScopeService) => {
    scope = $rootScope.$new();
    cachedEventsController = $controller('CachedEventsController', {$scope: scope});
  }));

  it('should be registered', inject(() => {
    expect(cachedEventsController).not.toEqual(null);
  }));

});
