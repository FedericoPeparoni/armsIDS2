// interfaces
import { IFlightSchedule } from '../flight-schedule-management.interface';
import { IStaticType } from '../../../angular-ids-project/src/helpers/interfaces/static-data-type.interface';

// services
import { CRUDFileUploadService } from '../../../angular-ids-project/src/helpers/services/crud-file-handler.service';

export let endpoint: string = 'flight-schedules';
export class FlightScheduleManagementService extends CRUDFileUploadService {

  protected restangular: restangular.IService;

  public _mod: IFlightSchedule = {
    id: null,
    account: null,
    flight_service_number: null,
    dep_ad: null,
    dep_time: null,
    dest_ad: null,
    dest_time: null,
    daily_schedule: null,
    self_care: false,
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

  public getModel(): IFlightSchedule {
    return angular.copy(this._mod);
  }

  // check to see which schedules will be replaced by upload
  public preUploadSchedules(startDate: string, document: any): ng.IPromise<any> {
    const formDate = new FormData();

    formDate.append('file', document, 'flight_schedules');

    return this.restangular.one(`${endpoint}/pre-upload?startDate=${startDate}`).withHttpConfig({ 'transformRequest': angular.identity })
      .customPUT(formDate, undefined, undefined, { 'Content-Type': undefined });
  }

  /**
   * Populate multiselect 
   * with days of the week
   * 
   * @returns Array
   */
  public getDaysOfWeek(): Array<IStaticType> {
    return [
      {
        id: 1,
        name: 'Sunday',
        value: '1'
      },
      {
        id: 2,
        name: 'Monday',
        value: '2'
      },
      {
        id: 3,
        name: 'Tuesday',
        value: '3'
      },
      {
        id: 4,
        name: 'Wednesday',
        value: '4'
      },
      {
        id: 5,
        name: 'Thursday',
        value: '5'
      },
      {
        id: 6,
        name: 'Friday',
        value: '6'
      },
      {
        id: 7,
        name: 'Saturday',
        value: '7'
      }
    ];
  }

}
