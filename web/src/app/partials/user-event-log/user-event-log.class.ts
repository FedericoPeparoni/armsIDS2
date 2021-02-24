// interfaces
import { IEvent } from './user-event-log.interface';

export class Event implements IEvent {

  constructor(public event_type: string, public record_primary_key: string, public unique_record_id: string) {}
}
