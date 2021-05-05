package ca.ids.abms.modules.billings;

import ca.ids.abms.config.error.CustomParametrizedException;
import ca.ids.abms.config.error.ErrorConstants;
import ca.ids.abms.modules.accounts.AccountRepository;
import ca.ids.abms.modules.chargesadjustment.ChargesAdjustmentService;
import ca.ids.abms.modules.common.enumerators.InvoiceStateType;
import ca.ids.abms.modules.currencies.Currency;
import ca.ids.abms.modules.currencies.CurrencyRepository;
import ca.ids.abms.modules.flightmovements.FlightMovementService;
import ca.ids.abms.modules.overdueinvoices.OverdueInvoiceService;
import ca.ids.abms.modules.reports2.common.RoundingUtils;
import ca.ids.abms.modules.system.SystemConfigurationService;
import ca.ids.abms.modules.system.summary.SystemConfigurationItemName;
import ca.ids.abms.modules.transactions.TransactionPaymentRepository;
import ca.ids.abms.modules.transactions.TransactionService;
import ca.ids.abms.modules.transactions.TransactionTypeService;
import ca.ids.abms.modules.translation.TranslationRequired;
import ca.ids.abms.modules.users.UserService;
import ca.ids.abms.modules.util.models.CurrencyUtils;
import ca.ids.abms.modules.utilities.invoices.InvoiceSequenceNumberHelper;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BillingLedgerServiceTest extends TranslationRequired {

    private BillingLedgerRepository billingLedgerRepository;
    private BillingLedgerService billingLedgerService;
    private CurrencyRepository currencyRepository;
    private CurrencyUtils currencyUtils;
    private InvoiceLineItemRepository invoiceLineItemRepository;

    @Before
    public void setup() {

        AccountRepository accountRepository = mock(AccountRepository.class);
        BillingLedgerFlightUtility billingLedgerFlightUtility = mock(BillingLedgerFlightUtility.class);
        ChargesAdjustmentService chargesAdjustmentService = mock(ChargesAdjustmentService.class);
        InvoiceOverduePenaltyRepository invoiceOverduePenaltyRepository = mock(InvoiceOverduePenaltyRepository.class);
        InvoiceSequenceNumberHelper invoiceSequenceNumberHelper = mock(InvoiceSequenceNumberHelper.class);
        OverdueInvoiceService overdueInvoiceService = mock(OverdueInvoiceService.class);
        RoundingUtils roundingUtils = mock(RoundingUtils.class);
        SystemConfigurationService systemConfigurationService = mock(SystemConfigurationService.class);
        TransactionPaymentRepository transactionPaymentRepository = mock(TransactionPaymentRepository.class);
        TransactionService transactionService = mock(TransactionService.class);
        UserService userService = mock(UserService.class);
        FlightMovementService flightMovementService = mock(FlightMovementService.class);
        TransactionTypeService transactionTypeService = mock(TransactionTypeService.class);

        InvoicesApprovalWorkflow invoicesApprovalWorkflow = new InvoicesApprovalWorkflow(systemConfigurationService);

        billingLedgerRepository = mock(BillingLedgerRepository.class);
        currencyRepository = mock(CurrencyRepository.class);
        currencyUtils = mock(CurrencyUtils.class);
        invoiceLineItemRepository = mock (InvoiceLineItemRepository.class);

        billingLedgerService = new BillingLedgerService(
            billingLedgerRepository,
            transactionService,
            transactionTypeService,
            currencyRepository,
            accountRepository,
            currencyUtils,
            transactionPaymentRepository,
            invoiceOverduePenaltyRepository,
            invoiceLineItemRepository,
            invoiceSequenceNumberHelper,
            userService,
            systemConfigurationService,
            billingLedgerFlightUtility,
            invoicesApprovalWorkflow,
            chargesAdjustmentService,
            overdueInvoiceService,
            roundingUtils,
            flightMovementService, null);

        when(roundingUtils.calculateSingleRoundedValue(anyDouble(), any(Currency.class), anyBoolean()))
            .thenAnswer(i -> i.getArguments()[0]);

        when(systemConfigurationService.getBoolean(eq(SystemConfigurationItemName.MANUAL_APPROVAL)))
            .thenReturn(true);
        when(systemConfigurationService.getBoolean(eq(SystemConfigurationItemName.MANUAL_PUBLISHING)))
            .thenReturn(true);
        when(systemConfigurationService.getBoolean(eq(SystemConfigurationItemName.MANUAL_APPROVAL), anyBoolean()))
            .thenReturn(true);
        when(systemConfigurationService.getBoolean(eq(SystemConfigurationItemName.MANUAL_PUBLISHING), anyBoolean()))
            .thenReturn(true);
        when(systemConfigurationService.getCurrentValue(eq(SystemConfigurationItemName.APPLY_INTEREST_PENALTY_ON)))
            .thenReturn("Next invoice");
    }

    @Test
    public void getAllBillingLedgers() {

        List<BillingLedger> billingLedgers = Collections.singletonList(new BillingLedger());

        when(billingLedgerRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(billingLedgers));

        Page<BillingLedger> results = billingLedgerService.findAll(null, mock(Pageable.class));

        assertThat(results.getTotalElements()).isEqualTo(billingLedgers.size());
    }

    @Test
    public void getBillingLedgerById() {

        BillingLedger billingLedger = new BillingLedger();
        billingLedger.setId(1);

        when(billingLedgerRepository.getOne(any())).thenReturn(billingLedger);

        BillingLedger result = billingLedgerService.getOne(1);
        assertThat(result).isEqualTo(billingLedger);
    }

    @Test
    public void getInvoicesThatArePaid() {

        List<BillingLedger> billingLedgers = new ArrayList<>(1);
        BillingLedger billingLedger = new BillingLedger();
        billingLedger.setInvoiceStateType(InvoiceStateType.PAID.toValue());
        billingLedgers.add(billingLedger);

        String[] stateTypeFilter = new String[]{InvoiceStateType.PAID.toValue()};

        when(billingLedgerRepository.findByInvoiceStateTypeInList(eq(stateTypeFilter), any(Pageable.class))).thenReturn(new PageImpl<>(billingLedgers));

        Page<BillingLedger> results = billingLedgerService.findAll("paid", mock(Pageable.class));
        assertThat(results.getTotalElements()).isEqualTo(billingLedgers.size());
    }

    @Test
    public void setStateAsApprovedfromNew() {

        Currency ansp = new Currency();
        ansp.setId(1);
        ansp.setCurrencyCode("USD");
        ansp.setDecimalPlaces(2);
        BillingLedger billingLedger = new BillingLedger();
        billingLedger.setId(1);
        billingLedger.setInvoiceExchangeToAnsp(1.0);
        billingLedger.setInvoiceExchange(1.0);
        billingLedger.setInvoiceCurrency(ansp);
        billingLedger.setInvoiceAmount(0.0);
        billingLedger.setInvoiceStateType(InvoiceStateType.NEW.toValue());
        billingLedger.setInvoiceType("aviation-noniata");

        when(billingLedgerRepository.getOne(1)).thenReturn(billingLedger);
        when(billingLedgerRepository.saveAndFlush(billingLedger)).thenReturn(billingLedger);
        when(currencyRepository.getOne(anyInt())).thenReturn(ansp);
        when(currencyRepository.getANSPCurrency()).thenReturn(ansp);
        when(currencyUtils.getAnspCurrency()).thenReturn(ansp);

        billingLedgerService.approve(1);
        assertThat(billingLedger.getInvoiceStateType()).isEqualTo(InvoiceStateType.APPROVED.toValue());

        billingLedgerService.reject(1);
        assertThat(billingLedger.getInvoiceStateType()).isEqualTo(InvoiceStateType.VOID.toValue());

        try {
            billingLedgerService.approve(1); // will throw error is already in `approved` state
        } catch (CustomParametrizedException e) {
            assertThat(e).isInstanceOf(CustomParametrizedException.class).hasMessage(
                String.format(ErrorConstants.ERR_CANNOT_UPDATE_STATE.toValue(), InvoiceStateType.VOID.toValue()));
        }
    }

    @Test
    public void setStateAsPublishedfromApproved() {

        Currency ansp = new Currency();
        ansp.setId(1);
        ansp.setCurrencyCode("USD");
        ansp.setDecimalPlaces(2);
        BillingLedger billingLedger = new BillingLedger();
        billingLedger.setId(1);
        billingLedger.setInvoiceExchangeToAnsp(1.0);
        billingLedger.setInvoiceExchange(1.0);
        billingLedger.setInvoiceCurrency(ansp);
        billingLedger.setInvoiceAmount(0.0);
        billingLedger.setInvoiceStateType(InvoiceStateType.APPROVED.toValue());

        when(billingLedgerRepository.getOne(1)).thenReturn(billingLedger);
        when(billingLedgerRepository.saveAndFlush(billingLedger)).thenReturn(billingLedger);
        when(currencyRepository.getOne(anyInt())).thenReturn(ansp);
        when(currencyRepository.getANSPCurrency()).thenReturn(ansp);
        when(currencyUtils.getAnspCurrency()).thenReturn(ansp);

        when(billingLedgerRepository.getOne(1)).thenReturn(billingLedger);
        when(billingLedgerRepository.saveAndFlush(billingLedger)).thenReturn(billingLedger);

        billingLedgerService.approve(1);
        assertThat(billingLedger.getInvoiceStateType()).isEqualTo(InvoiceStateType.PUBLISHED.toValue());

        try {
            billingLedgerService.approve(1); // will throw error, is already `published` state, will not go back
        } catch (CustomParametrizedException e) {
            assertThat(e).isInstanceOf(CustomParametrizedException.class).hasMessage(
                String.format(ErrorConstants.ERR_CANNOT_UPDATE_STATE.toValue(), InvoiceStateType.PUBLISHED.toValue()));
        }
    }

    @Test
    public void getLineItemsByInvoiceId() {

        List<InvoiceLineItem> lineItems = new ArrayList<>();
        BillingLedger billingLedger = new BillingLedger();
        billingLedger.setId(1);

        InvoiceLineItem invoiceLineItem = new InvoiceLineItem();
        invoiceLineItem.setId(1);
        invoiceLineItem.setBillingLedger(billingLedger);

        lineItems.add(invoiceLineItem);

        when(invoiceLineItemRepository.findByBillingLedger(any())).thenReturn(lineItems);

        List<InvoiceLineItem> result = billingLedgerService.getLineItemsByInvoiceId(1);
        assertThat(result.get(0)).isEqualTo(lineItems.get(0));
    }
}
