package ca.ids.abms.modules.flightmovementsbuilder.utility;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RouteUtilityTest {

    @Test
    public void testaddRouteEndPoints() {
        assertThat (RouteUtility.addRouteEndPoints(null, null, null)).isEmpty();
        assertThat (RouteUtility.addRouteEndPoints("   a   ", null, null)).isEqualTo("a");
        assertThat (RouteUtility.addRouteEndPoints("  x  y  z ", null, null)).isEqualTo("x y z");
        assertThat (RouteUtility.addRouteEndPoints("  x  y  z ", "  ", "  ")).isEqualTo("x y z");
        assertThat (RouteUtility.addRouteEndPoints("  x  y  z ", " x ", null)).isEqualTo("x y z");
        assertThat (RouteUtility.addRouteEndPoints("  x  y  z ", " x ", " z ")).isEqualTo("x y z");
        assertThat (RouteUtility.addRouteEndPoints("  x  y  z ", "a", "")).isEqualTo("a x y z");
        assertThat (RouteUtility.addRouteEndPoints("  x  y  z ", "a", " b")).isEqualTo("a x y z b");

        assertThat (RouteUtility.addRouteEndPoints("  XXX/0123 ", "AAA", "BBB")).isEqualTo("AAA XXX/0123 BBB");
        assertThat (RouteUtility.addRouteEndPoints("  XXX/0123 ", "XXX", "BBB")).isEqualTo("XXX/0123 BBB");
        assertThat (RouteUtility.addRouteEndPoints("  XXX/0123 ", "XXX/345", "BBB")).isEqualTo("XXX/0123 BBB");
        assertThat (RouteUtility.addRouteEndPoints("  XXX/0123 ", "YYY/345", "BBB")).isEqualTo("YYY/345 XXX/0123 BBB");
        assertThat (RouteUtility.addRouteEndPoints("  XXX/0123 ", "AAA", "XXX")).isEqualTo("AAA XXX/0123");
        assertThat (RouteUtility.addRouteEndPoints("  XXX/0123 ", "AAA", "XXX/345")).isEqualTo("AAA XXX/0123");
        
    }
    
}
