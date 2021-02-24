import { UtilitiesTownsController } from './utilities-towns.controller';

describe('controller UtilitiesTownsController', () => {

  let utilitiesTownsController: UtilitiesTownsController;
  let scope;

  beforeEach(angular.mock.module('armsWeb'));

  beforeEach(inject(($controller: angular.IControllerService, $rootScope: angular.IRootScopeService) => {
    scope = $rootScope.$new();
    utilitiesTownsController = $controller('UtilitiesTownsController', {$scope: scope});
  }));

  it('should be registered', inject(() => {
    expect(utilitiesTownsController).not.toEqual(null);
  }));

});
