package ca.ids.abms.atnmsg.amhs.addr;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;

public class UbimexAddrParserTest {
    
    @Test
    public void testBasic() {
        assertThatThrownBy (()->parse (null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy (()->parse ("")).hasMessageMatching("(?i)invalid empty.*");
        assertThatThrownBy (()->parse ("  ")).hasMessageMatching("(?i)invalid empty.*");
        assertThatThrownBy (()->parse (" foo ")).hasMessageMatching("(?i).*invalid.*expecting.*");
        assertThatThrownBy (()->parse (" cn=xyz ")).hasMessageMatching("(?i).*invalid.*expecting.*");
        assertThat (parse ("cn=aaaabbbb").toAftnString()).isEqualTo("AAAABBBB");
        assertThat (parse ("C=XX,A=ICAO,P=SURINAME,O=SMPM,OU1=SMJP,CN=SMJPP3TS").toAftnString()).isEqualTo("SMJPP3TS");
        assertThat (parse ("C=XX,A=ICAO,P=SURINAME,O=SMPM,OU1=SMJP,CN=AAAABBBB").toAftnString()).isEqualTo("AAAABBBB");
    }
    
    private AmhsAddress parse (final String s) {
        return new UbimexAddrParser().parse (s);
    }

}
