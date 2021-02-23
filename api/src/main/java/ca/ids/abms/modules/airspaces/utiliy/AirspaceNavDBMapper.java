package ca.ids.abms.modules.airspaces.utiliy;

import ca.ids.abms.modules.airspaces.Airspace;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKBReader;
import org.apache.log4j.Logger;
import org.postgresql.util.PGobject;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AirspaceNavDBMapper implements RowMapper<Airspace> {

    private static final Logger LOG = Logger.getLogger(AirspaceNavDBMapper.class);
    @Override
    public Airspace mapRow(ResultSet rs, int rowNum) throws SQLException {
        Airspace model = new Airspace();
        model.setId(rs.getInt("airspace_pk"));
        model.setAirspaceName(rs.getString("ident"));
        model.setAirspaceFullName(rs.getString("nam"));
        model.setAirspaceType(rs.getString("typ"));
        model.setAirspaceCeiling(rs.getDouble("absupperlimit"));
        PGobject obj = (PGobject)rs.getObject("geom");
        WKBReader wkbReader = new WKBReader();
        byte[] bytes = WKBReader.hexToBytes(obj.getValue());
        Geometry geom = null;
        if (bytes != null) {
            try {
                geom = wkbReader.read(bytes);
            } catch (ParseException e) {
                LOG.error("Error ParseException: ",e);

            }
        }
        model.setAirspaceBoundary(geom);
        return model;
    }

}
