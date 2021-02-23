package ca.ids.abms.util.recaptcha;

import ca.ids.abms.modules.system.SystemConfigurationService;
import ca.ids.abms.modules.system.summary.SystemConfigurationItemName;
import ca.ids.abms.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
class CaptchaValidator {

    private static final Logger LOG = LoggerFactory.getLogger(CaptchaValidator.class);

    private final CaptchaService captchaService;
    private final SystemConfigurationService systemConfigurationService;

    CaptchaValidator(
        final CaptchaService captchaService,
        final SystemConfigurationService systemConfigurationService
    ) {
        this.captchaService = captchaService;
        this.systemConfigurationService = systemConfigurationService;
    }

    boolean validateCaptcha(String captchaResponse) {

        // obtain secret key from captcha service to validate against
        CaptchaKey captchaSecret = captchaService.findKeyByType(CaptchaKeyType.SECRET);

        // validate that captcha is required for this request
        // must be enabled via system configuration and secret must be valid
        if (!isCaptchaRequired(captchaSecret)) return true;

        // validate captcha response against captche secrete via google recaptcha endpoint
        RestTemplate restTemplate = new RestTemplate();

        MultiValueMap<String, String> requestMap = new LinkedMultiValueMap<>();
        requestMap.add("secret", captchaSecret.getValue());
        requestMap.add("response", captchaResponse);

        CaptchaResponse apiResponse = restTemplate.postForObject(CaptchaConstants.GOOGLE_RECAPTCHA_ENDPOINT,
            requestMap, CaptchaResponse.class);

        LOG.debug("Captcha api response {}", apiResponse);
        if (apiResponse == null){
            return false;
        }

        return Boolean.TRUE.equals(apiResponse.getSuccess());
    }

    /**
     * Captcha validation is only required if system configuration setting "REQUIRE_CAPTCHA" is enabled
     * and a secret key is configured.
     */
    private boolean isCaptchaRequired(final CaptchaKey captchaSecret) {
        return captchaSecret != null && StringUtils.isNotBlank(captchaSecret.getValue()) &&
            systemConfigurationService.getBoolean(SystemConfigurationItemName.REQUIRE_CAPTCHA, false);
    }
}
