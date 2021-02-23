package ca.ids.abms.spreadsheets;

/**
 * Service for read-only access to data stored in Excel spreadsheets.
 * <p>
 * This service allows one to construct "view" objects that allow the
 * extraction of things like charges from Excel spreadsheets. The spreadsheet
 * files must follow a particular format for each supported view interface.
 * 
 * <h2>Examples</h2>
 * <pre><code>
 * SSerrvice svc = ...;
 * // Load aerodrome charges spreadsheet
 * byte[] aerodromeSpreadsheetData = ...; // from database column, etc.
 * // Get the aerodrome charge for a 10-ton aircraft for a domestic flight at 10 pm
 * AerodromeCharges aerodromeChargescharges = svc.aerodromeCharges().load (aerodromeSpreadsheetData);
 * double charge1 = aerodromeChargescharges.getCharge (
 *     10.0,
 *     LocalTime.parse ("10:00"),
 *     ChargeFlightType.DOMESTIC);
 * 
 * // Get the parking charge for a 7-ton aircraft for an international flight that arrived at 11am
 * // and departed at 1pm on Jan 3 2017
 * byte[] parkingSpreadsheetData = ...; // from database column, etc.
 * ParkingCharges parkingCharges = svc.parkingCharges().load (parkingSpreadsheetData);
 * double charge2 = charges.getCharge (
 *     7.0,
 *     LocalDateTime.parse ("2017-01-03T11:00:00"),
 *     LocalDateTime.parse ("2017-01-03T13:00:00"),
 *     ChargeFlightType.INTERNATIONAL);
 * </code></pre>
 * 
 * The returned view interface(s) allow one to load an example spreadsheet, rather than
 * an existing file or binary data. These example files are stored within the API project
 * under "resources":
 * <pre><code>
 * SSSerrvice svc = ...;
 * AerodromeCharges charges = svc.aerodromeCharges().loadExample();
 * 
 * // maybe also save the example to an external stream
 * charges.save (...);
 * </code></pre>
 * 
 * @author dpanech
 *
 */
public interface SSService {

    /** Get the aerodrome charges spreadsheet loader */
    public SSLoader <AerodromeCharges> aerodromeCharges();

    /** Get the parking charges spreadsheet loader */
    public SSLoader <ParkingCharges> parkingCharges();
}
