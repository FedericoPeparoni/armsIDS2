/**
 * Table Sort directive
 *
 * Note: This is set up with the Spring framework in mind, specifically the Pageable notation
 *
 * How to use:
 *
 * on a table add the attribute
 *
 * table-sort="refresh(null, sort)"
 *
 * The second parameter is a string in this format sort=name,desc&sort=address,asc
 * It's not part of the first parameter which is an object because javascript objects cannot have duplicate keys
 *
 * There is an optional parameter as well:
 *
 * table-sort-set="whatToSetTo"
 *
 * `whatToSetTo` is an object that follows the ISort interface
 *
 * After you set that, the directive will callback the controller as well to make an API call.
 * Only update `whatToSetTo` if you want to make an API call
 *
 * HTML bindings by column:
 *
 * on the `th` elements, add the attribute like this:
 *
 * sort="name"
 *
 * Note: these do not match the model, but bind one-to-one with the API's object
 *
 * If you need to bind to an object inside an object, dot notation works (Spring allows this)
 *
 * sort="group.id"
 */

// import interfaces
import { ISort, ITableSortDirective } from './tableSort.interface';

// import controllers
import { TableSortController } from './tableSort.controller';

/** @ngInject */
export function tableSort(): angular.IDirective {
  return {
    restrict: 'A',
    scope: {
      tableSort: '&',
      sortQueryString: '=',
      disableSort: '=?',
      cannedSort: '=?',
      sortQueryStringSuffix: '@?'
    },
    link: tableSortLinkFunc,
    controller: TableSortController,
    controllerAs: 'tableSortCtrl'
  };
}

/** @ngInject */
export function tableSortItem(): angular.IDirective {
  return {
    require: '?^^tableSort',
    restrict: 'A',
    link: tableSortItemLinkFunc
  };
}

function tableSortLinkFunc(scope: ITableSortDirective, elem: angular.IAugmentedJQuery): void {

  let th = elem.find('th');
  let sortArr: Array<ISort> = [];
  let sortString = '';

  scope.sortTable = () => sortTable();

  // add update element value function
  scope.updateElementSort = (e: angular.IAugmentedJQuery) => updateElementSort(e);
  scope.updateSortValue = (newValue: string, oldValue: string) => updateSortValue(newValue, oldValue);

  // disable sort when true
  scope.$watch('disableSort', () => disableSort(scope.disableSort));

  // update canned sort when changed
  scope.$watch('cannedSort', () => updateCannedSort(scope.cannedSort), true);

  // sort string getter method
  scope.sortQueryString = () => {
    sortString = buildQueryString(sortArr);
    return sortString;
  };

  /**
   * Build sort query string from sort array.
   * 
   * @param sortArr sort array
   */
  function buildQueryString(sortArr: Array<ISort>): string {
    let tmpArr: Array<string> = [];

    for (let i = 0; i < sortArr.length; i++) {
      tmpArr.push(`sort=${sortArr[i].value},${sortArr[i].dir}`);
    }
    if (tmpArr.length > 0 && scope.sortQueryStringSuffix) {
      tmpArr.push(scope.sortQueryStringSuffix);
    }
    return tmpArr.join('&'); // join the string
  }

  /**
   * Disable sorting by applying table-sort-disabled class.
   * 
   * @param disabled disable sorting
   */
  function disableSort(disabled: boolean): void {
    if (disabled) {
      elem.addClass('table-sort-disabled');
    } else {
      elem.removeClass('table-sort-disabled');
    }
  }

  /**
   * Update sort from a canned sort order.
   * 
   * @param sortString canned sort string
   */
  function updateCannedSort(sortString: string): void {
    sortString = sortString || '';
    let sortOptions = sortString.split('&');
    sortArr = []; // clear previously selected options

    for (let i = 0; i < th.length; i++) { // loop through headers
      let e = angular.element(th[i]);

      e.removeClass('sort-desc'); // remove all icons
      e.removeClass('sort-asc');

      for (let item of sortOptions) {
        let itemSort: Array<string> = item
          .replace('sort=', '')
          .split(',');

        // first index contains value
        // second index contains the sort direction (asc / desc)
        if (e.attr('sort') === itemSort[0]) {

          sortArr.push({
            value: itemSort[0],
            dir: itemSort[1]
          });

          e.addClass(`sort-${itemSort[1]}`);
        }
      }
    }
  }

  /**
   * Add/remove element from sort or update direction.
   * 
   * @param e element to update
   */
  function updateElementSort(e: angular.IAugmentedJQuery): void {
    if (scope.disableSort) { return; };

    let dir = null;
    let found: boolean = false;
    let value: string = e.attr('sort');

    if (e.hasClass('sort-asc')) {
      e.removeClass('sort-asc');
      e.addClass('sort-desc');
      dir = 'desc';
    } else if (e.hasClass('sort-desc')) {
      e.removeClass('sort-desc');
    } else {
      e.addClass('sort-asc');
      dir = 'asc';
    }

    for (let i = 0; i < sortArr.length; i++) {
      if (sortArr[i].value === value) { // found
        if (dir !== null) {
          sortArr[i].dir = dir; // update the direction
        } else {
          sortArr.splice(i , 1); // remove it from array
        }
        found = true;
        break;
      }
    }

    if (!found) { // wasn't found, append it to `sortArr`
      sortArr.push({
        value: value,
        dir: dir
      });
    }

    sortTable();
  }

  /**
   * Update sort value by old value if exists.
   * 
   * @param newValue new value of sort item
   * @param oldValue old value of sort item
   */
  function updateSortValue(newValue: string, oldValue: string): boolean {

    // used to indicate if value sorted
    let isSorted: boolean = false;

    // only update if values exist and aren't equal
    if (newValue && oldValue && newValue !== oldValue) {
      // loop through each array value and update sort string if found
      for (let i = 0; i < sortArr.length; i++) {
        if (sortArr[i].value === oldValue) { // found
          sortArr[i].value = newValue;
          isSorted = true;
          break;
        }
      }
    }

    // true if sorting by updated sort item value
    return isSorted;
  }

  function sortTable(): void {
    sortString = buildQueryString(sortArr); // update string for retrieving
    scope.tableSort({ sort: sortString });
  }
}

function tableSortItemLinkFunc(scope: angular.IScope, elem: angular.IAugmentedJQuery, attrs: angular.IAttributes, tableSortCtrl: TableSortController): void {

  // parent table sort controller is required
  if (!tableSortCtrl) { return; }

  // if sort value, bind click event, add class and icon
  if (attrs.sort) {
    elem.bind('click', () => tableSortCtrl.updateElementSort(elem));
  }

  // update sort value if attribute changed
  let currentValue: string = attrs.sort || '';
  attrs.$observe('sort', (value: string) => {
    tableSortCtrl.updateSortValue(value, currentValue);
    currentValue = value;
  });
}
