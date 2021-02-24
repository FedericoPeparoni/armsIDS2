// interfaces
import { IBillingCentre } from '../billing-centre-management/billing-centre-management.interface';
import { IAerodromeCategory } from '../aerodrome-category-management/aerodrome-category-management.interface';

import { ISpringPageableParams } from '../../angular-ids-project/src/helpers/services/crud.service';

export interface IAerodrome {
  id?: number;
  aerodrome_name: string;
  extended_aerodrome_name: string;
  aixm_flag: boolean;
  geometry: IPoint;
  is_default_billing_center: boolean;
  billing_center: IBillingCentre;
  aerodrome_category: IAerodromeCategory;
  external_accounting_system_identifier: string;
  aerodrome_services: IAerodromeServiceType[];
}

export interface IAerodromeSpring extends ISpringPageableParams {
  content: Array<IAerodrome>;
}

export interface IPoint {
  type: string;
  coordinates: Array<number>;
}

export interface IAerodromeServiceType {
  id?: number;
  service_name: string;
  service_outage_approach_discount_type: string;
  service_outage_approach_amount: number;
  service_outage_aerodrome_discount_type: string;
  service_outage_aerodrome_amount: number;
  default_flight_notes: string;
}

export interface IAerodromesScope extends ng.IScope {
  editable: IAerodrome;
  list: Array<IAerodrome>;
  create: Function;
  edit: Function;
  update: Function;
  delete: Function;
  reset: Function;
  billingCentres: Array<IBillingCentre>;
  aerodromeCategories: Array<IAerodromeCategory>;
  checkIfDefaultSet: (aerodrome: IAerodrome) => boolean;
  hasDefault: boolean;
  requireExternalSystemId: boolean;
}
