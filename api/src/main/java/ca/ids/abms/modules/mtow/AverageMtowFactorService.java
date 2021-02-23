package ca.ids.abms.modules.mtow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.ids.abms.config.db.Filter;
import ca.ids.abms.config.db.FiltersSpecification;
import ca.ids.abms.modules.flightmovements.enumerate.FlightmovementCategoryScope;
import ca.ids.abms.modules.system.SystemConfigurationService;
import ca.ids.abms.modules.system.summary.SystemConfigurationItemName;
import ca.ids.abms.modules.util.models.ModelUtils;

@Service
@Transactional
public class AverageMtowFactorService {

    private static final Logger LOG = LoggerFactory.getLogger(AverageMtowFactorService.class);
    private final static String FACTOR_CLASS = "factorClass";

    private AverageMtowFactorRepository averageMtowFactorRepository;
    private SystemConfigurationService systemConfigurationService;

    public AverageMtowFactorService(AverageMtowFactorRepository aAverageMtowFactorRepository,
                                    SystemConfigurationService aSystemConfigurationService) {
        averageMtowFactorRepository = aAverageMtowFactorRepository;
        systemConfigurationService = aSystemConfigurationService;
    }


    public void delete(Integer id) {
        LOG.debug("Request to delete average mtow factor : {}", id);
        averageMtowFactorRepository.delete(id);
    }


    @Transactional(readOnly = true)
    public Page<AverageMtowFactor> findAll(final FactorClass aFilter, final Pageable pageable) {
        final FiltersSpecification.Builder filterBuilder = new FiltersSpecification.Builder();
        if (aFilter != null) {
            filterBuilder.restrictOn(Filter.equals(FACTOR_CLASS, aFilter));
        }

        LOG.debug("Attempting to find average mtow factors with factor class: {}", aFilter);
        return averageMtowFactorRepository.findAll(filterBuilder.build(), pageable);
    }


    @Transactional(readOnly = true)
    public AverageMtowFactor getOne(Integer id) {
        LOG.debug("Request to get average mtow factor : {}", id);
        return averageMtowFactorRepository.getOne(id);
    }


    @Transactional(readOnly = true)
    public AverageMtowFactor getOneByUpperLimit(Double upperLimit) {
        LOG.debug("Request to get the best average mtow factor by upper limit {} ", upperLimit);
        return averageMtowFactorRepository.findAverageMtowFactorByUpperLimit(upperLimit);
    }


    public AverageMtowFactor save(AverageMtowFactor averageMtowFactor) {
        LOG.debug("Request to save average mtow factor : {}", averageMtowFactor);
        return averageMtowFactorRepository.save(averageMtowFactor);
    }


    public AverageMtowFactor update(Integer id, AverageMtowFactor averageMtowFactor) {
        LOG.debug("Request to update average mtow factor : {}", averageMtowFactor);
        AverageMtowFactor existingAverageMtowFactor = averageMtowFactorRepository.getOne(id);
        ModelUtils.merge(averageMtowFactor, existingAverageMtowFactor);
        return averageMtowFactorRepository.save(existingAverageMtowFactor);
    }

    @Transactional(readOnly = true)
    public AverageMtowFactor findAverageMtowFactorByUpperLimit(Double upperLimit){
        if (upperLimit == null)
            return null;

        LOG.debug("Request to find average mass factor by upper limit : {}", upperLimit);
        return averageMtowFactorRepository.findAverageMtowFactorByUpperLimit(upperLimit);
    }

    @Transactional(readOnly = true)
    public AverageMtowFactor findAverageMtowFactorByUpperLimitAndFactorClass(Double upperLimit, String factorClass){
        LOG.debug("Request to find average mass factor for upperLimit={} and factorClass={}", upperLimit, factorClass);
        return averageMtowFactorRepository.findAverageMtowFactorByUpperLimitAndFactorClass(upperLimit, factorClass);
    }
    
    @Transactional(readOnly = true)
    public AverageMtowFactor findAverageMtowFactorByUpperLimitAndScope (final Double upperLimit, final FlightmovementCategoryScope scope){
        LOG.debug("Request to find average mass factor for upperLimit={} and scope={}", upperLimit, scope);
        if (upperLimit != null && scope != null) {
            return averageMtowFactorRepository.findAverageMtowFactorByUpperLimitAndFactorClass(upperLimit, scope.getMtowFactorClass());
        }
        return null;
    }

    /**
     * Find out if MTOW factor class is used
     *
     * @return boolean
     */
    public Boolean isMtowFactorClassUsed(){
     return systemConfigurationService.getOneByItemName(SystemConfigurationItemName.USE_MTOW_FACTOR_CLASS) != null &&
            systemConfigurationService.getOneByItemName(SystemConfigurationItemName.USE_MTOW_FACTOR_CLASS).getCurrentValue() != null &&
            systemConfigurationService.getOneByItemName(SystemConfigurationItemName.USE_MTOW_FACTOR_CLASS).getCurrentValue().equalsIgnoreCase(SystemConfigurationItemName.SYSTEM_CONFIG_TRUE);
    }

    public long countAll() {
        return averageMtowFactorRepository.count();
    }
}
