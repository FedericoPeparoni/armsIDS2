/**
 * Override functionality from angular-progress-bar
 */

/** @ngInject */
export function LoadingBarConfig(cfpLoadingBarProvider: any): void {
    cfpLoadingBarProvider.startSize = 0.005;
    // we set auto increment to false so we can define our own increment
    cfpLoadingBarProvider.autoIncrement = false;
    cfpLoadingBarProvider.includeSpinner = true;
    cfpLoadingBarProvider.includeBar = false;
    cfpLoadingBarProvider.latencyThreshold = 1000;
    cfpLoadingBarProvider.parentSelector = '#loading-bar-container';
    cfpLoadingBarProvider.spinnerTemplate = '<div id="loading-bar-container"><div id="loading-bar-spinner"><i class="fa fa-spinner fa-pulse fa-5x fa-fw"></i><span class="sr-only">Loading...</span></div>';

   // cfpLoadingBarProvider.spinnerTemplate = '<div style="height: 100%; width:100%; position:absolute;z-index: 300; top: 0;bottom: 0; right: 0; left: 0"><div id="loading-bar-spinner"><div class="spinner-icon"></div></div>';

 // cfpLoadingBarProvider.spinnerTemplate = '<div id="loading-bar-container"><div id="loading-bar-spinner"><i class="fa fa-circle-o-notch fa-spin fa-3x fa-fw"><span class="sr-only">Loading...</span></i></div></div>';





}
