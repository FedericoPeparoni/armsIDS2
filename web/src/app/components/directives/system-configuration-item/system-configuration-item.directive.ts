// controllers
import { SystemConfigurationItemController } from './system-configuration-item.controller';

/** @ngInject */
export function systemConfigurationItem(): angular.IDirective {

    return {
        restrict: 'E',
        templateUrl: 'app/components/directives/system-configuration-item/system-configuration-item.template.html',
        controller: SystemConfigurationItemController,
        controllerAs: 'sytemConfigurationItemCtrl',
        scope: {
            item: '=',
            pluginKey: '=?',
            list: '='
        }
    };
}
