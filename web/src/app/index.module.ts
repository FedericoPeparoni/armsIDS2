import AppControllers from './index.controllers';
import AppServices from './index.services';
import AppDirectives from './index.directives';
import AppFilters from './index.filters';
import AppConfigs from './index.config';
import AppRun from './index.run';
import AppPlugins from './index.plugins';

module armsWeb {
  'use strict';

  angular.module('armsWeb', [
    'ngCookies',
    'ngTouch',
    'ngSanitize',
    'ngMessages',
    'ngAria',
    'restangular',
    'ui.router',
    'ui.bootstrap',
    'gridshore.c3js.chart',
    'satellizer',
    'angularjs-dropdown-multiselect',
    'ngFileUpload',
    'angularCesium',
    'angular-loading-bar',
    'pascalprecht.translate',
    'vcRecaptcha',
    'angular.filter',
    AppControllers.name,
    AppServices.name,
    AppDirectives.name,
    AppFilters.name,
    AppConfigs.name,
    AppRun.name,
    AppPlugins.name
  ]);
}
