// interfaces
import { IActiveOrganization } from '../../../partials/organization/organization.interface';
import { IBankAccount } from '../../../partials/bank-account-management/bank-account-management.interface';
import { ICurrency } from '../../../partials/currency-management/currency-management.interface';

export interface IPayment {
  description: string;
  amount: number;
  currency: ICurrency;
  payment_mechanism: string;
  payment_reference_number: string;
  payment_amount: number;
  payment_currency: ICurrency;
  payment_exchange_rate: number;
  invoice_permits: Array<any>;
  requisitions: Array<any>;
  kra_receipt_number: string;
  kra_clerk_name: string;
  bank_account_name?: string;
  bank_account_number?: string;
  bank_account_external_accounting_system_id?: string;
}

export interface IPaymentForm extends ng.IFormController {
  description: ng.INgModelController;
  paymentCurrency: ng.INgModelController;
  paymentAmount: ng.INgModelController;
  currency: ng.INgModelController;
  paymentExchangeRate: ng.INgModelController;
  amount: ng.INgModelController;
  paymentMechanism: ng.INgModelController;
  paymentReferenceNumber: ng.INgModelController;
}

export interface IPaymentScope extends ng.IScope {
  accountId: number;
  form: IPaymentForm;
  fullCurrencyList: Array<ICurrency>;
  payment: IPayment;
  paymentExchangeRate: string;
  isValid: boolean;
  reset: boolean;
  items: Array<any>;
  activeOrg: IActiveOrganization;
  isBankAccountSupported: boolean;
  filterPaymentMechanism: (mechanism: string) => boolean;
  updateExchangeRate: (fromCurrency: ICurrency, toCurrency: ICurrency, prioritizePayment?: boolean) => void;
  updateLocalAmount: () => void;
  updatePaymentAmount: () => void;
  getBankAccountLabel: (name: string, number: string) => string;
  isPaymentMechanism: (...mechanism: Array<string>) => boolean;
  setPaymentBankAccount: (bankAccount: IBankAccount) => void;
  onPaymentMechanismChange: (payment: IPayment) => void;
}
