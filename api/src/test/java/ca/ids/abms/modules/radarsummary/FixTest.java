package ca.ids.abms.modules.radarsummary;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;

public class FixTest {
    
    @Test
    public void testBasic() {
        // shouldn't throw
        final Fix f1 = new Fix ("0120", "XYZ", "123");
        final Fix f2 = new Fix (null, "XYZ", null);
        
        // should throw
        assertThatThrownBy(()->new Fix (null, null, null)).isInstanceOf (NullPointerException.class);
        
        // toString()
        assertThat (f1.toString()).isEqualTo("0120-XYZ-123");
        assertThat (f2.toString()).isEqualTo("[null]-XYZ-[null]");
        
        // valueOf
        assertThatThrownBy (()->Fix.valueOf ("abc")).hasMessageContaining("invalid fix");
        {
            final Fix f = Fix.valueOf("0011-ABC-223");
            assertThat (f.time).isEqualTo("0011");
            assertThat (f.point).isEqualTo("ABC");
            assertThat (f.level).isEqualTo("223");
        }
        {
            final Fix f = Fix.valueOf("[null]-ABC-[NULL]");
            assertThat (f.time).isNull();
            assertThat (f.point).isEqualTo("ABC");
            assertThat (f.level).isNull();
        }
        
        // formatList
        assertThat (Fix.formatList (null)).isNull();
        assertThat (Fix.formatList (new ArrayList <Fix>())).isNull();
        assertThat (
                Fix.formatList (Arrays.asList (
                    new Fix ("0001", "AAA", "900"),
                    new Fix ("0002", "AAB", "901"),
                    new Fix ("0003", "AAC", "902")
                ))
        ).isEqualTo("0001-AAA-900, 0002-AAB-901, 0003-AAC-902");
        
        // parseList
        assertThat (Fix.parseList (null)).isNull();
        assertThat (Fix.parseList (" ")).isNull();
        assertThat (Fix.parseList ("0001-AAA-900, 0002-AAB-901, 0003-AAC-902")).isEqualTo (Arrays.asList (
                new Fix ("0001", "AAA", "900"),
                new Fix ("0002", "AAB", "901"),
                new Fix ("0003", "AAC", "902")
        ));
        
        // formatFlightPlanRoute
        assertThat (Fix.formatFlightPlanRoute (null)).isNull();
        assertThat (Fix.formatFlightPlanRoute (new ArrayList <Fix>())).isNull();
        assertThat (
                Fix.formatFlightPlanRoute (Arrays.asList (
                    new Fix ("0001", "AAA", "900"),
                    new Fix ("0002", "AAB", "901"),
                    new Fix ("0003", "AAC", "902")
                ))
        ).isEqualTo("AAA AAB AAC");
        
        
    }

}
