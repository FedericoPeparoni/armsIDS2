// controllers
import { CRUDFormControllerUserService } from '../../angular-ids-project/src/helpers/controllers/crud-form-user-service/crud-form-user-service.controller';

// interfaces
import { IRestangularResponse } from '../../angular-ids-project/src/helpers/interfaces/restangularError.interface';
import { ISpringPageableParams } from '../../angular-ids-project/src/helpers/services/crud.service';
import { ICachedEvent, ICachedEventResult, ICachedEventScope } from './cached-events.interface';

// services
import { CachedEventsService } from './service/cached-events.service';
import { HelperService } from './../../angular-ids-project/src/components/services/helpers/helpers.service';

export class CachedEventsController extends CRUDFormControllerUserService {

  /* @ngInject */
  constructor(protected $scope: ICachedEventScope,
              private cachedEventsService: CachedEventsService) {

    // define and setup super class
    super($scope, cachedEventsService);
    super.setup();

    // define necessary defaults to scope
    this.$scope.message = null;
    this.$scope.pagination = <ISpringPageableParams>{};
    this.$scope.retryInProgress = false;

    // expose necessary methods to scope
    this.$scope.commaSeperate = (array: Array<any>, property: string) => HelperService.commaSeperate(array, property, 2);
    this.$scope.delete = (id: number) => this.delete(id);
    this.$scope.refresh = () => this.refresh();
    this.$scope.retry = (id: number) => this.retry(id);

    // get and update next retry cycle date
    this.updateNextRetryCycle();

    this.getFilterParameters();
  }

  protected delete(id: number): ng.IPromise<void> {
    this.$scope.retryInProgress = true;
    return this.cachedEventsService.delete(id)
      .then((response: ICachedEvent) => this.refresh())
      .catch((response: IRestangularResponse) => this.displayErrorResponse(response))
      .finally(() => this.$scope.retryInProgress = false);
  }

  protected refresh(): ng.IPromise<void> {
    this.getFilterParameters();

    // hold search value to update highlight text after promise returned
    // this forces highlighting to use the same search value even if the
    // search value changes after the promise was sent
    // and finally update next retry cycle
    return super
      .list(this.$scope.filterParameters, this.$scope.getSortQueryString())
      .then(() =>  {
        this.updateCachedEventResults();
        this.updateHighlight(this.$scope.search);
        this.updateNextRetryCycle();
      });
  }

  protected retry(id: number): ng.IPromise<void> {
    this.$scope.retryInProgress = true;
    return this.cachedEventsService.retry(id)
      .then((response: ICachedEvent) => {
        this.displayRetryResponse(response);
        this.refresh();
      })
      .catch((response: IRestangularResponse) => this.displayErrorResponse(response))
      .finally(() => this.$scope.retryInProgress = false);
  }

  private getFilterParameters(): void {
    this.$scope.filterParameters = {
      search: this.$scope.search,
      page: this.$scope.pagination ? this.$scope.pagination.number : 0
    };
  }

  /**
   * Display error message of restangular response.
   *
   * @param response restangular response with error
   */
  private displayErrorResponse(response: IRestangularResponse): void {
    this.$scope.error = {
      error: response
    };
  }

  /**
   * Display status of a cached event's latest result.
   *
   * @param response cached event to display
   */
  private displayRetryResponse(response: ICachedEvent): void {
    let cachedEventResult: ICachedEventResult = response.results[response.results.length - 1];
    this.$scope.message = {
      title: cachedEventResult.clazz,
      description: cachedEventResult.result,
      thrown: cachedEventResult.clazz !== null || cachedEventResult.result !== null
    };
  }

  /**
   * Update the editable cached event results value from list
   * if editable item still exists in the list.
   *
   * If editable item no longer exists, the editable item is reset.
   */
  private updateCachedEventResults(): void {

    // flag to indicate if editable item has been refreshed
    let refreshed: boolean = false;

    // if editable item, loop through refreshed list for match and update editable item
    let editable: ICachedEvent = this.$scope.editable;
    if (editable !== null && editable.id !== null) {
      let items: Array<ICachedEvent> = this.$scope.list;
      for (let i = 0; i < items.length; i++) {
        if (items[i].id === editable.id) {
          this.$scope.editable = items[i];
          refreshed = true;
          break;
        }
      }
    }

    // if editable item is NOT updated, clear it out as it is no longer visible in the list
    if (!refreshed) {
      this.$scope.editable = angular.copy(this.service.model);
    }
  }

  /**
   * Update scope highlight search with new search value.
   *
   * @param search search text
   */
  private updateHighlight(search: string): void {
    this.$scope.highlightSearch = search;
  }

  /**
   * Gets the next retry cycle date and sets scope value.
   */
  private updateNextRetryCycle(): void {
    this.cachedEventsService.getNextRetryCycle()
      .then((response: string) => this.$scope.nextRetryCycle = moment.utc(response).toDate());
  }
}
