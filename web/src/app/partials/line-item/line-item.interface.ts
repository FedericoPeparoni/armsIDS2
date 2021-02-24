import { IAerodrome } from '../aerodromes/aerodromes.interface';
import { ICatalogueServiceChargeType } from '../catalogue-service-charge/catalogue-service-charge.interface';
import { IAccount } from '../accounts/accounts.interface';
import { ITown } from '../utilities-towns/utilities-towns.interface';

// line item used for Non-Aviation Point of Sale and Non-Aviation Billing Engine
export interface IInvoiceLineItem {
  aerodrome: IAerodrome;
  service_charge_catalogue: ICatalogueServiceChargeType;
  recurring_charge: { // note: not used for POS Non-Aviation Invoice
    id: number;
    service_charge_catalogue: ICatalogueServiceChargeType;
    account: IAccount;
    start_date: string;
    end_date: string;
  };
  account_external_system_identifier?: string;
  amount: number;
  user_unit_amount?: number;
  user_markup_amount?: number;
  user_price?: number;
  user_electricity_meter_reading?: number;
  user_electricity_charge_type?: number; // (COMMERCIAL, RESIDENTIAL)
  user_water_meter_reading?: number;
  user_discount_percentage: number;
  user_town: ITown;
  price_per_unit: number;
  any_aerodrome: boolean;
  invoice_permit: IInvoicePermit;
  requisition: IRequisition;
}

export interface IInvoicePermit {
  invoice_permit_number: string;
  external_database_for_charge: string;
  adhoc_total_fee_payment_amount: number;
}

export interface IRequisition {
  req_number: string;
  external_database_for_charge: string;
  req_currency: string;
  req_total_amount: number;
  req_ar_id: number;
  req_country_id: number;
  req_id: number;
  req_maninfo_id: number;
}
