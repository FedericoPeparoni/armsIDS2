// runners
import { OAuthRun } from './run/oauth.run';
import { TranslateRun } from './run/translate.run';
import { ServerDateTimeRun } from './run/server-datetime.run';
import { RecaptchaRun } from './run/recaptcha.run';

// run module
export default angular.module('armsWeb.run', [])
  .run(OAuthRun)
  .run(TranslateRun)
  .run(ServerDateTimeRun)
  .run(RecaptchaRun);
