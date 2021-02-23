package ca.ids.abms.modules.radarsummary;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by s.craymer on 15/08/2017.
 */
public class RadarSummaryTest {

    private RadarSummary radarSummary;

    @Before
    public void setup() {
        radarSummary = new RadarSummary();
        radarSummary.setRegistration("   5NMJO  EET   ");
    }

    @Test
    public void setRegistration() throws Exception {
        assertThat(radarSummary.getRegistration()).isEqualTo("5NMJO");
    }
}
