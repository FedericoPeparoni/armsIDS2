import { AboutService } from './about.service';

describe('service about', () => {

  let httpBackend;

  beforeEach(angular.mock.module('armsWeb'));

  beforeEach(() => {
    inject(($httpBackend: angular.IHttpBackendService) => {
      httpBackend = $httpBackend;
    });
  });

  it('should be registered', inject((aboutService: AboutService) => {
    expect(aboutService).not.toBe(null);
  }));

  describe('list method', () => {

    it('should be registered', inject((aboutService: AboutService) => {
      expect(aboutService.list).not.toBe(null);
    }));

    it('should return all config variables', inject((aboutService: AboutService) => {
      let config = aboutService.list();
      expect(config).not.toBe(null);
    }));
  });
});
