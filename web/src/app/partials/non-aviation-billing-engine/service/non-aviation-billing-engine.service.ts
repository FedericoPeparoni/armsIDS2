// service
import { CRUDService } from '../../../angular-ids-project/src/helpers/services/crud.service';

// interface
import { INonAviationBillingEngine } from '../non-aviation-billing-engine.interface';

export let endpoint: string = 'non-aviation-billing';

export class NonAviationBillingEngineService extends CRUDService {

    protected restangular: restangular.IService;

    private _mod: INonAviationBillingEngine = {
        month: null,
        year: null,
        accountId: null,
        externalChargeCategory: null,
        currencyCode: null
    };

    /** @ngInject */
    constructor(protected Restangular: restangular.IService) {
        super(Restangular, endpoint);
        this.model = angular.copy(this._mod);
    }
}
