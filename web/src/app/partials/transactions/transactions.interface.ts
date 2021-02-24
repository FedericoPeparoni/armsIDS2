// interfaces
import { IAccount } from '../accounts/accounts.interface';
import { ICurrency } from '../currency-management/currency-management.interface';
import { ITransactionType } from '../transaction-types/transaction-types.interface';
import { IDateObject } from '../../angular-ids-project/src/helpers/interfaces/dateObject.interface';
import { IInvoice, IInvoiceSpring } from '../invoices/invoices.interface';
import { IExtendableError } from '../../angular-ids-project/src/helpers/interfaces/restangularError.interface';
import { IExternalChargeCategory } from '../external-charge-category/external-charge-category.interface';
import { IActiveOrganization } from '../organization/organization.interface';
import { IBankAccount } from '../bank-account-management/bank-account-management.interface';

export interface ITransaction {
  id?: number;
  account: IAccount;
  transaction_date_time : IDateObject;
  description: string;
  transaction_type : {
    id: number;
    name: string;
  };
  amount: number;
  currency: ICurrency;
  exchange_rate_to_usd: number;
  exchange_rate_to_ansp: number;
  balance: number;
  payment_mechanism: string;
  payment_reference_number: string;
  exported: boolean;
  billing_ledger_ids: any;
  payment_amount: number;
  payment_currency: ICurrency;
  payment_exchange_rate: number;
  charges_adjustment: Array<IAdjustmentCharge>;
  receipt_number?: string;
  kra_receipt_number: string;
  kra_clerk_name: string;
  payments_exported: boolean;
  has_approval_document: boolean;
  approval_document_name: string;
  approval_document_type: string;
  has_supporting_document: boolean;
  supporting_document_name: string;
  supporting_document_type: string;
  document?: Blob;
  document_filename?: string;
  payment_date?: IDateObject;
  bank_account_name?: string;
  bank_account_number?: string;
  bank_account_external_accounting_system_id?: string;
  interest_invoice_error: Array<IInterestInvoiceError>;
  transaction_approvals: Array<ITransactionApprovals>;
}

export interface IInterestInvoiceError {
  invoiceNumber: string;
  interestRate: string;
  exchangeRate: string;
}

export interface IAdjustmentCharge {
  id?: number;
  date: IDateObject;
  aerodrome: string;
  flight_id: string;
  charge_description: string;
  other_description: string;
  charge_amount: number;
  pending_transaction?: any;
  invoice_type?: string;
  transaction_id: ITransaction;
  external_accounting_system_identifier?: string;
  external_charge_category_name?: string;
}

export interface ITransactionApprovals {
  approver_name: string;
  action: string;
  approval_notes: string;
  approval_level: number;
  approval_date_time: string;
}

export interface ITransactionsScope extends ng.IScope {
  accountsList: Array<IAccount>;
  transactionTypesList: Array<ITransactionType>;
  editable: ITransaction;
  currencyList: Array<ICurrency>;
  fullCurrencyList: Array<ICurrency>;
  invoices: Array<IInvoice>;
  validateSelectedInvoice: (ids: Array<number>) => void;
  getCurrencyListByAccountId: (id: number) => Array<ICurrency>;
  calculatePayments: (id: number, amount: number) => void;
  updateExchangeRate: (transaction: ITransaction, prioritizePayment?: boolean) => void;
  updateLocalAmount: () => void;
  updatePaymentAmount: () => void;
  create: (transaction: ITransaction) => ng.IPromise<any>;
  reset: () => void;
  setExternalSystemIdentifiers: (chargeDescription: string) => void;
  selectedInvoiceIds: Array<number>;
  totalAmount: number;
  amounts: IInvoice;
  amountAvailable: number;
  paymentMechanisms: Array<string>;
  isDisabled: boolean;
  getInvoicesByTransactionId: Function;
  listOfInvoices: IInvoice;
  paymentExchangeRate: string;
  form: IEditableForm;
  charge: IAdjustmentCharge;
  charges: Array<IAdjustmentCharge>;
  addCharges: Function;
  editCharges: Function;
  updateCharges: Function;
  deleteCharges: Function;
  resetCharges: Function;
  chargeIndex: number;
  invoiceAdjusted: IInvoice;
  total: number;
  invoiceChecked: boolean;
  getCheckbox: Function;
  getInvoiceAdjusted: Function;
  getTotal: Function;
  chargeTypes: Array<IChargeTypeScope>;
  createNote: Function;
  cancelCharges: Function;
  totalAmountForAllInvoices: number;
  catalogueCharges: Array<IChargeTypeScope>;
  data: IInvoiceSpring;
  excessToBePaid: boolean;
  adjustmentInvoiceId: number;
  error: IExtendableError;
  exportSuccess: boolean;
  exportSupport: boolean;
  exportInProcess: boolean;
  selectedItems: any;
  exportedFilter: boolean;
  toggleExportDropdown: boolean;
  activeOrg: IActiveOrganization;
  isBankAccountSupported: boolean;
  isPaymentDateSupported: boolean;
  exportSelectedTransactions: (selected: any) => void;
  exportAllTransactions: () => void;
  isExported: (transaction: ITransaction) => boolean;
  isExportSupport: (transaction: ITransaction) => boolean;
  isSelectedItems: (selected: any) => boolean;
  setExportSupportMechanism: (mechanism: string) => void;
  refreshList: () => ng.IPromise<any>;
  refreshInvoiceList: (accountId: number, currencyId: number, page?: number) => ng.IPromise<IInvoiceSpring> | void;
  refreshAndHideCreateWizard: (transaction: ITransaction) => ng.IPromise<any>;
  hideCreateWizard: (transaction: ITransaction) => void;
  showChargeWizard: (editable: ITransaction) => void;
  showCreateWizard: (editable: ITransaction) => void;
  showExcessWizard: (editable: ITransaction) => void;
  getBankAccountLabel: (name: string, number: string) => string;
  isPaymentMechanism: (...mechanisms: Array<string>) => boolean;
  setEditableBankAccount: (bankAccount: IBankAccount) => void;
  onPaymentMechanismChange: (editable: ITransaction) => void;
}

export interface IEditableForm extends ng.IFormController {
  account: ng.INgModelController;
  description: ng.INgModelController;
  transactionType: ng.INgModelController;
  paymentCurrency: ng.INgModelController;
  paymentAmount: ng.INgModelController;
  currency: ng.INgModelController;
  paymentExchangeRate: ng.INgModelController;
  amount: ng.INgModelController;
  paymentMechanism: ng.INgModelController;
  paymentReferenceNumber: ng.INgModelController;
}

export interface IChargeTypeScope {
  description: string;
  external_charge_category?: IExternalChargeCategory;
  external_accounting_system_identifier?: string;
}

export interface ITransactionExportSupport {
  credit_notes: boolean,
  payments: boolean
}
