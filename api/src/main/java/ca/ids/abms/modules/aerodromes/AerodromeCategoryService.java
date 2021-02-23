package ca.ids.abms.modules.aerodromes;

import ca.ids.abms.config.db.FiltersSpecification;
import ca.ids.abms.config.error.ErrorConstants;
import ca.ids.abms.config.error.ErrorDTO;
import ca.ids.abms.config.error.ErrorVariables;
import ca.ids.abms.config.error.ExceptionFactory;
import ca.ids.abms.modules.formulas.ldp.LdpBillingFormula;
import ca.ids.abms.modules.formulas.ldp.LdpBillingFormulaRepository;
import ca.ids.abms.modules.util.models.ModelUtils;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@Transactional
public class AerodromeCategoryService {

    private final Logger log = LoggerFactory.getLogger(AerodromeCategoryService.class);

    private AerodromeCategoryRepository aerodromeCategoryRepository;
    private LdpBillingFormulaRepository ldpBillingFormulaRepository;

    public AerodromeCategoryService(AerodromeCategoryRepository aerodromeCategoryRepository, LdpBillingFormulaRepository ldpBillingFormulaRepository) {
        this.aerodromeCategoryRepository = aerodromeCategoryRepository;
        this.ldpBillingFormulaRepository = ldpBillingFormulaRepository;
    }

    public AerodromeCategory createAerodromeCategory(AerodromeCategory aerodromeCategory) {
        return aerodromeCategoryRepository.save(aerodromeCategory);
    }

    public void delete(Integer id) {
        log.debug("Request to delete Aerodrome Category : {}", id);
        try {
            final AerodromeCategory existingAerodromeCategory = aerodromeCategoryRepository.getOne(id);
            final Set<Aerodrome> aerodromes = existingAerodromeCategory.getAerodromes();
            if (CollectionUtils.isNotEmpty(aerodromes)) {
                StringBuilder sb = new StringBuilder();
                aerodromes.forEach(aerodrome -> sb.append(aerodrome.getAerodromeName() + ","));
                sb.setLength(sb.length() - 1);

                ErrorVariables errorVariables = new ErrorVariables();

                errorVariables.addEntry("sb", sb.toString());

                final ErrorDTO errorDto = new ErrorDTO.Builder()
                    .setErrorMessage(ErrorConstants.ERR_AERODROME_CATEGORY_DELETION)
                    .appendDetails("One or more aerodromes are associated with this category: {{sb}}")
                    .setDetailMessageVariables(errorVariables)
                    .build();

                log.debug(errorDto.getErrorDescription());
                throw ExceptionFactory.getInvalidDataException(errorDto);
            }
            final Set<LdpBillingFormula> charges = existingAerodromeCategory.getLdpBillingFormulas();
            if (CollectionUtils.isNotEmpty(charges)) {
                ldpBillingFormulaRepository.delete(existingAerodromeCategory.getLdpBillingFormulas());
                ldpBillingFormulaRepository.flush();
            }

            aerodromeCategoryRepository.delete(id);
        } catch (RuntimeException e) {
            throw ExceptionFactory.persistenceDataManagement(e, ErrorConstants.ERR_DELETE_NO_LONGER_EXISTS);
        }
        aerodromeCategoryRepository.flush();
    }

    @Transactional(readOnly = true)
    public List<AerodromeCategory> findAll() {
        log.debug("Request to get all Aerodrome Categories");

        return aerodromeCategoryRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Page<AerodromeCategory> findAll(String textSearch, Pageable pageable) {
        log.debug("Request to get aerodrome Categories by text search. Search: {}", textSearch);
        final FiltersSpecification.Builder filterBuilder = new FiltersSpecification.Builder().lookFor(textSearch);
        return aerodromeCategoryRepository.findAll(filterBuilder.build(), pageable);
    }

    @Transactional(readOnly = true)
    public AerodromeCategory getOne(Integer id) {
        log.debug("Request to get Aerodrome Category : {}", id);
        return aerodromeCategoryRepository.getOne(id);
    }

    public AerodromeCategory save(AerodromeCategory aerodromeCategory) {
        log.debug("Request to save Aerodrome Category : {}", aerodromeCategory);
        return aerodromeCategoryRepository.save(aerodromeCategory);
    }

    public AerodromeCategory update(Integer id, AerodromeCategory aerodromeCategory) {
        AerodromeCategory ac;
        try {
            log.debug("Request to update Aerodrome Category : {}", aerodromeCategory);
            aerodromeCategory.setId(id);
            final AerodromeCategory existingAerodromeCategory = aerodromeCategoryRepository.getOne(id);

            ModelUtils.checkVersionIfComparables(aerodromeCategory, existingAerodromeCategory);
            ModelUtils.merge(aerodromeCategory, existingAerodromeCategory, new String[0]);

            ac = aerodromeCategoryRepository.save(existingAerodromeCategory);
        } catch (RuntimeException e) {
            throw ExceptionFactory.persistenceDataManagement(e,ErrorConstants.ERR_UPDATE_NO_LONGER_EXISTS);
        }
        return ac;
    }

    public long countAll() {
        return aerodromeCategoryRepository.count();
    }
}
