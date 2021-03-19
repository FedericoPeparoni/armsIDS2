package ca.ids.abms.modules.unifiedtaxes;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.ids.abms.config.error.ErrorConstants;
import ca.ids.abms.config.error.ExceptionFactory;
import ca.ids.abms.modules.common.services.AbmsCrudService;

@Service
public class UnifiedTaxValidityService extends AbmsCrudService<UnifiedTaxValidity, Integer> {

//    private static final Logger LOG = LoggerFactory.getLogger(UnifiedTaxController.class);

    private final UnifiedTaxValidityRepository unifiedTaxValidityRepository;

    UnifiedTaxValidityService(final UnifiedTaxValidityRepository unifiedTaxValidityRepository) {
        super(unifiedTaxValidityRepository);
        this.unifiedTaxValidityRepository = unifiedTaxValidityRepository;
    }
    
    @Transactional
    @Override
    public UnifiedTaxValidity create(final UnifiedTaxValidity entity) {


        if(entity.getFromValidityYear() == null && entity.getToValidityYear() == null) {
            throw ExceptionFactory.persistenceDataManagement(new IllegalArgumentException("You have to specify at least one of the validity start and end date"),
                    ErrorConstants.ERR_DATE_START);
        }
        if(entity.getFromValidityYear() != null && entity.getToValidityYear()!= null && entity.getToValidityYear().isBefore(entity.getFromValidityYear())) {
            throw ExceptionFactory.persistenceDataManagement(new IllegalArgumentException("The validity start date must be before the validity end date"),
                    ErrorConstants.ERR_DATE_START);
        }
         
        Integer existingValidities = 0;
        if (entity.getFromValidityYear() == null) {
            existingValidities  = unifiedTaxValidityRepository.countValiditiesOverlappingToDate(entity.getToValidityYear());
        } else if (entity.getToValidityYear() == null) {
            existingValidities  = unifiedTaxValidityRepository.countValiditiesOverlappingFromDate(entity.getFromValidityYear());
        } else {
            existingValidities = unifiedTaxValidityRepository.countValiditiesOverlappingFromAndToDates(entity.getFromValidityYear(), entity.getToValidityYear());
        }
        if(existingValidities > 0) {
            throw ExceptionFactory.persistenceDataManagement(new IllegalArgumentException("Dates between the new validity and the existing validities are overlapping"),
                    ErrorConstants.ERR_DATE_OVERLAP);
        }
        return super.create(entity);
    }
    
    @Transactional
    @Override
    public UnifiedTaxValidity update(final Integer id, final UnifiedTaxValidity entity) {


        if(entity.getFromValidityYear() == null && entity.getToValidityYear() == null) {
            throw ExceptionFactory.persistenceDataManagement(new IllegalArgumentException("You have to specify at least one of the validity start and end date"),
                    ErrorConstants.ERR_DATE_START);
        }
        if(entity.getFromValidityYear() != null && entity.getToValidityYear()!= null && entity.getToValidityYear().isBefore(entity.getFromValidityYear())) {
            throw ExceptionFactory.persistenceDataManagement(new IllegalArgumentException("The validity start date must be before the validity end date"),
                    ErrorConstants.ERR_DATE_START);
        }
        Integer existingOverlappings = 0;
        if (entity.getFromValidityYear() == null) {
            existingOverlappings  = unifiedTaxValidityRepository.countValiditiesOverlappingToDateExcludingCurrentId(entity.getToValidityYear(), entity.getId());
        } else if (entity.getToValidityYear() == null) {
            existingOverlappings  = unifiedTaxValidityRepository.countValiditiesOverlappingFromDateExcludingCurrentId(entity.getFromValidityYear(), entity.getId());
        } else {
            existingOverlappings = unifiedTaxValidityRepository.countValiditiesOverlappingFromAndToDatesExcludingCurrentId(entity.getFromValidityYear(), entity.getToValidityYear(), entity.getId());
        }
        if(existingOverlappings > 0) {
            throw ExceptionFactory.persistenceDataManagement(new IllegalArgumentException("Dates between the updated validity and the existing validities are overlapping"),
                    ErrorConstants.ERR_DATE_OVERLAP);
        }
        return super.update(id, entity);
    }

    @Transactional(readOnly = true)
    public List<UnifiedTaxValidity> findAll() {
        return unifiedTaxValidityRepository.findAll();
    }

    public UnifiedTaxValidity findUnifiedTaxValidityByYear(LocalDateTime yearValidity) {

        Timestamp timestampValidity = Timestamp.valueOf(yearValidity);
        return unifiedTaxValidityRepository.findByValidityYear(timestampValidity);
    }

}
