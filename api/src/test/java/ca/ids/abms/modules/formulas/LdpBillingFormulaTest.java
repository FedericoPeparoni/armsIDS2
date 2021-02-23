package ca.ids.abms.modules.formulas;

import ca.ids.abms.config.db.FiltersSpecification;
import ca.ids.abms.modules.aerodromes.AerodromeCategory;
import ca.ids.abms.modules.aerodromes.AerodromeCategoryRepository;

import ca.ids.abms.modules.formulas.ldp.*;

import ca.ids.abms.spreadsheets.AerodromeCharges;
import ca.ids.abms.spreadsheets.SSLoader;
import ca.ids.abms.spreadsheets.SSService;
import ca.ids.abms.spreadsheets.SSView;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class LdpBillingFormulaTest {
    private LdpBillingFormulaRepository ldpBillingFormulaRepository;
    private LdpBillingFormulaService ldpBillingFormulaService;
    private AerodromeCategoryRepository aerodromeCategoryRepository;
    private SSService ssService;
    private SSLoader <AerodromeCharges> ssLoader;
    private SSView ssView;
    private AerodromeCharges aerodromeCharges;

    @Before
    public void setup() {
        ldpBillingFormulaRepository = mock(LdpBillingFormulaRepository.class);
        aerodromeCategoryRepository = mock(AerodromeCategoryRepository.class);
        ssService = mock(SSService.class);
        ssLoader = mock(SSLoader.class);
        ssView = mock(SSView.class);
        aerodromeCharges = mock(AerodromeCharges.class);
        ldpBillingFormulaService = new LdpBillingFormulaService(
                ldpBillingFormulaRepository, aerodromeCategoryRepository, ssService);
    }

    @Test
    public void getAllLdpBillingFormulasPerAerodromeCategory() throws Exception {
        AerodromeCategory aerodromeCategory = new AerodromeCategory();
        aerodromeCategory.setId(1);
        List<LdpBillingFormula> ldpBillingFormulas = Collections.singletonList(
            new LdpBillingFormula(aerodromeCategory, "landing"));

        when(ldpBillingFormulaRepository.findAll(any(FiltersSpecification.class), any(Pageable.class)))
        .thenReturn(new PageImpl<>(ldpBillingFormulas));

        Page<LdpBillingFormula> results = ldpBillingFormulaService.getAllFormulasInfo(mock(Pageable.class), null);

        assertThat(results.getTotalElements()).isEqualTo(ldpBillingFormulas.size());
    }

    @Test
    public void downloadLdpBillingFormula() throws Exception {
        AerodromeCategory aerodromeCategory = new AerodromeCategory();
        aerodromeCategory.setId(1);
        LdpBillingFormula ldpBillingFormula = new LdpBillingFormula(aerodromeCategory, "landing");

        when(ldpBillingFormulaRepository.getOne(any(LdpBillingFormulaKey.class)))
            .thenReturn(ldpBillingFormula);

        LdpBillingFormula result = ldpBillingFormulaService.downloadFormula(1,"landing");
        assertThat(result).isEqualTo(ldpBillingFormula);
    }

    @Test
    public void uploadLdpBillingFormula() throws Exception {
        AerodromeCategory aerodromeCategory = new AerodromeCategory();
        aerodromeCategory.setId(1);
        LdpBillingFormula existingFormula = new LdpBillingFormula(aerodromeCategory, "landing");
        existingFormula.setChargesSpreadsheet("some data".getBytes());
        existingFormula.setSpreadsheetContentType("text/plain");
        existingFormula.setSpreadsheetFileName("filename.txt");

        when(aerodromeCategoryRepository.getOne(any())).thenReturn(aerodromeCategory);

        when(ldpBillingFormulaRepository.getOne(any())).thenReturn(existingFormula);

        when(ldpBillingFormulaRepository.save(any(LdpBillingFormula.class)))
            .thenReturn(existingFormula);

        MockMultipartFile file = new MockMultipartFile("data", "AerodromeChargesExample.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "some data".getBytes());
        when(ssLoader.load(any(byte[].class), anyString())).thenReturn(aerodromeCharges);
        when(ssService.aerodromeCharges()).thenReturn(ssLoader);

        LdpBillingFormula result = ldpBillingFormulaService.uploadFormula(1, "approach_charges", file);

        assertThat(result.getAerodromeCategoryId().equals(existingFormula.getAerodromeCategory().getId()));
        assertThat(result.getSpreadsheetFileName().equals(existingFormula.getSpreadsheetFileName()));
        assertThat(result.getSpreadsheetContentType().equals(existingFormula.getSpreadsheetContentType()));
        assertThat(result.getChargesSpreadsheet().equals(existingFormula.getChargesSpreadsheet()));
    }

    @Test
    public void deleteUser() throws Exception {
        ldpBillingFormulaService.deleteFormula(1, "landing");
        verify(ldpBillingFormulaRepository).delete(any(LdpBillingFormulaKey.class));
    }
}
