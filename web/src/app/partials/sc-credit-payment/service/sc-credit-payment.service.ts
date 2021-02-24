// interface
import { IScCreditPayment } from '../sc-credit-payment.interface';

// service
import { CRUDService } from '../../../angular-ids-project/src/helpers/services/crud.service';

export let endpoint: string = 'sc-credit-payment';

export class ScCreditPaymentService extends CRUDService {

    protected restangular: restangular.IService;

    private _mod: IScCreditPayment = {
        id: null,
        transactionTime: null,
        account: null,
        requestorIp: null,
        request: null,
        response: null,
        responseStatus: null,
        responseDescription: null,
        version: null
    };

    /** @ngInject */
    constructor(protected Restangular: restangular.IService) {
        super(Restangular, endpoint);
        this.model = angular.copy(this._mod);
    }

    public sendNonSensitiveDataToCreditGateway(amount: number, accountId: number): ng.IPromise<any> {
        return this.restangular.one(this.endpoint).customPOST(null, null, { amount: amount, accountId: accountId });
    }

    public sendTokenIdToCreditGateway(token: string): ng.IPromise<any> {
        return this.restangular.one(`${this.endpoint}/token`).customPOST(null, null, { token: token });
    }
}
