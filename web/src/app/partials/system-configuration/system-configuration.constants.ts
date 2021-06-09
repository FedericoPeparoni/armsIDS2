export enum SysConfigConstants {
  ANSP_CURRENCY = <any>'ANSP currency',
  DOM_PAX_CURRENCY = <any>'Domestic passenger fee currency',
  INTL_PAX_CURRENCY = <any>'International passenger fee currency',
  DEFAULT_ACCOUNT_CREDIT_LIMIT = <any>'Default account credit limit',
  DEFAULT_ACCOUNT_MIN_CREDIT_NOTE = <any>'Default account minimum credit note amount',
  DEFAULT_ACCOUNT_MAX_CREDIT_NOTE = <any>'Default account maximum credit note amount',
  DEFAULT_ACCOUNT_PAYMENT_TERMS = <any>'Default account payment terms',
  DEFAULT_ACCOUNT_MONTHLY_PENALTY = <any>'Default account monthly penalty rate',
  DEFAULT_ACCOUNT_PARKING_EXEMPTION = <any>'Default account parking exemption',
  DEFAULT_BILLING_PERIOD_NON_IATA = <any>'Default billing period for NON-IATA invoices',
  FIRST_DAY_OF_WEEK_NON_IATA = <any>'First day of week for NON-IATA invoices',
  DEFAULT_BILLING_PERIOD_IATA = <any>'Default billing period for IATA invoices',
  FIRST_DAY_OF_WEEK_IATA = <any>'First day of week for IATA invoices',
  DATE_FORMAT = <any>'Date format',
  DEP_TIME_RANGE_MIN = <any>'Minimum range for flight match',
  DEP_TIME_RANGE_EET_PERCENTAGE = <any>'Percentage of EET to be used for flight match',
  RADAR_SUMMARY_REQUIRED = <any>'Radar summary is required',
  SYSTEM_CONFIG_TRUE = <any>'t',
  RADAR_FLOOR_LEVEL = <any>'Radar floor level',
  ATC_LOG_REQUIRED = <any>'ATC log is required',
  TOWER_LOG_REQUIRED = <any>'Tower aircraft/passenger movement log is required',
  FLIGHT_PLAN_REQUIRED = <any>'Flight plan is required',
  PASSENGER_SERVICE_CHARGE_REQUIRED = <any>'Passenger service charge return is required',
  CROSSING_DISTANCE_PRECEDENCE = <any>'Crossing distance precedence',
  SEPARATE_INVOICE_NUMBERS_PER_BILLING_CENTER = <any>'Use separate invoice number sequence for each billing centre',
  SEPARATE_RECEIPT_NUMBERS_PER_BILLING_CENTER = <any>'Use separate receipt number sequence for each billing centre',
  EXEMPT_FLIGHTS_DISTANCE = <any>'Exempt flights shorter than distance',
  MIN_DOMESTIC_CROSSING_DISTANCE = <any>'Minimum domestic crossing distance',
  MIN_REGIONAL_CROSSING_DISTANCE = <any>'Minimum regional crossing distance',
  MIN_INTERNATIONAL_CROSSING_DISTANCE = <any>'Minimum international crossing distance',
  MIN_RANGE_FOR_FLIGHT_MATCH = <any>'Minimum range for flight match',
  MAX_DOMESTIC_CROSSING_DISTANCE = <any>'Maximum domestic crossing distance',
  MAX_REGIONAL_CROSSING_DISTANCE = <any>'Maximum regional crossing distance',
  MAX_INTERNATIONAL_CROSSING_DISTANCE = <any>'Maximum international crossing distance',
  DOMESTIC_PASSENGER_FEE_PERCENTAGE = <any>'Domestic passenger fee percentage',
  INTERNATIONAL_PASSENGER_FEE_PERCENTAGE = <any>'International passenger fee percentage',
  ROUTE_CACHE_COUNT = <any>'Route caching retention',
  PASSWORD_MINIMUM_LENGTH = <any>'Password minimum length',
  PASSWORD_LOWERCASE = <any>'Password lowercase required',
  PASSWORD_UPPERCASE = <any>'Password uppercase required',
  PASSWORD_NUMERIC = <any>'Password numeric required',
  PASSWORD_SPECIAL = <any>'Password special character required',
  ENTRY_EXIT_POINT_ROUNDING_DISTANCE = <any>'Entry/exit point rounding distance',
  PERCENT_EET_USED_FOR_FLIGHT_MATCH = <any>'Percentage of EET to be used for flight match',
  ATC_MOVEMENT_RETENTION = <any>'ATC movement logs retention',
  FLIGHT_MOVEMENT_RETENTION = <any>'Flight movement retention',
  LINE_ITEM_RETENTION = <any>'Line item retention',
  USER_EVENT_LOG_RETENTION = <any>'User event log retention',
  MTOW_UNIT_OF_MEASURE = <any>'MTOW unit of measure',
  AUTOMATED_UPLOAD_SCHEDULING = <any>'Automated upload scheduling',
  USE_MTOW_FACTOR_CLASS = <any>'Use MTOW factor class',
  COORDINATE_FORMAT = <any>'Format coordinates',
  DEGREES_MINUTES_SECONDS = <any>'degrees minutes seconds',
  AIR_NAVIGATION_CHARGES_CURRENCY = <any>'Air navigation charges currency',
  CALCULATE_PARKING_CHARGES = <any>'Calculate parking charges',
  CALCULATE_TASP_CHARGES = <any>'TASP charges support',
  CALCULATE_ARRIVAL_CHARGES = <any>'Late arrival charges support',
  CALCULATE_DEPARTURE_CHARGES = <any>'Late departures charges support',
  ORGANIZATION = <any>'Organisation name.  Used to determine site-specific processing.',
  RADAR_SUMMARY_FORMAT = <any>'Radar flight strip format',
  CACHED_EVENT_RETRY_INTERVAL = <any>'Cached event retry interval',
  DISTANCE_UNIT_OF_MEASURE = <any>'Distance unit of measure',
  APPROACH_FEES_LABEL = <any>'Approach fees label',
  MOCK_FATAL_ERROR_OCCURRENCE_FREQUENCY = <any>'Mock fatal error occurrence frequency',
  LANGUAGE_ENABLED = <any>'Language enabled',
  LANGUAGE_SELECTION = <any>'Language selection',
  LANGUAGE_SUPPORTED = <any>'Language supported',
  SYSTEM_CONFIG_FALSE = <any>'f',
  DUPL_OR_MISS_FLIGHTS_MIN_WIND = <any>'Minimum window to detect duplicate flights',
  DUPL_OR_MISS_FLIGHTS_EET_PERC = <any>'Percentage of EET to detect duplicate flights',
  FIRST_DAY_OF_FISCAL_YEAR = <any>'First day of fiscal year',
  REQUIRE_AERODROME_EXTERNAL_SYSTEM_ID = <any>'Require aerodrome external system id',
  REQUIRE_ACCOUNT_EXTERNAL_SYSTEM_ID = <any>'Require account external system id',
  REQUIRE_BANK_ACCOUNT_EXTERNAL_SYSTEM_ID = <any>'Require bank account external system id',
  REQUIRE_UNIFIED_TAX_EXTERNAL_SYSTEM_ID = <any>'Require unified text external system id',
  REQUIRE_BILLING_CENTRE_EXTERNAL_SYSTEM_ID = <any>'Require billing centre external system id',
  REQUIRE_CURRENCY_EXTERNAL_SYSTEM_ID = <any>'Require currency external system id',
  REQUIRE_SERVICE_CHARGE_CATALOGUE_EXTERNAL_SYSTEM_ID = <any>'Require service charge catalogue external system id',
  INVERSE_CURRENCY_RATE = <any>'Display inverse currency exchange rate',
  ROW_FOR_PAGE = <any>'Number of rows per page',
  POINT_OF_SALE_WORKFLOW = <any>'Point-of-sale workflow',
  MAP_NORTH_LATITUDE = <any>'Map north latitude',
  MAP_SOUTH_LATITUDE = <any>'Map south latitude',
  MAP_EAST_LONGITUDE = <any>'Map east longitude',
  MAP_WEST_LONGITUDE = <any>'Map west longitude',
  MAX_USERS_PER_ACCOUNT = <any>'Maximum web users per account',
  MAX_ACCOUNTS_PER_USER = <any>'Maximum accounts per web user',
  REQUIRE_ADMIN_APPROVAL_FOR_SC_ACCOUNTS = <any>'Require admin approval for self-care accounts',
  REQUIRE_ADMIN_APPROVAL_FOR_SC_AIRCRAFT_REGISTRATION = <any>'Require admin approval for self-care aircraft registration',
  REQUIRE_ADMIN_APPROVAL_FOR_SC_FLIGHT_SCHEDULES = <any>'Require admin approval for self-care flight schedules',
  APPROVAL_REQUEST_APPROVAL_RESPONSE = <any>'Approval request approval response',
  APPROVAL_REQUEST_REJECTION_RESPONSE = <any>'Approval request rejection response',
  AVIATION_ROUNDING = <any>'Aviation invoice total rounding',
  NONAVIATION_ROUNDING = <any>'Non-aviation invoice total rounding',
  REQUIRE_CAPTCHA = <any>'Require captcha for verification',
  REQUIRE_EMAIL_VERIFICATION = <any>'Require email verification on user registration',
  REQUIRE_INVOICE_MANUAL_APPROVAL = <any>'Require invoice manual approval',
  REQUIRE_INVOICE_MANUAL_PUBLISHING = <any>'Require invoice manual publishing',
  NUMBER_OF_DAYS_OF_FLIGHT_DATA = <any>'Number of days of flight data',
  IATA_INVOICE_SUPPORT = <any>'IATA invoicing support',
  TASP_FEES_LABEL = <any>'TASP fees label',
  PASSENGER_CHARGES_SUPPORT = <any>'Passenger charges support',
  INCLUDE_PASSENGER_CHARGES_ON_INVOICE = <any>'Include passenger charges on invoice',
  ANSP_COUNTRY_CODE = <any>'Country code',
  CREDIT_CONVERSTION_DATE = <any>'Aviation credit invoice currency conversion date',
  CASH_CONVERSION_DATE = <any>'Aviation cash invoice currency conversion date',
  INVOICE_FM_CATEGORY = <any>'Invoice by flight movement category',
  INVOICE_CURRENCY_ENROUTE = <any>'Invoice currency for enroute charges',
  BACKDATE_PAYMENT_ALLOWED = <any>'Backdate payment allowed',
  EXTENDED_PSCR_PASSENGER_INFORMATION_SUPPORT = <any>'Extended PSCR passenger information support',
  EXTENDED_PSCR_CARGO_INFORMATION_SUPPORT = <any>'Extended PSCR cargo information support',
  CARGO_DISPLAY_UNITS = <any>'Cargo display units',
  SMALL_AIRCRAFT_MAXIMUM_WEIGHT = <any>'Small aircraft maximum weight',
  SMALL_AIRCRAFT_MINIMUN_WEIGHT = <any>'Small aircraft minimum weight',
  MARK_ZERO_FLIGHT_COSTS_AS_PAID = <any>'Mark zero flight costs as paid',
  EXTENDED_HOURS_SURCHARGE_SUPPORT = <any>'Extended hours surcharge support',
  PROFORMA_INVOICE_SUPPORT = <any>'Proforma invoice support',
  POINT_OF_SALE_DEFAULT_ACCOUNT_SELECTION = <any>'Point of sale default account selection',
  WL_ENABLED = <any>'Whitelisting enabled',
  WL_START_DATE = <any>'Whitelisting start date',
  WL_INACTIVITY_PERIOD = <any>'Inactivity period',
  WL_EXPIRY_PERIOD = <any>'Expiry period',
  WL_INACTIVITY_NOTICE_TEXT = <any>'Inactivity notice text',
  WL_EXPIRY_NOTICE_TEXT = <any>'Expiry notice text',
  WL_AC_REG_NOTICE_TEXT = <any>'Aircraft registration notice text',
  WL_PREPAYMENT_NOTICE_TEXT = <any>'Prepayment notice text',
  WL_ACCEPTED_FLIGHT_NOTICE_TEXT = <any>'Accepted flight notice text',
  WL_DECLINED_FLIGHT_NOTICE_TEXT = <any>'Declined flight notice text',
  WL_ORIGINATOR_AFTN_ADDRESS = <any>'Originator AFTN address',
  WL_ACCEPTED_FLIGHT_NOTICE_ADDRESS = <any>'Accepted flight notice addresses',
  WL_DECLINED_FLIGHT_NOTICE_ADDRESS = <any>'Declined flight notice addresses',
  WL_FLIGHT_NOTICE_PRIORITY = <any>'Flight notice priority',
  CC_PROCESSOR = <any>'Credit card processor',
  CC_PROCESSOR_URL = <any>'Credit card processor URL',
  CC_PROCESSOR_CONFIGURED = <any>'Credit card processor configured',
  CC_PROCESSOR_PUBLIC_KEY = <any>'Credit card processor public key',
  CC_PROCESSOR_PRIVATE_KEY = <any>'Credit card processor private key',
  VALIDATE_FLIGHT_LEVEL_AIRSPACE = <any>'Validate flight level on airspace',
  USE_ADDITIONAL_INVOICES_NUMBER = <any>'Use additional invoice number sequence',
  USE_RECEIPT_NUMBER_BY_PAYMENT_MECHANISM = <any>'Use receipt number sequence by payment mechanism'
}
