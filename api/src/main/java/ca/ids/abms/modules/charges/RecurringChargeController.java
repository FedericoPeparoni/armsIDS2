package ca.ids.abms.modules.charges;

import ca.ids.abms.modules.common.controllers.MediaDocumentComponent;
import ca.ids.abms.modules.reports2.common.ReportDocument;
import ca.ids.abms.modules.reports2.common.ReportDocumentCreator;
import ca.ids.abms.modules.util.models.PageImplCustom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@RestController
@RequestMapping("/api/recurring-charges")
@SuppressWarnings({"unused", "squid:S1452"})
public class RecurringChargeController extends MediaDocumentComponent {

    private static final Logger LOG = LoggerFactory.getLogger(RecurringChargeController.class);
    private final RecurringChargeService recurringChargeService;
    private final RecurringChargeMapper recurringChargeMapper;
    private final ReportDocumentCreator reportDocumentCreator;

    public RecurringChargeController(final RecurringChargeService recurringChargeService,
                                     final RecurringChargeMapper recurringChargeMapper,
                                     final ReportDocumentCreator reportDocumentCreator) {
        this.recurringChargeService = recurringChargeService;
        this.recurringChargeMapper = recurringChargeMapper;
        this.reportDocumentCreator = reportDocumentCreator;
    }

    @GetMapping
    public ResponseEntity<?> findAll(@RequestParam(name = "accountFilter", required = false) final Integer account,
                                     @RequestParam(name = "statusFilter", required = false) final String status,
                                     @RequestParam(name = "textFilter", required = false) final String textSearch,
                                     @SortDefault.SortDefaults({
                                        @SortDefault(sort = {"account.name"}),
                                        @SortDefault(sort = {"serviceChargeCatalogue.chargeClass"}),
                                        @SortDefault(sort = {"serviceChargeCatalogue.invoiceCategory"}),
                                        @SortDefault(sort = {"serviceChargeCatalogue.type"}),
                                        @SortDefault(sort = {"serviceChargeCatalogue.subtype"}),
                                        @SortDefault(sort = {"serviceChargeCatalogue.description"}),
                                     }) Pageable pageable,
                                     @RequestParam(name = "csvExport", required = false) Boolean csvExport) {

        LOG.debug("REST request to get All RecurringCharges by filters: textSearch: {}, account id: {}, status: {}", textSearch, account, status);
        final Page<RecurringCharge> page = recurringChargeService.findAllByFilters(textSearch, status, account, pageable);

        if (csvExport != null && csvExport) {
            final List<RecurringCharge> list = page.getContent();
            final List<RecurringChargeCsvExportModel> csvExportModel = recurringChargeMapper.toCsvModel(list);
            ReportDocument report = reportDocumentCreator.createCsvDocument("Recurring_Charges", csvExportModel,
                RecurringChargeCsvExportModel.class, true);
            return doCreateBinaryResponse(report);
        } else {
            final Page<RecurringChargeViewModel> resultPage = new PageImplCustom<>(recurringChargeMapper.toViewModel(page), pageable, page.getTotalElements(),
                recurringChargeService.countAll());
            return ResponseEntity.ok().body(resultPage);
        }
    }

    @PreAuthorize("hasAuthority('charges_modify')")
    @PostMapping
    public ResponseEntity<RecurringChargeViewModel> create(@Valid @RequestBody final RecurringChargeViewModel recurringCharge) throws URISyntaxException {

        if (recurringCharge.getId() != null) {
            return ResponseEntity.badRequest().body(null);
        }

        RecurringCharge result = recurringChargeService.create(recurringChargeMapper.toModel(recurringCharge));
        RecurringChargeViewModel viewModel = recurringChargeMapper.toViewModel(result);
        return ResponseEntity.created(new URI("/api/recurring-charges/" + result.getId())).body(viewModel);
    }

    @PreAuthorize("hasAuthority('charges_modify')")
    @PutMapping(value = "/{id}")
    public ResponseEntity<RecurringChargeViewModel> update(@Valid @RequestBody final RecurringChargeViewModel recurringCharge) throws URISyntaxException {

        if (recurringCharge.getId() == null) {
            return ResponseEntity.badRequest().body(null);
        }

        RecurringCharge result = recurringChargeService.update(recurringCharge.getId(), recurringChargeMapper.toModel(recurringCharge));
        RecurringChargeViewModel viewModel = recurringChargeMapper.toViewModel(result);
        return ResponseEntity.created(new URI("/api/recurring-charges/" + result.getId())).body(viewModel);
    }

    @PreAuthorize("hasAuthority('charges_modify')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        recurringChargeService.delete(id);
        return ResponseEntity.ok().build();
    }

}
