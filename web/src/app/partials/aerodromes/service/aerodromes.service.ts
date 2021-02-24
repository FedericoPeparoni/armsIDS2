// interfaces
import { IAerodrome, IAerodromeServiceType } from '../aerodromes.interface';

// services
import { CRUDService } from '../../../angular-ids-project/src/helpers/services/crud.service';

export let endpoint = '/aerodromes';

export class AerodromesService extends CRUDService {

  protected restangular: restangular.IService;

  private _mod: IAerodrome = {
    id: null,
    aerodrome_name: null,
    extended_aerodrome_name: null,
    aixm_flag: null,
    geometry: {
      type: 'Point',
      coordinates: [null, null]
    },
    is_default_billing_center: null,
    billing_center: null,
    aerodrome_category: null,
    external_accounting_system_identifier: null,
    aerodrome_services: []
  };

  /** @ngInject */
  constructor(protected Restangular: restangular.IService) {
    super(Restangular, endpoint);
    this.model = angular.copy(this._mod);
  }

  // returns only aerodromes managed by the billing center that is assigned to the current user
  public getAerodromesByBillingCentre(): ng.IPromise<IAerodrome[]> {
    return this.restangular.one(`${endpoint}?current-billing-center`).get();
  }

  public getAerodromeServiceTypes(): ng.IPromise<IAerodromeServiceType[]> {
    return this.restangular.one(`${endpoint}/aerodrome-service-types`).get();
  }

  // returns all aerodrome services
  public getAllAerodromeServices(): ng.IPromise<IAerodrome[]> {
    return this.restangular.one(`${endpoint}/aerodrome-services`).get();
  }
}
