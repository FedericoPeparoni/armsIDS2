import { IDownloadOAuth2 } from './download-oauth2.interface';
import { IExtendableError } from '../../../angular-ids-project/src/helpers/interfaces/restangularError.interface';

export class DownloadOauth2 {

    private Restangular: restangular.IService;
    private $scope: IDownloadOAuth2;

    constructor (scope: IDownloadOAuth2, Restangular: restangular.IService) {
        this.$scope = scope;
        this.Restangular = Restangular;
    }

    public generate(): void {
        const request: ng.IPromise<any> = this.getRequestMethod();

        request.then((resp: any) => {
            const filename: string = this.parseFileName(resp);
            const blob = this.generateBlob(resp);
            const browser = this.determineBrowser();

            if (browser === 'IE') {
                window.navigator.msSaveBlob(blob, filename);
            } else if (browser === 'CHROME') {
                this.generateChromeDownload(blob, filename);
            } else {
                this.generateOtherDownload(blob, filename);
            };

            if (this.$scope.callbackSuccessFn) {
                this.$scope.callbackSuccessFn();
            };
        }).catch((error: { data: ArrayBuffer }) => {
            this.$scope.error = <IExtendableError>{};
            let decodedString = String.fromCharCode.apply(null, new Uint8Array(error.data)); // must be decoded as error returns in an arraybuffer
            let errorObj = JSON.parse(decodedString);
            this.$scope.error.error = { data: errorObj }; // tie into current error handling
            if (this.$scope.callbackErrorFn) {
                this.$scope.callbackErrorFn();
            }
        });
    }

    /**
     * Construct a restangular promise based on parameters that returns full headers
     *
     * @return  {ng.IPromise<any>}  A restangular request
     */
    private getRequestMethod(): ng.IPromise<any> {
        const rest = this.Restangular.withConfig((RestangularConfigurer: restangular.IProvider) => RestangularConfigurer.setFullResponse(true));
        const req = rest.one(this.$scope.url).withHttpConfig({ responseType: 'arraybuffer' });
        const method = this.$scope.requestMethod;

        if (method === 'GET') {
            return req.get(this.$scope.params);
        } else {
            let body: any = this.$scope.bodyParamsFn ? this.$scope.bodyParamsFn(this.$scope.bodyParams) : this.$scope.bodyParams;
            return req.customPOST(body, null, this.$scope.params);
        };
    }

    /**
     * Construct the filename from the response, removing superfluous characters
     *
     * @param   {ng.IPromise<any>}  resp  response from the server
     * @return  {string}                  filename
     */
    private parseFileName(resp: any): string {
        const headers = resp.headers();
        const disposition = headers['content-disposition'];
        const filenameRegex = /filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/;
        const matches = filenameRegex.exec(disposition);

        let filename: string = null;

        if (disposition && disposition.indexOf('attachment') !== -1) {
            if (matches !== null && matches[1]) {
                filename = matches[1].replace(/['"]/g, '');
            };
        };

        return filename;
    }

    /**
     * Generate a blob for the file object
     *
     * @param   {ng.IPromise<any>}  resp  response from the server
     * @return  {Blob}                    file object
     */
    private generateBlob(resp: any): Blob {
        const headers = resp.headers();
        const contentType = headers['content-type'];

        return new Blob([resp.data], { type: contentType });
    }

    /**
     * Determine which browser is being used
     *
     * @return  {string}  file object
     */
    private determineBrowser(): string {
        if (navigator.msSaveBlob) {
            return 'IE';
        } else if (document.createEvent) {
            return 'CHROME';
        } else {
            return 'OTHER';
        };
    }

    /**
     * Construct the download for chrome
     *
     * @param   {Blob}    blob      file object
     * @param   {string}  filename  name of file to save
     */
    private generateChromeDownload(blob: Blob, filename: string): void {
        const target = this.buildTarget(blob, filename);
        const ev = document.createEvent('MouseEvent');

        ev.initMouseEvent('click', true, true, window, null, 0, 0, 0, 0, false, false, false, false, 0, null);

        target[0].dispatchEvent(ev);
    }

    /**
     * Construct the download for other browsers
     *
     * @param   {Blob}    blob      file object
     * @param   {string}  filename  name of file to save
     */
    private generateOtherDownload(blob: Blob, filename: string): void {
        const target = this.buildTarget(blob, filename);

        target[0].fireEvent('onclick');
    }

    /**
     * Creaet the target <a/> element
     *
     * @param   {Blob}    blob      file object
     * @param   {string}  filename  name of file to save
     */
    private buildTarget(blob: Blob, filename: string): any {
        let target: any = angular.element('<a/>');

        target.attr({
            href: window.URL.createObjectURL(blob),
            target: '_blank',
            download: filename
        });

        return target;
    }
}
