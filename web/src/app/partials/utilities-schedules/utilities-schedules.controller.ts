// controllers
import { CRUDFormControllerUserService } from '../../angular-ids-project/src/helpers/controllers/crud-form-user-service/crud-form-user-service.controller';

// interfaces
import { IRange, ISchedule, ISchedulesScope } from './utilities-schedules.interface';

// services
import { UtilitiesSchedulesService } from './service/utilities-schedules.service';

export class UtilitiesSchedulesController extends CRUDFormControllerUserService {

  /* @ngInject */
  constructor(protected $scope: ISchedulesScope, private utilitiesSchedulesService: UtilitiesSchedulesService) {
    super($scope, utilitiesSchedulesService);
    super.setup();

    $scope.scheduleTypes = utilitiesSchedulesService.listScheduleTypes();
    $scope.getScheduleTypeName = (scheduleValue: string) => this.getScheduleTypeName(scheduleValue);
    $scope.editRange = (item: IRange) => this.editRange(item);
    $scope.resetRange = (item: ISchedule) => this.resetRange(item);
    $scope.createRange = (schedule_id: number, range: IRange) => this.createRange(schedule_id, range);
    $scope.updateRange = (range: IRange) => this.updateRange(range);
    $scope.deleteRange = (id: number) => this.deleteRange(id);
    $scope.deleteEditableRangeBracket = (id: number) => this.deleteEditableRangeBracket(id);
    $scope.updateEditableRangeBracket = (range: IRange) => this.updateEditableRangeBracket(range);
    $scope.refresh = () => this.refreshOverride();
    this.getFilterParameters();

    this.$scope = $scope;
  }

  private getScheduleTypeName(scheduleValue: string): string {
    for (let i in this.$scope.scheduleTypes) {
      if (this.$scope.scheduleTypes[i].value === scheduleValue) {
        return this.$scope.scheduleTypes[i].name;
      }
    }
  }

  private editRange(item: IRange): void {
    this.$scope.editableRangeBracket = angular.copy(item);
  }

  private resetRange(item?: ISchedule): void {
    if (item) {
      this.$scope.ranges = angular.copy(item.utilities_range_bracket);
    }

    this.$scope.editableRangeBracket = {
      id: null,
      schedule_id: null,
      range_top_end: null,
      unit_price: null
    };
  }

  private createRange(schedule_id: number, range: IRange): void {
    this.service.createRange(schedule_id, range).then((range: IRange) => {
      this.resetRange();
      this.$scope.ranges.push(range);
      this.list({filter: this.$scope.filter});
    });
  }

  private updateRange(range: IRange): void {
    this.service.updateRange(range).then((range: IRange) => {
      this.resetRange();
      this.updateEditableRangeBracket(range);
      this.list({ filter: this.$scope.filter });
    });
  }

  private updateEditableRangeBracket(range: IRange): void {
    for (let i = 0; i < this.$scope.ranges.length; i++) {
      if (range.id === this.$scope.ranges[i].id) {
        this.$scope.ranges[i] = range;
        break;
      };
    };
  }

  private deleteRange(id: number): void {
    this.service.deleteRange(id).then(() => {
      this.resetRange();
      this.deleteEditableRangeBracket(id);
      this.list({ filter: this.$scope.filter });
    });
  }

  private deleteEditableRangeBracket(id: number): void {
    for (let i = 0; i < this.$scope.ranges.length; i++) {
      if (id === this.$scope.ranges[i].id) {
        this.$scope.ranges.splice(i, 1);
        break;
      };
    };
  }

  private getFilterParameters(): void {
    this.$scope.filterParameters = {
      filter: this.$scope.filter,
      textFilter: this.$scope.textFilter,
      page: this.$scope.pagination ? this.$scope.pagination.number : 0
    };
  }

  /**
   * Refresh data list in scope.
   */
  private refreshOverride(): ng.IPromise<any> {
    this.getFilterParameters();
    return super.list(this.$scope.filterParameters, this.$scope.getSortQueryString());
  }

}
