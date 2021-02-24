// directives
import { headerBar } from '../app/angular-ids-project/src/components/headerBar/headerBar.directive';
import { sideBar, sideBarCategory, sideBarLink } from '../app/angular-ids-project/src/components/sideBar/sideBar.directive';
import { boxHeader } from '../app/angular-ids-project/src/components/boxHeader/boxHeader.directive';
import { download } from '../app/angular-ids-project/src/components/download/download.directive';
import { dynamicChart } from '../app/angular-ids-project/src/components/dynamicChart/dynamicChart.directive';
import { footer } from '../app/angular-ids-project/src/components/footer/footer.directive';
import { dateRange } from '../app/angular-ids-project/src/components/dateRange/dateRange.directive';
import { notImplemented } from '../app/angular-ids-project/src/components/notImplemented/notImplemented.directive'; // upon completion of this project, this can be removed
import { password } from '../app/angular-ids-project/src/components/password/password.directive';
import { title } from '../app/angular-ids-project/src/components/title/title.directive';
import { tableSort, tableSortItem } from '../app/angular-ids-project/src/components/tableSort/tableSort.directive';
import { datePicker } from '../app/angular-ids-project/src/components/datePicker/datePicker.directive';
import { errorHandling } from '../app/angular-ids-project/src/components/errorHandling/errorHandling.directive';
import { input } from '../app/angular-ids-project/src/components/input/input.directive';
import { select } from './angular-ids-project/src/components/select/select.directive';
import { popup } from '../app/angular-ids-project/src/components/popup/popup.directive';
import { dynamicServiceDropdown } from '../app/angular-ids-project/src/components/dynamicServiceDropdown/dynamicServiceDropdown.directive';
import { downloadOauth2 } from '../app/components/directives/download-oauth2/download-oauth2.directive';
import { previewOauth2 } from '../app/components/directives/preview-oauth2/preview-oauth2.directive';
import { exportTable } from '../app/components/directives/export-table/export-table.directive';
import { exportPdf } from '../app/components/directives/export-pdf/export-pdf.directive';
import { modalMovable } from '../app/angular-ids-project/src/components/modalMovable/modalMovable.directive';
import { flightMovementForm } from './components/directives/flight-movement-form/flight-movement-form.directive';
import { flightMovementTable } from './components/directives/flight-movement-table/flight-movement-table.directive';
import { uploadFeedback } from './components/directives/upload-feedback/upload-feedback.directive';
import { lineItemsTable } from './components/directives/line-items-table/line-items-table.directive';
import { payment } from './components/directives/payment/payment.directive';
import { multiselect } from './components/directives/multiselect/multiselect.directive';
import { onlyNumbers } from '../app/angular-ids-project/src/components/onlyNumbers/onlyNumbers.directive';
import { onlyLetters } from '../app/angular-ids-project/src/components/onlyLetters/onlyLetters.directive';
import { emptyToNull } from '../app/angular-ids-project/src/components/emptyToNull/emptyToNull.directive';
import { convertToTons } from '../app/angular-ids-project/src/components/convertToTons/convertToTons.directive';
import { convertCoordinates } from '../app/angular-ids-project/src/components/convertCoordinates/convertCoordinates.directive';
import { rejectedRecords } from './components/directives/rejected-records/rejected-records.directive';
import { aerodromeIdentifiers } from '../app/angular-ids-project/src/components/aerodromeIdentifiers/aerodromeIdentifiers.directive';
import { unitConversionInput } from '../app/components/directives/unit-conversion-input/unitConversionInput.directive';
import { unitConverter } from '../app/angular-ids-project/src/components/unitConverter/unitConverter.directive';
import { copyToClipboard } from './angular-ids-project/src/components/copyToClipboard/copyToClipboard.directive';
import { systemConfigurationItem } from './components/directives/system-configuration-item/system-configuration-item.directive';
import { booleanText } from './components/directives/boolean-text/boolean-text.directive';
import { recaptcha } from '../app/angular-ids-project/src/components/recaptcha/recaptcha.directive';
import { externalDatabaseInput } from './components/directives/external-database-input/external-database-input.directive';
import { paginationDisplay } from './components/directives/pagination-display/pagination-display.directive';
import { uploadFileFormat } from '../app/angular-ids-project/src/components/uploadFileFormat/uploadFileFormat.directive';
import { uppercaseInput } from './components/directives/uppercase-input/uppercase-input.directive';
import { summaryUpload } from './components/directives/summary-upload/summary-upload.directive';

// directive module
export default angular.module('armsWeb.directives', [])
    .directive('headerBar', headerBar)
    .directive('sideBar', sideBar)
    .directive('sideBarCategory', sideBarCategory)
    .directive('sideBarLink', sideBarLink)
    .directive('boxHeader', boxHeader)
    .directive('download', download)
    .directive('dynamicChart', dynamicChart)
    .directive('footer', footer)
    .directive('dateRange', dateRange)
    .directive('notImplemented', notImplemented)
    .directive('password', password)
    .directive('title', title)
    .directive('tableSort', tableSort)
    .directive('sort', tableSortItem)
    .directive('datePicker', datePicker)
    .directive('errorHandling', errorHandling)
    .directive('input', input)
    .directive('select', select)
    .directive('popup', popup)
    .directive('dynamicServiceDropdown', dynamicServiceDropdown)
    .directive('downloadOauth2', downloadOauth2)
    .directive('previewOauth2', previewOauth2)
    .directive('exportTable', exportTable)
    .directive('exportPdf', exportPdf)
    .directive('modalMovable', modalMovable)
    .directive('flightMovementForm', flightMovementForm)
    .directive('flightMovementTable', flightMovementTable)
    .directive('uploadFeedback', uploadFeedback)
    .directive('lineItemsTable', lineItemsTable)
    .directive('payment', payment)
    .directive('multiselect', multiselect)
    .directive('onlyNumbers', onlyNumbers)
    .directive('onlyLetters', onlyLetters)
    .directive('emptyToNull', emptyToNull)
    .directive('convertToTons', convertToTons)
    .directive('convertCoordinates', convertCoordinates)
    .directive('rejectedRecords', rejectedRecords)
    .directive('aerodromeIdentifiers', aerodromeIdentifiers)
    .directive('unitConversionInput', unitConversionInput)
    .directive('unitConverter', unitConverter)
    .directive('copyToClipboard', copyToClipboard)
    .directive('systemConfigurationItem', systemConfigurationItem)
    .directive('booleanText', booleanText)
    .directive('recaptcha', recaptcha)
    .directive('externalDatabaseInput', externalDatabaseInput)
    .directive('paginationDisplay', paginationDisplay)
    .directive('uploadFileFormat', uploadFileFormat)
    .directive('uppercaseInput', uppercaseInput)
    .directive('summaryUpload', summaryUpload);
