package ca.ids.abms.modules.amhsconfiguration;

import ca.ids.abms.config.db.ABMSRepository;

import java.util.List;

import org.springframework.stereotype.Repository;

@Repository
public interface AmhsAccountRepository extends ABMSRepository<AmhsAccount, Integer> {
    
    public List <AmhsAccount> findByActive (final Boolean active);
}
