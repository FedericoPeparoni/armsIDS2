package ca.ids.abms.modules.currencies;

import ca.ids.abms.config.error.ErrorConstants;
import ca.ids.abms.config.error.ExceptionFactory;
import ca.ids.abms.modules.util.models.Calculation;
import ca.ids.abms.modules.util.models.ModelUtils;
import ca.ids.oxr.client.dto.Context;
import ca.ids.oxr.client.dto.LatestExchangeRates;
import ca.ids.oxr.client.service.OpenExchangeRatesService;
import com.google.common.base.Preconditions;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class CurrencyExchangeRateService {

    private static final Logger LOG = LoggerFactory.getLogger(CurrencyExchangeRateService.class);

    @SuppressWarnings("unused")
    @Value("${abms.oxr.client.appId}")
    private String openExchangeRatesAppId;

    private CurrencyRepository currencyRepository;

    private CurrencyExchangeRateRepository currencyExchangeRateRepository;

    private OpenExchangeRatesService openExchangeRatesService;

    public CurrencyExchangeRateService(CurrencyRepository currencyRepository,
                                       CurrencyExchangeRateRepository currencyExchangeRateRepository,
                                       OpenExchangeRatesService openExchangeRatesService) {
        this.currencyRepository = currencyRepository;
        this.currencyExchangeRateRepository = currencyExchangeRateRepository;
        this.openExchangeRatesService = openExchangeRatesService;
    }

    public void delete(Integer id) {
        LOG.debug("Request to delete CurrencyExchangeRate : {}", id);
        try {
            currencyExchangeRateRepository.delete(id);
        } catch (RuntimeException e) {
            throw ExceptionFactory.persistenceDataManagement(e,ErrorConstants.ERR_DELETE_NO_LONGER_EXISTS);
        }
    }

    @Transactional(readOnly = true)
    public Page<CurrencyExchangeRate> getAll(final Integer currencyId, final Pageable pageable) {
        LOG.debug("Request to get currency exchange rates by currency id {}", currencyId);
        return currencyExchangeRateRepository.getAllCurrencyExchangeRateByCurrencyId(currencyId, pageable);
    }

    @Transactional(readOnly = true)
    public List<CurrencyExchangeRate> getAll(final Integer currencyId, final Sort sort) {
        LOG.debug("Request to get currency exchange rates by currency id {}", currencyId);
        return currencyExchangeRateRepository.getAllCurrencyExchangeRateByCurrencyId (currencyId, sort);
    }

    @Transactional(readOnly = true)
    public List<CurrencyExchangeRate> getAllValidCurrencyExchangeRates() {
        final LocalDate now = LocalDate.now();
        final LocalDateTime startAt = now.atStartOfDay();
        final LocalDateTime endAt = now.atTime(LocalTime.MAX);
        LOG.debug("Request to get all valid currency exchange rates");
        return currencyExchangeRateRepository.getAllCurrencyExchangeRateDoNotOverlap(startAt, endAt);
    }

    /**
     * Get applicable exchange rate from source currency to target currency on a particular date.
     */
    @Transactional(readOnly = true)
    public CurrencyExchangeRate getApplicableRate(final Currency source, final Currency target, final LocalDateTime date) {
        Preconditions.checkNotNull(source, "source currency cannot be null");
        Preconditions.checkNotNull(target, "target currency cannot be null");
        Preconditions.checkNotNull(date, "exchange rate date cannot be null");

        LOG.debug("Request to get currency exchange rate from '{}' to '{}' applicable on '{}'",
            source.getCurrencyCode(), target.getCurrencyCode(), date);
        List<CurrencyExchangeRate> rates = currencyExchangeRateRepository.getApplicableCurrencyExchangeRate(
            source.getId(), target.getId(), date);
        return CollectionUtils.isNotEmpty(rates) ? rates.get(0) : null;
    }

    /**
     * Get applicable exchange rate from currency to USD on a particular date.
     */
    @Transactional(readOnly = true)
    public CurrencyExchangeRate getApplicableRateToUsd(final Currency currency, final LocalDateTime date) {
        LOG.debug("Request to get currency exchange rate from '{}' to 'USD' applicable on '{}'",
            currency.getCurrencyCode(), date);
        List<CurrencyExchangeRate> rates = currencyExchangeRateRepository.getApplicableCurrencyExchangeRateToUsd(
            currency.getId(), date);
        return CollectionUtils.isNotEmpty(rates) ? rates.get(0) : null;
    }

    /**
     * Get exchange rate from source currency to target currency either by target currency exchange rate
     * or USD exchange rate conversion.
     *
     * NOTE: Update `CurrencyUtils.getApplicableRate(Currency, Currency, LocalDateTime)` if this method
     * is modified.
     */
    @SuppressWarnings("squid:S2234")
    @Transactional(readOnly = true)
    public double getExchangeRate (final Currency source, final Currency target, final LocalDateTime date) {

        // return 1 if currencies are the same
        if (source != null && source.equals(target))
            return 1d;

        // attempt to get exchange rate from target currency
        final CurrencyExchangeRate x = getApplicableRate(source, target, date);
        if (x != null && x.getExchangeRate() != null && x.getExchangeRate() >= 0d)
            return x.getExchangeRate();

        // attempt to get exchange rate from target exchange currency conversion
        // inverse exchange rate value MUST be great then zero to divide by
        //
        final CurrencyExchangeRate xinverse = getApplicableRate(target, source, date);
        if (xinverse != null && xinverse.getExchangeRate() != null && xinverse.getExchangeRate() > 0d)
            return 1 / xinverse.getExchangeRate();

        // attempt to get exchange rate from usd exchange currency conversion
        // to currency usd exchange rate must be greater then zero to divide by
        final CurrencyExchangeRate xfrom = this.getApplicableRateToUsd(source, date);
        if (xfrom == null || xfrom.getExchangeRate() == null || xfrom.getExchangeRate() < 0d)
            return 0d;

        final CurrencyExchangeRate xto = this.getApplicableRateToUsd(target, date);
        if (xto == null || xto.getExchangeRate() == null || xto.getExchangeRate() <= 0d)
            return 0d;

        return xfrom.getExchangeRate() / xto.getExchangeRate();
    }

    public double getExchangeAmount(final double exchangeRate, final double fromAmount, final int toDecimalPoints) {
        return this.getExchangeAmount(exchangeRate, fromAmount, toDecimalPoints, false);
    }

    public double getExchangeAmount(final double exchangeRate, final double fromAmount, final int toDecimalPoints,
                                    final boolean inverse) {

        // calculate exchange amount from exchange rate and from amount
        // if inverse true, divide exchange rate instead of multiplying
        double result;
        if (inverse)
            result = fromAmount / exchangeRate;
        else
            result = fromAmount * exchangeRate;

        // return truncated amount to supplied decimal points
        return Calculation.truncate(result, toDecimalPoints);
    }

    @Transactional(readOnly = true)
    public CurrencyExchangeRate getOne(final Integer id) {
        LOG.debug("Request to get currency exchange rates by id {}", id);
        return currencyExchangeRateRepository.getOne(id);
    }

    public CurrencyExchangeRate create(final CurrencyExchangeRate currencyExchangeRate) {
        LOG.debug("Request to create CurrencyExchangeRate : {}", currencyExchangeRate);
        checkTheTemporalInterval(currencyExchangeRate.getExchangeRateValidFromDate(),
            currencyExchangeRate.getExchangeRateValidToDate());

        final List<CurrencyExchangeRate> itemsThatMustNotBeOverlapped;

        itemsThatMustNotBeOverlapped = currencyExchangeRateRepository.getAllCurrencyExchangeRateDoNotOverlap(currencyExchangeRate.getCurrency().getId(),
            currencyExchangeRate.getExchangeRateValidFromDate(),
            currencyExchangeRate.getExchangeRateValidToDate());

        if (CollectionUtils.isEmpty(itemsThatMustNotBeOverlapped)) {
            /*
             * No one records to overlap has been found
             */
            currencyExchangeRate.setCurrency(currencyRepository.getOne(currencyExchangeRate.getCurrency().getId()));
            return currencyExchangeRateRepository.save(currencyExchangeRate);
        } else {
            throw ExceptionFactory.getOverlappedCurrencyRateException(CurrencyExchangeRate.class,
                "exchangeRateValidFromDate", "exchangeRateValidToDate");
        }
    }

    public CurrencyExchangeRate update(Integer id, CurrencyExchangeRate currencyExchangeRate) {
        LOG.debug("Request to update CurrencyExchangeRate : {}", currencyExchangeRate);
        checkTheTemporalInterval(currencyExchangeRate.getExchangeRateValidFromDate(),
                currencyExchangeRate.getExchangeRateValidToDate());
        try {
            final CurrencyExchangeRate existingExchangeRate = currencyExchangeRateRepository.getOne(id);
            List<CurrencyExchangeRate> itemsThatMustNotBeOverlapped = null;
            if (currencyExchangeRate.getExchangeRateValidFromDate() != null
                    && currencyExchangeRate.getExchangeRateValidToDate() != null) {

                itemsThatMustNotBeOverlapped = currencyExchangeRateRepository.getAllCurrencyExchangeRateDoNotOverlap(
                    existingExchangeRate.getCurrency().getId(),
                    currencyExchangeRate.getExchangeRateValidFromDate(),
                    currencyExchangeRate.getExchangeRateValidToDate());
            }
            if (CollectionUtils.isEmpty(itemsThatMustNotBeOverlapped) || (itemsThatMustNotBeOverlapped.size() == 1
                    && id.equals(itemsThatMustNotBeOverlapped.get(0).getId()))) {
                /*
                 * The only one record to update without overlapping others
                 * records will be updated
                 */
                ModelUtils.merge(currencyExchangeRate, existingExchangeRate);
                existingExchangeRate.setCurrency(currencyRepository.getOne(currencyExchangeRate.getCurrency().getId()));
                return currencyExchangeRateRepository.save(existingExchangeRate);
            } else {
                throw ExceptionFactory.getOverlappedCurrencyRateException(CurrencyExchangeRate.class,
                        "exchangeRateValidFromDate", "exchangeRateValidToDate");
            }
        } catch (RuntimeException e) {
            throw ExceptionFactory.persistenceDataManagement(e,ErrorConstants.ERR_UPDATE_NO_LONGER_EXISTS);
        }
    }

    public void updateAll() {
        LOG.debug("Request to update all Currency Exchange Rates");
        final Currency usd = currencyRepository.findByCurrencyCode("USD");
        final Context context = new Context();
        context.setAppId(openExchangeRatesAppId);
        context.setBase("USD");
        final LatestExchangeRates latestExchangeRates = openExchangeRatesService.getLatestExchangeRates(context);

        if (latestExchangeRates == null || latestExchangeRates.getRates() == null) {
            throw new RuntimeException("Cannot retrieve the exchange rates, the service isn't working properly");
        }

        final List<Currency> currencyRecords = currencyRepository.findAll();
        int updates = 0;
        int insertions = 0;
        int discarded = 0;
        final LocalDate now = LocalDate.now();
        final LocalDateTime startAt = now.atStartOfDay();
        final LocalDateTime endAt = now.atTime(LocalTime.MAX);
        final List<CurrencyExchangeRate> listOfRatesInOverlapping = currencyExchangeRateRepository
            .getAllCurrencyExchangeRateDoNotOverlap(startAt, endAt);
        final Map<Integer, CurrencyExchangeRate> currencyRatesThatMustNotBeOverlapped;
        if (listOfRatesInOverlapping != null) {
            currencyRatesThatMustNotBeOverlapped = listOfRatesInOverlapping.stream()
                .collect(Collectors.toMap(rt -> rt.getCurrency().getId(), rt -> rt));
        } else {
            currencyRatesThatMustNotBeOverlapped = MapUtils.EMPTY_MAP;
        }
        for (final Currency currency : currencyRecords) {
            if (!currency.getCurrencyCode().equals("USD") && currency.getAllowUpdatedFromWeb() &&
                latestExchangeRates.getRates().containsKey(currency.getCurrencyCode())) {

                /*
                 * An ex.rate has been found for a given currency
                 */
                final double rate = latestExchangeRates.getRates().get(currency.getCurrencyCode());

                /*
                 * Find the exact record to update
                 */
                final CurrencyExchangeRate exactExchangeRateToUpdate = currencyExchangeRateRepository
                    .getCurrencyExchangeRateByCurrencyIdAndPeriod(currency.getId(), startAt, endAt);
                if (exactExchangeRateToUpdate != null) {
                    /*
                     * For the given currency here will be updated an existing exchange rate
                     */
                    exactExchangeRateToUpdate.setExchangeRate(1.0D / rate);
                    currencyExchangeRateRepository.save(exactExchangeRateToUpdate);
                    updates++;
                } else if (!currencyRatesThatMustNotBeOverlapped.containsKey(currency.getId())){
                    /* For the given currency here will be created an exchange rate if it doesn't overlap any other
                     * existing rate
                     */
                    final CurrencyExchangeRate newExchangeRate = new CurrencyExchangeRate();
                    newExchangeRate.setCurrency(currency);
                    newExchangeRate.setExchangeRateValidFromDate(startAt);
                    newExchangeRate.setExchangeRateValidToDate(endAt);
                    newExchangeRate.setExchangeRate(1.0D / rate);
                    newExchangeRate.setTargetCurrency(usd);
                    currencyExchangeRateRepository.save(newExchangeRate);
                    insertions++;
                } else {
                    /*
                     * Count the exchange rates discarded because an overlapping can occour.
                     */
                    discarded++;
                }
            }
        }
        LOG.debug("{} currency rates have been inserted, {} have been updated and {} have been discarded", insertions, updates, discarded);
        currencyExchangeRateRepository.flush();
    }

    /**
     * Get applicable currency exchange rate by currency code to 'USD' on current date.
     */
    @Transactional(readOnly = true)
    public Double getCurrentExchangeRatesByCurrencyCode(String currencyCode) {
        return currencyExchangeRateRepository.getCurrentExchangeRatesByCurrencyCode(currencyCode);
    }

    private void checkTheTemporalInterval(final LocalDateTime from, final LocalDateTime to) {
        if (to.isBefore(from)) {
            throw ExceptionFactory.getStartEndDateException(CurrencyExchangeRate.class,
                "exchangeRateValidFromDate",
                "exchangeRateValidToDate");
        }
    }
}
