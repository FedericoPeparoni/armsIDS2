// import interfaces
import { ITableSortDirective } from './tableSort.interface';

// import services
import { DebounceService } from '../services/debounce/debounce.service';

export class TableSortController {

    private sortTableDebounce: () => angular.IPromise<{}>;

    /* @ngInject */
    constructor(protected $scope: ITableSortDirective, protected debounceService: DebounceService) {
        this.sortTableDebounce = debounceService.debounce(() => this.$scope.sortTable(), 600);
    }

    public updateElementSort(element: angular.IAugmentedJQuery): void {
        this.$scope.updateElementSort(element);
    }

    public updateSortValue(newValue: string, oldValue: string): void {

        // update old sort value to new value
        let isSorted: boolean = this.$scope.updateSortValue(newValue, oldValue);

        // debounce table sort incase multiple sort values are to be updated
        if (isSorted) {
            this.sortTableDebounce();
        }

        // let all upstream scope know a table sort item value has changed
        // this is used on the flight movement page for dep|dest ad flags to
        // prevent flickering of column value changes on refresh
        this.$scope.$emit('table-sort-item-value-changed', newValue, oldValue, isSorted);
    }
}
