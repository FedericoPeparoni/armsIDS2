// interface
import { INominalType, IRouteType } from '../nominal-routes.interface';

// service
import { CRUDService } from '../../../angular-ids-project/src/helpers/services/crud.service';

export let endpoint: string = 'nominal-routes';

export class NominalRoutesService extends CRUDService {

  protected restangular: restangular.IService;

  private _mod: INominalType = {
    id: null,
    type: null,
    pointa: null,
    pointb: null,
    nominal_distance: null,
    bi_directional: true,
    nominal_route_floor: 0,
    nominal_route_ceiling: 999
  };

  public listRouteTypes(): Array<IRouteType> {
    let routeTypes = [
      {
        id: 0,
        name: 'Aerodrome/Aerodrome',
        value: 'AA'
      },
      {
        id: 1,
        name: 'FIR Entry/Exit to Aerodrome',
        value: 'FIRA'
      },
      {
        id: 2,
        name: 'FIR / FIR',
        value: 'FIRFIR'
      }
    ];

    return routeTypes;
  }

  /** @ngInject */
  constructor(protected Restangular: restangular.IService) {
    super(Restangular, endpoint);
    this.model = angular.copy(this._mod);
  }

}
