import { CachedEventsService } from './cached-events.service';

describe('service CachedEventsService', () => {

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

  it('should be registered', inject((cachedEventsService: CachedEventsService) => {
      expect(cachedEventsService).not.toEqual(null);
  }));
});
