package ca.ids.abms.spreadsheets.impl;

import org.junit.Test;

import ca.ids.abms.spreadsheets.FlightChargeType;
import ca.ids.abms.spreadsheets.InvalidSpreadsheetLayoutException;
import org.springframework.util.StringUtils;

import static ca.ids.abms.spreadsheets.FlightChargeType.*;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;

public class ParkingChargesTest {

    public static final String TEST_FILE = "ParkingChargesTest.xlsx";

    private static ParkingChargesImpl load (final String sheetName) {
        return (ParkingChargesImpl)new ParkingChargesLoader (sheetName).loadResource(TEST_FILE);
    }

    private static LocalDateTime t (final String s) {
        return LocalDateTime.parse (s);
    }

    @Test
    public void testExample() {
        new ParkingChargesLoader(null).loadExample();
    }

    @Test
    public void testBasic() {

        final ParkingChargesImpl x = load (null);
        assertThat (x.baseName()).isEqualTo(TEST_FILE);
        assertThat (x.sheetName()).isEqualTo("good1");
        assertThat (x.contentLength()).isGreaterThan(0);
        assertThat (x.mimeType()).isNotNull();

        assertThat (x.getCharge (5, t("2017-01-01T00:00:00"), t("2017-01-01T01:00:00"), INTERNATIONAL)).isEqualTo (20);
        assertThat (x.getCharge (15, t("2017-01-01T00:00:00"), t("2017-01-01T01:00:00"), INTERNATIONAL)).isEqualTo (60);
        assertThat (x.getCharge (80, t("2017-01-01T00:00:00"), t("2017-01-01T01:00:00"), INTERNATIONAL)).isEqualTo (100);

        assertThat (x.getCharge (5, t("2017-01-01T00:00:00"), t("2017-01-01T23:59:00"), DOMESTIC)).isEqualTo (30);
        assertThat (x.getCharge (15, t("2017-01-01T00:00:00"), t("2017-01-01T23:59:00"), DOMESTIC)).isEqualTo (70);
        assertThat (x.getCharge (80, t("2017-01-01T00:00:00"), t("2017-01-01T23:59:00"), DOMESTIC)).isEqualTo (110);

        assertThat (x.getCharge (5, t("2017-01-01T00:00:00"), t("2017-01-02T00:00:00"), REGIONAL)).isEqualTo (0);
        assertThat (x.getCharge (15, t("2017-01-01T00:00:00"), t("2017-01-02T00:00:00"), REGIONAL)).isEqualTo (0);
        assertThat (x.getCharge (80, t("2017-01-01T00:00:00"), t("2017-01-02T00:00:00"), REGIONAL)).isEqualTo (0);

        // bad MTOW
        assertThatThrownBy (()->x.getCharge (-1, t("2017-01-01T00:00:00"), t("2017-01-01T01:00:00"), FlightChargeType.INTERNATIONAL))
            .isInstanceOf(IllegalArgumentException.class);

        // bad start/end time
        x.getCharge (0, t("2017-01-01T00:00:00"), t("2017-01-01T01:00:00"), FlightChargeType.INTERNATIONAL);
        x.getCharge (0, t("2017-01-01T00:00:00"), t("2017-01-01T00:00:00"), FlightChargeType.INTERNATIONAL);
        assertThatThrownBy (()->x.getCharge (0, null, t("2017-01-01T01:00:00"), FlightChargeType.INTERNATIONAL))
            .isInstanceOf(NullPointerException.class);
        assertThatThrownBy (()->x.getCharge (0, t("2017-01-01T01:00:00"), null, FlightChargeType.INTERNATIONAL))
            .isInstanceOf(NullPointerException.class);
        assertThatThrownBy (()->x.getCharge (0, t("2017-01-01T00:00:01"), t("2017-01-01T00:00:00"), FlightChargeType.INTERNATIONAL))
            .isInstanceOf(IllegalArgumentException.class);

        // bad basis
        assertThatThrownBy (()->x.getCharge (0, t("2017-01-01T01:00:00"), t("2017-01-01T01:00:00"), null))
            .isInstanceOf(NullPointerException.class);

        // html structure
        String html = x.toHtml();
        assertThat(html).isNotNull();
        assertThat(StringUtils.countOccurrencesOf(html, "<tr>")).isEqualTo(7);
        assertThat(StringUtils.countOccurrencesOf(html, "<td>")).isEqualTo(16);
    }

    @Test
    public void testBasicMtowSelection() {
        final ParkingChargesImpl x = load (null);

        assertThat (x.getCharge (0, t("2017-01-01T00:00:00"), t("2017-01-01T01:00:00"), INTERNATIONAL)).isEqualTo (20);
        assertThat (x.getCharge (1, t("2017-01-01T00:00:00"), t("2017-01-01T01:00:00"), INTERNATIONAL)).isEqualTo (20);
        assertThat (x.getCharge (9.9, t("2017-01-01T00:00:00"), t("2017-01-01T01:00:00"), INTERNATIONAL)).isEqualTo (20);
        assertThat (x.getCharge (10, t("2017-01-01T00:00:00"), t("2017-01-01T01:00:00"), INTERNATIONAL)).isEqualTo (20);

        assertThat (x.getCharge (10.1, t("2017-01-01T00:00:00"), t("2017-01-01T01:00:00"), INTERNATIONAL)).isEqualTo (60);
        assertThat (x.getCharge (49.9, t("2017-01-01T00:00:00"), t("2017-01-01T01:00:00"), INTERNATIONAL)).isEqualTo (60);
        assertThat (x.getCharge (50, t("2017-01-01T00:00:00"), t("2017-01-01T01:00:00"), INTERNATIONAL)).isEqualTo (60);

        assertThat (x.getCharge (51, t("2017-01-01T00:00:00"), t("2017-01-01T01:00:00"), INTERNATIONAL)).isEqualTo (100);
        assertThat (x.getCharge (89, t("2017-01-01T00:00:00"), t("2017-01-01T01:00:00"), INTERNATIONAL)).isEqualTo (100);
        assertThat (x.getCharge (90, t("2017-01-01T00:00:00"), t("2017-01-01T01:00:00"), INTERNATIONAL)).isEqualTo (100);

        assertThat (x.getCharge (90.1, t("2017-01-01T00:00:00"), t("2017-01-01T01:00:00"), INTERNATIONAL)).isEqualTo (140);
        assertThat (x.getCharge (129, t("2017-01-01T00:00:00"), t("2017-01-01T01:00:00"), INTERNATIONAL)).isEqualTo (140);
        assertThat (x.getCharge (130, t("2017-01-01T00:00:00"), t("2017-01-01T01:00:00"), INTERNATIONAL)).isEqualTo (140);
        assertThat (x.getCharge (131, t("2017-01-01T00:00:00"), t("2017-01-01T01:00:00"), INTERNATIONAL)).isEqualTo (140);
        assertThat (x.getCharge (999, t("2017-01-01T00:00:00"), t("2017-01-01T01:00:00"), INTERNATIONAL)).isEqualTo (140);

    }

    @Test
    public void testBasicMtowOrder() {
        final ParkingChargesImpl x = load ("mtow_order");

        assertThat (x.getCharge (0, t("2017-01-01T00:00:00"), t("2017-01-01T01:00:00"), INTERNATIONAL)).isEqualTo (20);
        assertThat (x.getCharge (1, t("2017-01-01T00:00:00"), t("2017-01-01T01:00:00"), INTERNATIONAL)).isEqualTo (20);
        assertThat (x.getCharge (9.9, t("2017-01-01T00:00:00"), t("2017-01-01T01:00:00"), INTERNATIONAL)).isEqualTo (20);
        assertThat (x.getCharge (10, t("2017-01-01T00:00:00"), t("2017-01-01T01:00:00"), INTERNATIONAL)).isEqualTo (20);

        assertThat (x.getCharge (10.1, t("2017-01-01T00:00:00"), t("2017-01-01T01:00:00"), INTERNATIONAL)).isEqualTo (60);
        assertThat (x.getCharge (49.9, t("2017-01-01T00:00:00"), t("2017-01-01T01:00:00"), INTERNATIONAL)).isEqualTo (60);
        assertThat (x.getCharge (50, t("2017-01-01T00:00:00"), t("2017-01-01T01:00:00"), INTERNATIONAL)).isEqualTo (60);

        assertThat (x.getCharge (51, t("2017-01-01T00:00:00"), t("2017-01-01T01:00:00"), INTERNATIONAL)).isEqualTo (100);
        assertThat (x.getCharge (89, t("2017-01-01T00:00:00"), t("2017-01-01T01:00:00"), INTERNATIONAL)).isEqualTo (100);
        assertThat (x.getCharge (90, t("2017-01-01T00:00:00"), t("2017-01-01T01:00:00"), INTERNATIONAL)).isEqualTo (100);

        assertThat (x.getCharge (90.1, t("2017-01-01T00:00:00"), t("2017-01-01T01:00:00"), INTERNATIONAL)).isEqualTo (140);
        assertThat (x.getCharge (129, t("2017-01-01T00:00:00"), t("2017-01-01T01:00:00"), INTERNATIONAL)).isEqualTo (140);
        assertThat (x.getCharge (130, t("2017-01-01T00:00:00"), t("2017-01-01T01:00:00"), INTERNATIONAL)).isEqualTo (140);
        assertThat (x.getCharge (131, t("2017-01-01T00:00:00"), t("2017-01-01T01:00:00"), INTERNATIONAL)).isEqualTo (140);
        assertThat (x.getCharge (999, t("2017-01-01T00:00:00"), t("2017-01-01T01:00:00"), INTERNATIONAL)).isEqualTo (140);

    }

    @Test
    public void testHour_0() {
        final ParkingChargesImpl x = load ("hour_0");
        assertThat (x.getCharge (0, t("2017-01-01T00:00:00"), t("2017-01-01T00:00:01"), INTERNATIONAL)).isEqualTo (20);
        assertThat (x.getCharge (0, t("2017-01-01T00:00:00"), t("2017-01-01T00:59:59"), INTERNATIONAL)).isEqualTo (20);
        assertThat (x.getCharge (0, t("2017-01-01T00:00:00"), t("2017-01-01T01:00:00"), INTERNATIONAL)).isEqualTo (20);
        assertThat (x.getCharge (0, t("2017-01-01T00:00:00"), t("2017-01-01T01:00:01"), INTERNATIONAL)).isEqualTo (40);
        assertThat (x.getCharge (0, t("2017-01-01T22:30:00"), t("2017-01-02T01:30:00"), INTERNATIONAL)).isEqualTo (60);
    }

    @Test
    public void testHour_1() {
        final ParkingChargesImpl x = load ("hour_1");
        assertThat (x.getCharge (0, t("2017-01-01T00:00:00"), t("2017-01-01T00:00:01"), INTERNATIONAL)).isEqualTo (0);
        assertThat (x.getCharge (0, t("2017-01-01T00:00:00"), t("2017-01-01T00:59:59"), INTERNATIONAL)).isEqualTo (0);
        assertThat (x.getCharge (0, t("2017-01-01T00:00:00"), t("2017-01-01T01:00:00"), INTERNATIONAL)).isEqualTo (0);
        assertThat (x.getCharge (0, t("2017-01-01T00:00:00"), t("2017-01-01T01:00:01"), INTERNATIONAL)).isEqualTo (20);
        assertThat (x.getCharge (0, t("2017-01-01T00:00:00"), t("2017-01-01T01:59:59"), INTERNATIONAL)).isEqualTo (20);
        assertThat (x.getCharge (0, t("2017-01-01T00:00:00"), t("2017-01-01T02:00:00"), INTERNATIONAL)).isEqualTo (20);
        assertThat (x.getCharge (0, t("2017-01-01T00:00:00"), t("2017-01-01T02:00:01"), INTERNATIONAL)).isEqualTo (40);
    }

    @Test
    public void testDay_0() {
        final ParkingChargesImpl x = load ("day_0");
        assertThat (x.getCharge (0, t("2017-01-01T00:00:00"), t("2017-01-01T00:00:01"), DOMESTIC)).isEqualTo (30);
        assertThat (x.getCharge (0, t("2017-01-01T01:00:00"), t("2017-01-01T01:59:59"), DOMESTIC)).isEqualTo (30);
        assertThat (x.getCharge (0, t("2017-01-01T01:00:00"), t("2017-01-01T02:00:00"), DOMESTIC)).isEqualTo (30);
        assertThat (x.getCharge (0, t("2017-01-01T01:00:00"), t("2017-01-01T02:00:01"), DOMESTIC)).isEqualTo (30);
        assertThat (x.getCharge (0, t("2017-01-01T00:00:00"), t("2017-01-01T23:59:00"), DOMESTIC)).isEqualTo (30);
        assertThat (x.getCharge (0, t("2017-01-01T18:30:00"), t("2017-01-02T01:00:00"), DOMESTIC)).isEqualTo (60);
        assertThat (x.getCharge (0, t("2017-01-01T23:59:00"), t("2017-01-02T00:00:00"), DOMESTIC)).isEqualTo (30);
        assertThat (x.getCharge (0, t("2017-01-01T05:00:00"), t("2017-01-02T01:00:00"), DOMESTIC)).isEqualTo (60);
        assertThat (x.getCharge (0, t("2017-01-01T05:00:00"), t("2017-01-03T01:00:00"), DOMESTIC)).isEqualTo (90);
    }

    @Test
    public void testDay_1() {
        final ParkingChargesImpl x = load ("day_1");
        assertThat (x.getCharge (0, t("2017-01-01T00:00:00"), t("2017-01-01T00:00:01"), DOMESTIC)).isEqualTo (0);
        assertThat (x.getCharge (0, t("2017-01-01T01:00:00"), t("2017-01-01T01:59:59"), DOMESTIC)).isEqualTo (0);
        assertThat (x.getCharge (0, t("2017-01-01T01:00:00"), t("2017-01-01T02:00:00"), DOMESTIC)).isEqualTo (0);
        assertThat (x.getCharge (0, t("2017-01-01T01:00:00"), t("2017-01-01T02:00:01"), DOMESTIC)).isEqualTo (30);
        assertThat (x.getCharge (0, t("2017-01-01T00:00:00"), t("2017-01-01T23:59:00"), DOMESTIC)).isEqualTo (30);
        assertThat (x.getCharge (0, t("2017-01-01T18:30:00"), t("2017-01-02T01:00:00"), DOMESTIC)).isEqualTo (30);
        assertThat (x.getCharge (0, t("2017-01-01T23:59:00"), t("2017-01-02T01:00:00"), DOMESTIC)).isEqualTo (0);
        assertThat (x.getCharge (0, t("2017-01-01T05:00:00"), t("2017-01-02T01:00:00"), DOMESTIC)).isEqualTo (30);
        assertThat (x.getCharge (0, t("2017-01-01T05:00:00"), t("2017-01-03T01:00:00"), DOMESTIC)).isEqualTo (60);
    }

    @Test
    public void test24hr_0() {
        final ParkingChargesImpl x = load ("24hr_0");
        assertThat (x.getCharge (0, t("2017-01-01T00:00:00"), t("2017-01-01T00:00:01"), REGIONAL)).isEqualTo (40);
        assertThat (x.getCharge (0, t("2017-01-01T00:00:00"), t("2017-01-01T00:59:59"), REGIONAL)).isEqualTo (40);
        assertThat (x.getCharge (0, t("2017-01-01T00:00:00"), t("2017-01-01T01:00:00"), REGIONAL)).isEqualTo (40);
        assertThat (x.getCharge (0, t("2017-01-01T00:00:00"), t("2017-01-01T23:59:59"), REGIONAL)).isEqualTo (40);
        assertThat (x.getCharge (0, t("2017-01-01T00:00:00"), t("2017-01-02T00:00:00"), REGIONAL)).isEqualTo (40);
        assertThat (x.getCharge (0, t("2017-01-01T00:00:00"), t("2017-01-02T00:00:01"), REGIONAL)).isEqualTo (80);
        assertThat (x.getCharge (0, t("2017-01-01T00:00:00"), t("2017-01-02T23:59:00"), REGIONAL)).isEqualTo (80);
        assertThat (x.getCharge (0, t("2017-01-01T00:00:00"), t("2017-01-03T00:00:00"), REGIONAL)).isEqualTo (80);
        assertThat (x.getCharge (0, t("2017-01-01T00:00:00"), t("2017-01-03T00:00:01"), REGIONAL)).isEqualTo (120);
    }

    @Test
    public void test24hr_1() {
        final ParkingChargesImpl x = load ("24hr_1");
        assertThat (x.getCharge (0, t("2017-01-01T00:00:00"), t("2017-01-01T00:00:01"), REGIONAL)).isEqualTo (0);
        assertThat (x.getCharge (0, t("2017-01-01T00:00:00"), t("2017-01-01T00:59:59"), REGIONAL)).isEqualTo (0);
        assertThat (x.getCharge (0, t("2017-01-01T00:00:00"), t("2017-01-01T01:00:00"), REGIONAL)).isEqualTo (0);
        assertThat (x.getCharge (0, t("2017-01-01T00:00:00"), t("2017-01-01T01:00:01"), REGIONAL)).isEqualTo (40);
        assertThat (x.getCharge (0, t("2017-01-01T00:00:00"), t("2017-01-01T23:59:59"), REGIONAL)).isEqualTo (40);
        assertThat (x.getCharge (0, t("2017-01-01T00:00:00"), t("2017-01-02T00:00:00"), REGIONAL)).isEqualTo (0);
        assertThat (x.getCharge (0, t("2017-01-01T00:00:00"), t("2017-01-02T00:00:01"), REGIONAL)).isEqualTo (40);
        assertThat (x.getCharge (0, t("2017-01-01T00:00:00"), t("2017-01-02T23:59:00"), REGIONAL)).isEqualTo (80);
        assertThat (x.getCharge (0, t("2017-01-01T00:00:00"), t("2017-01-03T00:00:00"), REGIONAL)).isEqualTo (40);
        assertThat (x.getCharge (0, t("2017-01-01T00:00:00"), t("2017-01-03T00:00:01"), REGIONAL)).isEqualTo (80);
    }

    @Test
    public void testBadInput() {
        assertThatThrownBy (()->load ("x_freeMissing"))
            .isInstanceOf(InvalidSpreadsheetLayoutException.class)
            .hasMessageMatching (".*D5:.*invalid or missing free.*");
        assertThatThrownBy (()->load ("x_freeNegative"))
            .isInstanceOf(InvalidSpreadsheetLayoutException.class)
            .hasMessageMatching (".*D5:.*invalid or missing free.*");

        assertThatThrownBy (()->load ("x_basisMissing"))
            .isInstanceOf(InvalidSpreadsheetLayoutException.class)
            .hasMessageMatching (".*C6:.*invalid or missing charges basis.*");
        assertThatThrownBy (()->load ("x_basisInvalid"))
            .isInstanceOf(InvalidSpreadsheetLayoutException.class)
            .hasMessageMatching (".*C6:.*invalid or missing charges basis.*");
    }

}
