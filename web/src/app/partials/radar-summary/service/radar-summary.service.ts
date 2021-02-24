// interface
import { IRadarSummaryType, IRadarSummaryFilters } from '../radar-summary.interface';

// service
import { CRUDFileUploadService } from '../../../angular-ids-project/src/helpers/services/crud-file-handler.service';

export let endpoint: string = 'radar-summaries';

export class RadarSummaryService extends CRUDFileUploadService {

  protected restangular: restangular.IService;

  private _mod: IRadarSummaryType = {
    id: null,
    date: null,
    flight_identifier: null,
    day_of_flight: null,
    departure_time: null,
    registration: null,
    aircraft_type: null,
    departure_aero_drome: null,
    destination_aero_drome: null,
    route: null,
    fir_entry_point : null,
    fir_entry_time: null,
    fir_entry_flight_level: null,
    fir_exit_point: null,
    fir_exit_time: null,
    fir_exit_flight_level: null,
    flight_rule: null,
    flight_travel_category: null,
    cruising_speed: null,
    flight_level: null,
    wake_turb: null,
    entry_coordinate: null,
    exit_coordinate: null
  };

  /** @ngInject */
  constructor(protected Restangular: restangular.IService) {
    super(Restangular, endpoint);
    this.model = angular.copy(this._mod);
  }

  public listCategories(): Array<string> {
    return [
      'overflight',
      'arrival',
      'departure',
      'domestic'
    ];
  }

  public listFlightRules(): Array<string> {
    return ['IFR', 'VFR'];
  }

  // this has to be done because if filters are used, it is a different endpoint
  public list(params?: IRadarSummaryFilters, queryString?: string): ng.IPromise<any> {

    if (params &&
        (typeof params.search !== 'undefined' || (typeof params.start !== 'undefined' && typeof params.end !== 'undefined'))) {

      if (params.search === '') { // for both endpoints they accept `search`, if `search` is empty, just remove it otherwise can cause issues
        delete params.search;
      }

      this.endpoint = `${endpoint}/filters`; // to use filters, it is a different URL endpoint
    }

    return super.list(params, queryString)
      .then((data: Array<IRadarSummaryType>) => { this.endpoint = endpoint; return data; })
      .catch(() => this.endpoint = endpoint); // always revert endpoint
  }

}
