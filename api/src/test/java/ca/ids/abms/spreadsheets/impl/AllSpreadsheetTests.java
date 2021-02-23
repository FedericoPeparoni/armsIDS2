package ca.ids.abms.spreadsheets.impl;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith (Suite.class)
@Suite.SuiteClasses({
    ByteArrayLoaderTest.class,
    SimpleTimeRangeTest.class,
    MtowChargesFinderTest.class,
    AerodromeChargesTest.class,
    ParkingChargesTest.class,
})
public class AllSpreadsheetTests {
}
