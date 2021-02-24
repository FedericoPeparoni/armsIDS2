// interface
import { IMtowType } from '../mtow.interface';
import { IStaticType } from '../../../angular-ids-project/src/helpers/interfaces/static-data-type.interface';

// service
import { CRUDService } from '../../../angular-ids-project/src/helpers/services/crud.service';

export let endpoint: string = 'average-mtow-factors';

export class MtowService extends CRUDService {

  protected restangular: restangular.IService;

  private _mod: IMtowType = {
    id: null,
    upper_limit: null,
    average_mtow_factor: null,
    factor_class: 'DOMESTIC' // default factor class
  };

  /** @ngInject */
  constructor(protected Restangular: restangular.IService) {
    super(Restangular, endpoint);
    this.model = angular.copy(this._mod);
  }

  public listMtowFactorClass(): Array<IStaticType> {
    return [
      {
        name: 'Domestic',
        value: 'DOMESTIC'
      },
      {
        name: 'Regional',
        value: 'REGIONAL'
      },
      {
        name: 'International',
        value: 'INTERNATIONAL'
      }
    ];
  }

}
