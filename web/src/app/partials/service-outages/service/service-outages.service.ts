// interface
import { IServiceOutages } from '../service-outages.interface';

// service
import { CRUDService } from '../../../angular-ids-project/src/helpers/services/crud.service';

export const endpoint: string = 'aerodrome-service-outages';

export class ServiceOutagesService extends CRUDService {

  protected restangular: restangular.IService;

  private _mod: IServiceOutages = {
    id: null,
    aerodrome: null,
    aerodrome_service_type: null,
    start_date_time: null,
    end_date_time: null,
    approach_discount_type: null,
    approach_discount_amount: null,
    aerodrome_discount_type: null,
    aerodrome_discount_amount: null,
    flight_notes: null
  };

  /** @ngInject */
  constructor(protected Restangular: restangular.IService) {
    super(Restangular, endpoint);
    this.model = angular.copy(this._mod);
  }

  public getModel(): IServiceOutages {
    return angular.copy(this._mod);
  }
}
