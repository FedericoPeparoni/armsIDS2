package ca.ids.abms.util.recaptcha;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
public class CaptchaAspect {

    private final CaptchaValidator captchaValidator;

    CaptchaAspect(final CaptchaValidator captchaValidator) {
        this.captchaValidator = captchaValidator;
    }

    @Around("@annotation(RequiresCaptcha)")
    public Object validateCaptcha(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String captchaResponse = request.getHeader(CaptchaConstants.CAPTCHA_HEADER_NAME);
        boolean isValidCaptcha = captchaValidator.validateCaptcha(captchaResponse);
        if (!isValidCaptcha){
            throw new CaptchaException("Invalid captcha");
        }
        return joinPoint.proceed();
    }
}
