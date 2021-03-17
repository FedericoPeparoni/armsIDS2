/** serviceInject */
import { ScReportGenerationService } from './partials/sc-report-generation/service/sc-report-generation.service';
import { ScFlightSearchService } from './partials/sc-flight-search/service/sc-flight-search.service';
import { ScInactivityExpiryNoticeService } from './partials/sc-inactivity-expiry-notice/service/sc-inactivity-expiry-notice.service';
import { AerodromeOperationalHoursService } from './partials/aerodrome-operational-hours/service/aerodrome-operational-hours.service';
import { InterestRatesService } from './partials/interest-rates/service/interest-rates.service';
import { ServiceOutagesService } from './partials/service-outages/service/service-outages.service';
import { ScApprovalRequestService } from './partials/sc-approval-request/service/sc-approval-request.service';
import { PasswordChangeService } from './partials/password-change/service/password-change.service';
import { AircraftFlightsExemptionsService } from './partials/aircraft-flights-exemptions/service/aircraft-flights-exemptions.service';
import { ScPassengerServiceChargeReturnService } from './partials/sc-passenger-service-charge-return/service/sc-passenger-service-charge-return.service';
import { ScCreditPaymentService } from './partials/sc-credit-payment/service/sc-credit-payment.service';
import { ScInvoicesService } from './partials/sc-invoices/service/sc-invoices.service';
import { ScTransactionsService } from './partials/sc-transactions/service/sc-transactions.service';
import { ScFlightSchedulesService } from './partials/sc-flight-schedules/service/sc-flight-schedules.service';
import { ScAircraftRegistrationService } from './partials/sc-aircraft-registration/service/sc-aircraft-registration.service';
import { ScUserManagementService } from './partials/sc-user-management/service/sc-user-management.service';
import { ScUserRegistrationService } from './partials/sc-user-registration/service/sc-user-registration.service';
import { ScAccountManagementService } from './partials/sc-account-management/service/sc-account-management.service';
import { ScFlightCostCalculationService } from './partials/sc-flight-cost-calculation/service/sc-flight-cost-calculation.service';
import { ScQuerySubmissionService } from './partials/sc-query-submission/service/sc-query-submission.service';
import { ScLoginService } from './partials/sc-login/service/sc-login.service';
import { CachedEventsService } from './partials/cached-events/service/cached-events.service';
import { FlightScheduleManagementService } from './partials/flight-schedule-management/service/flight-schedule-management.service';
import { LocalAircraftRegistryService } from './partials/local-aircraft-registry/service/local-aircraft-registry.service';
import { UserEventLogService } from './partials/user-event-log/service/user-event-log.service';
import { ApplicationManagementService } from './partials/application-management/service/application-management.service';
import { NonAviationBillingEngineService } from './partials/non-aviation-billing-engine/service/non-aviation-billing-engine.service';
import { RevenueDataService } from './partials/revenue-data/service/revenue-data.service';
import { RejectedItemsService } from './partials/rejected-items/service/rejected-items.service';
import { RecurringChargesService } from './partials/recurring-charges/service/recurring-charges.service';
import { ReportGenerationService } from './partials/report-generation/service/report-generation.service';
import { AirTrafficDataService } from './partials/air-traffic-data/service/air-traffic-data.service';
import { ReportTemplatesService } from './partials/report-templates/service/report-templates.service';
import { TowerMovementLogsService } from './partials/tower-movement-logs/service/tower-movement-logs.service';
import { RepositioningAerodromeClustersService } from './partials/repositioning-aerodrome-clusters/service/repositioning-aerodrome-clusters.service';
import { SystemSummaryService } from './partials/system-summary/service/system-summary.service';
import { AtcMovementLogService } from './partials/atc-movement-log/service/atc-movement-log.service';
import { BillingCentreManagementService } from './partials/billing-centre-management/service/billing-centre-management.service';
import { UnspecifiedDepDestLocationsService } from './partials/unspecified-dep-dest-locations/service/unspecified-dep-dest-locations.service';
import { NominalRoutesService } from './partials/nominal-routes/service/nominal-routes.service';
import { FlightStatusExemptionsService } from './partials/flight-status-exemptions/service/flight-status-exemptions.service';
import { FlightRouteExemptionsService } from './partials/flight-route-exemptions/service/flight-route-exemptions.service';
import { AircraftUnspecifiedManagementService } from './partials/aircraft-unspecified-management/service/aircraft-unspecified-management.service';
import { RadarSummaryService } from './partials/radar-summary/service/radar-summary.service';
import { InvoiceTemplateManagementService } from './partials/invoice-template-management/service/invoice-template-management.service';
import { EnrouteAirNavigationChargesManagementService } from './partials/enroute-air-navigation-charges-management/service/enroute-air-navigation-charges-management.service';
import { CatalogueServiceChargeService } from './partials/catalogue-service-charge/service/catalogue-service-charge.service';
import { FlightMovementManagementService } from './partials/flight-movement-management/service/flight-movement-management.service';
import { FlightTypeService } from './partials/flight-types/service/flight-types.service';
import { AirspaceManagementService } from './partials/airspace-management/service/airspace-management.service';
import { AccountExemptManagementService } from './partials/account-exempt-management/service/account-exempt-management.service';
import { UtilitiesTownsService } from './partials/utilities-towns/service/utilities-towns.service';
import { UtilitiesSchedulesService } from './partials/utilities-schedules/service/utilities-schedules.service';
import { RegionalCountryManagementService } from './partials/regional-country-management/service/regional-country-management.service';
import { AircraftExemptManagementService } from './partials/aircraft-exempt-management/service/aircraft-exempt-management.service';
import { MtowService } from './partials/mtow/service/mtow.service';
import { InvoiceStateTypeService } from './partials/invoice-state-type/service/invoice-state-type.service';
import { InvoicesService } from './partials/invoices/service/invoices.service';
import { AirNavigationChargesService } from './partials/air-navigation-charges/service/air-navigation-charges.service';
import { SystemConfigurationService } from './partials/system-configuration/service/system-configuration.service';
import { PassengerServiceChargeReturnService } from './partials/passenger-service-charge-return/service/passenger-service-charge-return.service';
import { TransactionsService } from './partials/transactions/service/transactions.service';
import { AircraftRegistrationService } from './partials/aircraft-registration/service/aircraft-registration.service';
import { WakeTurbulenceCategoryService } from './partials/wake-turbulence-category/service/wake-turbulence-category.service';
import { RolesService } from './partials/roles/service/roles.service';
import { PermissionsService } from './partials/permissions/service/permissions.service';
import { TypesService } from './partials/types/service/types.service';
import { CountryManagementService } from './partials/country-management/service/country-management.service';
import { CurrencyManagementService } from './partials/currency-management/service/currency-management.service';
import { AccountsService } from './partials/accounts/service/accounts.service';
import { AerodromeCategoryManagementService } from './partials/aerodrome-category-management/service/aerodrome-category-management.service';
import { AerodromesService } from './partials/aerodromes/service/aerodromes.service';
import { UsersService } from './partials/users/service/users.service';
import { LoginService } from './login/service/login.service';
import { ConfigService } from './angular-ids-project/src/components/services/config/config.service';
import { AircraftTypeManagementService } from './partials/aircraft-type-management/service/aircraft-type-management.service';
import { SaveLocalTemplateService } from './angular-ids-project/src/components/services/saveLocalTemplate/saveLocalTemplate.service';
import { OAuthService } from './components/services/oauth/oauth.service';
import { TransactionTypesService } from './partials/transaction-types/service/transaction-types.service';
import { AboutService } from './partials/about/service/about.service';
import { RouteSegmentsService } from './partials/route-segments/service/route-segments.service';
import { CurrencyExchangeRatesService } from './partials/currency-exchange-rates/service/currency-exchange-rates.service';
import { ReportsService } from './partials/reports/service/reports.service';
import { DbqueryService } from './partials/dbquery/service/dbquery.service';
import { AviationBillingEngineService } from './partials/aviation-billing-engine/service/aviation-billing-engine.service';
import { ConvertItemTypeService } from './partials/convert-item-type/service/convert-item-type.service';
import { DebounceService } from './angular-ids-project/src/components/services/debounce/debounce.service';
import { PluginsService } from './partials/plugins/service/plugins.service';
import { LanguagesService } from './partials/languages/service/languages.service';
import { LocaleSwitcherService } from './angular-ids-project/src/helpers/services/localeSwitcher.service';
import { AMHSConnectionService } from './partials/amhs-connection/service/amhs-connection.service';
import { AMHSAccountsService } from './partials/amhs-accounts/service/amhs-accounts.service';
import { TransactionsPendingService } from './partials/transactions-pending/service/transactions-pending.service';
import { TransactionsWorkflowService } from './partials/transactions-workflow/service/transactions-workflow.service';
import { TranslateService } from './run/translate.run';
import { ReleaseNoteService } from './components/services/release-notes/release-notes.service';
import { ExternalDatabaseInputService } from './components/directives/external-database-input/external-database-input.service';
import { WindowLocationService } from './components/services/window-location/window-location.service';
import { SysConfigBoolean } from './angular-ids-project/src/components/services/sysConfigBoolean/sysConfigBoolean.service';
import { RevenueProjectionService } from './partials/revenue-projection/service/revenue-projection.service';
import { CustomDate } from './angular-ids-project/src/components/services/customDate/customDate.service';
import { ExternalChargeCategoryService } from './partials/external-charge-category/service/external-charge-category.service';
import { AccountExternalChargeCategoryService } from './partials/account-external-charge-category/service/account-external-charge-category.service';
import { ServerDatetimeService } from './angular-ids-project/src/components/server-datetime/server-datetime.service';
import { SelfCareHelper } from './angular-ids-project/src/components/services/selfCareHelper/selfCareHelper.service';
import { DownloadService } from './angular-ids-project/src/helpers/services/download.service';
import { FlightMovementCategoryService } from './partials/flight-movement-category/service/flight-movement-category.service';
import { FlightReassignmentService } from './partials/flight-reassignment/service/flight-reassignment.service';
import { ChargeAdjustmentsService } from './partials/charge-adjustments/service/charge-adjustments.service';
import { OrganizationService } from './partials/organization/service/organization.service';
import { BankAccountManagementService } from './partials/bank-account-management/service/bank-account-management.service';
import { RecaptchaService } from './angular-ids-project/src/components/recaptcha/service/recaptcha.service';
import { UnifiedTaxManagementService } from './partials/unified-tax-management/service/unified-tax-management.service';
import { UnifiedTaxValidityManagementService } from './partials/unified-tax-management/service/unified-tax-validity-management.service';
// service module
export default angular.module('armsWeb.services', [])

  /** bindServiceInject */
  .service('scReportGenerationService', ScReportGenerationService)
  .service('scFlightSearchService', ScFlightSearchService)
  .service('scInactivityExpiryNoticeService', ScInactivityExpiryNoticeService)
  .service('aerodromeOperationalHoursService', AerodromeOperationalHoursService)
  .service('interestRatesService', InterestRatesService)
  .service('serviceOutagesService', ServiceOutagesService)
  .service('scApprovalRequestService', ScApprovalRequestService)
  .service('passwordChangeService', PasswordChangeService)
  .service('aircraftFlightsExemptionsService', AircraftFlightsExemptionsService)
  .service('scCreditPaymentService', ScCreditPaymentService)
  .service('scPassengerServiceChargeReturnService', ScPassengerServiceChargeReturnService)
  .service('scInvoicesService', ScInvoicesService)
  .service('scTransactionsService', ScTransactionsService)
  .service('scFlightSchedulesService', ScFlightSchedulesService)
  .service('scAircraftRegistrationService', ScAircraftRegistrationService)
  .service('scUserManagementService', ScUserManagementService)
  .service('scUserRegistrationService', ScUserRegistrationService)
  .service('scAccountManagementService', ScAccountManagementService)
  .service('scFlightCostCalculationService', ScFlightCostCalculationService)
  .service('scQuerySubmissionService', ScQuerySubmissionService)
  .service('scLoginService', ScLoginService)
  .service('cachedEventsService', CachedEventsService)
  .service('flightScheduleManagementService', FlightScheduleManagementService)
  .service('localAircraftRegistryService', LocalAircraftRegistryService)
  .service('userEventLogService', UserEventLogService)
  .service('applicationManagementService', ApplicationManagementService)
  .service('nonAviationBillingEngineService', NonAviationBillingEngineService)
  .service('revenueDataService', RevenueDataService)
  .service('rejectedItemsService', RejectedItemsService)
  .service('recurringChargesService', RecurringChargesService)
  .service('reportGenerationService', ReportGenerationService)
  .service('airTrafficDataService', AirTrafficDataService)
  .service('reportTemplatesService', ReportTemplatesService)
  .service('towerMovementLogsService', TowerMovementLogsService)
  .service('repositioningAerodromeClustersService', RepositioningAerodromeClustersService)
  .service('systemSummaryService', SystemSummaryService)
  .service('atcMovementLogService', AtcMovementLogService)
  .service('billingCentreManagementService', BillingCentreManagementService)
  .service('unspecifiedDepDestLocationsService', UnspecifiedDepDestLocationsService)
  .service('nominalRoutesService', NominalRoutesService)
  .service('flightStatusExemptionsService', FlightStatusExemptionsService)
  .service('flightRouteExemptionsService', FlightRouteExemptionsService)
  .service('aircraftUnspecifiedManagementService', AircraftUnspecifiedManagementService)
  .service('radarSummaryService', RadarSummaryService)
  .service('invoiceTemplateManagementService', InvoiceTemplateManagementService)
  .service('enrouteAirNavigationChargesManagementService', EnrouteAirNavigationChargesManagementService)
  .service('catalogueServiceChargeService', CatalogueServiceChargeService)
  .service('flightMovementManagementService', FlightMovementManagementService)
  .service('flightTypeService', FlightTypeService)
  .service('airspaceManagementService', AirspaceManagementService)
  .service('accountExemptManagementService', AccountExemptManagementService)
  .service('utilitiesTownsService', UtilitiesTownsService)
  .service('utilitiesSchedulesService', UtilitiesSchedulesService)
  .service('regionalCountryManagementService', RegionalCountryManagementService)
  .service('aircraftExemptManagementService', AircraftExemptManagementService)
  .service('mtowService', MtowService)
  .service('invoiceStateTypeService', InvoiceStateTypeService)
  .service('invoicesService', InvoicesService)
  .service('airNavigationChargesService', AirNavigationChargesService)
  .service('systemConfigurationService', SystemConfigurationService)
  .service('passengerServiceChargeReturnService', PassengerServiceChargeReturnService)
  .service('transactionsService', TransactionsService)
  .service('aircraftRegistrationService', AircraftRegistrationService)
  .service('wakeTurbulenceCategoryService', WakeTurbulenceCategoryService)
  .service('typesService', TypesService)
  .service('countryManagementService', CountryManagementService)
  .service('currencyManagementService', CurrencyManagementService)
  .service('rolesService', RolesService)
  .service('permissionsService', PermissionsService)
  .service('accountsService', AccountsService)
  .service('aerodromeCategoryManagementService', AerodromeCategoryManagementService)
  .service('aerodromesService', AerodromesService)
  .service('usersService', UsersService)
  .service('loginService', LoginService)
  .service('configService', ConfigService)
  .service('aircraftTypeManagementService', AircraftTypeManagementService)
  .service('oAuthService', OAuthService)
  .service('transactionTypesService', TransactionTypesService)
  .service('aboutService', AboutService)
  .service('routeSegmentsService', RouteSegmentsService)
  .service('currencyExchangeRatesService', CurrencyExchangeRatesService)
  .service('reportsService', ReportsService)
  .service('dbqueryService', DbqueryService)
  .service('aviationBillingEngineService', AviationBillingEngineService)
  .service('saveLocalTemplateService', SaveLocalTemplateService)
  .service('convertItemTypeService', ConvertItemTypeService)
  .service('debounceService', DebounceService)
  .service('pluginsService', PluginsService)
  .service('languagesService', LanguagesService)
  .service('localeSwitcherService', LocaleSwitcherService)
  .service('amhsConnectionService', AMHSConnectionService)
  .service('amhsAccountsService', AMHSAccountsService)
  .service('transactionsPendingService', TransactionsPendingService)
  .service('transactionsWorkflowService', TransactionsWorkflowService)
  .service('translateService', TranslateService)
  .service('releaseNoteService', ReleaseNoteService)
  .service('externalDatabaseInputService', ExternalDatabaseInputService)
  .service('windowLocationService', WindowLocationService)
  .service('revenueProjectionService', RevenueProjectionService)
  .service('customDate', CustomDate)
  .service('externalChargeCategoryService', ExternalChargeCategoryService)
  .service('accountExternalChargeCategoryService', AccountExternalChargeCategoryService)
  .service('serverDatetimeService', ServerDatetimeService)
  .service('selfCareHelper', SelfCareHelper)
  .service('sysConfigBoolean', SysConfigBoolean)
  .service('flightMovementCategoryService', FlightMovementCategoryService)
  .service('flightReassignmentService', FlightReassignmentService)
  .service('downloadService', DownloadService)
  .service('chargeAdjustmentsService', ChargeAdjustmentsService)
  .service('organizationService', OrganizationService)
  .service('bankAccountManagementService', BankAccountManagementService)
  .service('recaptchaService', RecaptchaService)
  .service('unifiedTaxManagementService', UnifiedTaxManagementService)
  .service('unifiedTaxValidityManagementService', UnifiedTaxValidityManagementService);
