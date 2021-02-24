// interface
import { IRepositioningAerodromeCluster } from '../repositioning-aerodrome-clusters.interface';

// service
import { CRUDService } from '../../../angular-ids-project/src/helpers/services/crud.service';

export let endpoint: string = 'repositioning-aerodrome-clusters';

export class RepositioningAerodromeClustersService extends CRUDService {

  protected restangular: restangular.IService;

  private _mod: IRepositioningAerodromeCluster = {
    id: null,
    repositioning_aerodrome_cluster_name: null,
    enroute_fees_are_exempt: 0,
    approach_fees_are_exempt: 0,
    aerodrome_fees_are_exempt: 0,
    late_arrival_fees_are_exempt: 0,
    late_departure_fees_are_exempt: 0,
    parking_fees_are_exempt: 0,
    international_pax: 0,
    domestic_pax: 0,
    extended_hours: 0,
    aerodrome_identifiers: [],
    flight_notes: null
  };

  /** @ngInject */
  constructor(protected Restangular: restangular.IService) {
    super(Restangular, endpoint);
    this.model = angular.copy(this._mod);
  }

}
