// intefaces
import { ICRUDFormController } from "../controllers/crud-form/crud-form.controller";

/**
 * Refractor CRUDFormController to incorporate ICRUDFormScope type definitions.
 */
export interface ICRUDFormScope<T> extends angular.IScope {
  editable: T;
  list: Array<T>;
  search: string;
  create(obj: any): angular.IPromise<T>;
  delete(id: number): angular.IPromise<void>;
  edit(obj: any): void;
  refresh(data: ICRUDFormController, queryString: string): angular.IPromise<any>;
  reset(): void;
  update(obj: any, id: number): angular.IPromise<void>;
}
