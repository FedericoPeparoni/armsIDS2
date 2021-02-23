package ca.ids.abms.spreadsheets.impl;

import org.junit.BeforeClass;
import org.junit.Test;

import ca.ids.abms.spreadsheets.FlightChargeType;
import ca.ids.abms.spreadsheets.SSException;

import static org.assertj.core.api.Assertions.*;

public class MtowChargesFinderTest {
    
    public static final String TEST_FILE = "MtowChargesFinderTest.xlsx";
    private static WorkbookHelper helper;
    
    @BeforeClass
    public static void init() {
        helper = new WorkbookHelper (
                ByteArrayLoader.loadResource (
                        MtowChargesFinderTest.class, TEST_FILE, Integer.MAX_VALUE));
    }
    
    @Test
    public void testBasic() {
        final MtowChargesFinder.Result res = MtowChargesFinder.find (helper, "basic");
        assertThat (res).isNotNull();
        assertThat (res.mtow.formatAsString()).isEqualTo("B8");
        assertThat (res.mtowData.formatAsString()).isEqualTo("B9:B10");
        assertThat (res.charges.get (FlightChargeType.INTERNATIONAL).formatAsString()).isEqualTo("C8:D10");
        assertThat (res.charges.get (FlightChargeType.DOMESTIC).formatAsString()).isEqualTo("F8:G10");
        assertThat (res.charges.get (FlightChargeType.REGIONAL).formatAsString()).isEqualTo("I8:I10");
    }
    
    @Test
    public void testScanSheets() throws Exception {
        final MtowChargesFinder.Result res = MtowChargesFinder.find (helper);
        assertThat (res).isNotNull();
        assertThat (res.mtowData.formatAsString()).isEqualTo("B9:B10");
        assertThat (res.charges.get (FlightChargeType.INTERNATIONAL).formatAsString()).isEqualTo("C8:D10");
    }
    
    @Test
    public void testBadMtow() {
        assertThat (MtowChargesFinder.find (helper, "no_mtow")).isNull();
        
        assertThatThrownBy(()->MtowChargesFinder.find (helper, "bad_mtow"))
            .isInstanceOf (SSException.class)
            .hasMessageMatching(".*B5:.*negative MTOW.*");
    }
    
    @Test
    public void testBadCharges() {
        assertThatThrownBy(()->MtowChargesFinder.find (helper, "no_charge"))
            .isInstanceOf (SSException.class)
            .hasMessageMatching(".*D5:.*invalid empty or non-numeric charge.*");
        
        assertThatThrownBy(()->MtowChargesFinder.find (helper, "bad_charge"))
        .isInstanceOf (SSException.class)
        .hasMessageMatching(".*E6:.*negative charge.*-888.*");
    }
    
}
