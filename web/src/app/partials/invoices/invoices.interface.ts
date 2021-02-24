// contants
import { EditableType } from './invoices.constants';

// interfaces
import { IAccount } from '../accounts/accounts.interface';
import { ICurrency } from '../currency-management/currency-management.interface';
import { IUser } from '../users/users.interface';
import { ITransaction } from '../transactions/transactions.interface';
import { ICRUDScope } from '../../angular-ids-project/src/helpers/services/crud.interface';
import { IFlightMovement, IFlightMovementSpring } from '../flight-movement-management/flight-movement-management.interface';
import { IInvoiceLineItem } from '../line-item/line-item.interface';
import { IExtendableError } from '../../angular-ids-project/src/helpers/interfaces/restangularError.interface';

export interface IInvoice {
  id: number;
  account: IAccount;
  invoice_period_or_date: string;
  invoice_type: string;
  invoice_state_type: string;
  payment_due_date: string;
  user: IUser;
  invoice_number: string;
  invoice_document: string;
  invoice_amount: number;
  invoice_currency: ICurrency;
  invoice_exchange_to_usd: number;
  invoice_date_of_issue: string;
  payment_amount: number;
  payment_currency: ICurrency;
  payment_exchange_to_usd: number;
  payment_date: string;
  exported: boolean;
  amount_owing: number;
  proforma: boolean;
  point_of_sale: boolean;
}

export interface IInvoiceSpring {
  content: Array<IInvoice>;
  number_of_elements: number;
  total_elements: number;
  total_items: number;
}

export interface IInvoicesScope extends ICRUDScope {
  filterChanged: (filterValue: string) => void;
  showFlightMovementOnMap: (flightMovement: IFlightMovement) => void;
  listFlightMovements:IFlightMovementSpring;
  listTransactionPayments: Array<ITransaction>;
  getFlightMovementsByInvoiceId: (invoiceId: number, page?: number, size?: number) => ng.IPromise<void>;
  getTransactionPaymentsByInvoiceId: (invoiceId: number) => ng.IPromise<ITransaction[]>;
  getLineItemsByInvoiceId: (invoiceId: number) => void; // todo update when we get invoice line items
  statesList: Array<string>;
  updateState: (id: number, invoiceStatus: string) => void;
  getStatesObjFromName: (name: string) => void;
  updateInvoice: Function;
  getFlightType: (flightType: string) => string;
  transactions: Array<ITransaction>;
  openAccount: Function;
  search: Function;
  statusFilter: string;
  invoiceTextFilter: string;
  exportedFilter: boolean;
  lineItems: Array<IInvoiceLineItem>;
  editable: IInvoice;
  list: Array<IInvoice>;
  error: IExtendableError;
  exportSuccess: boolean;
  exportSupport: boolean;
  exportInProcess: boolean;
  selectedItems: any;
  toggleExportDropdown: boolean;
  exportSelectedInvoices: (selected: any) => void;
  exportAllInvoices: () => void;
  isExportSupport: (invoice: IInvoice) => boolean;
  isSelectedItems: (selected: any) => boolean;
  setExportSupportType: (type: string) => void;
  refreshList: () => ng.IPromise<any>;
  edit: (data: Object, type?: EditableType) => void;
  shouldShowCharge: (chargeType: string) => boolean;
}
