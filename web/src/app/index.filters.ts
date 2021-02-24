// filters
import { dateConverter } from '../app/angular-ids-project/src/components/filters/dateConverter/dateConverter.filter';
import { weightConverter } from '../app/angular-ids-project/src/components/filters/weightConverter/weightConverter.filter';
import { distanceConverter } from '../app/angular-ids-project/src/components/filters/distanceConverter/distanceConverter.filter';
import { longitudeConverter } from '../app/angular-ids-project/src/components/filters/longitudeConverter/longitudeConverter.filter';
import { latitudeConverter } from '../app/angular-ids-project/src/components/filters/latitudeConverter/latitudeConverter.filter';
import { abs } from '../app/angular-ids-project/src/components/filters/abs/abs.filter';
import { evenRound } from '../app/angular-ids-project/src/components/filters/evenRound/evenRound.filter';
import { customRound } from '../app/angular-ids-project/src/components/filters/customRound/customRound.filter';
import { highlight } from '../app/angular-ids-project/src/components/filters/highlight/highlight.filter';
import { hideCurrent } from '../app/angular-ids-project/src/components/filters/hideCurrent/hideCurrent.filter';
import { hideHigher } from '../app/angular-ids-project/src/components/filters/hideHigher/hideHigher.filter';
import { hideLower } from '../app/angular-ids-project/src/components/filters/hideLower/hideLower.filter';
import { systemConfigurationItemLabel } from '../app/components/filters/system-configuration-item-label/system-configuration-item-label.filter';

// filter module
export default angular.module('armsWeb.filters', [])
    .filter('dateConverter', dateConverter)
    .filter('weightConverter', weightConverter)
    .filter('distanceConverter', distanceConverter)
    .filter('longitudeConverter', longitudeConverter)
    .filter('latitudeConverter', latitudeConverter)
    .filter('abs', abs)
    .filter('evenRound', evenRound)
    .filter('customRound', customRound)
    .filter('highlight', highlight)
    .filter('hideCurrent', hideCurrent)
    .filter('hideHigher', hideHigher)
    .filter('hideLower', hideLower)
    .filter('systemConfigurationItemLabel', systemConfigurationItemLabel);
