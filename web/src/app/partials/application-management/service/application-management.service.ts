// interface
import { IRouteCache } from '../application-management.interface';

// service
import { CRUDService } from '../../../angular-ids-project/src/helpers/services/crud.service';

export let endpoint: string = 'route-caching';

export class ApplicationManagementService extends CRUDService {

  protected restangular: restangular.IService;

  private _mod: IRouteCache = {
    count: null,
    number_of_retention: null
  };

  /** @ngInject */
  constructor(protected Restangular: restangular.IService) {
    super(Restangular, endpoint);
    this.model = angular.copy(this._mod);
  }

  public clearCurrentCache(): ng.IPromise<any> {
    return this.restangular.one(`${endpoint}/reset`).remove();
  }

  public getNumberToRetain(): ng.IPromise<any> {
    return this.restangular.one(`${endpoint}/retention`).get();
  }

  public updateNumberToRetain(numberToRetain: number): ng.IPromise<any> {
    return this.restangular.one(`${endpoint}/retention/${numberToRetain}`).customPUT();
  }

}
