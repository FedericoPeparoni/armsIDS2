package ca.ids.abms.modules.charges;

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
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/service-charge-catalogues")
@SuppressWarnings({"unused", "squid:S1452"})
public class ServiceChargeCatalogueController extends MediaDocumentComponent {

    private static final Logger LOG = LoggerFactory.getLogger(ServiceChargeCatalogueController.class);

    private ServiceChargeCatalogueService serviceChargeCatalogueService;
    private ServiceChargeCatalogueMapper serviceChargeCatalogueMapper;
    private final ReportDocumentCreator reportDocumentCreator;

    public ServiceChargeCatalogueController(final ServiceChargeCatalogueService serviceChargeCatalogueService,
                                            final ServiceChargeCatalogueMapper serviceChargeCatalogueMapper,
                                            final ReportDocumentCreator reportDocumentCreator) {
        this.serviceChargeCatalogueService = serviceChargeCatalogueService;
        this.serviceChargeCatalogueMapper = serviceChargeCatalogueMapper;
        this.reportDocumentCreator = reportDocumentCreator;
    }

    @PreAuthorize("hasAuthority('service_charge_modify')")
    @PostMapping
    public ResponseEntity<ServiceChargeCatalogueViewModel> create(final @Valid @RequestBody ServiceChargeCatalogueViewModel serviceChargeCatalogueDto)
        throws URISyntaxException {
        LOG.debug("REST request to create a new ServiceChargeCatalogue");

        if (serviceChargeCatalogueDto.getId() != null) {
            return ResponseEntity.badRequest().body(null);
        }

        final ServiceChargeCatalogue serviceChargeCatalogue = serviceChargeCatalogueMapper.toModel(serviceChargeCatalogueDto);
        final ServiceChargeCatalogue result = serviceChargeCatalogueService.create(serviceChargeCatalogue);
        final ServiceChargeCatalogueViewModel resultDto = serviceChargeCatalogueMapper.toViewModel(result);

        return ResponseEntity.created(new URI("/api/service-charge-catalogues/" + result.getId())).body(resultDto);
    }

    @PreAuthorize("hasAuthority('service_charge_modify')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(final @PathVariable Integer id) {
        LOG.debug("REST request to remove the ServiceChargeCatalogue with id {}", id);
        serviceChargeCatalogueService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<?> findAll(@RequestParam(value = "textFilter", required = false) final String textFilter,
                                     @SortDefault.SortDefaults({
                                         @SortDefault(sort = {"category"}, direction = Sort.Direction.ASC),
                                         @SortDefault(sort = {"chargeClass"}, direction = Sort.Direction.ASC),
                                         @SortDefault(sort = {"subtype"}, direction = Sort.Direction.ASC),
                                         @SortDefault(sort = {"type"}, direction = Sort.Direction.ASC),
                                         @SortDefault(sort = {"description"}, direction = Sort.Direction.ASC)}) Pageable pageable,
                                     @RequestParam(name = "csvExport", required = false) Boolean csvExport) {
        LOG.debug("REST request to get all ServiceChargeCatalogues");
        final Page<ServiceChargeCatalogue> page = serviceChargeCatalogueService.findAll(textFilter, pageable);

        if (csvExport != null && csvExport) {
            final List<ServiceChargeCatalogue> list = page.getContent();
            final List<ServiceChargeCatalogueCsvExportModel> csvExportModel = serviceChargeCatalogueMapper.toCsvModel(list);
            ReportDocument report = reportDocumentCreator.createCsvDocument("Service_Charge_Catalogue", csvExportModel,
                ServiceChargeCatalogueCsvExportModel.class, true);
            return doCreateBinaryResponse(report);
        } else {
            final Page<ServiceChargeCatalogueViewModel> resultPage = new PageImplCustom<>(
                serviceChargeCatalogueMapper.toViewModel(page), pageable, page.getTotalElements(), serviceChargeCatalogueService.countAll());
            return ResponseEntity.ok().body(resultPage);
        }
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<ServiceChargeCatalogueViewModel> getOne(final @PathVariable Integer id) {
        LOG.debug("REST request to get the ServiceChargeCatalogue with id {}", id);
        final ServiceChargeCatalogue serviceChargeCatalogue = serviceChargeCatalogueService.getOne(id);
        final ServiceChargeCatalogueViewModel serviceChargeCatalogueDto = serviceChargeCatalogueMapper.toViewModel(serviceChargeCatalogue);
        return Optional.ofNullable(serviceChargeCatalogueDto)
                .map(result -> new ResponseEntity<>(
                        result,
                        HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PreAuthorize("hasAuthority('service_charge_modify')")
    @PutMapping(value = "/{id}")
    public ResponseEntity<ServiceChargeCatalogueViewModel> update(final @PathVariable Integer id,
                                                                  final @Valid @RequestBody ServiceChargeCatalogueViewModel serviceChargeCatalogueDto) {
        LOG.debug("REST request to update a ServiceChargeCatalogue with id {}", id);
        final ServiceChargeCatalogue serviceChargeCatalogue = serviceChargeCatalogueMapper.toModel(serviceChargeCatalogueDto);
        ServiceChargeCatalogue result = serviceChargeCatalogueService.update(id, serviceChargeCatalogue);
        final ServiceChargeCatalogueViewModel resultDto = serviceChargeCatalogueMapper.toViewModel(result);
        return ResponseEntity.ok().body(resultDto);
    }
}
