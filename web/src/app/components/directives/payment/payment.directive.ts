// controllers
import { PaymentController } from './payment.controller';

// interfaces
import { IPayment, IPaymentScope } from './payment.interface';

/**
 * Payment directive to be used for Point of Sale Invoice Generation
 *
 * Set up how an invoice will be paid
 *
 * <payment is-valid="paymentValid" reset="paymentReset" payment="payment" invoice-currency="invoiceCurrency"><!-- Payment Directive --></payment>
 *
 */
/** @ngInject */
export function payment(): angular.IDirective {
  return {
    restrict: 'E',
    templateUrl: 'app/components/directives/payment/payment.html',
    scope: {
      invoiceCurrency: '=', // currency
      payment: '=', // interface IPayment
      isValid: '=?', // boolean
      reset: '=?',  // sending any new value in will reset this
      items: '=',
      invoiceAmount: '=?',
      accountId: '=' // number
    },
    replace: true,
    controller: PaymentController,
    controllerAs: 'PaymentController',
    require: ['^form'],
    link: PaymentLink
  };
}

function PaymentLink(scope: IPaymentScope, elem: ng.IAugmentedJQuery, attr: any, ctrl: ng.INgModelController): void {

  // intialize payment as empty object
  scope.payment = scope.payment || GetEmptyPayment();

  // ensures on load form isn't `touched` and `invalid`
  scope.PaymentController.$timeout(() => ctrl[0].$setUntouched(), 0);

  // sets `isValid` to parent
  scope.$watch('payment', () => {
    attachExternalDatabaseItems(scope);
    scope.isValid = ctrl[0].$valid;
  }, true);

  // clear payment and set untouched on reset
  scope.$watch('reset', () => {
    scope.payment = GetEmptyPayment();
    ctrl[0].$setUntouched();
  });
}

function attachExternalDatabaseItems(scope: IPaymentScope): void {
  if (!scope.items || !scope.items.length) {
    return;
  }

  const requisitions = scope.items.filter((item: any) =>
    item.requisiton !== null && item.requisition.req_number !== null
  ).map((item: any) => item.requisition);

  const invoicePermits = scope.items.filter((item: any) =>
    item.invoice_permit !== null && item.invoice_permit.invoice_permit_number !== null
  ).map((item: any) => item.invoice_permit);

  scope.payment.requisitions = requisitions.length ? requisitions : null;
  scope.payment.invoice_permits = invoicePermits.length ? invoicePermits : null;
}

/**
 * Retrieve an empty payment object used on initialization or reset.
 */
function GetEmptyPayment(): IPayment {
  return {
    amount: null,
    currency: null,
    payment_mechanism: null,
    description: null,
    payment_reference_number: null,
    payment_amount: null,
    payment_currency: null,
    payment_exchange_rate: null,
    invoice_permits: null,
    requisitions: null,
    kra_receipt_number: null,
    kra_clerk_name: null,
    bank_account_name: null,
    bank_account_number: null,
    bank_account_external_accounting_system_id: null
  };
}
