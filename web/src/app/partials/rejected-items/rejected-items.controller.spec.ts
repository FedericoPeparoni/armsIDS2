import { RejectedItemsController } from './rejected-items.controller';

describe('controller RejectedItemsController', () => {

  let rejectedItemsController: RejectedItemsController;
  let scope;

  beforeEach(angular.mock.module('armsWeb'));

  beforeEach(inject(($controller: angular.IControllerService, $rootScope: angular.IRootScopeService) => {
    scope = $rootScope.$new();
    rejectedItemsController = $controller('RejectedItemsController', {$scope: scope});
  }));

  it('should be registered', inject(() => {
    expect(rejectedItemsController).not.toEqual(null);
  }));
});
