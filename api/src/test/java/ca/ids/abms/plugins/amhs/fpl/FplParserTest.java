package ca.ids.abms.plugins.amhs.fpl;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import ca.ids.abms.plugins.amhs.AmhsMessageType;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.apache.commons.lang3.StringUtils;

import static org.apache.commons.lang.StringUtils.stripToNull;

@Ignore
public class FplParserTest {

    // @Test
    public void testFplBasic() {
        final FlightMessage m = flightMessageParser.parse("(FPL-FDS01-IX\n" + "-PC12/L-SDFG/S\n" + "-HKNW0430\n"
                + "-N0240F250 NAVEX DCT\n" + "-HKML0100 HKMO\n" + "-DOF/180704 REG/5YFDF OPR/AMREF)\n" + "",
                LocalDateTime.of (2020, 1, 1, 0, 0));
        assertThat(m).isNotNull();
        assertThat (m.item3).isNotNull();
        assertThat (m.item7).isNotNull();
        assertThat (m.item8).isNotNull();
        
        assertThat(m.item3.type).isEqualTo(AmhsMessageType.FPL);
        assertThat(m.item7.callsign).isEqualTo("FDS01");
        assertThat(m.item8.flightRules).isEqualTo("I");
        assertThat(m.item8.flightType).isEqualTo("X");
    }

    public void testFplAgainstCronosRef(final String filename, final CronosProcessedFpl ref, final FlightMessage m) {
        if (ref.catalogue_fpl_id == 7839) {
            return;
        }
        assertThat (m.item3).isNotNull();
        assertThat (m.item7).isNotNull();
        assertThat (m.item8).isNotNull();
        assertThat (m.item9).isNotNull();
        assertThat (m.item10).isNotNull();
        assertThat (m.item13).isNotNull();
        assertThat (m.item15).isNotNull();
        assertThat (m.item16).isNotNull();
        
        assertThat(m.item3.type).isEqualTo(AmhsMessageType.FPL);
        assertThat(m.item7.callsign).isEqualTo(stripToNull(ref.flight_id));
        assertThat(m.item8.flightRules).isEqualTo(stripToNull(ref.flight_rules));
        assertThat(m.item8.flightType).isEqualTo(stripToNull(ref.flight_type));
        if (!StringUtils.equals(ref.aircraft_number, "01") && !StringUtils.equals(ref.aircraft_number, "1")) {
            assertThat(m.item9.aircraftNumber).isEqualTo(stripToNull(ref.aircraft_number));
        }
        assertThat(m.item9.aircraftType).isEqualTo(stripToNull(ref.aircraft_type));
        assertThat(m.item9.wakeTurb).isEqualTo(stripToNull(ref.wake_turb));
        assertThat(m.item10.equip).isEqualTo(stripToNull(ref.equipment));
        assertThat(m.item13.departureAirport).isEqualTo(stripToNull(ref.departure_ad));
        assertThat(m.item13.departureTime).isEqualTo(stripToNull(ref.departure_time));
        assertThat(m.item15.cruisingSpeed).isEqualTo(stripToNull(ref.speed));
        assertThat(m.item15.flightLevel).isEqualTo(stripToNull(ref.flight_level));
        assertThat(m.item15.route).isEqualTo(stripToNull(ref.route));
        assertThat(m.item16.destinationAirport).isEqualTo(stripToNull(ref.destination_ad));
        assertThat(m.item16.altDestinationAirport).isEqualTo(stripToNull(ref.alternate_ad));
        assertThat(m.item16.altDestinationAirport2).isEqualTo(stripToNull(ref.alternate_ad_2));
        assertThat(m.item16.totalEet).isEqualTo(stripToNull(ref.total_eet));
        assertThat(m.item18.otherInfo).isEqualTo(stripToNull(ref.other_info));
        assertThat(m.item18.dayOfFlight).isEqualTo(ref.getDayOfFlight());

    }

    public void testFplAgainstCronosRef(final RefMessageResource r) {
        LOG.info("checking {}", r.name);
        final CronosProcessedFpl ref = r
                .read(stream -> TestUtils.readJson(objectMapper, stream, CronosProcessedFpl.class));
        final String raw = ref.raw_fpl;
        try {
            final FlightMessage m = flightMessageParser.parse (raw, ref.com_filing_datetime);
            testFplAgainstCronosRef(r.name, ref, m);
        }
        catch (final AssertionError | RuntimeException x) {
            LOG.error ("failed {}", r.name, x);
            throw x;
        }
    }

    @Test
    public void testFplAgainstCronosRef() {
        TestUtils.readJsonFileNamesFromDir (CRONOS_FPL_REF_DIR).forEach (this::testFplAgainstCronosRef);
        //testFplAgainstCronosRef(new RefMessageResource("Z:\\db_scripts\\messages\\fpl\\007839.json"));
        //testFplAgainstCronosRef(new RefMessageResource("Z:\\db_scripts\\messages\\fpl\\008601.json"));
        //testFplAgainstCronosRef(new RefMessageResource("Z:\\db_scripts\\messages\\fpl\\009400.json"));
        //testFplAgainstCronosRef(new RefMessageResource("Z:\\db_scripts\\messages\\fpl\\000045.json"));
    }

    // --------------------- private ---------------------------

    @BeforeClass
    public static void initClass() {
        objectMapper = TestUtils.createObjectMapper();
    }

    private static final String CRONOS_FPL_REF_DIR = "Z:\\db_scripts\\messages\\fpl";
    private static final FlightMessageParser flightMessageParser = new FlightMessageParser();
    private static ObjectMapper objectMapper;
    private static final Logger LOG = LoggerFactory.getLogger(FplParserTest.class);

}
