/**
 * Override functionality from uib-pagination.
 */

 /** @ngInject */
export function PaginationConfig(uibPaginationConfig: any): void {
    uibPaginationConfig.previousText = '<';
    uibPaginationConfig.nextText = '>';
    uibPaginationConfig.firstText = '<<';
    uibPaginationConfig.lastText = '>>';
}
