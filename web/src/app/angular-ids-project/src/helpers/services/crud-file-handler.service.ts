// service
import { CRUDService } from './crud.service';

export class CRUDFileUploadService extends CRUDService {

  constructor(protected Restangular: restangular.IService, protected endpoint: string) {
    super(Restangular, endpoint);

    this.endpoint = endpoint;
  }

  public upload(method: string, fd: FormData, dest: string[], params?: any): ng.IPromise<any> {
    if (method === 'PUT') {
      return this.restangular.one(`${this.buildUrl(dest)}/upload`).withHttpConfig({ 'transformRequest': angular.identity })
        .customPUT(fd, undefined, params, { 'Content-Type': undefined });
    } else {
      return this.restangular.one(`${this.buildUrl(dest)}/upload`).withHttpConfig({ 'transformRequest': angular.identity })
        .customPOST(fd, undefined, params, { 'Content-Type': undefined });
    }
  }

  public preview(dest: any[]): ng.IPromise<any> {
    return this.restangular.one(this.buildUrl(dest) + '/preview').get({}, { 'Content-Type': 'text/json; charset=utf-8' });
  }

  public deleteFile(dest?: string[]): ng.IPromise<any> {
    return this.restangular.one(this.buildUrl(dest)).remove();
  }

  public buildUrl(dest: string[]): string {
    return this.endpoint + (angular.isArray(dest) ? '/' + dest.join('/') : '');
  }
}
