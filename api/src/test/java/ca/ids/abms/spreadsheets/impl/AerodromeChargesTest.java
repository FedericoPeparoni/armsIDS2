package ca.ids.abms.spreadsheets.impl;

import org.junit.Test;

import ca.ids.abms.spreadsheets.AerodromeCharges;
import ca.ids.abms.spreadsheets.FlightChargeType;
import ca.ids.abms.spreadsheets.InvalidSpreadsheetLayoutException;
import org.springframework.util.StringUtils;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalTime;

public class AerodromeChargesTest {

    public static final String TEST_FILE = "AerodromeChargesTest.xlsx";

    private static AerodromeChargesImpl load (final String sheetName) {
        return (AerodromeChargesImpl)new AerodromeChargesLoader (sheetName).loadResource(TEST_FILE);
    }

    @Test
    public void testExample() {
        new AerodromeChargesLoader (null).loadExample();
    }

    @Test
    public void testBasic() {
        final AerodromeChargesImpl x = load (null);
        assertThat (x.baseName()).isEqualTo(TEST_FILE);
        assertThat (x.sheetName()).isEqualTo("good1");
        assertThat (x.contentLength()).isGreaterThan(0);
        assertThat (x.mimeType()).isNotNull();
        assertThat (x.getCharge (15, LocalTime.of (5, 0), FlightChargeType.INTERNATIONAL)).isEqualTo(211);
        assertThat (x.getCharge (25, LocalTime.of (7, 0), FlightChargeType.DOMESTIC)).isEqualTo(322);
        assertThat (x.getCharge (999, LocalTime.of (23, 30), FlightChargeType.REGIONAL)).isEqualTo(333);

        // bad MTOW
        assertThatThrownBy (()->x.getCharge (-1, LocalTime.of (0, 0), FlightChargeType.INTERNATIONAL))
            .isInstanceOf(IllegalArgumentException.class);

        // bad time
        assertThatThrownBy (()->x.getCharge (0, null, FlightChargeType.INTERNATIONAL))
            .isInstanceOf(NullPointerException.class);

        // bad type
        assertThatThrownBy (()->x.getCharge (0, LocalTime.of (0, 0), null))
            .isInstanceOf(NullPointerException.class);

        // html structure
        String html = x.toHtml();
        assertThat(html).isNotNull();
        assertThat(StringUtils.countOccurrencesOf(html, "<tr>")).isEqualTo(5);
        assertThat(StringUtils.countOccurrencesOf(html, "<td>")).isEqualTo(30);
    }

    @Test
    public void testGood1() {
        final AerodromeCharges x = load ("good1");
        assertThat (x.baseName()).isEqualTo(TEST_FILE);
        assertThat (x.contentLength()).isGreaterThan(0);
        assertThat (x.mimeType()).isNotNull();
        assertThat (x.getCharge (15, LocalTime.of (5, 0), FlightChargeType.INTERNATIONAL)).isEqualTo(211);
        assertThat (x.getCharge (25, LocalTime.of (7, 0), FlightChargeType.DOMESTIC)).isEqualTo(322);
        assertThat (x.getCharge (999, LocalTime.of (23, 30), FlightChargeType.REGIONAL)).isEqualTo(333);

        // bad MTOWW
        assertThatThrownBy (()->x.getCharge (-1, LocalTime.of (0, 0), FlightChargeType.INTERNATIONAL))
            .isInstanceOf(IllegalArgumentException.class);

        // bad time
        assertThatThrownBy (()->x.getCharge (0, null, FlightChargeType.INTERNATIONAL))
            .isInstanceOf(NullPointerException.class);

        // bad type
        assertThatThrownBy (()->x.getCharge (0, LocalTime.of (0, 0), null))
            .isInstanceOf(NullPointerException.class);

    }

    @Test
    public void testGood2() {
        final AerodromeCharges x = load ("good2");
        assertThat (x.baseName()).isEqualTo(TEST_FILE);
        assertThat (x.contentLength()).isGreaterThan(0);
        assertThat (x.mimeType()).isNotNull();
        assertThat (x.getCharge (15, LocalTime.of (5, 0), FlightChargeType.INTERNATIONAL)).isEqualTo(211);
        assertThat (x.getCharge (25, LocalTime.of (7, 0), FlightChargeType.DOMESTIC)).isEqualTo(322);
        assertThat (x.getCharge (999, LocalTime.of (23, 30), FlightChargeType.REGIONAL)).isEqualTo(333);
    }

    @Test
    public void testGoodMissingRegional() {
        final AerodromeCharges x = load ("good_missing_regional");
        assertThat (x.baseName()).isEqualTo(TEST_FILE);
        assertThat (x.contentLength()).isGreaterThan(0);
        assertThat (x.mimeType()).isNotNull();
        assertThat (x.getCharge (15, LocalTime.of (5, 0), FlightChargeType.INTERNATIONAL)).isEqualTo(211);
        assertThat (x.getCharge (25, LocalTime.of (7, 0), FlightChargeType.DOMESTIC)).isEqualTo(322);
        assertThat (x.getCharge (999, LocalTime.of (23, 30), FlightChargeType.REGIONAL)).isEqualTo(0);
    }

    @Test
    public void testGoodMtowReorder() {
        final AerodromeCharges x = load ("good_mtow_reorder");
        assertThat (x.baseName()).isEqualTo(TEST_FILE);
        assertThat (x.contentLength()).isGreaterThan(0);
        assertThat (x.mimeType()).isNotNull();
        assertThat (x.getCharge (15, LocalTime.of (5, 0), FlightChargeType.INTERNATIONAL)).isEqualTo(211);
        assertThat (x.getCharge (25, LocalTime.of (7, 0), FlightChargeType.DOMESTIC)).isEqualTo(322);
        assertThat (x.getCharge (999, LocalTime.of (23, 30), FlightChargeType.REGIONAL)).isEqualTo(333);
    }

    @Test
    public void testGoodTimeSelection() {
        final AerodromeCharges x = load ("good1");

        // time & type selection with corner cases
        assertThat (x.getCharge(0, LocalTime.of (0, 0), FlightChargeType.INTERNATIONAL)).isEqualTo(111);
        assertThat (x.getCharge(0, LocalTime.of (0, 1), FlightChargeType.INTERNATIONAL)).isEqualTo(111);
        assertThat (x.getCharge(0, LocalTime.of (5, 58), FlightChargeType.INTERNATIONAL)).isEqualTo(111);
        assertThat (x.getCharge(0, LocalTime.of (5, 59), FlightChargeType.INTERNATIONAL)).isEqualTo(111);
        assertThat (x.getCharge(0, LocalTime.of (6, 0), FlightChargeType.INTERNATIONAL)).isEqualTo(112);
        assertThat (x.getCharge(0, LocalTime.of (6, 1), FlightChargeType.INTERNATIONAL)).isEqualTo(112);
        assertThat (x.getCharge(0, LocalTime.of (21, 58), FlightChargeType.INTERNATIONAL)).isEqualTo(112);
        assertThat (x.getCharge(0, LocalTime.of (21, 59), FlightChargeType.INTERNATIONAL)).isEqualTo(112);
        assertThat (x.getCharge(0, LocalTime.of (22, 00), FlightChargeType.INTERNATIONAL)).isEqualTo(113);
        assertThat (x.getCharge(0, LocalTime.of (22, 01), FlightChargeType.INTERNATIONAL)).isEqualTo(113);
        assertThat (x.getCharge(0, LocalTime.of (23, 58), FlightChargeType.INTERNATIONAL)).isEqualTo(113);
        assertThat (x.getCharge(0, LocalTime.of (23, 59), FlightChargeType.INTERNATIONAL)).isEqualTo(113);

        assertThat (x.getCharge(0, LocalTime.of (0, 0), FlightChargeType.DOMESTIC)).isEqualTo(121);
        assertThat (x.getCharge(0, LocalTime.of (0, 1), FlightChargeType.DOMESTIC)).isEqualTo(121);
        assertThat (x.getCharge(0, LocalTime.of (5, 58), FlightChargeType.DOMESTIC)).isEqualTo(121);
        assertThat (x.getCharge(0, LocalTime.of (5, 59), FlightChargeType.DOMESTIC)).isEqualTo(121);
        assertThat (x.getCharge(0, LocalTime.of (6, 0), FlightChargeType.DOMESTIC)).isEqualTo(122);
        assertThat (x.getCharge(0, LocalTime.of (6, 1), FlightChargeType.DOMESTIC)).isEqualTo(122);
        assertThat (x.getCharge(0, LocalTime.of (21, 58), FlightChargeType.DOMESTIC)).isEqualTo(122);
        assertThat (x.getCharge(0, LocalTime.of (21, 59), FlightChargeType.DOMESTIC)).isEqualTo(122);
        assertThat (x.getCharge(0, LocalTime.of (22, 00), FlightChargeType.DOMESTIC)).isEqualTo(123);
        assertThat (x.getCharge(0, LocalTime.of (22, 01), FlightChargeType.DOMESTIC)).isEqualTo(123);
        assertThat (x.getCharge(0, LocalTime.of (23, 58), FlightChargeType.DOMESTIC)).isEqualTo(123);
        assertThat (x.getCharge(0, LocalTime.of (23, 59), FlightChargeType.DOMESTIC)).isEqualTo(123);

        assertThat (x.getCharge(0, LocalTime.of (0, 0), FlightChargeType.REGIONAL)).isEqualTo(131);
        assertThat (x.getCharge(0, LocalTime.of (0, 1), FlightChargeType.REGIONAL)).isEqualTo(131);
        assertThat (x.getCharge(0, LocalTime.of (5, 58), FlightChargeType.REGIONAL)).isEqualTo(131);
        assertThat (x.getCharge(0, LocalTime.of (5, 59), FlightChargeType.REGIONAL)).isEqualTo(131);
        assertThat (x.getCharge(0, LocalTime.of (6, 0), FlightChargeType.REGIONAL)).isEqualTo(132);
        assertThat (x.getCharge(0, LocalTime.of (6, 1), FlightChargeType.REGIONAL)).isEqualTo(132);
        assertThat (x.getCharge(0, LocalTime.of (21, 58), FlightChargeType.REGIONAL)).isEqualTo(132);
        assertThat (x.getCharge(0, LocalTime.of (21, 59), FlightChargeType.REGIONAL)).isEqualTo(132);
        assertThat (x.getCharge(0, LocalTime.of (22, 00), FlightChargeType.REGIONAL)).isEqualTo(133);
        assertThat (x.getCharge(0, LocalTime.of (22, 01), FlightChargeType.REGIONAL)).isEqualTo(133);
        assertThat (x.getCharge(0, LocalTime.of (23, 58), FlightChargeType.REGIONAL)).isEqualTo(133);
        assertThat (x.getCharge(0, LocalTime.of (23, 59), FlightChargeType.REGIONAL)).isEqualTo(133);
    }

    @Test
    public void testGoodMtimeSelection() {
        final AerodromeCharges x = load ("good1");

        // time & type selection with corner cases
        assertThat (x.getCharge(0, LocalTime.of (0, 0), FlightChargeType.INTERNATIONAL)).isEqualTo(111);
        assertThat (x.getCharge(1, LocalTime.of (0, 0), FlightChargeType.INTERNATIONAL)).isEqualTo(111);
        assertThat (x.getCharge(10, LocalTime.of (0, 0), FlightChargeType.INTERNATIONAL)).isEqualTo(111);
        assertThat (x.getCharge(11, LocalTime.of (0, 0), FlightChargeType.INTERNATIONAL)).isEqualTo(211);
        assertThat (x.getCharge(19, LocalTime.of (0, 0), FlightChargeType.INTERNATIONAL)).isEqualTo(211);
        assertThat (x.getCharge(20, LocalTime.of (0, 0), FlightChargeType.INTERNATIONAL)).isEqualTo(211);
        assertThat (x.getCharge(21, LocalTime.of (0, 0), FlightChargeType.INTERNATIONAL)).isEqualTo(311);
        assertThat (x.getCharge(29, LocalTime.of (0, 0), FlightChargeType.INTERNATIONAL)).isEqualTo(311);
        assertThat (x.getCharge(30, LocalTime.of (0, 0), FlightChargeType.INTERNATIONAL)).isEqualTo(311);
        assertThat (x.getCharge(31, LocalTime.of (0, 0), FlightChargeType.INTERNATIONAL)).isEqualTo(311);
        assertThat (x.getCharge(1000, LocalTime.of (0, 0), FlightChargeType.INTERNATIONAL)).isEqualTo(311);
    }



    @Test
    public void testBadMtowMissing() {
        assertThatThrownBy(()->load ("bad_mtow_negative"))
            .isInstanceOf (InvalidSpreadsheetLayoutException.class)
            .hasMessageMatching(".*B7:.*invalid negative MTOW.*");
    }

    @Test
    public void testBadChargeNegative() {
        assertThatThrownBy(()->load ("bad_charge_negative"))
            .isInstanceOf (InvalidSpreadsheetLayoutException.class)
            .hasMessageMatching(".*E7:.*invalid negative charge.*");
    }

    @Test
    public void testBadChargeMissing() {
        assertThatThrownBy(()->load ("bad_charge_missing"))
            .isInstanceOf (InvalidSpreadsheetLayoutException.class)
            .hasMessageMatching(".*E7:.*invalid empty or non-numeric charge.*");
    }
}
