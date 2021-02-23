package ca.ids.abms.modules.reports2.common;

import ca.ids.abms.config.error.CustomParametrizedException;
import ca.ids.abms.config.error.ExceptionFactory;
import ca.ids.abms.config.security.SecurityUtils;
import ca.ids.abms.modules.accounts.Account;
import ca.ids.abms.modules.accounts.AccountExternalChargeCategory;
import ca.ids.abms.modules.accounts.AccountExternalChargeCategoryService;
import ca.ids.abms.modules.accounts.AccountService;
import ca.ids.abms.modules.billingcenters.BillingCenter;
import ca.ids.abms.modules.billingcenters.BillingCenterService;
import ca.ids.abms.modules.billings.BillingLedger;
import ca.ids.abms.modules.billings.BillingLedgerFlightUtility;
import ca.ids.abms.modules.billings.BillingLedgerRepository;
import ca.ids.abms.modules.billings.InvoicesApprovalWorkflow;
import ca.ids.abms.modules.common.enumerators.InvoiceStateType;
import ca.ids.abms.modules.currencies.Currency;
import ca.ids.abms.modules.flightmovements.FlightMovement;
import ca.ids.abms.modules.flightmovements.enumerate.FlightmovementCategoryType;
import ca.ids.abms.modules.flightmovementsbuilder.FlightMovementBuilderException;
import ca.ids.abms.modules.flightmovementsbuilder.utility.FlightMovementBuilderUtility;
import ca.ids.abms.modules.invoices.InvoiceTemplate;
import ca.ids.abms.modules.invoices.InvoiceTemplateRepository;
import ca.ids.abms.modules.reports2.InvoiceTemplateCategory;
import ca.ids.abms.modules.system.BillingOrgCode;
import ca.ids.abms.modules.system.SystemConfigurationService;
import ca.ids.abms.modules.system.summary.SystemConfigurationItemName;
import ca.ids.abms.modules.transactions.Transaction;
import ca.ids.abms.modules.transactions.TransactionRepository;
import ca.ids.abms.modules.transactions.TransactionType;
import ca.ids.abms.modules.transactions.TransactionTypeService;
import ca.ids.abms.modules.users.User;
import ca.ids.abms.modules.users.UserService;
import ca.ids.abms.util.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import javax.validation.constraints.NotNull;
import java.io.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for report generation
 */
@Component
public class ReportHelper {

	private static final Logger LOG = LoggerFactory.getLogger(ReportHelper.class);

    // convert ton to kg
    public static final double TO_KG = 907.18474;

    // convert km to nautical mile
    public static final double TO_NM = 0.539957;

    /** UTC time zone ID */
    public static final ZoneId UTC_ZONE_ID = ZoneId.of("UTC");

    /** Default date format */
    private static final String DFLT_DATE_FORMAT = "YYYY-mm-dd";

    /** Transaction types */
    private static final String TRANSACTION_TYPE_CREDIT = "credit";

    private static final String REPORT_TEMPLATE_DIRECTORY = File.separator	+ "reports";

    private static final String REPORT_INVOICE_TEMPLATE_DIRECTORY = REPORT_TEMPLATE_DIRECTORY + File.separator	+ "invoices";

    private static final String REPORT_OTHER_TEMPLATE_DIRECTORY = REPORT_TEMPLATE_DIRECTORY + File.separator + "other";

    private final InvoicesApprovalWorkflow invoicesApprovalWorkflow;

    @SuppressWarnings("squid:S00107") //Methods should not have too many parameters
    public ReportHelper (final BillingCenterService billingCenterService,
                         final UserService userService,
                         final FlightMovementBuilderUtility flightMovementBuilderUtility,
                         final AccountService accountService,
                         final TransactionTypeService transactionTypeService,
                         final SystemConfigurationService systemConfigurationService,
                         final BillingLedgerFlightUtility billingLedgerFlightUtility,
                         final BillingLedgerRepository billingLedgerRepository,
                         final TransactionRepository transactionRepository,
                         final InvoiceTemplateRepository invoiceTemplateRepository,
                         final InvoicesApprovalWorkflow invoicesApprovalWorkflow,
                         final AccountExternalChargeCategoryService accountExternalChargeCategoryService) {
        this.billingCenterService = billingCenterService;
        this.userService = userService;
        this.flightMovementBuilderUtility = flightMovementBuilderUtility;
        this.accountService = accountService;
        this.transactionTypeService = transactionTypeService;
        this.systemConfigurationService = systemConfigurationService;
        this.billingLedgerFlightUtility = billingLedgerFlightUtility;
        this.billingLedgerRepository = billingLedgerRepository;
        this.transactionRepository = transactionRepository;
        this.invoiceTemplateRepository = invoiceTemplateRepository;
        this.invoicesApprovalWorkflow = invoicesApprovalWorkflow;
        this.accountExternalChargeCategoryService = accountExternalChargeCategoryService;
    }

    /** Return the org code configured in system options */
    @NotNull
    public BillingOrgCode getBillingOrgCode() {
        return this.systemConfigurationService.getBillingOrgCode();
    }

    /**
     * Return the resource path of an .rptdesign file for an invoice
     */
    private String getInvoiceReportTemplatePath (final BillingOrgCode billingOrgCode, final InvoiceTemplateCategory category) {
        return REPORT_INVOICE_TEMPLATE_DIRECTORY +
                File.separator + billingOrgCode.getFileCode() + File.separator
                + category.name() + ".rptdesign";
    }

    /**
     * Return the resource path of an .rptdesign file for an "other" report (non-invoice)
     */
    private String getOtherReportTemplatePath (final BillingOrgCode billingOrgCode, final String baseName) {
        return REPORT_OTHER_TEMPLATE_DIRECTORY +
                File.separator + billingOrgCode.getFileCode() + File.separator
                + baseName + ".rptdesign";
    }

    /**
     * Get output date format for reports
     */
    public DateTimeFormatter getDateFormat() {
        final String format = systemConfigurationService.getStringNotEmpty(SystemConfigurationItemName.DATE_FORMAT, DFLT_DATE_FORMAT);
        try {
            return DateTimeFormatter.ofPattern (format);
        }
        catch (final IllegalArgumentException x) {
            LOG.error("Invalid date format \"{}\" in system configuration \"{}\"",
                format,SystemConfigurationItemName.DATE_FORMAT, x);
        }
        return DateTimeFormatter.ofPattern (DFLT_DATE_FORMAT, Locale.US);
    }

    /**
     * Format a UTC date
     */
    public String formatDateUtc (final LocalDateTime date, final DateTimeFormatter formatter) {
        final ZonedDateTime zdt = ZonedDateTime.of (date, UTC_ZONE_ID);
        return zdt.format (formatter);
    }

    /**
     * Format month
     */
    public String formatMonth (final LocalDateTime date) {
        final ZonedDateTime zdt = ZonedDateTime.of (date, UTC_ZONE_ID);
        return zdt.format (DateTimeFormatter.ofPattern ("yyyy-MM"));
    }

    /**
     * Format currency without the currency symbol
     */
    public String formatCurrency (final Double value, final Currency currency) {
        return NumberUtils.formatCurrency(value, currency);
    }

    /**
     * Format currency, including the currency symbol
     */
    public String formatCurrencyWithSymbol (final Double value, final Currency currency) {
        return NumberUtils.formatCurrencyWithSymbol(value, currency);
    }

    public String formatAmountInFullText(final Double value, final Locale locale) {
        return NumberUtils.formatAmountInFullText(Math.abs(value), locale);
    }

    public String formatAmountInFullTextWithCurrencyCode(
        final Double value, final Currency currency, final Locale locale
    ) {
        return NumberUtils.formatAmountInFullTextWithCurrencyCode(Math.abs(value), currency, locale);
    }

    public String formatAmountInFullTextWithCurrencyCode(
        final Double value, final Currency currency, final Boolean includePrefix, final Locale locale
    ) {
        return NumberUtils.formatAmountInFullTextWithCurrencyCode(Math.abs(value), currency, includePrefix, locale);
    }

    /**
     * Format flight time "1325" => "13:25"
     */
    private static final Pattern RE_RAW_FLIGHT_TIME = Pattern.compile ("^\\s*(\\d{2})(\\d{2})\\s*$");
    public String formatFlightTime (final String rawValue) {
        if (rawValue != null) {
            final String s = rawValue.trim();
            final Matcher m = RE_RAW_FLIGHT_TIME.matcher (s);
            if (m.matches()) {
                return String.format ("%s:%s", m.group(1), m.group(2));
            }
            if (s.length() > 5) {
                return s.substring(0, 6);
            }
            return s;
        }
        return null;
    }

    /**
     * Format aerodrome code to 5 symbols max
     */
    public String formatAerodromeCode (final String code) {
        if (code != null) {
            final String s = code.trim();
            if (s.length() > 5) {
                return s.substring(0, 6);
            }
            return s;
        }
        return null;
    }

    /**
     * TTCAA requested the feature described in US#115514, however, other customers started complaining about it (US#116058).
     * According to US US#116058, the decision was made to show FIR entry and exit points only for TTCAA,
     * for other customers - actualDeparture/actualDestination aerodromes
     *
     * US#115514
     * For international overflights the flight entry/exit points should be the actual FIR entry and exit points
     * For international arrivals the flight entry/exit points should be the actual FIR entry and the destination aerodrome
     * For international departures the flight entry/exit points should be the departure aerodrome and the actual FIR exit point
     * For domestic flights, the flight exit/entry points are the departure and destination aerodromes
     *
     * Departure/destination locations are from flightMovement actualDeparture and actualDestination fields
     *
     * @param fm calculated Flight Movement
     * @param departure boolean that indicate departure or arrival
     * @return departure/arrival location
     */
    @SuppressWarnings("squid:S3776")
    public String getLocation(FlightMovement fm, boolean departure) {
        if (fm == null) {
            return null;
        }

        final BillingOrgCode billingOrgCode = getBillingOrgCode();

        if (billingOrgCode != BillingOrgCode.TTCAA) {
            if (departure) {
                return formatAerodromeCode(fm.getActualDepAd());
            } else {
                return formatAerodromeCode(fm.getActualDestAd());
            }
        }

        FlightmovementCategoryType flightmovementCategoryType = fm.getFlightCategoryType();

        if (flightmovementCategoryType == null) {
            return null;
        }

        if (departure) {
            if (flightmovementCategoryType.equals(FlightmovementCategoryType.OVERFLIGHT) || flightmovementCategoryType.equals(FlightmovementCategoryType.ARRIVAL)) {
                return fm.getBillableEntryPoint();
            } else if (flightmovementCategoryType.equals(FlightmovementCategoryType.DEPARTURE) || flightmovementCategoryType.equals(FlightmovementCategoryType.DOMESTIC)) {
                return formatAerodromeCode(fm.getActualDepAd());
            }

        } else {
            if (flightmovementCategoryType.equals(FlightmovementCategoryType.OVERFLIGHT) || flightmovementCategoryType.equals(FlightmovementCategoryType.DEPARTURE)) {
                return fm.getBillableExitPoint();
            } else if (flightmovementCategoryType.equals(FlightmovementCategoryType.ARRIVAL) || flightmovementCategoryType.equals(FlightmovementCategoryType.DOMESTIC)) {
                return formatAerodromeCode(fm.getActualDestAd());
            }
        }
        return null;
    }

    /**
     * Format double with the given number of fractional digits
     */
    public String formatDouble (final Double value, int fractionalDigits) {
        return NumberUtils.formatDouble(value, fractionalDigits);
    }

    public Account getAccount (final Integer id) {
        final Account account = accountService.getOne(id);
        if (account == null) {
            throw new CustomParametrizedException("Account" + " #" + id + " " + "not found", "id");
        }
        return account;
    }

    /**
     * Return the currently logged-in user
     */
    public User getCurrentUser() {
        return userService.getUserByLogin(SecurityUtils.getCurrentUserLogin());
    }

    /**
     * Return the user by the login name
     */
    public User getUserByLogin(final String login) {
        return userService.getUserByLogin(login);
    }

    /**
     * Find billing center by ID
     */
    public BillingCenter getBillingCenter (final Integer id) {
        return billingCenterService.findOne(id);
    }

    /**
     * Find billing center ID of the current user
     */
    public BillingCenter getBillingCenterOfCurrentUser() {
        return getBillingCenterOfCurrentUser(null);
    }

    /**
     * Find billing center ID of the current user
     */
    public BillingCenter getBillingCenterOfCurrentUser(final User user) {
        final User currentUser = user != null ? user : getCurrentUser();
        if (currentUser == null) {
            throw ExceptionFactory.getMissingBillingCenterOfCurrentUserException (ReportHelper.class);
        } else if (currentUser.getBillingCenter() == null) {
            throw ExceptionFactory.getMissingBillingCenterOfCurrentUserException (ReportHelper.class, currentUser.getId().toString(), currentUser.getLogin());
        }
        return currentUser.getBillingCenter();
    }

    /**
     * Find billing center ID of the current user
     */
    public BillingCenter getHQBillingCenter() {
      return billingCenterService.findHq();
    }

    /**
     * Find a aerodrome code given icao and item18 codes from a flight plan
     */
    String findAerodromeCode(final String icaoCode, final String item18Code) {
        try {
            return flightMovementBuilderUtility.checkAerodrome (icaoCode, item18Code, false);
        }
        catch (FlightMovementBuilderException x) {
            LOG.error("Invalid aerodrome {}/{}: {}", icaoCode, item18Code, x.getMessage(), x);
        }
        return null;
    }

    /**
     * Get the invoice number suitable for showing to the user. This is the same as the real invoice number,
     * except in preview mode, in which case this method returns the string "PREVIEW".
     */
    public String getDisplayInvoiceNumber (final String realInvoiceNumber, final boolean preview) {
        return preview ? "PREVIEW" : realInvoiceNumber;
    }

    /**
     * Get the receipt number suitable for showing to the user. This is the same as the real receipt number,
     * except in preview mode, in which case this method returns the string "PREVIEW".
     */
    public String getDisplayReceiptNumber (final String realReceiptNumber, final boolean preview) {
        return preview ? "PREVIEW" : realReceiptNumber;
    }

    /**
     * Classify flight movements by account
     */
    public Map <Account, List <FlightMovement>> classifyFlightsByAccount (final Collection <FlightMovement> flights) {
        final Map <Account, List <FlightMovement>> accountFlightsMap = new HashMap<>();
        flights.forEach(fm->{
            if (fm.getAccount() != null) {
                List<FlightMovement> accountFlights = accountFlightsMap
                    .computeIfAbsent(fm.getAccount(), k -> new ArrayList<>());
                accountFlights.add(fm);
            }
        });
        return accountFlightsMap;
    }

    /**
     * Convert MTOW in tons from unit of measure specified by SystemConfiguration.
     * MTOW is stored in tons in database.
     *
     * @param mtow;
     * @return convertedMtow;
     */
    public Double convertMTOWinTons(Double mtow) {
        Double ret = mtow;
        String unitOfMeasure = getMTOWUnitOfMeasure();
        if (unitOfMeasure.equalsIgnoreCase("KG")) {
            ret = mtow / TO_KG;
        }
        return ret;
    }

    /**
     * Return the initial state for a ledger record
     */
    public InvoiceStateType getInitialLedgerState(boolean approvalWorkflow) {
        return invoicesApprovalWorkflow.getInitialLedgerState(approvalWorkflow);
    }

    /**
     * Update the status of each flight connected with a billing ledger to INVOICE or PAID if necessary.
     *
     * @param billingLedger -- the billing ledger record with state set to NEW, APPROVED, PUBLISHED or PAID
     * @param flightMovement -- the related flight movement record connected to the billing ledger via either of
     *                          the {enroute,other,passengder}_charges_status fields.
     */
    public void updateFlightStatusToMatchInvoice (final BillingLedger billingLedger, final FlightMovement flightMovement) {
        billingLedgerFlightUtility.updateFlightStatusToMatchInvoice (billingLedger, flightMovement, systemConfigurationService.shouldDisplayPaxCharges());
    }

    /**
     * Return transaction type "credit" entity
     */
    public TransactionType getTransactionTypeCredit() {
        return transactionTypeService.findOneByName (TRANSACTION_TYPE_CREDIT);
    }

    /** Get all billing ledgers from a transaction */
    public List<BillingLedger> getTransactionBillingLedgers(Transaction transaction) {
        return getTransactionBillingLedgers(transaction.getBillingLedgerIds());
    }

    /** Get all billing ledgers from a list of ID */
    public List<BillingLedger> getTransactionBillingLedgers(final List<Integer> billingLedgerIds) {
        if (billingLedgerIds == null) {
            return new ArrayList<>();
        }
        return this.billingLedgerRepository.findAll (billingLedgerIds);
    }

    public BillingLedger getBillingLedger(Integer invoiceId) {
        return billingLedgerRepository.getOne(invoiceId);
    }

    public Transaction getTransaction(Integer transactionId) {
        return transactionRepository.getOne (transactionId);
    }

    public Account tryGetUniqueAccount(List<BillingLedger> billingLedgerList) {
        Account account = null;
        for (final BillingLedger x: billingLedgerList) {
            if (x.getAccount() != null) {
                if (account == null) {
                    account = x.getAccount();
                }
                else if (!account.equals(x.getAccount())) {
                    throw new CustomParametrizedException("billing ledgers belong to more than one account");
                }
            }
        }
        return account;
    }

    public void setReportDocument(BillingLedger billingLedger, ReportDocument reportDocument) {
        billingLedger.setInvoiceFileName (reportDocument.fileName());
        billingLedger.setInvoiceDocument(reportDocument.data());
        billingLedger.setInvoiceDocumentType(reportDocument.contentType());
    }

    InputStream openOtherReportTemplate (final String baseName) {
        final BillingOrgCode billingOrgCode = getBillingOrgCode();
        ClassPathResource cpr = new ClassPathResource (getOtherReportTemplatePath (billingOrgCode, baseName));
        InputStream inputStream = tryOpenResource (cpr);
        if (inputStream == null && billingOrgCode != BillingOrgCode.DEFAULT) {
            cpr = new ClassPathResource (this.getOtherReportTemplatePath (BillingOrgCode.DEFAULT, baseName));
            inputStream = tryOpenResource (cpr);
        }
        if (inputStream == null) {
            throw ExceptionFactory.getMissingReportTemplateException(ReportHelper.class, billingOrgCode.name());
        }
        LOG.debug ("Loaded report template classpath:{}", cpr.getPath());
        return inputStream;
    }

    /**
     * Returns an invoice template for the given category as an InputStream.
     * <p>
     * This method searches the database first (table invoice_templates, column template_document),
     * and falls back to a resource file under "reports/invoices/{{billingOrgCode}}".
     */

    InputStream openInvoiceReportTemplate(final InvoiceTemplateCategory invoiceTemplateCategory) {
        InputStream inputStream = openInvoiceReportTemplateFromDatabase (invoiceTemplateCategory);
        if (inputStream == null) {
            LOG.debug ("Invoice template {} doesn't exist in the database, trying to load from classpath", invoiceTemplateCategory);
            final BillingOrgCode billingOrgCode = getBillingOrgCode();
            inputStream = openInvoiceReportTemplateFromClassPath (invoiceTemplateCategory, billingOrgCode);
            if (inputStream == null) {
                LOG.error ("Failed to load invoice template (category={}, org={})", invoiceTemplateCategory, billingOrgCode);
                throw ExceptionFactory.getMissingReportTemplateException(ReportHelper.class, billingOrgCode.name());
            }
        }
        return inputStream;
    }

    private final InputStream openInvoiceReportTemplateFromDatabase (final InvoiceTemplateCategory invoiceTemplateCategory) {
        LOG.debug("Getting invoice template from database (category={})", invoiceTemplateCategory);
        final String categoryName = invoiceTemplateCategory.name();
        final InvoiceTemplate invoiceTemplateFromDb = invoiceTemplateRepository.findInvoiceTemplateByCategory (categoryName);
        if (invoiceTemplateFromDb != null && invoiceTemplateFromDb.getTemplateDocument().length>0) {
            final byte[] templateFile = invoiceTemplateFromDb.getTemplateDocument();
            if (templateFile!=null && templateFile.length>0) {
                LOG.debug ("Loaded invoice template from database (category={})", categoryName);
                return new ByteArrayInputStream(templateFile);
            }
        }
        return null;
    }

    private final InputStream openInvoiceReportTemplateFromClassPath (final InvoiceTemplateCategory invoiceTemplateCategory, final BillingOrgCode billingOrgCode) {
        ClassPathResource cpr = new ClassPathResource (this.getInvoiceReportTemplatePath (billingOrgCode, invoiceTemplateCategory));
        InputStream inputStream = tryOpenResource (cpr);
        if (inputStream == null && billingOrgCode != BillingOrgCode.DEFAULT) {
            cpr = new ClassPathResource (this.getInvoiceReportTemplatePath (BillingOrgCode.DEFAULT, invoiceTemplateCategory));
            inputStream = tryOpenResource (cpr);
        }
        if (inputStream != null) {
            LOG.debug ("Loaded invoice template classpath:{}", cpr.getPath());
        }
        return inputStream;
    }

    private static InputStream tryOpenResource (final ClassPathResource cpr) {
        try {
            return cpr.getInputStream();
        }
        catch (final FileNotFoundException x) {
            LOG.warn ("Resource classpath:{} doesn't exist", cpr.getPath());
            return null;
        }
        catch (final IOException x) {
            LOG.error ("Error while opening resource classpath:{}: {}", cpr.getPath(), x.getMessage(), x);
            return null;
        }
    }

    /**
     * This method searches for Report template file in DB. If not found, it retrieves default
     * template file from resources
     */
	public byte[] getInvoiceTemplateFile(
			final InvoiceTemplateCategory invoiceTemplateCategory
    ) {

		byte[] result = null;
		try {
			InputStream fileInputStream = openInvoiceReportTemplate(invoiceTemplateCategory);
			result = FileCopyUtils.copyToByteArray(fileInputStream);

            // noinspection ConstantConditions
            if(fileInputStream != null)
                fileInputStream.close();

		} catch (IOException e) {
			LOG.error("Error reading report template resource file", e);
		}

		return result;
	}

    /**
     * This method retrieves
     * template example XML file from resources
     */
	public byte[] getInvoiceTemplateExampleXmlFile(
			final InvoiceTemplateCategory invoiceTemplateCategory
    ) {

		byte[] result = null;
		try {

			InputStream fileInputStream = getInvoiceTemplateExampleXmlFileInputStream(invoiceTemplateCategory);

			result = FileCopyUtils.copyToByteArray(fileInputStream);

            // noinspection ConstantConditions
            if(fileInputStream != null)
                fileInputStream.close();

		} catch (IOException e) {
			LOG.error("Error reading report template example xml resource file", e);
		}

		return result;
	}

	private InputStream getInvoiceTemplateExampleXmlFileInputStream(
        final InvoiceTemplateCategory invoiceTemplateCategory
    ) {

		InputStream result = null;

		try {

			// read template example xml file from resources

			String path = REPORT_INVOICE_TEMPLATE_DIRECTORY +
					File.separator + "example_xml" + File.separator
					+ invoiceTemplateCategory.name() + ".example.xml";

			ClassPathResource cpr = new ClassPathResource(path);
			result = cpr.getInputStream();

		} catch (IOException e) {
			LOG.error("Error reading report template example xml resource file", e);
		}

		return result;
	}

    /**
     * @return mtowUnitOfMeasure;
     */
    public String getMTOWUnitOfMeasure(){
        return systemConfigurationService.getCurrentValue(SystemConfigurationItemName.MTOW_UNIT_OF_MEASURE);
    }

    /**
     * @return taspChargesEnabled;
     */
    public Boolean getTASPChargesEnabled(){
        return systemConfigurationService.getBoolean(SystemConfigurationItemName.TASP_CHARGES_SUPPORT);
    }

    /**
     * Return MTOW in unit of measure specified by SystemConfiguration.
     * MTOW is stored in tons in database.
     *
     * @param mtow;
     * @return convertedMtow;
     */
    public Double mtowToUnitOfMeasure(Double mtow) {
        if (mtow == null) {
           return null;
        }

        return getMTOWUnitOfMeasure().equalsIgnoreCase("KG") ? mtow * TO_KG : mtow;
    }

    /**
     * @return distanceUnitOfMeasure;
     */
    public String getDistanceUnitOfMeasure() {
        return systemConfigurationService.getCurrentValue(SystemConfigurationItemName.DISTANCE_UNIT_OF_MEASURE);
    }

    /**
     * @return Boolean passengerChargesEnabled;
     */
    public Boolean passengerChargesEnabled() {
        return systemConfigurationService.shouldDisplayPaxCharges();
    }

    /**
     * @return Boolean extendedChargesEnabled;
     */
    public Boolean extendedChargesEnabled() {
        return systemConfigurationService.shouldExtendedHoursSurchargeCharges();
    }

    /**
     * @return Boolean passengerChargesInInvoiceTotal
     */
    public Boolean passengerChargesInInvoiceTotal() {
        return systemConfigurationService.getBoolean(SystemConfigurationItemName.INCLUDE_PASSENGER_FEES_ON_INVOICE);
    }

    /**
     * Get all account external charge category ids as a comma separated list.
     */
    public String getAccountExternalSystemIdentifiers(final Account account) {

        List<AccountExternalChargeCategory> externalIds = accountExternalChargeCategoryService
            .findByAccount(account.getId());

        StringJoiner result = new StringJoiner(", ");
        for (AccountExternalChargeCategory externalId : externalIds) {
            if (externalId.getExternalSystemIdentifier() != null)
                result.add(externalId.getExternalSystemIdentifier());
        }

        return result.toString();
    }

    // ------------------------ private -------------------------

    private final BillingCenterService billingCenterService;
    private final UserService userService;
    private final FlightMovementBuilderUtility flightMovementBuilderUtility;
    private final AccountService accountService;
    private final TransactionTypeService transactionTypeService;
    private final SystemConfigurationService systemConfigurationService;
    private final BillingLedgerFlightUtility billingLedgerFlightUtility;
    private final BillingLedgerRepository billingLedgerRepository;
    private final TransactionRepository transactionRepository;
    private final InvoiceTemplateRepository invoiceTemplateRepository;
    private final AccountExternalChargeCategoryService accountExternalChargeCategoryService;
}
