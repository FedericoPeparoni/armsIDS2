package ca.ids.abms.modules.radarsummary.parsers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.*;

import org.junit.Test;

import ca.ids.abms.modules.radarsummary.FlightTravelCategory;
import ca.ids.abms.modules.radarsummary.RadarSummaryCsvViewModel;
import ca.ids.abms.util.csv.CsvFormatException;

public class LeonardoCsvParserTest {

    @Test
    public void testValidateEntryExitPoints() {
        final String[] fields = new String[100];
        
        fields[0] = "TEST";
        fields[4] = "DEPA";
        fields[8] = "DEST";
        fields[24] = "I";
        
        fields[10] = "ENTRY";
        fields[14] = "EXIT";
        fields[11] = "204500N0391500W";
        fields[15] = "202157N0422102W";
        fields[3] = "2020-01-22 12:35";       
        
        LeonardoCsvParser parser = new LeonardoCsvParser();       
        RadarSummaryCsvViewModel model = null;
        
        // Test 1 Exclude DYINB, DYOUT, RYWnn, LLnnn, and nnNnn from entry/exit points
        // Coordinate is used instead
        model = parser.parseFields(fields);
        assertThat(model).isNotNull();
        assertThat(model.getFirEntryPoint()).isEqualTo("ENTRY");
        assertThat(model.getFirExitPoint()).isEqualTo("EXIT");
        
        fields[10] = "DYINB";
        fields[14] = "DYOUT";
        model = parser.parseFields(fields);
        assertThat(model).isNotNull();
        assertThat(model.getFirEntryPoint()).isEqualTo("204500N0391500W");
        assertThat(model.getFirExitPoint()).isEqualTo("202157N0422102W");

        fields[10] = "RYW12";
        fields[14] = "LL001";
        model = parser.parseFields(fields);
        assertThat(model).isNotNull();
        assertThat(model.getFirEntryPoint()).isEqualTo("204500N0391500W");
        assertThat(model.getFirExitPoint()).isEqualTo("202157N0422102W");

        fields[10] = "RYW12";
        fields[14] = "12N01";
        model = parser.parseFields(fields);
        assertThat(model).isNotNull();
        assertThat(model.getFirEntryPoint()).isEqualTo("204500N0391500W");
        assertThat(model.getFirExitPoint()).isEqualTo("202157N0422102W");

        // Test 2 Flight level
        fields[19] = "32000";
        model = parser.parseFields(fields);
        assertThat(model).isNotNull();
        assertThat(model.getFlightLevel()).isEqualTo("F320");
        
        fields[19] = "3200";
        model = parser.parseFields(fields);
        assertThat(model).isNotNull();
        assertThat(model.getFlightLevel()).isEqualTo("F032");

        fields[13] = "32000";
        model = parser.parseFields(fields);
        assertThat(model).isNotNull();
        assertThat(model.getFirEntryFlightLevel()).isEqualTo("F320");
        
        fields[17] = "32000";
        model = parser.parseFields(fields);
        assertThat(model).isNotNull();
        assertThat(model.getFirExitFlightLevel().equals("F320"));
        
        // Test 3 Flight category
        fields[27] = "0";
        model = parser.parseFields(fields);
        assertThat(model).isNotNull();
        assertThat(model.getFlightTravelCategory()).isEqualTo(FlightTravelCategory.ARRIVAL.getValue());
    }

}
