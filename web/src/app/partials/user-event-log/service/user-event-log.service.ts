// interface
import { IUserEventLog } from '../user-event-log.interface';

// service
import { CRUDService } from '../../../angular-ids-project/src/helpers/services/crud.service';

// class
import { Event } from '../user-event-log.class';

export let endpoint: string = 'user-event-logs';

export class UserEventLogService extends CRUDService {

  protected restangular: restangular.IService;

  private _mod: IUserEventLog = {
    id: null,
    user_name: null,
    date_time: null,
    ip_address: null,
    event_type: null,
    record_primary_key: null,
    unique_record_id: null,
    modified_column_names_values: null
  };

  /** @ngInject */
  constructor(protected Restangular: restangular.IService) {
    super(Restangular, endpoint);
    this.model = angular.copy(this._mod);
  }

  /**
   * Creates 'delete' logs from requests
   * Creates 'add' or 'update' logs from responses
   * 
   * @param  {any} element - the data from a request/response
   * @param  {string} operation - the type of request (get, post, put, remove)
   * @param  {any} id - the identifier (/radar-summaries/4782)
   * @returns void
   */
  public createEvent(element: any, operation: string, id: string): void {
    // prevent duplicate records
    const identifier = this.formatIdentifier(id);
    const isNotEventLog = identifier.includes(endpoint) && operation === 'post';
    const data = this.getData(element, id);

    let eventType = this.getEventTypeFromResponse(data, operation);

    if (operation !== 'get' && !isNotEventLog) {
      if (data && data.id) {
        const event = new Event(eventType, data.id.toString(), identifier.split('-').join(' '));
        super.create(event);
      }
    }
  }

  /**
   * Returns the event type. Usually a CRUD operation,
   * except for custom events like credit/debit note
   * generation
   * 
   * @param  {any} data - response object
   * @param  {string} operation - request type
   * @returns string
   */
  private getEventTypeFromResponse(data: any, operation: string): string {
    const CREDIT_NOTE_EVENT = 'credit note creation';
    const DEBIT_NOTE_EVENT = 'debit note creation';

    // custom events for credit / debit note creation
    const isCreditDebitNote = data && data.payment_mechanism && data.payment_mechanism === 'adjustment';
    if (isCreditDebitNote) { return data.transaction_type.name === 'credit' ? CREDIT_NOTE_EVENT : DEBIT_NOTE_EVENT; }

    switch (operation) {
      // returns the event type
      // associated with the operation
      case 'post': return 'add';
      case 'put': return 'update';
      case 'remove': return 'delete';
    }
  }

  /**
   * Will return a user readable identifier
   * that corresponts with the modified table,
   * for logging purposes
   * 
   * @param  {string} identifier
   * @returns string
   */
  private formatIdentifier(identifier: string): string {
    const splitIdentifier = identifier.split('/');
    const lastPosition = splitIdentifier[splitIdentifier.length - 1];

    // if it is not a number, use the last
    // part of the identifier in the
    // user event log
    if (isNaN(<any>lastPosition)) {
      return lastPosition;
    }

    // if it is a number, return the
    // first part for the identifier
    if (identifier.includes('/')) {
      return splitIdentifier[0];
    }

    // return as is if no parsing
    // is necessary
    return identifier;
  }

  /**
   * If there is no data.id, this will
   * search for and return an id from
   * the last part of the identifier
   * eg: radar-summaries/2873
   * 
   * @param  {} element
   * @param  {string} identifier
   * @returns any
   */
  private getData(element: any, identifier: string): any {
    const splitIdentifier = identifier.split('/');
    const lastPosition = splitIdentifier[splitIdentifier.length - 1];
    if (element && element.id) {
      return element;
    } else {
      // if data.id is undefined,
      // use the last part of the
      // identifier, if it is a number
      if (!isNaN(<any>lastPosition)) {
        return { id: lastPosition };
      }
    }
  }
}
