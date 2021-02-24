import { GroupManagementController } from './group-management.controller';

describe('controller GroupManagementController', () => {

  let groupManagementController: GroupManagementController, scope;

  beforeEach(angular.mock.module('armsWeb'));

  beforeEach(inject(($controller: angular.IControllerService, $rootScope: angular.IRootScopeService) => {

    scope = $rootScope.$new();

    groupManagementController = $controller('GroupManagementController', {$scope: scope});

  }));
  it('should be registered', inject(() => {

    expect(groupManagementController).not.toEqual(null);

  }));
});
