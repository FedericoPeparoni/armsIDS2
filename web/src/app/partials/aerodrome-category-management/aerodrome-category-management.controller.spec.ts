import { AerodromeCategoryManagementController } from './aerodrome-category-management.controller';

describe('controller AerodromeCategoryManagementController', () => {

  let aerodromeCategoryManagementController: AerodromeCategoryManagementController;
  let scope;

  beforeEach(angular.mock.module('armsWeb'));

  beforeEach(inject(($controller: angular.IControllerService, $rootScope: angular.IRootScopeService) => {
    scope = $rootScope.$new();
    aerodromeCategoryManagementController = $controller('AerodromeCategoryManagementController', {$scope: scope});
  }));

  it('should be registered', inject(() => {
    expect(aerodromeCategoryManagementController).not.toEqual(null);
  }));

});
