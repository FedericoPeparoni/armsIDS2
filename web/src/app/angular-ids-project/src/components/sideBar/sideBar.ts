// services
import {
  NO_PERMISSIONS_REQUIRED,
  SELF_CARE_PORTAL_PERMISSIONS,
  SELF_CARE_PORTAL_PERMISSIONS_NOT_AUTHORIZED
} from '../../../../partials/permissions/service/permissions.service';

export const sideBar = [
  {
    title: 'Billing', icon: 'file-text', permissions: ['invoices_view', 'transaction_view', 'aviation_invoice_preview',
    'point_of_sale_access', 'invoices_approve', 'invoices_publish', 'transaction_modify', 'aviation_invoice_generate', 'aviation_invoice_recalculate',
    'aviation_invoice_validate', 'nonaviation_invoice_preview', 'nonaviation_invoice_generate', 'passenger_revenue_reconcile',
    'transaction_pending_modify', 'transaction_pending_view'],
    links: [{ category: 'Invoices', icon: 'book', permissions: ['invoices_view', 'invoices_approve', 'invoices_publish'], url: 'invoices'},
      { category: 'Transactions', icon: 'exchange', permissions: ['transaction_view', 'transaction_modify'], url: 'transactions'},
      { category: 'Pending Transactions', icon: 'exchange', permissions: ['transaction_pending_view', 'transaction_pending_modify'], url: 'transactions-pending'},
      { category: 'Aviation Billing', icon: 'plane', permissions: ['aviation_invoice_preview', 'aviation_invoice_generate',
        'aviation_invoice_recalculate', 'aviation_invoice_validate'], url: 'aviation-billing-engine'},
      { category: 'Non-Aviation Billing', icon: 'plane', permissions: ['nonaviation_invoice_preview', 'nonaviation_invoice_generate'], url: 'non-aviation-billing-engine'},
      { category: 'Point Of Sale Invoice Generation', icon: 'file-text-o', permissions: ['point_of_sale_access'], url: 'invoice-generation'},
      { category: 'Passenger Revenue Reconciliation', icon: 'plane', permissions: ['passenger_revenue_reconcile'], url: 'passenger-revenue-reconciliation'}]
  },
  {
    title: 'Flight Data', icon: 'arrow-up', permissions: ['flight_movement_view', 'flight_movement_modify', 'flight_log_view', 'flight_log_modify', 'radar_summary_view',
    'rejected_data_view', 'radar_summary_modify', 'rejected_data_modify', 'flight_schedule_view', 'flight_schedule_modify', 'flight_movement_key_modify'],
    links: [{ category: 'Flight Movements', icon: 'plane', permissions: ['flight_movement_view', 'flight_movement_modify', 'flight_movement_key_modify'], url: 'flight-movement-management' },
    { category: 'Flight Schedules', icon: 'plane', permissions: ['flight_schedule_view', 'flight_schedule_modify'], url: 'flight-schedule-management' },
    { category: 'ATC Movement Log', icon: 'book', permissions: ['flight_log_view', 'flight_log_modify'], url: 'atc-movement-log' },
    { category: 'Radar Summary', icon: 'rss', permissions: ['radar_summary_view', 'radar_summary_modify'], url: 'radar-summary' },
    { category: 'Tower Movement Log', icon: 'tasks', permissions: ['flight_log_view', 'flight_log_modify'], url: 'tower-movement-logs' },
    { category: 'Passenger Service Charge Return', icon: 'retweet', permissions: ['flight_log_view', 'flight_log_modify'], url: 'passenger-service-charge-return' },
    { category: 'Rejected Items', icon: 'times-circle', permissions: ['rejected_data_view', 'rejected_data_modify'], url: 'rejected-items' }]
  },
  {
    title: 'Operations', icon: 'cube', permissions: ['account_view', 'account_modify', 'aircraft_registration_view', 'aircraft_registration_modify',
      'nominal_route_view', 'local_acreg_modify', 'local_acreg_view', 'nominal_route_modify', 'zzzz_aircraft_type_view', 'zzzz_aircraft_type_modify',
      'zzzz_locations_view', 'zzzz_locations_modify', 'currency_view', 'currency_modify', 'aircraft_type_view', 'aircraft_type_modify',
      'flight_reassignment_view', 'flight_reassignment_modify', 'charges_view', 'charges_modify'],
    links: [{ category: 'Accounts', icon: 'users', permissions: ['account_view', 'account_modify'], url: 'accounts' },
    { category: 'Aircraft Registrations', icon: 'fighter-jet', permissions: ['aircraft_registration_view', 'aircraft_registration_modify'], url: 'aircraft-registration' },
    { category: 'Local Aircraft Registry', icon: 'fighter-jet', permissions: ['local_acreg_modify', 'local_acreg_view'], url: 'local-aircraft-registry' },
    { category: 'Aircraft Types', icon: 'fighter-jet', permissions: ['aircraft_type_view', 'aircraft_type_modify'], url: 'aircraft-type-management' },
    { category: 'Currencies', icon: 'dollar', permissions: ['currency_view', 'currency_modify'], url: 'currency-management' },
    { category: 'Nominal Routes', icon: 'road', permissions: ['nominal_route_view', 'nominal_route_modify'], url: 'nominal-routes' },
    { category: 'Unspecified Aircraft Types', icon: 'paper-plane-o', permissions: ['zzzz_aircraft_type_view', 'zzzz_aircraft_type_modify'], url: 'aircraft-unspecified-management' },
    { category: 'Unspecified Locations', icon: 'map-pin', permissions: ['zzzz_locations_view', 'zzzz_locations_modify'], url: 'unspecified-locations' },
    { category: 'Flight Reassignment', icon: 'plane', permissions: ['flight_reassignment_view', 'flight_reassignment_modify'], url: 'flight-reassignment' },
    { category: 'Recurring Charges', icon: 'google-wallet', permissions: ['charges_view', 'charges_modify'], url: 'recurring-charges' }]
  },
  {
    title: 'Charges and Formulas', icon: 'check-square-o', permissions: ['charges_schedule_view', 'charges_schedule_modify', 'avg_mtow_factor_view', 'avg_mtow_factor_modify',
    'enroute_charges_view', 'enroute_charges_modify', 'service_charge_view', 'service_charge_modify','tu_rate_management_view','tu_rate_management_modify', 'utilities_schedule_view',
    'utilities_schedule_modify', 'utilities_towns_view', 'utilities_towns_modify'],
    links: [{ category: 'Air Navigation Charges Schedules', icon: 'cloud', permissions: ['charges_schedule_view', 'charges_schedule_modify'], url: 'air-navigation-charges' },
    { category: 'Average MTOW Factor', icon: 'balance-scale', permissions: ['avg_mtow_factor_view', 'avg_mtow_factor_modify'], url: 'mtow' },
    { category: 'Enroute Air Navigation Charges', icon: 'flask', permissions: ['enroute_charges_view', 'enroute_charges_modify'], url: 'enroute-air-navigation-charges-management' },
    { category: 'Service Charge Catalogue', icon: 'retweet', permissions: ['service_charge_view', 'service_charge_modify'], url: 'catalogue-service-charge' },
    { category: 'TU Rate Management', icon: 'check-square-o', permissions: ['tu_rate_management_view', 'tu_rate_management_modify'], url: 'tu-rate-management' },
    { category: 'Utilities Schedules', icon: 'flash', permissions: ['utilities_schedule_view', 'utilities_schedule_modify'], url: 'utilities-schedules' },
    { category: 'Utilities Towns and Villages', icon: 'tint', permissions: ['utilities_towns_view', 'utilities_towns_modify'], url: 'utilities-towns' }]
  },
  {
    title: 'Exemptions', icon: 'minus-circle', permissions: ['exempt_account_view', 'exempt_account_modify', 'exempt_aircraft_type_view', 'exempt_aircraft_type_modify',
    'exempt_flight_route_view', 'exempt_flight_route_modify', 'exempt_flight_status_view', 'exempt_flight_status_modify', 'exempt_aircraft_flights_view',
    'exempt_aircraft_flights_modify', 'aerodrome_category_view', 'aerodrome_category_modify', 'aerodrome_service_outage_view', 'aerodrome_service_outage_modify'],
    links: [{ category: 'Exempt Accounts', icon: 'users', permissions: ['exempt_account_view', 'exempt_account_modify'], url: 'account-exempt-management' },
    { category: 'Exempt Aircraft Types', icon: 'fighter-jet', permissions: ['exempt_aircraft_type_view', 'exempt_aircraft_type_modify'], url: 'aircraft-exempt-management' },
    { category: 'Exempt Flight Routes', icon: 'external-link', permissions: ['exempt_flight_route_view', 'exempt_flight_route_modify'], url: 'flight-route-exemptions' },
    { category: 'Exempt Flight Status', icon: 'pencil-square-o', permissions: ['exempt_flight_status_view', 'exempt_flight_status_modify'], url: 'flight-status-exemptions' },
    { category: 'Exempt Aircraft and Flights', icon: 'paper-plane', permissions: ['exempt_aircraft_flights_view', 'exempt_aircraft_flights_modify'], url: 'aircraft-flights-exemptions' },
    { category: 'Repositioning Aerodrome Clusters', icon: 'cubes', permissions: ['aerodrome_category_view', 'aerodrome_category_modify'], url: 'repositioning-aerodrome-clusters' },
    { category: 'Service Outages', icon: 'adjust', permissions: ['aerodrome_service_outage_view', 'aerodrome_service_outage_modify'], url: 'service-outages' }]
  },
  {
    title: 'Management', icon: 'user-plus', permissions: ['aerodrome_category_view', 'aerodrome_category_modify', 'aerodrome_view', 'aerodrome_modify',
      'airspace_view', 'airspace_modify', 'billing_center_view', 'billing_center_modify', 'regional_countries_view', 'regional_countries_modify',
      'countries_view', 'countries_modify', 'route_cache_modify', 'route_cache_view', 'sys_config_view', 'sys_config_modify', 'approval_workflow_view', 'approval_workflow_modify',
      'manage_cached_event_view', 'manage_cached_event_modify', 'manage_plugin_view', 'manage_plugin_modify', 'banking_information_view', 'banking_information_modify',
      'interest_rate_view', 'interest_rate_modify', 'aerodrome_operational_hours_view', 'aerodrome_operational_hours_modify', 'amhs_config_view', 'amhs_config_modify '],
    links: [
    { category: 'Aerodromes', icon: 'paper-plane', permissions: ['aerodrome_view', 'aerodrome_modify'], url: 'aerodromes' },
    { category: 'Aerodrome Operational Hours', icon: 'hourglass', permissions: ['aerodrome_operational_hours_view', 'aerodrome_operational_hours_modify'],
    url: 'aerodrome-operational-hours' },
    { category: 'Aerodrome Categories', icon: 'object-group', permissions: ['aerodrome_category_view', 'aerodrome_category_modify'], url: 'aerodrome-category' },
    { category: 'Airspaces', icon: 'object-group', permissions: ['airspace_view', 'airspace_modify'], url: 'airspace-management' },
    { category: 'AMHS Connection', icon: 'link', permissions: ['amhs_config_view', 'amhs_config_modify'], url: 'amhs-connection' },
    { category: 'AMHS Accounts', icon: 'link', permissions: ['amhs_config_view', 'amhs_config_modify'], url: 'amhs-accounts' },
    { category: 'Application Management', icon: 'cog', permissions: ['route_cache_modify', 'route_cache_view'], url: 'application-management' },
    { category: 'Bank Accounts', icon: 'university', permissions: ['banking_information_view', 'banking_information_modify'], url: 'bank-account-management' },
    { category: 'Interest Rates', icon: 'percent', permissions: ['interest_rate_view', 'interest_rate_modify'], url: 'interest-rates' },
    { category: 'Billing Centres', icon: 'book', permissions: ['billing_center_view', 'billing_center_modify'], url: 'billing-centre-management' },
    { category: 'Countries', icon: 'flag', permissions: ['countries_view', 'countries_modify'], url: 'country-management' },
    { category: 'Regional Countries', icon: 'flag', permissions: ['regional_countries_view', 'regional_countries_modify'], url: 'regional-country-management' },
    { category: 'System Configuration', icon: 'cog', permissions: ['sys_config_view', 'sys_config_modify'], url: 'system-configuration' },
    { category: 'Transaction Workflow', icon: 'exchange', permissions: ['approval_workflow_view', 'approval_workflow_modify'], url: 'transactions-workflow' },
    { category: 'Cached Events', icon: 'cog', permissions: ['manage_cached_event_view', 'manage_cached_event_modify'], url: 'cached-events' },
    { category: 'Plugins', icon: 'plug', permissions: ['manage_plugin_view', 'manage_plugin_modify'], url: 'plugins' }]
  },
  {
    title: 'Data Analysis & Statistics', icon: 'pie-chart', permissions: ['system_summary_view', 'statistics_generate', 'reports_generate'],
    links: [{ category: 'System Summary', icon: 'bar-chart', permissions: ['system_summary_view'], url: 'system-summary' },
    { category: 'Analysis and Stats - Air Traffic', icon: 'line-chart', permissions: ['statistics_generate'], url: 'air-traffic-data' },
    { category: 'Analysis and Stats - Revenue', icon: 'line-chart', permissions: ['statistics_generate'], url: 'revenue-data' },
    { category: 'Report Generation', icon: 'file-text-o', permissions: ['reports_generate'], url: 'report-generation' }]
    // hide Revenue Projection until it works correctly as PO and Kieran asked 2018-11-28
    // { category: 'Revenue Projection', icon: 'dollar', permissions: ['reports_generate'], url: 'revenue-projection' }]
  },
  {
    title: 'Templates', icon: 'align-justify', permissions: ['invoice_template_view', 'invoice_template_modify', 'report_template_view', 'report_template_modify'],
    links: [{ category: 'Invoice Templates', icon: 'flash', permissions: ['invoice_template_view', 'invoice_template_modify'], url: 'invoice-template-management' },
    { category: 'Report Templates', icon: 'book', permissions: ['report_template_view', 'report_template_modify'], url: 'report-templates' }]
  },
  {
    title: 'Security', icon: 'lock', permissions: [NO_PERMISSIONS_REQUIRED],
    links: [{ category: 'Groups', icon: 'users', permissions: ['group_view', 'group_modify'], url: 'group-management' },
    { category: 'User Event Log', icon: 'server', permissions: ['user_event_view', 'user_event_modify'], url: 'user-event-log' },
    { category: 'Users', icon: 'user', permissions: ['user_view', 'user_modify'], url: 'users' },
    { category: 'Password Change', icon: 'user', permissions: [NO_PERMISSIONS_REQUIRED], url: 'current-user' }]
  },
  {
    title: 'Help', icon: 'info', permissions: [NO_PERMISSIONS_REQUIRED],
    links: [{ category: 'About', icon: 'code', permissions: [NO_PERMISSIONS_REQUIRED], url: 'about' },
    { category: 'User Manual', icon: 'book', permissions: [NO_PERMISSIONS_REQUIRED], url: 'help' }]
  },
  {
    title: 'Self-Care Portal', icon: 'user-plus', permissions: [SELF_CARE_PORTAL_PERMISSIONS],
    links: [{ category: 'Home Page', icon: 'home', permissions: [SELF_CARE_PORTAL_PERMISSIONS], url: 'sc-home-page' },
    { category: 'Login', icon: 'sign-in', permissions: [SELF_CARE_PORTAL_PERMISSIONS_NOT_AUTHORIZED], url: 'sc-login' },
    { category: 'Query Submission', icon: 'envelope', permissions: [SELF_CARE_PORTAL_PERMISSIONS], url: 'sc-query-submission' },
    { category: 'User Registration', icon: 'user', permissions: [SELF_CARE_PORTAL_PERMISSIONS_NOT_AUTHORIZED], url: 'sc-user-registration' },
    { category: 'Flight Cost Calculation', icon: 'calculator', permissions: [SELF_CARE_PORTAL_PERMISSIONS], url: 'sc-flight-cost-calculation' },
    { category: 'Users', icon: 'user', permissions: ['self_care_access', 'self_care_admin'], url: 'sc-user-management' },
    { category: 'Accounts', icon: 'key', permissions: ['self_care_access', 'self_care_admin'], url: 'sc-account-management' },
    { category: 'Flight Search', icon: 'plane', permissions: ['self_care_access', 'self_care_admin'], url: 'sc-flight-search' },
    { category: 'Aircraft Registrations', icon: 'paper-plane', permissions: ['self_care_access', 'self_care_admin'], url: 'sc-aircraft-registration' },
    { category: 'Flight Schedules', icon: 'calendar', permissions: ['self_care_access', 'self_care_admin'], url: 'sc-flight-schedules' },
    { category: 'Transactions', icon: 'exchange', permissions: ['self_care_access', 'self_care_admin'], url: 'sc-transactions' },
    { category: 'Invoices', icon: 'book', permissions: ['self_care_access', 'self_care_admin'], url: 'sc-invoices' },
    { category: 'Passenger Service Charge Return', icon: 'retweet', permissions: ['self_care_access', 'self_care_admin'], url: 'sc-passenger-service-charge-return' },
    { category: 'Credit Payment', icon: 'credit-card', permissions: ['self_care_access', 'self_care_admin'], url: 'sc-credit-payment' },
    { category: 'Approval Request', icon: 'unlock', permissions: ['self_care_admin'], url: 'sc-approval-request' },
    { category: 'Inactivity and Expiry Notices', icon: 'exclamation-circle', permissions: ['self_care_admin'], url: 'sc-inactivity-expiry-notice' },
    { category: 'Report Generation', icon: 'file-text-o', permissions: ['self_care_access', 'self_care_admin'], url: 'sc-report-generation' }
    ]
  }
];
