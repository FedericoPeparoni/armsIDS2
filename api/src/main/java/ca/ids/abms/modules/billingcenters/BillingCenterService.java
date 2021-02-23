package ca.ids.abms.modules.billingcenters;

import ca.ids.abms.config.db.FiltersSpecification;
import ca.ids.abms.modules.flightmovements.FlightMovement;
import ca.ids.abms.modules.flightmovements.FlightMovementValidationViewModel;
import ca.ids.abms.modules.flightmovementsbuilder.FlightMovementBuilder;
import ca.ids.abms.modules.flightmovementsbuilder.FlightMovementValidator;
import ca.ids.abms.modules.util.models.ModelUtils;
import ca.ids.abms.modules.utilities.invoices.InvoiceSequenceNumberConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Service
@Transactional
public class BillingCenterService {
    private static final Logger LOG = LoggerFactory.getLogger(BillingCenterService.class);

    private final BillingCenterRepository billingCenterRepository;

    @Autowired
    @Lazy
    private FlightMovementBuilder flightMovementBuilder;

    @Autowired
    private FlightMovementValidator flightMovementValidator;

    public BillingCenterService(
        final BillingCenterRepository billingCenterRepository
    ){
        this.billingCenterRepository = billingCenterRepository;
    }

    @Transactional(readOnly = true)
    public Page<BillingCenter> findAll(Pageable pageable) {
        LOG.debug("Request to find all BillingCenters");
        return billingCenterRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<BillingCenter> findAll(Pageable pageable, String searchText) {
        LOG.debug("Request to find all BillingCenters");
        final FiltersSpecification.Builder filterBuilder = new FiltersSpecification.Builder()
            .lookFor(searchText);
        return billingCenterRepository.findAll(filterBuilder.build(), pageable);
    }

    @Transactional(readOnly = true)
    public Collection<BillingCenter> findAll() {
        LOG.debug("Request to find list of all BillingCenters");
        return billingCenterRepository.findAllByOrderByName();
    }

    @Transactional(readOnly = true)
    public BillingCenter findOne(Integer id) {
        LOG.debug("Request to find BillingCenter by ID: {}",id);
        return billingCenterRepository.findOne(id);
    }

    @Transactional(readOnly = true)
    public BillingCenter findHq() {
        LOG.debug("Request to find HQ BillingCenter");
        return billingCenterRepository.findHq();
    }

    public BillingCenter create(BillingCenter billingCenter) {
        LOG.debug("Request to create BillingCenter ");

        if (billingCenter.getHq()) {
            this.setCurrentHQToFalse();
        }

        if(billingCenter.getIataInvoiceSequenceNumber() == null) {
        	billingCenter.setIataInvoiceSequenceNumber(1);
        }

        if(billingCenter.getReceiptChequeSequenceNumber() == null) {
        	billingCenter.setReceiptChequeSequenceNumber(1);
        }

        if(billingCenter.getReceiptWireSequenceNumber() == null) {
        	billingCenter.setReceiptWireSequenceNumber(1);
        }
        
        return billingCenterRepository.save(billingCenter);
    }

    public void delete(Integer id) {
        LOG.debug("Request to delete BillingCenter : {}", id);
        billingCenterRepository.delete(id);
    }

    public BillingCenter update(Integer id, BillingCenter billingCenter) {
        LOG.debug("Request to update BillingCenter : {}", billingCenter);

        BillingCenter existingBillingCenter = billingCenterRepository.findOne(id);

        if (billingCenter.getHq() && !existingBillingCenter.getHq()) {
            this.setCurrentHQToFalse();
        }

        ModelUtils.merge(billingCenter, existingBillingCenter, "id", "invoiceSequenceNumber", "iataInvoiceSequenceNumber", "receiptSequenceNumber", "receiptChequeSequenceNumber", "receiptWireSequenceNumber");

        return billingCenterRepository.save(existingBillingCenter);
    }

    /**
     * Reset sequence numbers of all billing centers. Will set to
     * next available sequence number for the unique prefix.
     *
     * This method should only be called when system configuration item
     * `RESET_SEQUENCE_NUMBERS_ON_NEW_FISCAL_YEAR` is true.
     *
     * @param fiscalYear fiscal year to use
     */
    @Transactional
    public void resetSequenceNumbers(String fiscalYear) {
        List<BillingCenter> billingCenters = billingCenterRepository.findAll();
        for (BillingCenter billingCenter : billingCenters) {
            resetSequenceNumbers(billingCenter, fiscalYear);
        }
    }

    private void setCurrentHQToFalse() {

        BillingCenter billingCenterHQ = this.findHq();

        LOG.debug("if HQ already exists, setting it to false, hq: {}", billingCenterHQ);

        if (billingCenterHQ != null) {
            billingCenterHQ.setHq(false);
            billingCenterRepository.saveAndFlush(billingCenterHQ);
        }
    }

    /**
     * Reset the invoice and receipt sequence number for a billing center.
     *
     * @param billingCenter billing center to reset
     * @param fiscalYear fiscal year to use
     */
    private void resetSequenceNumbers(BillingCenter billingCenter, String fiscalYear) {

        String invoicePrefix = billingCenter.getPrefixInvoiceNumber()
            + fiscalYear + InvoiceSequenceNumberConstants.FISCAL_YEAR_SUFFIX;
        String receiptPrefix = billingCenter.getPrefixReceiptNumber()
            + fiscalYear + InvoiceSequenceNumberConstants.FISCAL_YEAR_SUFFIX;

        billingCenter.setInvoiceSequenceNumber(nextSequenceNumber(billingCenterRepository
            .lastInvoiceNumber(prefixNumberRegex(invoicePrefix)), invoicePrefix));
        billingCenter.setReceiptSequenceNumber(nextSequenceNumber(billingCenterRepository
            .lastReceiptNumber(prefixNumberRegex(receiptPrefix)), receiptPrefix));

        update(billingCenter.getId(), billingCenter);
    }

    /**
     * Parse last invoice or receipt number and get next sequence number.
     *
     * @param lastNumber last invoice or receipt number
     * @param prefix prefix of invoice or receipt number
     *
     * @return next sequence number
     */
    private Integer nextSequenceNumber(String lastNumber, String prefix) {
        if (lastNumber == null || lastNumber.isEmpty())
            return 1;
        else
            return Integer.parseInt(lastNumber.substring(prefix.length())) + 1;
    }

    private String prefixNumberRegex(String prefix) {
        return "^" + prefix + "\\d+$";
    }

    public long countAll() {
        return billingCenterRepository.count();
    }

    /**
     * This method prepares the flightCategoryType
     * and flightMovementCategory properties to be
     * able to resolve a billing center for the fm
     *
     * @param dep: the departure aerodrome
     * @param dest: the destination aerodrome
     * @return flightMovement with billing center
     */
    public FlightMovement getFlightMovementWithBillingCenter(final String dep, final String dest) {
        FlightMovement fm = new FlightMovement();

        // add departure aerodrome
        fm.setDepAd(dep);
        // add destination aerodrome
        fm.setDestAd(dest);
        // add flight category type
        fm.setFlightCategoryType(this.flightMovementValidator.validateFlightmovementCategoryType(fm));
        FlightMovementValidationViewModel fmvm = this.flightMovementValidator.validateFlightMovementCategory(fm);
        // add flight movement category
        fm.setFlightmovementCategory(fmvm.getFlightmovementCategory());

        // find the billing center for the flight
        this.flightMovementBuilder.resolveBillingCenter(fm, dep, dest);
        return fm;
    }
    
    @Transactional
    public void lockBillingCenterNoKeyUpdate(BillingCenter bc) {
        this.billingCenterRepository.lockNoKeyUpdate(bc.getId());
    }
}
