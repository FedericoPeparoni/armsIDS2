package ca.ids.abms.util.recaptcha;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.EnumMap;

@Service
public class CaptchaService {

    private EnumMap<CaptchaKeyType, String> captchaKeys = new EnumMap<>(CaptchaKeyType.class);

    CaptchaService(
        @Value("${google.recaptcha.public}") final String publicKey,
        @Value("${google.recaptcha.secret}") final String secretKey
    ) {
        captchaKeys.put(CaptchaKeyType.PUBLIC, publicKey);
        captchaKeys.put(CaptchaKeyType.SECRET, secretKey);
    }

    public CaptchaKey findKeyByType(final CaptchaKeyType type) {
        return new CaptchaKey(type, captchaKeys.get(type));
    }
}
