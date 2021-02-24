// interfaces
import { IPayment } from '../../components/directives/payment/payment.interface';
import { IInvoiceLineItem, IInvoicePermit, IRequisition } from '../line-item/line-item.interface';

export interface INonAviationInvoicePayload {
  line_items?: Array<IInvoiceLineItem>;
  payment?: IPayment;
  permit_numbers?: Array<IInvoicePermit>;
  requisition_numbers?: Array<IRequisition>;
}
