package ca.ids.abms.modules.currencies;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityNotFoundException;

import ca.ids.abms.util.billingcontext.BillingContext;
import ca.ids.abms.util.billingcontext.BillingContextKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.ids.abms.config.db.Filter;
import ca.ids.abms.config.db.FiltersSpecification;
import ca.ids.abms.config.error.ErrorConstants;
import ca.ids.abms.config.error.ExceptionFactory;
import ca.ids.abms.modules.util.models.ModelUtils;

@Service
@Transactional
public class CurrencyService {

    private static final Logger LOG = LoggerFactory.getLogger(CurrencyService.class);

    @Value("${abms.oxr.client.appId}")
    private String openExchangeRatesAppId;

    private CurrencyRepository currencyRepository;

    public CurrencyService(CurrencyRepository currencyRepository) {
        this.currencyRepository = currencyRepository;
    }

    public void delete(Integer id) {
        LOG.debug("Request to delete Currency : {}", id);
        try {
            currencyRepository.delete(id);
        } catch (EntityNotFoundException e) {
            throw ExceptionFactory.persistenceDataManagement(e,ErrorConstants.ERR_DELETE_NO_LONGER_EXISTS);
        }
    }

    @Transactional(readOnly = true)
    public Page<Currency> findAll(final boolean activeOnly, final String textSearch, final Pageable pageable) {
        LOG.debug("Request to get currencies: activeOnly={}, filter=[{}]", activeOnly, textSearch);
        final FiltersSpecification.Builder filterBuilder = new FiltersSpecification
                .Builder()
                .lookFor (textSearch);
        if (activeOnly) {
            filterBuilder.restrictOn(Filter.equals("active", activeOnly));
        }
        return currencyRepository.findAll(filterBuilder.build(), pageable);
    }

    @Transactional(readOnly = true)
    public Currency getOne(Integer id) {
        LOG.debug("Request to get Currency : {}", id);
        return currencyRepository.getOne(id);
    }

    @Transactional(readOnly = true)
    public Currency findOne(Integer id) {
        LOG.debug("Request to find Currency : {}", id);
        return currencyRepository.findOne(id);
    }

    public Currency create(Currency currency) {
        LOG.debug("Request to save Currency : {}", currency);
        return currencyRepository.save(currency);
    }

    public Currency update(Integer id, Currency currency) {
        LOG.debug("Request to update Currency : {}", currency);
        Currency c = null;
        try {
            Currency existingCurrency = currencyRepository.getOne(id);
            LOG.debug("existingCurrency : {}", existingCurrency);
            ModelUtils.merge(currency, existingCurrency);
            c = currencyRepository.save(existingCurrency);
        } catch (EntityNotFoundException e) {
            throw ExceptionFactory.persistenceDataManagement(e,ErrorConstants.ERR_UPDATE_NO_LONGER_EXISTS);
        }
        return c;
    }

    @Transactional(readOnly = true)
    public Currency getANSPCurrency() {

        if (BillingContext.get(BillingContextKey.ANSP_CURRENCY) != null) {
            return BillingContext.get(BillingContextKey.ANSP_CURRENCY);
        }

        return currencyRepository.getANSPCurrency();
    }

    @Transactional(readOnly = true)
    public Page<Currency> getANSPCurrencyAndUSD() {

        final List<Currency> anspAndUSDCurrency = new ArrayList<>(2);

        final Currency usdCurrency = findByCurrencyCode("USD");
        final Currency anspCurrency = getANSPCurrency();

        if (usdCurrency != null) {
            anspAndUSDCurrency.add(usdCurrency);
        }

        if (anspCurrency != null) {
            anspAndUSDCurrency.add(anspCurrency);
        }

        return new PageImpl<>(anspAndUSDCurrency);
    }

    @Transactional(readOnly = true)
    public Currency findByCurrencyCode(String currencyCode) {
        LOG.debug("Request findByCurrencyCode : {}", currencyCode);
        Currency currency=null;
        if(currencyCode!=null && !currencyCode.isEmpty()){
            currency=currencyRepository.findByCurrencyCode(currencyCode);
        }
        return currency;
    }

    @Transactional(readOnly = true)
    public Currency findByExternalAccountingSystemId(String externalAccountingSystemId) {
        LOG.debug("Request findByExternalAccountingSystemId : {}", externalAccountingSystemId);
        Currency currency = null;
        if (externalAccountingSystemId != null && !externalAccountingSystemId.isEmpty())
            currency = currencyRepository.findByExternalAccountingSystemIdentifier(externalAccountingSystemId);
        return currency;
    }

    @Transactional(readOnly = true)
    public List<Currency> getActiveCurrency() {
        return currencyRepository.getActiveCurrency();
    }
    
    @Transactional(readOnly = true)
    public boolean isUsedAsExchangeTargetByAnotherActiveCurrency (final Integer currencyId) {
        return this.currencyRepository.isUsedAsExchangeTargetByAnotherActiveCurrency (currencyId);
    }

    public long countAll() {
        return currencyRepository.count();
    }
}
