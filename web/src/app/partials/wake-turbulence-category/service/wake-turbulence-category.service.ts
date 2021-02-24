// services
import { CRUDService } from '../../../angular-ids-project/src/helpers/services/crud.service';

export let endpoint = '/wake-turbulence-categories';

export class WakeTurbulenceCategoryService extends CRUDService {

  protected restangular: restangular.IService;

  /** @ngInject */
  constructor(protected Restangular: restangular.IService) {
    super(Restangular, endpoint);
  }

}
