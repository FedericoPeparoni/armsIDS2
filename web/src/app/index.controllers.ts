/** controllerInject */
import { ScReportGenerationController } from './partials/sc-report-generation/sc-report-generation.controller';
import { ScFlightSearchController } from './partials/sc-flight-search/sc-flight-search.controller';
import { ScInactivityExpiryNoticeController } from './partials/sc-inactivity-expiry-notice/sc-inactivity-expiry-notice.controller';
import { AerodromeOperationalHoursController } from './partials/aerodrome-operational-hours/aerodrome-operational-hours.controller';
import { InterestRatesController } from './partials/interest-rates/interest-rates.controller';
import { ServiceOutagesController } from './partials/service-outages/service-outages.controller';
import { ScApprovalRequestController } from './partials/sc-approval-request/sc-approval-request.controller';
import { ScCreditPaymentController } from './partials/sc-credit-payment/sc-credit-payment.controller';
import { PasswordChangeController } from './partials/password-change/password-change.controller';
import { AircraftFlightsExemptionsController } from './partials/aircraft-flights-exemptions/aircraft-flights-exemptions.controller';
import { ScPassengerServiceChargeReturnController } from './partials/sc-passenger-service-charge-return/sc-passenger-service-charge-return.controller';
import { ScInvoicesController } from './partials/sc-invoices/sc-invoices.controller';
import { ScTransactionsController } from './partials/sc-transactions/sc-transactions.controller';
import { ScFlightSchedulesController } from './partials/sc-flight-schedules/sc-flight-schedules.controller';
import { ScAircraftRegistrationController } from './partials/sc-aircraft-registration/sc-aircraft-registration.controller';
import { ScUserManagementController } from './partials/sc-user-management/sc-user-management.controller';
import { ScUserRegistrationController } from './partials/sc-user-registration/sc-user-registration.controller';
import { ScAccountManagementController } from './partials/sc-account-management/sc-account-management.controller';
import { ScFlightCostCalculationController } from './partials/sc-flight-cost-calculation/sc-flight-cost-calculation.controller';
import { ScQuerySubmissionController } from './partials/sc-query-submission/sc-query-submission.controller';
import { ScLoginController } from './partials/sc-login/sc-login.controller';
import { ScHomePageController } from './partials/sc-home-page/sc-home-page.controller';
import { CachedEventsController } from './partials/cached-events/cached-events.controller';
import { FlightScheduleManagementController } from './partials/flight-schedule-management/flight-schedule-management.controller';
import { LocalAircraftRegistryController } from './partials/local-aircraft-registry/local-aircraft-registry.controller';
import { UserEventLogController } from './partials/user-event-log/user-event-log.controller';
import { HelpController } from './partials/help/help.controller';
import { ApplicationManagementController } from './partials/application-management/application-management.controller';
import { NonAviationBillingEngineController } from './partials/non-aviation-billing-engine/non-aviation-billing-engine.controller';
import { RevenueDataController } from './partials/revenue-data/revenue-data.controller';
import { RejectedItemsController } from './partials/rejected-items/rejected-items.controller';
import { RecurringChargesController } from './partials/recurring-charges/recurring-charges.controller';
import { ReportGenerationController } from './partials/report-generation/report-generation.controller';
import { AirTrafficDataController } from './partials/air-traffic-data/air-traffic-data.controller';
import { ReportTemplatesController } from './partials/report-templates/report-templates.controller';
import { RepositioningAerodromeClustersController } from './partials/repositioning-aerodrome-clusters/repositioning-aerodrome-clusters.controller';
import { SystemSummaryController } from './partials/system-summary/system-summary.controller';
import { TowerMovementLogsController } from './partials/tower-movement-logs/tower-movement-logs.controller';
import { AtcMovementLogController } from './partials/atc-movement-log/atc-movement-log.controller';
import { BillingCentreManagementController } from './partials/billing-centre-management/billing-centre-management.controller';
import { UnspecifiedDepDestLocationsController } from './partials/unspecified-dep-dest-locations/unspecified-dep-dest-locations.controller';
import { NominalRoutesController } from './partials/nominal-routes/nominal-routes.controller';
import { FlightStatusExemptionsController } from './partials/flight-status-exemptions/flight-status-exemptions.controller';
import { FlightRouteExemptionsController } from './partials/flight-route-exemptions/flight-route-exemptions.controller';
import { AircraftUnspecifiedManagementController } from './partials/aircraft-unspecified-management/aircraft-unspecified-management.controller';
import { RadarSummaryController } from './partials/radar-summary/radar-summary.controller';
import { InvoiceTemplateManagementController } from './partials/invoice-template-management/invoice-template-management.controller';
import { EnrouteAirNavigationChargesManagementController } from './partials/enroute-air-navigation-charges-management/enroute-air-navigation-charges-management.controller';
import { CatalogueServiceChargeController } from './partials/catalogue-service-charge/catalogue-service-charge.controller';
import { InvoiceGenerationController } from './partials/invoice-generation/invoice-generation.controller';
import { FlightMovementManagementController } from './partials/flight-movement-management/flight-movement-management.controller';
import { AirspaceManagementController } from './partials/airspace-management/airspace-management.controller';
import { AccountExemptManagementController } from './partials/account-exempt-management/account-exempt-management.controller';
import { UtilitiesTownsController } from './partials/utilities-towns/utilities-towns.controller';
import { UtilitiesSchedulesController } from './partials/utilities-schedules/utilities-schedules.controller';
import { RegionalCountryManagementController } from './partials/regional-country-management/regional-country-management.controller';
import { MapController } from './partials/map/map.controller';
import { AircraftExemptManagementController } from './partials/aircraft-exempt-management/aircraft-exempt-management.controller';
import { MtowController } from './partials/mtow/mtow.controller';
import { InvoicesController } from './partials/invoices/invoices.controller';
import { AviationBillingEngineController } from './partials/aviation-billing-engine/aviation-billing-engine.controller';
import { AirNavigationChargesController } from './partials/air-navigation-charges/air-navigation-charges.controller';
import { SystemConfigurationController } from './partials/system-configuration/system-configuration.controller';
import { PassengerServiceChargeReturnController } from './partials/passenger-service-charge-return/passenger-service-charge-return.controller';
import { TransactionsController } from './partials/transactions/transactions.controller';
import { AircraftRegistrationController } from './partials/aircraft-registration/aircraft-registration.controller';
import { CurrencyManagementController } from './partials/currency-management/currency-management.controller';
import { AccountsController } from './partials/accounts/accounts.controller';
import { AerodromeCategoryManagementController } from './partials/aerodrome-category-management/aerodrome-category-management.controller';
import { AerodromesController } from './partials/aerodromes/aerodromes.controller';
import { MainController } from './main/main.controller';
import { UsersController } from './partials/users/users.controller';
import { LoginController } from './login/login.controller';
import { AMHSConnectionController } from './partials/amhs-connection/amhs-connection.controller';
import { AMHSAccountsController } from './partials/amhs-accounts/amhs-accounts.controller';
import { AircraftTypeManagementController } from './partials/aircraft-type-management/aircraft-type-management.controller';
import { GroupManagementController } from './partials/group-management/group-management.controller';
import { AboutController } from './partials/about/about.controller';
import { FlightMovementTableController } from './components/directives/flight-movement-table/flight-movement-table.controller';
import { CountryManagementController } from './partials/country-management/country-management.controller';
import { PluginsController } from './partials/plugins/plugins.controller';
import { SystemConfigurationItemController } from './components/directives/system-configuration-item/system-configuration-item.controller';
import { PassengerRevenueReconciliationController } from './partials/passenger-revenue-reconciliation/passenger-revenue-reconciliation.controller';
import { TableSortController } from './angular-ids-project/src/components/tableSort/tableSort.controller';
import { TransactionsPendingController } from './partials/transactions-pending/transactions-pending.controller';
import { TransactionsWorkflowController } from './partials/transactions-workflow/transactions-workflow.controller';
import { RevenueProjectionController } from './partials/revenue-projection/revenue-projection.controller';
import { CurrentUserController } from './partials/current-user/current-user.controller';
import { FlightReassignmentScopeController } from './partials/flight-reassignment/flight-reassignment.controller';
import { BankAccountManagementController } from './partials/bank-account-management/bank-account-management.controller';
import { UnifiedTaxManagementController } from './partials/unified-tax-management/unified-tax-management.controller';

// controller module
export default angular.module('armsWeb.controllers', [])

  /** bindControllerInject */
  .controller('ScReportGenerationController', ScReportGenerationController)
  .controller('ScFlightSearchController', ScFlightSearchController)
  .controller('ScInactivityExpiryNoticeController', ScInactivityExpiryNoticeController)
  .controller('AerodromeOperationalHoursController', AerodromeOperationalHoursController)
  .controller('InterestRatesController', InterestRatesController)
  .controller('ServiceOutagesController', ServiceOutagesController)
  .controller('ScApprovalRequestController', ScApprovalRequestController)
  .controller('PasswordChangeController', PasswordChangeController)
  .controller('AircraftFlightsExemptionsController', AircraftFlightsExemptionsController)
  .controller('ScPassengerServiceChargeReturnController', ScPassengerServiceChargeReturnController)
  .controller('ScCreditPaymentController', ScCreditPaymentController)
  .controller('ScInvoicesController', ScInvoicesController)
  .controller('ScTransactionsController', ScTransactionsController)
  .controller('ScFlightSchedulesController', ScFlightSchedulesController)
  .controller('ScAircraftRegistrationController', ScAircraftRegistrationController)
  .controller('ScUserManagementController', ScUserManagementController)
  .controller('ScUserRegistrationController', ScUserRegistrationController)
  .controller('ScAccountManagementController', ScAccountManagementController)
  .controller('ScFlightCostCalculationController', ScFlightCostCalculationController)
  .controller('ScQuerySubmissionController', ScQuerySubmissionController)
  .controller('ScLoginController', ScLoginController)
  .controller('ScHomePageController', ScHomePageController)
  .controller('CachedEventsController', CachedEventsController)
  .controller('FlightScheduleManagementController', FlightScheduleManagementController)
  .controller('LocalAircraftRegistryController', LocalAircraftRegistryController)
  .controller('UserEventLogController', UserEventLogController)
  .controller('HelpController', HelpController)
  .controller('ApplicationManagementController', ApplicationManagementController)
  .controller('NonAviationBillingEngineController', NonAviationBillingEngineController)
  .controller('RevenueDataController', RevenueDataController)
  .controller('RejectedItemsController', RejectedItemsController)
  .controller('RecurringChargesController', RecurringChargesController)
  .controller('ReportGenerationController', ReportGenerationController)
  .controller('AirTrafficDataController', AirTrafficDataController)
  .controller('ReportTemplatesController', ReportTemplatesController)
  .controller('TowerMovementLogsController', TowerMovementLogsController)
  .controller('RepositioningAerodromeClustersController', RepositioningAerodromeClustersController)
  .controller('SystemSummaryController', SystemSummaryController)
  .controller('AtcMovementLogController', AtcMovementLogController)
  .controller('BillingCentreManagementController', BillingCentreManagementController)
  .controller('UnspecifiedDepDestLocationsController', UnspecifiedDepDestLocationsController)
  .controller('NominalRoutesController', NominalRoutesController)
  .controller('FlightStatusExemptionsController', FlightStatusExemptionsController)
  .controller('FlightRouteExemptionsController', FlightRouteExemptionsController)
  .controller('AircraftUnspecifiedManagementController', AircraftUnspecifiedManagementController)
  .controller('RadarSummaryController', RadarSummaryController)
  .controller('InvoiceTemplateManagementController', InvoiceTemplateManagementController)
  .controller('EnrouteAirNavigationChargesManagementController', EnrouteAirNavigationChargesManagementController)
  .controller('CatalogueServiceChargeController', CatalogueServiceChargeController)
  .controller('InvoiceGenerationController', InvoiceGenerationController)
  .controller('FlightMovementManagementController', FlightMovementManagementController)
  .controller('AirspaceManagementController', AirspaceManagementController)
  .controller('AccountExemptManagementController', AccountExemptManagementController)
  .controller('UtilitiesTownsController', UtilitiesTownsController)
  .controller('UtilitiesSchedulesController', UtilitiesSchedulesController)
  .controller('RegionalCountryManagementController', RegionalCountryManagementController)
  .controller('MapController', MapController)
  .controller('AircraftExemptManagementController', AircraftExemptManagementController)
  .controller('MtowController', MtowController)
  .controller('InvoicesController', InvoicesController)
  .controller('AviationBillingEngineController', AviationBillingEngineController)
  .controller('AirNavigationChargesController', AirNavigationChargesController)
  .controller('SystemConfigurationController', SystemConfigurationController)
  .controller('PassengerServiceChargeReturnController', PassengerServiceChargeReturnController)
  .controller('TransactionsController', TransactionsController)
  .controller('AircraftRegistrationController', AircraftRegistrationController)
  .controller('CurrencyManagementController', CurrencyManagementController)
  .controller('AccountsController', AccountsController)
  .controller('AerodromeCategoryManagementController', AerodromeCategoryManagementController)
  .controller('AerodromesController', AerodromesController)
  .controller('MainController', MainController)
  .controller('UsersController', UsersController)
  .controller('LoginController', LoginController)
  .controller('AMHSConnectionController', AMHSConnectionController)
  .controller('AMHSAccountsController', AMHSAccountsController)
  .controller('AircraftTypeManagementController', AircraftTypeManagementController)
  .controller('GroupManagementController', GroupManagementController)
  .controller('AboutController', AboutController)
  .controller('FlightMovementTableController', FlightMovementTableController)
  .controller('CountryManagementController', CountryManagementController)
  .controller('PluginsController', PluginsController)
  .controller('SystemConfigurationItemController', SystemConfigurationItemController)
  .controller('PassengerRevenueReconciliationController', PassengerRevenueReconciliationController)
  .controller('TableSortController', TableSortController)
  .controller('TransactionsPendingController', TransactionsPendingController)
  .controller('TransactionsWorkflowController', TransactionsWorkflowController)
  .controller('RevenueProjectionController', RevenueProjectionController)
  .controller('CurrentUserController', CurrentUserController)
  .controller('FlightReassignmentScopeController', FlightReassignmentScopeController)
  .controller('BankAccountManagementController', BankAccountManagementController)
  .controller('UnifiedTaxManagementController', UnifiedTaxManagementController);
