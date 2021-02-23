package ca.ids.abms.modules.aerodromes.cluster;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import ca.ids.abms.config.db.ABMSRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface RepositioningAerodromeClusterRepository
    extends ABMSRepository<RepositioningAerodromeCluster, Integer> {

    Page<RepositioningAerodromeCluster> findAllByOrderByRepositioningAerodromeClusterNameAsc(Pageable pageable);

    @Query("SELECT DISTINCT rac FROM RepositioningAerodromeCluster rac " +
        "INNER JOIN rac.aerodromeIdentifiers racDep INNER JOIN rac.aerodromeIdentifiers racDest " +
        "WHERE racDep.aerodromeIdentifier != racDest.aerodromeIdentifier AND racDep.aerodromeIdentifier = :depAd " +
        "AND racDest.aerodromeIdentifier = :destAd")
    Collection<RepositioningAerodromeCluster> findAllByDepAdAndDestAd(
        @Param("depAd") String depAd, @Param("destAd") String destAd);
}
