// enums
import { CrudFileHandlerPropertyType } from './crud-file-handler-type';

// interfaces
import { IRestangularResponse } from '../../interfaces/restangularError.interface';
import { ICRUDFormController } from '../crud-form/crud-form.controller';

// controllers
import { CRUDFormController } from './../crud-form/crud-form.controller';

export class CRUDFileUploadController extends CRUDFormController {

  /**
   * Used for determining how request data model properties are to be included in request body.
   * 
   * CrudFileHandlerPropertyType.Param: [Default] Legacy support and assumes each property of the data model
   * is a form data parameter. Only primative types supported.
   * 
   * CrudFileHandlerPropertyType.Part: Preferred approach as data model is defined in its own form data
   * part. Any object type supported.
   * 
   * CrudFileHandlerPropertyType.None: All data model properties are excluded form upload request body.
   */
  private propertyType: CrudFileHandlerPropertyType;

  protected constructor(protected $scope: any, protected service: any, protected $uibModal: ng.ui.bootstrap.IModalService,
    fileUploadLabel: string = 'Template Document', propertyType: CrudFileHandlerPropertyType = CrudFileHandlerPropertyType.Param) {
    super(service);

    this.propertyType = propertyType;

    $scope.pattern = '.csv';
    $scope.multiple = 'false';
    $scope.fileUploadLabel = fileUploadLabel;
    $scope.fileUploadRequired = true;
    $scope.fileUploadDisabled = false;
    $scope.acceptedFileType = true;

    // added because the 'editable' object on the $scope does not exist until something is changed in the data entry form,
    // however, some file uploads have no fields to enter and thus when a file is selected $scope.editable is undefined
    // which causes an error
    if (this.$scope.editable === undefined || !this.$scope.editable.hasOwnProperty('id')) {
      this.$scope.editable = {
        id: null
      };
    }
  }

  protected createFormData(data?: Object): FormData {
    let fd = new FormData();

    if (this.$scope.editable.document) {
      fd.append('file', this.$scope.editable.document, this.$scope.editable.document_filename);
    }

    // add data properties to form data
    if (data && this.propertyType === CrudFileHandlerPropertyType.Param) {
      for (let key in data) {
        if (data.hasOwnProperty(key)) {
          fd.append(key, data[key]);
        }
      }
    } else if (data && this.propertyType === CrudFileHandlerPropertyType.Part) {
      fd.append('properties', new Blob([angular.toJson(data)], { type: 'application/json' }));
    }

    return fd;
  }

  protected setup(obj?: ICRUDFormController): void {
    // add all functions from the service to this scope so they can be accessed
    this.$scope.parse = (file: File, filename: string) => this.parse(file, filename);
    this.$scope.upload = (method: string, dest?: any[], data?: Object, callback?: string, params?: string) => this.upload(method, dest, data, callback, params);
    this.$scope.preview = (dest: any[]) => this.preview(dest);
    this.$scope.deleteFile = (dest: any[]) => this.deleteFile(dest);

    // call setup in CRUDFormController
    super.setup(obj);
  }

  protected upload(method: string, dest?: any[], data?: Object, callBack?: string, params?: any, preventCallback?: boolean): ng.IPromise<any> {
    return this.service.upload(method, this.createFormData(data), dest, params).then((resp: any) => {
      this.$scope.error = null;

      if (method === 'PUT') {
        this.$scope.uploadJob = resp;
      }

      if (resp.error) {
        return Promise.reject(resp);
      }

      if (callBack && callBack === 'list') {
        return this.service.list(params).then((resp: any) => {
          this.$scope.list = resp.content;
          let pagination = angular.copy(resp);
          delete pagination.content;
          this.$scope.pagination = pagination;
        });
      }

      if (!preventCallback) {
        callBack ? this.$scope[callBack](resp) : this.service.listAll(params).then((resp: any) => this.$scope.list = resp.content);
      }
    }, (error: IRestangularResponse) => this.handleErrorResponse(error));
  }

  // sets the required file information to the scope to be used when the user
  // uploads the file and any related fields of data
  private parse(file: File, filename: string = 'document_filename'): void {
    let r = new FileReader();

    if (file) {
      r.onload = (readerEvt: any) => {
        let binaryString = readerEvt.target.result;
        let type = (file.name.substr(file.name.lastIndexOf('.') + 1) === 'csv' ? 'text/plain' : file.type);
        let array = new Uint8Array(binaryString.length);

        for (let i = 0, len = binaryString.length; i < len; i++) {
          array[i] = binaryString.charCodeAt(i);
        }

        this.$scope.editable[filename] = file.name;
        this.$scope.editable.mime_type = type;
        this.$scope.editable.document = new Blob([array], { type: type });

        // grab file extension and compare with pattern of acceptable types
        // compare lower case version of file ext to list of valid extensions
        const fileNameSplit = this.$scope.editable[filename].split('.');
        this.$scope.acceptedFileType = this.$scope.pattern === '.*' || this.$scope.pattern.includes(fileNameSplit[fileNameSplit.length - 1].toLowerCase());
      };

      r.readAsBinaryString(file);
    }
  }

  private deleteFile(dest?: string[]): void {
    return this.service.deleteFile(dest).then(() => this.service.listAll().then((resp: any) => { this.$scope.list = resp.content; this.reset(); }),
      (error: IRestangularResponse) => this.handleErrorResponse(error));
  }

  private preview(dest: any[]): ng.IPromise<any> {
    return this.service.preview(dest).then((resp: any) => {
      this.createPreviewTable(resp.data);
    }, (error: IRestangularResponse) => this.handleErrorResponse(error));
  }

  private createPreviewTable(data: Object): void {
    let modal = this.$uibModal.open({
      templateUrl: 'app/angular-ids-project/src/helpers/controllers/crud-file-handler/preview.template.html',
      controller: ['$scope', ($scope: any) => {
        $scope.text = data;
        $scope.close = () => modal.close();

        this.$scope.confirm = () => {
          modal.close();
        };
      }]
    });
  }

  private handleErrorResponse(error: IRestangularResponse): void {
    this.$scope.error = {
      error: error
    };
  }
}
