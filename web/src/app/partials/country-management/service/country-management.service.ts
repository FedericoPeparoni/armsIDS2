// interfaces
import { ICountry, IAerodromePrefix, IAircraftRegistrationPrefix } from '../country-management.interface';

// crud service and interface
import { CRUDService, IRestParams } from '../../../angular-ids-project/src/helpers/services/crud.service';

// endpoint
export let endpoint: string = '/countries';

export class CountryManagementService extends CRUDService {

  protected restangular: restangular.IService;

  private _mod: ICountry = {
    id: null,
    country_code: null,
    country_name: null,
    aircraft_registration_prefixes: null,
    aerodrome_prefixes: null
  };

  /** @ngInject */
  constructor(protected Restangular: restangular.IService) {
    super(Restangular, endpoint);
    this.model = angular.copy(this._mod);
  }

  // override to handle prefix arrays
  public listAll(params?: IRestParams): ng.IPromise<any> {
    if (!params) { // does not exist, create the object (it's mandatory to function)
      params = {};
    }
    params.size = -1; // override and ensure get all results
    return this.list(params).then((result: any) => {
      const formattedResults = result.content.map((entry: any) => {
        entry.aerodrome_prefixes = entry.aerodrome_prefixes.map((aP: IAerodromePrefix) => aP.aerodrome_prefix).join(', ');
        entry.aircraft_registration_prefixes = entry.aircraft_registration_prefixes.map((aRP: IAircraftRegistrationPrefix) => aRP.aircraft_registration_prefix).join(', ');
        return entry;
      });

      result.content = formattedResults;
      return result;
    });
  }

  public findCountryByCountryCode(countryCode: string): ng.IPromise<ICountry> {
    return this.restangular.one(`${endpoint}/country-by-country-code/${countryCode}`).get();
  }
}
