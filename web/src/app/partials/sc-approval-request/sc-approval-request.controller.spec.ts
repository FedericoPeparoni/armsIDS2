import { ScApprovalRequestController } from './sc-approval-request.controller';

describe('controller ScApprovalRequestController', () => {

  let scApprovalRequestController: ScApprovalRequestController;
  let scope;

  beforeEach(angular.mock.module('armsWeb'));

  beforeEach(inject(($controller: angular.IControllerService, $rootScope: angular.IRootScopeService) => {
    scope = $rootScope.$new();
    scApprovalRequestController = $controller('ScApprovalRequestController', {$scope: scope});
  }));

  it('should be registered', inject(() => {
    expect(scApprovalRequestController).not.toEqual(null);
  }));

});
