package ca.ids.abms.util.recaptcha;

class CaptchaConstants {

    /**
     * HTTP request header name for client-side recaptcha response token.
     */
    static final String CAPTCHA_HEADER_NAME = "g-recaptcha-response";

    /**
     * API endpoint for validating recaptcha response tokens.
     */
    static final String GOOGLE_RECAPTCHA_ENDPOINT = "https://www.google.com/recaptcha/api/siteverify";

    private CaptchaConstants() {
        throw new IllegalStateException("Constants class, do not instantiate a new instance.");
    }
}
