import { ReportTemplatesController } from './report-templates.controller';

describe('controller ReportTemplatesController', () => {

  let reportTemplatesController: ReportTemplatesController;
  let scope;

  beforeEach(angular.mock.module('armsWeb'));

  beforeEach(inject(($controller: angular.IControllerService, $rootScope: angular.IRootScopeService) => {
    scope = $rootScope.$new();
    reportTemplatesController = $controller('ReportTemplatesController', {$scope: scope});
  }));

  it('should be registered', inject(() => {
    expect(reportTemplatesController).not.toEqual(null);
  }));

});
