// interfaces
import { IRejectedItem } from '../rejected-items.interface';

// services
import { CRUDService } from '../../../angular-ids-project/src/helpers/services/crud.service';

export const endpoint = 'rejected-items';

export enum RejectedItemType {
  FLIGHT_MOV = <any>'Flight Movement',
  ATS_MESS = <any>'ATS Message',
  ATS_MOV = <any>'ATC Movement Log',
  TOWER_AIRC = <any>'Tower Movement Log',
  PASSENGER_SERV = <any>'Passenger Service Charge Return',
  RADAR_SUMM = <any>'Radar Summary',
  OTHER = <any>'Other'
}

export class RejectedItemsService extends CRUDService {

  protected restangular: restangular.IService;

  private _mod: IRejectedItem = {
    id: null,
    filter_by: null,
    filter_by_record_type: null,
    filter_by_date: null,
    filter_by_status: null,
    filter_by_originator: null,
    filter_by_file_name: null,
    record_type: null,
    rejected_date_time: null,
    error_message: null,
    error_details: null,
    rejected_reason: null,
    raw_text: null,
    header: null,
    json_text: null,
    originator: null,
    status: null,
    file_name: null
  };

  /** @ngInject */
  constructor(protected Restangular: restangular.IService) {
    super(Restangular, endpoint);
    this.model = angular.copy(this._mod);
  }

  public getModel(): IRejectedItem {
    return angular.copy(this._mod);
  }

  public updateOverride(obj: any, id: number, merge?: boolean): ng.IPromise<any> {
    return this.restangular.one(this.endpoint, id).customPUT(obj, undefined, { 'merge': merge });
  }
}
