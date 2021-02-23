package ca.ids.abms.modules.unspecified;

import ca.ids.abms.config.db.ABMSRepository;

import java.util.List;

public interface UnspecifiedDepartureDestinationLocationRepository
        extends ABMSRepository<UnspecifiedDepartureDestinationLocation, Integer> {

    /**
     * Returns the first match by textIdentifier.
     *
     * For consistency, the results are ordered by {@code id} in ascending order and the top entry is returned.
     *
     * LIMITATION: Theoretically, all entries with the same text identifier should have the same lat/long values.
     * However, this method should ONLY ever be used when checking if identifier exists in unspecified locations.
     *
     * Use {@link #findAllByTextIdentifierOrderById(String)} and apply necessary logic to determine the 'right'
     * entry to use.
     */
    UnspecifiedDepartureDestinationLocation findFirstByTextIdentifierOrderById(String identifier);

    /**
     * Returns all that match by textIdentifier.
     *
     * For consistency, the results are ordered by {@code id} in ascending order.
     */
    List<UnspecifiedDepartureDestinationLocation> findAllByTextIdentifierOrderById(String identifier);

    /**
     * Returns the first match by aerodromeIdentifier.
     *
     * For consistency, the results are ordered by {@code id} in ascending order and the top entry is returned.
     *
     * LIMITATION: Theoretically, all entries with the same aerodrome identifier should have the same lat/long values.
     * However, this method should ONLY ever be used when checking if identifier exists in unspecified locations.
     *
     * Use {@link #findAllByAerodromeIdentifierAerodromeNameOrderById(String)} and apply necessary logic to determine
     * the 'right' entry to use.
     */
    UnspecifiedDepartureDestinationLocation findFirstByAerodromeIdentifierAerodromeNameOrderById(String identifier);

    /**
     * Returns all that match by aerodromeIdentifier.
     *
     * For consistency, the results are ordered by {@code id} in ascending order.
     */
    List<UnspecifiedDepartureDestinationLocation> findAllByAerodromeIdentifierAerodromeNameOrderById(String identifier);
}
