/** @ngInject */
export function RouterConfig($httpProvider: ng.IHttpProvider, $stateProvider: any, $locationProvider: ng.ILocationProvider, $urlRouterProvider: ng.ui.IUrlRouterProvider): void {

  $httpProvider.defaults.withCredentials = true;
  $httpProvider.defaults.headers.common['Access-Control-Allow-Headers'] = '*';

  $stateProvider

    .state('main', {
      url: '',
      views: {
        '': {
          templateUrl: 'app/main/main.html',
          controller: 'MainController',
          controllerAs: 'main'
        },
        'map@main': {
          templateUrl: 'app/partials/map/map.html',
          controller: 'MapController',
          title: 'Map'
        }
      }
    })

    .state('main.map', {
      name: 'map',
      templateUrl: 'app/partials/users/users.html',
      controller: 'MapController',
      title: 'Users'
    })

    .state('login', {
      url: '/login',
      templateUrl: 'app/login/login.html',
      controller: 'LoginController',
      controllerAs: 'login',
      title: 'Log In'
    })

    .state('main.users', {
      url: '/users',
      templateUrl: 'app/partials/users/users.html',
      controller: 'UsersController',
      title: 'Users'
    })

    .state('main.aircraft-type-management', {
      url: '/aircraft-type-management',
      templateUrl: 'app/partials/aircraft-type-management/aircraft-type-management.html',
      controller: 'AircraftTypeManagementController',
      title: 'Aircraft Type Management'
    })

    .state('main.aircraft-exempt-management', {
      url: '/aircraft-exempt-management',
      templateUrl: 'app/partials/aircraft-exempt-management/aircraft-exempt-management.html',
      controller: 'AircraftExemptManagementController',
      title: 'Exempt Aircraft Management'
    })

    .state('main.aircraft-registration', {
      url: '/aircraft-registration',
      templateUrl: 'app/partials/aircraft-registration/aircraft-registration.html',
      controller: 'AircraftRegistrationController',
      title: 'Aircraft Registration',
      params: {
        after: null
      }
    })

    .state('main.aerodromes', {
      url: '/aerodromes',
      templateUrl: 'app/partials/aerodromes/aerodromes.html',
      controller: 'AerodromesController',
      title: 'Aerodromes'
    })

    .state('main.accounts', {
      url: '/accounts/:accountId',
      templateUrl: 'app/partials/accounts/accounts.html',
      controller: 'AccountsController',
      title: 'Accounts',
      params: {
        after: null
      }
    })

    .state('main.account-exempt-management', {
      url: '/account-exempt-management',
      templateUrl: 'app/partials/account-exempt-management/account-exempt-management.html',
      controller: 'AccountExemptManagementController',
      title: 'Accounts Exempt Management'
    })

    .state('main.aerodrome-category', {
      url: '/aerodrome-category',
      templateUrl: 'app/partials/aerodrome-category-management/aerodrome-category-management.html',
      controller: 'AerodromeCategoryManagementController',
      title: 'Aerodrome Category Management'
    })

    .state('main.transactions-workflow', {
      url: '/transactions-workflow',
      templateUrl: 'app/partials/transactions-workflow/transactions-workflow.html',
      controller: 'TransactionsWorkflowController',
      title: 'Transactions Workflow'
    })

    .state('main.airspace-management', {
      url: '/airspace-management',
      templateUrl: 'app/partials/airspace-management/airspace-management.html',
      controller: 'AirspaceManagementController',
      title: 'Airspace Management'
    })

    .state('main.currency-management', {
      url: '/currency-management',
      templateUrl: 'app/partials/currency-management/currency-management.html',
      controller: 'CurrencyManagementController',
      title: 'Currency Management'
    })

    .state('main.group-management', {
      url: '/group-management',
      templateUrl: 'app/partials/group-management/group-management.html',
      controller: 'GroupManagementController',
      title: 'Group Management'
    })

    .state('main.transactions', {
      url: '/transactions',
      templateUrl: 'app/partials/transactions/transactions.html',
      controller: 'TransactionsController',
      title: 'Transactions',
      params: {
        transaction: null
      }
    })

    .state('main.transactions-pending', {
      url: '/transactions-pending',
      templateUrl: 'app/partials/transactions-pending/transactions-pending.html',
      controller: 'TransactionsPendingController',
      title: 'Pending Transactions'
    })


    .state('main.invoice-generation', {
      url: '/invoice-generation/:accountId',
      templateUrl: 'app/partials/invoice-generation/invoice-generation.html',
      controller: 'InvoiceGenerationController',
      title: 'Point Of Sale Invoice Generation',
      params: {
        accountId: null
      }
    })

    .state('main.air-navigation-charges', {
      url: '/air-navigation-charges',
      templateUrl: 'app/partials/air-navigation-charges/air-navigation-charges.html',
      controller: 'AirNavigationChargesController',
      title: 'Air Navigation Charges'
    })

    .state('main.enroute-air-navigation-charges-management', {
      url: '/enroute-air-navigation-charges-management',
      templateUrl: 'app/partials/enroute-air-navigation-charges-management/enroute-air-navigation-charges-management.html',
      controller: 'EnrouteAirNavigationChargesManagementController',
      title: 'Enroute Air Navigation Charges Management'
    })

    .state('main.system-configuration', {
      url: '/system-configuration',
      templateUrl: 'app/partials/system-configuration/system-configuration.html',
      controller: 'SystemConfigurationController',
      title: 'System Configuration'
    })

    .state('main.languages', {
      url: '/languages',
      templateUrl: 'app/partials/languages/languages.html',
      controller: 'LanguagesController',
      title: 'Language Management'
    })

    .state('main.application-management', {
      url: '/application-management',
      templateUrl: 'app/partials/application-management/application-management.html',
      controller: 'ApplicationManagementController',
      title: 'Application Management'
    })

    .state('main.system-summary', {
      url: '/system-summary',
      templateUrl: 'app/partials/system-summary/system-summary.html',
      controller: 'SystemSummaryController',
      title: 'System Summary'
    })

    .state('main.regional-country-management', {
      url: '/regional-country-management',
      templateUrl: 'app/partials/regional-country-management/regional-country-management.html',
      controller: 'RegionalCountryManagementController',
      title: 'Regional Country'
    })

    .state('main.country-management', {
      url: '/country-management',
      templateUrl: 'app/partials/country-management/country-management.html',
      controller: 'CountryManagementController',
      title: 'Countries'
    })

    .state('main.invoices', {
      url: '/invoices',
      templateUrl: 'app/partials/invoices/invoices.html',
      controller: 'InvoicesController',
      title: 'Invoices'
    })

    .state('main.aviation-billing-engine', {
      url: '/aviation-billing-engine',
      templateUrl: 'app/partials/aviation-billing-engine/aviation-billing-engine.html',
      controller: 'AviationBillingEngineController',
      title: 'Aviation Billing Engine'
    })

    .state('main.non-aviation-billing-engine', {
      url: '/non-aviation-billing-engine',
      templateUrl: 'app/partials/non-aviation-billing-engine/non-aviation-billing-engine.html',
      controller: 'NonAviationBillingEngineController',
      title: 'Non-Aviation Billing Engine'
    })

    .state('main.passenger-service-charge-return', {
      url: '/passenger-service-charge-return',
      templateUrl: 'app/partials/passenger-service-charge-return/passenger-service-charge-return.html',
      controller: 'PassengerServiceChargeReturnController',
      title: 'Passenger Service Charge Return Management'
    })

    .state('main.catalogue-service-charge', {
      url: '/catalogue-service-charge',
      templateUrl: 'app/partials/catalogue-service-charge/catalogue-service-charge.html',
      controller: 'CatalogueServiceChargeController',
      title: 'Service Charge Catalogue'
    })

    .state('main.utilities-schedules', {
      url: '/utilities-schedules',
      templateUrl: 'app/partials/utilities-schedules/utilities-schedules.html',
      controller: 'UtilitiesSchedulesController',
      title: 'Utilities Schedules Management'
    })

    .state('main.utilities-towns', {
      url: '/utilities-towns',
      templateUrl: 'app/partials/utilities-towns/utilities-towns.html',
      controller: 'UtilitiesTownsController',
      title: 'Utilities Towns and Villages Management'
    })

    .state('main.flight-movement-management', {
      url: '/flight-movement-management',
      templateUrl: 'app/partials/flight-movement-management/flight-movement-management.html',
      controller: 'FlightMovementManagementController',
      title: 'Flight Movement Management'
    })

    .state('main.mtow', {
      url: '/mtow',
      templateUrl: 'app/partials/mtow/mtow.html',
      controller: 'MtowController',
      title: 'MTOW Factor Management'
    })

    .state('main.invoice-template-management', {
      url: '/invoice-template-management',
      templateUrl: 'app/partials/invoice-template-management/invoice-template-management.html',
      controller: 'InvoiceTemplateManagementController',
      title: 'Invoice Template Management'
    })

    .state('main.report-templates', {
      url: '/report-templates',
      templateUrl: 'app/partials/report-templates/report-templates.html',
      controller: 'ReportTemplatesController',
      title: 'Report Templates'
    })

    .state('main.unspecified-locations', {
      url: '/unspecified-locations',
      templateUrl: 'app/partials/unspecified-dep-dest-locations/unspecified-dep-dest-locations.html',
      controller: 'UnspecifiedDepDestLocationsController',
      title: 'Unspecified Departure and Destination Locations'
    })

    .state('main.aircraft-unspecified-management', {
      url: '/aircraft-unspecified-management',
      templateUrl: 'app/partials/aircraft-unspecified-management/aircraft-unspecified-management.html',
      controller: 'AircraftUnspecifiedManagementController',
      title: 'Unspecified Aircraft'
    })

    .state('main.about', {
      url: '/about',
      templateUrl: 'app/partials/about/about.html',
      controller: 'AboutController',
      title: 'About'
    })

    .state('main.help', {
      url: '/help',
      templateUrl: 'app/partials/help/help.html',
      controller: 'HelpController',
      title: 'Help'
    })

    .state('main.radar-summary', {
      url: '/radar-summary',
      templateUrl: 'app/partials/radar-summary/radar-summary.html',
      controller: 'RadarSummaryController',
      title: 'Radar Summary'
    })

    .state('main.flight-route-exemptions', {
      url: '/flight-route-exemptions',
      templateUrl: 'app/partials/flight-route-exemptions/flight-route-exemptions.html',
      controller: 'FlightRouteExemptionsController',
      title: 'Exempt Flight Route Management'
    })

    .state('main.flight-status-exemptions', {
      url: '/flight-status-exemptions',
      templateUrl: 'app/partials/flight-status-exemptions/flight-status-exemptions.html',
      controller: 'FlightStatusExemptionsController',
      title: 'Exempt Flight Status Management'
    })

    .state('main.billing-centre-management', {
      url: '/billing-centre-management',
      templateUrl: 'app/partials/billing-centre-management/billing-centre-management.html',
      controller: 'BillingCentreManagementController',
      title: 'Billing Centre Management'
    })

    .state('main.nominal-routes', {
      url: '/nominal-routes',
      templateUrl: 'app/partials/nominal-routes/nominal-routes.html',
      controller: 'NominalRoutesController',
      title: 'Nominal Routes'
    })

    .state('main.atc-movement-log', {
      url: '/atc-movement-log',
      templateUrl: 'app/partials/atc-movement-log/atc-movement-log.html',
      controller: 'AtcMovementLogController',
      title: 'ATC Movement Logs'
    })

    .state('main.tower-movement-logs', {
      url: '/tower-movement-logs',
      templateUrl: 'app/partials/tower-movement-logs/tower-movement-logs.html',
      controller: 'TowerMovementLogsController',
      title: 'Tower Movement Logs'
    })

    .state('main.user-event-log', {
      url: '/user-event-log',
      templateUrl: 'app/partials/user-event-log/user-event-log.html',
      controller: 'UserEventLogController',
      title: 'User Event Log'
    })

    .state('main.rejected-items', {
      url: '/rejected-items',
      templateUrl: 'app/partials/rejected-items/rejected-items.html',
      controller: 'RejectedItemsController',
      title: 'Rejected Items'
    })

    .state('main.recurring-charges', {
      url: '/recurring-charges',
      templateUrl: 'app/partials/recurring-charges/recurring-charges.html',
      controller: 'RecurringChargesController',
      title: 'Recurring Charges'
    })

    .state('main.report-generation', {
      url: '/report-generation',
      templateUrl: 'app/partials/report-generation/report-generation.html',
      controller: 'ReportGenerationController',
      title: 'Report Generation'
    })

    .state('main.revenue-projection', {
      url: '/revenue-projection',
      templateUrl: 'app/partials/revenue-projection/revenue-projection.html',
      controller: 'RevenueProjectionController',
      title: 'Revenue Projection'
    })

    .state('main.air-traffic-data', {
      url: '/air-traffic-data',
      templateUrl: 'app/partials/air-traffic-data/air-traffic-data.html',
      controller: 'AirTrafficDataController',
      title: 'Data Analysis and Statistics - Air Traffic'
    })

    .state('main.revenue-data', {
      url: '/revenue-data',
      templateUrl: 'app/partials/revenue-data/revenue-data.html',
      controller: 'RevenueDataController',
      title: 'Data Analysis and Statistics - Revenue'
    })

    .state('main.repositioning-aerodrome-clusters', {
      url: '/repositioning-aerodrome-clusters',
      templateUrl: 'app/partials/repositioning-aerodrome-clusters/repositioning-aerodrome-clusters.html',
      controller: 'RepositioningAerodromeClustersController',
      title: 'Repositioning Aerodrome Clusters'
    })

    .state('main.flight-schedule-management', {
      url: '/flight-schedule-management',
      templateUrl: 'app/partials/flight-schedule-management/flight-schedule-management.html',
      controller: 'FlightScheduleManagementController',
      title: 'Flight Schedule Management'
    })

    .state('main.flight-reassignment', {
      url: '/flight-reassignment',
      templateUrl: 'app/partials/flight-reassignment/flight-reassignment.html',
      controller: 'FlightReassignmentScopeController',
      title: 'Flight Reassignment'
    })

    .state('main.welcome-page', {
      url: '/welcome-page',
      templateUrl: 'app/partials/welcome-page/welcome-page.html',
      title: 'Welcome to ARMS'
    })

    .state('main.local-aircraft-registry', {
      url: '/local-aircraft-registry',
      templateUrl: 'app/partials/local-aircraft-registry/local-aircraft-registry.html',
      controller: 'LocalAircraftRegistryController',
      title: 'Local Aircraft Registry'
    })

    .state('main.cached-events', {
      url: '/cached-events',
      templateUrl: 'app/partials/cached-events/cached-events.html',
      controller: 'CachedEventsController',
      title: 'Cached Events'
    })

    .state('main.plugins', {
      url: '/plugins/:hidden',
      templateUrl: 'app/partials/plugins/plugins.html',
      controller: 'PluginsController',
      title: 'Plugins',
      params: {
        hidden: { squash: true, value: null }
      }
    })

    .state('main.passenger-revenue-reconciliation', {
      url: '/passenger-revenue-reconciliation',
      templateUrl: 'app/partials/passenger-revenue-reconciliation/passenger-revenue-reconciliation.html',
      controller: 'PassengerRevenueReconciliationController',
      title: 'Passenger Revenue Reconciliation'
    })

    .state('main.sc-home-page', {
      url: '/sc-home-page',
      templateUrl: 'app/partials/sc-home-page/sc-home-page.html',
      controller: 'ScHomePageController',
      title: 'Home Page'
    })

    .state('main.sc-login', {
      url: '/sc-login',
      templateUrl: 'app/partials/sc-login/sc-login.html',
      controller: 'ScLoginController',
      title: 'Login'
    })

    .state('main.sc-query-submission', {
      url: '/sc-query-submission',
      templateUrl: 'app/partials/sc-query-submission/sc-query-submission.html',
      controller: 'ScQuerySubmissionController',
      title: 'Query Submission'
    })

    .state('main.sc-user-registration', {
      url: '/sc-user-registration',
      templateUrl: 'app/partials/sc-user-registration/sc-user-registration.html',
      controller: 'ScUserRegistrationController',
      title: 'User Registration'
    })

    .state('main.sc-user-registration-activate', {
      url: '/sc-user-registration-activate',
      templateUrl: 'app/partials/sc-user-registration/sc-user-registration-activate.html',
      controller: 'ScUserRegistrationController',
      title: 'Activating User Registration'
    })

    .state('main.sc-flight-cost-calculation', {
      url: '/sc-flight-cost-calculation',
      templateUrl: 'app/partials/sc-flight-cost-calculation/sc-flight-cost-calculation.html',
      controller: 'ScFlightCostCalculationController',
      title: 'Flight Cost Calculation'
    })

    .state('main.sc-user-management', {
      url: '/sc-user-management',
      templateUrl: 'app/partials/sc-user-management/sc-user-management.html',
      controller: 'ScUserManagementController',
      title: 'User Management'
    })

    .state('main.sc-account-management', {
      url: '/sc-account-management',
      templateUrl: 'app/partials/sc-account-management/sc-account-management.html',
      controller: 'ScAccountManagementController',
      title: 'Account Management'
    })

    .state('main.sc-flight-schedules', {
      url: '/sc-flight-schedules',
      templateUrl: 'app/partials/sc-flight-schedules/sc-flight-schedules.html',
      controller: 'ScFlightSchedulesController',
      title: 'Flight Schedules'
    })

    .state('main.sc-transactions', {
      url: '/sc-transactions',
      templateUrl: 'app/partials/sc-transactions/sc-transactions.html',
      controller: 'ScTransactionsController',
      title: 'Transactions'
    })

    .state('main.sc-invoices', {
      url: '/sc-invoices',
      templateUrl: 'app/partials/sc-invoices/sc-invoices.html',
      controller: 'ScInvoicesController',
      title: 'Invoices'
    })

    .state('main.sc-aircraft-registration', {
      url: '/sc-aircraft-registration',
      templateUrl: 'app/partials/sc-aircraft-registration/sc-aircraft-registration.html',
      controller: 'ScAircraftRegistrationController',
      title: 'Aircraft Registration'
    })

    .state('main.sc-flight-search', {
      url: '/sc-flight-search',
      templateUrl: 'app/partials/sc-flight-search/sc-flight-search.html',
      controller: 'ScFlightSearchController',
      title: 'Flight Search'
    })

    .state('main.sc-passenger-service-charge-return', {
      url: '/sc-passenger-service-charge-return',
      templateUrl: 'app/partials/sc-passenger-service-charge-return/sc-passenger-service-charge-return.html',
      controller: 'ScPassengerServiceChargeReturnController',
      title: 'Passenger Service Charge Return Management'
    })

    .state('main.sc-credit-payment', {
      url: '/sc-credit-payment',
      templateUrl: 'app/partials/sc-credit-payment/sc-credit-payment.html',
      controller: 'ScCreditPaymentController',
      title: 'Credit Payment'
    })

    .state('main.sc-report-generation', {
      url: '/sc-report-generation',
      templateUrl: 'app/partials/sc-report-generation/sc-report-generation.html',
      controller: 'ScReportGenerationController',
      title: 'Report Generation'
    })

    .state('main.current-user', {
      url: '/current-user',
      templateUrl: 'app/partials/current-user/current-user.html',
      controller: 'CurrentUserController',
      title: 'Current User'
    })

    .state('main.password-change', {
      url: '/password-change',
      templateUrl: 'app/partials/password-change/password-change.html',
      controller: 'PasswordChangeController',
      title: 'Password Change'
    })

    .state('main.aircraft-flights-exemptions', {
      url: '/aircraft-flights-exemptions',
      templateUrl: 'app/partials/aircraft-flights-exemptions/aircraft-flights-exemptions.html',
      controller: 'AircraftFlightsExemptionsController',
      title: 'Exempt Aircraft and Flights'
    })

    .state('main.sc-approval-request', {
      url: '/sc-approval-request',
      templateUrl: 'app/partials/sc-approval-request/sc-approval-request.html',
      controller: 'ScApprovalRequestController',
      title: 'Approval Request'
    })

    .state('main.service-outages', {
      url: '/service-outages',
      templateUrl: 'app/partials/service-outages/service-outages.html',
      controller: 'ServiceOutagesController',
      title: 'Service Outages'
    })

    .state('main.bank-account-management', {
      url: '/bank-account-management',
      templateUrl: 'app/partials/bank-account-management/bank-account-management.html',
      controller: 'BankAccountManagementController',
      title: 'Bank Account Management'
    })

    .state('main.interest-rates', {
      url: '/interest-rates',
      templateUrl: 'app/partials/interest-rates/interest-rates.html',
      controller: 'InterestRatesController',
      title: 'Interest Rates'
    })

    .state('main.amhs-connection', {
      url: '/amhs-connection',
      templateUrl: 'app/partials/amhs-connection/amhs-connection.html',
      controller: 'AMHSConnectionController',
      title: 'AMHS Connection'
    })

    .state('main.amhs-accounts', {
      url: '/amhs-accounts',
      templateUrl: 'app/partials/amhs-accounts/amhs-accounts.html',
      controller: 'AMHSAccountsController',
      title: 'AMHS Accounts'
    })

    .state('main.aerodrome-operational-hours', {
      url: '/aerodrome-operational-hours',
      templateUrl: 'app/partials/aerodrome-operational-hours/aerodrome-operational-hours.html',
      controller: 'AerodromeOperationalHoursController',
      title: 'Aerodrome Operational Hours'
    })

    .state('main.sc-inactivity-expiry-notice', {
      url: '/sc-inactivity-expiry-notice',
      templateUrl: 'app/partials/sc-inactivity-expiry-notice/sc-inactivity-expiry-notice.html',
      controller: 'ScInactivityExpiryNoticeController',
      title: 'Inactivity and Expiry Notices'
    });

  $urlRouterProvider.otherwise('/login');

  // use the HTML5 History API
  $locationProvider.html5Mode({
    enabled: false,
    requireBase: false
  });
}
