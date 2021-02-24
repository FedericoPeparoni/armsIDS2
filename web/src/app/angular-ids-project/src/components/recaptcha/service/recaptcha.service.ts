// interfaces
import { RecaptchaKey } from '../recaptcha.interface';

export class RecaptchaService {

  /**
   * Google reCAPTCHA public key used for all captcha validation.
   */
  private _publicKey: string = null;

  /** @ngInject */
  constructor(private vcRecaptchaService: any, private Restangular: restangular.IService) {}

  /**
   * Wrapper for recaptcha service implemntation to reload widget when expired.
   *
   * @param widgetId widget id to reload
   */
  public reload(widgetId?: number): void {
    this.vcRecaptchaService.reload(widgetId);
  }

  /**
   * Retreives the reCAPTCHA public key from the provided enpoint
   * and updates publicKey value.
   *
   * @param endpoint url of reCATPCHA public key
   */
  public updatePublishKey(endpoint: string): void {
    this.Restangular.one(endpoint).get()
      .then((response: RecaptchaKey) => this.publicKey = typeof response !== 'undefined' && response.value
        ? response.value : null)
      .catch((response: any) => console.warn(`Could not retreive reCAPTCHA public key at ${endpoint}: ${response}`));
  }

  get publicKey(): string {
    return this._publicKey;
  }

  set publicKey(value: string) {
    this._publicKey = value;
  }
}
