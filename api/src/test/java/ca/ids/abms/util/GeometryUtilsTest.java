package ca.ids.abms.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import org.junit.Test;

import com.vividsolutions.jts.geom.Coordinate;

public class GeometryUtilsTest {

    @Test
    public void testParseAviationCoordinate() {
        Coordinate res;

        res = GeometryUtils.parseAviationCoordinate((String)null);
        assertThat (res).isNull();

        res = GeometryUtils.parseAviationCoordinate("01N002E");
        assertThat (res.getOrdinate(Coordinate.Y)).isEqualTo (1.0);
        assertThat (res.getOrdinate(Coordinate.X)).isEqualTo (2.0);

        res = GeometryUtils.parseAviationCoordinate("0100N00200E");
        assertThat (res.getOrdinate(Coordinate.Y)).isEqualTo (1.0);
        assertThat (res.getOrdinate(Coordinate.X)).isEqualTo (2.0);

        res = GeometryUtils.parseAviationCoordinate("010000N0020000E");
        assertThat (res.getOrdinate(Coordinate.Y)).isEqualTo (1.0);
        assertThat (res.getOrdinate(Coordinate.X)).isEqualTo (2.0);

        res = GeometryUtils.parseAviationCoordinate("0100S00200W");
        assertThat (res.getOrdinate(Coordinate.Y)).isEqualTo (-1.0);
        assertThat (res.getOrdinate(Coordinate.X)).isEqualTo (-2.0);

        res = GeometryUtils.parseAviationCoordinate("010203N0040506E");
        assertThat (res.getOrdinate(Coordinate.Y)).isCloseTo (1.034, within (0.001));
        assertThat (res.getOrdinate(Coordinate.X)).isCloseTo (4.085, within (0.001));

        res = GeometryUtils.parseAviationCoordinate("  01  02   03   N   004   05   06   E  ");
        assertThat (res.getOrdinate(Coordinate.Y)).isCloseTo (1.034, within (0.001));
        assertThat (res.getOrdinate(Coordinate.X)).isCloseTo (4.085, within (0.001));

        res = GeometryUtils.parseAviationCoordinate("  010203N  0040506E  ");
        assertThat (res.getOrdinate(Coordinate.Y)).isCloseTo (1.034, within (0.001));
        assertThat (res.getOrdinate(Coordinate.X)).isCloseTo (4.085, within (0.001));
    }

    @Test
    public void testFormatAviationCoordinate() {
        assertThat (GeometryUtils.formatAviationCoordinate(1.0, 2.0)).isEqualTo("010000N0020000E");
        assertThat (GeometryUtils.formatAviationCoordinate(-1.0, -2.0)).isEqualTo("010000S0020000W");
        assertThat (GeometryUtils.formatAviationCoordinate(1.034, 4.085)).isEqualTo("010202N0040506E");
    }

    @Test
    public void testParseFormatAviationCoordinate() {
        final String coordStr = "010203N0040506E";
        Coordinate coord = GeometryUtils.parseAviationCoordinate (coordStr);
        final double lat = coord.getOrdinate(Coordinate.Y);
        final double lng = coord.getOrdinate(Coordinate.X);
        final String coordStr2 = GeometryUtils.formatAviationCoordinate (lat, lng);
        assertThat (coordStr2).isEqualTo (coordStr);
    }
}
