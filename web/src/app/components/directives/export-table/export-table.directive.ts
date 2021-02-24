// interface
import { IExportTableScope } from './export-table.interface';
import * as PDF from 'jspdf';
import { DownloadOauth2 } from '../download-oauth2/download-oauth2.class';
import { IDownloadOAuth2 } from '../download-oauth2/download-oauth2.interface';

/**
 * Export TABLE to CSV/XLS/PDF
 *
 * This directive is used in two ways.
 *
 * The first is used on a row containing a table or
 * data-grid, and creates a dropdown allowing creation
 * of files (currently .pdf/.csv/.xls ) in the specified formats
 *
 * The second is for a single button file generation that
 * generates .csv OR .xls files depending on the specified format
 *
 */

/** @ngInject */
export function exportTable(): angular.IDirective {

    return {
        restrict: 'A',
        scope: {
            chartJson: '<?',
            filename: '@',
            sorted: '<?', // if should sort alphabetically
            formats: '<?', // if used as a dropdown ['csv', 'xls', 'pdf']
            format: '@?', // if used a a single button csv OR xls (default csv)
            button: '<?', // specify if this is used as a single button
            columnsPerPage: '<?', // specify how many table columns per page on the pdf (default 13)
            columnWidth: '<?', // specify width of columns (default 63)
            rowHeight: '<?', // specify height of rows (default 26.6 for 20 rows per page)
            fontSize: '<?', // specify font size (default 6)
            boxHeader: '<?', // which box header to display the icon (default 0)
            exportTableId: '@?', // specify id of a table to export
            service: '@?',
            list: '=?', // specify a table data to export (if not making an api call and there no pages)
            queryString: '<?', // allows export to work with filters and pages. If not specified, adds all pages
            sortQueryString: '<?', // allows export to work with filters and pages. If not specified, adds all pages
            endpointParam: '@?', // may be used if different endpoint, for instance flightmovements/filters
            maximumRows: '<?', // maximum number of rows per pdf page. Default is 20
            csvHeader: '<?', // include CSV headers on exported file (default true)
            backEndExport: '<?'// indicate that CSV should be created on back-end (default false)
        },
        controller: ExportTable,
        compile: compileTableData
    };
}

/**
 * $scope.theaders are the table headers used for both
 * PDF and CSV generation
 *
 * $scope.tfields is an object that contains the properties
 * required to access row object properties. For instance,
 * if the account row propery is [Object object], it will check the tfields
 * and find {account: name}, then use it value to access the account name.
 * This is done in both exportCSV and exportPDF
 *
 * @param  {ng.IAugmentedJQuery} element
 * @returns Function
 */
function compileTableData(element: ng.IAugmentedJQuery, attrs: any): Function {
    let htmlString = element[0].innerText;
    let splitString = htmlString.toLocaleLowerCase().split('item.');
    let theaders = splitString.map((x: string) => x.substr(0, x.indexOf(' ')));
    let tfields = {};

    // these lines set and init a fixed 
    // header on every table with this directive 
    element[0].addEventListener('scroll', tableFixHead);
    const scroll = new Event('scroll');
    element[0].dispatchEvent(scroll);

    theaders.shift(); // we do not want all json data in the csv file, only the json displayed in the table
    theaders = theaders.map((x: string) => {
        // remove commas to prevent incorrect field name
        let split = x.replace(/,/, '').split('.');
        if (split[1]) {
            tfields[split[0]] = split[1];
        }
        return split[0];
    });

    return (scope: IExportTableScope) => {
        scope.tfields = tfields;
        scope.theaders = theaders;
        // sort alphabetically by default
        scope.sorted = attrs.sorted ? attrs.sorted : true;
    };
}

/** @ngInject */
export class ExportTable {

    constructor(private $scope: IExportTableScope, $element: any, $timeout: ng.ITimeoutService, $compile: ng.ICompileService,
        $templateRequest: ng.ITemplateRequestService, private $injector: ng.auto.IInjectorService, private $filter: ng.IFilterService,
        $translate: angular.translate.ITranslateService, private $uibModal: any, private Restangular: restangular.IService) {
        $scope.translate = $translate;

        // listens for trigger to scroll
        // this table to the top
        $scope.$on('scrollToTop', () => {
            $element[0].scrollTop = 0;
        });

        if ($scope.service) {
            $scope.getAllDataFromService = $injector.get($scope.service);
        }

        $scope.uibModal = this.$uibModal;

        $scope.formats = $scope.formats || ['csv'];
        $scope.backEndExport = $scope.backEndExport || false;

        $scope.getCsvFromBackend = () => this.getCsvFromBackend();

        $scope.exportCsv = (format: string) => {
            $scope.format = format || $scope.format || 'csv'; // dropdown / single / default
            if (this.$scope.chartJson) {
                this.exportCsv(this.$scope.chartJson, $scope.filename, $scope.tfields, $scope.theaders);
            } else {
                this.export('csv');
            }
        };
        $scope.exportPdf = () => this.export('pdf');

        $timeout(() => {
            if (!$scope.button) { // if this is a table, add the dropdown button
                $templateRequest('app/components/directives/export-table/export-table.html').then((temp: string) => {
                    const boxHeader = $scope.boxHeader || 0; // defaults to first box header
                    const template = angular.element(temp);
                    angular.element(document).ready(() => {
                        let exportButton = document.getElementsByClassName('export-button')[boxHeader];
                        if (exportButton) {
                            exportButton.innerHTML = ''; // safety so that only one is appended at a time
                            exportButton.appendChild($compile(template)($scope)[0]);
                        }
                    });
                });
            } else { // if used as a single button
                $element.on('click', () => $scope.exportCsv());
            }
        });
    }

    /**
     * If there is a different endpoint than the
     * one typically used, this function will modify it with
     * the endpointParam value that is
     * passed into the directive
     *
     * This method also combines the queryString
     * object into a queryString and adds it to
     * the endpoint
     *
     * @returns any // returns any service
     */
    private getFullService(): any { // may return any service
        let fullService;

        try {
            fullService = angular.copy(this.$scope.getAllDataFromService);
        } catch (err) {
            // can't copy user service, the error - Can't copy! Making copies of Window or Scope instances is not supported
            fullService = this.$injector.get(this.$scope.service);
            const index = fullService.endpoint.indexOf('?');
            if (index !== -1) {
                fullService.endpoint = fullService.endpoint.slice(0, index);
            }
        }

        if (!this.$scope.queryString && !this.$scope.sortQueryString && !this.$scope.endpointParam) {
            return fullService;
        } else {
            fullService.endpoint += `${this.$scope.endpointParam ? this.$scope.endpointParam : ''}?`;
        }

        if (this.$scope.queryString || this.$scope.backEndExport) {
            let queryString = angular.copy(this.$scope.queryString);
            let params: any = [];
            this.$scope.endpointParam = this.$scope.endpointParam || '';

            if (this.$scope.queryString) {
                for (let key in queryString) {
                    if (queryString.hasOwnProperty(key) && queryString[key] !== undefined && queryString[key] !== null) {
                        let page = queryString.page;
                        queryString.page = page > 0 ? page - 1 : page; // if page is greater than 0, minus 1 for proper paging
                        params.push(`${key}=${queryString[key]}`);
                    }
                }
            }
            if (this.$scope.backEndExport) {
                params.push('csvExport=true');
                // setting size=-1 to return all data
                const indexSize = params.findIndex((param: string) => param.startsWith('size'));
                if (indexSize > -1) {
                    params[indexSize] = 'size=-1';
                } else {
                    params.push('size=-1');
                }

                // setting page=0 to return data from a table from the first page
                const indexPage = params.findIndex((param: string) => param.startsWith('page'));
                if (indexPage > -1) {
                    params[indexPage] = 'page=0';
                } else {
                    params.push('page=0');
                }
            }
            queryString = params.join('&');
            fullService.endpoint += `${queryString}`;
        }


        if (this.$scope.sortQueryString) {
            fullService.endpoint += `&${this.$scope.sortQueryString}`;
        }

        return fullService;
    }


    /**
     * This either loops through the specified
     * number of pages, adds the data, then
     * exports to pdf or csv. OR if no pages
     * are specified, it exports all the data
     *
     * @returns void
     */
    private export(type: string): void {
        let fullService = this.getFullService();

        if (this.$scope.queryString) {
            let firstCall = fullService.list();

            firstCall.then((data: any) => { // any because it could be from any service
                fullService.endpoint = data.route; // makes sure the endpoint doesn't reset to the default
                let totalPromises = [];
                let totalPages = data.total_pages;
                let allData = data.content;
                let currentPage = data.number;
                let finalPage = totalPages - currentPage;
                let pagesToGo = finalPage - currentPage;

                if (totalPages > 10) {
                  let modal = this.$scope.uibModal.open({
                    animation: true,
                    templateUrl: 'app/components/directives/export-table/export-table-warning.template.html',
                    controller: ['$scope', ($scope: ng.IScope) => {
                      $scope.close = () => modal.close();
                    }]
                  });

                  totalPages = 10;
                  finalPage = totalPages - currentPage;
                  pagesToGo = finalPage - currentPage;
                };

                if (totalPages > 1) {
                  while (pagesToGo) {
                      if (currentPage > 0) {
                          pagesToGo--;
                          if (fullService.endpoint.indexOf('page') !== -1) { // if there is a page param
                              let newEndpoint = fullService.endpoint.replace(`page=${currentPage - 1}`, `page=${currentPage}`);
                              fullService.endpoint = newEndpoint;
                          }

                          totalPromises.push(fullService.list());
                      }
                      currentPage = currentPage + 1;
                  }
              }

              Promise.all(totalPromises).then((pages: any) => {
                  for (let page of pages) {
                      allData = [...allData, ...page.content];
                  }

                  // export set of pages to pdf or csv
                  type === 'pdf' ? this.exportPDF(allData) : this.exportCsv(allData, this.$scope.filename, this.$scope.tfields, this.$scope.theaders);

              });
            });
        } else {
            if (this.$scope.list) {
                this.checkListData(this.$scope.list);
                type === 'pdf' ? this.exportPDF(this.$scope.list) : this.exportCsv(this.$scope.list, this.$scope.filename, this.$scope.tfields, this.$scope.theaders);
            } else {
                fullService.listAll().then((allData: any) => {
                    let data = allData.content || allData;
                    // export all data to pdf or csv
                    type === 'pdf' ? this.exportPDF(data) : this.exportCsv(data, this.$scope.filename, this.$scope.tfields, this.$scope.theaders);
                });
            }
        }
    };

    private checkListData(list: Array<object>): void {
        list.forEach((element: object) => {
            for (let item in element) {
                if (element.hasOwnProperty(item)) {
                    if (item.includes('date_time')) {
                        element[item] = this.$filter('dateConverter')(element[item], 'HH:mm');
                    }
                    element[item] = element[item] === null ? '' : element[item];
                }
            }
        });
    }

    private snakeToCamel(snake: string): string {
      snake = snake.replace(/[\-_\s]+(.)?/g, ((match: string, ch: string) => {
        return ch ? ch.toUpperCase() : '';
      }));

      // ensure 1st char is always lowercase
      return snake.substr(0, 1).toLowerCase() + snake.substr(1);
    }

    /**
     * Find table header text value if sort attribute exists as
     * camel case version of header snake case.
     * 
     * @param header header property snake case
     * @param columns header columns (thead > th)
     */
    private getHeaderText(header: string, columns: any): string {

        // convert snake case header proeprty into camel case
        let camel = this.snakeToCamel(header);

        // loop through each table header column and parse for matching header
        for (let i = 0, n = columns.length; i < n; i++) {

            // sort attribute is used to match header value
            let sort = columns[i].attributes.sort;

            // skip if no sort attribute as it is required for matching
            if (!sort) {
                continue;
            }

            // get all table suffixes and split by comma
            // used to add additional text suffix
            let suffix = columns[i].attributes['export-table-suffix'];
            let suffixes = suffix ? suffix.value.split(',') : [];

            // loop through each sort value and return header text with suffix if camel case matches
            let sorts = sort.value.split(',');
            for (let ii = 0; ii < sorts.length; ii++) {

                // continue if sort does not match camel case
                if (sorts[ii].split('.')[0] !== camel) {
                    continue;
                }

                // return translated text as sort innerText and any suffixes
                let text = this.$scope.translate.instant(columns[i].innerText);
                if (suffixes[ii]) {
                    text = `${text} ${this.$scope.translate.instant(suffixes[ii])}`;
                }

                return text;
            }
        }
    }

    private exportPDF(data: Array<Object>): void {
        let allData = data;

        // the first object of the row will be the header object
        this.$scope.headerObj = {};

        let columns = angular.element(document).find('thead').find('th');

        for (let header of this.$scope.theaders) {
          let result = this.getHeaderText(header, columns);

          if (result) {
            this.$scope.headerObj[header] = result;
          }
        }

        for (let item of allData) {
            for (let key in item) {
                if (item.hasOwnProperty(key)) {
                    if (item[key] === null) {
                        item[key] = '';
                    } else if (typeof item[key] !== 'object') {
                        item[key] = this.$scope.translate.instant(String(item[key]));
                    }

                    if (!this.$scope.theaders.includes(key)) {
                        delete item[key];
                    } else if (this.$scope.tfields.hasOwnProperty(key)) {
                        item[key] = this.$scope.translate.instant(item[key][this.$scope.tfields[key]]); // if it is an object, get the property
                    }
                }
            }
        }

        allData.unshift(this.$scope.headerObj); // add headers to beginning of rows

        let columnsPerPage = this.$scope.columnsPerPage || 13;
        let cellWidth = this.$scope.columnWidth || 63;
        let fontSize = this.$scope.fontSize || 6;
        let pdf = new PDF('landscape', 'pt', 'a4');
        let rowHeight = this.$scope.rowHeight || 26.6;
        let paddingLeft = 10;
        let paddingTop = 20;

        pdf.cellInitialize();

        this.addColumnsToPdf(pdf, allData, paddingLeft, paddingTop, cellWidth, rowHeight, columnsPerPage, fontSize);
        pdf.save(`${this.$scope.filename}.pdf`);
    }

    /**
     *
     * PDF Files
     *
     * This is for the style of the pdf rows/header
     * It will add the number of specified table columns
     * and then add a new page and continue if there are
     * further columns
     *
     * @param  {any} pdf - this is the jspdf object
     * @param  {Array<Object>} rows
     * @param  {number} paddingLeft
     * @param  {number} paddingTop
     * @param  {number} cellWidth
     * @param  {number} rowHeight
     * @param  {number} columnsPerPage
     * @returns void
     */
    private addColumnsToPdf(pdf: any, rows: Array<Object>, paddingLeft: number, paddingTop: number, cellWidth: number, rowHeight: number, columnsPerPage: number, fontSize: number, captured?: Object[]): void {
        let nextPage = [];
        let is = [];
        let excess = captured || [];
        const maxCellCharLength = 75;
        const headers = this.$scope.sorted ? Object.keys(rows[0]).sort() : Object.keys(rows[0]);

        let addNewPage = () => { // adds a new page to the pdf document
            pdf.addPage();
            pdf.setTableHeaderRow({});
            pdf.setHeaderFunction(() => { return [10, 20, 0, 0]; }); // this resets the cell margins so that it starts at the top of new page
            pdf.printHeaderRow();
        };

        /*
         * Filter out empty rows
         * A row will be empty if it is filtered out using query params.
         * This prevents multiple blank pages.
         */
        rows = rows.filter((row: Object) => Object.keys(row).length);

        rows.forEach((row: Object, i: number) => {
            let column = 0;
            let maximumRows = this.$scope.maximumRows || 20;
            row = this.createTableRow(headers, row);

            if (i > maximumRows) {
                is.push(row);
            }

            for (let key in row) {
                if (row.hasOwnProperty(key)) {

                    column++;

                    pdf.setFontStyle('helvetica');
                    pdf.setFontSize(fontSize);
                    pdf.setTextColor(0, 0, 0);
                    pdf.printingHeaderRow = false;

                    if (i === 0) { // header row
                        pdf.printingHeaderRow = true;
                        pdf.setFontStyle('bold');
                        pdf.setTextColor(255, 255, 255); // text color
                        pdf.setFillColor(51, 122, 183); // background color
                        pdf.setDrawColor(211, 211, 211); // table outline color
                    };

                    if (column > columnsPerPage && column <= Object.keys(row).length && i <= maximumRows) { // after specified # of columns per page reached, push remainder of row to a new array
                        if (nextPage[i] === null || nextPage[i] === undefined) {
                            nextPage[i] = {};
                        }
                        Object.assign(nextPage[i], { [key]: row[key] });
                    } else if (i <= maximumRows) {
                        let cellText = row[key];

                        if (cellText === '') { cellText = '\ '; } // if empty string, escape a space so that it still creates a table cell
                        if (angular.isString(cellText)) { cellText = cellText.replace(/(\r\n|\n|\r)/gm, ' '); }; // remove any line breaks before placing in the cell

                        if (angular.isArray(cellText)) {
                            let joinedCellText = cellText.join('\n');
                            if (joinedCellText.length > maxCellCharLength) {
                                cellText = joinedCellText.substring(0, maxCellCharLength) + '...'; // max 75 characters per table cell, at smallest font
                            }
                        };

                        pdf.cell(paddingLeft, paddingTop, cellWidth, rowHeight, cellText, i);
                    }
                }
            }
        });

        if (nextPage.length > 0) { // create a new page and add the remaining columns
            addNewPage();
            this.addColumnsToPdf(pdf, nextPage, paddingLeft, paddingTop, cellWidth, rowHeight, columnsPerPage, fontSize, excess); // calls recursively until no new pages needed
        }

        if ([...excess, ...is].length > 0) { // no more columns left, but rows to be added.
            excess = [...excess, ...is];
            addNewPage();
            excess.unshift(this.$scope.headerObj);
            this.addColumnsToPdf(pdf, excess, paddingLeft, paddingTop, cellWidth, rowHeight, columnsPerPage, fontSize, []);
        }
    }

    /**
     *
     * CSV / XSL Files
     *
     * If this directive is not used with a table it
     * will use the JSON provided for the CSV file
     *
     * If this directive is used with a table, it will loop
     * through the provided JSON and delete anything
     * not in the table before it creates the CSV File
     *
     * @param  {Array<Object>} json
     * @param  {string} filename
     * @param  {object} obj
     * @param  {any} th
     * @returns void
     */
    private exportCsv(jsonData: Array<Object>, filename: string, obj: object, th: any): void {

      let includeHeader = this.$scope.csvHeader === undefined ? true : this.$scope.csvHeader;
      let row = '';
      let CSV = '';

      if (!this.$scope.button) { // only used for tables
        for (let key in obj) {
          if (obj.hasOwnProperty(key)) {
            for (let item of jsonData) {
              if (item[key] === null) {
                item[key] = '';
              }
              item[key] = item[key][obj[key]]; // changes [object Object] to the correct value
            }
          }
        }

        for (let item of jsonData) {
          for (let key in item) {
            if (item.hasOwnProperty(key)) {
              if (item[key] && item[key].length > 0 && item[key].constructor === Array) {
                item[key] = item[key].join('; ');  // if any props are an array, turn it into a string, otherwise it may misalign the exported csv
              }
              if (!th.includes(key)) {
                delete item[key]; // remove json key/value pairs that are not inside the table
              }
            }
          }
        }
      }

      for (let index in jsonData[0]) { // this loop will extract the label from 1st index of on
        if (jsonData[0].hasOwnProperty(index)) {
          row += `${index},`; // now convert each value to string and comma-seprated
        }
      }

      row = row.slice(0, -1);

      if (includeHeader) {
        let columns;
        if (this.$scope.exportTableId) {
            columns = angular.element(document.getElementById(this.$scope.exportTableId)).find('thead').find('th');
        } else {
            columns = angular.element(document).find('thead').find('th');
        }
        let headerRow = row.split(',');
        let output = [];

        for (let i = 0, n = headerRow.length; i < n; i++) {
            output.push(this.getHeaderText(headerRow[i], columns));
        }

        CSV += `${output.join(',')}\r\n`; // append header row with line break
      }

      for (let i = 0; i < jsonData.length; i++) { // 1st loop is to extract each row
        let row = '';

        for (let index in jsonData[i]) { // 2nd loop will extract each column and convert it in string comma-separated
          if (jsonData[i].hasOwnProperty(index)) {
            row += this.$scope.translate.instant(`${jsonData[i][index]}`) + ',';
          }
        }

        row.slice(0, row.length - 1);
        CSV += `${row}\r\n`; // add a line break after each row
      }

      if (CSV === '') {
        console.error('Invalid data');
        return;
      }

      filename = filename.replace(/ /g, '_'); // if blank spaces in filename, they are replaced with underscores

      // handle EDGE and IE (do not support download attribute on <a> tag)
      if (window.navigator.msSaveBlob) {
          window.navigator.msSaveBlob(new Blob([CSV]), filename);
          return;
      }

      let uri = `data:text/${this.$scope.format};charset=utf-8,${encodeURI(CSV)}`;
      let link = angular.element('<a></a>');

      link.attr('href', uri);
      link.attr('download', `${filename}.${this.$scope.format}`);

      // handle CHROME and FIREFOX
      this.click(link[0]);
    }

    private click(target: any): void {
        const event = typeof(Event) === 'function'
        ? new MouseEvent('click')
        : document.createEvent('click');

        target.dispatchEvent(event);
    }

    private createTableRow(headers: Array<String>, object: Object): Object {
        return headers.reduce((tableRow: Object, header: string) => {
            if (object[header] === undefined) {
                  object[header] = '';
                }
            tableRow[header] = object[header];
            return tableRow;
        }, {});
    }

    private getCsvFromBackend(): void {
        const downloadScope = this.setDownloadOauth2();
        const downloadOAuth2 = new DownloadOauth2(downloadScope, this.Restangular);
        downloadOAuth2.generate();
    }

    private setDownloadOauth2(): IDownloadOAuth2 {
        return {
            url: this.getFullService().endpoint,
            requestMethod: 'GET'
        };
    }
}

/**
 * Because this directive is already on
 * every table. Here we add fixed table headers
 */
function tableFixHead(e: any): void {
    const el = e.target;
    const sT = el.scrollTop;

    el.querySelectorAll('thead').forEach((thead: HTMLElement) => {
        thead.style.transform = `translateY(${sT}px)`;
        thead.style.background = `white`;
        thead.style.backgroundClip = `padding-box`;
    });

    el.querySelectorAll('th').forEach((th: HTMLElement) => {
        th.style.background = 'white';
        th.style.backgroundClip = `padding-box`;
    });
}
