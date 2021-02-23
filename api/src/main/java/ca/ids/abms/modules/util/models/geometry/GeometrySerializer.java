package ca.ids.abms.modules.util.models.geometry;

import static ca.ids.abms.modules.util.models.geometry.GeoJson.COORDINATES;
import static ca.ids.abms.modules.util.models.geometry.GeoJson.GEOMETRIES;
import static ca.ids.abms.modules.util.models.geometry.GeoJson.GEOMETRY_COLLECTION;
import static ca.ids.abms.modules.util.models.geometry.GeoJson.LINE_STRING;
import static ca.ids.abms.modules.util.models.geometry.GeoJson.MULTI_LINE_STRING;
import static ca.ids.abms.modules.util.models.geometry.GeoJson.MULTI_POINT;
import static ca.ids.abms.modules.util.models.geometry.GeoJson.MULTI_POLYGON;
import static ca.ids.abms.modules.util.models.geometry.GeoJson.POINT;
import static ca.ids.abms.modules.util.models.geometry.GeoJson.POLYGON;
import static ca.ids.abms.modules.util.models.geometry.GeoJson.TYPE;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

public class GeometrySerializer extends JsonSerializer<Geometry> {

    @Override
    public Class<Geometry> handledType() {
        return Geometry.class;
    }

    @Override
    public void serialize(Geometry value, JsonGenerator jgen, SerializerProvider provider) throws IOException {

        writeGeometry(jgen, value);
    }

    public void writeGeometry(JsonGenerator jgen, Geometry value) throws IOException {
        if (value instanceof Polygon) {
            writePolygon(jgen, (Polygon) value);

        } else if (value instanceof Point) {
            writePoint(jgen, (Point) value);

        } else if (value instanceof MultiPoint) {
            writeMultiPoint(jgen, (MultiPoint) value);

        } else if (value instanceof MultiPolygon) {
            writeMultiPolygon(jgen, (MultiPolygon) value);

        } else if (value instanceof LineString) {
            writeLineString(jgen, (LineString) value);

        } else if (value instanceof MultiLineString) {
            writeMultiLineString(jgen, (MultiLineString) value);

        } else if (value instanceof GeometryCollection) {
            writeGeometryCollection(jgen, (GeometryCollection) value);

        }
    }

    private void writeGeometryCollection(JsonGenerator jgen, GeometryCollection value) throws IOException {
        jgen.writeStartObject();
        jgen.writeStringField(TYPE, GEOMETRY_COLLECTION);
        jgen.writeArrayFieldStart(GEOMETRIES);

        for (int i = 0; i != value.getNumGeometries(); ++i) {
            writeGeometry(jgen, value.getGeometryN(i));
        }

        jgen.writeEndArray();
        jgen.writeEndObject();
    }

    private void writeLineString(JsonGenerator jgen, LineString lineString) throws IOException {
        jgen.writeStartObject();
        jgen.writeStringField(TYPE, LINE_STRING);
        jgen.writeFieldName(COORDINATES);
        writeLineStringCoords(jgen, lineString);
        jgen.writeEndObject();
    }

    private void writeLineStringCoords(JsonGenerator jgen, LineString ring) throws IOException {
        jgen.writeStartArray();
        for (int i = 0; i != ring.getNumPoints(); ++i) {
            Point p = ring.getPointN(i);
            writePointCoords(jgen, p);
        }
        jgen.writeEndArray();
    }

    private void writeMultiLineString(JsonGenerator jgen, MultiLineString value) throws IOException {
        jgen.writeStartObject();
        jgen.writeStringField(TYPE, MULTI_LINE_STRING);
        jgen.writeArrayFieldStart(COORDINATES);

        for (int i = 0; i != value.getNumGeometries(); ++i) {
            writeLineStringCoords(jgen, (LineString) value.getGeometryN(i));
        }

        jgen.writeEndArray();
        jgen.writeEndObject();
    }

    private void writeMultiPoint(JsonGenerator jgen, MultiPoint value) throws IOException {
        jgen.writeStartObject();
        jgen.writeStringField(TYPE, MULTI_POINT);
        jgen.writeArrayFieldStart(COORDINATES);

        for (int i = 0; i != value.getNumGeometries(); ++i) {
            writePointCoords(jgen, (Point) value.getGeometryN(i));
        }

        jgen.writeEndArray();
        jgen.writeEndObject();
    }

    private void writeMultiPolygon(JsonGenerator jgen, MultiPolygon value) throws IOException {
        jgen.writeStartObject();
        jgen.writeStringField(TYPE, MULTI_POLYGON);
        jgen.writeArrayFieldStart(COORDINATES);

        for (int i = 0; i != value.getNumGeometries(); ++i) {
            writePolygonCoordinates(jgen, (Polygon) value.getGeometryN(i));
        }

        jgen.writeEndArray();
        jgen.writeEndObject();
    }

    private void writePoint(JsonGenerator jgen, Point p) throws IOException {
        jgen.writeStartObject();
        jgen.writeStringField(TYPE, POINT);
        jgen.writeFieldName(COORDINATES);
        writePointCoords(jgen, p);
        jgen.writeEndObject();
    }

    private void writePointCoords(JsonGenerator jgen, Point p) throws IOException {
        jgen.writeStartArray();
        jgen.writeNumber(p.getX());
        jgen.writeNumber(p.getY());
        jgen.writeEndArray();
    }

    private void writePolygon(JsonGenerator jgen, Polygon value) throws IOException {
        jgen.writeStartObject();
        jgen.writeStringField(TYPE, POLYGON);
        jgen.writeFieldName(COORDINATES);
        writePolygonCoordinates(jgen, value);

        jgen.writeEndObject();
    }

    private void writePolygonCoordinates(JsonGenerator jgen, Polygon value) throws IOException {
        jgen.writeStartArray();
        writeLineStringCoords(jgen, value.getExteriorRing());

        for (int i = 0; i < value.getNumInteriorRing(); ++i) {
            writeLineStringCoords(jgen, value.getInteriorRingN(i));
        }
        jgen.writeEndArray();
    }
}
