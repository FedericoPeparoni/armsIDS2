/**
 * Override functionality from angular-progress-bar
 */

/** @ngInject */
export function LoadingBarConfig(cfpLoadingBarProvider: any): void {
    cfpLoadingBarProvider.startSize = 0.005;
    // we set auto increment to false so we can define our own increment
    cfpLoadingBarProvider.autoIncrement = false;
    cfpLoadingBarProvider.includeSpinner = false;
}
