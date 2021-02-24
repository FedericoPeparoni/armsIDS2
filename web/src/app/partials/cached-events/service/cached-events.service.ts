// interfaces
import { ICachedEvent } from '../cached-events.interface';

// services
import { CRUDService } from '../../../angular-ids-project/src/helpers/services/crud.service';

export let endpoint = 'cachedevents';

export class CachedEventsService extends CRUDService {

  protected restangular: restangular.IService;

  private _mod: ICachedEvent = {
    id: null,
    target: null,
    method_name: null,
    param_types: [],
    exceptions: [],
    caches: [],
    results: [],
    metadata: [],
    retry_count: 0,
    last_attempt: null
  };

  /** @ngInject */
  constructor(protected Restangular: restangular.IService) {
    super(Restangular, endpoint);
    this.model = angular.copy(this._mod);
  }

  public getNextRetryCycle(): restangular.IPromise<any> {
    return this.restangular.one(`${endpoint}/nextretrycycle`).get();
  }

  public retry(id: number): restangular.IPromise<any> {
    return this.restangular.one(`${endpoint}`, id).one(`retry`).put();
  }
}
