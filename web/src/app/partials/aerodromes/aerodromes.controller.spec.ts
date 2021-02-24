import { AerodromesController } from './aerodromes.controller';

describe('controller AerodromesController', () => {

  let aerodromesController: AerodromesController;
  let scope;

  beforeEach(angular.mock.module('armsWeb'));

  beforeEach(inject(($controller: angular.IControllerService, $rootScope: angular.IRootScopeService) => {
    scope = $rootScope.$new();
    aerodromesController = $controller('AerodromesController', {$scope: scope});
  }));

  it('should be registered', inject(() => {
    expect(aerodromesController).not.toEqual(null);
  }));

});
