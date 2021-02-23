package ca.ids.abms.util.recaptcha;

public class CaptchaRequest {

    private String secret;

    private String response;

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
