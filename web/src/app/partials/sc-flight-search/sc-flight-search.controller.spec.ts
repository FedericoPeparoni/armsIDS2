import { ScFlightSearchController } from './sc-flight-search.controller';

describe('controller ScFlightSearchController', () => {

  let scFlightSearchController: ScFlightSearchController;
  let scope;

  beforeEach(angular.mock.module('armsWeb'));

  beforeEach(inject(($controller: angular.IControllerService, $rootScope: angular.IRootScopeService) => {
    scope = $rootScope.$new();
    scFlightSearchController = $controller('ScFlightSearchController', {$scope: scope});
  }));

  it('should be registered', inject(() => {
    expect(scFlightSearchController).not.toEqual(null);
  }));

});
