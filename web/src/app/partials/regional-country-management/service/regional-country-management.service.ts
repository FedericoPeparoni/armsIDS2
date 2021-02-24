// interfaces
import { IRegionalCountry } from '../regional-country-management.interface';

// services
import { CRUDService } from '../../../angular-ids-project/src/helpers/services/crud.service';

// endpoint
export let endpoint: string = 'regional-countries';

export class RegionalCountryManagementService extends CRUDService {

  protected restangular: restangular.IService;

  private _mod: IRegionalCountry = {
      id: null,
      country: {
        id: null,
        country_code: null,
        country_name: null,
        aircraft_registration_prefixes: null,
        aerodrome_prefixes: null
      }
  };

  /** @ngInject */
  constructor(protected Restangular: restangular.IService) {
    super(Restangular, endpoint);
    this.model = angular.copy(this._mod);
  }

  public update(list: Array<IRegionalCountry>): ng.IPromise<void> {
    let obj = [];

    for (let i = 0; i < list.length; i++) { // expects countries in an object with a `country` key
      obj.push({
        country: list[i]
      });
    }

    return super.update(obj, null);
  }

}
