// services
import { RecaptchaService } from '../angular-ids-project/src/components/recaptcha/service/recaptcha.service';

/** @ngInject */
export function RecaptchaRun(recaptchaService: RecaptchaService): void {
  // on initial run, retreive recaptcha public key from api if avaialble
  recaptchaService.updatePublishKey('util/recaptcha-public-key');
}
