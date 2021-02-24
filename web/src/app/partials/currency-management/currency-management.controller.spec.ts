import { CurrencyManagementController } from './currency-management.controller';

describe('controller CurrencyManagementController', () => {

  let currencyManagementController: CurrencyManagementController;
  let scope;

  beforeEach(angular.mock.module('armsWeb'));

  beforeEach(inject(($controller: angular.IControllerService, $rootScope: angular.IRootScopeService) => {
    scope = $rootScope.$new();
    currencyManagementController = $controller('CurrencyManagementController', {$scope: scope});
  }));

  it('should be registered', inject(() => {
    expect(currencyManagementController).not.toEqual(null);
  }));

});
