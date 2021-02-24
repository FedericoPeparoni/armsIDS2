import { AirspaceManagementController } from './airspace-management.controller';

describe('controller AirspaceManagementController', () => {

  let airspaceManagementController: AirspaceManagementController;
  let scope;

  beforeEach(angular.mock.module('armsWeb'));

  beforeEach(inject(($controller: angular.IControllerService, $rootScope: angular.IRootScopeService) => {
    scope = $rootScope.$new();
    airspaceManagementController = $controller('AirspaceManagementController', { $scope: scope });
  }));

  it('should be registered', inject(() => {
    expect(airspaceManagementController).not.toEqual(null);
  }));

});
