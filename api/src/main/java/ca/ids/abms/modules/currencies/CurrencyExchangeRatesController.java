package ca.ids.abms.modules.currencies;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import ca.ids.abms.modules.system.BillingOrgCode;
import ca.ids.abms.modules.system.SystemConfigurationService;
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

import ca.ids.abms.config.error.CustomParametrizedException;
import ca.ids.abms.config.error.ErrorConstants;
import ca.ids.abms.config.error.ExceptionFactory;
import ca.ids.abms.modules.common.mappers.DateTimeMapperUtils;

@RestController
@RequestMapping("/api/currency-exchange-rates")
@SuppressWarnings("unused")
public class CurrencyExchangeRatesController {

    private static final Logger LOG = LoggerFactory.getLogger(CurrencyExchangeRatesController.class);

    private final CurrencyExchangeRateService currencyExchangeRateService;
    private final CurrencyService currencyService;
    private final SystemConfigurationService systemConfigurationService;

    public CurrencyExchangeRatesController(
        final CurrencyExchangeRateService currencyExchangeRateService,
        final CurrencyService currencyService,
        final SystemConfigurationService systemConfigurationService
    ) {
        this.currencyExchangeRateService = currencyExchangeRateService;
        this.currencyService = currencyService;
        this.systemConfigurationService = systemConfigurationService;
    }

    @GetMapping
    public List<CurrencyExchangeRate> getAllValidCurrencyExchangeRates() {
        LOG.debug("REST request to get all valid currency exchange rates");
        return currencyExchangeRateService.getAllValidCurrencyExchangeRates();
    }

    @PreAuthorize("hasAuthority('currency_modify')")
    @PostMapping
    public ResponseEntity<CurrencyExchangeRate> create(@Valid @RequestBody CurrencyExchangeRate currencyExchangeRate) throws URISyntaxException {
        LOG.debug("REST request to save CurrencyExchangeRate : {}", currencyExchangeRate);
        Integer currencyId = currencyExchangeRate.getCurrency().getId();
        Currency c = currencyService.getOne(currencyId);
        if (c != null && c.getCurrencyCode().equals("USD")) {
            throw ExceptionFactory.getInvalidCurrencyUpdating (CurrencyExchangeRatesController.class);
        }
        final CurrencyExchangeRate result = currencyExchangeRateService.create(currencyExchangeRate);
        return ResponseEntity.created(new URI("/api/currency-exchange-rates/" + result.getId()))
                .body(result);
    }

    @PreAuthorize("hasAuthority('currency_modify')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        LOG.debug("REST request to delete CurrencyExchangeRate : {}", id);
        try {
            CurrencyExchangeRate currencyExchangeRate = currencyExchangeRateService.getOne(id);
            Integer currencyId = currencyExchangeRate.getCurrency().getId();
            Currency c = currencyService.getOne(currencyId);
            if (c != null && c.getCurrencyCode().equals("USD")) {
                throw ExceptionFactory.getInvalidCurrencyUpdating(CurrencyExchangeRatesController.class);
            }

            currencyExchangeRateService.delete(id);
        } catch (DataIntegrityViolationException ex) {
            throw new CustomParametrizedException(ErrorConstants.ERR_DEPENDENCY_VIOLATION, ex);
        } catch (RuntimeException e) {
            throw ExceptionFactory.persistenceDataManagement(e, ErrorConstants.ERR_DELETE_NO_LONGER_EXISTS);
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/for-currency/{currencyId}")
    public Page<CurrencyExchangeRate> getAll(
            @PathVariable final Integer currencyId,
            // date columns are not not unique, so we need to sort by ID as well, otherwise row order would be unstable
            @SortDefault(sort = {"exchangeRateValidFromDate", "exchangeRateValidToDate", "id"}, direction = Sort.Direction.DESC) final Pageable pageable) {
        LOG.debug("REST request to get currencyExchangeRates for currencyId {} pageable=[{}]", currencyId, pageable);
        return currencyExchangeRateService.getAll(currencyId, pageable);
    }

    @GetMapping(value = "/all-currency/{currencyId}")
    public List<CurrencyExchangeRate> getAll(
            @PathVariable final Integer currencyId,
            // date columns are not not unique, so we need to sort by ID as well, otherwise row order would be unstable
            @SortDefault(sort = {"exchangeRateValidFromDate", "exchangeRateValidToDate", "id"}, direction = Sort.Direction.DESC) final Sort sort) {
        LOG.debug("REST request to get currency exchange rates for currencyId={} sort=[{}]", currencyId, sort);
        return currencyExchangeRateService.getAll (currencyId, sort);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<CurrencyExchangeRate> getOne(@PathVariable Integer id) {
        LOG.debug("REST request to get CurrencyExchangeRate : {}", id);

        CurrencyExchangeRate currencyExchangeRate = currencyExchangeRateService.getOne(id);

        return Optional.ofNullable(currencyExchangeRate)
                .map(result -> new ResponseEntity<>(
                        result,
                        HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping(value = "/for-currency/{fromCurrencyId}/to/{toCurrencyId}")
    public ResponseEntity<Double> getExchangeRate(
        @PathVariable Integer fromCurrencyId, @PathVariable Integer toCurrencyId,
        @RequestParam(required = false) String datetime) {

        LOG.debug("REST request to get exchange rate from currency id {} to currency id {} on {}.",
            fromCurrencyId, toCurrencyId, datetime);

        // find from and to currencies
        Currency fromCurrency = currencyService.getOne(fromCurrencyId);
        Currency toCurrency = currencyService.getOne(toCurrencyId);

        // if either is null, throw exception
        if (fromCurrency == null || toCurrency == null) {
            LOG.debug("Could not find currency with id {} or {}.",
                fromCurrencyId, toCurrencyId);
            throw new CustomParametrizedException("Could not find the currency requested.");
        }

        // if date param not null, parse as datetime
        // else default to today's date
        LocalDateTime dateTimeFilter;
        if (datetime != null)
            dateTimeFilter = DateTimeMapperUtils.parseISODateTime(datetime);
        else
            dateTimeFilter = LocalDateTime.now();

        // EANA expects to use the previous day of payment dates for exchange rates
        if (BillingOrgCode.EANA == systemConfigurationService.getBillingOrgCode())
            dateTimeFilter = dateTimeFilter.minusDays(1);

        // find to exchange rate
        double result = currencyExchangeRateService.getExchangeRate(fromCurrency, toCurrency, dateTimeFilter);

        // if result is 0d, return status code 404
        // else return result cast as Double class with status code 200
        if (result == 0d) {
            LOG.debug("Could not find an exchange rate for currency id {} to currency id {} on {}.",
                fromCurrencyId, toCurrencyId, datetime);
            throw new CustomParametrizedException(
                String.format(
                    "Could not find exchange rate from (%s) to (%s) on (%s)",
                fromCurrency.getCurrencyCode(), toCurrency.getCurrencyCode(), dateTimeFilter));
        } else {
            return new ResponseEntity<>(result, HttpStatus.OK);
        }
    }

    /**
     * A simple request to get exchange amount following system rounding logic.
     *
     * This should NOT perform any repository requests as it will be called frequently.
     *
     * @param exchangeRate exchange rate
     * @param fromAmount amount to exchange
     * @param toDecimalPoints decimal points to round
     * @param inverse inverse exchange rate (divide instead of multiple)
     * @return exchanged amount
     */
    @GetMapping(value = "/for-exchange-rate/{exchangeRate}/from-amount/{fromAmount}/rounded-to/{toDecimalPoints}")
    public ResponseEntity<Double> getExchangeAmount(@PathVariable Double exchangeRate, @PathVariable Double fromAmount,
                                                    @PathVariable Integer toDecimalPoints,
                                                    @RequestParam(required = false, defaultValue = "false") Boolean inverse) {

        LOG.debug("REST request to get exchange amount for exchange rate {} from amount {} rounded to {} points.",
            exchangeRate, fromAmount, toDecimalPoints);

        return new ResponseEntity<>(currencyExchangeRateService.getExchangeAmount(
            exchangeRate, fromAmount, toDecimalPoints, inverse), HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('currency_modify')")
    @PutMapping(value = "/{id}")
    public ResponseEntity<CurrencyExchangeRate> updateCurrency(@RequestBody CurrencyExchangeRate currencyExchangeRate, @PathVariable Integer id) {
        LOG.debug("REST request to update CurrencyExchangeRate : {}, Code: {}", currencyExchangeRate, id);
        Currency c = currencyExchangeRate.getCurrency();
        if (c != null && c.getCurrencyCode().equals("USD")) {
            throw ExceptionFactory.getInvalidCurrencyUpdating (CurrencyExchangeRatesController.class);
        }
        CurrencyExchangeRate result = currencyExchangeRateService.update(id, currencyExchangeRate);

        return ResponseEntity.ok().body(result);
    }

    @PreAuthorize("hasAuthority('currency_modify')")
    @PutMapping(value = "/update-all")
    public ResponseEntity<Void> updateAllCurrency() {
        LOG.debug("REST request to update all currencies");
        this.currencyExchangeRateService.updateAll();
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/for-currency-code/{currencyCode}")
    public ResponseEntity<Double> getCurrentExchangeRatesByCurrencyCode(
        @PathVariable String currencyCode) {

        return new ResponseEntity<>(this.currencyExchangeRateService.getCurrentExchangeRatesByCurrencyCode(currencyCode), HttpStatus.OK);
    }
}
