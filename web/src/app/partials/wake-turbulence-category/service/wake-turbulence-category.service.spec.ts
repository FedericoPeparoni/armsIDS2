// interface
import { IStaticType } from '../../../angular-ids-project/src/helpers/interfaces/static-data-type.interface';

// service
import { WakeTurbulenceCategoryService, endpoint } from './wake-turbulence-category.service';

describe('service groups', () => {

  let httpBackend,
    restangular,
    response;

  let category: IStaticType = {
    id: null,
    name: null
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

      response = null;
    });
  });

  describe('list', () => {
    it('should get a list of categories', inject((wakeTurbulenceCategoryService: WakeTurbulenceCategoryService) => {

      let categoryList: Array<IStaticType> = [];

      categoryList.push(category);

      wakeTurbulenceCategoryService.list();

      httpBackend
        .expect('GET', `${restangular.configuration.baseUrl}${endpoint}?size=20`)
        .respond(200, categoryList);

      httpBackend.flush();

    }));
  });

});
