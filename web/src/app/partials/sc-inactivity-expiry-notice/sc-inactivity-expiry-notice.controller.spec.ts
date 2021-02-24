import { ScInactivityExpiryNoticeController } from './sc-inactivity-expiry-notice.controller';

describe('controller ScInactivityExpiryNoticeController', () => {

  let scInactivityExpiryNoticeController: ScInactivityExpiryNoticeController;
  let scope;

  beforeEach(angular.mock.module('armsWeb'));

  beforeEach(inject(($controller: angular.IControllerService, $rootScope: angular.IRootScopeService) => {
    scope = $rootScope.$new();
    scInactivityExpiryNoticeController = $controller('ScInactivityExpiryNoticeController', {$scope: scope});
  }));

  it('should be registered', inject(() => {
    expect(scInactivityExpiryNoticeController).not.toEqual(null);
  }));

});
