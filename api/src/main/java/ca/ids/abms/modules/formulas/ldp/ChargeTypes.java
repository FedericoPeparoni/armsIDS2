package ca.ids.abms.modules.formulas.ldp;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import ca.ids.abms.spreadsheets.SSService;
import ca.ids.abms.spreadsheets.dto.WorkbookDto;

/**
 * Static charge type utilities.
 */
public class ChargeTypes {

	public enum LdpBillingFormulaChargeType {
		approach_charges,
		aerodrome_charges,
		late_arrival_charges,
		late_departure_charges,
		parking_charges,
        extended_hours_service_charge
	}

    /** Returns true if the given charge type ID is known/valid: "landing", "parking", or "departure". */
    public static boolean isAllowed (final String chargeType) {
        return allowedChargeTypes.containsKey(chargeType);
    }

    /**
     * Validate a spreadsheet file that contains charges of the given type.
     *
     * @param chargeType - one of "approach_charges", "aerodrome_charges", "late_arrival_charges",
     *                   "late_departure_charges" or "parking_charges"
     * @param svc - an instance of {@link SSService}
     * @param data - the Excel spreadsheet file contents
     *
     * @throws ca.ids.abms.spreadsheets.SSException if file data is not a valid Excel file or if it's layout does not match expectations for the given type.
     *
     * @see ca.ids.abms.spreadsheets.AerodromeCharges
     * @see ca.ids.abms.spreadsheets.ParkingCharges
     */
    public static void validateSpreadsheet (final String chargeType, final SSService svc, final byte[] data) {
            validateSpreadsheet (chargeType, svc, data, null);
    }

    /**
     * Validate a spreadsheet file that contains charges of the given type.
     *
     * @param chargeType - one of "approach_charges", "aerodrome_charges", "late_arrival_charges",
     *                   "late_departure_charges", "parking_charges" or "extended_hours_service_charge"
     * @param svc - an instance of {@link ca.ids.abms.spreadsheets.SSService}
     * @param data - the Excel spreadsheet file contents
     * @param fileName - original file name from which the file was read (used in error and log messages)
     *
     * @throws ca.ids.abms.spreadsheets.SSException if file data is not a valid Excel file or if it's layout does not match expectations for the given type.
     *
     * @see ca.ids.abms.spreadsheets.AerodromeCharges
     * @see ca.ids.abms.spreadsheets.ParkingCharges
     */
    public static void validateSpreadsheet (final String chargeType, final SSService svc, final byte[] data, final String fileName) {
        allowedChargeTypes.get (chargeType).spreadsheetValidator.validate(svc, data, fileName);
    }

    /**
     * Render the spreadsheet file that contains charges of the given type.
     *
     * @param chargeType - one of "approach_charges", "aerodrome_charges", "late_arrival_charges",
     *                   "late_departure_charges" or "parking_charges"
     * @param svc - an instance of {@link ca.ids.abms.spreadsheets.SSService}
     * @param stream - the stream containing the Excel spreadsheet file contents
     *
     * @throws ca.ids.abms.spreadsheets.SSException if file data is not a valid Excel file or if it's layout does not match expectations for the given type.
     *
     * @see ca.ids.abms.spreadsheets.AerodromeCharges
     * @see ca.ids.abms.spreadsheets.ParkingCharges
     */
    public static WorkbookDto getModelSpreadsheet (final String chargeType, final SSService svc, final InputStream stream) {
        return allowedChargeTypes.get (chargeType).spreadsheetHtmlRenderer.toModel(svc, stream);
    }

    // --------------------- private --------------------

    @FunctionalInterface
    private interface SpreadsheetValidator {
        void validate (final SSService svc, final byte[] data, final String fileName);
    }

    @FunctionalInterface
    private interface SpreadsheetHtmlRenderer {
        WorkbookDto toModel (final SSService svc, final InputStream stream);
    }

    private static final class ChargeInfo {
        final SpreadsheetValidator spreadsheetValidator;
        final SpreadsheetHtmlRenderer spreadsheetHtmlRenderer;

        ChargeInfo (final SpreadsheetValidator spreadsheetValidator,
                           final SpreadsheetHtmlRenderer spreadsheetHtmlRenderer) {
            this.spreadsheetValidator = spreadsheetValidator;
            this.spreadsheetHtmlRenderer = spreadsheetHtmlRenderer;
        }
    }

    private static final Map <String, ChargeInfo> allowedChargeTypes = new HashMap<>();

    static {
        allowedChargeTypes.put (LdpBillingFormulaChargeType.approach_charges.name(), new ChargeInfo (
            (svc, data, fileName)-> svc.aerodromeCharges().load (data, fileName),
            (svc, stream)-> svc.aerodromeCharges().load(stream).toModel()
        ));
        allowedChargeTypes.put (LdpBillingFormulaChargeType.aerodrome_charges.name(), new ChargeInfo (
            (svc, data, fileName)-> svc.aerodromeCharges().load (data, fileName),
            (svc, stream)-> svc.aerodromeCharges().load(stream).toModel()
        ));
        allowedChargeTypes.put (LdpBillingFormulaChargeType.late_arrival_charges.name(), new ChargeInfo (
            (svc, data, fileName)-> svc.aerodromeCharges().load (data, fileName),
            (svc, stream)-> svc.aerodromeCharges().load(stream).toModel()
        ));
        allowedChargeTypes.put (LdpBillingFormulaChargeType.late_departure_charges.name(), new ChargeInfo (
            (svc, data, fileName)-> svc.aerodromeCharges().load (data, fileName),
            (svc, stream)-> svc.aerodromeCharges().load(stream).toModel()
        ));
        allowedChargeTypes.put (LdpBillingFormulaChargeType.parking_charges.name(), new ChargeInfo (
            (svc, data, fileName)-> svc.parkingCharges().load (data, fileName),
            (svc, stream)-> svc.parkingCharges().load(stream).toModel()
        ));
        allowedChargeTypes.put (LdpBillingFormulaChargeType.extended_hours_service_charge.name(), new ChargeInfo (
            (svc, data, fileName)-> svc.aerodromeCharges().load (data, fileName),
            (svc, stream)-> svc.aerodromeCharges().load(stream).toModel()
        ));
    }

    private ChargeTypes() {
    }
}
