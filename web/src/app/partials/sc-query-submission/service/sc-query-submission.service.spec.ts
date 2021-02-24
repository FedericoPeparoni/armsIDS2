// interface
import { IScQuerySubmission } from '../sc-query-submission.interface';

// service
import { ScQuerySubmissionService, endpoint } from './sc-query-submission.service';

describe('service ScQuerySubmissionService', () => {

  let httpBackend,
      restangular;

  let query: IScQuerySubmission = {
      sender_email: 'ids@gmail.com',
      subject: 'Hello',
      message: 'Test'
    };

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

  it('should be registered', inject((scQuerySubmissionService: ScQuerySubmissionService) => {
      expect(scQuerySubmissionService).not.toEqual(null);
  }));

  describe('create a query submission', () => {
    it('should create query submission', inject((scQuerySubmissionService: ScQuerySubmissionService) => {

      scQuerySubmissionService.send(query);

      httpBackend
        .expect('POST', `${restangular.configuration.baseUrl}/${endpoint}`)
        .respond(200, query);

      httpBackend.flush();

    }));
  });
});
