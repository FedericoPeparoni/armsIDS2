export interface IExportTableScope extends ng.IScope {
    chartJson: Array<Object>;
    filename: string;
    exportCsv: Function;
    exportPdf: Function;
    formats: Array<string>;
    format: string;
    tfields: Object;
    button: boolean;
    theaders: any; // todo change to Array<string> after support implemented for includes
    columnsPerPage: number;
    columnWidth: number;
    fontSize: number;
    boxHeader: number;
    service: string;
    sorted: boolean;
}