// interfaces
import { ICRUDScope } from '../../angular-ids-project/src/helpers/services/crud.interface';
import { ICurrency } from '../currency-management/currency-management.interface';
import { IAircraftRegistration } from '../aircraft-registration/aircraft-registration.interface';
import { ISystemConfigurationSpring } from '../system-configuration/system-configuration.interface';
import { IStaticType } from '../../angular-ids-project/src/helpers/interfaces/static-data-type.interface';
import { IAccountExternalChargeCategory } from '../account-external-charge-category/account-external-charge-category.interface';

// services
import { ISpringPageableParams } from '../../angular-ids-project/src/helpers/services/crud.service';

export interface IAccount {
  id?: number;
  name: string;
  alias: string;
  aviation_billing_contact_person_name: string;
  aviation_billing_phone_number: string;
  aviation_billing_mailing_address: string;
  aviation_billing_email_address: string;
  aviation_billing_sms_number: string;
  non_aviation_billing_contact_person_name: string;
  non_aviation_billing_phone_number: string;
  non_aviation_billing_mailing_address: string;
  non_aviation_billing_email_address: string;
  non_aviation_billing_sms_number: string;
  is_self_care: boolean;
  iata_code: string;
  icao_code: string;
  opr_identifier: string;
  payment_terms: number;
  discount_structure: number;
  cash_account: boolean;
  account_type: {
    id: number;
    name: string;
  };
  tax_profile: string; // tbd in requirements
  percentage_of_passenger_fee_payable: number;
  invoice_delivery_format: string;
  invoice_delivery_method: string;
  invoice_currency: ICurrency;
  monthly_overdue_penalty_rate: number;
  notes: string;
  black_listed_indicator: boolean;
  black_listed_override: boolean;
  credit_limit: number;
  aircraft_parking_exemption: number;
  list_of_events_account_notified: Array<IAccountEventMap>;
  iata_member: boolean;
  separate_pax_invoice: boolean;
  external_accounting_system_identifier: string;
  active: boolean;
  approved_flight_school_indicator: boolean;
  account_users: string[];
  nationality: string;
  whitelist_last_activity_date_time: string;
  whitelist_state: string;
  whitelist_inactivity_notice_sent_flag: string;
  whitelist_expiry_notice_sent_flag:string;
}

export interface IAccountEventMap {
  id?: number;
  notification_event_type: number;
  notification_email: boolean;
  notification_sms: boolean;
}

export interface INotificationList {
  id: number;
  name: string;
  model: Array<IStaticType>;
}

export interface IAccountsScope extends ICRUDScope {
  editable: IAccount;
  $state: angular.ui.IStateService;
  accountFilter: string;
  pagination: ISpringPageableParams;
  currencyList: Array<ICurrency>;
  minCreditLimit: number;
  maxCreditLimit: number;
  refreshOverride: () => ng.IPromise<IAccountSpring>;
  setDefaultFields: (data: ISystemConfigurationSpring) => void;
  asnpCurrency: string;
  getAviationInfo: Function;
  notificationsList: Array<any>;
  notificationsTypeList: Array<Object>;
  addNotificationsToList: Function;
  getAllNotifications: Function;
  requireExternalSystemId: boolean;
  editableExternal: IAccountExternalChargeCategory;
  editOverride: (item: IAccount) => void;
  editExternal: (item: IAccountExternalChargeCategory) => void;
  createExternal: (item: IAccountExternalChargeCategory) => ng.IPromise<any>;
  updateExternal: (item: IAccountExternalChargeCategory, id: number) => ng.IPromise<any>;
  deleteExternal: (id: number) => ng.IPromise<any>;
  resetExternal: () => void;
}

export interface IAccountSpring {
  content: Array<IAccount>;
}

export interface IAccountRefresh extends ISpringPageableParams {
  invoices?: string;
  credit?: boolean;
}

export interface IAccountMinimal {
  id: number;
  name: string;
}
