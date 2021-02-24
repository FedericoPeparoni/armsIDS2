export interface RecaptchaKey {
  type: string;
  value: string;
}

export interface RecaptchaScope extends ng.IScope {
  widgetId: number;
  showButton: boolean;
  publicKey: string;
  onWidgetCreate: Function;
  setResponse: Function;
  recaptchaExpire: Function;
}
