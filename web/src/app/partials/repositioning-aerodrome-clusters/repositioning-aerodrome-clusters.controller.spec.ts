import { RepositioningAerodromeClustersController } from './repositioning-aerodrome-clusters.controller';

describe('controller RepositioningAerodromeClustersController', () => {

  let repositioningAerodromeClustersController: RepositioningAerodromeClustersController;
  let scope;

  beforeEach(angular.mock.module('armsWeb'));

  beforeEach(inject(($controller: angular.IControllerService, $rootScope: angular.IRootScopeService) => {
    scope = $rootScope.$new();
    repositioningAerodromeClustersController = $controller('RepositioningAerodromeClustersController', {$scope: scope});
  }));

  it('should be registered', inject(() => {
    expect(repositioningAerodromeClustersController).not.toEqual(null);
  }));

});
