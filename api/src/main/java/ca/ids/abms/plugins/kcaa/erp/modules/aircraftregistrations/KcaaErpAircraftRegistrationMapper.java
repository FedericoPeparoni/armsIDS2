package ca.ids.abms.plugins.kcaa.erp.modules.aircraftregistrations;

import org.springframework.stereotype.Component;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

@Component
public class KcaaErpAircraftRegistrationMapper implements RowMapper<KcaaErpAircraftRegistration> {

    private static final String DEFAULT_OWNER_NAME = "GENERIC";

    private static final String DEFAULT_ANALYSIS_TYPE = "GENERIC";

    @Override
    public KcaaErpAircraftRegistration mapRow(ResultSet rs, int rowNum) throws SQLException {
        KcaaErpAircraftRegistration kEAR = new KcaaErpAircraftRegistration();

        kEAR.setRegistrationNumber(rs.getString(KcaaErpAircraftRegistration.REG_MARK_COLUMN_NAME));
        kEAR.setCoaDateOfRenewal(new Timestamp(rs.getDate(KcaaErpAircraftRegistration.COA_START_DATE_COLUMN_NAME).getTime()).toLocalDateTime());
        kEAR.setCoaDateOfExpiry(new Timestamp(rs.getDate(KcaaErpAircraftRegistration.COA_END_DATE_COLUMN_NAME).getTime()).toLocalDateTime());
        kEAR.setMtowWeight(rs.getDouble(KcaaErpAircraftRegistration.MTOW_COLUMN_NAME));
        kEAR.setOwnerName(DEFAULT_OWNER_NAME);
        kEAR.setAnalysisType(DEFAULT_ANALYSIS_TYPE);

        return kEAR;
    }
}

