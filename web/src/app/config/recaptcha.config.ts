/**
 * Override functionality from angular-recaptcha.
 * https://github.com/VividCortex/angular-recaptcha#service-provider
 */

/** @ngInject */
export function RecaptchaConfig(vcRecaptchaServiceProvider: any): void {
    // default to an invalid key placeholder so that a warning is displayed to the user.
    // if key is undefined, null, or blank, it only logs a console error.
    vcRecaptchaServiceProvider.setSiteKey('INVALID_PUBLIC_KEY');
}
