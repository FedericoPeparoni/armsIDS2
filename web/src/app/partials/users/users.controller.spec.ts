import { UsersController } from './users.controller';

describe('controller UsersController', () => {

  let usersController: UsersController,
    scope;

  beforeEach(angular.mock.module('armsWeb'));

  beforeEach(inject(($controller: angular.IControllerService, $rootScope: angular.IRootScopeService) => {
    scope = $rootScope.$new();
    usersController = $controller('UsersController', {$scope: scope});
  }));

  it('should be registered', inject(() => {
    expect(usersController).not.toEqual(null);
  }));

});
