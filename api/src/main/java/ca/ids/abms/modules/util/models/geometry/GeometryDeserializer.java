package ca.ids.abms.modules.util.models.geometry;

import static ca.ids.abms.modules.util.models.geometry.GeoJson.COORDINATES;
import static ca.ids.abms.modules.util.models.geometry.GeoJson.CRS;
import static ca.ids.abms.modules.util.models.geometry.GeoJson.GEOMETRIES;
import static ca.ids.abms.modules.util.models.geometry.GeoJson.GEOMETRY_COLLECTION;
import static ca.ids.abms.modules.util.models.geometry.GeoJson.LINE_STRING;
import static ca.ids.abms.modules.util.models.geometry.GeoJson.MULTI_LINE_STRING;
import static ca.ids.abms.modules.util.models.geometry.GeoJson.MULTI_POINT;
import static ca.ids.abms.modules.util.models.geometry.GeoJson.MULTI_POLYGON;
import static ca.ids.abms.modules.util.models.geometry.GeoJson.NAME;
import static ca.ids.abms.modules.util.models.geometry.GeoJson.POINT;
import static ca.ids.abms.modules.util.models.geometry.GeoJson.POLYGON;
import static ca.ids.abms.modules.util.models.geometry.GeoJson.PROPERTIES;
import static ca.ids.abms.modules.util.models.geometry.GeoJson.TYPE;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.ids.abms.modules.translation.Translation;

public class GeometryDeserializer extends JsonDeserializer<Geometry> {

    private final Logger log = LoggerFactory.getLogger(GeometryDeserializer.class);

    private GeometryFactory gf = new GeometryFactory();

    private static final int SRID = 4326;

    @Override
    public Geometry deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        ObjectCodec oc = jp.getCodec();
        JsonNode root = oc.readTree(jp);
        return parseGeometry(root);
    }

    private int handleSrid(JsonNode root) {
        int srid = SRID;
        JsonNode crs_node = root.get(CRS);
        if (crs_node != null) {
            JsonNode properties_node = crs_node.get(PROPERTIES);
            if (properties_node != null) {
                String name = properties_node.get(NAME).asText();
                String[] split = name.split(":");
                if (split.length > 1) {
                    String value = split[1];
                    log.debug("Adding geometry with srid " + value);
                    srid = Integer.parseInt(value);
                }
            }
        }
        return srid;
    }

    private Coordinate parseCoordinate(JsonNode array) {
        assert array.isArray() && array.size() == 2 : Translation.getLangByToken("expecting coordinate array with single point [ x, y ]");
        return new Coordinate(array.get(0).asDouble(), array.get(1).asDouble());
    }

    private Coordinate[] parseCoordinates(JsonNode array) {
        Coordinate[] points = new Coordinate[array.size()];
        for (int i = 0; i != array.size(); ++i) {
            points[i] = parseCoordinate(array.get(i));
        }
        return points;
    }

    private Geometry[] parseGeometries(JsonNode arrayOfGeoms) throws JsonMappingException {
        Geometry[] items = new Geometry[arrayOfGeoms.size()];
        for (int i = 0; i != arrayOfGeoms.size(); ++i) {
            items[i] = parseGeometry(arrayOfGeoms.get(i));
        }
        return items;
    }

    private Geometry parseGeometry(JsonNode root) throws JsonMappingException {
        String typeName = root.get(TYPE).asText();
        Geometry geom = null;
        int srid_value = handleSrid(root);
        if (POINT.equals(typeName)) {
            geom = parsePoint(root);

        } else if (MULTI_POINT.equals(typeName)) {
            geom = parseMultiPoint(root);

        } else if (LINE_STRING.equals(typeName)) {
            geom = parseLineString(root);

        } else if (MULTI_LINE_STRING.equals(typeName)) {
            geom = parseMultiLineStrings(root);

        } else if (POLYGON.equals(typeName)) {
            geom = parsePolygon(root);

        } else if (MULTI_POLYGON.equals(typeName)) {
            geom = parseMultiPolygon(root);

        } else if (GEOMETRY_COLLECTION.equals(typeName)) {
            geom = parseGeometryCollection(root);

        }
        if (geom != null) {
            geom.setSRID(srid_value);
        }
        return geom;
    }

    private GeometryCollection parseGeometryCollection(JsonNode root) throws JsonMappingException {
        return gf.createGeometryCollection(parseGeometries(root.get(GEOMETRIES)));
    }

    private LinearRing[] parseInteriorRings(JsonNode arrayOfRings) {
        LinearRing[] rings = new LinearRing[arrayOfRings.size() - 1];
        for (int i = 1; i < arrayOfRings.size(); ++i) {
            rings[i - 1] = parseLinearRing(arrayOfRings.get(i));
        }
        return rings;
    }

    private LinearRing parseLinearRing(JsonNode coordinates) {
        assert coordinates.isArray() : Translation.getLangByToken("expected coordinates array");

        return gf.createLinearRing(parseCoordinates(coordinates));
    }

    private LineString parseLineString(JsonNode root) {
        return gf.createLineString(parseCoordinates(root.get(COORDINATES)));
    }

    private LineString[] parseLineStrings(JsonNode array) {
        LineString[] strings = new LineString[array.size()];
        for (int i = 0; i != array.size(); ++i) {
            strings[i] = gf.createLineString(parseCoordinates(array.get(i)));
        }
        return strings;
    }

    private MultiLineString parseMultiLineStrings(JsonNode root) {
        return gf.createMultiLineString(parseLineStrings(root.get(COORDINATES)));
    }

    private MultiPoint parseMultiPoint(JsonNode root) {
        return gf.createMultiPoint(parseCoordinates(root.get(COORDINATES)));
    }

    private MultiPolygon parseMultiPolygon(JsonNode root) {
        JsonNode arrayOfPolygons = root.get(COORDINATES);
        return gf.createMultiPolygon(parsePolygons(arrayOfPolygons));
    }

    private Point parsePoint(JsonNode root) {
        return gf.createPoint(parseCoordinate(root.get(COORDINATES)));
    }

    private Polygon parsePolygon(JsonNode root) {
        JsonNode arrayOfRings = root.get(COORDINATES);
        return parsePolygonCoordinates(arrayOfRings);
    }

    private Polygon parsePolygonCoordinates(JsonNode arrayOfRings) {
        return gf.createPolygon(parseLinearRing(arrayOfRings.get(0)), parseInteriorRings(arrayOfRings));
    }

    private Polygon[] parsePolygons(JsonNode arrayOfPolygons) {
        Polygon[] polygons = new Polygon[arrayOfPolygons.size()];
        for (int i = 0; i != arrayOfPolygons.size(); ++i) {
            polygons[i] = parsePolygonCoordinates(arrayOfPolygons.get(i));
        }
        return polygons;
    }
}
