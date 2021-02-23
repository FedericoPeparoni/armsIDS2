package ca.ids.abms.modules.manifests;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface PassengerManifestRepository extends JpaRepository<PassengerManifest, Integer>,
    JpaSpecificationExecutor {

    @Query(value = OrphanPassengerManifestFilter.QUERY, nativeQuery = true)
    List<Integer> getOrphanPassengerManifests ();
}
