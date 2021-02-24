import { LoginController } from './login.controller';

describe('controller login', () => {
  let loginController: LoginController;
  let scope;

  beforeEach(angular.mock.module('armsWeb'));

  beforeEach(inject(($controller: angular.IControllerService, $rootScope: angular.IRootScopeService) => {
    scope = $rootScope.$new();
    loginController = $controller('LoginController', {$scope: scope});
  }));

  it('should be registered', inject(() => {
    expect(loginController).not.toEqual(null);
  }));
});
