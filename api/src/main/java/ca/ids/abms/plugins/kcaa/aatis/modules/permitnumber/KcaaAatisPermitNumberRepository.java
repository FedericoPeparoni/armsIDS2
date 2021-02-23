package ca.ids.abms.plugins.kcaa.aatis.modules.permitnumber;

import ca.ids.abms.config.db.ABMSRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KcaaAatisPermitNumberRepository extends ABMSRepository<KcaaAatisPermitNumber, Integer> {
}
