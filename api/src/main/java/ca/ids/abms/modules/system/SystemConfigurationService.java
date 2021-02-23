package ca.ids.abms.modules.system;

import ca.ids.abms.config.error.CustomParametrizedException;
import ca.ids.abms.config.error.ErrorConstants;
import ca.ids.abms.modules.countries.Country;
import ca.ids.abms.modules.countries.CountryRepository;
import ca.ids.abms.modules.flightmovements.FlightMovement;
import ca.ids.abms.modules.flightmovements.FlightMovementRepository;
import ca.ids.abms.modules.system.summary.SystemConfigurationItemName;
import ca.ids.abms.modules.transactions.Transaction;
import ca.ids.abms.modules.transactions.TransactionRepository;
import ca.ids.abms.modules.util.models.ModelUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;

@Service
@Transactional
public class SystemConfigurationService {

    private static final Logger LOG = LoggerFactory.getLogger(SystemConfigurationService.class);

    private SystemConfigurationRepository systemConfigurationRepository;

    private TransactionRepository transactionRepository;

    private FlightMovementRepository flightMovementRepository;

    private CountryRepository countryRepository;

    public SystemConfigurationService(SystemConfigurationRepository aSystemConfigurationRepository, TransactionRepository aTransactionRepository,
            FlightMovementRepository aFlightMovementRepository, CountryRepository countryRepository) {
        systemConfigurationRepository = aSystemConfigurationRepository;
        transactionRepository = aTransactionRepository;
        flightMovementRepository = aFlightMovementRepository;
        this.countryRepository = countryRepository;
    }

    public List<SystemConfiguration> getNoauthLanguagesSystemConfigurations() {
        return this.systemConfigurationRepository.getNoauthLanguagesSystemConfigurations();
    }

    public List<SystemConfiguration> getNoauthUnitsOfMeasureSystemConfigurations() {
        return this.systemConfigurationRepository.getNoauthUnitsOfMeasureSystemConfigurations();
    }

    public List<SystemConfiguration> getPasswordSettings() {
        return this.systemConfigurationRepository.getPasswordSettings();
    }

    public List<SystemConfiguration> getSelfCareSettings(String param) {
        return this.systemConfigurationRepository.getSelfCareSettings(param);
    }

    public SystemConfiguration getNoauthAirNavigationChargesCurrency() {
        return this.systemConfigurationRepository.getNoauthAirNavigationChargesCurrency();
    }

    public SystemConfiguration getNoauthAnspCurrency() {
        return this.systemConfigurationRepository.getNoauthAnspCurrency();
    }

    @Transactional(readOnly = true)
    public List<SystemConfiguration> findAll() {
        LOG.debug("Request to get all systemConfigurations");

        return systemConfigurationRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Page<SystemConfiguration> findSystemConfigurations(Pageable pageable) {
        LOG.debug("Request to get system configurations");
        return systemConfigurationRepository.findSystemConfigurationsByOrderByItemClassAscItemNameAsc(pageable);
    }

    @Transactional(readOnly = true)
    public Page<SystemConfiguration> findPluginConfigurations(Integer pluginId, Pageable pageable) {
        LOG.debug("Request to get plugin configurations");
        return systemConfigurationRepository.findPluginConfigurationsByOrderByItemClassAscItemNameAsc(pluginId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<SystemConfiguration> findClientStorageConfigurations(Pageable pageable) {
        LOG.debug("Request to get client storage configurations");
        return systemConfigurationRepository.findByClientStorageForbiddenFalse(pageable);
    }

    @Transactional(readOnly = true)
    public String getCurrentValue(String itemName) {
        LOG.trace("Request to get current value by itemname : {}", itemName);
        final SystemConfiguration sc = doGetByName (itemName);

        String value = null;
        if (sc == null) {
            LOG.error("Could not find system configuration item '{}'", itemName);
        } else if (sc.getCurrentValue() != null) {
            LOG.trace("Found system configuration item '{}' with current value of '{}'", itemName, sc.getCurrentValue());
            value = sc.getCurrentValue();
        } else {
            LOG.trace("Found system configuration item '{}' with default value of '{}'", itemName, sc.getDefaultValue());
            value = sc.getDefaultValue();
        }

        return value;
    }

    /**
     * Get current value if exists, else default value if exists. If no values exist, returns
     * `null` indicating no value found.
     *
     * @param itemName system configuration item name
     * @return current or default configuration value
     */
    @Transactional(readOnly = true)
    public String getValue(String itemName) {
        LOG.trace("Request to get current or default value by itemname : {}", itemName);
        SystemConfiguration systemConfiguration = doGetByName (itemName);

        // if exists, attempt to return current or default value
        if (systemConfiguration != null) {
            if(systemConfiguration.getCurrentValue() != null && !systemConfiguration.getCurrentValue().isEmpty())
                return systemConfiguration.getCurrentValue();
            else if (systemConfiguration.getDefaultValue() != null && !systemConfiguration.getDefaultValue().isEmpty())
                return systemConfiguration.getDefaultValue();
        }

        // else return null indicating no value found
        return null;
    }

    @Transactional(readOnly = true)
    public SystemConfiguration getOne(Integer id) {
        LOG.debug("Request to get SystemConfiguration : {}", id);
        return systemConfigurationRepository.getOne(id);
    }

    @Transactional(readOnly = true)
    public SystemConfiguration getOneByItemName(String itemName) {
        LOG.trace("Request to get SystemConfiguration  by itemName : {}", itemName);
        return doGetByName (itemName);
    }

    @Transactional(readOnly = true)
    public Boolean getBoolean (final String name) {
        return getBoolean (name, false);
    }

    @Transactional(readOnly = true)
    public Boolean getBoolean (final String name, final Boolean dflt) {
        final SystemConfiguration x = doGetByName (name);
        if (x == null || x.getCurrentValue() == null || x.getCurrentValue().isEmpty()) {
            return dflt;
        }
        final String value = x.getCurrentValue();
        if (value.equalsIgnoreCase("t"))
            return Boolean.TRUE;
        return Boolean.FALSE;
    }

    @Transactional(readOnly = true)
    public String getString (final String name) {
        return getString (name, null);
    }

    @Transactional(readOnly = true)
    public String getString (final String name, final String dflt) {
        final SystemConfiguration x = doGetByName (name);
        if (x == null) {
            return dflt;
        }
        return x.getCurrentValue();
    }

    @Transactional(readOnly = true)
    public String getStringNotEmpty (final String name, final String dflt) {
        final SystemConfiguration x = doGetByName (name);
        if (x == null || x.getCurrentValue() == null || x.getCurrentValue().isEmpty()) {
            return dflt;
        }
        final String value = x.getCurrentValue().trim();
        if (value.isEmpty())
            return dflt;
        return value;
    }

    @Transactional(readOnly = true)
    public Boolean isLocaleSupported(String locale) {
        JSONParser parser = new JSONParser();
        JSONArray langArray;

        try {
            langArray = (JSONArray) parser.parse(getCurrentValue(SystemConfigurationItemName.LANGUAGE_SUPPORTED));
        } catch (Exception e) {
            LOG.debug("Locale is not supported: {}", e);
            return false;
        }

        for (Object lang : langArray) {
            JSONObject langObject = (JSONObject) lang;
            if (langObject.get("code").equals(locale)) {
                return true;
            }
        }

        LOG.debug("Locale is not supported: {}", locale);
        return false;
    }

    @Transactional(readOnly = true)
    @NotNull
    public BillingOrgCode getBillingOrgCode() {
        return BillingOrgCode.safeParseDatabaseCode (getCurrentValue(SystemConfigurationItemName.ORGANISATION_NAME));
    }

    @Transactional(readOnly = true)
    public boolean shouldCalculateParkingCharges () {
        return Boolean.TRUE.equals(getBoolean(SystemConfigurationItemName.CALCULATE_PARKING_CHARGES));
    }

    @Transactional(readOnly = true)
    public boolean shouldIncludePAXinInvoiceTotal () {
        return Boolean.TRUE.equals(getBoolean(SystemConfigurationItemName.INCLUDE_PASSENGER_FEES_ON_INVOICE));
    }

    @Transactional(readOnly = true)
    public boolean shouldDisplayPaxCharges () {
        return Boolean.TRUE.equals(getBoolean(SystemConfigurationItemName.PASSENGER_CHARGES_SUPPORT));
    }

    @Transactional(readOnly = true)
    public boolean shouldExtendedHoursSurchargeCharges () {
        return Boolean.TRUE.equals(getBoolean(SystemConfigurationItemName.EXTENDED_HOURS_SURCHARGE_SUPPORT));
    }

    @Transactional(readOnly = true)
    public boolean shouldDisplayLateArrivalCharges () {
        return Boolean.TRUE.equals(getBoolean(SystemConfigurationItemName.LATE_ARRIVAL_CHARGES_SUPPORT));
    }

    @Transactional(readOnly = true)
    public boolean shouldDisplayLateDepartureCharges () {
        return Boolean.TRUE.equals(getBoolean(SystemConfigurationItemName.LATE_DEPARTURE_CHARGES_SUPPORT));
    }

    @Transactional(readOnly = true)
    public boolean shouldDisplayTASPCharges () {
        return Boolean.TRUE.equals(getBoolean(SystemConfigurationItemName.TASP_CHARGES_SUPPORT));
    }

    /**
     * Return a configuration option as an int, or 0 if the item is invalid or unparsable.
     * @param itemName
     * @return
     */
    @Transactional(readOnly = true)
    public int getIntOrZero (final String itemName) {
        return getInteger (itemName, 0);
    }

    /**
     * Get a configuration item as an Integer; returns the specified default value if all else fails (item unparsable, etc).
     */
    @Transactional(readOnly = true)
    public Integer getInteger (final String itemName, final Integer dflt) {
        Preconditions.checkNotNull (itemName);
        Integer itemValue = dflt;
        final SystemConfiguration systemConfiguration = doGetByName (itemName);
        if(systemConfiguration != null) {
            try {
                if (org.apache.commons.lang.StringUtils.isNotBlank(systemConfiguration.getCurrentValue())) {
                    itemValue = Integer.parseInt(systemConfiguration.getCurrentValue());
                } else if (org.apache.commons.lang.StringUtils.isNotBlank(systemConfiguration.getDefaultValue())) {
                    itemValue = Integer.parseInt(systemConfiguration.getDefaultValue());
                } else {
                    LOG.warn("The configuration item {} hasn't any value set", itemName);
                }
            } catch (NumberFormatException e) {
                LOG.warn("The configuration item {} has a wrong value: {}", itemName, e.getLocalizedMessage());
            }
        } else {
            LOG.warn("The configuration item {} is missing", itemName);
        }
        return itemValue;
    }

    /**
     * Get a configuration item as Double; returns the specified default value if all else fails (item unparsable, etc).
     */
    @Transactional(readOnly = true)
    public Double getDouble (final String itemName, final Double dflt) {
        Preconditions.checkNotNull (itemName);
        Double itemValue = dflt;
        final SystemConfiguration systemConfiguration = doGetByName (itemName);
        if(systemConfiguration != null) {
            try {
                if (org.apache.commons.lang.StringUtils.isNotBlank(systemConfiguration.getCurrentValue())) {
                    itemValue = Double.parseDouble(systemConfiguration.getCurrentValue());
                } else if (org.apache.commons.lang.StringUtils.isNotBlank(systemConfiguration.getDefaultValue())) {
                    itemValue = Double.parseDouble(systemConfiguration.getDefaultValue());
                } else {
                    LOG.warn("The configuration item {} hasn't any value set", itemName);
                }
            } catch (NumberFormatException e) {
                LOG.warn("The configuration item {} has a wrong value: {}", itemName, e.getLocalizedMessage());
            }
        } else {
            LOG.warn("The configuration item {} is missing", itemName);
        }
        return itemValue;
    }

    private void validateCreditAmount(Collection<SystemConfigurationViewModel> systemConfigurations) {
        LOG.debug("Validate credit limit and min and max note amount");

        Double max = null;
        Double min = null;
        Double creditLimit = null;

        for (SystemConfigurationViewModel systemConfiguration : systemConfigurations) {
            if (systemConfiguration.getItemName().equals(SystemConfigurationItemName.DFLT_ACCOUNT_CREDIT_LIMIT)){
                if (systemConfiguration.getCurrentValue() == null) {
                    throw new CustomParametrizedException(ErrorConstants.ERR_VALIDATION,
                        new Exception("Default account credit limit can not be null"));
                }
                else {
                    creditLimit = Double.parseDouble(systemConfiguration.getCurrentValue());
                }
            }
            if (systemConfiguration.getItemName().equals(SystemConfigurationItemName.DFLT_ACCOUNT_MAX_CREDIT_LIMIT)){
                if (systemConfiguration.getCurrentValue() == null) {
                    throw new CustomParametrizedException(ErrorConstants.ERR_VALIDATION,
                        new Exception("Default account maximum credit note amount can not be null"));
                }
                else {
                    max = Double.parseDouble(systemConfiguration.getCurrentValue());
                }
            }
            if (systemConfiguration.getItemName().equals(SystemConfigurationItemName.DFLT_ACCOUNT_MIN_CREDIT_LIMIT)){
                if (systemConfiguration.getCurrentValue() == null) {
                    throw new CustomParametrizedException(ErrorConstants.ERR_VALIDATION,
                        new Exception("Default account minimum credit note amount can not be null"));
                }
                else {
                    min = Double.parseDouble(systemConfiguration.getCurrentValue());
                }
            }
        }

        if (creditLimit != null && min != null && max != null) {
            if (min < 0 || max < 0) {
                throw new CustomParametrizedException(ErrorConstants.ERR_VALIDATION,
                    new Exception(
                        "Default account minimum credit note amount or Default account maximum credit note amount can not be less than 0"));
            }

            if (min > max) {
                throw new CustomParametrizedException(ErrorConstants.ERR_VALIDATION,
                    new Exception(
                        String.format(
                            "Default account maximum credit note amount (%.0f) should be greater than minimum amount" + " (%.0f)", max, min)));
            }
            if (creditLimit < min || creditLimit > max) {
                throw new CustomParametrizedException(ErrorConstants.ERR_VALIDATION,
                    new Exception(
                        String.format(
                            "Default account credit limit" +
                            " (%.0f) " +
                            "should be within the range of minimum" +
                            " (%.0f) " +
                            "and maximum credit note amounts" +
                            " (%.0f).", creditLimit, min, max)));
            }
        }
    }

    @Transactional
    public SystemConfiguration update(final String itemName, final String itemValue) {
        if(itemName == null)
            throw new IllegalArgumentException("Empty item name");


        SystemConfiguration systemConfiguration = doGetByName (itemName);
        if (systemConfiguration == null)
            throw new IllegalStateException("No system configuration item exists with name " + itemName);

        validate (systemConfiguration, itemValue);

        systemConfiguration.setCurrentValue(itemValue);
        return update(systemConfiguration);
    }

    private void validate (final SystemConfiguration existingItem, final String newValue) {
        assert (existingItem != null && existingItem.getItemName() != null);
        if (existingItem.getItemName().equals(SystemConfigurationItemName.ANSP_COUNTRY_CODE) && StringUtils.isNotBlank(newValue)) {
            LOG.debug("Validating the country code {}", newValue);
            final Country country = countryRepository.findCountryByCountryCode(newValue);
            if (country == null) {
                throw new CustomParametrizedException("Default country code not valid");
            }
        }
    }

    @Transactional
    public SystemConfiguration update(final SystemConfiguration item) {
        if (item != null && item.getId() != null) {
            return systemConfigurationRepository.save(item);
        } else {
            return item;
        }
    }

    @Transactional
    public Collection<SystemConfiguration> update(Collection<SystemConfigurationViewModel> systemConfigurations) {
        LOG.debug("Request to update SystemConfiguration : {}", systemConfigurations);

        Collection<SystemConfiguration> returnCollection = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(systemConfigurations)) {
            validateCreditAmount(systemConfigurations);
            validateANSPCurrency(systemConfigurations);
            for (SystemConfigurationViewModel systemConfiguration : systemConfigurations) {
                SystemConfiguration existingSystemConfiguration = systemConfigurationRepository.getOne(systemConfiguration.getId());
                validate(existingSystemConfiguration, systemConfiguration.getCurrentValue());
                ModelUtils.mergeOnly(systemConfiguration, existingSystemConfiguration, "currentValue");
                SystemConfiguration updated = systemConfigurationRepository.save(existingSystemConfiguration);
                returnCollection.add(updated);
            }
        }
        return returnCollection;
    }

    private void validateANSPCurrency(Collection<SystemConfigurationViewModel> aSystemConfigurations) {
        LOG.debug("Validate ansp currency and pax fee currency");

        for (SystemConfigurationViewModel systemConfiguration : aSystemConfigurations) {
            switch (systemConfiguration.getItemName()) {
                case SystemConfigurationItemName.ANSP_CURRENCY:
                    this.do_validateAnspValue(systemConfiguration);
                    break;
                case SystemConfigurationItemName.AIR_NAVIGATION_CHARGES_CURRENCY:
                    this.do_validateAnspAirNavValue(systemConfiguration);
                    break;
                case SystemConfigurationItemName.DOMESTIC_PAX_FEE_CURRENCY:
                    this.do_validateAnspDomPaxValue(systemConfiguration);
                    break;
                case SystemConfigurationItemName.INTERNATIONAL_PAX_FEE_CURRENCY:
                    this.do_validateAnspIntlPaxValue(systemConfiguration);
                    break;
                default:
                    // no match, do nothing and continue loop
                    break;
            }
        }
    }

    private int countFlightMovementsWithNonZeroAirNavCharges() {
        List<FlightMovement> flightMovements = flightMovementRepository.findByAirNavigationChargesGreaterThan(0d);
        if (flightMovements != null)
            return flightMovements.size();
        else
            return 0;
    }

    private int countFlightsMovementWithNonZeroPaxCharges(String type) {
        int count = 0;
        Double zero = 0d;
        List<FlightMovement> flightMovements = null;
        if (type.equals(SystemConfigurationItemName.DOMESTIC_PAX_FEE_CURRENCY)) {
            flightMovements = flightMovementRepository.findByInternationalPassengerChargesGreaterThan(zero);
        } else if (type.equals(SystemConfigurationItemName.INTERNATIONAL_PAX_FEE_CURRENCY)) {
            flightMovements = flightMovementRepository.findByDomesticPassengerChargesGreaterThan(zero);
        }
        if (flightMovements != null) {
            count = flightMovements.size();
        }
        LOG.debug("There are {} flight_movements with non-zero passenger charges for {} ", count, type);
        return count;
    }

    private int countTransactions() {
        int count = 0;
        List<Transaction> transactions = transactionRepository.findAll();
        if (transactions != null) {
            count = transactions.size();
        }
        LOG.debug("There are {} transactions ", count);
        return count;
    }

    private void do_validateAnspValue(SystemConfigurationViewModel systemConfiguration) {
        if (systemConfiguration.getCurrentValue() == null) {
            throw new CustomParametrizedException(ErrorConstants.ERR_VALIDATION,
                new Exception("ANSP currency can not be null"));
        } else {
            SystemConfiguration actualValue = doGetByName(SystemConfigurationItemName.ANSP_CURRENCY);
            if (actualValue!=null && actualValue.getCurrentValue() != null && !actualValue.getCurrentValue().equals(systemConfiguration.getCurrentValue())) {
                int countTransactions = countTransactions();
                if (countTransactions > 0) {
                    throw new CustomParametrizedException(ErrorConstants.ERR_VALIDATION,
                        new Exception("ANSP currency can not be modified if there are transactions."));
                }
            }
        }
    }

    private void do_validateAnspAirNavValue(SystemConfigurationViewModel systemConfiguration) {
        if (systemConfiguration.getCurrentValue() == null) {
            throw new CustomParametrizedException(ErrorConstants.ERR_VALIDATION,
                new Exception("Air navigation charges currency can not be null"));
        } else {
            SystemConfiguration actualValue = doGetByName(SystemConfigurationItemName.AIR_NAVIGATION_CHARGES_CURRENCY);
            if (actualValue !=null && actualValue.getCurrentValue() != null && !actualValue.getCurrentValue().equals(systemConfiguration.getCurrentValue())) {
                int countFlights = countFlightMovementsWithNonZeroAirNavCharges();
                if (countFlights > 0) {
                    throw new CustomParametrizedException(ErrorConstants.ERR_VALIDATION,
                        new Exception("Air navigation charges currency can not be modified if there are flight movements with non-zero air navigation charges."));
                }
            }
        }
    }

    private void do_validateAnspDomPaxValue(SystemConfigurationViewModel systemConfiguration) {
        if (systemConfiguration.getCurrentValue() == null) {
            throw new CustomParametrizedException(ErrorConstants.ERR_VALIDATION,
                new Exception("Domestic passenger fee currency can not be null"));
        } else {
            SystemConfiguration actualValue = doGetByName(SystemConfigurationItemName.DOMESTIC_PAX_FEE_CURRENCY);
            if (actualValue!=null && actualValue.getCurrentValue() != null && !actualValue.getCurrentValue().equals(systemConfiguration.getCurrentValue())) {
                int countFlights = countFlightsMovementWithNonZeroPaxCharges(SystemConfigurationItemName.DOMESTIC_PAX_FEE_CURRENCY);
                if (countFlights > 0) {
                    throw new CustomParametrizedException(ErrorConstants.ERR_VALIDATION,
                        new Exception("Domestic passenger fee currency can not be modified if there are flight movements with non-zero passenger charges."));
                }
            }
        }
    }

    private void do_validateAnspIntlPaxValue(SystemConfigurationViewModel systemConfiguration) {
        if (systemConfiguration.getCurrentValue() == null) {
            throw new CustomParametrizedException(ErrorConstants.ERR_VALIDATION,
                new Exception("International passenger fee currency can not be null"));
        } else {
            SystemConfiguration actualValue = doGetByName(SystemConfigurationItemName.INTERNATIONAL_PAX_FEE_CURRENCY);
            if (actualValue !=null && actualValue.getCurrentValue() != null && !actualValue.getCurrentValue().equals(systemConfiguration.getCurrentValue())) {
                int countFlights = countFlightsMovementWithNonZeroPaxCharges(SystemConfigurationItemName.INTERNATIONAL_PAX_FEE_CURRENCY);
                if (countFlights > 0) {
                    throw new CustomParametrizedException(ErrorConstants.ERR_VALIDATION,
                        new Exception("International passenger fee currency can not be modified if there are flight movements with non-zero passenger charges."));
                }
            }
        }
    }

    /**
     * Get a system config item by name. This method maintains a name => id map in memory and
     * looks up items by id whenever possible -- this improves performance because PK lookups
     * hit the Hibernate session cache.
     * 
     * <p>
     * <b>WARNING</b>: it's important that this method returns the real SystemConfiguration entity
     *                 objects, rather than transaction-bound proxies that Hibernate someteimes
     *                 uses to implement lazy initializations. This is because other ABMS classes
     *                 sometimes access system properties outside of DB transactions.
     * 
     */
    private SystemConfiguration doGetByName(String itemName) {
        if (itemName != null) {
            Integer id;
            synchronized (this) {
                id = nameToId.get (itemName);
                if (id == null) {
                    final SystemConfiguration x = this.systemConfigurationRepository.getOneByItemName (itemName);
                    if (x != null) {
                        nameToId.put (itemName, x.getId());
                    }
                    return x;
                }
            }
            // Don't call getOne() here because it returns a proxy object that doesn't
            // work outside of DB transactions; but findOne() is OK.
            return this.systemConfigurationRepository.findOne(id);
        }
        return this.systemConfigurationRepository.getOneByItemName(itemName);
    }
    private final Map<String,Integer> nameToId = new HashMap<>();
}
