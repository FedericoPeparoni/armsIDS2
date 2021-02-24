import { ScAircraftRegistrationController } from './sc-aircraft-registration.controller';

describe('controller ScAircraftRegistrationController', () => {

  let scAircraftRegistrationController: ScAircraftRegistrationController;
  let scope;

  beforeEach(angular.mock.module('armsWeb'));

  beforeEach(inject(($controller: angular.IControllerService, $rootScope: angular.IRootScopeService) => {
    scope = $rootScope.$new();
    scAircraftRegistrationController = $controller('ScAircraftRegistrationController', {$scope: scope});
  }));

  it('should be registered', inject(() => {
    expect(scAircraftRegistrationController).not.toEqual(null);
  }));

});
