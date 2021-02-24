import { PasswordChangeController } from './password-change.controller';

describe('controller PasswordChangeController', () => {

  let passwordChangeController: PasswordChangeController;
  let scope;

  beforeEach(angular.mock.module('armsWeb'));

  beforeEach(inject(($controller: angular.IControllerService, $rootScope: angular.IRootScopeService) => {
    scope = $rootScope.$new();
    passwordChangeController = $controller('PasswordChangeController', {$scope: scope});
  }));

  it('should be registered', inject(() => {
    expect(passwordChangeController).not.toEqual(null);
  }));

});
