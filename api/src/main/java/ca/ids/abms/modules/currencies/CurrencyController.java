package ca.ids.abms.modules.currencies;

import ca.ids.abms.config.error.CustomParametrizedException;
import ca.ids.abms.config.error.ErrorConstants;
import ca.ids.abms.modules.accounts.AccountService;
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
@RequestMapping("/api/currencies")
@SuppressWarnings({"unused", "squid:S1452"})
public class CurrencyController extends MediaDocumentComponent {

    private static final Logger LOG = LoggerFactory.getLogger(CurrencyController.class);
    private final CurrencyService currencyService;
    private final AccountService accountService;
    private final CurrencyMapper currencyMapper;
    private final ReportDocumentCreator reportDocumentCreator;

    public CurrencyController(final CurrencyService currencyService,
                              final AccountService accountService,
                              final CurrencyMapper currencyMapper,
                              final ReportDocumentCreator reportDocumentCreator) {
        this.currencyService = currencyService;
        this.accountService = accountService;
        this.currencyMapper = currencyMapper;
        this.reportDocumentCreator = reportDocumentCreator;
    }

    @PreAuthorize("hasAuthority('currency_modify')")
    @PostMapping
    public ResponseEntity<CurrencyViewModel> createCurrency(@Valid @RequestBody final CurrencyViewModel currency) throws URISyntaxException {
        LOG.debug("REST request to save Currency : {}", currency);

        if (currency.getCurrencyCode() == null) {
            return ResponseEntity.badRequest().body(null);
        }
        Currency result;
        CurrencyViewModel viewModel;
        try {
            result = currencyService.create(currencyMapper.toModel(currency));
            viewModel = currencyMapper.toViewModel(result);
        } catch (DataIntegrityViolationException ex) {
            throw new CustomParametrizedException(ErrorConstants.ERR_UNIQUENESS_VIOLATION, ex, "currency_code");
        }
        return ResponseEntity.created(new URI("/api/currencies/" + result.getCurrencyCode()))
                .body(viewModel);
    }

    @PreAuthorize("hasAuthority('currency_modify')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteCurrency(@PathVariable final Integer id) {
        LOG.debug("REST request to delete Currency : {}", id);

        try {
            currencyService.delete(id);
        } catch (DataIntegrityViolationException ex) {
            throw new CustomParametrizedException(ErrorConstants.ERR_DEPENDENCY_VIOLATION, ex);
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<?> getAllCurrencies(@RequestParam(required = false) final String filter, // Only one "filter" is supported, "active"
                                              @RequestParam(required = false) final String textSearch,
                                              @SortDefault(sort = {"currencyCode"}, direction = Sort.Direction.ASC)
                                                      Pageable pageable,
                                              @RequestParam(name = "csvExport", required = false) Boolean csvExport) {
        LOG.debug("REST request to get all Currencies");
        final boolean activeOnly = filter != null && filter.equalsIgnoreCase("active");
        final Page<Currency> page = currencyService.findAll(activeOnly, textSearch, pageable);

        if (csvExport != null && csvExport) {
            final List<Currency> list = page.getContent();
            final List<CurrencyCsvExportModel> csvExportModel = currencyMapper.toCsvModel(list);
            ReportDocument report = reportDocumentCreator.createCsvDocument("Currencies", csvExportModel,
                CurrencyCsvExportModel.class, true);
            return doCreateBinaryResponse(report);
        } else {
            final Page<CurrencyViewModel> resultPage = new PageImplCustom<>(currencyMapper.toViewModel(page), pageable,
                page.getTotalElements(), currencyService.countAll());
            return ResponseEntity.ok().body(resultPage);
        }
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Currency> getCurrency(@PathVariable final Integer id) {
        LOG.debug("REST request to get Currency : {}", id);

        Currency currency = currencyService.getOne(id);

        return Optional.ofNullable(currency)
                .map(result -> new ResponseEntity<>(
                        result,
                        HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    @GetMapping(value = "/{id}/info")
    public ResponseEntity<CurrencyInfoViewModel> getCurrencyInfo(@PathVariable final Integer id) {
        final CurrencyInfoViewModel x = new CurrencyInfoViewModel();
        x.setCurrencyId(id);
        x.setRefAccounts(accountService.getTopNamesByInvoiceCurrency(id, 4));
        x.setRefAccountTotal(accountService.countByCurrency(id));
        x.setUsedAsExchangeTargetByAnotherActiveCurrency (currencyService.isUsedAsExchangeTargetByAnotherActiveCurrency(id));
        return ResponseEntity.ok(x);
    }

    @GetMapping("/ansp")
    public Page<Currency> getANSPCurrencyAndUSD() {
        LOG.debug("REST request to get ANSP Currency as well as USD");
        return currencyService.getANSPCurrencyAndUSD();
    }

    @GetMapping("/ansp-usd-list")
    public List<Currency> getListCurrencyANSPAndUSD() {
        LOG.debug("REST request to get ANSP Currency as well as USD");
        return currencyService.getANSPCurrencyAndUSD().getContent();
    }

    @PreAuthorize("hasAuthority('currency_modify')")
    @PutMapping(value = "/{id}")
    public ResponseEntity<CurrencyViewModel> updateCurrency(@RequestBody final CurrencyViewModel currency, @PathVariable final Integer id) {
        LOG.debug("REST request to update Currency : {}, Code: {}", currency, id);
        Currency result = currencyService.update(id, currencyMapper.toModel(currency));
        CurrencyViewModel viewModel = currencyMapper.toViewModel(result);

        return ResponseEntity.ok().body(viewModel);
    }

    @GetMapping(value = "/currency-code/{code}")
    public ResponseEntity<Currency> getOneByCurrencyCode(@PathVariable final String code) {
        LOG.debug("REST request to get Currency by currency code : {}", code);
        Currency currency = currencyService.findByCurrencyCode(code);
        return Optional.ofNullable(currency)
            .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));

    }

    @GetMapping(value = "/active")
    public List<Currency> getActiveCurrency() {
        LOG.debug("REST request to get all active Currencies");
        return currencyService.getActiveCurrency();
    }
}
