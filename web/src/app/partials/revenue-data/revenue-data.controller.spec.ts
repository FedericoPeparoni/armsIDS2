import { RevenueDataController } from './revenue-data.controller';

describe('controller RevenueDataController', () => {

  let revenueDataController: RevenueDataController;
  let scope;

  beforeEach(angular.mock.module('armsWeb'));

  beforeEach(inject(($controller: angular.IControllerService, $rootScope: angular.IRootScopeService) => {
    scope = $rootScope.$new();
    revenueDataController = $controller('RevenueDataController', {$scope: scope});
  }));

  it('should be registered', inject(() => {
    expect(revenueDataController).not.toEqual(null);
  }));

});
