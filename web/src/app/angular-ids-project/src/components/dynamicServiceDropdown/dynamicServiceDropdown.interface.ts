export interface IDynamicServiceDropdown extends ng.IScope {
  dropdownType: string;
  ngOptions: string;
  ngModel: ng.IControllerService;
  class: string;
  service: string;
  list: {
    content: Array<any>;
  };
  method: string;
  allowEmpty: boolean;
  queryString: string;
  sortedBy: string;
}
