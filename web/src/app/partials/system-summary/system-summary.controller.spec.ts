import { SystemSummaryController } from './system-summary.controller';

describe('controller SystemSummaryController', () => {

  let systemSummaryController: SystemSummaryController;
  let scope;

  beforeEach(angular.mock.module('armsWeb'));

  beforeEach(inject(($controller: angular.IControllerService, $rootScope: angular.IRootScopeService) => {
    scope = $rootScope.$new();
    systemSummaryController = $controller('SystemSummaryController', {$scope: scope});
  }));

  it('should be registered', inject(() => {
    expect(systemSummaryController).not.toEqual(null);
  }));

});
