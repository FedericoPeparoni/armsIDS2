import { LocalStorageService } from '../localStorage/localStorage.service';

/**
 * This service can be used to save data, filter,
 * and sorting templates on different UIs
 *
 * It requires the UI to have the cannedSort
 * attribute on the data-grid table
 * eg: canned-sort=cannedSort
 */

export class SaveLocalTemplateService {

    private scope: any;
    private moduleName: string;
    private filterPairs: string[][];

    /** @ngInject */
    constructor(private $rootScope: ng.IRootScopeService, private $window: ng.IWindowService) {
        this.setupEventListeners();
    }

    /**
     * Use to save data, filter, and sorting between page changes.
     * Note, all parameters are required.
     *
     * The module's scope
     * Usually just this.scope
     * @param  {angular.IScope} scope
     *
     * The module name
     * Must correspond with the state name
     * eg: if state name is main.radar-summary
     * pass in 'radar-summary'
     * @param  {string} moduleName
     *
     * Two dimensional array
     * Each array has a pair of strings
     * eg: ['filterModelName', 'filterParam']
     * Do not include pagination or date range pairs
     * See flightmovements for an example of this array
     * @param  {string[][]} filterPairs
     */
    public saveLocalTemplate(scope: any, moduleName: string, filterPairs: string[][]): void {
        this.scope = scope;
        this.moduleName = moduleName;
        this.filterPairs = filterPairs;

        // watch for list change and apply previous state
        // from local storage, remove watch once complete
        let clearWatch = this.scope.$watch('list', () => {
            if (this.scope.list !== undefined) {
                this.getAndApplyTemplate();
                clearWatch();
            }
        });
    }

    /**
     * Apply preivous state from local storage and refresh. This causes list to
     * refresh a second time with previous state.. should fine another approach.
     */
    private getAndApplyTemplate(): void {

        // returns the data, filters, and queryString from local storage
        let editable = LocalStorageService.get(`${this.moduleName}Data`);
        let filters = LocalStorageService.get(`${this.moduleName}Filters`);
        let queryString = LocalStorageService.get(`${this.moduleName}QueryString`);

        if (editable) {
            this.scope.editable = editable;
        }

        // if there is a dateRange, apply UTC time
        if (filters && filters.start && this.scope.control ) {
            let startDate = new Date(filters.start);
            this.scope.control.setUTCStartDate(startDate);
        }

        if (filters && filters.end && this.scope.control) {
            let endDate = new Date(filters.end);
            this.scope.control.setUTCEndDate(endDate);
        }

        if (filters || queryString) {

            // fills in the filter inputs
            for (let filterPair of this.filterPairs) {
                this.scope[filterPair[0]] = filters[filterPair[1]];
            }

            // sets the table sorting icon
            this.scope.cannedSort = queryString;

            // call the endpoint with filters and queryString
            this.scope.refresh(filters, this.scope.cannedSort);
        }
    }

    /**
     * Save current template state to local storage for use latter.
     */
    private saveTemplate(): void {

        // require local fields to be set from preivous state
        if (!this.scope || !this.moduleName || !this.filterPairs) {
            return;
        }

        let filters = <any>{};

        // gets scope values
        // adds to to filters object
        // keys are query params, values are query values eg: {search : 'FAOR'}
        for (let filterPair of this.filterPairs) {
            filters[filterPair[1]] = this.scope[filterPair[0]];
        }

        // if there is a page number, add it to the filters object
        if (this.scope.pagination && this.scope.pagination.number) {
            filters.page = this.scope.pagination.number;
        }

        // if there is a date range, add dates to filters object
        if (this.scope.control && this.scope.control.getUTCStartDate()) {
            filters.start = this.scope.control.getUTCStartDate().toISOString().substr(0, 10);
        }

        if (this.scope.control && this.scope.control.getUTCEndDate()) {
            filters.end = this.scope.control.getUTCEndDate().toISOString().substr(0, 10);
        }

        // save data, filters, and queryString to local storage
        LocalStorageService.set(`${this.moduleName}Data`, this.scope.editable);
        LocalStorageService.set(`${this.moduleName}Filters`, filters);
        LocalStorageService.set(`${this.moduleName}QueryString`, (typeof this.scope.getSortQueryString === 'function')
            ? this.scope.getSortQueryString() : this.scope.getSortQueryString);
    }

    /**
     * Use this function on construction to add necessary state event listeners - should only be done once.
     */
    private setupEventListeners(): void {

        // save template if name matches from state and always clear fields to keep fresh between state changes
        this.$rootScope.$on('$stateChangeStart', (event: ng.IAngularEvent, toState: any, toParams: any, fromState: any, fromParams: any) => {

            if (this.moduleName && fromState.name === `main.${this.moduleName}`) {
                this.saveTemplate();
            }

            // clear fields on every state change
            delete this.scope;
            delete this.moduleName;
            delete this.filterPairs;
        });

        // save template when window closed only if fields set
        this.$window.addEventListener('beforeunload', () => this.saveTemplate());
    }
}
