package ca.ids.abms.modules.unifiedtaxes;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ca.ids.abms.modules.flightmovements.category.FlightmovementCategory;
import ca.ids.abms.modules.flightmovements.enumerate.CostFormulaVar;
import ca.ids.abms.modules.formulas.FormulaEvaluator;
import ca.ids.abms.modules.formulas.enroute.EnrouteAirNavigationChargeCategory;
import ca.ids.abms.modules.formulas.enroute.EnrouteAirNavigationChargeFormula;
import ca.ids.abms.modules.formulas.enroute.EnrouteAirNavigationChargeFormulaValidationViewModel;
import ca.ids.abms.modules.formulas.unifiedtax.UnifiedTaxChargeFormulaValidationViewModel;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.ids.abms.config.error.ErrorConstants;
import ca.ids.abms.config.error.ExceptionFactory;
import ca.ids.abms.modules.common.services.AbmsCrudService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UnifiedTaxService extends AbmsCrudService<UnifiedTax, Integer> {

    private static final Logger LOG = LoggerFactory.getLogger(UnifiedTaxController.class);

	private final UnifiedTaxRepository unifiedTaxRepository;

	private final UnifiedTaxValidityService unifiedTaxValidityService;

    private final FormulaEvaluator formulaEvaluator;

    private static Map<String, Object> vars;

	UnifiedTaxService(final UnifiedTaxValidityService unifiedTaxValidityService,
			final UnifiedTaxRepository unifiedTaxRepository,
            final FormulaEvaluator formulaEvaluator) {
		super(unifiedTaxRepository);
		this.unifiedTaxRepository = unifiedTaxRepository;
		this.unifiedTaxValidityService = unifiedTaxValidityService;
        this.formulaEvaluator = formulaEvaluator;
        initVarsMap();
	}

	@Transactional
	@Override
	public UnifiedTax create(final UnifiedTax entity) {

		if (entity.getFromManufactureYear() == null && entity.getToManufactureYear() == null) {
			throw ExceptionFactory.persistenceDataManagement(
					new IllegalArgumentException(
							"You have to specify at least one of the manufacture start and end date"),
					ErrorConstants.ERR_DATE_START);
		}
		if (entity.getFromManufactureYear() != null && entity.getToManufactureYear() != null
				&& entity.getToManufactureYear().isBefore(entity.getFromManufactureYear())) {
			throw ExceptionFactory.persistenceDataManagement(
					new IllegalArgumentException("The manufacture start date must be before the manufacture end date"),
					ErrorConstants.ERR_DATE_START);
		}
		UnifiedTaxValidity validity = unifiedTaxValidityService.findOne(entity.getValidity().getId());
		if (validity == null) {
			throw ExceptionFactory.persistenceDataManagement(
					new IllegalArgumentException("The specified validity is not available in the database"),
					ErrorConstants.ERR_INVALID_UNIFIED_TAX_VALIDITY);
		}
		entity.setValidity(validity);
		Integer existingOverlappings = 0;
		if (entity.getFromManufactureYear() == null) {
			existingOverlappings = unifiedTaxRepository.countManifactureOverlappingToDateOnTheSameValidityPeriod(
					entity.getToManufactureYear(), entity.getValidity().getId());
		} else if (entity.getToManufactureYear() == null) {
			existingOverlappings = unifiedTaxRepository.countManifactureOverlappingFromDateOnTheSameValidityPeriod(
					entity.getFromManufactureYear(), entity.getValidity().getId());
		} else {
			existingOverlappings = unifiedTaxRepository
					.countManifactureOverlappingFromAndToDatesOnTheSameValidityPeriod(entity.getFromManufactureYear(),
							entity.getToManufactureYear(), entity.getValidity().getId());
		}
		if (existingOverlappings > 0) {
			throw ExceptionFactory.persistenceDataManagement(
					new IllegalArgumentException(
							"Dates between the updated tax and the existing taxes are overlapping"),
					ErrorConstants.ERR_DATE_OVERLAP);
		}

		return super.create(entity);
	}

	@Transactional
	@Override
	public UnifiedTax update(final Integer id, final UnifiedTax entity) {

		if (entity.getFromManufactureYear() == null && entity.getToManufactureYear() == null) {
			throw ExceptionFactory.persistenceDataManagement(
					new IllegalArgumentException(
							"You have to specify at least one of the manufacture start and end date"),
					ErrorConstants.ERR_DATE_START);
		}
		if (entity.getFromManufactureYear() != null && entity.getToManufactureYear() != null
				&& entity.getToManufactureYear().isBefore(entity.getFromManufactureYear())) {
			throw ExceptionFactory.persistenceDataManagement(
					new IllegalArgumentException("The manufacture start date must be before the manufacture end date"),
					ErrorConstants.ERR_DATE_START);
		}
		UnifiedTaxValidity validity = unifiedTaxValidityService.findOne(entity.getValidity().getId());
		if (validity == null) {
			throw ExceptionFactory.persistenceDataManagement(
					new IllegalArgumentException("The specified validity is not available in the database"),
					ErrorConstants.ERR_INVALID_UNIFIED_TAX_VALIDITY);
		}
		entity.setValidity(validity);
		Integer existingOverlappings = 0;
		if (entity.getFromManufactureYear() == null) {
			existingOverlappings = unifiedTaxRepository
					.countManifactureOverlappingToDateOnTheSameValidityPeriodExcludingCurrentId(
							entity.getToManufactureYear(), entity.getValidity().getId(), entity.getId());
		} else if (entity.getToManufactureYear() == null) {
			existingOverlappings = unifiedTaxRepository
					.countManifactureOverlappingFromDateOnTheSameValidityPeriodExcludingCurrentId(
							entity.getFromManufactureYear(), entity.getValidity().getId(), entity.getId());
		} else {
			existingOverlappings = unifiedTaxRepository
					.countManifactureOverlappingFromAndToDatesOnTheSameValidityPeriodExcludingCurrentId(
							entity.getFromManufactureYear(), entity.getToManufactureYear(),
							entity.getValidity().getId(), entity.getId());
		}
		if (existingOverlappings > 0) {
			throw ExceptionFactory.persistenceDataManagement(
					new IllegalArgumentException(
							"Dates between the updated tax and the existing taxes are overlapping"),
					ErrorConstants.ERR_DATE_OVERLAP);
		}
		return super.update(id, entity);
	}

	@Transactional(readOnly = true)
	public List<UnifiedTax> findAll() {
		return unifiedTaxRepository.findAll();
	}

	@Transactional(readOnly = true)
	public UnifiedTax findUnifiedTaxByValidityYearAndManufactureYear(LocalDateTime yearValidity,
			LocalDateTime yearManufacture) {

		UnifiedTaxValidity unifiedTaxValidity = unifiedTaxValidityService.findUnifiedTaxValidityByYear(yearValidity);
		if (unifiedTaxValidity == null) {
			throw ExceptionFactory.persistenceDataManagement(
					new IllegalArgumentException("The specified validity year is not available in the database"),
					ErrorConstants.ERR_INVALID_UNIFIED_TAX_VALIDITY);
		}

		Timestamp timestampManufacture = Timestamp.valueOf(yearManufacture);
		UnifiedTax unifiedTax = unifiedTaxRepository.findByValidityAndManifactureYear(unifiedTaxValidity.getId(), timestampManufacture);
		if (unifiedTax == null) {
			throw ExceptionFactory.persistenceDataManagement(
					new IllegalArgumentException("No unified tax is available in the database for the specified years"),
					ErrorConstants.ERR_NO_UNIFIED_TAX);
		}
		
		return unifiedTax;
	}

	public List<UnifiedTax> findAllByValidityId(Integer validityId) {
		List<UnifiedTax> taxManagement = new ArrayList<UnifiedTax>();
		List<UnifiedTax> taxManagement2 = unifiedTaxRepository.findAllByValidityId(validityId);
		for (UnifiedTax u : taxManagement2) {
			if (u.getFromManufactureYear() == null) {
				taxManagement.add(u);
			}
		}
		for (UnifiedTax u : taxManagement2) {
			if (u.getFromManufactureYear() != null) {
				taxManagement.add(u);
			}
		}
		return taxManagement;
	}

    public FormulaEvaluator getFormulaEvaluator() {
        return formulaEvaluator;
    }


    UnifiedTaxChargeFormulaValidationViewModel validateUnifiedTaxFormula(String aFormula) {
        // evaluate wformula
        UnifiedTaxChargeFormulaValidationViewModel wformulaValidation=new UnifiedTaxChargeFormulaValidationViewModel();
        try {
            wformulaValidation.setFormula(aFormula);
            formulaEvaluator.evalDouble(aFormula,vars);
            wformulaValidation.setFormulaValid(Boolean.TRUE);
        } catch (Exception e) {
            LOG.error(e.getLocalizedMessage(), e);
            wformulaValidation.setFormulaValid(Boolean.FALSE);
            wformulaValidation.setIssue(e.getLocalizedMessage());
        }

        return wformulaValidation;
    }

    private void initVarsMap(){
        vars = new HashedMap();
        vars.put(CostFormulaVar.MTOW.varName(),12.89);
        vars.put(CostFormulaVar.SCHEDCROSSDIST.varName(),25.03);
        vars.put(CostFormulaVar.AVGMASSFACTOR.varName(),45.65);
    }

}
