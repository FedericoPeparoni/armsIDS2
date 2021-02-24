// controllers
import { CRUDFormControllerUserService } from '../../angular-ids-project/src/helpers/controllers/crud-form-user-service/crud-form-user-service.controller';

// interfaces
import { IRestangularResponse } from '../../angular-ids-project/src/helpers/interfaces/restangularError.interface';
import { IScQuerySubmission, ScQuerySubmissionScope } from './sc-query-submission.interface';

// services
import { RecaptchaService } from '../../angular-ids-project/src/components/recaptcha/service/recaptcha.service';
import { ScQuerySubmissionService } from './service/sc-query-submission.service';

export class ScQuerySubmissionController extends CRUDFormControllerUserService {

  /* @ngInject */
  constructor(protected $scope: ScQuerySubmissionScope, private scQuerySubmissionService: ScQuerySubmissionService, private recaptchaService: RecaptchaService) {
    super($scope, scQuerySubmissionService);
    super.setup({ refresh: false });

    $scope.discard = () => $scope.editable = this.scQuerySubmissionService.getModel();
    $scope.send = (editable: IScQuerySubmission) => scQuerySubmissionService.send(editable).then(() => {
      this.$scope.messageSent = true;
      if (this.$scope.widgetId) {
        recaptchaService.reload(this.$scope.widgetId);
      }
      this.reset();
    },
      (error: IRestangularResponse) => {
        this.$scope.error = { error: error };
        recaptchaService.reload(this.$scope.widgetId);
      }
    );
  }
}
