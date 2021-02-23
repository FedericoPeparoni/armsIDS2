package ca.ids.abms.modules.amhsconfiguration;

import ca.ids.abms.config.db.ABMSRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AmhsConfigurationRepository extends ABMSRepository<AmhsConfiguration, Integer> {
    
    public AmhsConfiguration findByActive (final Boolean active);
    
    default public AmhsConfiguration getActive() {
        return findByActive (true);
    }
    
}
