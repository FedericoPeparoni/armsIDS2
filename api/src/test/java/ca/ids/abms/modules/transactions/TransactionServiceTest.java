package ca.ids.abms.modules.transactions;

import ca.ids.abms.modules.accounts.AccountService;
import ca.ids.abms.modules.bankcode.BankCodeService;
import ca.ids.abms.modules.billingcenters.BillingCenter;
import ca.ids.abms.modules.billings.*;
import ca.ids.abms.modules.accounts.Account;
import ca.ids.abms.modules.accounts.AccountRepository;
import ca.ids.abms.modules.chargesadjustment.ChargesAdjustmentService;
import ca.ids.abms.modules.common.enumerators.InvoiceStateType;
import ca.ids.abms.modules.countries.Country;
import ca.ids.abms.modules.countries.CountryRepository;
import ca.ids.abms.modules.currencies.*;
import ca.ids.abms.modules.flightmovements.FlightMovementRepository;
import ca.ids.abms.modules.flightmovements.FlightMovementRepositoryUtility;
import ca.ids.abms.modules.flightmovementsbuilder.FlightMovementBuilder;
import ca.ids.abms.modules.interestrates.InterestRateService;
import ca.ids.abms.modules.overdueinvoices.OverdueInvoiceService;
import ca.ids.abms.modules.pendingtransactions.PendingTransactionMapper;
import ca.ids.abms.modules.pendingtransactions.PendingTransactionRepository;
import ca.ids.abms.modules.reports2.common.ReportDocument;
import ca.ids.abms.modules.reports2.common.ReportFormat;
import ca.ids.abms.modules.reports2.common.ReportHelper;
import ca.ids.abms.modules.reports2.common.RoundingUtils;
import ca.ids.abms.modules.reports2.invoices.interest.InterestInvoiceService;
import ca.ids.abms.modules.reports2.transaction.TransactionReceiptData;
import ca.ids.abms.modules.reports2.transaction.TransactionReceiptDocumentCreator;
import ca.ids.abms.modules.system.SystemConfigurationService;
import ca.ids.abms.modules.system.summary.SystemConfigurationItemName;
import ca.ids.abms.modules.translation.TranslationRequired;
import ca.ids.abms.modules.users.User;
import ca.ids.abms.modules.util.models.Calculation;
import ca.ids.abms.modules.util.models.CurrencyUtils;
import ca.ids.abms.modules.util.models.WhitelistingUtils;
import ca.ids.abms.modules.utilities.invoices.InvoiceSequenceNumberHelper;
import ca.ids.abms.modules.workflows.ApprovalWorkflowService;
import org.assertj.core.data.Offset;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TransactionServiceTest extends TranslationRequired {

    private final static Logger log = LoggerFactory.getLogger(TransactionServiceTest.class);

    private TransactionRepository transactionRepository;
    private TransactionService transactionService;
    private TransactionTypeRepository transactionTypeRepository;
    private AccountRepository accountRepository;
    private CurrencyRepository currencyRepository;
    private CurrencyExchangeRateRepository currencyExchangeRateRepository;
    private TransactionPaymentRepository transactionPaymentRepository;
    private CurrencyExchangeRateService currencyExchangeRateService;
    private BillingLedgerRepository billingLedgerRepository;

    @Before
    public void setup() {
        transactionRepository = mock(TransactionRepository.class);
        transactionTypeRepository = mock(TransactionTypeRepository.class);
        accountRepository = mock(AccountRepository.class);
        AccountService accountService = mock(AccountService.class);
        currencyRepository = mock(CurrencyRepository.class);
        currencyExchangeRateRepository = mock(CurrencyExchangeRateRepository.class);
        transactionPaymentRepository = mock(TransactionPaymentRepository.class);
        FlightMovementRepository flightMovementRepository = mock(FlightMovementRepository.class);
        BillingLedgerFlightUtility billingLedgerFlightUtility = mock(BillingLedgerFlightUtility.class);
        currencyExchangeRateService = mock(CurrencyExchangeRateService.class);
        CurrencyService currencyService = new CurrencyService(currencyRepository);
        ApprovalWorkflowService approvalWorkflowService = mock(ApprovalWorkflowService.class);
        PendingTransactionMapper pendingTransactionMapper = mock(PendingTransactionMapper.class);
        PendingTransactionRepository pendingTransactionRepository = mock(PendingTransactionRepository.class);
        billingLedgerRepository = mock(BillingLedgerRepository.class);
        ChargesAdjustmentService chargesAdjustmentService = mock(ChargesAdjustmentService.class);
        ReportHelper reportHelper = mock(ReportHelper.class);
        TransactionReceiptDocumentCreator transactionReceiptDocumentCreator = mock(TransactionReceiptDocumentCreator.class);
        InvoiceSequenceNumberHelper invoiceSequenceNumberHelper = mock(InvoiceSequenceNumberHelper.class);
        BillingLedgerService billingLedgerService = mock(BillingLedgerService.class);
        RoundingUtils roundingUtils = mock(RoundingUtils.class);
        CountryRepository countryRepository = mock(CountryRepository.class);
        when(currencyRepository.getANSPCurrency()).thenReturn(currencyRates.get("BWP").getCurrency());
        when(invoiceSequenceNumberHelper.generator()).thenReturn(mock(InvoiceSequenceNumberHelper.Generator.class));
        when(reportHelper.getBillingCenterOfCurrentUser()).thenReturn(getUserMock().getBillingCenter());
        when(reportHelper.getCurrentUser()).thenReturn(getUserMock());
        when(transactionReceiptDocumentCreator.create(any(TransactionReceiptData.class), any(ReportFormat.class)))
            .thenReturn(mock(ReportDocument.class));

        SystemConfigurationService systemConfigurationService = mock(SystemConfigurationService.class);
        when(systemConfigurationService.getString(SystemConfigurationItemName.ORGANISATION_NAME)).thenReturn("EANA");
        when(systemConfigurationService.getCurrentValue(eq(SystemConfigurationItemName.APPLY_INTEREST_PENALTY_ON)))
            .thenReturn("Next invoice");

        CurrencyUtils currencyUtils = new CurrencyUtils(currencyService,
            new CurrencyExchangeRateService(currencyRepository, currencyExchangeRateRepository, null), systemConfigurationService);

        OverdueInvoiceService overdueInvoiceService = mock(OverdueInvoiceService.class);
        InterestInvoiceService interestInvoiceService = mock(InterestInvoiceService.class);
        InterestRateService interestRateService = mock(InterestRateService.class);
        WhitelistingUtils whitelistingUtils = mock(WhitelistingUtils.class);
        FlightMovementBuilder flightMovementBuilder = mock(FlightMovementBuilder.class);
        FlightMovementRepositoryUtility flightMovementRepositoryUtility = mock(FlightMovementRepositoryUtility.class);

        transactionService = new TransactionService(
            transactionRepository,
            transactionTypeRepository,
            accountRepository,
            accountService,
            currencyService,
            transactionPaymentRepository,
            currencyUtils,
            billingLedgerFlightUtility,
            flightMovementRepository,
            currencyExchangeRateService,
            systemConfigurationService,
            approvalWorkflowService,
            pendingTransactionMapper,
            pendingTransactionRepository,
            billingLedgerRepository,
            chargesAdjustmentService,
            reportHelper,
            transactionReceiptDocumentCreator,
            invoiceSequenceNumberHelper,
            billingLedgerService,
            roundingUtils,
            mock(BankCodeService.class),
            null,
            overdueInvoiceService,
            interestInvoiceService,
            interestRateService,
            whitelistingUtils,
            flightMovementBuilder, flightMovementRepositoryUtility, null);

        when(currencyRepository.getANSPCurrency()).thenReturn(currencyRates.get("BWP").getCurrency());
        when(currencyRepository.findByCurrencyCode(eq("USD"))).thenReturn(currencyRates.get("USD").getCurrency());
        when(currencyRepository.findByCurrencyCode(eq("BWP"))).thenReturn(currencyRates.get("BWP").getCurrency());
        when(currencyRepository.findByCurrencyCode(eq("CAD"))).thenReturn(currencyRates.get("CAD").getCurrency());
        when(currencyRepository.findByCurrencyCode(eq("EUR"))).thenReturn(currencyRates.get("EUR").getCurrency());

        when(currencyExchangeRateRepository.getApplicableCurrencyExchangeRateToUsd(eq(1), any(LocalDateTime.class)))
            .thenReturn(buildRates(currencyRates.get("USD")));
        when(currencyExchangeRateRepository.getApplicableCurrencyExchangeRateToUsd(eq(2), any(LocalDateTime.class)))
            .thenReturn(buildRates(currencyRates.get("BWP")));
        when(currencyExchangeRateRepository.getApplicableCurrencyExchangeRateToUsd(eq(3), any(LocalDateTime.class)))
            .thenReturn(buildRates(currencyRates.get("CAD")));
        when(currencyExchangeRateRepository.getApplicableCurrencyExchangeRateToUsd(eq(4), any(LocalDateTime.class)))
            .thenReturn(buildRates(currencyRates.get("EUR")));

        when(currencyRepository.getOne(eq(1))).thenReturn(currencyRates.get("USD").getCurrency());
        when(currencyRepository.getOne(eq(2))).thenReturn(currencyRates.get("BWP").getCurrency());
        when(currencyRepository.getOne(eq(3))).thenReturn(currencyRates.get("CAD").getCurrency());
        when(currencyRepository.getOne(eq(4))).thenReturn(currencyRates.get("EUR").getCurrency());

    }

    @Test
    public void getAllTransactions() {
        List<Transaction> transactions = Collections.singletonList(new Transaction());

        when(transactionRepository.findAll(Matchers.<Specification<Transaction>>any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(transactions));

        Page<Transaction> results = transactionService.findAll(mock(Pageable.class), null, null, null, null, null);

        assertThat(results.getTotalElements()).isEqualTo(transactions.size());
    }

    @Test
    public void getTransactionById() {
        Transaction transaction = new Transaction();
        transaction.setId(1);

        when(transactionRepository.getOne(any())).thenReturn(transaction);

        Transaction result = transactionService.getOne(1);
        assertThat(result).isEqualTo(transaction);
    }
/*
    @Test
    public void create() {
        CurrencyExchangeRate exRate =currencyRates.get("USD");
        Currency currency = exRate.getCurrency();

        when(currencyRepository.getOne(any())).thenReturn(currency);
        when(currencyExchangeRateService.getExchangeRate(anyObject(), anyObject(), anyObject())).thenReturn(1.0d);
        when(currencyExchangeRateRepository.getApplicableCurrencyExchangeRateToUsd(anyInt(), anyObject())).thenReturn(exRate);
        TransactionType credit = getCreditTransactionType();
        when(transactionTypeRepository.getOne(any())).thenReturn(credit);

        Account account = getAccountMock();
        when(accountRepository.getOne(any())).thenReturn(account);

        when(transactionRepository.findOneByDescription(any(String.class)))
            .thenReturn(null);

        Transaction lastTransaction = getDebitTransaction();

        when(transactionRepository.findTopByAccountIdAndCurrencyIdOrderByTransactionDateTimeDesc(any(Integer.class), any(Integer.class)))
            .thenReturn(lastTransaction);

        when(currencyExchangeRateService.getExchangeAmount(eq(1.0), eq(-6.0),
            anyInt())).thenReturn(-6.0);

        Transaction newTransaction = new Transaction();
        newTransaction.setDescription("N/A");
        newTransaction.setAmount(-6.0);
        newTransaction.setPaymentAmount(-6.0);
        newTransaction.setTransactionType(credit);
        newTransaction.setCurrency(currencyRates.get("USD").getCurrency());
        newTransaction.setPaymentCurrency(currencyRates.get("USD").getCurrency());
        newTransaction.setAccount(account);
        newTransaction.setExchangeRateToAnsp(1.0);
        newTransaction.setExchangeRate(1.0);
        newTransaction.setPaymentExchangeRate(1.0);
        newTransaction.setPaymentMechanism(TransactionPaymentMechanism.cash);
        newTransaction.setPaymentReferenceNumber("N/A");

        when(transactionRepository.save(any(Transaction.class)))
            .thenReturn(newTransaction);

        Transaction savedTransaction =  transactionService.createCreditTransactionByPayments(newTransaction);

        assertThat(savedTransaction.getBalance().doubleValue())
            .isEqualTo((lastTransaction.getBalance().doubleValue() + savedTransaction.getAmount().doubleValue()));
    }
*/
    @Test
    public void getTransactionPaymentsByBillingLedgerId() {

        BillingLedger billingLedger = new BillingLedger();
        billingLedger.setId(1);

        TransactionPayment transactionPayment = new TransactionPayment();
        transactionPayment.setBillingLedger(billingLedger);
        transactionPayment.setId(1);

        List<TransactionPayment> transactionPayments = Collections.singletonList(transactionPayment);

        when(transactionPaymentRepository.findByBillingLedgerId(any()))
            .thenReturn(transactionPayments);

        List<TransactionPayment> results = transactionService.getTransactionPaymentsByBillingLedgerId(billingLedger.getId());

        assertThat(results).isEqualTo(transactionPayments);
    }

    @Test
    public void createByBillingLedger() {
        CurrencyExchangeRate exRate =currencyRates.get("USD");
        Currency currency = exRate.getCurrency();

        when(currencyRepository.getOne(any())).thenReturn(currency);
        when(currencyExchangeRateRepository.getOne(any())).thenReturn(exRate);
        when(currencyExchangeRateRepository.getApplicableCurrencyExchangeRateToUsd(anyInt(), anyObject())).thenReturn(buildRates(exRate));

        TransactionType debit = getDebitTransactionType();
        when(transactionTypeRepository.getOne(any())).thenReturn(debit);

        Account account = getAccountMock();
        when(accountRepository.getOne(any())).thenReturn(account);


        BillingLedger bill = new BillingLedger();
        bill.setId(1);
        bill.setInvoiceAmount(8.0);
        bill.setInvoiceExchange(1.0);
        bill.setInvoiceCurrency(currency);
        bill.setInvoiceNumber("1234");
        bill.setAccount(account);
        bill.setInvoiceStateType(InvoiceStateType.PUBLISHED.toValue());
        bill.setAmountOwing(8.0);

        when(accountRepository.getOne(any(Integer.class)))
            .thenReturn(account);

        when(transactionRepository.findOneByDescription(any(String.class)))
            .thenReturn(null);

        when(transactionTypeRepository.findOneByName(any(String.class)))
            .thenReturn(debit);

        Transaction lastTransaction = getDebitTransaction();

        /*List<Transaction> items = Collections.singletonList(lastTransaction);

        when(transactionRepository.findTop10ByAccountOrderByTransactionDateTimeDesc(any(Account.class)))
            .thenReturn(items);
*/
        Transaction newTransaction = new Transaction();
        newTransaction.setDescription("invoice");
        newTransaction.setBalance(4.0);
        newTransaction.setAmount(6.0);
        newTransaction.setTransactionType(debit);
        newTransaction.setCurrency(currency);
        newTransaction.setAccount(account);
        newTransaction.setCurrency(currencyRates.get("USD").getCurrency());

        when(transactionRepository.save(any(Transaction.class)))
            .thenReturn(newTransaction);

        Transaction savedTransaction =  transactionService.createDebitTransactionByInvoice(bill,false);

        assertThat(savedTransaction.getBalance().doubleValue())
            .isEqualTo((lastTransaction.getBalance() - savedTransaction.getAmount()));

    }

    @Test
    public void createTransactionByPaymentValues() {

        CurrencyExchangeRate exRateEur = currencyRates.get("EUR");
        CurrencyExchangeRate exRateBwp = currencyRates.get("BWP");

        Currency currencyEur = exRateEur.getCurrency();
        Currency currencyBwp = exRateBwp.getCurrency();

        double payment = -100D;
        double exRate = exRateEur.getExchangeRate() / exRateBwp.getExchangeRate();
        double amount = Calculation.truncate(exRate * payment,
            currencyBwp.getDecimalPlaces());

        when(currencyExchangeRateService.getExchangeAmount(eq(exRate), eq(payment), eq(currencyBwp.getDecimalPlaces())))
            .thenReturn(amount);

        when(currencyRepository.getOne(eq(4))).thenReturn(currencyEur);
        when(currencyRepository.getOne(eq(2))).thenReturn(currencyBwp);

        when(currencyExchangeRateRepository.getOne(eq(4))).thenReturn(exRateEur);
        when(currencyExchangeRateRepository.getOne(eq(2))).thenReturn(exRateBwp);

        when(currencyExchangeRateService.getExchangeRate(eq(currencyEur), eq(currencyBwp), any(LocalDateTime.class)))
            .thenReturn(exRate);

        TransactionType credit = getCreditTransactionType();
        when(transactionTypeRepository.getOne(any())).thenReturn(credit);

        Account account = getAccountMock();
        account.setInvoiceCurrency(currencyBwp);
        when(accountRepository.getOne(any())).thenReturn(account);

        when(transactionRepository.findOneByDescription(any(String.class)))
            .thenReturn(null);

        Transaction lastTransaction = getDebitTransaction();

        when(transactionRepository.findTopByAccountIdAndCurrencyIdOrderByTransactionDateTimeDesc(any(Integer.class), any(Integer.class)))
            .thenReturn(lastTransaction);

        BillingLedger billingLedger = new BillingLedger();
        billingLedger.setId(1);
        billingLedger.setInvoiceAmount(8.0);
        billingLedger.setInvoiceExchange(1.0);
        billingLedger.setInvoiceCurrency(currencyBwp);
        billingLedger.setAccount(account);
        billingLedger.setAmountOwing(0.0);
        billingLedger.setInvoiceStateType(InvoiceStateType.PUBLISHED.toValue());
        when(billingLedgerRepository.findOne(eq(1))).thenReturn(billingLedger);

        Transaction newTransaction = new Transaction();
        newTransaction.setDescription("payment");
        newTransaction.setAmount(amount);
        newTransaction.setTransactionType(credit);
        newTransaction.setCurrency(currencyBwp);
        newTransaction.setTargetCurrency(currencyBwp);
        newTransaction.setAccount(account);
        newTransaction.setPaymentAmount(payment);
        newTransaction.setPaymentCurrency(currencyEur);
        newTransaction.setPaymentExchangeRate(exRate);
        newTransaction.setPaymentMechanism(TransactionPaymentMechanism.cash);
        newTransaction.setPaymentReferenceNumber("N/A");
        newTransaction.setReceiptNumber("TEST");

        // add list of billing ledge ids
        List<Integer> billingLedgerIds = new ArrayList<>();
        billingLedgerIds.add(billingLedger.getId());
        newTransaction.setBillingLedgerIds(billingLedgerIds);

        when(transactionRepository.save(any(Transaction.class)))
            .thenReturn(newTransaction);

        Transaction savedTransaction =  transactionService.createCreditTransactionByPayments(newTransaction);

        assertThat(savedTransaction.getBalance())
            .isEqualTo((lastTransaction.getBalance() + savedTransaction.getAmount()));
    }

    @Test
    public void calculateBalanceAndCheckRoundings() {
        final double firstBalance = 2255.17D;
        final double increments = 219.31D;

        final CurrencyExchangeRate exchangeRateBwp = currencyRates.get("BWP");
        final CurrencyExchangeRate exchangeRateUsd = currencyRates.get("USD");

        final Account account = new Account();
        account.setId(1);
        account.setInvoiceCurrency(exchangeRateBwp.getCurrency());

        Transaction last = new Transaction();
        last.setAccount(account);
        last.setDescription("last1");
        last.setBalance(firstBalance);
        last.setCurrency(exchangeRateBwp.getCurrency());
        last.setExchangeRate(exchangeRateBwp.getExchangeRate());
        last.setExchangeRateToAnsp(exchangeRateUsd.getExchangeRate());
        last.setTransactionType(getDebitTransactionType());

        Transaction nue = new Transaction();
        nue.setAccount(account);
        nue.setDescription("D1");
        nue.setAmount(increments);
        nue.setCurrency(exchangeRateBwp.getCurrency());
        nue.setExchangeRate(exchangeRateBwp.getExchangeRate());
        nue.setExchangeRateToAnsp(exchangeRateUsd.getExchangeRate());
        nue.setTransactionType(getDebitTransactionType());

        when(transactionRepository.findTopByAccountIdAndCurrencyIdOrderByTransactionDateTimeDesc(eq(account.getId()), eq(account.getInvoiceCurrency().getId())))
            .thenReturn(last);
        transactionService.calculateTransactionBalance(nue);
        logTransactions(last, nue);

        last = nue;
        nue = new Transaction();
        nue.setAccount(account);
        nue.setDescription("D2");
        nue.setAmount(increments);
        nue.setCurrency(exchangeRateBwp.getCurrency());
        nue.setExchangeRate(exchangeRateBwp.getExchangeRate());
        nue.setExchangeRateToAnsp(exchangeRateUsd.getExchangeRate());
        nue.setTransactionType(getDebitTransactionType());

        when(transactionRepository.findTopByAccountIdAndCurrencyIdOrderByTransactionDateTimeDesc(eq(account.getId()), eq(account.getInvoiceCurrency().getId())))
            .thenReturn(last);
        transactionService.calculateTransactionBalance(nue);
        logTransactions(last, nue);

        last = nue;
        nue = new Transaction();
        nue.setAccount(account);
        nue.setDescription("D3");
        nue.setAmount(increments);
        nue.setCurrency(exchangeRateBwp.getCurrency());
        nue.setExchangeRate(exchangeRateBwp.getExchangeRate());
        nue.setExchangeRateToAnsp(exchangeRateUsd.getExchangeRate());
        nue.setTransactionType(getDebitTransactionType());

        when(transactionRepository.findTopByAccountIdAndCurrencyIdOrderByTransactionDateTimeDesc(eq(account.getId()), eq(account.getInvoiceCurrency().getId())))
            .thenReturn(last);
        transactionService.calculateTransactionBalance(nue);
        logTransactions(last, nue);

        last = nue;
        nue = new Transaction();
        nue.setAccount(account);
        nue.setDescription("C1");
        nue.setAmount(-increments);
        nue.setCurrency(exchangeRateBwp.getCurrency());
        nue.setExchangeRate(exchangeRateBwp.getExchangeRate());
        nue.setExchangeRateToAnsp(exchangeRateUsd.getExchangeRate());
        nue.setTransactionType(getCreditTransactionType());

        when(transactionRepository.findTopByAccountIdAndCurrencyIdOrderByTransactionDateTimeDesc(eq(account.getId()), eq(account.getInvoiceCurrency().getId())))
            .thenReturn(last);
        transactionService.calculateTransactionBalance(nue);
        logTransactions(last, nue);

        last = nue;
        nue = new Transaction();
        nue.setAccount(account);
        nue.setDescription("C2");
        nue.setAmount(-increments);
        nue.setCurrency(exchangeRateBwp.getCurrency());
        nue.setExchangeRate(exchangeRateBwp.getExchangeRate());
        nue.setExchangeRateToAnsp(exchangeRateUsd.getExchangeRate());
        nue.setTransactionType(getCreditTransactionType());

        when(transactionRepository.findTopByAccountIdAndCurrencyIdOrderByTransactionDateTimeDesc(eq(account.getId()), eq(account.getInvoiceCurrency().getId())))
            .thenReturn(last);
        transactionService.calculateTransactionBalance(nue);
        logTransactions(last, nue);

        last = nue;
        nue = new Transaction();
        nue.setAccount(account);
        nue.setDescription("C3");
        nue.setAmount(-increments);
        nue.setCurrency(exchangeRateBwp.getCurrency());
        nue.setExchangeRate(exchangeRateBwp.getExchangeRate());
        nue.setExchangeRateToAnsp(exchangeRateUsd.getExchangeRate());
        nue.setTransactionType(getCreditTransactionType());

        when(transactionRepository.findTopByAccountIdAndCurrencyIdOrderByTransactionDateTimeDesc(eq(account.getId()), eq(account.getInvoiceCurrency().getId())))
            .thenReturn(last);
        transactionService.calculateTransactionBalance(nue);
        logTransactions(last, nue);

        last = nue;
        nue = new Transaction();
        nue.setAccount(account);
        nue.setDescription("R1");
        nue.setAmount(0.0D);
        nue.setCurrency(exchangeRateBwp.getCurrency());
        nue.setExchangeRate(exchangeRateBwp.getExchangeRate());
        nue.setExchangeRateToAnsp(exchangeRateUsd.getExchangeRate());
        nue.setTransactionType(getDebitTransactionType());

        when(transactionRepository.findTopByAccountIdAndCurrencyIdOrderByTransactionDateTimeDesc(eq(account.getId()), eq(account.getInvoiceCurrency().getId())))
            .thenReturn(last);
        transactionService.calculateTransactionBalance(nue);
        logTransactions(last, nue);

        final double lastBalance = nue.getBalance();

        /* Let's verify that we're avoiding loss of precision when we use the exchange rates */
        assertThat(lastBalance).isEqualTo(firstBalance);
    }

    @Test
    public void testSpecificCaseOfInfinitesimalRate() {
        final double firstBalance = 2551.86D;
        final double increments = 100.0D;

        final CurrencyExchangeRate exchangeRateBwp = currencyRates.get("BWP");
        final CurrencyExchangeRate exchangeRateUsd = currencyRates.get("USD");

        final Account account = new Account();
        account.setId(1);
        account.setInvoiceCurrency(exchangeRateBwp.getCurrency());

        Transaction last = new Transaction();
        last.setAccount(account);
        last.setDescription("last1");
        last.setBalance(firstBalance);
        last.setCurrency(exchangeRateBwp.getCurrency());
        last.setExchangeRate(exchangeRateBwp.getExchangeRate());
        last.setExchangeRateToAnsp(exchangeRateUsd.getExchangeRate());
        last.setTransactionType(getDebitTransactionType());

        Transaction nue = new Transaction();
        nue.setAccount(account);
        nue.setDescription("D1");
        nue.setAmount(increments);
        nue.setCurrency(exchangeRateBwp.getCurrency());
        nue.setExchangeRate(exchangeRateBwp.getExchangeRate());
        nue.setExchangeRateToAnsp(exchangeRateUsd.getExchangeRate());
        nue.setTransactionType(getDebitTransactionType());

        when(transactionRepository.findTopByAccountIdAndCurrencyIdOrderByTransactionDateTimeDesc(eq(account.getId()), eq(account.getInvoiceCurrency().getId())))
            .thenReturn(last);
        transactionService.calculateTransactionBalance(nue);
        logTransactions(last, nue);

        last = nue;
        nue = new Transaction();
        nue.setAccount(account);
        nue.setDescription("D2");
        nue.setAmount(increments);
        nue.setCurrency(exchangeRateBwp.getCurrency());
        nue.setExchangeRate(exchangeRateBwp.getExchangeRate());
        nue.setExchangeRateToAnsp(exchangeRateUsd.getExchangeRate());
        nue.setTransactionType(getDebitTransactionType());

        when(transactionRepository.findTopByAccountIdAndCurrencyIdOrderByTransactionDateTimeDesc(eq(account.getId()), eq(account.getInvoiceCurrency().getId())))
            .thenReturn(last);
        transactionService.calculateTransactionBalance(nue);
        logTransactions(last, nue);

        last = nue;
        nue = new Transaction();
        nue.setAccount(account);
        nue.setDescription("C1");
        nue.setAmount(-increments);
        nue.setCurrency(exchangeRateBwp.getCurrency());
        nue.setExchangeRate(exchangeRateBwp.getExchangeRate());
        nue.setExchangeRateToAnsp(exchangeRateUsd.getExchangeRate());
        nue.setTransactionType(getCreditTransactionType());

        when(transactionRepository.findTopByAccountIdAndCurrencyIdOrderByTransactionDateTimeDesc(eq(account.getId()), eq(account.getInvoiceCurrency().getId())))
            .thenReturn(last);
        transactionService.calculateTransactionBalance(nue);
        logTransactions(last, nue);

        last = nue;
        nue = new Transaction();
        nue.setAccount(account);
        nue.setDescription("C2");
        nue.setAmount(-increments);
        nue.setCurrency(exchangeRateBwp.getCurrency());
        nue.setExchangeRate(exchangeRateBwp.getExchangeRate());
        nue.setExchangeRateToAnsp(exchangeRateUsd.getExchangeRate());
        nue.setTransactionType(getCreditTransactionType());

        when(transactionRepository.findTopByAccountIdAndCurrencyIdOrderByTransactionDateTimeDesc(eq(account.getId()), eq(account.getInvoiceCurrency().getId())))
            .thenReturn(last);
        transactionService.calculateTransactionBalance(nue);
        logTransactions(last, nue);

        last = nue;
        nue = new Transaction();
        nue.setAccount(account);
        nue.setDescription("R1");
        nue.setAmount(0.0D);
        nue.setCurrency(exchangeRateBwp.getCurrency());
        nue.setExchangeRate(exchangeRateBwp.getExchangeRate());
        nue.setExchangeRateToAnsp(exchangeRateUsd.getExchangeRate());
        nue.setTransactionType(getDebitTransactionType());

        when(transactionRepository.findTopByAccountIdAndCurrencyIdOrderByTransactionDateTimeDesc(eq(account.getId()), eq(account.getInvoiceCurrency().getId())))
            .thenReturn(last);
        transactionService.calculateTransactionBalance(nue);
        logTransactions(last, nue);

        final double lastBalance = nue.getBalance();

        assertThat(lastBalance).isCloseTo(firstBalance, Offset.offset(0.01D));
        if (firstBalance != lastBalance) {
            log.warn("Precision loss of about {}", Math.abs(lastBalance - firstBalance));
        }
    }

    private void logTransactions(Transaction last, Transaction nue) {
        log.debug("Balance: {}{}{}{}{} = {}{}", last.getBalance(), last.getCurrency().getCurrencyCode(),
            (nue.getTransactionType().getName().equals("debit") ? " + " : " - "),
            nue.getAmount(), nue.getCurrency().getCurrencyCode(), nue.getBalance(), nue.getCurrency().getCurrencyCode());
    }

    private static Transaction getDebitTransaction() {
        final Account account = new Account();
        account.setId(1);

        final Transaction lastTransaction = new Transaction();
        lastTransaction.setDescription("Test");
        lastTransaction.setBalance(10.0);
        lastTransaction.setAmount(1.0);
        lastTransaction.setTransactionType(getDebitTransactionType());
        lastTransaction.setCurrency(currencyRates.get("USD").getCurrency());
        lastTransaction.setAccount(account);
        return lastTransaction;
    }

    private static TransactionType getDebitTransactionType() {
        final TransactionType debit = new TransactionType();
        debit.setId(1);
        debit.setName("debit");
        return debit;
    }

    private static TransactionType getCreditTransactionType() {
        final TransactionType credit = new TransactionType();
        credit.setId(2);
        credit.setName("credit");
        return credit;
    }

    private static HashMap<String, CurrencyExchangeRate> currencyRates = buildCurrencyRates();

    private static HashMap<String, CurrencyExchangeRate> buildCurrencyRates() {
        final Country usa = new Country();
        usa.setId(1);
        usa.setCountryCode("USA");
        usa.setCountryName("United States of America");

        final Country bwa = new Country();
        bwa.setId(2);
        bwa.setCountryCode("BWA");
        bwa.setCountryName("Botsawana");

        final Country can = new Country();
        can.setId(3);
        can.setCountryCode("CAN");
        can.setCountryName("Canada");

        final Country ita = new Country();
        ita.setId(4);
        ita.setCountryCode("ITA");
        ita.setCountryName("Italy");

        final Currency usd = new Currency();
        usd.setId(1);
        usd.setCurrencyCode("USD");
        usd.setDecimalPlaces(2);
        usd.setSymbol("$");
        usd.setCountryCode(usa);

        final Currency bwp = new Currency();
        bwp.setId(2);
        bwp.setCurrencyCode("BWP");
        bwp.setDecimalPlaces(2);
        bwp.setSymbol("B");
        bwp.setCountryCode(bwa);

        final Currency cad = new Currency();
        cad.setId(3);
        cad.setCurrencyCode("CAD");
        cad.setDecimalPlaces(2);
        cad.setSymbol("$");
        cad.setCountryCode(can);

        final Currency eur = new Currency();
        eur.setId(4);
        eur.setCurrencyCode("EUR");
        eur.setDecimalPlaces(2);
        eur.setSymbol("€");
        eur.setCountryCode(ita);

        final CurrencyExchangeRate usdRate = new CurrencyExchangeRate();
        usdRate.setCurrency(usd);
        usdRate.setId(1);
        usdRate.setExchangeRate(1.0D);
        usdRate.setExchangeRateValidFromDate(LocalDateTime.now().minusDays(1));
        usdRate.setExchangeRateValidToDate(LocalDateTime.now().plusDays(1));

        final CurrencyExchangeRate bwpRate = new CurrencyExchangeRate();
        bwpRate.setCurrency(bwp);
        bwpRate.setId(2);
        //bwpRate.setExchangeRate(1.5);
        bwpRate.setExchangeRate(0.096818D);
        bwpRate.setExchangeRateValidFromDate(LocalDateTime.now().minusDays(1));
        bwpRate.setExchangeRateValidToDate(LocalDateTime.now().plusDays(1));

        final CurrencyExchangeRate cadRate = new CurrencyExchangeRate();
        cadRate.setCurrency(cad);
        cadRate.setId(3);
        cadRate.setExchangeRate(0.804149D);
        cadRate.setExchangeRateValidFromDate(LocalDateTime.now().minusDays(1));
        cadRate.setExchangeRateValidToDate(LocalDateTime.now().plusDays(1));

        final CurrencyExchangeRate eurRate = new CurrencyExchangeRate();
        eurRate.setCurrency(eur);
        eurRate.setId(4);
        eurRate.setExchangeRate(1.15D);
        eurRate.setExchangeRateValidFromDate(LocalDateTime.now().minusDays(1));
        eurRate.setExchangeRateValidToDate(LocalDateTime.now().plusDays(1));

        final HashMap<String, CurrencyExchangeRate> currencyRates = new HashMap<>(4);
        currencyRates.put("USD", usdRate);
        currencyRates.put("BWP", bwpRate);
        currencyRates.put("CAD", cadRate);
        currencyRates.put("EUR", eurRate);
        return currencyRates;
    }

    static Account getAccountMock() {
        final Currency accountCurrency = currencyRates.get("USD").getCurrency();
        final Account account = new Account();
        account.setId(1);
        account.setInvoiceCurrency(accountCurrency);
        return account;
    }

    static BillingCenter getBillingCenterMock() {
        final BillingCenter billingCenter = new BillingCenter();
        billingCenter.setName("TEST");
        billingCenter.setHq(true);
        return billingCenter;
    }

    static User getUserMock() {
        final User user = new User();
        user.setLogin("mock.user");
        user.setEmail("mock.user@abms.ids.ca");
        user.setName("Mock User");
        user.setContactInformation("16135551234");
        user.setIsSelfcareUser(false);
        user.setForcePasswordChange(false);
        user.setRegistrationStatus(false);
        user.setBillingCenter(getBillingCenterMock());
        return user;
    }

    private List<CurrencyExchangeRate> buildRates (final CurrencyExchangeRate cer) {
        final List<CurrencyExchangeRate> items = new ArrayList<>(1);
        items.add(cer);
        return items;
    }
}
