import { AtcMovementLogController } from './atc-movement-log.controller';

describe('controller AtcMovementLogController', () => {

  let atcMovementLogController: AtcMovementLogController;
  let scope;

  beforeEach(angular.mock.module('armsWeb'));

  beforeEach(inject(($controller: angular.IControllerService, $rootScope: angular.IRootScopeService) => {
    scope = $rootScope.$new();
    atcMovementLogController = $controller('AtcMovementLogController', {$scope: scope});
  }));

  it('should be registered', inject(() => {
    expect(atcMovementLogController).not.toEqual(null);
  }));

});
