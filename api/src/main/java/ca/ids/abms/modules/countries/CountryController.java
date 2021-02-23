package ca.ids.abms.modules.countries;

import ca.ids.abms.config.error.CustomParametrizedException;
import ca.ids.abms.config.error.ErrorConstants;
import ca.ids.abms.modules.common.controllers.MediaDocumentComponent;
import ca.ids.abms.modules.reports2.common.ReportDocument;
import ca.ids.abms.modules.reports2.common.ReportDocumentCreator;
import ca.ids.abms.modules.util.models.PageImplCustom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@RestController
@RequestMapping("/api/countries")
@SuppressWarnings({"unused", "squid:S1452"})
public class CountryController extends MediaDocumentComponent {

    private static final Logger LOG = LoggerFactory.getLogger(CountryController.class);
    private final CountryService countryService;
    private final CountryMapper countryMapper;
    private final ReportDocumentCreator reportDocumentCreator;

    public CountryController(final CountryService aCountryService,
                             final CountryMapper aCountryMapper,
                             final ReportDocumentCreator reportDocumentCreator) {
        countryService = aCountryService;
        countryMapper = aCountryMapper;
        this.reportDocumentCreator = reportDocumentCreator;
    }

    @GetMapping
    public ResponseEntity<?> findAll(@RequestParam(name = "search", required = false) final String searchText,
                                     @SortDefault(sort = {"countryName"}, direction = Sort.Direction.ASC) Pageable pageable,
                                     @RequestParam(name = "csvExport", required = false) Boolean csvExport) {
        LOG.debug("REST request to get all Countries that contain the text: {}", searchText);

        final Page<Country> page = countryService.findAll(pageable, searchText);
        if (csvExport != null && csvExport) {
            final List<Country> list = page.getContent();
            final List<CountryCsvExportModel> csvExportModel = countryMapper.toCsvModel(list);
            ReportDocument report = reportDocumentCreator.createCsvDocument("Countries", csvExportModel, CountryCsvExportModel.class, true);
            return doCreateBinaryResponse(report);
        } else {
            final Page<CountryViewModel> resultPage = new PageImplCustom<>(countryMapper.toViewModel(page),
                pageable, page.getTotalElements(), countryService.countAll());
            return ResponseEntity.ok().body(resultPage);
        }
    }

    @PreAuthorize("hasAuthority('countries_modify')")
    @PutMapping(value = "/{id}")
    public ResponseEntity<CountryViewModel> updateCountry(@RequestBody final CountryViewModel countryDto,
                                                          @PathVariable final Integer id) {
        LOG.debug("REST request to update Country: {}", countryDto);

        final Country country = countryMapper.toModel(countryDto);
        Country result = countryService.update(id, country);
        final CountryViewModel resultDto = countryMapper.toViewModel(result);

        return ResponseEntity.ok().body(resultDto);
    }

    @PreAuthorize("hasAuthority('countries_modify')")
    @PostMapping
    public ResponseEntity<CountryViewModel> createCountry(@Valid @RequestBody final CountryViewModel countryDto) throws URISyntaxException {
        LOG.debug("REST request to create Country : {}", countryDto);

        final Country country = countryMapper.toModel(countryDto);
        Country result = countryService.create(country);
        final CountryViewModel resultDto = countryMapper.toViewModel(result);

        return ResponseEntity.created(new URI("/api/country-management/" + resultDto.getId()))
            .body(resultDto);
    }

    @PreAuthorize("hasAuthority('countries_modify')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteCountry(@PathVariable final Integer id) {
        LOG.debug("REST request to delete Country : {}", id);

        try {
            countryService.delete(id);
        } catch (DataIntegrityViolationException ex) {
            throw new CustomParametrizedException(ErrorConstants.ERR_DEPENDENCY_VIOLATION, ex);
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/country-by-country-code/{countryCode}")
    public ResponseEntity<CountryViewModel> getCountryByCountryCode (@PathVariable final String countryCode) {
        LOG.debug("REST request to get Country by county code: {}", countryCode);

        if (countryCode == null) {
            return ResponseEntity.badRequest().body(null);
        }

        Country country = countryService.findCountryByCountryCode(countryCode);
        final CountryViewModel resultDto = countryMapper.toViewModel(country);
        return ResponseEntity.ok().body(resultDto);
    }
}
