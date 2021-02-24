// interface
import { IAirTrafficData, IAirTrafficResponse, IAirTrafficDataTemplate } from '../air-traffic-data.interface';
import { IStaticType } from '../../../angular-ids-project/src/helpers/interfaces/static-data-type.interface';

// service
import { CRUDService } from '../../../angular-ids-project/src/helpers/services/crud.service';

export let endpoint: string = 'dbqueries/stats/air-traffic-statistics';
export let endpointAirTraffic: string = 'air-traffic-statistics';

export class AirTrafficDataService extends CRUDService {

  protected restangular: restangular.IService;

  private _mod: IAirTrafficData = {
    start_date: null,
    end_date: null,
    aerodromes: null,
    aircraft_types: null,
    mtow_categories: null,
    billing_centres: null,
    accounts: null,
    temporal_group: null,
    flight_types: [],
    flight_scopes: [],
    flight_categories: [],
    flight_rules: [],
    flight_levels: null,
    routes: null,
    sort: null,
    group_by: null,
    fiscal_year: false
  };

  /** @ngInject */
  constructor(protected Restangular: restangular.IService) {
    super(Restangular, endpoint);
    this.model = angular.copy(this._mod);
  }

  public getModel(): IAirTrafficData {
    return angular.copy(this._mod);
  }

  public displayValues(): Array<IStaticType> {
    return [
      {
        name: 'Number of Flights',
        value: 'count'
      },
      {
        name: 'Total Revenue Generated',
        value: 'sum_total_charges'
      },
      {
        name: 'Domestic Passengers',
        value: 'sum_passengers_chargeable_domestic'
      },
      {
        name: 'International Passengers',
        value: 'sum_passengers_chargeable_intern'
      },
      {
        name: 'Revenue by Category',
        value: 'revenue_category'
      }
    ];
  }


  /**
   * Used for `Group By` as well as fields to ignore
   */
  public groupByValues(): Array<IStaticType> {
    return [
      {
        id: 1,
        name: 'Billing Centre',
        value: 'billing_centre'
      },
      {
        id: 2,
        name: 'Account',
        value: 'account'
      },
      {
        id: 3,
        name: 'Flight Type',
        value: 'flight_type'
      },
      {
        id: 4,
        name: 'Flight Scope',
        value: 'flight_scope'
      },
      {
        id: 5,
        name: 'Flight Schedule Type',
        value: 'flight_category'
      },
      {
        id: 6,
        name: 'Flight Rules',
        value: 'flight_rules'
      },
      {
        id: 7,
        name: 'Aerodromes',
        value: 'bill_aerodrome'
      },
      {
        id: 8,
        name: 'Route',
        value: 'route'
      },
      {
        id: 9,
        name: 'Aircraft Type',
        value: 'aircraft_type'
      },
      {
        id: 10,
        name: 'MTOW Category',
        value: 'mtow_category'
      },
      {
        id: 11,
        name: 'Flight Level',
        value: 'flight_level'
      }
    ];
  }

  public query(obj: Object): ng.IPromise<IAirTrafficResponse[]> {
    return this.restangular.all(`${endpoint}`).customPOST(obj);
  }

  public createTemplate(obj: Object): ng.IPromise<IAirTrafficDataTemplate> {
    return this.restangular.all(`${endpointAirTraffic}`).customPOST(obj);
  }

  public deleteTemplate(id: number): ng.IPromise<void> {
    return this.restangular.one(`${endpointAirTraffic}/${id}`).remove();
  }

  public updateTemplate(obj: Object, id: number): ng.IPromise<IAirTrafficDataTemplate> {
    return this.restangular.one(`${endpointAirTraffic}/${id}`).customPUT(obj);
  }

  // returns all template names
  public getAllNames(): ng.IPromise<Array<string>> {
    return this.restangular.one(`${endpointAirTraffic}/getNames/`).get();
  }

  // returns template by name
  public getTemplateByName(name: string): ng.IPromise<IAirTrafficDataTemplate> {
    return this.restangular.one(`${endpointAirTraffic}/template-name/${name}`).get();

  }

}
