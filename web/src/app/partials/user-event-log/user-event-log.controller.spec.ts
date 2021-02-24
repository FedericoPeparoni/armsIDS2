import { UserEventLogController } from './user-event-log.controller';

describe('controller UserEventLogController', () => {

  let userEventLogController: UserEventLogController;
  let scope;

  beforeEach(angular.mock.module('armsWeb'));

  beforeEach(inject(($controller: angular.IControllerService, $rootScope: angular.IRootScopeService) => {
    scope = $rootScope.$new();
    userEventLogController = $controller('UserEventLogController', {$scope: scope});
  }));

  it('should be registered', inject(() => {
    expect(userEventLogController).not.toEqual(null);
  }));

});
