import { RadarSummaryController } from './radar-summary.controller';

describe('controller RadarSummaryController', () => {

  let radarSummaryController: RadarSummaryController;
  let scope;

  beforeEach(angular.mock.module('armsWeb'));

  beforeEach(inject(($controller: angular.IControllerService, $rootScope: angular.IRootScopeService) => {
    scope = $rootScope.$new();
    radarSummaryController = $controller('RadarSummaryController', {$scope: scope});
  }));

  it('should be registered', inject(() => {
    expect(radarSummaryController).not.toEqual(null);
  }));

  // model defaults
  describe('model defaults', () => {
    it('pattern should contain comma seperated values extension', () => {
      expect(scope.pattern === '.csv' || scope.pattern === '.txt' );
    });
  });
});
