// interfaces
import { ICurrency } from '../currency-management/currency-management.interface';
import { ITransactionWorkflow } from '../transactions-workflow/transactions-workflow.interface';
import { IAdjustmentCharge } from '../transactions/transactions.interface';
import { IInvoice } from '../invoices/invoices.interface';
import { IAccount } from '../accounts/accounts.interface';
import { IStaticType } from '../../angular-ids-project/src/helpers/interfaces/static-data-type.interface';
import { IRestangularResponse } from '../../angular-ids-project/src/helpers/interfaces/restangularError.interface';
import { IActiveOrganization } from '../organization/organization.interface';

export interface ITransactionPending {
  id?: number;
  account: IAccount;
  can_approve: boolean;
  can_reject: boolean;
  current_approval_level: ITransactionWorkflow;
  description: string;
  detailed_invoices: Array<IInvoice>;
  exchange_rate_to_ansp: number;
  exchange_rate_to_usd: number;
  exported: boolean;
  local_amount: number;
  local_currency: ICurrency;
  payment_amount: number;
  payment_currency: ICurrency;
  payment_exchange_rate: number;
  payment_mechanism: string;
  payment_reference_number: string;
  pending_charge_adjustments: Array<IAdjustmentCharge>;
  previous_approval_level: ITransactionWorkflow;
  related_invoices: string;
  transaction_date_time: string;
  transaction_type: IStaticType;
  version: number;
  document?: File;
  document_filename?: string;
  has_approval_document: boolean;
  approval_document_name: string;
  approval_document_type: string;
  has_supporting_document: boolean;
  supporting_document_name: string;
  supporting_document_type: string;
  pending_transaction_approvals: Array<IPendingTransactionApprovals>
}

export interface IPendingTransactionApprovals {
  approver_name: string;
  action: string;
  approval_notes: string;
  approval_level: number;
  approval_date_time: string;
}

export interface ITransactionPendingScope extends ng.IScope {
  selectedItem: Function;
  approve: Function;
  reject: Function;
  invoices: Array<IInvoice>;
  charges: Array<IAdjustmentCharge>;
  levels: Array<Object>;
  editable: ITransactionPending;
  selected: boolean;
  can_reject: boolean;
  can_approve: boolean;
  error: { error: IRestangularResponse };
  inverseExchange: any;
  activeOrg: IActiveOrganization;
  roundingAv: string;
  roundingNonAv: string;
  decimalPlaces: number;
  approval_document_level: number;
  approval_document_required: boolean;
  invoice_to_approve_id: number;
  pattern: string;
  currencyList: Array<ICurrency>;
  paymentMechanisms: Array<string>;
  approval_document: Blob;
  approval_document_filename: string;
  textFilter: string;
  absoluteValue: (value: number) => number;
  parseApprovalDocument: (file: File) => void;
  resetForm: () => void;
}
