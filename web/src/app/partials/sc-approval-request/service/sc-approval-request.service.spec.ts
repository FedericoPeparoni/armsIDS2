import { ScApprovalRequestService } from './sc-approval-request.service';

describe('service ScApprovalRequestService', () => {

  let httpBackend,
      restangular;

  beforeEach(angular.mock.module('armsWeb'));

  beforeEach(() => {
      inject(($httpBackend: angular.IHttpBackendService, Restangular: restangular.IService) => {
          httpBackend = $httpBackend;
          restangular = Restangular;
      });
  });

  it('should be registered', inject((scApprovalRequestService: ScApprovalRequestService) => {
      expect(scApprovalRequestService).not.toEqual(null);
  }));
});
