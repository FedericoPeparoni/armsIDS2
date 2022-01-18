package ca.ids.abms.modules.reports2.invoices.nonaviation;

import static ca.ids.abms.util.MiscUtils.nvl;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import ca.ids.abms.config.error.CustomParametrizedException;
import ca.ids.abms.modules.accounts.AccountExternalChargeCategoryService;
import ca.ids.abms.modules.bankcode.BankCodeService;
import ca.ids.abms.modules.common.services.AbstractPluginService;
import ca.ids.abms.modules.currencies.CurrencyService;
import ca.ids.abms.plugins.kcaa.aatis.modules.permitnumber.KcaaAatisPermitNumber;
import ca.ids.abms.plugins.kcaa.eaip.modules.requisitionnumbers.KcaaEaipRequisitionNumber;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.ids.abms.modules.accounts.Account;
import ca.ids.abms.modules.aerodromes.AerodromeService;
import ca.ids.abms.modules.billingcenters.BillingCenter;
import ca.ids.abms.modules.billings.BillingLedgerService;
import ca.ids.abms.modules.billings.InvoiceLineItemMapper;
import ca.ids.abms.modules.billings.InvoiceLineItemViewModel;
import ca.ids.abms.modules.charges.RecurringCharge;
import ca.ids.abms.modules.charges.RecurringChargeMapper;
import ca.ids.abms.modules.charges.RecurringChargeService;
import ca.ids.abms.modules.charges.ServiceChargeCatalogue;
import ca.ids.abms.modules.charges.ServiceChargeCatalogueMapper;
import ca.ids.abms.modules.charges.ServiceChargeCatalogueService;
import ca.ids.abms.modules.charges.ServiceChargeCatalogueViewModel;
import ca.ids.abms.modules.common.enumerators.InvoiceStateType;
import ca.ids.abms.modules.reports2.common.InvoicePaymentParameters;
import ca.ids.abms.modules.reports2.common.ReportDocument;
import ca.ids.abms.modules.reports2.common.ReportDocumentCreator;
import ca.ids.abms.modules.reports2.common.ReportFormat;
import ca.ids.abms.modules.reports2.common.ReportHelper;
import ca.ids.abms.modules.transactions.TransactionService;
import ca.ids.abms.modules.translation.Translation;
import ca.ids.abms.modules.usereventlogs.UserEventLogService;
import ca.ids.abms.modules.util.models.CurrencyUtils;
import ca.ids.abms.modules.utilities.invoices.InvoiceSequenceNumberHelper;
import ca.ids.abms.modules.utilities.towns.UtilitiesTownsAndVillageService;
import ca.ids.abms.modules.reports2.common.RoundingUtils;
import ca.ids.abms.modules.reports2.invoices.aviation.charges.AviationInvoiceChargeProvider;
import ca.ids.abms.modules.system.SystemConfigurationService;

/**
 * Create non-aviation invoices
 */
@Service
public class NonAviationInvoiceService extends AbstractPluginService<NonAviationInvoiceServiceProvider> {

    private static final Logger LOG = LoggerFactory.getLogger (NonAviationInvoiceService.class);
    private static final ReportFormat DFLT_FORMAT = ReportFormat.pdf;

    private final ReportHelper reportHelper;
    private final ReportDocumentCreator reportDocumentCreator;
    private final BillingLedgerService billingLedgerService;
    private final NonAviationInvoiceDocumentCreator nonAviationInvoiceDocumentCreator;
    private final InvoiceSequenceNumberHelper invoiceSequenceNumberHelper;
    private final TransactionService transactionService;
    private final ServiceChargeCatalogueService serviceChargeCatalogueService;
    private final ServiceChargeCatalogueMapper serviceChargeCatalogueMapper;
    private final AerodromeService aerodromeService;
    private final UtilitiesTownsAndVillageService utilitiesTownsAndVillageService;
    private final InvoiceLineItemMapper invoiceLineItemMapper;
    private final RecurringChargeService recurringChargeService;
    private final RecurringChargeMapper recurringChargeMapper;
    private final CurrencyUtils currencyUtils;
    private final UserEventLogService userEventLogService;
    private final CurrencyService currencyService;
    private final RoundingUtils roundingUtils;
    private final SystemConfigurationService systemConfigurationService;
    private final AccountExternalChargeCategoryService accountExternalChargeCategoryService;
    private final BankCodeService bankCodeService;
    private final List<AviationInvoiceChargeProvider> aviationInvoiceChargeProviders;

    @SuppressWarnings("squid:S00107")
    public NonAviationInvoiceService(
        final ReportHelper reportHelper,
        final ReportDocumentCreator reportDocumentCreator,
        final BillingLedgerService billingLedgerService,
        final NonAviationInvoiceDocumentCreator nonAviationInvoiceDocumentCreator,
        final InvoiceSequenceNumberHelper invoiceSequenceNumberHelper,
        final TransactionService transactionService,
        final ServiceChargeCatalogueService serviceChargeCatalogueService,
        final ServiceChargeCatalogueMapper serviceChargeCatalogueMapper,
        final UtilitiesTownsAndVillageService utilitiesTownsAndVillageService,
        final InvoiceLineItemMapper invoiceLineItemMapper,
        final AerodromeService aerodromeService,
        final RecurringChargeService recurringChargeService,
        final RecurringChargeMapper recurringChargeMapper,
        final CurrencyUtils currencyUtils,
        final UserEventLogService userEventLogService,
        final CurrencyService currencyService,
        final RoundingUtils roundingUtils,
        final SystemConfigurationService systemConfigurationService,
        final AccountExternalChargeCategoryService accountExternalChargeCategoryService,
        final BankCodeService bankCodeService,
        final List<AviationInvoiceChargeProvider> aviationInvoiceChargeProviders
    ) {
        this.reportHelper = reportHelper;
        this.reportDocumentCreator = reportDocumentCreator;
        this.billingLedgerService = billingLedgerService;
        this.nonAviationInvoiceDocumentCreator = nonAviationInvoiceDocumentCreator;
        this.invoiceSequenceNumberHelper = invoiceSequenceNumberHelper;
        this.transactionService = transactionService;
        this.serviceChargeCatalogueService = serviceChargeCatalogueService;
        this.serviceChargeCatalogueMapper = serviceChargeCatalogueMapper;
        this.utilitiesTownsAndVillageService = utilitiesTownsAndVillageService;
        this.invoiceLineItemMapper = invoiceLineItemMapper;
        this.aerodromeService = aerodromeService;
        this.recurringChargeService = recurringChargeService;
        this.recurringChargeMapper = recurringChargeMapper;
        this.currencyUtils = currencyUtils;
        this.userEventLogService = userEventLogService;
        this.currencyService = currencyService;
        this.roundingUtils = roundingUtils;
        this.systemConfigurationService = systemConfigurationService;
        this.accountExternalChargeCategoryService = accountExternalChargeCategoryService;
        this.bankCodeService = bankCodeService;
        this.aviationInvoiceChargeProviders = aviationInvoiceChargeProviders;
    }

    /**
     * Create a monthly non-aviation invoice
     */
    @Transactional
    public ReportDocument createMonthlyInvoice (
            final Integer year,
            final Integer month,
            ReportFormat format,
            final Integer accountId,
            final List <InvoiceLineItemViewModel> lineItemViewModelList,
            boolean preview,
            String ipAddress) {
        final ZonedDateTime zdtNow = ZonedDateTime.now (ReportHelper.UTC_ZONE_ID);
        final ZonedDateTime zdtStart = ZonedDateTime.of(year, month, 1, 0, 0, 0, 0, ReportHelper.UTC_ZONE_ID);
        final ZonedDateTime zdtEnd = zdtStart.plusMonths(1);
        final LocalDateTime ldtStart = zdtStart.toLocalDateTime();
        final LocalDateTime ldtEnd = zdtEnd.toLocalDateTime();
        final LocalDateTime ldtNow = zdtNow.toLocalDateTime();
        final LocalDateTime endDateInclusive = zdtEnd.minusDays(1).toLocalDateTime();
        final int monthValue = month == null ? zdtNow.getMonthValue() : month;
        final int yearValue = year == null ? zdtNow.getYear() : year;
        final BillingCenter billingCenter = reportHelper.getBillingCenterOfCurrentUser();
        final ReportFormat reportFormat = nvl (format, DFLT_FORMAT);
        final Account account = this.reportHelper.getAccount (accountId);

        LOG.info ("Creating monthly non-aviation invoice for billingCenter={}/{}, billing period = {}-{}, account={}/{}",
                billingCenter.getId(),
                billingCenter.getName(),
                String.format("%04d", yearValue),
                String.format("%02d", monthValue),
                account.getId(), account.getName());

        final NonAviationInvoiceCreator invoiceCreator = monthlyInvoiceCreator(ldtNow, endDateInclusive, preview, true);

        final NonAviationInvoice invoice = invoiceCreator.createMonthlyInvoice (
            account, ldtStart, ldtEnd, lineItemViewModelList, reportFormat);

        LOG.info ("Created monthly non-aviation invoice document \"{}\" for billing center {}/{}, account={}/{}, file length={} byte(s)",
                invoice.invoiceDocument().fileName(),
                billingCenter.getId(),
                billingCenter.getName(),
                account.getId(),
                account.getName(),
                invoice.invoiceDocument().contentLength());

        // create user event log for monthly non-aviation
        if (!preview) {
            try {
                userEventLogService.createInvoiceUserEventLog(ipAddress, invoice.billingLedger().getId().toString());
            } catch (Exception e) {
                LOG.error("Unexpected exception: {}", e.getMessage(), e);
            }
        }
        if (preview || InvoiceStateType.PUBLISHED.equals(invoiceCreator.getInitialInvoiceStateType())) {
            return invoice.invoiceDocument();
        } else {
            return null;
        }
    }
    /**
     * Validate a single monthly line item
     */
    InvoiceLineItemViewModel validateMonthlyLineItem (final Integer accountId, final Integer year, final Integer month, final InvoiceLineItemViewModel lineItem) {
        final ZonedDateTime zdtNow = ZonedDateTime.now (ReportHelper.UTC_ZONE_ID);
        final LocalDateTime ldtNow = zdtNow.toLocalDateTime();
        final ZonedDateTime zdtStart = ZonedDateTime.of(year, month, 1, 0, 0, 0, 0, ReportHelper.UTC_ZONE_ID);
        final ZonedDateTime zdtEnd = zdtStart.plusMonths(1);
        final LocalDateTime ldtStart = zdtStart.toLocalDateTime();
        final LocalDateTime ldtEnd = zdtEnd.toLocalDateTime();
        final LocalDateTime endDateInclusive = zdtEnd.minusDays(1).toLocalDateTime();
        final Account account = reportHelper.getAccount (accountId);
        return monthlyInvoiceCreator(ldtNow, endDateInclusive, true, false)
            .validateMonthlyLineItem (account, ldtStart, ldtEnd, lineItem);
    }

    /**
     * Prepare the list of line items for a monthly non-aviation invoice. Users have to fill
     * out unit amounts, etc. before submitting to createMonthlyInvoice, etc.
     */
    List <InvoiceLineItemViewModel> prepareMonthlyLineItems (final Integer accountId,
                                                             final Integer year,
                                                             final Integer month,
                                                             final Integer externalChargeCategoryId) {
        final ZonedDateTime zdtNow = ZonedDateTime.now (ReportHelper.UTC_ZONE_ID);
        final int monthValue = month == null ? zdtNow.getMonthValue() : month;
        final int yearValue = year == null ? zdtNow.getYear() : year;
        final ZonedDateTime zdtStart = ZonedDateTime.of(yearValue, monthValue, 1, 0, 0, 0, 0, ReportHelper.UTC_ZONE_ID);
        final ZonedDateTime zdtEnd = zdtStart.plusMonths (1);
        final LocalDateTime ldtStart = zdtStart.toLocalDateTime();
        final LocalDateTime ldtEnd = zdtEnd.toLocalDateTime();

        // Find recurring charges for the account, period and invoice category, that
        // haven't yet been invoiced this month.
        final List <RecurringCharge> list = recurringChargeService.findChargesNotIncludedInAccountInvoiceForPeriod (
                accountId,
                ldtStart,
                ldtEnd,
                externalChargeCategoryId,
                NonAviationInvoiceCreator.MONTHLY_INVOICE_CATEGORIES);

        // Convert them to view models
        return list.stream()
                .filter (rc->rc.getServiceChargeCatalogue() != null)
                .map (rc->{
                    final InvoiceLineItemViewModel lineItemViewModel = new InvoiceLineItemViewModel();
                    lineItemViewModel.setServiceChargeCatalogue (this.serviceChargeCatalogueMapper.toViewModel (rc.getServiceChargeCatalogue()));
                    lineItemViewModel.setRecurringCharge(this.recurringChargeMapper.toViewModel(rc));
                    lineItemViewModel.getRecurringCharge().setServiceChargeCatalogue (null);
                    lineItemViewModel.getRecurringCharge().setAccount(null);
                    return lineItemViewModel;
                })
                .collect (Collectors.toList());
    }


    /**
     * Create a POS non-aviation invoice
     */
    @SuppressWarnings("squid:S00107")
    ReportDocument createPosInvoice (ReportFormat format,
                                            final Integer accountId,
                                            final String invoiceCurrency,
                                            final List <InvoiceLineItemViewModel> lineItemViewModelList,
                                            final InvoicePaymentParameters payment,
                                            boolean preview,
                                            String ipAddress,
                                            final List<KcaaAatisPermitNumber> permitNumbers,
                                            final List<KcaaEaipRequisitionNumber> requisitionNumbers,
                                            boolean proforma) {

        final ZonedDateTime zdtNow = ZonedDateTime.now (ReportHelper.UTC_ZONE_ID);
        final LocalDateTime ldtNow = zdtNow.toLocalDateTime();
        final BillingCenter billingCenter = reportHelper.getBillingCenterOfCurrentUser();
        final ReportFormat reportFormat = nvl (format, DFLT_FORMAT);
        final Account account = reportHelper.getAccount (accountId);

        do_ensureItemListNotEmpty(lineItemViewModelList);

        LOG.info("Creating POS aviation invoice for billingCenter={}/{}, account={}/{}",
                billingCenter.getId(),
                billingCenter.getName(),
                account.getId(),
                account.getName());

        // validate point-of-sale line items and resolve invoice currency code via providers
        String invoiceCurrencyCode = validatePosInvoiceAndPreferredInvoiceCurrencyCode(account, lineItemViewModelList, invoiceCurrency);

        // Create the invoice
        final NonAviationInvoiceCreator invoiceCreator = this.posInvoiceCreator(ldtNow, preview);
        final NonAviationInvoice invoice = invoiceCreator.createPosInvoice(account, lineItemViewModelList, reportFormat,
            payment, permitNumbers, requisitionNumbers, invoiceCurrencyCode, proforma);

        // Create payment if necessary
        if (payment != null) {
            if (reportFormat != null && !reportFormat.equals(ReportFormat.pdf)) {
                throw new UnsupportedOperationException("This report doesn't support output format" + "\"" + reportFormat.toString() + "\"");
            }
            invoiceCreator.createPayment(invoice, payment);
            final String bundleName =  String.format (
                Translation.getLangByToken("Non-aviation invoice") +
                " %s " +
                Translation.getLangByToken("with receipt"), invoice.invoiceData().global.invoiceNumber);
            ReportDocument combinedDoc;
            if (ReportFormat.pdf.equals(reportFormat)) {
                combinedDoc = reportDocumentCreator.combinePdfFiles(bundleName,
                    Arrays.asList(invoice.invoiceDocument(), invoice.transactionDocument()), preview);
            } else {
                combinedDoc = invoice.invoiceDocument();
            }
            LOG.debug("Created combined non-aviation POS invoice + receipt document \"{}\" for billing center {}/{}, file length={} byte(s)",
                    combinedDoc.fileName(),
                    billingCenter.getId(),
                    billingCenter.getName(),
                    combinedDoc.contentLength());
            return combinedDoc;

        }

        // create user event log for pos
        if (!preview) {
            try {
                userEventLogService.createInvoiceUserEventLog(ipAddress, invoice.billingLedger().getId().toString());
            } catch (Exception e) {
                LOG.error("Unexpected exception: {}", e.getMessage(), e);
            }
        }
        if (preview || InvoiceStateType.PUBLISHED.equals(invoiceCreator.getInitialInvoiceStateType())) {
            return invoice.invoiceDocument();
        } else {
            return null;
        }
    }


    /**
     * Validate a single POS line item
     */
    InvoiceLineItemViewModel validatePosLineItem(final Integer accountId, final InvoiceLineItemViewModel lineItem, final String selectedInvoiceCurrency) {
        ZonedDateTime zdtNow = ZonedDateTime.now (ReportHelper.UTC_ZONE_ID);
        LocalDateTime ldtNow = zdtNow.toLocalDateTime();
        Account account = reportHelper.getAccount (accountId);
        String invoiceCurrencyCode = validatePosInvoiceAndPreferredInvoiceCurrencyCode(account, Collections.singletonList(lineItem), selectedInvoiceCurrency);
        return posInvoiceCreator(ldtNow, true)
            .validatePosLineItem(account, lineItem, invoiceCurrencyCode);
    }

    /**
     * Return the service charges applicable for the POS invoice
     */
    List<ServiceChargeCatalogueViewModel> getApplicablePosServiceCharges() {
        final List <ServiceChargeCatalogue> entityList = serviceChargeCatalogueService.findChargesForCategories (NonAviationInvoiceCreator.POS_INVOICE_CATEGORIES);
        return this.serviceChargeCatalogueMapper.toViewModel(entityList);
    }

    // ------------------------ private -----------------------------


    /**
     * Make sure line item list is not empty; throw an exception otherwise
     */
    private void do_ensureItemListNotEmpty(List<InvoiceLineItemViewModel> lineItemViewModelList) {
        if (lineItemViewModelList == null || lineItemViewModelList.isEmpty()) {
            throw new CustomParametrizedException("No line items defined");
        }
    }

    /**
     * Construct a new creator object
     */
    private NonAviationInvoiceCreator monthlyInvoiceCreator(final LocalDateTime ldtNow, final LocalDateTime endDateInclusive,
                                                        final boolean preview, boolean approvalWorkflow) {
        return new NonAviationInvoiceCreator (
            reportHelper,
            billingLedgerService,
            nonAviationInvoiceDocumentCreator,
            transactionService,
            invoiceSequenceNumberHelper,
            serviceChargeCatalogueService,
            recurringChargeService,
            utilitiesTownsAndVillageService,
            invoiceLineItemMapper,
            aerodromeService,
            ldtNow,
            preview,
            currencyUtils,
            approvalWorkflow,
            currencyService,
            endDateInclusive,
            roundingUtils,
            systemConfigurationService,
            accountExternalChargeCategoryService,
            bankCodeService,
            aviationInvoiceChargeProviders
        );
    }

    /**
     * Construct a new creator object
     */
    private NonAviationInvoiceCreator posInvoiceCreator(final LocalDateTime ldtNow, final boolean preview) {
        // use ldtNow for endDateInclusive because this method is called only from point-of-sale which uses issue date
        return monthlyInvoiceCreator(ldtNow, ldtNow, preview, false);
    }

    /**
     * Validate point-of-sale invoice line items and return invoice currency based on providers. Default
     * invoice currency to account invoice currency if no preferred currency returned.
     */
    private String validatePosInvoiceAndPreferredInvoiceCurrencyCode(final Account account,
                                                                     final List<InvoiceLineItemViewModel> invoiceLineItems,
                                                                     final String selectedInvoiceCurrencyCode) {

        String invoiceCurrencyCode;

        if (selectedInvoiceCurrencyCode != null && currencyService.findByCurrencyCode(selectedInvoiceCurrencyCode) != null) {
            invoiceCurrencyCode = selectedInvoiceCurrencyCode;
        } else {
            invoiceCurrencyCode = account.getInvoiceCurrency().getCurrencyCode();
        }


        // validate point-of-sale line items and resolve invoice currency code via providers
        for (NonAviationInvoiceServiceProvider provider : super.getPluginServiceProviders()) {
            provider.validatePosInvoice(account, invoiceLineItems);
            String preferredCurrencyCode = provider.preferredPosInvoiceCurrencyCode(account, invoiceLineItems);
            if (StringUtils.isNotBlank(preferredCurrencyCode)) {
                invoiceCurrencyCode = preferredCurrencyCode;
            }
        }

        return invoiceCurrencyCode;
    }
}
