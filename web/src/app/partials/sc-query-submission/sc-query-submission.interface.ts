export interface IScQuerySubmission {
  sender_email: string;
  subject: string;
  message: string;
}

export interface ScQuerySubmissionScope extends ng.IScope {
  widgetId: number;
  send: Function;
  setResponse: Function;
  recaptchaExpire: Function;
  discard: Function;
}
