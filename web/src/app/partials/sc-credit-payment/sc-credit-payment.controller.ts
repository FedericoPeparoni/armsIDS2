// constants
import { SysConfigConstants } from '../../partials/system-configuration/system-configuration.constants';

// controllers
import { CRUDFormControllerUserService } from '../../angular-ids-project/src/helpers/controllers/crud-form-user-service/crud-form-user-service.controller';

// services
import { ScCreditPaymentService } from './service/sc-credit-payment.service';
import { SystemConfigurationService } from '../system-configuration/service/system-configuration.service';
import { HelperService } from '../../angular-ids-project/src/components/services/helpers/helpers.service';

export class ScCreditPaymentController extends CRUDFormControllerUserService {

    private apiKey: string;
    private url: string;
    private processor: string;
    private configured: boolean;

    /* @ngInject */
    constructor(protected $scope: ng.IScope,
        private scCreditPaymentService: ScCreditPaymentService,
        private systemConfigurationService: SystemConfigurationService,
        private $sce: angular.ISCEService,
        private $state: angular.ui.IStateService) {

        super($scope, scCreditPaymentService);
        super.setup();
        this.setCreditDefaults();
        this.validateCreditDefaults();
        this.getFilterParameters();
        $scope.trustSrc = (src: string) => $sce.trustAsResourceUrl(src);
        $scope.refreshOverride = () => this.refreshOverride();

        // step 1 of cxPay system
        $scope.sendNonSensitiveDataToCreditGateway = (amount: number, accountId: number) => this.sendNonSensitiveDataToCreditGateway(amount, accountId);
        // step 3 of cxPay system
        this.checkTokenId();
    }

    protected refreshOverride(): angular.IPromise<void> {
        this.getFilterParameters();
        return super.list(this.$scope.filterParameters, this.$scope.getSortQueryString());
    }

    private getFilterParameters(): void {
        this.$scope.filterParameters = {
            search: this.$scope.textFilter,
            page: this.$scope.pagination ? this.$scope.pagination.number : 0
        };
    }

    private setCreditDefaults(): void {
        this.configured = this.systemConfigurationService.getBooleanFromValueByName(<any>SysConfigConstants.CC_PROCESSOR_CONFIGURED);
        this.apiKey = this.systemConfigurationService.getValueByName(<any>SysConfigConstants.CC_PROCESSOR_PUBLIC_KEY);
        this.url = this.systemConfigurationService.getValueByName(<any>SysConfigConstants.CC_PROCESSOR_URL);
        this.processor = this.systemConfigurationService.getValueByName(<any>SysConfigConstants.CC_PROCESSOR);
    }

    private validateCreditDefaults(): void {
        this.$scope.configurationWarning = false;
        const isConfigured = (v: string): boolean => {
            if (v === null || v === undefined || v.length === 0) {
                return false;
            }
            return true;
        };

        if (!this.configured || !isConfigured(this.apiKey) || !isConfigured(this.url) || !isConfigured(this.processor)) {
            this.$scope.configurationWarning = true;
        }
    }

    // send amount to be paid to BE, which sends it to cxPay
    // and returns an object with a formUrl prop
    private async sendNonSensitiveDataToCreditGateway(amount: number, accountId: number): Promise<any> {
        // if the amout or accound id is cleared by the user
        if (!amount || !accountId) {
            // set the formUrl back to null as he will
            // need a new one. This disables the subit button
            this.$scope.formUrl = null;
            return;
        }
        try {
            // get the formUrl from the cxPay step1 response
            const cxPayStep1 = await this.scCreditPaymentService.sendNonSensitiveDataToCreditGateway(amount, accountId);
            // step 2 is an HTML form submission to the URL
            this.$scope.formUrl = cxPayStep1.response['form-url'];

            // if the response code is not success
            if (cxPayStep1.response.result !== 1) {
                // display the error from the gateway
                this.$scope.payment = cxPayStep1.response;
                this.$scope.paymentDeclinedMessage = true;
            } else {
                // success
                // update the amount to be paid var in the UI with
                // the user entered amount
                this.$scope.amountToBePaid = this.$scope.payment_amount;
            }
        } catch (error) {
            console.error(error);
        }
    }

    // step 3 of cxPay system
    private async checkTokenId(): Promise<any> {
        const token = HelperService.getUrlParamValueByName('token-id');
        if (token) {
            // submit token to cxPay to complete request
            const completed = await this.scCreditPaymentService.sendTokenIdToCreditGateway(token);

            // as step 3 is finished, remove the token from the URL
            this.$state.go('main.sc-credit-payment');

            // toggle the form open
            this.$scope.toggle = true;

            // refresh to update the table
            super.list(this.$scope.filterParameters, this.$scope.getSortQueryString());

            // parse and display gateway response
            this.displayTransactionCompletedResponse(completed.response);
        }
    }

    private displayTransactionCompletedResponse(response: any): void {
        this.$scope.paymentSuccessMessage = false;
        this.$scope.paymentDeclinedMessage = false;
        this.$scope.payment = response;
        if (this.$scope.payment.result === 1) {
            this.$scope.paymentSuccessMessage = true;
        } else {
            this.$scope.paymentDeclinedMessage = true;
        }
    }
}
