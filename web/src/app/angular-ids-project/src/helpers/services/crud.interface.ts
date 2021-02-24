export interface ICRUDScope extends ng.IScope {
  editable: Object;
  edit(obj: Object): void;
  delete(id: number): void;
  update(obj: Object, id: number): void;
  create(obj: Object): void;
  reset(): void;
  resetPass?: Object; // used if a form has the password directive
  list?: any; // this is used when API calls do not exist and we have to use the Mock
  refresh: Function;
}
