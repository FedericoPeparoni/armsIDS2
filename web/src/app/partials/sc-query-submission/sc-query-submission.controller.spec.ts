import { ScQuerySubmissionController } from './sc-query-submission.controller';

describe('controller ScQuerySubmissionController', () => {

  let scQuerySubmissionController: ScQuerySubmissionController;
  let scope;

  beforeEach(angular.mock.module('armsWeb'));

  beforeEach(inject(($controller: angular.IControllerService, $rootScope: angular.IRootScopeService) => {
    scope = $rootScope.$new();
    scQuerySubmissionController = $controller('ScQuerySubmissionController', {$scope: scope});
  }));

  it('should be registered', inject(() => {
    expect(scQuerySubmissionController).not.toEqual(null);
  }));

});
