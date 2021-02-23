package ca.ids.abms.modules.aerodromes;

import ca.ids.abms.config.db.ABMSRepository;
import ca.ids.abms.modules.users.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface AerodromeRepository extends ABMSRepository<Aerodrome, Integer> {


    Collection<Aerodrome> findAllByIdIn(Collection<Integer> ids);

    @Override
    void refresh(Aerodrome entity);

    Aerodrome findByAerodromeName(String aerodromeName);

    @Query (value =
        "select a " +
        "from Aerodrome as a " +
        "inner join a.billingCenter.users as u " +
        "where u = :user " +
        "order by a.aerodromeName")
    List<Aerodrome> findForUserBC (final @Param("user") User user);

    /**
     * Check if aerodrome is within an FIR
     * @param aerodromeName;
     * @param firName;
     * @return boolean;
     */
    @Query(nativeQuery = true, value =
        "SELECT ST_Intersects(" +
            "(SELECT geometry FROM aerodromes WHERE aerodrome_name = :aerodromeName), " +
            "(SELECT airspace_boundary FROM airspaces WHERE airspace_name = :firName) " +
        ")"
    )
    Boolean isAerodromeWithinFIR (
        final @Param("aerodromeName") String aerodromeName,
        final @Param("firName") String firName
    );

    @Query("SELECT DISTINCT a FROM Aerodrome a JOIN a.aerodromeServices aser ORDER BY a.aerodromeName, a.id")
    List<Aerodrome> getAllAerodromesWithServices();


    @Query("SELECT a FROM Aerodrome a JOIN a.aerodromeServiceOutage aso WHERE a.id = :id")
    Aerodrome findAerodromeServiceOutagesByAerodromeId(final @Param("id") Integer id);

}
