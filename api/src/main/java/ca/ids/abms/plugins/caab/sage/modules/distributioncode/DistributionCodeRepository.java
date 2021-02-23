package ca.ids.abms.plugins.caab.sage.modules.distributioncode;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DistributionCodeRepository extends JpaRepository<DistributionCode, Integer> {

    @Query("SELECT dc FROM DistributionCode dc " +
        "WHERE dc.code LIKE :chargeCode || '-%' " +
          "AND dc.code LIKE '%-' || :operationCode || '-%' " +
          "AND dc.code LIKE '%-00'")
    DistributionCode findByChargeAndOperationCode(@Param("chargeCode") final String chargeCode,
                                                  @Param("operationCode") final String operationCode);
}
