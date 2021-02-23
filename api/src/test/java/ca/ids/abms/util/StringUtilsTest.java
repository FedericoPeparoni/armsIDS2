package ca.ids.abms.util;

import static org.assertj.core.api.Assertions.*;
import static ca.ids.abms.util.StringUtils.*;

import org.junit.Test;

public class StringUtilsTest {
    
    @Test
    public void testDos2unix() {
    	assertThat (dos2unix (null)).isNull();
    	assertThat (dos2unix ("")).isEmpty();
    	assertThat (dos2unix ("a")).isEqualTo("a");
    	assertThat (dos2unix ("\r\n")).isEqualTo("\n");
    	assertThat (dos2unix ("\r")).isEqualTo("\n");
    	assertThat (dos2unix ("\r\n")).isEqualTo("\n");
    	assertThat (dos2unix ("\n\r")).isEqualTo("\n\n");
    	assertThat (dos2unix ("\n\r\n\r")).isEqualTo("\n\n\n");
    	assertThat (dos2unix ("A\r\nB")).isEqualTo("A\nB");
    	assertThat (dos2unix ("A\rB")).isEqualTo("A\nB");
    	assertThat (dos2unix ("A\r\nB")).isEqualTo("A\nB");
    	assertThat (dos2unix ("A\r\nB\r\n\r\nC\n")).isEqualTo("A\nB\n\nC\n");
    	assertThat (dos2unix ("A\rB\n\rC")).isEqualTo("A\nB\n\nC");
    }
    
    @Test
    public void testUnix2Dos() {
    	assertThat (unix2dos (null)).isNull();
    	assertThat (unix2dos ("")).isEmpty();
    	assertThat (unix2dos ("a")).isEqualTo("a");
    	assertThat (unix2dos ("\r\n")).isEqualTo("\r\n");
    	assertThat (unix2dos ("\r")).isEqualTo("\r\n");
    	assertThat (unix2dos ("\r\n")).isEqualTo("\r\n");
    	assertThat (unix2dos ("\n\r")).isEqualTo("\r\n\r\n");
    	assertThat (unix2dos ("\n\r\n\r")).isEqualTo("\r\n\r\n\r\n");
    	assertThat (unix2dos ("A\r\nB")).isEqualTo("A\r\nB");
    	assertThat (unix2dos ("A\rB")).isEqualTo("A\r\nB");
    	assertThat (unix2dos ("A\r\nB")).isEqualTo("A\r\nB");
    	assertThat (unix2dos ("A\r\nB\r\n\r\nC\n")).isEqualTo("A\r\nB\r\n\r\nC\r\n");
    	assertThat (unix2dos ("A\rB\n\rC")).isEqualTo("A\r\nB\r\n\r\nC");
    }
    
    @Test
    public void testNormalizeWhiteSpace() {
        assertThat (normalizeWhiteSpace (null)).isNull();
        assertThat (normalizeWhiteSpace (" a b c ")).isEqualTo (" a b c ");
        assertThat (normalizeWhiteSpace ("    a \n b   \r\n  c \t ")).isEqualTo (" a b c ");
    }

}
