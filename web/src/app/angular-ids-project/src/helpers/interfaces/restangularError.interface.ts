export interface IFieldError {
  name: string;
  field: string;
  message: string;
  object_name: string; // Spring Object
}

export interface IError {
  error: string;
  error_description: string;
  field_errors: Array<IFieldError>;
  form_errors: Array<IFieldError>;
  hide_subtitle: boolean;
  rejected_reasons: string;
}

export interface IRestangularResponse {
  config: Object;
  data: IError;
  status: number;
  statusText: string;
  headers: Function;
}

export interface IExtendableError {
  error: {data: IError};
}
