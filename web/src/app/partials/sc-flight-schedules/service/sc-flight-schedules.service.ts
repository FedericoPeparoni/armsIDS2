// interfaces
import { IFlightSchedule } from '../../flight-schedule-management/flight-schedule-management.interface';

// services
import { CRUDFileUploadService } from '../../../angular-ids-project/src/helpers/services/crud-file-handler.service';

export let endpoint: string = 'sc-flight-schedules';
export class ScFlightSchedulesService extends CRUDFileUploadService {

  protected restangular: restangular.IService;

  private _mod: IFlightSchedule = {
    id: null,
    account: null,
    flight_service_number: null,
    dep_ad: null,
    dep_time: null,
    dest_ad: null,
    dest_time: null,
    daily_schedule: null,
    self_care: true,
    active_indicator: 'active',
    start_date: null,
    end_date: null,
    missing_flight_movements: null,
    unexpected_flights: null
  };

  /** @ngInject */
  constructor(protected Restangular: restangular.IService) {
    super(Restangular, endpoint);
    this.model = angular.copy(this._mod);
  }

  // check to see which schedules will be replaced by upload
  public preUploadSchedules(startDate: string, document: any): ng.IPromise<any> {
      let formDate = new FormData();
      formDate.append('file', document, 'flight_schedules');

    return this.restangular.one(`${endpoint}/pre-upload?startDate=${startDate}`).withHttpConfig({ 'transformRequest': angular.identity })
      .customPUT(formDate, undefined, undefined, { 'Content-Type': undefined });
  }
}
