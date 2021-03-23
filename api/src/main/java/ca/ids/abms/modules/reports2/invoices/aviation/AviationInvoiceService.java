package ca.ids.abms.modules.reports2.invoices.aviation;

import ca.ids.abms.config.error.ErrorDTO;
import ca.ids.abms.config.error.ExceptionFactory;
import ca.ids.abms.config.error.RejectedReasons;
import ca.ids.abms.modules.accounts.Account;
import ca.ids.abms.modules.accounts.AccountRepository;
import ca.ids.abms.modules.accounts.AccountService;
import ca.ids.abms.modules.aircraft.AircraftRegistration;
import ca.ids.abms.modules.aircraft.AircraftRegistrationService;
import ca.ids.abms.modules.bankcode.BankCodeService;
import ca.ids.abms.modules.billingcenters.BillingCenter;
import ca.ids.abms.modules.billings.BillingLedger;
import ca.ids.abms.modules.billings.BillingLedgerRepository;
import ca.ids.abms.modules.billings.BillingLedgerService;
import ca.ids.abms.modules.common.enumerators.InvoiceStateType;
import ca.ids.abms.modules.flightmovements.FlightMovement;
import ca.ids.abms.modules.flightmovements.FlightMovementRepository;
import ca.ids.abms.modules.flightmovements.FlightMovementRepositoryUtility;
import ca.ids.abms.modules.flightmovements.category.FlightmovementCategory;
import ca.ids.abms.modules.flightmovements.category.FlightmovementCategoryRepository;
import ca.ids.abms.modules.flightmovements.enumerate.FlightMovementStatus;
import ca.ids.abms.modules.jobs.JobMessage;
import ca.ids.abms.modules.jobs.impl.InvoiceProgressCounter;
import ca.ids.abms.modules.reports2.async.AsyncInvoiceGeneratorScope;
import ca.ids.abms.modules.reports2.common.*;
import ca.ids.abms.modules.reports2.invoices.aviation.charges.AviationInvoiceChargeProvider;
import ca.ids.abms.modules.system.SystemConfiguration;
import ca.ids.abms.modules.system.SystemConfigurationService;
import ca.ids.abms.modules.system.summary.SystemConfigurationItemName;
import ca.ids.abms.modules.transactions.TransactionService;
import ca.ids.abms.modules.translation.Translation;
import ca.ids.abms.modules.unifiedtaxes.UnifiedTax;
import ca.ids.abms.modules.unifiedtaxes.UnifiedTaxService;
import ca.ids.abms.modules.usereventlogs.UserEventLogService;
import ca.ids.abms.modules.users.User;
import ca.ids.abms.modules.util.models.CurrencyUtils;
import ca.ids.abms.modules.utilities.invoices.InvoiceSequenceNumberHelper;
import ca.ids.abms.plugins.kcaa.aatis.modules.permitnumber.KcaaAatisPermitNumber;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import ca.ids.abms.modules.formulas.unifiedtax.*;

import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static ca.ids.abms.modules.reports2.invoices.ChargeSelection.*;
import static ca.ids.abms.util.MiscUtils.nvl;

@Service
public class AviationInvoiceService {

    private static final Logger LOG = LoggerFactory.getLogger (AviationInvoiceService.class);

    public static final ReportFormat DFLT_FORMAT = ReportFormat.pdf;
    private static final String NO_RELEVANT_FLIGHTS_FOUND = "No relevant flights found";

    private final ReportHelper reportHelper;
    private final ReportDocumentCreator reportDocumentCreator;
    private final FlightMovementRepository flightMovementRepository;
    private final BillingLedgerService billingLedgerService;
    private final AviationInvoiceDocumentCreator aviationInvoiceDocumentCreator;
    private final InvoiceSequenceNumberHelper invoiceSequenceNumberHelper;
    private final TransactionService transactionReceiptService;
    private final CurrencyUtils currencyUtils;
    private final UserEventLogService userEventLogService;
    private final RoundingUtils roundingUtils;
    private final SystemConfigurationService systemConfigurationService;
    private final FlightmovementCategoryRepository flightmovementCategoryRepository;
    private final BankCodeService bankCodeService;
    private final List<AviationInvoiceChargeProvider> aviationInvoiceChargeProviders;
    private final FlightMovementRepositoryUtility flightMovementRepositoryUtility;
    private final AccountService accountService;

    @SuppressWarnings("squid:S00107")
    public AviationInvoiceService(final ReportHelper reportHelper,
                                  final ReportDocumentCreator reportDocumentCreator,
                                  final FlightMovementRepository flightMovementRepository,
                                  final BillingLedgerService billingLedgerService,
                                  final AviationInvoiceDocumentCreator aviationInvoiceDocumentCreator,
                                  final InvoiceSequenceNumberHelper invoiceSequenceNumberHelper,
                                  final TransactionService transactionReceiptService,
                                  final CurrencyUtils currencyUtils,
                                  final UserEventLogService userEventLogService,
                                  final RoundingUtils roundingUtils,
                                  final SystemConfigurationService systemConfigurationService,
                                  final FlightmovementCategoryRepository flightmovementCategoryRepository,
                                  final BankCodeService bankCodeService,
                                  final List<AviationInvoiceChargeProvider> aviationInvoiceChargeProviders,
                                  final FlightMovementRepositoryUtility flightMovementRepositoryUtility,
                                  final AccountService accountService) {
        this.reportHelper = reportHelper;
        this.reportDocumentCreator = reportDocumentCreator;
        this.flightMovementRepository = flightMovementRepository;
        this.billingLedgerService = billingLedgerService;
        this.aviationInvoiceDocumentCreator = aviationInvoiceDocumentCreator;
        this.invoiceSequenceNumberHelper = invoiceSequenceNumberHelper;
        this.transactionReceiptService = transactionReceiptService;
        this.currencyUtils = currencyUtils;
        this.userEventLogService = userEventLogService;
        this.roundingUtils = roundingUtils;
        this.systemConfigurationService = systemConfigurationService;
        this.flightmovementCategoryRepository = flightmovementCategoryRepository;
        this.bankCodeService = bankCodeService;
        this.aviationInvoiceChargeProviders = aviationInvoiceChargeProviders;
        this.flightMovementRepositoryUtility = flightMovementRepositoryUtility;
        this.accountService = accountService;
    }

    @SuppressWarnings({"squid:S00107", "WeakerAccess"})
    @Transactional
    public ReportDocument createCombinedMonthlyInvoice (final BillingInterval billingInterval,
                                                        final LocalDateTime startDate,
                                                        final LocalDateTime endDateInclusive,
                                                        final boolean userBillingCenterOnly,
                                                        ReportFormat format,
                                                        final List <Integer> accountIdList,
                                                        boolean preview,
                                                        final String ipAddress,
                                                        final Integer flightMovementCategoryId,
                                                        final User currentUser) {
        if (currentUser.getBillingCenter() == null) {
            throw ExceptionFactory.getMissingBillingCenterOfCurrentUserException (ReportHelper.class, currentUser.getId().toString(), currentUser.getLogin());
        }

        final FlightmovementCategory flightmovementCategory;
        if (flightMovementCategoryId!=null) {
            flightmovementCategory = flightmovementCategoryRepository.findOne(flightMovementCategoryId);
        } else {
            flightmovementCategory = null;
        }
        final AviationInvoiceCreator invoiceCreator = buildTheInvoiceCreator(startDate, endDateInclusive,
            billingInterval, nvl (format, DFLT_FORMAT), preview, currentUser);

        final Map<Account, List<FlightMovement>> accountFlightMap = getAccountFlightMap(flightMovementCategoryId, billingInterval, startDate,
            endDateInclusive, userBillingCenterOnly, accountIdList, currentUser, null);

        // Sort accounts
        final List<Account> sortedAccounts = this.sortAccounts(accountFlightMap.keySet());
        final List<AviationInvoice> invoiceList = new ArrayList<>();
        for (final Account account : sortedAccounts) {
            generateAviationInvoice(account, invoiceCreator, accountFlightMap.get(account), invoiceList, flightmovementCategory,
                currentUser, ipAddress, null, preview);
        }

        if (preview || InvoiceStateType.PUBLISHED.equals(invoiceCreator.getInitialInvoiceStateType())) {
            return mergeDocumentsForPreview(invoiceCreator, currentUser, invoiceList, preview);
        }
        return null;
    }

    public AviationInvoiceCreator buildTheInvoiceCreator(final LocalDateTime startDate,
                                                         final LocalDateTime endDateInclusive,
                                                         final BillingInterval billingInterval,
                                                         final ReportFormat format, boolean preview,
                                                         final User currentUser) {

        // validate billing center
        ensureBillingCenterValid(currentUser.getBillingCenter());

        final LocalDateTime ldtNow = ZonedDateTime.now (ReportHelper.UTC_ZONE_ID).toLocalDateTime();
        do_ensureBillingPeriodValid(billingInterval, preview, ldtNow, startDate, endDateInclusive);

        final ZonedDateTime zdtStart = ZonedDateTime.of(startDate, ReportHelper.UTC_ZONE_ID);
        final String invoiceNameSuffix = String.format (" - %s", zdtStart.format (DateTimeFormatter.ofPattern("MMM YYYY")));

        // Create invoices for each account
        return new AviationInvoiceCreator (
            reportHelper,
            billingLedgerService,
            aviationInvoiceDocumentCreator,
            transactionReceiptService,
            invoiceSequenceNumberHelper,
            ldtNow,
            nvl (format, DFLT_FORMAT),
            invoiceNameSuffix,
            preview,
            currencyUtils,
            true, // approvalWorkflow
            startDate,
            endDateInclusive,
            roundingUtils,
            systemConfigurationService,
            bankCodeService,
            billingInterval,
            false, // pointOfSale
            currentUser,
            aviationInvoiceChargeProviders);
    }

    /**
     * Find all flight movements for the provided accounts that can be invoiced. All flight movements are detached from
     * managed entities and will need to be persisted for changes to take affect. The persistence of flight movements
     * should only be done at the end when not in preview mode.
     */
    @Transactional(readOnly = true)
    @SuppressWarnings("squid:S00107")
    public Map<Account, List<FlightMovement>> getAccountFlightMap(final Integer flightMovementCategoryId,
                                                                  final BillingInterval billingInterval,
                                                                  final LocalDateTime startDate,
                                                                  final LocalDateTime endDateInclusive,
                                                                  boolean userBillingCenterOnly,
                                                                  final List <Integer> accountIdList,
                                                                  final User currentUser,
                                                                  final InvoiceProgressCounter counter) {

        final BillingCenter billingCenter = currentUser.getBillingCenter();

        LOG.info ("Creating {} aviation invoices for billingCenter '{} - {}', billing period from '{}' to '{}', accountIdList '{}'",
            billingInterval, billingCenter.getId(), billingCenter.getName(), startDate.toLocalDate(),
            endDateInclusive.toLocalDate(), accountIdList);

        final FlightmovementCategory flightmovementCategory = getFlightMovementCategory(flightMovementCategoryId);

        final Map <Account, List <FlightMovement>> accountFlightMap = do_findMonthlyAccountFlightsByAccount (
            startDate,
            endDateInclusive,
            userBillingCenterOnly,
            accountIdList,
            billingCenter,
            counter);

        final Set<Map.Entry<Account, List<FlightMovement>>> accountsSet = accountFlightMap.entrySet();

        if (counter != null) {
            counter.setMessage("Filtering the flight movements retrieved");
            counter.update();
        }

        // filter flight movement lists to include only total charges greater then 0
        for (Map.Entry<Account, List<FlightMovement>> entry : accountsSet) {

            if (counter != null) {
                counter.setAccountName(entry.getKey().getName());
                counter.update();
            }

            boolean asInvoiced = do_includeAsInvoiced();
            entry.getValue().removeIf(flightMovement -> {
                if (!asInvoiced && this.do_checkFlightChargesZero(flightMovement, entry.getKey())) {
                    LOG.debug("skipping flight {} because its charges are zero", flightMovement.getFlightName());
                    return true;
                }
                return false;
            });

            if (counter != null) {
                counter.update();
            }
            //if invoicing is by flightmovementCategory, filter flight movements by provided flightmovementCategory
            if (do_checkIfAviationInvoicingIsByFlightmovementCategory() && flightmovementCategory!= null) {
                entry.getValue().removeIf(flightMovement -> {
                    if (!flightMovement.getFlightmovementCategory().equals(flightmovementCategory)) {
                        LOG.debug ("skipping flight {} because its category {} doesn't match requested category {}",
                                flightMovement.getFlightName(), flightMovement.getFlightmovementCategory().getName(),
                                flightmovementCategory.getName());
                        return true;
                    }
                    return false;
                });
            }
        }

        if (counter != null) {
            counter.setMessage("Validating the flights movements list");
            counter.update();
        }

        // remove any accounts that no longer have any flight movements after filtering above
        accountFlightMap.entrySet().removeIf(entry -> entry.getValue().isEmpty());

        // validate list is not empty
        return accountFlightMap;
    }

    private FlightmovementCategory getFlightMovementCategory(Integer flightMovementCategoryId) {
        if (flightMovementCategoryId != null) {
            return flightmovementCategoryRepository.findOne(flightMovementCategoryId);
        } else {
            return null;
        }
    }

    @SuppressWarnings("squid:S00107")
    @Transactional()

    public void generateAviationInvoice (final Account account,
                                         final AviationInvoiceCreator invoiceCreator,
                                         final List<FlightMovement> accountFlights,
                                         final List<AviationInvoice> invoicesList,
                                         final FlightmovementCategory flightmovementCategory,
                                         final User currentUser,
                                         final String ipAddress,
                                         final InvoiceProgressCounter counter,
                                         final boolean preview) {

        if (Boolean.TRUE.equals(account.getSeparatePaxInvoice())) {
            LOG.debug("The account {} requires a separate invoice for the passenger charges.", account.getName());

            final AviationInvoice onlyEnrouteInvoice = invoiceCreator.createInvoice(account, accountFlights, null, ONLY_ENROUTE, flightmovementCategory, counter, null, null);
            if (onlyEnrouteInvoice != null) {
                invoicesList.add(onlyEnrouteInvoice);
                if(!preview) {
                    userEventLogService.createInvoiceUserEventLogAsync(ipAddress, String.valueOf(onlyEnrouteInvoice.billingLedger().getId()), currentUser);
                }
            }
            final AviationInvoice onlyPaxInvoice = invoiceCreator.createInvoice(account, accountFlights, null, ONLY_PAX, flightmovementCategory, counter, null, null);
            if (onlyPaxInvoice != null) {
                invoicesList.add(onlyPaxInvoice);
                if(!preview) {
                    userEventLogService.createInvoiceUserEventLogAsync(ipAddress, String.valueOf(onlyPaxInvoice.billingLedger().getId()), currentUser);
                }
            }
        } else {
            LOG.debug("The account {} requires an unique invoice for all charges.", account.getName());
            final AviationInvoice invoice = invoiceCreator.createInvoice(account, accountFlights, null, ALL, flightmovementCategory, counter, null, null);
            if (invoice != null) {
                invoicesList.add(invoice);
                if(!preview) {
                    userEventLogService.createInvoiceUserEventLogAsync(ipAddress, String.valueOf(invoice.billingLedger().getId()), currentUser);
                }
            }
        }

        // persist flight movement changes if not in preview mode, should only be status and invoice ids
        // this is necessary as account flights are detached from managed entities
        if (!preview) {
            for (FlightMovement fm : accountFlights) {
                flightMovementRepositoryUtility.persist(fm);
            }
        }
    }

    public ReportDocument mergeDocumentsForPreview (final AviationInvoiceCreator invoiceCreator,
            final User currentUser,
            final List <AviationInvoice> invoiceList, boolean preview
        ) {
        return mergeDocumentsForPreview (invoiceCreator.getReportFormat(),invoiceCreator.getInvoiceNameSuffix(), currentUser,invoiceList,  preview );

    }
    public ReportDocument mergeDocumentsForPreview (
        final ReportFormat reportFormat, String invoiceNameSuffix, final User currentUser,
        final List <AviationInvoice> invoiceList, boolean preview
    ) {

        final BillingCenter billingCenter = currentUser.getBillingCenter();

        // Combine multiple report documents into one
        final String bundleName = String.format (
            preview ? Translation.getLangByToken("Aviation invoices PREVIEW") + " - %s%s" : Translation.getLangByToken("Aviation invoices") + " - %s%s",
            billingCenter.getName(),
            invoiceNameSuffix);

        ReportDocument combinedDoc = this.do_combineInvoices (reportFormat, bundleName, invoiceList);

        if (LOG.isInfoEnabled()) LOG.info("Created combined aviation invoice document '{}' for billing center '{} - {}', " +
                "file length '{}' byte(s)",
            combinedDoc.fileName(), billingCenter.getId(), billingCenter.getName(), combinedDoc.contentLength());

        return combinedDoc;
    }

    /**
     * Create an invoice and return the invoice document for the given account and flight IDs; and simultaneously pay it
     * if payment supplied.
     *
     * This creates billing ledger records and also creates a document in the output given format. You need
     * to rollback current transaction if you just want a preview. This method creates an invoice
     * for the billing center associated with the current user for the given flight IDs.
     *
     * This document also records a payment (credit) transaction for the generated invoice,
     * using the specified invoicePaymentParameters.
     *
     * It returns a single PDF file that contains both the invoice and the receipt.
     *
     * @param format          - output format; if null defaults to PDF
     * @param accountId       - ID of the account; all flight IDs, if provided must belong to this acount
     * @param flightIdList    - include only these flights in the invoice; all flights must belong to the same
     *                          account
     * @param payment         - transaction/payment information
     * @param preview         - if true, the output document will have a fake invoice number ("PREVIEW");
     *                          useful for the preview scenario
     * @return combined invoice and receipt in one document
     */
    @SuppressWarnings({"WeakerAccess", "squid:S00107"})
    @Transactional
    public ReportDocument createPosOrFmInvoice(final ReportFormat format,
                                               final Integer accountId,
                                               final List <Integer> flightIdList,
                                               final InvoicePaymentParameters payment,
                                               final boolean preview,
                                               final String ipAddress,
                                               final Integer flightMovementCategoryId,
                                               final List<KcaaAatisPermitNumber> invoicePermits,
                                               final boolean pointOfSale,
                                               final String selectedInvoiceCurrency) {

        final ZonedDateTime zdtNow = ZonedDateTime.now (ReportHelper.UTC_ZONE_ID);
        final LocalDateTime ldtNow = zdtNow.toLocalDateTime();
        final BillingCenter billingCenter = reportHelper.getBillingCenterOfCurrentUser();
        final ReportFormat reportFormat = nvl (format, DFLT_FORMAT);
        final Account account = reportHelper.getAccount (accountId);
        FlightmovementCategory flightmovementCategory = null;

        if (flightMovementCategoryId != null) {
        	flightmovementCategory = flightmovementCategoryRepository.findOne(flightMovementCategoryId);
        }

        ReportDocument output;

        LOG.info("Creating aviation invoices for billingCenter={}/{}, account={}/{}, flightIdList={}",
                billingCenter.getId(),
                billingCenter.getName(),
                account.getId(),
                account.getName(),
                flightIdList);

        // validate billing center
        ensureBillingCenterValid(billingCenter);

        // find flights by account
        final Collection <FlightMovementStatus> statusList = pointOfSale
                ? Arrays.asList (FlightMovementStatus.PENDING, FlightMovementStatus.INCOMPLETE)
                : Collections.singletonList(FlightMovementStatus.PENDING);
        final List<FlightMovement> accountFlights = do_findAccountFlights(account, statusList, flightIdList);
        // filter flight movement list to include only total charges greater then 0
        // TODO: clean up PENDING flight movements excluded from this list by changing the status or removing all together

        if (!pointOfSale && !do_includeAsInvoiced()) {
            accountFlights.removeIf(fm -> {
                if (this.do_checkFlightChargesZero(fm, account)) {
                    LOG.debug("skipping flight {} because all of its charges are zero", fm.getFlightName());
                    return true;
                }
                return false;
            });
        }

        // validate list is not empty
        this.do_ensureFlightListNotEmpty(accountFlights);

        final String invoiceNameSuffix = "";

        // Create the invoice including all charges
        final AviationInvoiceCreator invoiceCreator = new AviationInvoiceCreator(
            reportHelper,
            billingLedgerService,
            aviationInvoiceDocumentCreator,
            transactionReceiptService,
            invoiceSequenceNumberHelper,
            ldtNow,
            reportFormat,
            invoiceNameSuffix,
            preview,
            currencyUtils,
            false,
            null,
            ldtNow, // use datetime now because this method is called only from point-of-sale which uses issue date
            roundingUtils,
            systemConfigurationService,
            bankCodeService,
            null, // billingInterval
            pointOfSale,
            reportHelper.getCurrentUser(),
            aviationInvoiceChargeProviders);

        boolean supportPassengerCharges = reportHelper.passengerChargesEnabled();

        AviationInvoice invoice;
        if (supportPassengerCharges && (Boolean.FALSE.equals(account.getSeparatePaxInvoice()) || !ReportFormat.pdf.equals(reportFormat))) {
            invoice = invoiceCreator.createInvoice(account, accountFlights, payment, ALL, flightmovementCategory, null, invoicePermits, selectedInvoiceCurrency);
        } else {
            invoice = invoiceCreator.createInvoice(account, accountFlights, payment, ONLY_ENROUTE, flightmovementCategory, null, invoicePermits, selectedInvoiceCurrency);
        }

        /* Create the passenger charges invoice, if required */
        String bundleName;
        AviationInvoiceCreator paxInvoiceCreator = null;
        AviationInvoice paxInvoice = null;
        if (supportPassengerCharges && Boolean.TRUE.equals(account.getSeparatePaxInvoice())
            && ReportFormat.pdf.equals(reportFormat)) {
            LOG.debug("The account {} requires a separate invoice for the passenger charges.", account.getName());

             // Create the invoice including pax charges
            paxInvoiceCreator = new AviationInvoiceCreator(
                reportHelper,
                billingLedgerService,
                aviationInvoiceDocumentCreator,
                transactionReceiptService,
                invoiceSequenceNumberHelper,
                ldtNow,
                reportFormat,
                invoiceNameSuffix,
                preview,
                currencyUtils,
                false,
                null,
                ldtNow, // use datetime now because this method is called only from point-of-sale which uses issue date
                roundingUtils,
                systemConfigurationService,
                bankCodeService,
                null, // billingInterval
                true,
                reportHelper.getCurrentUser(),
                aviationInvoiceChargeProviders);

            paxInvoice = paxInvoiceCreator.createInvoice(account, accountFlights, null,ONLY_PAX, flightmovementCategory, null, null, selectedInvoiceCurrency);
        } else {
            LOG.debug("The account {} requires an unique invoice for all charges.", account.getName());
        }
        if (paxInvoice != null) {
            bundleName =  String.format (
                Translation.getLangByToken("Aviation invoices") +
                " %s " +
                Translation.getLangByToken("and") +
                " %s " +
                Translation.getLangByToken("with receipt"),
                invoice.invoiceData().global.invoiceNumber,
                paxInvoice.invoiceData().global.invoiceNumber);
        } else {
            bundleName =  String.format (
                Translation.getLangByToken("Aviation invoice") +
                " %s " +
                Translation.getLangByToken("with receipt"), invoice.invoiceData().global.invoiceNumber);
        }

        if (payment != null) {
            /* Create the transaction payments, if required */

            if (paxInvoice != null) {
                /* Two invoices generated: the payment will be splitted in two payments */

                if (reportFormat == null || !reportFormat.equals(ReportFormat.pdf)) {
                    throw new UnsupportedOperationException ("This report doesn't support output format" + "\"" + reportFormat.toString() + "\"");
                }
                final InvoicePaymentParameters paxPayment = payment.clone();

                final Double paxTotalAmount = paxInvoice.invoiceData().global.totalAmount;
                final Double paymentPaxTotalAmount = currencyUtils.getCurrencyExchangeRateService().getExchangeAmount(
                    payment.getPaymentExchangeRate(), paxTotalAmount, 2, true);

                paxPayment.setAmount(paxTotalAmount);
                paxPayment.setPaymentAmount(paymentPaxTotalAmount);

                final Double enrouteTotalAmount = invoice.invoiceData().global.totalAmount;
                final Double paymentEnrouteTotalAmount = currencyUtils.getCurrencyExchangeRateService().getExchangeAmount(
                    payment.getPaymentExchangeRate(), enrouteTotalAmount, 2, true);

                payment.setAmount(enrouteTotalAmount);
                payment.setPaymentAmount(paymentEnrouteTotalAmount);

                invoiceCreator.createPayment (invoice, payment);
                paxInvoiceCreator.createPayment (paxInvoice, paxPayment);

                if (ReportFormat.pdf.equals(reportFormat)) {
                    output = buildFinalDocument(bundleName, invoiceCreator.getInitialInvoiceStateType(),
                        preview, invoice.invoiceDocument(), invoice.transactionDocument(),
                        paxInvoice.invoiceDocument(), paxInvoice.transactionDocument());
                } else {
                    output = buildFinalDocument(bundleName, invoiceCreator.getInitialInvoiceStateType(),
                        preview, invoice, paxInvoice);
                }
            } else {
                /* Pay the only one invoice generated */
                if (reportFormat == null || !reportFormat.equals(ReportFormat.pdf)) {
                    String outputFormat = reportFormat == null ? "unknown" : reportFormat.toString();
                    throw new UnsupportedOperationException ("This report doesn't support output format '" +
                        outputFormat + "'");
                }
                invoiceCreator.createPayment (invoice, payment);

                if (ReportFormat.pdf.equals(reportFormat)) {
                    output = buildFinalDocument(bundleName, invoiceCreator.getInitialInvoiceStateType(),
                        preview, invoice.invoiceDocument(), invoice.transactionDocument());
                } else {
                    output = buildFinalDocument(bundleName, invoiceCreator.getInitialInvoiceStateType(),
                        preview, invoice);
                }
            }

            if (output != null && LOG.isInfoEnabled()) LOG.info ("Created combined aviation invoice + receipt document '{}' for billing " +
                    "center '{} - {}', file length '{}' byte(s)",
                output.fileName(), billingCenter.getId(), billingCenter.getName(), output.contentLength());
        } else {
            /* In case the approval workflow is enabled, the document won't be returned to the frontend because
             * it is not approved and published yet.
             */
            if (paxInvoice != null) {
                output = buildFinalDocument(bundleName, invoiceCreator.getInitialInvoiceStateType(),
                    preview, invoice, paxInvoice);
            } else {
                output = buildFinalDocument(bundleName, invoiceCreator.getInitialInvoiceStateType(),
                    preview, invoice);
            }
        }

        /* Log the creation of the invoices before to return them */
        if (!preview) {
            try {
                userEventLogService.createInvoiceUserEventLog(ipAddress, invoice.billingLedger().getId().toString());
                if (paxInvoice != null) {
                    userEventLogService.createInvoiceUserEventLog(ipAddress, paxInvoice.billingLedger().getId().toString());
                }
            } catch (Exception e) {
                LOG.warn("Cannot update the user event log because: {}", e.getLocalizedMessage());
            }
        }

        return output;
    }

    private ReportDocument buildFinalDocument (final String bundleName, final InvoiceStateType invoiceStateType,
                                               boolean preview, final AviationInvoice ... documents) {
        ReportDocument doc = null;
        if (preview || invoiceStateType == InvoiceStateType.PUBLISHED) {
            assert (documents != null && documents.length > 0);
            if (documents.length == 1) {
                doc = documents[0].invoiceDocument();
            } else {
                doc = do_combineInvoices(ReportFormat.pdf, bundleName, Arrays.asList(documents));
            }
        }
        return doc;
    }

    private ReportDocument buildFinalDocument (final String bundleName, final InvoiceStateType invoiceStateType,
                                               boolean preview, final ReportDocument ... documents) {
        ReportDocument doc = null;
        if (preview || invoiceStateType == InvoiceStateType.PUBLISHED) {
            assert (documents != null && documents.length > 0);
            if (documents.length == 1) {
                doc = documents[0];
            } else {
                doc = reportDocumentCreator.combinePdfFiles(bundleName, Arrays.asList(documents));
            }
        }
        return doc;
    }

    /**
     * Return the list of PENDING billable flights for the given account
     */
    Page <FlightMovement> getAllFlightMovementsForAccountInvoice(
        final Integer accountId, final boolean userBillingCenterOnly, final Integer flightMovementCategoryId,
        final Pageable pageable
    ) {
        long elapsedTime = System.currentTimeMillis();
        final Account account = reportHelper.getAccount (accountId);
        final Page<FlightMovement> flights;
        final BillingCenter billingCenter = reportHelper.getBillingCenterOfCurrentUser();

        boolean aviationInvoicingIsByFlightmovementCategory = do_checkIfAviationInvoicingIsByFlightmovementCategory();
        boolean isIataSupport = systemConfigurationService.getBoolean(SystemConfigurationItemName.IATA_INVOICING_SUPPORT);

        LOG.debug("Looking for billable flights for account id:{} name:{}", account.getId(), account.getName());
        if (userBillingCenterOnly) {
        	if (aviationInvoicingIsByFlightmovementCategory && flightMovementCategoryId != null) {
        		flights = flightMovementRepository.findAllForGeneralAviationInvoiceByAccountAndBCAndFlightMovementCategory(
        		    account.getId(), billingCenter.getId(), flightMovementCategoryId, isIataSupport, pageable);
        	} else {
        		flights = flightMovementRepository.findAllForGeneralAviationInvoiceByAccountAndBC(
        		    account.getId(), billingCenter.getId(), isIataSupport, pageable);
        	}
        }
        else {
        	if (aviationInvoicingIsByFlightmovementCategory && flightMovementCategoryId != null) {
        		flights = flightMovementRepository.findAllForGeneralAviationInvoiceByAccountAndFlightMovementCategory(
        		    account.getId(), flightMovementCategoryId, isIataSupport, pageable);
        	} else {
        		flights = flightMovementRepository.findAllForGeneralAviationInvoiceByAccount(
        		    account.getId(), isIataSupport, pageable);
        	}
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Found {} billable flights for the account id:{} and billing center id:{} name:{} of the current user. Finished after {} ms.",
                flights != null ? flights.getTotalElements() : "0", accountId, billingCenter.getId(), billingCenter.getName(),
                System.currentTimeMillis() - elapsedTime );
        }

        return flights;
    }

    // ------------------------ private -----------------------------

    /**
     * Find PENDING flight movements and sort them by account into a map. All flight movements are detached from
     * managed entities and will need to be persisted for changes to take affect.
     */
    private Map <Account, List <FlightMovement>> do_findMonthlyAccountFlightsByAccount (final LocalDateTime startDate,
                                                                                        final LocalDateTime endDate,
                                                                                        final boolean userBillingCenterOnly,
                                                                                        final Collection <Integer> accountIdList,
                                                                                        final BillingCenter billingCenter,
                                                                                        final InvoiceProgressCounter counter) {

        final String bcName = String.format ("#%d[name=%s]", billingCenter.getId(), billingCenter.getName());
        final boolean invoiceByDateOfFlight = systemConfigurationService.getBoolean(SystemConfigurationItemName.INVOICE_BY_DAY_OF_FLIGHT);

        final String invoiceBy = invoiceByDateOfFlight ? "date of flight" : "billing date";
        LOG.debug ("Looking for billable flights by {} for billingCenter {}, startDate {}, endDate{}",
            invoiceBy, bcName, startDate.toLocalDate(), endDate.toLocalDate());

        if (counter != null) {
            if (!accountIdList.isEmpty() && accountIdList.size() == 1) {
                String accountName = accountService.getAccountNameById(accountIdList.iterator().next());
                counter.setAccountName(accountName);
            }
            counter.setMessage("Looking for billable flight movements");
            counter.update();
        }

        final List <FlightMovement> flightsList = this.flightMovementRepository.findAll((root, query, criteriaBuilder) -> {
            final List <Predicate> andList = new ArrayList<>();

            final String dateOfFlight = "dateOfFlight";
            final String billingDate = "billingDate";
            final String flightId = "flightId";
            final String id = "id";
            final String account = "account";

             if (invoiceByDateOfFlight) {
                 andList.add(criteriaBuilder.between(root.get(dateOfFlight), startDate.with(LocalTime.MIN), endDate.with(LocalTime.MAX)));
                 query.orderBy(criteriaBuilder.asc(root.get(dateOfFlight)), criteriaBuilder.asc(root.get(flightId)), criteriaBuilder.asc(root.get(id)));
             } else {
                 andList.add(criteriaBuilder.between(root.get(billingDate), startDate.with(LocalTime.MIN), endDate.with(LocalTime.MAX)));
                 query.orderBy(criteriaBuilder.asc(root.get(billingDate)), criteriaBuilder.asc(root.get(flightId)), criteriaBuilder.asc(root.get(id)));
             }

             andList.add(criteriaBuilder.equal(root.get("status"), FlightMovementStatus.PENDING));

             if (accountIdList != null && !accountIdList.isEmpty()) {
                 andList.add(root.join(account).get(id).in(accountIdList));
             } else {
                 andList.add(criteriaBuilder.equal(root.join(account).get("cashAccount"), false));
             }

             if (userBillingCenterOnly) {
                 andList.add(criteriaBuilder.equal(root.join("billingCenter").get(id), billingCenter.getId()));
             }

             andList.add(criteriaBuilder.or(criteriaBuilder.isNull(root.get("passengerInvoiceId")), criteriaBuilder.isNull(root.get("otherInvoiceId")),
                 criteriaBuilder.and(criteriaBuilder.isNull(root.get("enrouteInvoiceId")),
                     criteriaBuilder.equal(root.join(account).get("iataMember"), false))));

            // Combine all predicates
            return criteriaBuilder.and(andList.toArray(new Predicate[0]));
        });

        LOG.debug ("Found {} pending flight(s) for this aviation invoice", flightsList != null ? flightsList.size() : "0");

        if (counter != null) {
            if (CollectionUtils.isNotEmpty(flightsList)) {
                counter.setMessage(new JobMessage.Builder()
                    .setMessage("Loading {{flights}} flight movements found")
                    .addVariable("flights", flightsList.size())
                    .addVariable("accountName", counter.getAccountName())
                    .addVariable("accountsTotal", counter.getAccountsTotal())
                    .addVariable("accountNumber", counter.getAccountNumber())
                    .build());
            } else {
                counter.setMessage("No billable flight movements has been found");
                counter.update();
            }
        }

        // detach all flight movements as we don't want it holding onto records in managed entities
        // we will persist any status changes at the end if not in preview mode
        if (flightsList != null) {
            for (FlightMovement fm : flightsList) {
                flightMovementRepositoryUtility.detach(fm);
            }
        }

        final Map <Account, List <FlightMovement>> map = reportHelper.classifyFlightsByAccount (flightsList);
        if (counter != null) {
            counter.update();
        }
        return map;
    }

    /**
     * Find PENDING flight movements that belong to the given account
     */
    private List <FlightMovement> do_findAccountFlights (
            final Account account,
            final Collection <FlightMovementStatus> statusList,
            final Collection <Integer> flightIdList) {
        final String accountName = String.format ("#%d[name=%s]", account.getId(), account.getName());
        LOG.debug ("Looking for billable flights for account={}, statusList={}, flightIdList={}",
                accountName, statusList, flightIdList);
        final List <String> statusStrList = statusList.stream()
                .map(FlightMovementStatus::toString)
                .collect(Collectors.toList());
        final List <FlightMovement> flights =
                (flightIdList == null || flightIdList.isEmpty()) ?
                        flightMovementRepository.findForGeneralAviationInvoiceByAccount (account.getId(), statusStrList) :
                        flightMovementRepository.findForGeneralAviationInvoiceByAccount (account.getId(), statusStrList, flightIdList);

        LOG.debug ("Found {} billable flight(s) for this aviation invoice", flights != null ? flights.size() : "0");
        return flights;
    }

    /**
     * Make sure flight list is not empty; throw an exception otherwise
     */
    private void do_ensureFlightListNotEmpty (final Collection <FlightMovement> flights) {
        if (flights != null && flights.isEmpty()) {
                new ErrorDTO.Builder(NO_RELEVANT_FLIGHTS_FOUND)
                    .throwInvalidDataException();
        }
    }

    /**
     * Make sure billing period is valid for invoice.
     * User can preview monthly invoice for current month but not generate
     *
     * @param billingInterval       - billing period monthly/weekly
     * @param preview               - if true, the output document will have a fake invoice number ("PREVIEW");
     *                                useful for the preview scenario
     * @param today                 - current date
     * @param startDate             - start date for billing period
     * @param endDateInclusive      - end date (inclusive) for billing period
     */
    private void do_ensureBillingPeriodValid(BillingInterval billingInterval, boolean preview, LocalDateTime today,
                                             LocalDateTime startDate, LocalDateTime endDateInclusive) {

        final LocalDateTime ldtStartOfCurrentMonth = LocalDateTime.of(today.getYear(), today.getMonth(), 1, 0, 0, 0, 0);
        final boolean validMonthlyBillingPeriod = startDate.isBefore(ldtStartOfCurrentMonth);
        final boolean validWeeklyBillingPeriod = endDateInclusive.isBefore(today);
        final boolean validOpenBillingPeriod = endDateInclusive.isBefore(today) && startDate.isBefore(endDateInclusive);

        // Ensure billing period is valid if not a preview
        if (!validMonthlyBillingPeriod && !preview && billingInterval.equals(BillingInterval.MONTHLY) ||
            !validWeeklyBillingPeriod && !preview && billingInterval.equals(BillingInterval.WEEKLY) ||
            !validOpenBillingPeriod && !preview && billingInterval.equals(BillingInterval.OPEN)) {

            LOG.debug("Invalid IATA invoice billing period.");

            throw new ErrorDTO.Builder("Invalid Aviation invoice billing period.")
                .appendDetails("Aviation invoices cannot be generated for the current or future billing period." +
                    "Please choose a valid Aviation invoice billing period.")
                .addRejectedReason(RejectedReasons.VALIDATION_ERROR)
                .buildInvalidDataException();
        }
    }

    /**
     * If system configuration item "Prohibit aviation charges to HQ billing centre" enabled,
     * HQ Billing Center cannot create aviation charges.
     *
     * @param billingCenter billing center to validate
     */
    private void ensureBillingCenterValid(final BillingCenter billingCenter) {

        // no aviation charges to hq when sys config set
        if (billingCenter != null && billingCenter.getHq()
            && systemConfigurationService.getBoolean(SystemConfigurationItemName.PROHIBIT_HQ_AVIATION_CHARGES)) {
            LOG.warn("Invalid NON-IATA invoice billing centre '{}', cannot be HQ.", billingCenter.getName());
            throw new ErrorDTO.Builder("Cannot generate NON-IATA aviation invoice for HQ billing centre.")
                .addRejectedReason(RejectedReasons.VALIDATION_ERROR)
                .buildInvalidDataException();
        }
    }

    /**
     * Sort account list by name
     */
    public List <Account> sortAccounts (final Collection <Account> accountList) {
        return accountList.stream().sorted(Comparator.comparing(Account::getName)).collect(Collectors.toList());
    }

    /**
     * Combine multiple invoices into one
     */
    private ReportDocument do_combineInvoices(
        final ReportFormat reportFormat, final String bundleName, final List<AviationInvoice> invoiceList
    ) {
        switch (reportFormat) {
        case json:
            return reportDocumentCreator.createJsonDocument (
                    bundleName,
                    invoiceList.stream().map (AviationInvoice::invoiceData).collect (Collectors.toList()));
        case csv:
        case txt:
            return reportDocumentCreator.createCsvDocument (
                    bundleName,
                    invoiceList.stream()
                        .map (x->x.invoiceData().flightInfoList)
                        .flatMap(List::stream)
                        .collect (Collectors.toList()),
                    AviationInvoiceData.FlightInfo.class, false);
        case pdf:
            //
            return reportDocumentCreator.combinePdfFiles (bundleName,
                    invoiceList.stream().map (AviationInvoice::invoiceDocument).collect (Collectors.toList()));
        case docx:
        case xlsx:
        case xml:
        default:
            throw new UnsupportedOperationException("This invoice doesn't support output format" + "\"" + reportFormat.toString() + "\"");
        }
    }

    /**
     * Check if a Flight Movement has a total cost of zero and thus can be removed from inclusion in an invoice.
     *
     * @param flightMovement flight movement to validate
     * @param account account to validate against
     * @return true if all flight charges are zero
     */
    private boolean do_checkFlightChargesZero(final FlightMovement flightMovement, final Account account) {

        boolean iataInvoiceEnabled = systemConfigurationService.getBoolean(SystemConfigurationItemName.IATA_INVOICING_SUPPORT);

        // validate enroute charges if only NON-IATA member that don't have an invoice yet
        // if iata invoice support is false we have to ignore the iata member check
        // check enroute charges are greater then zero
        if (((!iataInvoiceEnabled) || (account.getIataMember() != null && !account.getIataMember()))
                && flightMovement.getEnrouteInvoiceId() == null && (flightMovement.getEnrouteCharges() != null && flightMovement.getEnrouteCharges() > 0)) {
                return false;
        }

        // validate other charges if no invoice yet
        if (flightMovement.getOtherInvoiceId() == null) {

            // check TASP charges are greater then zero
            if (flightMovement.getTaspCharge() != null && flightMovement.getTaspCharge() > 0)
                return false;

            // check approach charges are greater then zero
            if (flightMovement.getApproachCharges() != null && flightMovement.getApproachCharges() > 0)
                return false;

            // check aerodrome charges are greater then zero
            if (flightMovement.getAerodromeCharges() != null && flightMovement.getAerodromeCharges() > 0)
                return false;

            // check parking charges are greater then zero
            if (flightMovement.getParkingCharges() != null && flightMovement.getParkingCharges() > 0)
                return false;

            // check late arrival charges are greater then zero
            if (flightMovement.getLateArrivalCharges() != null && flightMovement.getLateArrivalCharges() > 0)
                return false;

            // check late departure charges are greater then zero
            if (flightMovement.getLateDepartureCharges() != null && flightMovement.getLateDepartureCharges() > 0)
                return false;
        }

        // validate passenger charges if no invoice yet
        if (flightMovement.getPassengerInvoiceId() == null) {

            // check domestic passenger charges are greater then zero
            if (flightMovement.getDomesticPassengerCharges() != null && flightMovement.getDomesticPassengerCharges() > 0)
                return false;

            // check international passenger charges are greater then zero
            if (flightMovement.getInternationalPassengerCharges() != null && flightMovement.getInternationalPassengerCharges() > 0)
                return false;
        }

        // otherwise return true to remove flight movement
        return true;
    }

    private boolean do_checkIfAviationInvoicingIsByFlightmovementCategory() {
    	final SystemConfiguration aviationInvoiceCurrencyItem = systemConfigurationService.getOneByItemName(SystemConfigurationItemName.INVOICE_CURRENCY_ENROUTE);
    	boolean invoiceByFlightMovementCategory = systemConfigurationService.getBoolean(SystemConfigurationItemName.INVOICE_BY_FLIGHT_MOVEMENT_CATEGORY);

    	return invoiceByFlightMovementCategory && aviationInvoiceCurrencyItem!=null;
    }

    private boolean do_includeAsInvoiced() {
       final SystemConfiguration zeroChargeFlight = systemConfigurationService.getOneByItemName(SystemConfigurationItemName.MARK_ZERO_FLIGHT_COSTS_AS_PAID);
       return  (zeroChargeFlight.getCurrentValue() != null && zeroChargeFlight.getCurrentValue().equals("As invoiced"));

    }

    @Transactional(readOnly = true)
    public FlightmovementCategory findFlightMovementCategory(final Integer flightCategoryId) {
        LOG.debug("Find FlightmovementCategory by id : {}", flightCategoryId);
        return flightmovementCategoryRepository.findOne(flightCategoryId);
    }

    @Transactional()
    public Boolean processAccountUT(AsyncInvoiceGeneratorScope scope,AccountRepository accountRepository, BillingLedgerService billingLedgerService, AircraftRegistrationService aircraftRegistrationService, Account account, UnifiedTaxService unifiedTaxService, List <AviationInvoice> invoiceList,
             FlightmovementCategory flightmovementCategory,User currentUser) {
        Boolean accountProcessed = false;
        LocalDateTime currentDate = LocalDateTime.now();
        LocalDateTime expiry_date = null;
        LocalDateTime yearManufacture = null;
        LocalDateTime yearValidity = null;
        Double taxAmount = 0.0;
        Integer accountId = account.getId();
        Double aircraftWeight = 0.0;
        Double invoiceAmount = 0.0;
        Integer mesiRimasti = 0;
        Integer month = currentDate.getMonthValue();
        FormulaEvaluatorUT fe = null;
        UnifiedTax ut = null;
        String rate = null;
        final BillingInterval billingInterval = scope.getBillingInterval();

        try {

            LOG.debug("Sono all'interno del metodo processAccountUT");
            Set<BillingLedger> bilSet = account.getBillingLedgers();
            // Calcolo fattura
            for (BillingLedger bl : bilSet) {
                if (billingInterval == BillingInterval.ANNUALLY) {
                    LOG.debug("billingInterval è ANNUALLY");
                    processAccountUTBillingIntervalANNUALLY(unifiedTaxService, scope, account, bl, aircraftRegistrationService);
/*
                    for (AircraftRegistration ar : account.getAircraftRegistrations()) {
                        if ((scope.getStartDate() != null) && (scope.getEndDateInclusive() != null)) {

                            if(ar.getCoaExpiryDate() == null && ar.getCoaIssueDate() ==null){
                                LOG.debug("Il TU non è pagato");
                            }else if(ar.getCoaExpiryDate() != null || ar.getCoaIssueDate() !=null){

                            }else if (!((scope.getStartDate().isBefore(ar.getCoaExpiryDate()))
                                && (scope.getStartDate().isAfter(ar.getCoaIssueDate()))
                                && (scope.getEndDateInclusive().isBefore(ar.getCoaExpiryDate()))
                                && (scope.getEndDateInclusive().isAfter(ar.getCoaIssueDate()))
                                && (scope.getStartDate().isBefore(scope.getEndDateInclusive())))) {
                                LOG.debug("Il TU non è pagato");
                                aircraftWeight = ar.getMtowOverride();
                                yearManufacture = ar.getAircraftServiceDate();
                                if (yearManufacture != null) {
                                    if (scope.getStartDate().getYear() == scope.getEndDateInclusive().getYear()) {
                                        LocalDateTime nowYear = LocalDateTime.now();
                                        // settare anno in base a localdatetime (anno mese giorno ora min sec
                                        // nanosecondi)
                                        yearValidity = nowYear.of(scope.getStartDate().getYear(), Month.JANUARY, 1, 0, 0,
                                            0);
                                        try {
                                            ut = unifiedTaxService.findUnifiedTaxByValidityYearAndManufactureYear(
                                                yearManufacture, yearValidity);

                                            rate = ut.getRate();

                                            fe = new FormulaEvaluatorUTImpl(new JavascriptEngineUTFactory());
                                            try {
                                                taxAmount = fe.evalDouble(rate);
                                            } catch (Exception e) {
                                                // TODO Auto-generated catch block
                                                e.printStackTrace();
                                            }
                                            if (taxAmount != null) {
                                                invoiceAmount = (aircraftWeight / 1000) * taxAmount;
                                                LOG.debug("invoiceAmount è {} ", invoiceAmount);
                                                bl.setInvoiceAmount(invoiceAmount);
                                                billingLedgerService.updateBillingLedgerByIdAndInvoiceAmount(bl.getId(),
                                                    invoiceAmount);
                                                expiry_date = bl.getInvoiceDateOfIssue().plusYears(1);
                                                LOG.debug(
                                                    "AircraftRegistration ha id = {}, date of issue = {} e expiry date = {}",
                                                    ar.getId(), bl.getInvoiceDateOfIssue(), expiry_date);
                                                aircraftRegistrationService.updateAircraftRegistrationByIdAndDates(
                                                    ar.getId(), bl.getInvoiceDateOfIssue(), expiry_date);
                                                accountProcessed = true;
                                            } else {
                                                LOG.debug("invoiceAmount è {} e taxAmount è {} ", invoiceAmount, taxAmount);
                                            }
                                        } catch (Exception e) {
                                            LOG.debug("ut ha valore {}", ut);
                                        }
                                    } else {
                                        LOG.debug(
                                            "l'anno di startDate ha valore = {}, l'anno di endDateInclusive ha valore = {}",
                                            scope.getStartDate().getYear(), scope.getEndDateInclusive().getYear());
                                    }
                                } else {
                                    LOG.debug(
                                        "la data yearManufacture ha valore = {}, la data startDate ha valore = {}, la data endDateInclusive ha valore = {}",
                                        yearManufacture, scope.getStartDate(), scope.getEndDateInclusive());
                                }
                            } else {
                                LOG.debug("Il TU è pagato");
                            }
                        } else {
                            LOG.debug("startDate ha valore {} e endDateInclusive ha valore {}", scope.getStartDate(),
                                scope.getEndDateInclusive());
                        }
                    }
*/

                } else if(billingInterval == BillingInterval.PARTIALLY) {
                    processAccountUTBillingIntervalPARTIALLY(scope, account, bl);
                    /*
                    for (AircraftRegistration ar : account.getAircraftRegistrations()) {
                        if ((scope.getStartDate() != null) && (scope.getEndDateInclusive() != null)) {
                            if (!((scope.getStartDate().isBefore(ar.getCoaExpiryDate()))
                                && (scope.getStartDate().isAfter(ar.getCoaIssueDate()))
                                && (scope.getEndDateInclusive().isBefore(ar.getCoaExpiryDate()))
                                && (scope.getEndDateInclusive().isAfter(ar.getCoaIssueDate()))
                                && (scope.getStartDate().isBefore(scope.getEndDateInclusive())))) {
                                LOG.debug("Il TU non è pagato");
  //                              if (billingInterval == BillingInterval.PARTIALLY) {
                                    LOG.debug("billingInterval è PARTIALLY");
                                    mesiRimasti = 12 - month;
                                    invoiceAmount = (bl.getInvoiceAmount() / 12.0) * mesiRimasti;
                                    LOG.debug("invoiceAmount è {}:", invoiceAmount);
                                    bl.setInvoiceAmount(invoiceAmount);
                                    billingLedgerService.updateBillingLedgerByIdAndInvoiceAmount(bl.getId(), invoiceAmount);
                                    accountProcessed = true;
  //                              }
                            } else {
                                LOG.debug("Il TU è pagato");
                            }
                        } else {
                            LOG.debug("startDate ha valore {} e endDateInclusive ha valore {}", scope.getStartDate(),
                                scope.getEndDateInclusive());
                        }
                    }
                     */
                }
            }
        }catch (RuntimeException e){
            e.printStackTrace();
            throw e;
        }
        return accountProcessed;
    }

    private Boolean processAccountUTBillingIntervalANNUALLY(UnifiedTaxService unifiedTaxService, AsyncInvoiceGeneratorScope scope, Account account, BillingLedger bl, AircraftRegistrationService aircraftRegistrationService){
   /*
        Double aircraftWeight = 0.0;
        LocalDateTime yearManufacture = null;
        LocalDateTime yearValidity = null;
        String rate = null;
        UnifiedTax ut = null;
        FormulaEvaluatorUT fe = null;
        Double taxAmount = 0.0;
        Double invoiceAmount = 0.0;
        LocalDateTime expiry_date = null;
     */
        Boolean accountProcessed = false;
        LOG.debug("billingInterval è ANNUALLY");
        for (AircraftRegistration ar : account.getAircraftRegistrations()) {
            if ((scope.getStartDate() != null) && (scope.getEndDateInclusive() != null)) {
                if(ar.getCoaExpiryDate() == null && ar.getCoaIssueDate() == null){
                    LOG.debug("Il TU non è pagato");
                    processBillingIntervalANNUALLY(unifiedTaxService, ar, scope, bl, aircraftRegistrationService, accountProcessed);
                }else if(ar.getCoaExpiryDate() != null ^ ar.getCoaIssueDate() !=null){
                    //The A XOR B operation is equivalent to (A AND !B) OR (!A AND B)
                    LOG.error("ar.getCoaExpiryDate() != null ^ ar.getCoaIssueDate() !=null");
                }else if (!((scope.getStartDate().isBefore(ar.getCoaExpiryDate()))
                    && (scope.getStartDate().isAfter(ar.getCoaIssueDate()))
                    && (scope.getEndDateInclusive().isBefore(ar.getCoaExpiryDate()))
                    && (scope.getEndDateInclusive().isAfter(ar.getCoaIssueDate()))
                    && (scope.getStartDate().isBefore(scope.getEndDateInclusive())))) {
                    LOG.debug("Il TU non è pagato");
                    processBillingIntervalANNUALLY(unifiedTaxService, ar, scope, bl, aircraftRegistrationService, accountProcessed);
                } else {
                    LOG.debug("Il TU è pagato");
                }
            } else {
                LOG.debug("startDate ha valore {} e endDateInclusive ha valore {}", scope.getStartDate(),
                    scope.getEndDateInclusive());
            }
        }
        return accountProcessed;
    }

    /**
     *
     * @param unifiedTaxService
     * @param ar
     * @param scope
     * @param bl
     * @param aircraftRegistrationService
     * @param accountProcessed dal momento che è stato passato come riferimento
     */
    private void processBillingIntervalANNUALLY(UnifiedTaxService unifiedTaxService,AircraftRegistration ar, AsyncInvoiceGeneratorScope scope, BillingLedger bl, AircraftRegistrationService aircraftRegistrationService, Boolean accountProcessed){
        Double aircraftWeight = 0.0;
        LocalDateTime yearManufacture = null;
        LocalDateTime yearValidity = null;
        String rate = null;
        UnifiedTax ut = null;
        FormulaEvaluatorUT fe = null;
        Double taxAmount = 0.0;
        Double invoiceAmount = 0.0;
        LocalDateTime expiry_date = null;
        aircraftWeight = ar.getMtowOverride();
        yearManufacture = ar.getAircraftServiceDate();
        if (yearManufacture != null) {
            if (scope.getStartDate().getYear() == scope.getEndDateInclusive().getYear()) {
                LocalDateTime nowYear = LocalDateTime.now();
                // settare anno in base a localdatetime (anno mese giorno ora min sec
                // nanosecondi)
                yearValidity = nowYear.of(scope.getStartDate().getYear(), Month.JANUARY, 1, 0, 0,
                    0);
                try {
                    ut = unifiedTaxService.findUnifiedTaxByValidityYearAndManufactureYear(
                        yearManufacture, yearValidity);

                    rate = ut.getRate();

                    fe = new FormulaEvaluatorUTImpl(new JavascriptEngineUTFactory());
                    try {
                        taxAmount = fe.evalDouble(rate);
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    if (taxAmount != null) {
                        invoiceAmount = (aircraftWeight / 1000) * taxAmount;
                        LOG.debug("invoiceAmount è {} ", invoiceAmount);
                        bl.setInvoiceAmount(invoiceAmount);
                        billingLedgerService.updateBillingLedgerByIdAndInvoiceAmount(bl.getId(),
                            invoiceAmount);
                        expiry_date = bl.getInvoiceDateOfIssue().plusYears(1);
                        LOG.debug(
                            "AircraftRegistration ha id = {}, date of issue = {} e expiry date = {}",
                            ar.getId(), bl.getInvoiceDateOfIssue(), expiry_date);
                        aircraftRegistrationService.updateAircraftRegistrationByIdAndDates(
                            ar.getId(), bl.getInvoiceDateOfIssue(), expiry_date);
                        accountProcessed = true;
                    } else {
                        LOG.debug("invoiceAmount è {} e taxAmount è {} ", invoiceAmount, taxAmount);
                    }
                } catch (Exception e) {
                    LOG.debug("ut ha valore {}", ut);
                }
            } else {
                LOG.debug(
                    "l'anno di startDate ha valore = {}, l'anno di endDateInclusive ha valore = {}",
                    scope.getStartDate().getYear(), scope.getEndDateInclusive().getYear());
            }
        } else {
            LOG.debug(
                "la data yearManufacture ha valore = {}, la data startDate ha valore = {}, la data endDateInclusive ha valore = {}",
                yearManufacture, scope.getStartDate(), scope.getEndDateInclusive());
        }
    }



    private Boolean processAccountUTBillingIntervalPARTIALLY(AsyncInvoiceGeneratorScope scope, Account account,  BillingLedger bl){
        LocalDateTime currentDate = LocalDateTime.now();
        Double invoiceAmount = 0.0;
        Integer mesiRimasti = 0;
        Boolean accountProcessed = false;
        Integer month = currentDate.getMonthValue();
        for (AircraftRegistration ar : account.getAircraftRegistrations()) {
            if(ar.getCoaExpiryDate() == null && ar.getCoaIssueDate() == null){
                LOG.debug("Il TU non è pagato");

                /*Lo stesso di quello sotto*/
                LOG.debug("billingInterval è PARTIALLY");
                mesiRimasti = 12 - month;
                invoiceAmount = (bl.getInvoiceAmount() / 12.0) * mesiRimasti;
                LOG.debug("invoiceAmount è {}:", invoiceAmount);
                bl.setInvoiceAmount(invoiceAmount);
                billingLedgerService.updateBillingLedgerByIdAndInvoiceAmount(bl.getId(), invoiceAmount);
                accountProcessed = true;

            }else if(ar.getCoaExpiryDate() != null ^ ar.getCoaIssueDate() !=null){
                //The A XOR B operation is equivalent to (A AND !B) OR (!A AND B)
                LOG.error("ar.getCoaExpiryDate() != null ^ ar.getCoaIssueDate() !=null");
            }else if ((scope.getStartDate() != null) && (scope.getEndDateInclusive() != null)) {
                if (!((scope.getStartDate().isBefore(ar.getCoaExpiryDate()))
                    && (scope.getStartDate().isAfter(ar.getCoaIssueDate()))
                    && (scope.getEndDateInclusive().isBefore(ar.getCoaExpiryDate()))
                    && (scope.getEndDateInclusive().isAfter(ar.getCoaIssueDate()))
                    && (scope.getStartDate().isBefore(scope.getEndDateInclusive())))) {
                    LOG.debug("Il TU non è pagato");
                    //                              if (billingInterval == BillingInterval.PARTIALLY) {
                    LOG.debug("billingInterval è PARTIALLY");
                    mesiRimasti = 12 - month;
                    invoiceAmount = (bl.getInvoiceAmount() / 12.0) * mesiRimasti;
                    LOG.debug("invoiceAmount è {}:", invoiceAmount);
                    bl.setInvoiceAmount(invoiceAmount);
                    billingLedgerService.updateBillingLedgerByIdAndInvoiceAmount(bl.getId(), invoiceAmount);
                    accountProcessed = true;
                    //                              }
                } else {
                    LOG.debug("Il TU è pagato");
                }
            } else {
                LOG.debug("startDate ha valore {} e endDateInclusive ha valore {}", scope.getStartDate(),
                    scope.getEndDateInclusive());
            }
        }
        return accountProcessed;
    }


/* Alessio
    @Transactional(readOnly = true)
    public Boolean previewAccountUT(AsyncInvoiceGeneratorScope scope,AccountRepository accountRepository, BillingLedgerService billingLedgerService, AircraftRegistrationService aircraftRegistrationService, Account account, UnifiedTaxService unifiedTaxService, List <AviationInvoice> invoiceList,
            FlightmovementCategory flightmovementCategory,User currentUser) {
        Boolean accountProcessed = false;
        LocalDateTime currentDate = LocalDateTime.now();
        LocalDateTime expiry_date = null;
        LocalDateTime yearManufacture = null;
        LocalDateTime yearValidity = null;
        Double taxAmount = 0.0;
       //Integer accountId = account.getId();
        Double aircraftWeight = 0.0;
        Double invoiceAmount = 0.0;
        Integer mesiRimasti = 0;
        Integer month = currentDate.getMonthValue();
        FormulaEvaluatorUT fe = null;
        UnifiedTax ut = null;
        String rate = null;
        BillingInterval billingInterval = scope.getBillingInterval();

        LOG.debug("Sono all'interno del metodo previewAccountUT");
        Set<BillingLedger> bilSet = account.getBillingLedgers();
        // Calcolo fattura
        for (BillingLedger bl : bilSet) {
            if (billingInterval == BillingInterval.ANNUALLY) {
                LOG.debug("billingInterval è ANNUALLY");
                for (AircraftRegistration ar : account.getAircraftRegistrations()) {
                    if ((scope.getStartDate() != null) && (scope.getEndDateInclusive() != null)) {
                        if (!((scope.getStartDate().isBefore(ar.getCoaExpiryDate()))
                                && (scope.getStartDate().isAfter(ar.getCoaIssueDate()))
                                && (scope.getEndDateInclusive().isBefore(ar.getCoaExpiryDate()))
                                && (scope.getEndDateInclusive().isAfter(ar.getCoaIssueDate()))
                                && (scope.getStartDate().isBefore(scope.getEndDateInclusive())))) {
                            LOG.debug("Il TU non è pagato");
                            aircraftWeight = ar.getMtowOverride();
                            yearManufacture = ar.getAircraftServiceDate();
                            if (yearManufacture != null) {
                                if (scope.getStartDate().getYear() == scope.getEndDateInclusive().getYear()) {
                                    LocalDateTime nowYear = LocalDateTime.now();
                                    // settare anno in base a localdatetime (anno mese giorno ora min sec
                                    // nanosecondi)
                                    yearValidity = nowYear.of(scope.getStartDate().getYear(), Month.JANUARY, 1, 0, 0,
                                            0);
                                    try {
                                        ut = unifiedTaxService.findUnifiedTaxByValidityYearAndManufactureYear(
                                                yearManufacture, yearValidity);

                                        rate = ut.getRate();

                                        fe = new FormulaEvaluatorUTImpl(new JavascriptEngineUTFactory());
                                        try {
                                            taxAmount = fe.evalDouble(rate);
                                        } catch (Exception e) {
                                            // TODO Auto-generated catch block
                                            e.printStackTrace();
                                        }
                                        if (taxAmount != null) {
                                            invoiceAmount = (aircraftWeight / 1000) * taxAmount;
                                            LOG.debug("invoiceAmount è {} ", invoiceAmount);
                                            bl.setInvoiceAmount(invoiceAmount);
                                            billingLedgerService.updateBillingLedgerByIdAndInvoiceAmount(bl.getId(),
                                                    invoiceAmount);
                                            expiry_date = bl.getInvoiceDateOfIssue().plusYears(1);
                                            LOG.debug(
                                                    "AircraftRegistration ha id = {}, date of issue = {} e expiry date = {}",
                                                    ar.getId(), bl.getInvoiceDateOfIssue(), expiry_date);
                                            aircraftRegistrationService.updateAircraftRegistrationByIdAndDates(
                                                    ar.getId(), bl.getInvoiceDateOfIssue(), expiry_date);
                                            accountProcessed = true;
                                        } else {
                                            LOG.debug("invoiceAmount è {} e taxAmount è {} ", invoiceAmount, taxAmount);
                                        }
                                    } catch(Exception e) {
                                        LOG.debug("ut ha valore {}", ut);
                                    }
                                } else {
                                    LOG.debug(
                                            "l'anno di startDate ha valore = {}, l'anno di endDateInclusive ha valore = {}",
                                            scope.getStartDate().getYear(), scope.getEndDateInclusive().getYear());
                                }
                            } else {
                                LOG.debug(
                                        "la data yearManufacture ha valore = {}, la data startDate ha valore = {}, la data endDateInclusive ha valore = {}",
                                        yearManufacture, scope.getStartDate(), scope.getEndDateInclusive());
                            }
                        } else {
                            LOG.debug("Il TU è pagato");
                        }
                    } else {
                        LOG.debug("startDate ha valore {} e endDateInclusive ha valore {}", scope.getStartDate(),
                                scope.getEndDateInclusive());
                    }
                }

            } else {
                for (AircraftRegistration ar : account.getAircraftRegistrations()) {
                    if ((scope.getStartDate() != null) && (scope.getEndDateInclusive() != null)) {
                        if (!((scope.getStartDate().isBefore(ar.getCoaExpiryDate()))
                                && (scope.getStartDate().isAfter(ar.getCoaIssueDate()))
                                && (scope.getEndDateInclusive().isBefore(ar.getCoaExpiryDate()))
                                && (scope.getEndDateInclusive().isAfter(ar.getCoaIssueDate()))
                                && (scope.getStartDate().isBefore(scope.getEndDateInclusive())))) {
                            LOG.debug("Il TU non è pagato");
                            if (billingInterval == BillingInterval.PARTIALLY) {
                                LOG.debug("billingInterval è PARTIALLY");
                                mesiRimasti = 12 - month;
                                invoiceAmount = (bl.getInvoiceAmount() / 12.0) * mesiRimasti;
                                LOG.debug("invoiceAmount è {}:", invoiceAmount);
                                bl.setInvoiceAmount(invoiceAmount);
                                billingLedgerService.updateBillingLedgerByIdAndInvoiceAmount(bl.getId(), invoiceAmount);
                                accountProcessed = true;
                            }
                        } else {
                            LOG.debug("Il TU è pagato");
                        }
                    } else {
                        LOG.debug("startDate ha valore {} e endDateInclusive ha valore {}", scope.getStartDate(),
                                scope.getEndDateInclusive());
                    }
                }
            }
        }
        return accountProcessed;
    }
*/

    @Transactional(readOnly = true)
    public Boolean previewAccountUT(AsyncInvoiceGeneratorScope scope,AccountRepository accountRepository, BillingLedgerService billingLedgerService, AircraftRegistrationService aircraftRegistrationService, Account account, UnifiedTaxService unifiedTaxService, List <AviationInvoice> invoiceList,
                                    FlightmovementCategory flightmovementCategory,User currentUser) {
        Boolean accountProcessed = false;
     /*   LocalDateTime currentDate = LocalDateTime.now();
        LocalDateTime expiry_date = null;
        LocalDateTime yearManufacture = null;
        LocalDateTime yearValidity = null;
        Double taxAmount = 0.0;
        //Integer accountId = account.getId();
        Double aircraftWeight = 0.0;
        Double invoiceAmount = 0.0;
        Integer mesiRimasti = 0;
        Integer month = currentDate.getMonthValue();
        FormulaEvaluatorUT fe = null;
        UnifiedTax ut = null;
        String rate = null;
        */
        BillingInterval billingInterval = scope.getBillingInterval();

        LOG.debug("Sono all'interno del metodo previewAccountUT");
        Set<BillingLedger> bilSet = account.getBillingLedgers();
        // Calcolo fattura
        for (BillingLedger bl : bilSet) {
            if (billingInterval == BillingInterval.ANNUALLY) {
                LOG.debug("billingInterval è ANNUALLY");
                processAccountUTBillingIntervalANNUALLY(unifiedTaxService, scope, account, bl, aircraftRegistrationService);

                /*
                for (AircraftRegistration ar : account.getAircraftRegistrations()) {
                    if ((scope.getStartDate() != null) && (scope.getEndDateInclusive() != null)) {
                        if(ar.getCoaExpiryDate() == null && ar.getCoaIssueDate() == null){
                            LOG.debug("Il TU non è pagato");
                            processBillingIntervalANNUALLY(unifiedTaxService, ar, scope, bl, aircraftRegistrationService, accountProcessed);
                        }else if(ar.getCoaExpiryDate() != null ^ ar.getCoaIssueDate() !=null){
                            //The A XOR B operation is equivalent to (A AND !B) OR (!A AND B)
                            LOG.error("ar.getCoaExpiryDate() != null ^ ar.getCoaIssueDate() !=null");
                        }else if (!((scope.getStartDate().isBefore(ar.getCoaExpiryDate()))
                            && (scope.getStartDate().isAfter(ar.getCoaIssueDate()))
                            && (scope.getEndDateInclusive().isBefore(ar.getCoaExpiryDate()))
                            && (scope.getEndDateInclusive().isAfter(ar.getCoaIssueDate()))
                            && (scope.getStartDate().isBefore(scope.getEndDateInclusive())))) {
                            LOG.debug("Il TU non è pagato");
                            processBillingIntervalANNUALLY(unifiedTaxService, ar, scope, bl, aircraftRegistrationService, accountProcessed);
                        } else {
                            LOG.debug("Il TU è pagato");
                        }
                    } else {
                        LOG.debug("startDate ha valore {} e endDateInclusive ha valore {}", scope.getStartDate(),
                            scope.getEndDateInclusive());
                    }
                }
*/
            } else if(billingInterval == BillingInterval.PARTIALLY) {
                LOG.debug("billingInterval è PARTIALLY");
                processAccountUTBillingIntervalPARTIALLY(scope, account, bl);
                /*
                for (AircraftRegistration ar : account.getAircraftRegistrations()) {
                    if(ar.getCoaExpiryDate() == null && ar.getCoaIssueDate() == null){
                        LOG.debug("Il TU non è pagato");
                        //Lo stesso di quello sotto

                        mesiRimasti = 12 - month;
                        invoiceAmount = (bl.getInvoiceAmount() / 12.0) * mesiRimasti;
                        LOG.debug("invoiceAmount è {}:", invoiceAmount);
                        bl.setInvoiceAmount(invoiceAmount);
                        billingLedgerService.updateBillingLedgerByIdAndInvoiceAmount(bl.getId(), invoiceAmount);
                        accountProcessed = true;

                    }else if(ar.getCoaExpiryDate() != null ^ ar.getCoaIssueDate() !=null){
                        //The A XOR B operation is equivalent to (A AND !B) OR (!A AND B)
                        LOG.error("ar.getCoaExpiryDate() != null ^ ar.getCoaIssueDate() !=null");
                    }else if ((scope.getStartDate() != null) && (scope.getEndDateInclusive() != null)) {
                        if (!((scope.getStartDate().isBefore(ar.getCoaExpiryDate()))
                            && (scope.getStartDate().isAfter(ar.getCoaIssueDate()))
                            && (scope.getEndDateInclusive().isBefore(ar.getCoaExpiryDate()))
                            && (scope.getEndDateInclusive().isAfter(ar.getCoaIssueDate()))
                            && (scope.getStartDate().isBefore(scope.getEndDateInclusive())))) {
                            LOG.debug("Il TU non è pagato");
                                LOG.debug("billingInterval è PARTIALLY");
                                mesiRimasti = 12 - month;
                                invoiceAmount = (bl.getInvoiceAmount() / 12.0) * mesiRimasti;
                                LOG.debug("invoiceAmount è {}:", invoiceAmount);
                                bl.setInvoiceAmount(invoiceAmount);
                                billingLedgerService.updateBillingLedgerByIdAndInvoiceAmount(bl.getId(), invoiceAmount);
                                accountProcessed = true;
                        } else {
                            LOG.debug("Il TU è pagato");
                        }
                    } else {
                        LOG.debug("startDate ha valore {} e endDateInclusive ha valore {}", scope.getStartDate(),
                            scope.getEndDateInclusive());
                    }
                }
                */
            }
        }
        return accountProcessed;
    }



    @Transactional()
    public Boolean processAccount(AsyncInvoiceGeneratorScope scope, Integer accountId, List <AviationInvoice> invoiceList,
             FlightmovementCategory flightmovementCategory,User currentUser) {

        Boolean accountProcessed = false;
        if (scope.getPreview()) {
           TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        List<Integer> accountIds = new ArrayList<>();
        accountIds.add(accountId);

        final AviationInvoiceCreator invoiceCreator = this.buildTheInvoiceCreator(scope.getStartDate(), scope.getEndDateInclusive(),
                scope.getBillingInterval(), nvl (scope.getFormat(), AviationInvoiceService.DFLT_FORMAT), scope.getPreview(), currentUser);

        final Map<Account, List<FlightMovement>> accountFlightMap = this.getAccountFlightMap(scope.getFlightCategory(),
                scope.getBillingInterval(), scope.getStartDate(), scope.getEndDateInclusive(),
                scope.getUserBillingCenterOnly(), accountIds, currentUser, scope.getInvoiceProgressCounter());
        // Sort accounts
        final List <Account> sortedAccounts = new ArrayList<>(accountFlightMap.keySet());

        List<FlightMovement> fmList;
        List<Integer> fmIds = new ArrayList<>();
        if(accountFlightMap != null && !accountFlightMap.isEmpty()) {
            fmList = accountFlightMap.get(sortedAccounts.get(0));
            if(fmList != null && !fmList.isEmpty()) {
                for(FlightMovement fm:fmList) {
                    if(fm != null)
                        fmIds.add(fm.getId());
                }
            }
        }
        List<FlightMovement> lockedList = new ArrayList<>();
        if(!fmIds.isEmpty()) {
            lockedList = this.flightMovementRepository.findAllByIdInUpdateSkipLocked(fmIds);
        }

        try {
            if(!accountFlightMap.isEmpty()) {
                this.generateAviationInvoice(sortedAccounts.get(0), invoiceCreator, lockedList, invoiceList, flightmovementCategory,
                currentUser, scope.getIpAddress(), scope.getInvoiceProgressCounter(), scope.getPreview());
                accountProcessed = true;
            } else {
                LOG.debug("Account {} have been discarded because don't have billable flights", accountIds.get(0));
            }
        } catch (Exception e) {
            LOG.error("Cannot generate an invoice for the Account {}: {}", sortedAccounts.get(0).getName(),
                ExceptionFactory.resolveManagedErrors(e));
            throw e;
        }
        return accountProcessed;
    }

    @Transactional(readOnly = true)
    public Boolean previewAccount(AsyncInvoiceGeneratorScope scope, Integer accountId, List <AviationInvoice> invoiceList,
             FlightmovementCategory flightmovementCategory,User currentUser) {

        boolean accountProcessed = false;
        List<Integer> accountIds = new ArrayList<>();
        accountIds.add(accountId);

        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

        final AviationInvoiceCreator invoiceCreator = this.buildTheInvoiceCreator(scope.getStartDate(), scope.getEndDateInclusive(),
                scope.getBillingInterval(), nvl (scope.getFormat(), AviationInvoiceService.DFLT_FORMAT), scope.getPreview(), currentUser);

        final Map<Account, List<FlightMovement>> accountFlightMap = this.getAccountFlightMap(scope.getFlightCategory(),
                scope.getBillingInterval(), scope.getStartDate(), scope.getEndDateInclusive(),
                scope.getUserBillingCenterOnly(), accountIds, currentUser, scope.getInvoiceProgressCounter());
            // Sort accounts
            final List <Account> sortedAccounts = new ArrayList<>(accountFlightMap.keySet());

        try {
            if(!accountFlightMap.isEmpty()) {
                this.generateAviationInvoice(sortedAccounts.get(0), invoiceCreator, accountFlightMap.get(sortedAccounts.get(0)), invoiceList, flightmovementCategory,
                currentUser, scope.getIpAddress(), scope.getInvoiceProgressCounter(), scope.getPreview());
                accountProcessed = true;
            } else {
                LOG.debug("Account {} have been discarded because don't have billable flights", accountIds.get(0));
            }
        } catch (Exception e) {
            LOG.error("Cannot generate an invoice for the Account {}: {}", sortedAccounts.get(0).getName(),
                ExceptionFactory.resolveManagedErrors(e));
            throw e;
        }
        return accountProcessed;
    }
    public InvoiceStateType getInitialLedgerState(Boolean approvalWorkflow) {
        return reportHelper.getInitialLedgerState(approvalWorkflow);
    }
}
