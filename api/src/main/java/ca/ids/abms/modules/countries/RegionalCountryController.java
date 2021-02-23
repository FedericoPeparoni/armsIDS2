package ca.ids.abms.modules.countries;

import ca.ids.abms.modules.common.controllers.MediaDocumentComponent;
import ca.ids.abms.modules.reports2.common.ReportDocument;
import ca.ids.abms.modules.reports2.common.ReportDocumentCreator;
import ca.ids.abms.modules.util.models.PageImplCustom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/regional-countries")
@SuppressWarnings({"unused", "squid:S1452"})
public class RegionalCountryController extends MediaDocumentComponent {

    private static final Logger LOG = LoggerFactory.getLogger(RegionalCountryController.class);

    private RegionalCountryService regionalCountryService;
    private RegionalCountryMapper regionalCountryMapper;
    private final ReportDocumentCreator reportDocumentCreator;

    public RegionalCountryController(final RegionalCountryService regionalCountryService,
                                     final RegionalCountryMapper regionalCountryMapper,
                                     final ReportDocumentCreator reportDocumentCreator) {
        this.regionalCountryService = regionalCountryService;
        this.regionalCountryMapper = regionalCountryMapper;
        this.reportDocumentCreator = reportDocumentCreator;
    }

    @PreAuthorize("hasAuthority('regional_countries_modify')")
    @PostMapping
    public ResponseEntity<Collection<RegionalCountryViewModel>> create(@Valid @RequestBody Collection<RegionalCountryViewModel> items) throws URISyntaxException {
        LOG.debug("REST request to add regional countries");
        final Collection<RegionalCountry> models = regionalCountryMapper.toModel(items);
        final Collection<RegionalCountry> results = regionalCountryService.create(models);
        final Collection<RegionalCountryViewModel> resultsDto = regionalCountryMapper.toViewModel(results);

        return ResponseEntity.created(new URI("/api/regional-countries/" + resultsDto.size())).body(resultsDto);

    }

    @PreAuthorize("hasAuthority('regional_countries_modify')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        LOG.debug("REST request to remove the regional country with id {}", id);
        regionalCountryService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<?> findAll(@SortDefault(sort = {"country.countryName"}, direction = Sort.Direction.ASC) Pageable pageable,
                                     @RequestParam(name = "searchFilter", required = false) final String searchFilter,
                                     @RequestParam(name = "csvExport", required = false) Boolean csvExport) {
        LOG.debug("REST request to get all regional countries");
        final Page<RegionalCountry> page = regionalCountryService.findAll(pageable, searchFilter);

        if (csvExport != null && csvExport) {
            final List<RegionalCountry> list = page.getContent();
            final List<RegionalCountryCsvExportModel> csvExportModel = regionalCountryMapper.toCsvModel(list);
            ReportDocument report = reportDocumentCreator.createCsvDocument("Regional_Countries", csvExportModel,
                RegionalCountryCsvExportModel.class, true);
            return doCreateBinaryResponse(report);
        } else {
            final Page<RegionalCountryViewModel> resultPage = new PageImplCustom<>(regionalCountryMapper.toViewModel(page),
                pageable, page.getTotalElements(), regionalCountryService.countAll());
            return ResponseEntity.ok().body(resultPage);
        }
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<RegionalCountryViewModel> getOne(@PathVariable Integer id) {
        LOG.debug("REST request to get the regional country with id {}", id);
        final RegionalCountry model = regionalCountryService.getOne(id);
        final RegionalCountryViewModel formulaDto = regionalCountryMapper.toViewModel(model);
        return Optional.ofNullable(formulaDto).map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PreAuthorize("hasAuthority('regional_countries_modify')")
    @PutMapping
    public ResponseEntity<Collection<RegionalCountryViewModel>> update(
        @Valid @RequestBody Collection<RegionalCountryViewModel> items) {
        LOG.debug("REST request to update the regional countries list");
        final Collection<RegionalCountry> models = regionalCountryMapper.toModel(items);
        final Collection<RegionalCountry> results = regionalCountryService.update(models);
        final Collection<RegionalCountryViewModel> resultsDto = regionalCountryMapper.toViewModel(results);
        return ResponseEntity.ok().body(resultsDto);
    }
}
