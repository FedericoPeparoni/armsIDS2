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
public class UnifiedTaxService extends AbmsCrudService<UnifiedTax, Integer> {

//    private static final Logger LOG = LoggerFactory.getLogger(UnifiedTaxController.class);

    private final UnifiedTaxRepository unifiedTaxRepository;
    
    private final UnifiedTaxValidityService unifiedTaxValidityService;

    UnifiedTaxService(final UnifiedTaxValidityService unifiedTaxValidityService, final UnifiedTaxRepository unifiedTaxRepository) {
        super(unifiedTaxRepository);
        this.unifiedTaxRepository = unifiedTaxRepository;
        this.unifiedTaxValidityService = unifiedTaxValidityService;
    }
    
    @Transactional
    @Override
    public UnifiedTax create(final UnifiedTax entity) {

        if(entity.getFromManufactureYear() != null && entity.getToManufactureYear()!= null && entity.getToManufactureYear().isBefore(entity.getFromManufactureYear())) {
            throw ExceptionFactory.persistenceDataManagement(new IllegalArgumentException("The manufacture start date must be before the manufacture end date"),
                    ErrorConstants.ERR_DATE_START);
        }
        UnifiedTaxValidity validity = unifiedTaxValidityService.findOne(entity.getValidity().getId());
        if(validity == null) {
            throw ExceptionFactory.persistenceDataManagement(new IllegalArgumentException("The specified validity is not available in the database"),
                    ErrorConstants.ERR_INVALID_UNIFIED_TAX_VALIDITY);
        }
        entity.setValidity(validity);
        Integer existingOverlappings = unifiedTaxRepository.countManifactureOverlappingFromAndToDatesOnTheSameValidityPeriod(entity.getFromManufactureYear(), entity.getToManufactureYear(),entity.getValidity().getId());
        if(existingOverlappings > 0) {
            throw ExceptionFactory.persistenceDataManagement(new IllegalArgumentException("Dates between the updated tax and the existing taxes are overlapping"),
                    ErrorConstants.ERR_DATE_OVERLAP);
        }
        
        return super.create(entity);
    }
    
    @Transactional
    @Override
    public UnifiedTax update(final Integer id, final UnifiedTax entity) {
        
        if(entity.getFromManufactureYear() != null && entity.getToManufactureYear()!= null && entity.getToManufactureYear().isBefore(entity.getFromManufactureYear())) {
            throw ExceptionFactory.persistenceDataManagement(new IllegalArgumentException("The manufacture start date must be before the manufacture end date"),
                    ErrorConstants.ERR_DATE_START);
        }
        UnifiedTaxValidity validity = unifiedTaxValidityService.findOne(entity.getValidity().getId());
        if(validity == null) {
            throw ExceptionFactory.persistenceDataManagement(new IllegalArgumentException("The specified validity is not available in the database"),
                    ErrorConstants.ERR_INVALID_UNIFIED_TAX_VALIDITY);
        }
        entity.setValidity(validity);
        Integer existingOverlappings = unifiedTaxRepository.countManifactureOverlappingFromAndToDatesOnTheSameValidityPeriodExcludingCurrentId(entity.getFromManufactureYear(), entity.getToManufactureYear(),entity.getValidity().getId(), entity.getId());
        if(existingOverlappings > 0) {
            throw ExceptionFactory.persistenceDataManagement(new IllegalArgumentException("Dates between the updated tax and the existing taxes are overlapping"),
                    ErrorConstants.ERR_DATE_OVERLAP);
        }
        return super.update(id, entity);
    }

    @Transactional(readOnly = true)
    public List<UnifiedTax> findAll() {
        return unifiedTaxRepository.findAll();
    }

    @Transactional(readOnly = true)
    public UnifiedTax findUnifiedTaxByValidityYearAndManufactureYear(LocalDateTime yearManufacture, LocalDateTime yearValidity) {

        Timestamp timestampManufacture = Timestamp.valueOf(yearManufacture);
        UnifiedTaxValidity unifiedTaxValidity = unifiedTaxValidityService.findUnifiedTaxValidityByYear(yearValidity);
        return unifiedTaxRepository.findByValidityAndManifactureYear(unifiedTaxValidity.getId(), timestampManufacture);
    }

    public List<UnifiedTax> findAllByValidityId(Integer validityId) {
        return unifiedTaxRepository.findAllByValidityId(validityId);
    }

}
