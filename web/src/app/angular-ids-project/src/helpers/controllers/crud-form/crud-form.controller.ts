// constants
import { DISPLAY } from '../../../components/input/input.constants';

// has basic form functionality as well as CRUD operations
import { CRUDController } from './../crud/crud.controller';
import { IRestangularResponse, IExtendableError, IError } from '../../interfaces/restangularError.interface';
import { ISpringPageableParams } from '../../services/crud.service';

export interface ICRUDFormController {
  filter?: Object;
  refresh?: boolean;
}

interface IFilterObj {
  page: number;
}

export class CRUDFormController extends CRUDController {

  protected service: any;
  protected $scope: any;

  protected constructor(service: any) {
    super();
    this.service = service;
    return this;
  }

  // filter object is used to store/get the last previous GET parameters that we sent to the backend
  // this is used to make the same API call after a create/update/delete
  private _filterObj: IFilterObj;
  private queryString: string = '';

  protected setup(obj?: ICRUDFormController): void {

    let data: ICRUDFormController = obj === undefined ? {} : obj;
    data.refresh = data.refresh === undefined ? true : data.refresh;

    this.$scope.create = (obj: any) => this.create(obj);
    this.$scope.delete = (id: number) => this.delete(id);
    this.$scope.update = (obj: any, id: number) => this.update(obj, id);
    this.$scope.edit = (obj: any) => this.edit(obj);
    this.$scope.reset = () => this.reset();
    this.reset(); // will set up initial model
    this.$scope.refresh = (data: ICRUDFormController, queryString: string) => this.list(data, queryString);

    data.filter = data.filter = undefined ? '' : data.filter;

    if (data.refresh) {
      this.$scope.refresh({ filter: data.filter });
    };
  }

  protected create(data: Object): ng.IPromise<any> {
    return super.create(data).then((newObject: Object) => {
      this.reset();
      this.filterObj = this.filterObj || <IFilterObj>{};
      this.filterObj.page++; // fake the number one higher because it will be decreased in crud.service.ts
      this.list(this.filterObj, this.queryString);
      return newObject;
    }, (error: IRestangularResponse) => this.bindErrorAndThrow(error));
  }

  protected update(data: Object, id: number): ng.IPromise<any> {
    return super.update(data, id).then(() => { this.reset(); this.filterObj.page++; // fake the number one higher because it will be decreased in crud.service.ts
      this.list(this.filterObj, this.queryString);
    }, (error: IRestangularResponse) => this.bindErrorAndThrow(error));
  }

  /**
   * Sets the form to edit the single entry
   * @param {Object} data   entity to edit
   */
  protected edit(data: Object): void {
    this.$scope.error = null;
    this.$scope.editable = angular.copy(data);
  }

  protected list(data?: Object, queryString?: string, endpoint?: string): ng.IPromise<any> {
    this.filterObj = <any>data; // set the filterObj, so we can reuse
    this.queryString = queryString; // set the queryString, so we can reuse

    return super.list(this.filterObj, queryString, endpoint).then((data: ISpringPageableParams) => {
      this.$scope.list = data.content;
      let pagination = angular.copy(data);
      delete pagination.content;
      this.$scope.pagination = pagination;

      // scrolls the respective table to the top
      this.$scope.$broadcast('scrollToTop');

      return data;
    });
  }

  /**
   * Resets the form
   */
  protected reset(): void {
    this.$scope.error = null;
    this.$scope.editable = angular.copy(this.service.model);
    if (this.$scope.form) {
      this.$scope.form.$setUntouched();
    }
  }

  /**
   * Calls the service delete method then resets the form
   * @param {number} id  the id to delete
   */
  protected delete(id: number): ng.IPromise<void> {
    return super.delete(id)
      .then(() => {
        this.reset();
        this.filterObj.page++; // fake the number one higher because it will be decreased in crud.service.ts
        this.list(this.filterObj, this.queryString);
      }, (error: IRestangularResponse) => this.bindErrorAndThrow(error));
  }

  /**
   * Set error in scope from error object using default error hanlding.
   *
   * @param error error object
   */
  protected setError(error: IError): void {
    this.$scope.error = <IExtendableError>{};
    this.$scope.error.error = { data: error };
  }

  /**
   * Set error in scope from message using default error handling.
   *
   * @param message error message
   * @param description error message description
   */
  protected setErrorMessage(message: string, description?: string): void {
    // ties into default error handling
    this.$scope.error = <IExtendableError>{};
    this.$scope.error.error = { data: <IError> {
      error: message,
      error_description: description
    }};
  }

  /**
   * Set error in scope from response error using default error handling.
   *
   * @param response restangluar error response
   * @returns response for chaining
   */
  protected setErrorResponse(response: IRestangularResponse): IRestangularResponse {
    this.$scope.error = <IExtendableError>{};
    this.$scope.error.error = response;

    // in this order because if something were to fail it would be highlightFieldsWithError
    this.highlightFieldsWithError(response);

    return response;
  }

  private get filterObj(): IFilterObj {
    return this._filterObj;
  }

  private set filterObj(filterObj: IFilterObj) {
    this._filterObj = filterObj;
  }

  // if Spring returns `field_errors`, this will try to set the validity to the corresponding inputs as false
  private highlightFieldsWithError(error: IRestangularResponse): void {
    if (typeof error.data.field_errors !== 'undefined' && error.data.field_errors !== null) {
      // api response MUST throw error with rejected reason "ALREADY_EXISTS"
      let errorKey: string = error.data.rejected_reasons === 'ALREADY_EXISTS'
        ? DISPLAY.API_RESPONSE_EXISTS_CLASS : DISPLAY.API_RESPONSE_CLASS;
      for (let i = 0; i < error.data.field_errors.length; i++) { // binds field errors if there are any
        if (this.$scope.form[error.data.field_errors[i].field] !== undefined) {
          this.$scope.form[error.data.field_errors[i].field].$setValidity(errorKey, false);
        } else {
          console.warn('issue with backend sending input binding that does not exist on $scope');
        }
      }
    }
  }

  private bindErrorAndThrow(response: IRestangularResponse): void {
    throw this.setErrorResponse(response);
  }
}
