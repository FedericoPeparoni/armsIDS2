package ca.ids.abms.modules.utilities.invoices;

import javax.persistence.EntityManager;

import ca.ids.abms.config.error.ExceptionFactory;
import ca.ids.abms.config.security.SecurityUtils;
import ca.ids.abms.modules.billingcenters.BillingCenter;
import ca.ids.abms.modules.billingcenters.BillingCenterService;
import ca.ids.abms.modules.common.enumerators.InvoiceType;
import ca.ids.abms.modules.system.SystemConfigurationService;
import ca.ids.abms.modules.system.summary.SystemConfigurationItemName;
import ca.ids.abms.modules.transactions.TransactionPaymentMechanism;
import ca.ids.abms.modules.users.User;
import ca.ids.abms.modules.users.UserService;

class InvoiceSequenceNumberGeneratorImpl implements InvoiceSequenceNumberHelper.Generator {

    public InvoiceSequenceNumberGeneratorImpl (
            final EntityManager entityManager,
            final UserService userService,
            final SystemConfigurationService systemConfigurationService,
            final BillingCenterService billingCenterService) {

        this.entityManager = entityManager;
        this.billingCenterService = billingCenterService;

        final User currentUser = userService.getUserByLogin (SecurityUtils.getCurrentUserLogin());

        final boolean useSeparateInvoiceNumbersPerBillingCenter = systemConfigurationService.getBoolean (SystemConfigurationItemName.SEPARATE_INVOICE_NUMBERS_PER_BILLING_CENTER);
        this.invoiceSequenceNumberBillingCenter = currentUser != null
            ? do_getBillingCenter (currentUser, useSeparateInvoiceNumbersPerBillingCenter)
            : do_getBillingCenter (currentUser, false);

        final boolean useSeparateReceiptNumbersPerBillingCenter = systemConfigurationService.getBoolean (SystemConfigurationItemName.SEPARATE_RECEIPT_NUMBERS_PER_BILLING_CENTER);
        this.receiptSequenceNumberBillingCenter = currentUser != null
            ? do_getBillingCenter (currentUser, useSeparateReceiptNumbersPerBillingCenter)
            : do_getBillingCenter (currentUser, false);

        final boolean useFiscalYear = systemConfigurationService.getBoolean(SystemConfigurationItemName.RESET_SEQUENCE_NUMBERS_ON_NEW_FISCAL_YEAR, false);
        this.currentFiscalYear = useFiscalYear
            ? systemConfigurationService.getCurrentValue(SystemConfigurationItemName.CURRENT_FISCAL_YEAR)
            : null;

        this.useAdditionalInvoicesNumber = systemConfigurationService.getBoolean(SystemConfigurationItemName.USE_ADDITIONAL_INVOICES_NUMBER, false);
        this.useReceiptsNumbeByPaymentMechanim = systemConfigurationService.getBoolean(SystemConfigurationItemName.USE_RECEIPT_NUMBER_BY_PAYMENT_MECHANISM, false);

        // make sure that if use fiscal year, then we have one set
        if (useFiscalYear && (this.currentFiscalYear == null || this.currentFiscalYear.isEmpty()))
            throw new IllegalStateException("Cannot initialize invoice sequence number generator. Must define fiscal year system configuration items.");
    }

    @Override
    public String getInvoiceSequenceNumberType(final InvoiceType invoiceType) {
        
    	String invoiceSequenceNumberType = InvoiceSequenceNumberType.GENERIC.toString();
        if (useAdditionalInvoicesNumber && invoiceType != null && InvoiceType.AVIATION_IATA.equals(invoiceType)) {
        	invoiceSequenceNumberType = InvoiceSequenceNumberType.IATA.toString();
        }
       
        return invoiceSequenceNumberType;
    }
    
    @Override
    public String nextInvoiceSequenceNumber(final InvoiceType invoiceType) {
        
        if (useAdditionalInvoicesNumber && invoiceType != null && InvoiceType.AVIATION_IATA.equals(invoiceType)) {
        	return nextIATAInvoiceSequenceNumber();
        }
        
        do_lock (invoiceSequenceNumberBillingCenter);
        Integer seqNum = invoiceSequenceNumberBillingCenter.getInvoiceSequenceNumber();
        if (seqNum == null || seqNum < 0) {
            seqNum = 0;
        }
        invoiceSequenceNumberBillingCenter.setInvoiceSequenceNumber (seqNum + 1);
        entityManager.persist (invoiceSequenceNumberBillingCenter);
        final String prefix = invoiceSequenceNumberBillingCenter.getPrefixInvoiceNumber();
        return do_format (prefix, seqNum);
    }
    
    private String nextIATAInvoiceSequenceNumber() {
        do_lock (invoiceSequenceNumberBillingCenter);
        Integer seqNum = invoiceSequenceNumberBillingCenter.getIataInvoiceSequenceNumber();
        if (seqNum == null || seqNum < 0) {
            seqNum = 0;
        }
        invoiceSequenceNumberBillingCenter.setIataInvoiceSequenceNumber (seqNum + 1);
        entityManager.persist (invoiceSequenceNumberBillingCenter);
        final String prefix = invoiceSequenceNumberBillingCenter.getPrefixInvoiceNumber();
        return do_format (prefix, seqNum);
    }


    @Override
    public String getReceiptSequenceNumberType(final TransactionPaymentMechanism transactPayMechanism) {
        
    	String invoiceSequenceNumberType = ReceiptSequenceNumberType.GENERIC.toString();
    	if (this.useReceiptsNumbeByPaymentMechanim && transactPayMechanism != null) {
        	switch(transactPayMechanism) {
            case cheque:
            	invoiceSequenceNumberType = ReceiptSequenceNumberType.CHEQUE.toString();
            	break;
            case wire:
            	invoiceSequenceNumberType = ReceiptSequenceNumberType.WIRE.toString();
            	break;
            default:
            	invoiceSequenceNumberType = ReceiptSequenceNumberType.GENERIC.toString();
        	}
        }
       
        return invoiceSequenceNumberType;
    }
    
    @Override
    public String nextReceiptSequenceNumber(final TransactionPaymentMechanism transactPayMechanism) {        
        if (this.useReceiptsNumbeByPaymentMechanim && transactPayMechanism != null) {
        	switch(transactPayMechanism) {
            case cheque:
            	return nextChequeReceiptSequenceNumber();
            case wire:
                return nextWireReceiptSequenceNumber();
            default:
                // ignore
                break;
    		}
        }

        do_lock (receiptSequenceNumberBillingCenter);
        Integer seqNum = receiptSequenceNumberBillingCenter.getReceiptSequenceNumber();
        if (seqNum == null || seqNum < 0) {
        	seqNum = 0;
        }
        receiptSequenceNumberBillingCenter.setReceiptSequenceNumber (seqNum + 1);
        entityManager.persist (receiptSequenceNumberBillingCenter);
        final String prefix = receiptSequenceNumberBillingCenter.getPrefixReceiptNumber();
        return do_format (prefix, seqNum);
    }
    
    private String nextChequeReceiptSequenceNumber() {
        do_lock (receiptSequenceNumberBillingCenter);
        Integer seqNum = receiptSequenceNumberBillingCenter.getReceiptChequeSequenceNumber();
        if (seqNum == null || seqNum < 0) {
            seqNum = 0;
        }
        receiptSequenceNumberBillingCenter.setReceiptChequeSequenceNumber(seqNum + 1);
        entityManager.persist (receiptSequenceNumberBillingCenter);
        final String prefix = receiptSequenceNumberBillingCenter.getPrefixReceiptNumber();
        return do_format (prefix, seqNum);
    }
    
    private String nextWireReceiptSequenceNumber() {
        do_lock (receiptSequenceNumberBillingCenter);
        Integer seqNum = receiptSequenceNumberBillingCenter.getReceiptWireSequenceNumber();
        if (seqNum == null || seqNum < 0) {
            seqNum = 0;
        }
        receiptSequenceNumberBillingCenter.setReceiptWireSequenceNumber (seqNum + 1);
        entityManager.persist (receiptSequenceNumberBillingCenter);
        final String prefix = receiptSequenceNumberBillingCenter.getPrefixReceiptNumber();
        return do_format (prefix, seqNum);
    }

    private void do_lock (final BillingCenter bc) {
        entityManager.flush();
        billingCenterService.lockBillingCenterNoKeyUpdate(bc);
    }

    private String do_format (final String prefix, final int seqNum) {
        String trimmedPrefix;
        if (prefix == null || prefix.isEmpty())
            trimmedPrefix = "";
        else
            trimmedPrefix = prefix.trim();
        // currentFiscalYear will only be set if useFiscalYear is true
        if (this.currentFiscalYear != null && !this.currentFiscalYear.isEmpty())
            trimmedPrefix += this.currentFiscalYear.trim() + InvoiceSequenceNumberConstants.FISCAL_YEAR_SUFFIX;
        return String.format ("%s%06d", trimmedPrefix, seqNum);
    }

    private BillingCenter do_getHqBillingCenter() {
        if (this.hqBillingCenter == null) {
            this.hqBillingCenter = billingCenterService.findHq();
            if (this.hqBillingCenter == null) {
                throw ExceptionFactory.getMissingHqBillingCenterException (InvoiceSequenceNumberHelperImpl.class);
            }
        }
        return this.hqBillingCenter;
    }

    private BillingCenter do_getBillingCenter (final User user, final boolean dontUseHq) {
        if (dontUseHq) {
            BillingCenter billingCenter = user.getBillingCenter();
            if (billingCenter == null) {
                // self-care user doesn't have a billing center assigned but can create a credit card payment,
                // in this case the Hq should be used
                if (user.getIsSelfcareUser()) {
                    billingCenter = billingCenterService.findHq();
                } else {
                    throw ExceptionFactory.getMissingBillingCenterOfCurrentUserException (InvoiceSequenceNumberHelperImpl.class);
                }
            }
            return billingCenter;
        }
        return do_getHqBillingCenter();

    }
    private final EntityManager entityManager;
    private final BillingCenterService billingCenterService;
    private final BillingCenter invoiceSequenceNumberBillingCenter;
    private final BillingCenter receiptSequenceNumberBillingCenter;
    private final String currentFiscalYear;
    private final boolean useAdditionalInvoicesNumber;
    private final boolean useReceiptsNumbeByPaymentMechanim;
    private BillingCenter hqBillingCenter = null;
}
