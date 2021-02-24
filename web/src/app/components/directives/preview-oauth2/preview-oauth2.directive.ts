// interface
import { IExtendableError } from '../../../angular-ids-project/src/helpers/interfaces/restangularError.interface';

// services
import { DownloadService } from '../../../angular-ids-project/src/helpers/services/download.service';

/**
 * Preview directive
 *
 */
/** @ngInject */
export function previewOauth2(): angular.IDirective {

  return {
    restrict: 'E',
    templateUrl: 'app/components/directives/preview-oauth2/preview-oauth2.html',
    scope: {
      bodyParams: '<?', // body parameters
      bodyParamsFn: '=?', // body parameters as a function, takes precedence over bodyParams
      disable: '=?',
      error: '=',
      height: '=?',
      params: '=?', // queryString
      requestMethod: '@?', // http method, defaults to GET
      callbackSuccessFn: '&?', // if specified, runs after successful response
      url: '@',
      classUsed: '@?',
      paramId: '@?'
    },
    transclude: true,
    controller: PreviewOAuth2Controller
  };
}

interface IPreviewOAuth2 {
  bodyParams: Object;
  error: IExtendableError;
  getPreview: Function;
  height: string;
  params: Object;
  requestMethod: string;
  preview: boolean;
  url: string;
  hide: boolean;
  showButton: boolean;
  removePreview: boolean;
  callbackSuccessFn: Function;
  $on: any;
  classUsed: string;
  paramId: string;
  bodyParamsFn: (params: any) => any;
}

/** @ngInject */
export class PreviewOAuth2Controller {

  private $sce: ng.ISCEService;

  constructor(private $scope: IPreviewOAuth2, $sce: ng.ISCEService, private Restangular: restangular.IService, private downloadService: DownloadService) {
    $scope.getPreview = () => this.getPreview();
    $scope.height = $scope.height || '1100px';
    $scope.error = <IExtendableError>{};
    $scope.requestMethod = $scope.requestMethod || 'GET';
    this.$sce = $sce;
    $scope.classUsed = $scope.classUsed === undefined ? 'btn btn-primary btn-preview' : $scope.classUsed;
    $scope.paramId = $scope.paramId === undefined ? 'preview' : $scope.paramId;

    $scope.$on('hidePreview', () => this.$scope.hide = true);
  }

  private getPreview(): void {

    let rest = this.Restangular.withConfig((RestangularConfigurer: restangular.IProvider) => RestangularConfigurer.setFullResponse(true)); // this is done to get the full headers
    const method = this.$scope.requestMethod;

    let req = rest.one(this.$scope.url).withHttpConfig({responseType: 'arraybuffer'});
    let request = null;

    // which http method to use
    if (method === 'GET') {
      request = req.get(this.$scope.params);
    } else if (method === 'POST' || method === 'PUT' || method === 'DELETE') {
      let body: any = this.$scope.bodyParamsFn ? this.$scope.bodyParamsFn(this.$scope.bodyParams) : this.$scope.bodyParams;
      request = req.customPOST(body, null, this.$scope.params);
    } else {
      console.error('`requestMethod` not valid:', method);
    }

    request.then((resp: any) => {
      this.$scope.preview = this.downloadService.getPreview(resp);

      this.$scope.hide = false;
      this.$scope.showButton = true;

      if (this.$scope.callbackSuccessFn) {
        this.$scope.callbackSuccessFn();
      };
    })
      .catch((error: { data: ArrayBuffer }) => {
        const decodedString = String.fromCharCode.apply(null, new Uint8Array(error.data)); // must be decoded as error returns in an arraybuffer
        const errorObj = JSON.parse(decodedString);
        this.$scope.error = <IExtendableError>{};
        this.$scope.error.error = { data: errorObj };
      });
  }
}
