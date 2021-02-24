// interface
import { IExtendableError } from '../../../angular-ids-project/src/helpers/interfaces/restangularError.interface';
import * as PDF from 'jspdf';

/**
 * Exports HTML Elements to PDF Format
 *
 * This directive was built specifically for c3 charts
 * However, it should work with other SVGs
 *
 */
/** @ngInject */
export function exportPdf(): angular.IDirective {

    return {
        restrict: 'E',
        templateUrl: 'app/components/directives/export-pdf/export-pdf.html',
        scope: {
            element: '@', // svg tag or id of HTML element one wants to export to PDF
            filename: '@',
            orientation: '@?', // portrait or landscape
            disable: '=?',
            error: '=',
            text: '@'
        },
        transclude: true,
        controller: ExportPdf
    };
}

interface IExportPdf {
    element: string;
    filename: string;
    error: IExtendableError;
    exportPdf: Function;
    format: string;
    orientation: string;
}

/** @ngInject */
export class ExportPdf {

    constructor(private $scope: IExportPdf) {
        $scope.exportPdf = () => this.exportPdf();
        $scope.error = <IExtendableError>{};
        $scope.filename = $scope.filename.replace(/ /g, '_'); // if blank spaces in filename, they are replaced with underscores
        $scope.orientation = 'portrait' || $scope.orientation;
    }

    private exportPdf(): void {

        // start c3 chart specific code
        let labels = document.getElementsByClassName('c3-legend-item');
        if (labels) {
            for (let i = 0; i < labels.length; i++) {
                labels[i].setAttribute('font-size', '11px'); // this makes it so the chart labels do not over-lap
            }
        } // end

        let svg = document.getElementsByTagName(this.$scope.element)[0] || document.getElementById(this.$scope.element);
        let svg_xml = (new XMLSerializer()).serializeToString(svg);
        let blob = new Blob([svg_xml], { type: 'image/svg+xml;charset=utf-8' });
        let url = window.URL.createObjectURL(blob);
        let img = new Image();

        img.onload = () => {
            let canvas = document.createElement('canvas');
            canvas.width = img.width;
            canvas.height = img.height;

            let ctx = canvas.getContext('2d');
            ctx.drawImage(img, 0, 0, img.width, img.height);

            window.URL.revokeObjectURL(url);
            let canvasdata = canvas.toDataURL('image/png');

            let pdf = new PDF(this.$scope.orientation);
            pdf.addImage(canvasdata, 'PNG', 5, 50, 285, 60);
            pdf.save(`${this.$scope.filename}.pdf`);
        };

        img.src = url;
    }
}
