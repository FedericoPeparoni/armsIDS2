import {ReportGenerationController} from './report-generation.controller';

describe('controller ReportGenerationController', () => {

  let reportGenerationController: ReportGenerationController;
  let scope;

  beforeEach(angular.mock.module('armsWeb'));

  beforeEach(inject(($controller: angular.IControllerService, $rootScope: angular.IRootScopeService) => {
    scope = $rootScope.$new();
    reportGenerationController = $controller('ReportGenerationController', {$scope: scope});
  }));

  it('should be registered', inject(() => {
    expect(reportGenerationController).not.toEqual(null);
  }));

  it('should be registered', () => {
    reportGenerationController.setDefaultDate();
    expect(reportGenerationController.setDefaultDate).not.toEqual(null);
    expect(reportGenerationController.$scope.start).not.toEqual(null);
    expect(reportGenerationController.$scope.end).not.toEqual(null);
  });
});

