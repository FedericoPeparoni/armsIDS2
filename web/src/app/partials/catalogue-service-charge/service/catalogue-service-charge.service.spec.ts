import { CatalogueServiceChargeService } from './catalogue-service-charge.service';

describe('service CatalogueServiceChargeService', () => {

  let httpBackend,
      restangular;

  beforeEach(angular.mock.module('armsWeb'));

  beforeEach(() => {
      inject(($httpBackend: angular.IHttpBackendService, Restangular: restangular.IService) => {
          httpBackend = $httpBackend;
          restangular = Restangular;

          $httpBackend.when('GET', (url: string): any => {
            if ('http://localhost:8080/api/system-configurations/noauth') {
              return true;
            };
          }).respond('ok');
      });
  });

  it('should be registered', inject((catalogueServiceChargeService: CatalogueServiceChargeService) => {
      expect(catalogueServiceChargeService).not.toEqual(null);
  }));
});
