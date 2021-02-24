import { ScReportGenerationController } from './sc-report-generation.controller';

describe('controller ScReportGenerationController', () => {

  let scReportGenerationController: ScReportGenerationController;
  let scope;

  beforeEach(angular.mock.module('armsWeb'));

  beforeEach(inject(($controller: angular.IControllerService, $rootScope: angular.IRootScopeService) => {
    scope = $rootScope.$new();
    scReportGenerationController = $controller('ScReportGenerationController', {$scope: scope});
  }));

  it('should be registered', inject(() => {
    expect(scReportGenerationController).not.toEqual(null);
  }));

});
