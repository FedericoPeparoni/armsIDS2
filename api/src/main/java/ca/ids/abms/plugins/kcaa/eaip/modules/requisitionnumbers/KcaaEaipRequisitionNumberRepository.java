package ca.ids.abms.plugins.kcaa.eaip.modules.requisitionnumbers;

import ca.ids.abms.config.db.ABMSRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KcaaEaipRequisitionNumberRepository extends ABMSRepository<KcaaEaipRequisitionNumber, Integer> {
}
