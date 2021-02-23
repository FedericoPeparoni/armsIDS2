package ca.ids.abms.modules.reports2.invoices;

import ca.ids.abms.modules.bankcode.BankCode;
import ca.ids.abms.modules.bankcode.BankCodeService;
import ca.ids.abms.modules.billingcenters.BillingCenter;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import ca.ids.abms.modules.flightmovements.FlightMovement;
import ca.ids.abms.modules.reports2.invoices.aviation.AviationInvoiceData;
import ca.ids.abms.modules.reports2.invoices.aviation.charges.AviationInvoiceChargeHelper;
import ca.ids.abms.modules.reports2.invoices.aviation.charges.AviationInvoiceChargeProvider;

import ca.ids.abms.modules.billings.BillingLedger;
import ca.ids.abms.modules.billings.BillingLedgerService;
import ca.ids.abms.modules.billings.InvoiceOverduePenalty;
import ca.ids.abms.modules.currencies.Currency;
import ca.ids.abms.modules.reports2.common.CachedCurrencyConverter;
import ca.ids.abms.modules.reports2.common.ReportHelper;
import ca.ids.abms.modules.system.SystemConfigurationService;
import ca.ids.abms.modules.system.summary.SystemConfigurationItemName;
import ca.ids.abms.modules.transactions.Transaction;
import ca.ids.abms.modules.transactions.TransactionPayment;
import ca.ids.abms.modules.transactions.TransactionService;
import ca.ids.abms.modules.translation.Translation;

public class InvoiceReportUtility {

    private final List<AviationInvoiceChargeProvider> aviationInvoiceChargeProviders;
    private final BillingLedgerService billingLedgerService;
    private final ReportHelper reportHelper;
    private final SystemConfigurationService systemConfigurationService;
    private final TransactionService transactionService;
    private final CachedCurrencyConverter cachedCurrencyConverter;
    private final BankCodeService bankCodeService;
    private final Currency anspCurrency;
    private final DateTimeFormatter dateFormatter;

    public InvoiceReportUtility(
        final ReportHelper reportHelper,
        final BillingLedgerService billingLedgerService,
        final TransactionService transactionService,
        final SystemConfigurationService systemConfigurationService,
        final CachedCurrencyConverter cachedCurrencyConverter,
        final BankCodeService bankCodeService,
        final List<AviationInvoiceChargeProvider> aviationInvoiceChargeProviders
    ) {
        this.reportHelper = reportHelper;
        this.billingLedgerService = billingLedgerService;
        this.transactionService = transactionService;
        this.systemConfigurationService = systemConfigurationService;
        this.cachedCurrencyConverter = cachedCurrencyConverter;
        this.bankCodeService = bankCodeService;
        this.aviationInvoiceChargeProviders = aviationInvoiceChargeProviders;
        this.anspCurrency = cachedCurrencyConverter.getAnspCurrency();
        this.dateFormatter = reportHelper.getDateFormat();
    }

    /**
     * Find all account credits applied.
     */
    public List<AccountCredit> getAppliedAccountCreditAsAdditionalChargeList(final BillingLedger billingLedger) {
        List<TransactionPayment> accountCreditPayments = billingLedgerService.getAllAccountCreditPaymentsForInvoice(billingLedger.getId());
        Currency currency = billingLedger.getInvoiceCurrency();
        List<AccountCredit> result = null;

        if (accountCreditPayments != null && !accountCreditPayments.isEmpty()) {
            result = new ArrayList<>(accountCreditPayments.size());

            for (TransactionPayment accountCreditPayment : accountCreditPayments) {
                AccountCredit accountCreditObj = new AccountCredit();

                Transaction transactionFromDb = transactionService.getOne(accountCreditPayment.getTransaction().getId());
                String receiptNumberStr = transactionFromDb.getReceiptNumber() != null ? " (" + Translation.getLangByToken("transaction receipt") + " #" + transactionFromDb.getReceiptNumber() + ")" : "";

                String transText = Translation.getLangByToken("Account credit");
                accountCreditObj.description = String.format("%s - %s", transText, receiptNumberStr);

                accountCreditObj.amount = accountCreditPayment.getAmount();
                accountCreditObj.amountStr = reportHelper.formatCurrency(accountCreditObj.amount, currency);
                accountCreditObj.amountStrWithCurrencySymbol = reportHelper.formatCurrencyWithSymbol(accountCreditObj.amount, currency);

                accountCreditObj.amountAnsp = cachedCurrencyConverter.toANSPCurrency(accountCreditPayment.getAmount(), currency);
                accountCreditObj.amountAnspStr = reportHelper.formatCurrency(accountCreditObj.amountAnsp, anspCurrency);

                result.add(accountCreditObj);
            }
        }
        return result != null ? result : new ArrayList<>();
    }

    /**
     * Find all invoice overdue penalties applied for report.
     */
    public List<AdditionalCharge> getAppliedPenaltyAsAdditionalChargeList(final BillingLedger billingLedger) {
        Boolean toList = systemConfigurationService.getBoolean(SystemConfigurationItemName.INDIVIDUAL_OUTSTANDING_INVOICES);
        List<AdditionalCharge> result = null;

        final Currency currency = billingLedger.getInvoiceCurrency();

        List<InvoiceOverduePenalty> penaltyList = billingLedgerService.getAllPenaltiesAppliedToInvoice(billingLedger.getId());
        if (penaltyList != null && !penaltyList.isEmpty()) {

            result = new ArrayList<>(penaltyList.size());

            if (toList) {

                for (InvoiceOverduePenalty penalty : penaltyList) {
                    final AdditionalCharge additionalChargeObj = new AdditionalCharge();
                    final String transText = Translation.getLangByToken("Penalty for overdue invoice");
                    additionalChargeObj.description = String.format("%s - #%s", transText, penalty.getPenalizedInvoice().getInvoiceNumber());

                    additionalChargeObj.amount = penalty.getDefaultPenaltyAmount() + penalty.getPunitivePenaltyAmount();
                    additionalChargeObj.amountStr = reportHelper.formatCurrency(additionalChargeObj.amount, currency);
                    additionalChargeObj.amountStrWithCurrencySymbol = reportHelper.formatCurrencyWithSymbol(penalty.getDefaultPenaltyAmount() + penalty.getPunitivePenaltyAmount(), currency);

                    additionalChargeObj.amountAnsp = cachedCurrencyConverter.toANSPCurrency(penalty.getDefaultPenaltyAmount() + penalty.getPunitivePenaltyAmount(), currency);
                    additionalChargeObj.amountAnspStr = reportHelper.formatCurrency(additionalChargeObj.amountAnsp, anspCurrency);

                    result.add(additionalChargeObj);
                }
            } else {
                AdditionalCharge additionalChargeObj = new AdditionalCharge();
                double amount = 0.0d;

                additionalChargeObj.description = Translation.getLangByToken("Overdue invoices");

                for (InvoiceOverduePenalty penalty : penaltyList) {
                    amount += penalty.getDefaultPenaltyAmount() + penalty.getPunitivePenaltyAmount();
                }

                additionalChargeObj.amount = amount;
                additionalChargeObj.amountStr = reportHelper.formatCurrency(additionalChargeObj.amount, currency);
                additionalChargeObj.amountStrWithCurrencySymbol = reportHelper.formatCurrencyWithSymbol(additionalChargeObj.amount, currency);

                additionalChargeObj.amountAnsp = cachedCurrencyConverter.toANSPCurrency(amount, currency);
                additionalChargeObj.amountAnspStr = reportHelper.formatCurrency(additionalChargeObj.amountAnsp, anspCurrency);

                result.add(additionalChargeObj);
            }
        }
        return result != null ? result : new ArrayList<>();
    }

    /**
     * Get a list of bank accounts for billing ledger
     */
    public List<BankAccount> getBankAccountList(final BillingCenter billingCenter) {
        List<BankAccount> result = new ArrayList<>();

        List<BankCode> bankCodes = null;
        if (billingCenter != null)
            bankCodes = bankCodeService.findAllBy(billingCenter);

        if (bankCodes == null)
            return result;

        for (BankCode bankCode : bankCodes) {
            final BankAccount bankAccount = new BankAccount();
            bankAccount.accountNumber = bankCode.getAccountNumber();
            bankAccount.branchCode = bankCode.getBranchCode();
            bankAccount.currencyCode = bankCode.getCurrency().getCurrencyCode();
            result.add(bankAccount);
        }

        return result;
    }

    /**
     * Get a list of penalized invoices for report.
     */
    public List<OverduePenaltyInvoice> getOverduePenaltyInvoiceList(final BillingLedger billingLedger) {
        Boolean toList = systemConfigurationService.getBoolean(SystemConfigurationItemName.INDIVIDUAL_OUTSTANDING_INVOICES);
        List<OverduePenaltyInvoice> result = null;

        if (toList) {
            final Currency currency = billingLedger.getInvoiceCurrency();

            List<InvoiceOverduePenalty> penaltyList = billingLedgerService.getAllPenaltiesAppliedToInvoice(billingLedger.getId());

            if (penaltyList != null && !penaltyList.isEmpty()) {

                result = new ArrayList<>(penaltyList.size());

                for (final InvoiceOverduePenalty penalty : penaltyList) {
                    OverduePenaltyInvoice overduePenaltyInvoiceObj = new OverduePenaltyInvoice();

                    BillingLedger penalizedInvoiceFromDb = billingLedgerService.getOne(penalty.getPenalizedInvoice().getId());

                    overduePenaltyInvoiceObj.amountOwing = penalizedInvoiceFromDb.getAmountOwing();
                    overduePenaltyInvoiceObj.amountOwingStr = reportHelper.formatCurrency(overduePenaltyInvoiceObj.amountOwing, currency);
                    overduePenaltyInvoiceObj.amountOwingStrWithCurrencySymbol = reportHelper.formatCurrencyWithSymbol(overduePenaltyInvoiceObj.amountOwing, currency);

                    overduePenaltyInvoiceObj.amountOwingAnsp = cachedCurrencyConverter.toANSPCurrency(penalizedInvoiceFromDb.getAmountOwing(), currency);
                    overduePenaltyInvoiceObj.amountOwingAnspStr = reportHelper.formatCurrency(overduePenaltyInvoiceObj.amountOwingAnsp, anspCurrency);

                    overduePenaltyInvoiceObj.invoiceDateOfIssueStr = penalizedInvoiceFromDb.getInvoiceDateOfIssue().format(dateFormatter);
                    overduePenaltyInvoiceObj.invoiceNumber = penalizedInvoiceFromDb.getInvoiceNumber();
                    overduePenaltyInvoiceObj.paymentDueDateStr = penalizedInvoiceFromDb.getPaymentDueDate().format(dateFormatter);

                    result.add(overduePenaltyInvoiceObj);
                }
            }
        }

        return result != null ? result : new ArrayList<>();
    }

    /**
     * Apply aviation invoice charge provider specific values to aviation invoice additional charge data.
     *
     * @param flightMovements flight movements to apply
     * @param invoiceData aviation invoice data
     * @param aviationInvoiceChargeHelper aviation invoice charge helper
     * @return additional charge items
     */
    public List<AdditionalCharge> processAdditionalCharges(
        final List<FlightMovement> flightMovements, final AviationInvoiceData invoiceData,
        final AviationInvoiceChargeHelper aviationInvoiceChargeHelper
    ) {
        List<AdditionalCharge> additionalCharges = new ArrayList<>();
        for (AviationInvoiceChargeProvider aviationInvoiceChargeProvider : aviationInvoiceChargeProviders) {
            additionalCharges.addAll(aviationInvoiceChargeProvider.processAdditionalCharges(
                flightMovements, invoiceData, aviationInvoiceChargeHelper));
        }
        return additionalCharges;
    }

    public List<InvoiceOverduePenalty> getOverduePenaltyByOverdueInvoiceId(Integer overdueInvoiceId) {
        return billingLedgerService.getAllPenaltiesAppliedToInvoice(overdueInvoiceId);

    }
}
