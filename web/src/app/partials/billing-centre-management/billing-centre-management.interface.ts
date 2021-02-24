import { IAerodrome } from '../aerodromes/aerodromes.interface';

export interface IBillingCentre {
  aerodromes: IAerodrome[];
  hq: boolean;
  id?: number;
  name: string;
  invoice_sequence_number: number;
  prefix_invoice_number: number;
  prefix_receipt_number: number;
  receipt_sequence_number: number;
  users: string[];
  external_accounting_system_identifier: string;
  iata_invoice_sequence_number: number;
  receipt_cheque_sequence_number: number;
  receipt_wire_sequence_number: number;
}

export interface IBillingCentreScope extends ng.IScope {
  editable: IBillingCentre;
  checkHeadquarters: Function;
  edit: Function;
  delete: Function;
  headquarter: boolean;
  exist: boolean;
  isHeadquarter: boolean;
  list: Array<IBillingCentre>;
  updateWarning: string;
  createWarning: string;
  deleteWarning: string;
  original: IBillingCentre;
  hqIsSet: boolean | IBillingCentre;
  requireExternalSystemId: boolean;
  iataInvoiceSeparatedSeqNumber: boolean;
  receiptSeqNumberByPayMechanism: boolean;
}

export interface IBillingCentreSpring {
  content: Array<IBillingCentre>;
}

export interface IBillingCentreMinimal {
  id: number;
  name: string;
}
