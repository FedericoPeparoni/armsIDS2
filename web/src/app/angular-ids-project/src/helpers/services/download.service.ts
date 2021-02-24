export abstract class DownloadService {

  constructor(private $sce: ng.ISCEService) { }

  public getPreview(resp: any): any {
    const headers = resp.headers();
    const contentType = headers['content-type'];
    const blob = new Blob([resp.data], { type: contentType });
    const objectUrl = URL.createObjectURL(blob);
    const iframe = `<iframe src="${objectUrl}" style="width: 100%; height: 800px;"></iframe>`;

    return this.$sce.trustAsHtml(iframe);
  }

  public getDownload(resp: any): any {
    const headers: any = resp.headers();
    const contentType: string = headers['content-type'];
    const disposition: string = headers['content-disposition'];
    const blob = new Blob([resp.data], { type: contentType });
    let filename: string;

    // removes superfluous part of filename
    if (disposition && disposition.indexOf('attachment') !== -1) {
      let filenameRegex = /filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/;
      let matches = filenameRegex.exec(disposition);

      if (matches !== null && matches[1]) {
        filename = matches[1].replace(/['"]/g, '');
      }
    }

    // check for Microsoft specific download, or regular download
    if (navigator.msSaveBlob) {
      window.navigator.msSaveBlob(blob, filename);
    } else {
      const downloadLink = angular.element('<a></a>');

      downloadLink.attr('href', window.URL.createObjectURL(blob));
      downloadLink.attr('download', filename);
      downloadLink[0].click();
    }
  };

}

DownloadService.$inject = ['$sce'];
