// contants
import { ServerDatetimeConstant } from './server-datetime.contant';

export class ServerDatetimeService {

  protected interval: ng.IIntervalService;
  protected restangular: restangular.IService;

  /**
   * Service api endpoint used to GET the server
   * current datetime.
   */
  private endpoint: string;

  /**
   * Offset between server and client datetime. Unit of measure is defined
   * as a constant at `ServerDatetimeConstant.offsetPercision`.
   */
  private offset: number;

  /** @ngInject */
  constructor(private $interval: ng.IIntervalService, protected Restangular: restangular.IService) {
    this.interval = $interval;
    this.restangular = Restangular;
  }

  /**
   * Initialize server datetime offset service.
   * 
   * @param endpoint api endpoint to call for current datetime
   * @param updateDuration duration in milliseconds to refresh offset
   */
  public initialize(endpoint: string, updateDuration: number, defaultOffset: number = 0): void {
    this.endpoint = endpoint;
    this.offset = defaultOffset;
    this.updateOffset();
    this.interval(() => this.updateOffset(), updateDuration);
  }

  /**
   * Get the local date of the supplied server date.
   * 
   * @param serverDatetime server date to conver to local date
   */
  public getLocalDate(serverDate: Date): Date {
    return moment(serverDate)
      .subtract(this.offset, ServerDatetimeConstant.offsetPercision)
      .toDate();
  }

  /**
   * Get the server date of supplied date.
   * 
   * @param localDatetime local date to convert to server date
   */
  public getServerDate(localDate: Date): Date {
    return moment(localDate)
      .add(this.offset, ServerDatetimeConstant.offsetPercision)
      .toDate();
  }

  /**
   * Return server date/time
   * @param endpoint 
   */
  public getCurrentDateTime(endpoint: string): ng.IPromise<any> {
    return this.restangular.one(endpoint).get();
  }

  /**
   * Determine date time difference between server time and local time using.
   * 
   * @param serverDatetime server datetime ISO-8601 value
   * @param requestDatetime request sent date
   */
  private resolveOffset(serverDatetime: string, requestDatetime: Date): number {
    let local: moment.Moment = moment(new Date());
    let request: moment.Moment = moment(requestDatetime);
    let server: moment.Moment = moment(serverDatetime);

    // adjust for delay in request
    let delay: number = local.diff(request, ServerDatetimeConstant.offsetPercision) / 2;
    server.add(delay, ServerDatetimeConstant.offsetPercision);

    // return difference in local time verse server time
    return server.diff(local, ServerDatetimeConstant.offsetPercision);
  }

  /**
   * Update offset from remote server.
   */
  private updateOffset(): void {
    let requestDatetime: Date = new Date();
    this.restangular.one(this.endpoint).get()
      .then((response: string) => this.offset = this.resolveOffset(response, requestDatetime));
  }
}
