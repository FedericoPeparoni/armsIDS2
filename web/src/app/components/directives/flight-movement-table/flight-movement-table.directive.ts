// controllers
import { FlightMovementTableController } from './flight-movement-table.controller';

/** @ngInject */
export function flightMovementTable(): angular.IDirective {
    return {
        restrict: 'E',
        templateUrl: 'app/components/directives/flight-movement-table/flight-movement-table.directive.html',
        controller: FlightMovementTableController,
        controllerAs: 'flightMovementTableCtrl'
    };
}
