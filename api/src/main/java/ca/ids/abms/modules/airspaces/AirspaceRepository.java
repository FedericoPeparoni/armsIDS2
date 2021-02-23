package ca.ids.abms.modules.airspaces;

import ca.ids.abms.config.db.ABMSRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AirspaceRepository extends ABMSRepository<Airspace, Integer> {

    /**
     * @deprecated - use {@link #getLocalAirspaceUnionGeom}
     * @return
     */
    @Deprecated
    @Query(nativeQuery = true, value = "select ST_AsText (local_airspace_union_geom())")
    List <String> getDistinctFirPolygonsWKT();
    
    /**
     * Get all FIRs as a single polygon WKT
     */
    @Query(nativeQuery = true, value = "select ST_AsText (local_airspace_union_geom())")
    String getLocalAirspaceUnionGeom();
    
    /**
     * Get all non-FIR airspaces
     */
    @Query ("from Airspace where airspaceType not like 'FIR%'")
    List <Airspace> getNonFirAirspaces();

    
    /**
     * Get difference between included FIRs and excluded TMAs
     */   
    @Query(nativeQuery = true, value = "SELECT St_AsText(COALESCE(ST_Difference(included_fir_union_geom_by_flight_level(:flightLevel),"
            + " (SELECT ST_Collect(b.airspace_boundary)"
            + " FROM airspaces b"
            + " WHERE ST_Intersects(a.airspace_boundary, b.airspace_boundary)"
            + " AND a.id != b.id and b.airspace_type like 'TMA%' and airspace_ceiling > :flightLevel )), a.airspace_boundary) )"
            + " FROM airspaces a limit 1")
    List<String> getDifference(final @Param("flightLevel") Double flightLevel);
    
    // The following methods are not currently used in the code. They will be used in future while expanding 3d routing.
    /**
     * Get all FIRs included
     */
    @Query ("from Airspace where airspaceType like 'FIR%' and included = true")
    List <Airspace> getIncludedFirs();
    
    /**
     * Get all TMAs excluded
     */
    @Query ("from Airspace where airspaceType like 'TMA%' and included = false")
    List <Airspace> getExcludedTMAs();
    
    /**
     * Get all included FIRs as a single polygon WKT
     */
    @Query(nativeQuery = true, value = "select ST_AsText (included_fir_union_geom_by_flight_level(:flightLevel))")
    List<String> getFirIncludedUnionGeom(final @Param("flightLevel") Double flightLevel);
    
    /**
     * Get all excluded TMAs as a single polygon WKT
     */
    @Query(nativeQuery = true, value = "select ST_AsText (excluded_tma_union_geom_by_flight_level(:flightLevel))")
    List<String> getTmaExcludedUnionGeom(final @Param("flightLevel") Double flightLevel);
        
}
