// interface
import { IExtendableError } from '../../../angular-ids-project/src/helpers/interfaces/restangularError.interface';

export interface IDownloadOAuth2 {
    url: string;
    params?: Object;
    error?: IExtendableError;
    requestMethod: string;
    bodyParams?: Object;
    classUsed?: string;
    generate?: Function;
    callbackSuccessFn?: Function;
    callbackErrorFn?: Function;
    bodyParamsFn?: (params: any) => any;
  }