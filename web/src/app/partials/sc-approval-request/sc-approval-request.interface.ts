// interfaces
import { IStartEndDates } from '../../angular-ids-project/src/components/dateRange/dateRange.interface';

// services
import { ISpringPageableParams } from '../../angular-ids-project/src/helpers/services/crud.service';

export interface ISCApprovalRequest {
  id?: number;
  account: number;
  user: number;
  request_type: string;
  request_dataset: string;
  object_id: number;
  request_text: string;
  status: string;
  responders_name: string;
  response_date: string;
  response_text: string;
  created_at: string;
}

export interface ISCApprovalRequestScope extends ng.IScope {
  editable: ISCApprovalRequest;
  customDate: string;
  approvalResponse: string;
  rejectionResponse: string;
  selectedRequests: Array<number>;
  refreshOverride: Function;
  approve: Function;
  reject: Function;
  approveSelected: Function;
  rejectSelected: Function;
  getSelected: Function;
  formatNameAndTranslate: (name: string) => string;
  selected: Array<number>;
  date: string;
  responseDate: string;
  requestText: any;
  error: any;
  control: IStartEndDates;
  search: string;
  account: string;
  objectType: string;
  status: string;
  pagination: ISpringPageableParams;
  getSortQueryString: Function;
  edit: Function;
  reset: Function;
  mtowUnitOfMeasure: string;
}
