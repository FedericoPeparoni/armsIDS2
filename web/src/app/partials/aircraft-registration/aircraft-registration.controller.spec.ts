import { AircraftRegistrationController } from './aircraft-registration.controller';

describe('controller AircraftRegistrationController', () => {

  let aircraftRegistrationController: AircraftRegistrationController;
  let scope;

  beforeEach(angular.mock.module('armsWeb'));

  beforeEach(inject(($controller: angular.IControllerService, $rootScope: angular.IRootScopeService) => {
    scope = $rootScope.$new();
    aircraftRegistrationController = $controller('AircraftRegistrationController', {$scope: scope});
  }));

  it('should be registered', inject(() => {
    expect(AircraftRegistrationController).not.toEqual(null);
  }));

});

