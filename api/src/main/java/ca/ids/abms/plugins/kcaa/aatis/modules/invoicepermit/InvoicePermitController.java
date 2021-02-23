package ca.ids.abms.plugins.kcaa.aatis.modules.invoicepermit;

import ca.ids.abms.modules.currencies.Currency;
import ca.ids.abms.modules.reports2.common.CachedCurrencyConverter;
import ca.ids.abms.modules.util.models.CurrencyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("api/aatis_invoice_permits")
public class InvoicePermitController {

    private static final Logger LOG = LoggerFactory.getLogger(InvoicePermitController.class);
    private final InvoicePermitService invoicePermitService;
    private final CachedCurrencyConverter cachedCurrencyConverter;

    public InvoicePermitController(final InvoicePermitService invoicePermitService,
                                   final CurrencyUtils currencyUtils) {
        this.invoicePermitService = invoicePermitService;
        this.cachedCurrencyConverter = new CachedCurrencyConverter (currencyUtils, LocalDateTime.now());
    }

    @GetMapping
    public ResponseEntity<InvoicePermit> findInvoicePermitByPermitNumber(@RequestParam(name = "permitNumber") String permitNumber,
                                                                         @RequestParam(name = "currencyCode", required = false) String currencyCode) {
        LOG.debug("REST request to get InvoicePermit By PermitNumber: {}", permitNumber);
        InvoicePermit invoicePermit = invoicePermitService.findByAdhocPermitNumber(permitNumber);

        if (currencyCode != null && invoicePermit != null) {
            // adhoc fee amount is in USD currency by default
            Currency invoicePermitCurrency = cachedCurrencyConverter.getCurrencyByCode(currencyCode);
            Currency usdCurrency = cachedCurrencyConverter.getUsdCurrency();

            if (invoicePermitCurrency != null && invoicePermit.getAdhocFee() != null) {
                invoicePermit.setAdhocFeeCurrencyCode(usdCurrency.getCurrencyCode());
                invoicePermit.setAdhocFeeConverted(cachedCurrencyConverter.convertCurrency(invoicePermit.getAdhocFee(), usdCurrency, invoicePermitCurrency));
                invoicePermit.setAdhocFeeConvertedCurrencyCode(currencyCode);
            }
        }

        return ResponseEntity.ok().body(invoicePermit);
    }

}
