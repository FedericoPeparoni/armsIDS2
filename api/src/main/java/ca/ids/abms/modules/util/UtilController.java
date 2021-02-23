package ca.ids.abms.modules.util;

import ca.ids.abms.util.recaptcha.CaptchaKey;
import ca.ids.abms.util.recaptcha.CaptchaKeyType;
import ca.ids.abms.util.recaptcha.CaptchaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;

@RestController
@RequestMapping("api/util")
@SuppressWarnings("unused")
public class UtilController {

    private static final Logger LOG = LoggerFactory.getLogger(UtilController.class);

    private final CaptchaService captchaService;

    UtilController(final CaptchaService captchaService) {
        this.captchaService = captchaService;
    }

    @GetMapping("/current-datetime")
    public ResponseEntity<ZonedDateTime> getCurrentDateTime() {
        LOG.debug("REST request to get server datetime.");
        return ResponseEntity.ok().body(ZonedDateTime.now());
    }

    @GetMapping("/recaptcha-public-key")
    public ResponseEntity<CaptchaKey> getRecaptchaPublicKey() {
        LOG.debug("REST request to get reCAPTCHA public key");
        return ResponseEntity.ok().body(captchaService.findKeyByType(CaptchaKeyType.PUBLIC));
    }
}
