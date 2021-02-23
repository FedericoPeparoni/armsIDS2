package ca.ids.abms.spreadsheets.impl;

import org.junit.Test;

import ca.ids.abms.spreadsheets.SSException;

import static org.assertj.core.api.Assertions.assertThat;

public class SimpleTimeRangeTest {
    
    @Test
    public void testParseTime_basic() throws Exception {
        assertThat (SimpleTimeRange.parseTime ("11:22")).isEqualTo(SimpleTimeRange.secondOfDay (11, 22));
        assertThat (SimpleTimeRange.parseTime ("00:00")).isEqualTo(SimpleTimeRange.secondOfDay (0, 0));
        assertThat (SimpleTimeRange.parseTime ("23:59")).isEqualTo(SimpleTimeRange.secondOfDay (23, 59));
        
        assertThat (SimpleTimeRange.parseTime ("1122")).isEqualTo(SimpleTimeRange.secondOfDay (11, 22));
        assertThat (SimpleTimeRange.parseTime ("0000")).isEqualTo(SimpleTimeRange.secondOfDay (0, 0));
        assertThat (SimpleTimeRange.parseTime ("2359")).isEqualTo(SimpleTimeRange.secondOfDay (23, 59));
    }
    
    @Test(expected = SSException.class)
    public void testParseTime_hoursTooLarge() throws Exception {
        SimpleTimeRange.parseTime ("24:00");
    }
    
    @Test(expected = SSException.class)
    public void testParseTime_minutesTooLarge() throws Exception {
        SimpleTimeRange.parseTime ("00:60");
    }
    
    @Test(expected = SSException.class)
    public void testParseTime_timeTooLarge() throws Exception {
        SimpleTimeRange.parseTime ("24:00");
    }
    
    @Test
    public void testParseTime2400_basic() throws Exception {
        assertThat (SimpleTimeRange.parseTime ("11:22")).isEqualTo(SimpleTimeRange.secondOfDay (11, 22));
        assertThat (SimpleTimeRange.parseTime ("00:00")).isEqualTo(SimpleTimeRange.secondOfDay (0, 0));
        assertThat (SimpleTimeRange.parseTime ("23:59")).isEqualTo(SimpleTimeRange.secondOfDay (23, 59));
        assertThat (SimpleTimeRange.parseTime2400 ("24:00")).isEqualTo(SimpleTimeRange.secondOfDay2400 (24, 00));
        
        assertThat (SimpleTimeRange.parseTime ("1122")).isEqualTo(SimpleTimeRange.secondOfDay (11, 22));
        assertThat (SimpleTimeRange.parseTime ("0000")).isEqualTo(SimpleTimeRange.secondOfDay (0, 0));
        assertThat (SimpleTimeRange.parseTime ("2359")).isEqualTo(SimpleTimeRange.secondOfDay (23, 59));
        assertThat (SimpleTimeRange.parseTime2400 ("2400")).isEqualTo(SimpleTimeRange.secondOfDay2400 (24, 00));
    }
    
    @Test(expected = SSException.class)
    public void testParseTime2400_hoursTooLarge() throws Exception {
        SimpleTimeRange.parseTime ("25:00");
    }
    
    @Test(expected = SSException.class)
    public void testParseTime2400_minutesTooLarge() throws Exception {
        SimpleTimeRange.parseTime ("00:60");
    }
    
    @Test(expected = SSException.class)
    public void testParseTime2400_timeTooLarge() throws Exception {
        SimpleTimeRange.parseTime ("24:01");
    }
    
    @Test(expected = SSException.class)
    public void testParseTime_bad24hours() throws Exception {
        SimpleTimeRange.parseTime ("24:01");
    }
    
    @Test(expected = SSException.class)
    public void testParseTime_bad() throws Exception {
        SimpleTimeRange.parseTime ("abc");
    }
    
    @Test
    public void testParseRange_basic() throws Exception {
        SimpleTimeRange x;
        x = SimpleTimeRange.parseRange ("09:11-22:33");
        assertThat (x.startSecond).isNotNull();
        assertThat (x.endSecond).isNotNull();
        assertThat (x.startSecond).isEqualTo(SimpleTimeRange.secondOfDay(9, 11));
        assertThat (x.endSecond).isEqualTo(SimpleTimeRange.secondOfDay(22, 33));
        
        x = SimpleTimeRange.parseRange ("09:11 - 22:33");
        assertThat (x.startSecond).isEqualTo(SimpleTimeRange.secondOfDay(9, 11));
        assertThat (x.endSecond).isEqualTo(SimpleTimeRange.secondOfDay(22, 33));
        
        x = SimpleTimeRange.parseRange ("09:11 to 22:33");
        assertThat (x.startSecond).isEqualTo(SimpleTimeRange.secondOfDay(9, 11));
        assertThat (x.endSecond).isEqualTo(SimpleTimeRange.secondOfDay(22, 33));
        
        x = SimpleTimeRange.parseRange ("09:11 TO 22:33");
        assertThat (x.startSecond).isEqualTo(SimpleTimeRange.secondOfDay(9, 11));
        assertThat (x.endSecond).isEqualTo(SimpleTimeRange.secondOfDay(22, 33));
        
        x = SimpleTimeRange.parseRange ("09:11to22:33");
        assertThat (x.startSecond).isEqualTo(SimpleTimeRange.secondOfDay(9, 11));
        assertThat (x.endSecond).isEqualTo(SimpleTimeRange.secondOfDay(22, 33));
        
        x = SimpleTimeRange.parseRange ("09:11 - 24:00");
        assertThat (x.startSecond).isEqualTo(SimpleTimeRange.secondOfDay(9, 11));
        assertThat (x.endSecond).isEqualTo(SimpleTimeRange.secondOfDay2400(24, 00));
    }
    
    @Test
    public void testParseRange_partial() throws Exception {
        SimpleTimeRange x;
        x = SimpleTimeRange.parseRange ("22:33");
        assertThat (x.startSecond).isNull();
        assertThat (x.endSecond).isNotNull();
        assertThat (x.endSecond).isEqualTo(SimpleTimeRange.secondOfDay(22, 33));
    }
        
    @Test(expected = SSException.class)
    public void testParseRange_order() throws Exception {
        SimpleTimeRange.parseRange ("22:33-09:11");
    }
    
}
