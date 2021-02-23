package ca.ids.abms.plugins.kcaa.erp.modules.aircraftregistrations;

import ca.ids.abms.modules.localaircraftregistry.LocalAircraftRegistry;
import ca.ids.abms.modules.localaircraftregistry.LocalAircraftRegistryService;
import ca.ids.abms.plugins.kcaa.erp.config.KcaaErpDatabaseConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@SuppressWarnings("WeakerAccess")
public class KcaaErpAircraftRegistrationService {

    private static final Logger LOG = LoggerFactory.getLogger(KcaaErpAircraftRegistrationService.class);

    private final KcaaErpAircraftRegistrationRepository kcaaErpAircraftRegistrationRepository;

    private final LocalAircraftRegistryService localAircraftRegistryService;

    public KcaaErpAircraftRegistrationService(
        final KcaaErpAircraftRegistrationRepository kcaaErpAircraftRegistrationRepository,
        final LocalAircraftRegistryService localAircraftRegistryService
    ) {
        this.kcaaErpAircraftRegistrationRepository = kcaaErpAircraftRegistrationRepository;
        this.localAircraftRegistryService = localAircraftRegistryService;
    }

    @Transactional(readOnly = true, value = KcaaErpDatabaseConfig.TRANSACTION_MANAGER)
    public List<KcaaErpAircraftRegistration> findByTimestampBetween(byte[] timestampFrom, byte[] timestampTo) {
        LOG.debug("Request to get sales invoice line by timestamp between : from {}, to {}", timestampFrom, timestampTo);
        return kcaaErpAircraftRegistrationRepository.findByTimestampBetween(timestampFrom, timestampTo);
    }

    @Transactional(readOnly = true, value = KcaaErpDatabaseConfig.TRANSACTION_MANAGER)
    public byte[] findLatestTimestamp() {
        LOG.trace("Request to get sales invoice line latest timestamp");
        return kcaaErpAircraftRegistrationRepository.findLatestTimestamp();
    }

    @Transactional
    public LocalAircraftRegistry createLocalAircraftRegistry(KcaaErpAircraftRegistration registration) {
        LocalAircraftRegistry aircraftRegistration = new LocalAircraftRegistry();

        aircraftRegistration.setRegistrationNumber(registration.getRegistrationNumber());
        aircraftRegistration.setCoaDateOfRenewal(registration.getCoaDateOfRenewal());
        aircraftRegistration.setCoaDateOfExpiry(registration.getCoaDateOfExpiry());
        aircraftRegistration.setOwnerName(registration.getOwnerName());
        aircraftRegistration.setAnalysisType(registration.getAnalysisType());
        aircraftRegistration.setMtowWeight(registration.getMtowWeight());

        return localAircraftRegistryService.createOrUpdate(aircraftRegistration);
    }
}
