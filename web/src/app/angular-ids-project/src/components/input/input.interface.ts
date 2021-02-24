import { IAugmentedJQuery, INgModelController, IFormController } from 'angular';

export interface IInput extends ng.IScope {
  showTooltip: Object;
  errorTooltip: any; // this is any because the typings consider `$error` for angular to be any
  InputController: {
    $compile: ng.ICompileService;
  }
}

export interface IInputAttrs extends ng.IAttributes {
  ngModel: string;
}

export interface IInputCtrl extends INgModelController {
  // this is dangerous, '$$' values are for AngularJS internal use only
  $$parentForm: IFormController;
}

export interface IInputElement extends IAugmentedJQuery {
  labels: any;
}

