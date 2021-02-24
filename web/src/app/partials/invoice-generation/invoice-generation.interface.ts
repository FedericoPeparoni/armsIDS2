import { ICatalogueServiceChargeType } from '../catalogue-service-charge/catalogue-service-charge.interface';
import { IFlightMovementCategory } from '../../partials/flight-movement-category/flight-movement-category.interface';
import { IFlightMovement, IFlightMovementSpring } from '../flight-movement-management/flight-movement-management.interface';
import { IExtendableError } from '../../angular-ids-project/src/helpers/interfaces/restangularError.interface';
import { IAerodrome } from '../aerodromes/aerodromes.interface';
import { IAdhocFee } from '../../components/directives/external-database-input/external-database-input.directive';
import { IAccount, IAccountMinimal } from '../accounts/accounts.interface';

export interface IAviationInvoiceResponseResult {
  sum_arrival_charges: number;
  sum_enroute_charges: number;
  sum_departure_charges: number;
  sum_parking_charges: number;
  sum_total_charges: number;
}

export interface IGeneralInvoiceResponseResult {
  sum_general_charges : number;
}

export interface InvoiceGenerationScope extends ng.IScope {
  invoiceByFmCategory: boolean;
  accountsList: Array<IAccountMinimal>;
  typeOfSaleSelection: string;
  account: IAccount;
  accounts_type: string;
  error: IExtendableError;
  selectedFlights: Array<number>;
  lineItems: Array<any>;
  flightToggleSelection: (id: number) => void;
  tempFlightMovementList: IFlightMovement[];
  validateFlightMovement: (flightMovement: IFlightMovement) => void;
  previewAviationInvoiceJSON: Function
  createFlightMovement: (account: IAccount) => void;
  editFlightMovement: (account: IAccount, flightMovemnt: IFlightMovement, index: number) => void;
  removeFlightMovement: (index: number) => void;
  modal: any; // ng.ui.bootstrap.IModalService; (doesn't like appendTo)
  invoiceCreated: boolean;
  invoiceCreatedAndPaid: boolean;
  getNonInvoicedFlightMovementsByAccount: Function;
  results: IAviationInvoiceResponseResult | IGeneralInvoiceResponseResult | Array<string>;
  serviceChargeList: ICatalogueServiceChargeType[];
  aerodromesList: IAerodrome[];
  flightMovementList: IFlightMovementSpring;
  createAccount: Function;
  getAccounts: Function;
  accountsListAll: Array<IAccountMinimal>;
  accountsListCash: Array<IAccountMinimal>;
  accountsListCredit: Array<IAccountMinimal>;
  createAircraftRegistration: Function;
  flightCategories: Array<IFlightMovementCategory>;
  chargeProps: IChargeProps;
  clearChargeItemForm: (chargeItemForm: IChargeItemForm) => void;
  isGenerateDisabled: (isPayment: boolean, isItemsValid: boolean, isPaymentValid?: boolean) => boolean;
  lineItemsValid: boolean;
  workflow: any;
  removeError: Function;
  generateSuccess: Function;
  generateError: Function;
  generateAviationInvoicePaySuccess: Function;
  payment: any;
  getTypes: Function;
  getDescriptions: Function;
  addChargeItem: Function;
  lineItemError: any[];
  calculatedAmount: any[];
  previewNonAviationInvoiceJSON: Function;
  nonAviationInvoices: any;
  removeChargeItem: Function;
  generateNonAviSuccess: Function;
  generateNonAviError: Function;
  generateNonAviationInvoicePaySuccess: Function;
  generateAviationInvoice: Function;
  generateNonAviationInvoice: Function;
  taspLabel: string;
  adhocFees: IAdhocFee[];
  organizationName: string;
  addAdhocFeeToList: Function;
  paymentReset: any;
  aircraftTypesList: any;
  aviationInvoices: any;
  form: any;
  showPassengerCounts: boolean;
  approachLabel: string;
  accountCurrencyCode: string;
}

export interface IChargeItemForm extends ng.IFormController {
  category: string;
  type: string;
  description: string;
}

export interface IChargeProps {
  categories: Array<any>;
  types: Array<any>;
  descriptions: Array<any>;
}

export interface IAviationInvoice {
  global : {
    real_invoice_number: number;
    invoice_number: string;
    invoice_name: string;
    invoice_issue_location: string;
    invoice_date_str: string;
    account_id: number;
    account_name: string;
    from_name: string;
    from_position: null;
    billing_name: string;
    billing_address: number;
    billing_contact_tel: number;
    enroute_charges: number;
    enroute_charges_str: string;
    enroute_charges_str_with_currency_symbol: string;
    tasp_charges: number;
    tasp_charges_str: number;
    tasp_charges_str_with_currency_symbol: string;
    landing_charges: number;
    landing_charges_str: number;
    landing_charges_str_with_currency_symbol: string;
    parking_charges: number;
    parking_charges_str: number;
    parking_charges_str_with_currency_symbol: string;
    passenger_charges: number;
    passenger_charges_str: string;
    passenger_charges_str_with_currency_symbol: number;
    late_departure_arrival_charges: number;
    late_departure_arrival_charges_str: string;
    late_departure_arrival_charges_str_with_currency_symbol: number;
    total_amount: number;
    total_amount_str: string;
    total_amount_str_with_currency_symbol: string;
    credit_amount: number;
    credit_amount_str: string;
    credit_amount_str_with_currency_symbol: string;
    amount_due: number;
    amount_due_str: string;
    amount_due_str_with_currency_symbol: string;
    invoice_currency_code: string;
    flightCategory: number;
    aerodrome_charges: number;
    aerodrome_charges_str: string;
    aerodrome_charges_str_with_currency_symbol: string;
    approach_charges: number;
    approach_charges_str: string;
    approach_charges_str_with_currency_symbol: string;
  };
  flight_info_list: IFlightInfo[];
  additional_charges: any[] // was empty
}

interface IFlightInfo {
  enroute_charges_included: boolean;
  passenger_charges_included: true;
  other_charges_included: true;
  account_id: number;
  account_name: string;
  flight_movement_id: number;
  billing_date_str: string;
  billing_entry_date_str: string;
  billing_exit_date_str: string;
  reg_num: string;
  aircraft_type: string;
  mtow: number;
  mtow_str: string;
  mtow_kg: number;
  mtow_kg_str: string;
  entry_point: string;
  exit_point: null;
  cross_dist: null;
  cross_dist_str: null;
  departure_location: null;
  departure_time_str: string;
  arrival_location: null;
  arrival_time_str: string;
  departure_passenger_count: null;
  arrival_passenger_count: null;
  transit_passenger_count: null;
  infant_passenger_count: null;
  enroute_charges: null;
  enroute_charges_str: null;
  enroute_charges_str_with_currency_symbol: null;
  tasp_charges: null;
  tasp_charges_str: null;
  tasp_charges_str_with_currency_symbol: null;
  landing_charges: null;
  landing_charges_str: null;
  landing_charges_str_with_currency_symbol: null;
  parking_charges: null;
  parking_charges_str: null;
  parking_charges_str_with_currency_symbol: null;
  passenger_charges: null;
  passenger_charges_str: null;
  passenger_charges_str_with_currency_symbol: null;
  late_departure_arrival_charges: null;
  late_departure_arrival_charges_str: null;
  late_departure_arrival_charges_str_with_currency_symbol: null;
  total_charges: number;
  total_charges_str: string;
  total_charges_str_with_currency_symbol: string;
  aerodrome_charges: number;
  aerodrome_charges_str: string;
  aerodrome_charges_str_with_currency_symbol: string;
  approach_charges: number;
  approach_charges_str: string;
  approach_charges_str_with_currency_symbol: string;
}

export interface IAdhocPermit {
  flight_movement_id: null,
  permit_number: null,
  fee_amount: null
}
