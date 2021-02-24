import { ScLoginController } from './sc-login.controller';

describe('controller ScLoginController', () => {

  let scLoginController: ScLoginController;
  let scope;

  beforeEach(angular.mock.module('armsWeb'));

  beforeEach(inject(($controller: angular.IControllerService, $rootScope: angular.IRootScopeService) => {
    scope = $rootScope.$new();
    scLoginController = $controller('ScLoginController', {$scope: scope});
  }));

  it('should be registered', inject(() => {
    expect(scLoginController).not.toEqual(null);
  }));

});
