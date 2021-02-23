package ca.ids.abms.modules.util.models.geometry;

import ca.ids.abms.util.StringUtils;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.util.GeometricShapeFactory;
import org.apache.log4j.Logger;
import org.geotools.geojson.geom.GeometryJSON;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;


public class GeometryUtil {

    private static Logger LOG = Logger.getLogger(GeometryUtil.class);

    private static final int SRID = 4326;

    public enum GeometryType {
        POINT, LINE, POLYGON, CIRCLE;
    }

    public enum GeometryFieldName {
        GEOM("geom"),
        SHAPE("shape");

        private String value;

        private GeometryFieldName(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public static Geometry fromJsonGeometry(String jsonGeom) {
        GeometryJSON gjson = new GeometryJSON();
        // be sure to strip whitespace
        Geometry toReturn = null;
        StringReader reader = new StringReader(jsonGeom);

        try {
            toReturn = gjson.read(reader);
            toReturn.setSRID(SRID);
        } catch (IOException e) {
            LOG.error("Error ", e);
        }
        return toReturn;
    }


    public static Geometry convertStringToGeometry(String value) {
        Geometry jtsGeometry = null;
        WKTReader reader = new WKTReader();
        try {
            if(StringUtils.isStringIfNotNull(value)) {
                jtsGeometry = reader.read(value);
                jtsGeometry.setSRID(SRID);
            }

        } catch (Exception ex) {
            LOG.error("ERROR on WKT parsing. WKT value : "+value, ex);
        }
        return jtsGeometry;
    }


    public static String toJsonGeometry(Geometry geom) {
        if (geom != null) {
            GeometryJSON gjson = new GeometryJSON();
            // be sure to strip whitespace
            StringWriter writer = new StringWriter();

            try {
                gjson.write(geom, writer);
            } catch (IOException e) {
                LOG.error("Error ", e);
            }
            return writer.toString();
        } else {
            return null;
        }
    }

    public static Geometry unionGeometries(List<Geometry> geometries, GeometryFactory factory) {
        Geometry geom = factory.buildGeometry(geometries);
        if (geom instanceof GeometryCollection) {
            geom = null;
            for (Geometry g : geometries) {
                if (geom == null) {
                    geom = g;
                } else {
                    geom = geom.union(g);
                }
            }
        }
        return geom;
    }

    public static MultiPolygon CreateMultipolygon(Polygon polygon) {
        Polygon[] polygons = new Polygon[1];
        polygons[0] = polygon;
        MultiPolygon mp = new MultiPolygon(polygons, polygon.getFactory());
        mp.setSRID(polygon.getSRID());
        return mp;
    }

    public static Polygon createCircle(double radius, Point center, int numPoints) {
        GeometricShapeFactory gsf = new GeometricShapeFactory();
//		gsf.setSize(radius * 2);
        gsf.setHeight(radius * 2);
        gsf.setWidth(radius * 2);
        gsf.setNumPoints(numPoints);
        gsf.setCentre(center.getCoordinate());
        return gsf.createCircle();
    }

}
