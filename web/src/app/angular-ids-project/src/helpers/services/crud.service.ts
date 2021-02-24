// class with basic crud operations

// services
import { LocalStorageService } from '../../../../angular-ids-project/src/components/services/localStorage/localStorage.service';

// constants
import { SysConfigConstants } from '../../../../partials/system-configuration/system-configuration.constants';

// http://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/domain/Pageable.html
// all these parameters are optional
export interface ISpringPageableParams {
  content?: Array<any>;
  first?: boolean;
  last?: boolean;
  number?: number;
  number_of_elements?: number;
  page?: number; // note `page` and `number` are the same
  size?: any;
  sort?: Object;
  total_elements?: number;
  total_pages?: number;
  total_items?: number;
}

export interface IRestParams extends ISpringPageableParams {
  filter?: string;
}

export class CRUDService {

  protected restangular: restangular.IService;
  private _model: Object;

  constructor(protected Restangular: restangular.IService, protected endpoint: string) {
    this.restangular = Restangular;
    this.endpoint = endpoint;
  }

  protected get model(): Object { return this._model; };
  protected set model(obj: Object) { this._model = obj; };

  public create(obj: any): ng.IPromise<any> {
    return this.restangular.all(this.endpoint).customPOST(obj, '', undefined, { 'Content-Type': 'application/json' }); // todo should be handled better without forcing the content type
  }

  /**
   * Makes a GET request to the endpoint
   *
   * Note: This is kind of set up with Spring in mind
   *
   * @param params        is an Object that will be converted to GET parameters
   * @param queryString   is GET parameters that will be appended to the URL
   * @returns {IPromise<ISpringPageableParams>}
   */
  public list(params: ISpringPageableParams = {}, queryString: string = '', endpoint: string = ''): ng.IPromise<any> {
    if (queryString !== '') {
      queryString = `?${queryString}`;
    }

    // if not set to 'get all / -1' add the system config page size
    if (!params.size) {
      params.size = LocalStorageService.get(`SystemConfiguration:${SysConfigConstants.ROW_FOR_PAGE}`) || 20;
    };

    // the uib-pagination starts at `1` but Spring Framework starts at `0`, therefore we fake it
    if (params.page) {
      params.page--;
    }

    return this.restangular.one(`${endpoint ? endpoint : this.endpoint}${queryString}`).get(params)
      .then((response: ISpringPageableParams) => this.rectifyPageableParams(response));
  }


  // sends size=-1 with parameters, returning all rows
  public listAll(params?: IRestParams): ng.IPromise<any> {
    if (!params) { // does not exist, create the object (it's mandatory to function)
      params = {};
    }
    params.size = -1; // override and ensure get all results
    return this.list(params); // calls list method since it's the same
  }

  public update(obj: any, id: number): ng.IPromise<any> {
    return this.restangular.one(this.endpoint, id).customPUT(obj);
  }

  public delete(id: number): ng.IPromise<any> {
    return this.restangular.one(this.endpoint, id).remove();
  }

  public get(id: number): ng.IPromise<any> {
    return this.restangular.one(this.endpoint, id).get();
  }

  /**
   * To not screw up the uib-pagination directive, we fake the number one higher.
   *
   * @param response from spring
   */
  protected rectifyPageableParams(response: ISpringPageableParams): ISpringPageableParams {
    if (response.number) {
      response.number++;
    }
    return response;
  }

}
