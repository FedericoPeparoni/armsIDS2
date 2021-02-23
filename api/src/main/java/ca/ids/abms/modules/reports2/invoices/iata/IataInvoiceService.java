package ca.ids.abms.modules.reports2.invoices.iata;

import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import ca.ids.abms.modules.flightmovements.enumerate.FlightMovementStatus;
import ca.ids.abms.modules.jobs.JobMessage;
import ca.ids.abms.modules.jobs.impl.InvoiceProgressCounter;
import ca.ids.abms.modules.system.SystemConfigurationService;
import ca.ids.abms.modules.system.summary.SystemConfigurationItemName;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.ids.abms.config.error.ErrorDTO;
import ca.ids.abms.config.error.RejectedReasons;
import ca.ids.abms.config.security.SecurityUtils;
import ca.ids.abms.modules.accounts.Account;
import ca.ids.abms.modules.billingcenters.BillingCenter;
import ca.ids.abms.modules.billings.BillingLedger;
import ca.ids.abms.modules.billings.BillingLedgerService;
import ca.ids.abms.modules.billings.InvoicesApprovalWorkflow;
import ca.ids.abms.modules.common.enumerators.InvoiceStateType;
import ca.ids.abms.modules.common.enumerators.InvoiceType;
import ca.ids.abms.modules.currencies.Currency;
import ca.ids.abms.modules.flightmovements.FlightMovement;
import ca.ids.abms.modules.flightmovements.FlightMovementRepository;
import ca.ids.abms.modules.reports2.common.ReportDocument;
import ca.ids.abms.modules.reports2.common.ReportFormat;
import ca.ids.abms.modules.reports2.common.ReportHelper;
import ca.ids.abms.modules.reports2.common.RoundingUtils;
import ca.ids.abms.modules.reports2.invoices.aviation.BillingInterval;
import ca.ids.abms.modules.transactions.TransactionPaymentMechanism;
import ca.ids.abms.modules.translation.Translation;
import ca.ids.abms.modules.usereventlogs.UserEventLogService;
import ca.ids.abms.modules.users.User;
import ca.ids.abms.modules.users.UserService;
import ca.ids.abms.modules.util.models.CurrencyUtils;
import ca.ids.abms.modules.utilities.invoices.InvoiceSequenceNumberHelper;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.persistence.criteria.Predicate;

/**
 * Generate IATA invoices
 */
@Service
public class IataInvoiceService {

    public IataInvoiceService(
        final ReportHelper reportHelper,
        final IataInvoiceDocumentCreator iataInvoiceDocumentCreator,
        final FlightMovementRepository flightMovementRepository,
        final BillingLedgerService billingLedgerService,
        final UserService userService,
        final InvoiceSequenceNumberHelper invoiceSequenceNumberHelper,
        final CurrencyUtils currencyUtils,
        final UserEventLogService userEventLogService,
        final InvoicesApprovalWorkflow invoicesApprovalWorkflow,
        final RoundingUtils roundingUtils,
        final SystemConfigurationService systemConfigurationService
    ) {
        this.reportHelper = reportHelper;
        this.iataInvoiceDocumentCreator = iataInvoiceDocumentCreator;
        this.flightMovementRepository = flightMovementRepository;
        this.billingLedgerService = billingLedgerService;
        this.userService = userService;
        this.invoiceSequenceNumberHelper = invoiceSequenceNumberHelper;
        this.currencyUtils = currencyUtils;
        this.userEventLogService = userEventLogService;
        this.invoicesApprovalWorkflow = invoicesApprovalWorkflow;
        this.roundingUtils = roundingUtils;
        this.systemConfigurationService = systemConfigurationService;
        this.invoiceByDateOfFlight = systemConfigurationService.getBoolean(SystemConfigurationItemName.INVOICE_BY_DAY_OF_FLIGHT);
    }

    /**
     * Create an invoice and return the invoice document
     * <p>
     * This creates billing ledger records and also creates a document in the output given format. You need
     * to rollback current transaction if you just want a preview. This method creates an invoice
     * for the billing center associated with the current user.
     *
     * @param billingInterval       - billing period monthly/weekly; optional
     * @param startDate             - start date for billing period; optional
     * @param endDateInclusive      - end date (inclusive) for billing period; optional
     * @param format                - output format; if null defaults to XLSX because this is preferred for IATA reports
     * @param accountIdList         - return flights only for these accounts
     * @param preview               - if true, the output document will have a fake invoice number ("PREVIEW");
     *                                useful for the preview scenario
     * @param order                 - sort order for line items in the report
     * @param userBillingCenterOnly - flights by user's billing centre or all flights (true/false2)
     * @return the generated document.
     */
    @Transactional
    public ReportDocument createInvoiceForCurrentUser(
        final BillingInterval billingInterval,
        final LocalDateTime startDate,
        final LocalDateTime endDateInclusive,
        ReportFormat format,
        final List<Integer> accountIdList,
        boolean preview,
        final IataInvoiceItemOrder order,
        final boolean userBillingCenterOnly,
        final String ipAddress) {

        if (preview) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        final ZonedDateTime zdNow = ZonedDateTime.now(ReportHelper.UTC_ZONE_ID);
        final LocalDateTime ldtNow = zdNow.toLocalDateTime();

        validate(billingInterval, startDate, endDateInclusive, ldtNow,
            reportHelper.getBillingCenterOfCurrentUser(), accountIdList, true, preview);

        final List<FlightMovement> flightMovements = loadFlightMovements(billingInterval, startDate, endDateInclusive,
            format, reportHelper.getBillingCenterOfCurrentUser(), accountIdList, true, preview,
            order, userBillingCenterOnly, null);

        return generateInvoice(billingInterval, startDate, endDateInclusive,
            format, reportHelper.getBillingCenterOfCurrentUser(), accountIdList, true, preview,
            order, userBillingCenterOnly, ipAddress, null, flightMovements, ldtNow, null);
    }
    
    protected void validate (
        final BillingInterval billingInterval,
                        final LocalDateTime startDate,
                        final LocalDateTime endDateInclusive,
                        final LocalDateTime ldtNow,
                        final BillingCenter billingCenter,
                        final List<Integer> accountIdList,
                        final boolean preview,
                        final boolean userBillingCenterOnly) {


        final LocalDateTime ldtStartOfCurrentMonth = LocalDateTime.of(ldtNow.getYear(), ldtNow.getMonth(), 1, 0, 0, 0, 0);
        final ZonedDateTime zdtStart = ZonedDateTime.of(startDate, ReportHelper.UTC_ZONE_ID);
        final LocalDateTime ldtEnd = endDateInclusive.plusDays(1);
        final ZonedDateTime zdtEnd = ZonedDateTime.of(ldtEnd, ReportHelper.UTC_ZONE_ID);

        validateBillingPeriod(billingInterval, preview, ldtNow, ldtStartOfCurrentMonth, startDate, endDateInclusive);

        if (userBillingCenterOnly) {
            logger.info("Creating {} IATA/en-route invoice for billingCenter={}/{}, billing period from {} to {}, accountIdList={}",
                billingInterval.toString().toLowerCase(),
                billingCenter.getId(),
                billingCenter.getName(),
                zdtStart.toLocalDate(),
                zdtEnd.toLocalDate(),
                accountIdList);
        } else {
            logger.info("Creating {} IATA/en-route invoice for all billing centres, billing period from {} to {}, accountIdList={}",
                billingInterval.toString().toLowerCase(),
                zdtStart.toLocalDate(),
                zdtEnd.toLocalDate(),
                accountIdList);
        }
    }

    /**
     * Create an invoice (internal method);
     *
     * @param billingInterval     - billing period monthly/weekly
     * @param startDate           - start date for billing period
     * @param endDateInclusive    - end date (inclusive) for billing period
     * @param format              - output format: "pdf", etc; defaults to "xlsx" for IATA reports
     * @param billingCenter       - include only flights related to this billing center (required)
     * @param accountIdList       - include only flights related to these accounts. If "preview" is false this parameter will be ignored
     * @param createLedgerRecords - If set to false this will only create output documents without creating billing ledger records.
     *                              It's better to always set it to "true", but do a transaction rollback when previewing.
     * @param preview             - If "true" then accountIdList must be provided, and the invoice number included in the output document(s)
     *                              will be "fake" ("PREVIEW").
     * @param order               - sort order for line items in the report
     * @param userBillingCenterOnly - flights by user's billing centre or all flights (true/false2)
     * @return
     */
    protected List<FlightMovement> loadFlightMovements(
        final BillingInterval billingInterval,
        final LocalDateTime startDate,
        final LocalDateTime endDateInclusive,
        final ReportFormat format,
        final BillingCenter billingCenter,
        final List<Integer> accountIdList,
        final boolean createLedgerRecords,
        final boolean preview,
        final IataInvoiceItemOrder order,
        final boolean userBillingCenterOnly,
        final InvoiceProgressCounter counter) {

        final ZonedDateTime zdNow = ZonedDateTime.now(ReportHelper.UTC_ZONE_ID);
        final LocalDateTime ldtNow = zdNow.toLocalDateTime();
        final LocalDateTime ldtStartOfCurrentMonth = LocalDateTime.of(ldtNow.getYear(), ldtNow.getMonth(), 1, 0, 0, 0, 0);

        validateBillingPeriod(billingInterval, preview, ldtNow, ldtStartOfCurrentMonth, startDate, endDateInclusive);

        if (userBillingCenterOnly) {
            logger.info("Creating {} IATA/en-route invoice for billingCenter={}/{}, billing period from {} to {}, accountIdList={}",
                billingInterval.toString().toLowerCase(),
                billingCenter.getId(),
                billingCenter.getName(),
                startDate.toLocalDate(),
                endDateInclusive.toLocalDate(),
                accountIdList);
        } else {
            logger.info("Creating {} IATA/en-route invoice for all billing centres, billing period from {} to {}, accountIdList={}",
                billingInterval.toString().toLowerCase(),
                startDate.toLocalDate(),
                endDateInclusive.toLocalDate(),
                accountIdList);
        }

        if (counter != null) {
            counter.setMessage("Looking for billable flight movements");
            counter.update();
        }

        final String invoiceBy = this.invoiceByDateOfFlight ? "date of flight" : "billing date";
        logger.debug ("Looking for billable flights by {} for billingCenter {}, startDate {}, endDate{}",
            invoiceBy, billingCenter.getName(), startDate.toLocalDate(), endDateInclusive.toLocalDate());

        // Find flight movements for the given period and filter-in only records related to this billing center
        final List<FlightMovement> flightMovements = this.flightMovementRepository.findAll((root, query, criteriaBuilder) -> {
            final List <Predicate> andList = new ArrayList<>();

            final String dateOfFlight = "dateOfFlight";
            final String billingDate = "billingDate";
            final String flightId = "flightId";
            final String id = "id";
            final String account = "account";
            final String name = "name";
            final String status = "status";

            if (this.invoiceByDateOfFlight) {
                andList.add(criteriaBuilder.between(root.get(dateOfFlight), startDate.with(LocalTime.MIN), endDateInclusive.with(LocalTime.MAX)));

                query.orderBy(criteriaBuilder.asc(root.join(account).get(name)), criteriaBuilder.asc(root.get(dateOfFlight)),
                    criteriaBuilder.asc(root.get(flightId)), criteriaBuilder.asc(root.get(id)));
            } else {
                andList.add(criteriaBuilder.between(root.get(billingDate), startDate.with(LocalTime.MIN), endDateInclusive.with(LocalTime.MAX)));

                query.orderBy(criteriaBuilder.asc(root.join(account).get(name)), criteriaBuilder.asc(root.get(billingDate)),
                    criteriaBuilder.asc(root.get(flightId)), criteriaBuilder.asc(root.get(id)));
            }

            andList.add(criteriaBuilder.or(criteriaBuilder.equal(root.get(status), FlightMovementStatus.PENDING),
                criteriaBuilder.equal(root.get(status), FlightMovementStatus.INCOMPLETE)));

            //  Account selection is taken into account only for preview of invoice
            if (accountIdList != null && !accountIdList.isEmpty() || !preview) {
                andList.add(root.join(account).get(id).in(accountIdList));
            }

            if (userBillingCenterOnly) {
                andList.add(criteriaBuilder.equal(root.join("billingCenter").get(id), billingCenter.getId()));
            }

            andList.add(criteriaBuilder.equal(root.join(account).get("iataMember"), true));
            andList.add(criteriaBuilder.isNull(root.get("enrouteInvoiceId")));
            andList.add(criteriaBuilder.greaterThan(root.get("enrouteCharges"), 0));
            andList.add(criteriaBuilder.notEqual(root.join("flightmovementCategory").get(name), "OTHER"));

            // Combine all predicates
            return criteriaBuilder.and(andList.toArray(new Predicate[0]));
        });

        logger.debug("Found {} pending flight(s) for this aviation invoice", flightMovements != null ? flightMovements.size() : "0");
        if (counter != null) {
            if (CollectionUtils.isNotEmpty(flightMovements)) {
                counter.setMessage(new JobMessage.Builder()
                    .setMessage("Loading {{flights}} flight movements found")
                    .addVariable("flights", flightMovements.size())
                    .build());
            } else {
                counter.setMessage("No billable flight movements has been found");
                counter.update();
            }
        }

        if (counter != null) {
            counter.update();
        }

        // Results returned by repository methods are sorted by account,date/time
        // If user requested a different sort order, re-sort the list
        if (order != null && order.equals(IataInvoiceItemOrder.DATETIME_ACCOUNT)) {
            Collections.sort(flightMovements, (a, b) -> {

                // Check date of flight
                //   a is before b
                if (a.getDateOfFlight().isBefore(b.getDateOfFlight())) {
                    return -1;
                }
                //   a is after b
                if (a.getDateOfFlight().isAfter(b.getDateOfFlight())) {
                    return 1;
                }

                // dates are the same: compare accounts
                //    a doesn't have an account
                if (a.getAccount() == null) {
                    // b also doesn't have an account
                    if (b.getAccount() == null) {
                        // compare the PKs
                        return a.getId().compareTo(b.getId());
                    }
                    // a doesn't have an account, but b does; assume a < b
                    return -1;
                }
                // a has an account, but b doesn't; assume a > b
                if (b.getAccount() == null) {
                    return 1;
                }
                // Compare account names; treat NULL names as empty strings
                return a.getAccount().getName().compareTo(b.getAccount().getName());
            });
        }

        if (CollectionUtils.isEmpty(flightMovements)) {
            if (userBillingCenterOnly) {
                throw new ErrorDTO.Builder("No pending IATA flights found")
                    .appendDetails("There are no pending IATA flights for the selected account and billing period, " +
                        "and associated with airports managed by the current billing center. " +
                        "Either no such flights are registered in the database, or their charges haven't been " +
                        "calculated yet, or they have already been invoiced, paid, canceled or declined.")
                    .addRejectedReason(RejectedReasons.NOT_FOUND)
                    .buildInvalidDataException();
            } else {
                throw new ErrorDTO.Builder("No pending IATA flights found")
                    .appendDetails("There are no pending IATA flights for the selected account and billing period. " +
                        "Either no such flights are registered in the database, or their charges haven't been " +
                        "calculated yet, or they have already been invoiced, paid, canceled or declined.")
                    .addRejectedReason(RejectedReasons.NOT_FOUND)
                    .buildInvalidDataException();
            }
        }

        return flightMovements;
    }

    /**
     * Create an invoice (internal method);
     *
     * @param billingInterval     - billing period monthly/weekly
     * @param startDate           - start date for billing period
     * @param endDateInclusive    - end date (inclusive) for billing period
     * @param format              - output format: "pdf", etc; defaults to "xlsx" for IATA reports
     * @param billingCenter       - include only flights related to this billing center (required)
     * @param accountIdList       - include only flights related to these accounts. If "preview" is false this parameter will be ignored
     * @param createLedgerRecords - If set to false this will only create output documents without creating billing ledger records.
     *                              It's better to always set it to "true", but do a transaction rollback when previewing.
     * @param preview             - If "true" then accountIdList must be provided, and the invoice number included in the output document(s)
     *                              will be "fake" ("PREVIEW").
     * @param order               - sort order for line items in the report
     * @param userBillingCenterOnly - flights by user's billing centre or all flights (true/false2)
     * @return
     */
    protected ReportDocument generateInvoice(
        final BillingInterval billingInterval,
        final LocalDateTime startDate,
        final LocalDateTime endDateInclusive,
        ReportFormat format,
        final BillingCenter billingCenter,
        final List<Integer> accountIdList,
        final boolean createLedgerRecords,
        final boolean preview,
        final IataInvoiceItemOrder order,
        final boolean userBillingCenterOnly,
        final String ipAddress,
        final InvoiceProgressCounter counter,
        final List<FlightMovement> flightMovements,
        final LocalDateTime ldtNow,
        final User currentUser
    ) {

        if (counter != null) {
            counter.setMessage("Calculating the flight movements");
            counter.setFlightsTotal(flightMovements.size());
            counter.update();
        }

        // get invoice approval workflow initial state
        final InvoiceStateType initialInvoiceStateType = invoicesApprovalWorkflow.getInitialLedgerState(true);
        final InvoiceSequenceNumberHelper.Generator invoiceSeqNumGen = invoiceSequenceNumberHelper.generator();

        // Generate report data
        final IataInvoiceData data = do_createReportData(startDate, endDateInclusive, flightMovements, invoiceSeqNumGen,
            billingCenter, preview, billingInterval, userBillingCenterOnly, counter);
        if (format == null) {
            format = DFLT_FORMAT;
        }

        if (counter != null) {
            counter.setMessage("Creating the document");
            counter.update();
        }

        // Export output format
        final ReportDocument reportDocument = iataInvoiceDocumentCreator.create(data, format);

        // Create ledger records, etc.
        if (createLedgerRecords) {

            final User user = userService.getUserByLogin(SecurityUtils.getCurrentUserLogin());
            final Currency currencyUSD = currencyUtils.getCurrencyUSD();
            final Currency currencyANSP = currencyUtils.getAnspCurrency();
            final Double exchangeRateToAnsp = currencyUtils.getExchangeRate(currencyUSD, currencyANSP, endDateInclusive);

            if (counter != null) {
                counter.setMessage("Creating the invoice for each account");
                counter.update();
            }

            // classify flight movements by account
            final Map<Account, List<FlightMovement>> accountMap = new HashMap<>();
            flightMovements.forEach(fm -> {
                if (fm.getAccount() != null) {
                    List<FlightMovement> lfm = accountMap.get(fm.getAccount());
                    if (lfm == null) {
                        lfm = new ArrayList<>();
                        accountMap.put(fm.getAccount(), lfm);
                    }
                    lfm.add(fm);
                }
            });

            if (counter != null) {
                counter.resetAccountsTotal(accountMap.size());
                counter.update();
            }

            // create a billing ledger record for each account
            accountMap.keySet().stream()
                .sorted((a, b) -> a.getName().compareTo(b.getName()))
                .forEach(account -> {

                    if (counter != null) {
                        counter.increaseAccountNumber();
                        counter.setAccountName(account.getName());
                        counter.update();
                    }

                    // flights for this account
                    final List<FlightMovement> accountFlights = accountMap.get(account);

                    // sum of enroute charges for all flights for this account
                    final double accountChargesUSD = accountFlights.stream()
                        .map(FlightMovement::getEnrouteCharges)
                        .filter(Objects::nonNull)
                        .reduce(0d, (a, b) -> a + b);

                    // create billing ledger
                    final BillingLedger bl = new BillingLedger();
                    bl.setAccount(account);
                    bl.setBillingCenter(user != null ? user.getBillingCenter() : null);
                    bl.setInvoicePeriodOrDate(startDate);
                    bl.setInvoiceType(InvoiceType.AVIATION_IATA.toValue());
                    bl.setInvoiceStateType(initialInvoiceStateType.toValue());
                    bl.setPaymentDueDate(ldtNow.plusDays(account.getPaymentTerms()));
                    bl.setUser(user);
                    bl.setInvoiceDocument(reportDocument.data());
                    bl.setInvoiceDocumentType(reportDocument.contentType());
                    bl.setInvoiceAmount(accountChargesUSD);
                    bl.setInvoiceCurrency(currencyUSD);
                    bl.setInvoiceExchange(1.0d);
                    bl.setTargetCurrency(currencyUSD);
                    bl.setInvoiceExchangeToAnsp(exchangeRateToAnsp);
                    bl.setInvoiceDateOfIssue(ldtNow);
                    bl.setExported(false);
                    bl.setAmountOwing(accountChargesUSD);
                    bl.setInvoiceNumber(data.props.realInvoiceNumber);
                    bl.setInvoiceFileName(reportDocument.fileName());
                    if (account.getCashAccount()) {
                        bl.setPaymentMode(TransactionPaymentMechanism.cash.toString());
                    } else {
                        bl.setPaymentMode(TransactionPaymentMechanism.credit.toString());
                    }

                    // penalties will be applied and invoice total amounts rounded
                    // based on system configuration settings
                    final BillingLedger billingLedger = billingLedgerService.createBillingLedgerAndTransaction(bl, false,preview);

                    // link flights to this ledger entry
                    accountFlights.forEach(fm -> {
                        fm.setEnrouteInvoiceId(billingLedger.getId());
                        reportHelper.updateFlightStatusToMatchInvoice(billingLedger, fm);
                        logger.debug("Flight movement #{} regNum={}, updated status={}",
                            fm.getId(), fm.getItem18RegNum(), fm.getStatus());
                    });

                    logger.info("Created IATA billing ledger record id={}, amount={} {}",
                        billingLedger.getId(), billingLedger.getInvoiceAmount(), billingLedger.getInvoiceCurrency().getCurrencyCode());

                    if (!preview) {

                        try {
                            // create a user event log for each record inserted into billing ledgers
                            if (currentUser == null) {
                                userEventLogService.createInvoiceUserEventLog(ipAddress, billingLedger.getId().toString());

                            } else {
                                userEventLogService.createInvoiceUserEventLogAsync(ipAddress, String.valueOf(billingLedger.getId()), currentUser);
                            }
                        } catch (Exception e) {
                            logger.warn ("Failed to create user event log: ", e.getMessage(), e);
                        }

                        // event trigger to indicate that a billing ledger was created with associated flight movements
                        // should only be triggered if NOT in preview mode
                        billingLedgerService.created(billingLedger, accountFlights);
                    }
                });
        }

        logger.info("Created IATA en-route invoice \"{}\" for billing center {}/{}, total amount={} USD, file length={} byte(s)",
            reportDocument.fileName(),
            billingCenter.getId(),
            billingCenter.getName(),
            data.props.totalEnrouteChargesUSD,
            reportDocument.contentLength());

        if (preview || InvoiceStateType.PUBLISHED.equals(initialInvoiceStateType)) {
            return reportDocument;
        } else {
            return null;
        }
    }

    // -------------------------- private ---------------------


    /**
     * Validate billing period.
     * User can preview monthly invoice for current month but not generate
     *
     * @param billingInterval       - billing period monthly/weekly
     * @param preview               - if true, the output document will have a fake invoice number ("PREVIEW");
     *                                useful for the preview scenario
     * @param today                 - current date
     * @param startOfCurrentMonth   - first date of current month
     * @param startDate             - start date for billing period
     * @param endDateInclusive      - end date (inclusive) for billing period
     */
    private void validateBillingPeriod(BillingInterval billingInterval, boolean preview, LocalDateTime today,
                                       LocalDateTime startOfCurrentMonth, LocalDateTime startDate, LocalDateTime endDateInclusive) {


        final Boolean validMonthlyBillingPeriod = startDate.isBefore(startOfCurrentMonth);
        final Boolean validWeeklyBillingPeriod = endDateInclusive.isBefore(today);

        // Ensure billing period is valid if not a preview
        if (!validMonthlyBillingPeriod && !preview && billingInterval.equals(BillingInterval.MONTHLY) ||
            !validWeeklyBillingPeriod && !preview && billingInterval.equals(BillingInterval.WEEKLY)) {

            logger.debug("Invalid IATA invoice billing period.");

            throw new ErrorDTO.Builder("Invalid IATA invoice billing period.")
                .appendDetails("IATA invoices cannot be generated for the current or future billing periods. " +
                    "Please choose a valid IATA invoice billing period.")
                .addRejectedReason(RejectedReasons.VALIDATION_ERROR)
                .buildInvalidDataException();
        }
    }

    /**
     * Create a pure data object from the given flights and billing center.
     */
    private IataInvoiceData do_createReportData(final LocalDateTime startDate,
                                                final LocalDateTime endDateInclusive,
                                                final List<FlightMovement> flightMovements,
                                                final InvoiceSequenceNumberHelper.Generator invoiceSeqNumGen,
                                                final BillingCenter billingCenter,
                                                boolean fakeInvoiceNumber,
                                                BillingInterval billingInterval,
                                                boolean userBillingCenterOnly,
                                                final InvoiceProgressCounter counter) {
        final Currency usdCurrency = currencyUtils.getCurrencyUSD();
        final DateTimeFormatter dateFormatter = reportHelper.getDateFormat();
        final IataInvoiceData x = new IataInvoiceData();
        final String distanceUnitOfMeasure = reportHelper.getDistanceUnitOfMeasure();
        final String mtowUnitOfMeasure = reportHelper.getMTOWUnitOfMeasure();
        String transText = Translation.getLangByToken("IATA invoice");

        logger.debug("IATA invoice report: found {} flight(s)", flightMovements.size());

        x.props = new IataInvoiceData.Props();
        x.props.realInvoiceNumber = invoiceSeqNumGen.nextInvoiceSequenceNumber(InvoiceType.AVIATION_IATA);	
        x.props.invoiceNumber = reportHelper.getDisplayInvoiceNumber(x.props.realInvoiceNumber, fakeInvoiceNumber);

        if (billingInterval.equals(BillingInterval.MONTHLY)) {
            x.props.intervalDescr = startDate.format(DateTimeFormatter.ofPattern("MMMM 'of' YYYY"));
            x.props.name = String.format("%s - %s - %s", transText, x.props.invoiceNumber, startDate.format(DateTimeFormatter.ofPattern("MMM YYYY")));

        } else {
            String weeklyName = String.format(
                "%s - %s", startDate.format(DateTimeFormatter.ofPattern("d MMM YYYY")), endDateInclusive.format(DateTimeFormatter.ofPattern("d MMM YYYY")));
            x.props.name = String.format("%s - %s - %s", transText, x.props.invoiceNumber, weeklyName);
            x.props.intervalDescr = weeklyName;
        }

        x.flightMovements = flightMovements.stream().map(fm -> {
            if (counter != null) {
                counter.increaseFlightNumber();
                counter.update();
            }

            final IataInvoiceData.FlightMovementInfo xx = new IataInvoiceData.FlightMovementInfo();
            xx.flightMovementId = fm.getId();
            if (this.invoiceByDateOfFlight) {
               xx.billingDateStr = reportHelper.formatDateUtc(fm.getDateOfFlight(), dateFormatter);
            } else {
                xx.billingDateStr = reportHelper.formatDateUtc(fm.getBillingDate() != null ? fm.getBillingDate() : fm.getDateOfFlight(), dateFormatter);
            }
            xx.operatorName = fm.getAccount().getName();
            xx.icaoCode = fm.getAccount().getIcaoCode();
            xx.flightId = fm.getFlightId();
            xx.aircraftType = fm.getAircraftType();
            xx.regNum = fm.getItem18RegNum();
            xx.depAd = reportHelper.getLocation(fm, true);
            xx.destAd = reportHelper.getLocation(fm, false);
            xx.distanceUnitOfMeasure = distanceUnitOfMeasure;
            xx.mtowUnitOfMeasure = mtowUnitOfMeasure;

            xx.crossDist = fm.getBillableCrossingDist();
            if (xx.crossDist != null && distanceUnitOfMeasure.equalsIgnoreCase("NM")) {
                xx.crossDist = xx.crossDist * ReportHelper.TO_NM;
            }

            xx.mtow = fm.getActualMtow();
            if (xx.mtow != null && mtowUnitOfMeasure.equalsIgnoreCase("KG")) {
                xx.mtow = xx.mtow * ReportHelper.TO_KG;
            }

            xx.wFactor = fm.getWFactor();
            xx.dFactor = fm.getDFactor();
            xx.routing = fm.getBillableRouteString();

            // must ensure flight movement enroute currency is in USD with new flight movement category logic
            // this will also insure proper currency decimal place rounding
            LocalDateTime exchageRateDate = fm.getBillingDate() != null ? fm.getBillingDate() : fm.getDateOfFlight();
            xx.enrouteChargesUSD = currencyUtils.convertCurrency(fm.getEnrouteCharges(), fm.getEnrouteResultCurrency(),
                usdCurrency, exchageRateDate);

            return xx;
        }).collect(Collectors.toList());

        Double totalEnrouteChargesUSD = x.flightMovements.stream().map(fm -> fm.enrouteChargesUSD).reduce(0d, Double::sum);
        x.props.totalEnrouteChargesUSD = roundingUtils.calculateSingleRoundedValue(totalEnrouteChargesUSD, usdCurrency, true);

        // this is for the exchanges spreadsheet
        // currently only used on Botswana IATA invoices
        // all days of the month should have the same ex rate
        Currency exchangeCurrency = currencyUtils.getAnspCurrency();
        Double exchangeRate = currencyUtils.getApplicableRateToUsd(exchangeCurrency, endDateInclusive);
        int monthToFindExchangeRate = endDateInclusive.getMonth().getValue();
        int yearToFindExchangeRate = endDateInclusive.getYear();
        List<String> listOfDays = new ArrayList<>();

        YearMonth ym = YearMonth.of(yearToFindExchangeRate, monthToFindExchangeRate);
        // get the last day of month
        int lastDay = ym.lengthOfMonth();
        // loop through the days
        for(int day = 1; day <= lastDay; day++) {
            // create the day
            LocalDate dt = ym.atDay(day);
            LocalDateTime dayOfMonth = Timestamp.valueOf(dt.atStartOfDay()).toLocalDateTime();
            // list of days of the month as strings
            listOfDays.add(reportHelper.formatDateUtc(dayOfMonth, dateFormatter));
        }

        // apply the exchange rate
        // to every day of the month
        x.exchangeRates = listOfDays.stream().map(dt -> {
            final IataInvoiceData.ExchangeRateInfo ex = new IataInvoiceData.ExchangeRateInfo();
            ex.dayOfMonth = dt;
            ex.exchangeRate = exchangeRate;
            return ex;
        }).collect(Collectors.toList());

        if (userBillingCenterOnly) {
            logger.info("Created IATA invoice for billing center {}/{}, invoice number = {}, total enroute charges = {}, flight movements count = {}, exchange rate count = {}",
                billingCenter.getId(),
                billingCenter.getName(),
                x.props.realInvoiceNumber,
                x.props.totalEnrouteChargesUSD,
                x.flightMovements.size(),
                x.exchangeRates.size());
        } else {
            logger.info("Created IATA invoice for all billing centers, invoice number = {}, total enroute charges = {}, flight movements count = {}, exchange rate count = {}",
                x.props.realInvoiceNumber,
                x.props.totalEnrouteChargesUSD,
                x.flightMovements.size(),
                x.exchangeRates.size());
        }
        return x;
    }

    protected InvoiceStateType getInitialInvoiceStateType() {
        return invoicesApprovalWorkflow.getInitialLedgerState(true);
    }

    protected ReportHelper getReportHelper() {
        return reportHelper;
    }

    private static final ReportFormat DFLT_FORMAT = ReportFormat.xlsx;
    private final Logger logger = LoggerFactory.getLogger(IataInvoiceService.class);
    private final ReportHelper reportHelper;
    private final CurrencyUtils currencyUtils;
    private final FlightMovementRepository flightMovementRepository;
    private final BillingLedgerService billingLedgerService;
    private final UserService userService;
    private final IataInvoiceDocumentCreator iataInvoiceDocumentCreator;
    private final InvoiceSequenceNumberHelper invoiceSequenceNumberHelper;
    private final UserEventLogService userEventLogService;
    private final InvoicesApprovalWorkflow invoicesApprovalWorkflow;
    private final RoundingUtils roundingUtils;
    private final SystemConfigurationService systemConfigurationService;
    private boolean invoiceByDateOfFlight;
}
