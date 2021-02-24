import { ScUserRegistrationController } from './sc-user-registration.controller';

describe('controller ScUserRegistrationController', () => {

  let scUserRegistrationController: ScUserRegistrationController;
  let scope;

  beforeEach(angular.mock.module('armsWeb'));

  beforeEach(inject(($controller: angular.IControllerService, $rootScope: angular.IRootScopeService) => {
    scope = $rootScope.$new();
    scUserRegistrationController = $controller('ScUserRegistrationController', {$scope: scope});
  }));

  it('should be registered', inject(() => {
    expect(scUserRegistrationController).not.toEqual(null);
  }));

});
