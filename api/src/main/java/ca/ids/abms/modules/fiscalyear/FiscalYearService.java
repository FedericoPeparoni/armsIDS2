package ca.ids.abms.modules.fiscalyear;

import ca.ids.abms.modules.billingcenters.BillingCenterService;
import ca.ids.abms.modules.billings.BillingLedgerService;
import ca.ids.abms.modules.system.SystemConfigurationService;
import ca.ids.abms.modules.system.summary.SystemConfigurationItemName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;

@Service
public class FiscalYearService {

    private static final Logger LOG = LoggerFactory.getLogger(FiscalYearService.class);

    private static final DateTimeFormatter FIRST_OF_FISCAL_FORMATTER = DateTimeFormatter.ofPattern("MM/dd");

    private final BillingCenterService billingCenterService;

    private final BillingLedgerService billingLedgerService;

    private final SystemConfigurationService systemConfigurationService;

    public FiscalYearService(
        final BillingCenterService billingCenterService,
        final BillingLedgerService billingLedgerService,
        final SystemConfigurationService systemConfigurationService
    ) {
        this.billingCenterService = billingCenterService;
        this.billingLedgerService = billingLedgerService;
        this.systemConfigurationService = systemConfigurationService;
    }

    @Transactional
    @SuppressWarnings("WeakerAccess")
    public void checkFiscalYear() {

        String firstDayOfFiscalYear = systemConfigurationService.getValue(SystemConfigurationItemName.FIRST_DAY_OF_FISCAL_YEAR);
        String calculatedFiscalYear = currentFiscalYear(firstDayOfFiscalYear);
        String currentFiscalYear = systemConfigurationService.getValue(SystemConfigurationItemName.CURRENT_FISCAL_YEAR);

        // if current fiscal year is the same as the calculated fiscal year, do nothing
        if (calculatedFiscalYear == null || (currentFiscalYear != null && currentFiscalYear.equals(calculatedFiscalYear)))
            return;

        // update system configuration item value with new calculated fiscal year
        String updatedFiscalYear = updateFiscalYear(currentFiscalYear, calculatedFiscalYear);

        // reset billing center invoice and receipt sequence if configured
        if (systemConfigurationService.getBoolean(SystemConfigurationItemName.RESET_SEQUENCE_NUMBERS_ON_NEW_FISCAL_YEAR, false)) {
            billingCenterService.resetSequenceNumbers(updatedFiscalYear);

            LocalDate ldtNow = LocalDate.now();
            billingLedgerService.resetInvoiceNumber(
                parseFiscalYear(firstDayOfFiscalYear, ldtNow.getYear())
            );
        }
    }

    /**
     * Get current fiscal year string as 'yy/yy'. Returns null if
     * firstOfFiscalYear does not exist.
     *
     * @param firstOfFiscalYear first of fiscal year
     *
     * @return fiscal year as 'yy/yy'
     */
    private String currentFiscalYear(String firstOfFiscalYear) {
        if (firstOfFiscalYear == null || firstOfFiscalYear.isEmpty())
            return null;

        LocalDate ldtNow = LocalDate.now();
        LocalDate fiscalYear = parseFiscalYear(firstOfFiscalYear, ldtNow.getYear());
        if (fiscalYear.isAfter(ldtNow))
            return formatFiscalYear(fiscalYear.minusYears(1), fiscalYear.minusDays(1));
        else
            return formatFiscalYear(fiscalYear, fiscalYear.plusYears(1).minusDays(1));
    }

    /**
     * Format fiscal year start and end as string 'yy/yy'.
     *
     * @param start start date of fiscal year
     * @param end end date of fiscal year
     *
     * @return fiscal year string format
     */
    private String formatFiscalYear(LocalDate start, LocalDate end) {
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yy");
        return start.format(dateFormat) + "/" + end.format(dateFormat);
    }

    /**
     * Parse mm/dd string into LocalDate defaulting to 01/01 if null supplied.
     *
     * @param firstOfFiscalYear first of fiscal year start
     * @param year fiscal year start
     *
     * @return local date representation of fiscal year
     */
    private LocalDate parseFiscalYear(String firstOfFiscalYear, Integer year) {
        int month;
        int dayOfMonth;

        // default year to current date
        if (year == null)
            year = LocalDate.now().getYear();

        // get month and year from param of default to January 1st if null
        if (firstOfFiscalYear != null && !firstOfFiscalYear.isEmpty()) {
            TemporalAccessor parsedDate = FIRST_OF_FISCAL_FORMATTER.parse(firstOfFiscalYear);
            month = parsedDate.get(ChronoField.MONTH_OF_YEAR);
            dayOfMonth = parsedDate.get(ChronoField.DAY_OF_MONTH);
        } else {
            month = 1;
            dayOfMonth = 1;
        }

        return LocalDate.of(year, month, dayOfMonth);
    }

    /**
     * Update current fiscal year system configuration item with new value.
     *
     * @param currentFiscalYear previous fiscal year value
     * @param newFiscalYear new fiscal year value
     */
    private String updateFiscalYear(String currentFiscalYear, String newFiscalYear) {
        LOG.debug("Updating current fiscal year from '{}' to '{}'.", currentFiscalYear, newFiscalYear);
        return systemConfigurationService
            .update(SystemConfigurationItemName.CURRENT_FISCAL_YEAR, newFiscalYear)
            .getCurrentValue();
    }
}
