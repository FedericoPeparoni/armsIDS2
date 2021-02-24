export interface IPermissions {
  id: number;
  name: string;
  permissions: Array<any>;
}

export interface IPermissionsMap {
  'account-exempt-management': Array<string>;
  'accounts': Array<string>;
  'aerodrome-category': Array<string>;
  'aerodromes': Array<string>;
  'amhs-connection': Array<string>;
  'air-navigation-charges': Array<string>;
  'air-traffic-data': Array<string>;
  'aircraft-exempt-management': Array<string>;
  'aircraft-registration': Array<string>;
  'aircraft-type-management': Array<string>;
  'aircraft-unspecified-management': Array<string>;
  'airspace-management': Array<string>;
  'application-management': Array<string>;
  'atc-movement-log': Array<string>;
  'aviation-billing-engine': Array<string>;
  'billing-centre-management': Array<string>;
  'catalogue-service-charge': Array<string>;
  'currency-management': Array<string>;
  'enroute-air-navigation-charges-management': Array<string>;
  'flight-movement-management': Array<string>;
  'flight-route-exemptions': Array<string>;
  'flight-status-exemptions': Array<string>;
  'group-management': Array<string>;
  'invoice-generation': Array<string>;
  'invoice-template-management': Array<string>;
  'invoices': Array<string>;
  'mtow': Array<string>;
  'nominal-routes': Array<string>;
  'non-aviation-billing-engine': Array<string>;
  'passenger-service-charge-return': Array<string>;
  'radar-summary': Array<string>
  'regional-country-management': Array<string>;
  'rejected-items': Array<string>;
  'report-generation': Array<string>;
  'report-templates': Array<string>;
  'repositioning-aerodrome-clusters': Array<string>;
  'revenue-data': Array<string>;
  'system-configuration': Array<string>;
  'system-summary': Array<string>;
  'tower-movement-logs': Array<string>;
  'transactions': Array<string>;
  'unspecified-locations': Array<string>;
  'users': Array<string>;
  'utilities-schedules': Array<string>;
  'utilities-towns': Array<string>;
}
