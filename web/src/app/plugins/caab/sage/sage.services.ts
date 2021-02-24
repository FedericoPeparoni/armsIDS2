// services
import { CaabSageChargeAdjustmentsService } from './partials/charge-adjustments/service/charge-adjustments.caab.sage.service';

// service module
export default angular.module('armsWeb.plugins.caab.sage.services', [])
  .service('caabSageChargeAdjustmentsService', CaabSageChargeAdjustmentsService);
