package ca.ids.abms.modules.route;

import ca.ids.abms.config.db.ABMSRepository;
import org.springframework.stereotype.Repository;

@Repository
interface NominalRouteRepository extends ABMSRepository<NominalRoute, Integer> {}
