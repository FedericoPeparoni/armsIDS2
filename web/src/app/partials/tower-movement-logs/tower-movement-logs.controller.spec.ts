import { TowerMovementLogsController } from './tower-movement-logs.controller';

describe('controller TowerMovementLogsController', () => {

  let towerMovementLogsController: TowerMovementLogsController;
  let scope;

  beforeEach(angular.mock.module('armsWeb'));

  beforeEach(inject(($controller: angular.IControllerService, $rootScope: angular.IRootScopeService) => {
    scope = $rootScope.$new();
    towerMovementLogsController = $controller('TowerMovementLogsController', {$scope: scope});
  }));

  it('should be registered', inject(() => {
    expect(towerMovementLogsController).not.toEqual(null);
  }));

});
