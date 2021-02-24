// configs
import { LoadingBarConfig } from './config/loading-bar.config';
import { LogConfig } from './config/log.config';
import { RouterConfig } from './config/router.config';
import { TranslateConfig } from './config/translate.config';
import { LocaleInterceptor } from './config/locale-interceptor.config';
import { PaginationConfig } from './config/pagination.config';
import { RecaptchaConfig } from './config/recaptcha.config';

// decorators
import { MultiselectDecorator } from '../app/components/decorators/multiselect';
import { ProgressBarDecorator } from '../app/components/decorators/progressBar';
import { DatePickerDecorator } from '../app/components/decorators/datePicker';
import { LocaleDecorator } from '../app/components/decorators/locale';
import { InputDecorator } from '../app/components/decorators/input';

// config module
export default angular.module('armsWeb.configs', [])
    .config(LoadingBarConfig)
    .config(LogConfig)
    .config(RouterConfig)
    .config(TranslateConfig)
    .config(LocaleInterceptor)
    .config(MultiselectDecorator)
    .config(ProgressBarDecorator)
    .config(DatePickerDecorator)
    .config(LocaleDecorator)
    .config(PaginationConfig)
    .config(RecaptchaConfig)
    .config(InputDecorator);
