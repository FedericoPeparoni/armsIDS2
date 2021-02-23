package ca.ids.abms.modules.aerodromes.cluster;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepositioningAssignedAerodromeClusterRepository
    extends JpaRepository<RepositioningAssignedAerodromeCluster, Integer> {
}
