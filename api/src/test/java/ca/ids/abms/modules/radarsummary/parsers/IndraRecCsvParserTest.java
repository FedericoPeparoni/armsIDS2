package ca.ids.abms.modules.radarsummary.parsers;

import static org.assertj.core.api.Assertions.*;

import java.io.InputStream;
import java.util.List;

import org.junit.Test;

import ca.ids.abms.modules.radarsummary.RadarSummaryCsvViewModel;
import ca.ids.abms.modules.radarsummary.enumerators.RadarSummaryFormat;
import ca.ids.abms.util.csv.CsvFormatException;

public class IndraRecCsvParserTest {
    
    @Test
    public void testGuessFormatVersion() {
        final String[] fields = new String[50];
        
        assertThatThrownBy(()->IndraRecCsvParser.guessFormatVersion (fields)).isInstanceOf(CsvFormatException.class);
        
        fields[12] = "01-02-19 13:14:15";
        assertThat (IndraRecCsvParser.guessFormatVersion (fields)).isEqualTo(IndraRecCsvParser.FormatVersion.OLD);
        
        fields[12] = "01-02-19";
        assertThat (IndraRecCsvParser.guessFormatVersion (fields)).isEqualTo(IndraRecCsvParser.FormatVersion.OLD);
        
        fields[12] = "123";
        fields[48] = "2018-01-01";
        assertThat (IndraRecCsvParser.guessFormatVersion (fields)).isEqualTo(IndraRecCsvParser.FormatVersion.NEW);
        
    }
    
    @Test
    public void testNormalizeCruisingSpeed() {
        assertThat (IndraRecCsvParser.normalizeCruisingSpeedInKnots(null)).isNull();
        assertThat (IndraRecCsvParser.normalizeCruisingSpeedInKnots("  ")).isNull();
        
        assertThat (IndraRecCsvParser.normalizeCruisingSpeedInKnots("M1")).isEqualTo("M001");
        assertThat (IndraRecCsvParser.normalizeCruisingSpeedInKnots("M12")).isEqualTo("M012");
        assertThat (IndraRecCsvParser.normalizeCruisingSpeedInKnots("M123")).isEqualTo("M123");
        assertThat (IndraRecCsvParser.normalizeCruisingSpeedInKnots("M1234")).isEqualTo("M1234");
        
        assertThat (IndraRecCsvParser.normalizeCruisingSpeedInKnots("N1")).isEqualTo("N0001");
        assertThat (IndraRecCsvParser.normalizeCruisingSpeedInKnots("N12")).isEqualTo("N0012");
        assertThat (IndraRecCsvParser.normalizeCruisingSpeedInKnots("N123")).isEqualTo("N0123");
        assertThat (IndraRecCsvParser.normalizeCruisingSpeedInKnots("N1234")).isEqualTo("N1234");
        assertThat (IndraRecCsvParser.normalizeCruisingSpeedInKnots("N12345")).isEqualTo("N12345");
        
        assertThat (IndraRecCsvParser.normalizeCruisingSpeedInKnots("1")).isEqualTo("N0001");
        assertThat (IndraRecCsvParser.normalizeCruisingSpeedInKnots("12")).isEqualTo("N0012");
        assertThat (IndraRecCsvParser.normalizeCruisingSpeedInKnots("123")).isEqualTo("N0123");
        assertThat (IndraRecCsvParser.normalizeCruisingSpeedInKnots("1234")).isEqualTo("N1234");
        assertThat (IndraRecCsvParser.normalizeCruisingSpeedInKnots("12345")).isEqualTo("N12345");
        
        assertThat (IndraRecCsvParser.normalizeCruisingSpeedInKnots("K1")).isEqualTo("K0001");
        assertThat (IndraRecCsvParser.normalizeCruisingSpeedInKnots("K12")).isEqualTo("K0012");
        assertThat (IndraRecCsvParser.normalizeCruisingSpeedInKnots("K123")).isEqualTo("K0123");
        assertThat (IndraRecCsvParser.normalizeCruisingSpeedInKnots("K1234")).isEqualTo("K1234");
        assertThat (IndraRecCsvParser.normalizeCruisingSpeedInKnots("K12345")).isEqualTo("K12345");
        
        assertThat (IndraRecCsvParser.normalizeCruisingSpeedInKnots(" XYZ")).isEqualTo("XYZ");
    }
    
    @Test
    public void testNormalizeCruisingLevel() {
        assertThat (IndraRecCsvParser.normalizeCruisingLevel(null)).isNull();
        assertThat (IndraRecCsvParser.normalizeCruisingLevel("  ")).isNull();
        
        assertThat (IndraRecCsvParser.normalizeCruisingLevel("F1")).isEqualTo("F001");
        assertThat (IndraRecCsvParser.normalizeCruisingLevel("F12")).isEqualTo("F012");
        assertThat (IndraRecCsvParser.normalizeCruisingLevel("F123")).isEqualTo("F123");
        assertThat (IndraRecCsvParser.normalizeCruisingLevel("F1234")).isEqualTo("F1234");
        
        assertThat (IndraRecCsvParser.normalizeCruisingLevel("A1")).isEqualTo("A001");
        assertThat (IndraRecCsvParser.normalizeCruisingLevel("A12")).isEqualTo("A012");
        assertThat (IndraRecCsvParser.normalizeCruisingLevel("A123")).isEqualTo("A123");
        assertThat (IndraRecCsvParser.normalizeCruisingLevel("A1234")).isEqualTo("A1234");
        
        assertThat (IndraRecCsvParser.normalizeCruisingLevel("S1")).isEqualTo("S0001");
        assertThat (IndraRecCsvParser.normalizeCruisingLevel("S12")).isEqualTo("S0012");
        assertThat (IndraRecCsvParser.normalizeCruisingLevel("S123")).isEqualTo("S0123");
        assertThat (IndraRecCsvParser.normalizeCruisingLevel("S1234")).isEqualTo("S1234");
        assertThat (IndraRecCsvParser.normalizeCruisingLevel("S12345")).isEqualTo("S12345");
        
        assertThat (IndraRecCsvParser.normalizeCruisingLevel("M1")).isEqualTo("M0001");
        assertThat (IndraRecCsvParser.normalizeCruisingLevel("M12")).isEqualTo("M0012");
        assertThat (IndraRecCsvParser.normalizeCruisingLevel("M123")).isEqualTo("M0123");
        assertThat (IndraRecCsvParser.normalizeCruisingLevel("M1234")).isEqualTo("M1234");
        assertThat (IndraRecCsvParser.normalizeCruisingLevel("M12345")).isEqualTo("M12345");
        
        assertThat (IndraRecCsvParser.normalizeCruisingLevel("1")).isEqualTo("F001");
        assertThat (IndraRecCsvParser.normalizeCruisingLevel("12")).isEqualTo("F012");
        assertThat (IndraRecCsvParser.normalizeCruisingLevel("123")).isEqualTo("F123");
        assertThat (IndraRecCsvParser.normalizeCruisingLevel("1234")).isEqualTo("F1234");
        
        assertThat (IndraRecCsvParser.normalizeCruisingLevel(" XYZ")).isEqualTo("XYZ");
    }
    
    
    @Test
    public void testOldFormat() throws Exception {
        final IndraRecCsvParser p = new IndraRecCsvParser();
        final List <RadarSummaryCsvViewModel> list = p.parseStream (openTestFile ("indra_old_test_1.csv"));
        assertThat (list.size()).isGreaterThanOrEqualTo(1);
        final RadarSummaryCsvViewModel x = list.get (0);
        assertThat (x.getDate()).isEqualTo ("2018-10-14");
        assertThat (x.getFlightId()).isEqualTo ("AUT2509");
        assertThat (x.getDayOfFlight()).isNull();
        assertThat (x.getDepartureTime()).isNull();
        assertThat (x.getRegistration()).isEqualTo("LVCHS");
        assertThat (x.getAircraftType()).isEqualTo("E190");
        assertThat (x.getDepartureAeroDrome()).isEqualTo("SACO");
        assertThat (x.getDestinationAeroDrome()).isEqualTo("SAEZ");
        assertThat (x.getFlightRule()).isNull();
        assertThat (x.getFlightTravelCategory()).isNull();
        assertThat (x.getFlightType()).isEqualTo("S");
        assertThat (x.getDestTime()).isEqualTo("2300");
        assertThat (x.getOperatorName()).isEqualTo("AUT");
        assertThat (x.getFormat()).isEqualTo(RadarSummaryFormat.INDRA_REC);
        assertThat (x.getCruisingSpeed()).isEqualTo("N0458");
        assertThat (x.getFlightLevel()).isEqualTo("F390");
        assertThat (x.getWakeTurb()).isEqualTo("M");
        
        assertThat (x.getFirEntryPoint()).isEqualTo("ASEMI");
        assertThat (x.getFirEntryTime()).isEqualTo("2204");
        assertThat (x.getFirEntryFlightLevel()).isEqualTo("56");
        assertThat (x.getFirExitPoint()).isEqualTo("KIKIP");
        assertThat (x.getFirExitTime()).isEqualTo("2231");
        assertThat (x.getFirExitFlightLevel()).isEqualTo("290");
        assertThat (x.getRoute()).isEqualTo("ASEMI UTRAX MJZ KIKIP");
        assertThat (x.getFixes()).isEqualTo("2204-ASEMI-56, 2212-UTRAX-223, 2223-MJZ-310, 2231-KIKIP-290");
    }
    
    @Test
    public void testNewFormat() throws Exception {
        final IndraRecCsvParser p = new IndraRecCsvParser();
        final List <RadarSummaryCsvViewModel> list = p.parseStream (openTestFile ("indra_new_test_1.csv"));
        assertThat (list.size()).isGreaterThanOrEqualTo(1);
        final RadarSummaryCsvViewModel x = list.get (0);
        assertThat (x.getDate()).isNull();
        assertThat (x.getFlightId()).isEqualTo ("LVS025");
        assertThat (x.getDayOfFlight()).isEqualTo ("2018-10-14");
        assertThat (x.getDepartureTime()).isEqualTo("21:50");
        assertThat (x.getRegistration()).isEqualTo("N568TA");
        assertThat (x.getAircraftType()).isEqualTo("SIRA");
        assertThat (x.getDepartureAeroDrome()).isEqualTo("SAZN");
        assertThat (x.getDestinationAeroDrome()).isEqualTo("SAMR");
        assertThat (x.getFlightRule()).isEqualTo("V");
        assertThat (x.getFlightTravelCategory()).isNull();
        assertThat (x.getFlightType()).isEqualTo("G");
        assertThat (x.getDestTime()).isEqualTo("22:50");
        assertThat (x.getOperatorName()).isEqualTo("DACOSTA");
        assertThat (x.getFormat()).isEqualTo(RadarSummaryFormat.INDRA_REC);
        assertThat (x.getCruisingSpeed()).isEqualTo("N0100");
        assertThat (x.getFlightLevel()).isEqualTo("F110");
        assertThat (x.getWakeTurb()).isEqualTo("L");
        
        assertThat (x.getFirEntryPoint()).isEqualTo("3748S06812W");
        assertThat (x.getFirEntryTime()).isEqualTo("2057");
        assertThat (x.getFirEntryFlightLevel()).isEqualTo("110");
        assertThat (x.getFirExitPoint()).isEqualTo("SRA");
        assertThat (x.getFirExitTime()).isEqualTo("2250");
        assertThat (x.getFirExitFlightLevel()).isEqualTo("028");
        assertThat (x.getRoute()).isEqualTo("3748S06812W SRA");
        assertThat (x.getFixes()).isEqualTo("2057-3748S06812W-110, 2250-SRA-028");
    }
    
    private InputStream openTestFile (final String fileName) {
        return this.getClass().getResourceAsStream (fileName);
    }

}
