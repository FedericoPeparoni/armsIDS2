package ca.ids.abms.util.recaptcha;

/**
 * Captcha response was not valid.
 */
class CaptchaException extends RuntimeException {

    CaptchaException(String message) {
        super(message);
    }
}
