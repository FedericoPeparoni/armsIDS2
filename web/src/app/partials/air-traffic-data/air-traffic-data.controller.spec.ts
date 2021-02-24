import { AirTrafficDataController } from './air-traffic-data.controller';

describe('controller AirTrafficDataController', () => {

  let airTrafficDataController: AirTrafficDataController;
  let scope;

  beforeEach(angular.mock.module('armsWeb'));

  beforeEach(inject(($controller: angular.IControllerService, $rootScope: angular.IRootScopeService) => {
    scope = $rootScope.$new();
    airTrafficDataController = $controller('AirTrafficDataController', {$scope: scope});
  }));

  it('should be registered', inject(() => {
    expect(airTrafficDataController).not.toEqual(null);
  }));

});
