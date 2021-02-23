package ca.ids.abms.modules.formulas.ldp;

import java.io.IOException;
import java.util.Collection;

import ca.ids.abms.config.db.FiltersSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import ca.ids.abms.config.error.ErrorConstants;
import ca.ids.abms.config.error.ExceptionFactory;
import ca.ids.abms.modules.aerodromes.AerodromeCategory;
import ca.ids.abms.modules.aerodromes.AerodromeCategoryRepository;
import ca.ids.abms.spreadsheets.SSException;
import ca.ids.abms.spreadsheets.SSService;

@Service
@Transactional
public class LdpBillingFormulaService {

    private static final Logger LOG = LoggerFactory.getLogger(LdpBillingFormulaService.class);

    private final LdpBillingFormulaRepository ldpBillingFormulaRepository;
    private final AerodromeCategoryRepository aerodromeCategoryRepository;
    private final SSService ssService;

    private final Logger log = LoggerFactory.getLogger(LdpBillingFormulaService.class);

    public LdpBillingFormulaService(final LdpBillingFormulaRepository ldpBillingFormulaRepository,
                                    final AerodromeCategoryRepository aerodromeCategoryRepository,
                                    final SSService ssService) {
        this.ldpBillingFormulaRepository = ldpBillingFormulaRepository;
        this.aerodromeCategoryRepository = aerodromeCategoryRepository;
        this.ssService = ssService;
    }

    @Transactional(readOnly = true)
    public Collection<LdpBillingFormula> getFormulaInfo(final Integer aerodromeCategoryId) {
        final AerodromeCategory aerodromeCategory = this.aerodromeCategoryRepository.getOne(aerodromeCategoryId);
        return aerodromeCategory.getLdpBillingFormulas();
    }

    @Transactional(readOnly = true)
    public Page<LdpBillingFormula> getAllFormulasInfo(final Pageable pageable, final String textSearch) {
        LOG.debug("Request to find all LdpBillingFormulas by textSearch: {}", textSearch);
        final FiltersSpecification.Builder filterBuilder = new FiltersSpecification.Builder().lookFor(textSearch);
        return ldpBillingFormulaRepository.findAll(filterBuilder.build(), pageable);
    }

    @Transactional(readOnly = true)
    public LdpBillingFormula downloadFormula(final Integer aerodromeCategoryId, final String chargeType) {
        final LdpBillingFormulaKey primaryKey = new LdpBillingFormulaKey(aerodromeCategoryId, chargeType);
        return ldpBillingFormulaRepository.getOne(primaryKey);
    }

    public LdpBillingFormula uploadFormula(final Integer aerodromeCategoryId, final String chargeType,
                                           final MultipartFile spreadsheet) throws IOException {
        final LdpBillingFormulaKey primaryKey = new LdpBillingFormulaKey(aerodromeCategoryId, chargeType);
        LdpBillingFormula formula = ldpBillingFormulaRepository.findOne(primaryKey);
        if (formula == null) {
            final AerodromeCategory aerodromeCategory = aerodromeCategoryRepository.getOne(aerodromeCategoryId);
            formula = new LdpBillingFormula(aerodromeCategory, chargeType);
        }
        final byte[] file = spreadsheet.getBytes();
        validateSpreadsheet (chargeType, file, spreadsheet.getOriginalFilename());

        formula.setSpreadsheetContentType(spreadsheet.getContentType());
        formula.setSpreadsheetFileName(spreadsheet.getOriginalFilename());
        formula.setChargesSpreadsheet(file);
        ldpBillingFormulaRepository.saveAndFlush(formula);
        return ldpBillingFormulaRepository.getOne(primaryKey);
    }

    private void validateSpreadsheet(final String chargeType, final byte[] file, final String fileName) {
        try {
            ChargeTypes.validateSpreadsheet (chargeType, ssService, file, fileName);
        } catch (final SSException ex) {
            log.error(
                "LdpBillingFormulaService.validateSpreadsheet: Spreadsheet validation failed. " +
                    "chargeType: '{}', fileName: '{}', cause: {}", chargeType, fileName, ex);
            throw ExceptionFactory.getInvalidFileException(LdpBillingFormula.class, ex);
        }
    }

    public void deleteFormula(final Integer aerodromeCategoryId, final String chargeType) {
        try {
            ldpBillingFormulaRepository.delete(new LdpBillingFormulaKey(aerodromeCategoryId, chargeType));
        } catch (RuntimeException e) {
            throw ExceptionFactory.persistenceDataManagement(e,ErrorConstants.ERR_DELETE_NO_LONGER_EXISTS);
        }
    }

    public long countAll() {
        return ldpBillingFormulaRepository.count();
    }
}
