import { NominalRoutesController } from './nominal-routes.controller';

describe('controller NominalRoutesController', () => {

  let nominalRoutesController: NominalRoutesController;
  let scope;

  beforeEach(angular.mock.module('armsWeb'));

  beforeEach(inject(($controller: angular.IControllerService, $rootScope: angular.IRootScopeService) => {
    scope = $rootScope.$new();
    nominalRoutesController = $controller('NominalRoutesController', {$scope: scope});
  }));

  it('should be registered', inject(() => {
    expect(nominalRoutesController).not.toEqual(null);
  }));

});
