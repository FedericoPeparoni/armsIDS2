package ca.ids.abms.modules.utilities.invoices;

import javax.persistence.EntityManager;

import org.springframework.stereotype.Component;

import ca.ids.abms.modules.billingcenters.BillingCenterService;
import ca.ids.abms.modules.system.SystemConfigurationService;
import ca.ids.abms.modules.users.UserService;

@Component
class InvoiceSequenceNumberHelperImpl implements InvoiceSequenceNumberHelper {
    
    private final EntityManager entityManager;
    private final UserService userService;
    private final SystemConfigurationService systemConfigurationService;
    private final BillingCenterService billingCenterService;
    
    public InvoiceSequenceNumberHelperImpl (
            final EntityManager entityManager,
            final UserService userService,
            final SystemConfigurationService systemConfigurationService,
            final BillingCenterService billingCenterService) {
        this.entityManager = entityManager;
        this.userService = userService;
        this.systemConfigurationService = systemConfigurationService;
        this.billingCenterService = billingCenterService;
    }
    
    @Override
    public InvoiceSequenceNumberGeneratorImpl generator() {
        final InvoiceSequenceNumberGeneratorImpl generatorImpl = new InvoiceSequenceNumberGeneratorImpl (entityManager, userService, systemConfigurationService, billingCenterService);
        return generatorImpl;
    }
    
    
}
