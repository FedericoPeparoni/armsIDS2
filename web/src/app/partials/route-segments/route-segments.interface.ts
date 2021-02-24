// interfaces
import { ISpringPageableParams } from '../../angular-ids-project/src/helpers/services/crud.service';

export interface IRouteSegment {
  id: number;
  flight_record_id: number;
  segment_type: string;
  segment_number: number;
  location: any;
  segment_start_label: string;
  segment_length: number;
  segment_cost: number;
  destination?: string;
}

export interface IRouteSegmentSpring extends ISpringPageableParams {
  content: Array<IRouteSegment>;
}