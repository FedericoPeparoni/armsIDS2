// class with basic crud operations

export class CRUDController {

  protected service: any;

  protected create(data: Object): ng.IPromise<any> {
    return this.service.create(data);
  }

  protected list(data: Object = {}, queryString: string = '', endpoint?: string): ng.IPromise<any> {
    return this.service.list(data, queryString, endpoint);
  }

  protected update(data: Object, id: number): ng.IPromise<any> {
    return this.service.update(data, id);
  }

  protected delete(id: number): ng.IPromise<void> {
    return this.service.delete(id);
  }

}
