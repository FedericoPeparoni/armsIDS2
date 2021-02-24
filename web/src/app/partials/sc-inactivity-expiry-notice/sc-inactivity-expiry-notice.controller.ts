import { CRUDFormControllerUserService } from '../../angular-ids-project/src/helpers/controllers/crud-form-user-service/crud-form-user-service.controller';
import { ScInactivityExpiryNoticeService } from './service/sc-inactivity-expiry-notice.service';
import { ISCInactivityExpiryNotices } from './sc-inactivity-expiry-notice.interface';

export class ScInactivityExpiryNoticeController extends CRUDFormControllerUserService {

  /* @ngInject */
  constructor(protected $scope: ng.IScope, private scInactivityExpiryNoticeService: ScInactivityExpiryNoticeService, private $filter: ng.IFilterService,
    private $translate: angular.translate.ITranslateService) {
    super($scope, scInactivityExpiryNoticeService);
    super.setup();

    this.getFilterParameters();
    this.$scope.refreshOverride = () => this.refreshOverride();
    this.$scope.editOverride = (item: ISCInactivityExpiryNotices) => this.editOverride(item);
  }

  protected refreshOverride(): angular.IPromise<void> {
    this.getFilterParameters();
    return super.list(this.$scope.filterParameters, this.$scope.getSortQueryString());
  }

  private getFilterParameters(): void {
    this.$scope.filterParameters = {
      search: this.$scope.search,
      page: this.$scope.pagination ? this.$scope.pagination.number : 0
    };
  }

  private editOverride(item: ISCInactivityExpiryNotices): void {
    super.edit(item);
    this.$scope.dateTime = this.$filter('dateConverter')(item.date_time, 'HH:mm');
    this.$scope.noticeType = this.$translate.instant(item.notice_type);

  }
}
