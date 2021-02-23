/**
 *
 */
package ca.ids.abms.modules.mtow;

import ca.ids.abms.config.db.ABMSRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

public interface AverageMtowFactorRepository extends ABMSRepository<AverageMtowFactor, Integer> {

    Page<AverageMtowFactor> findAllByOrderByUpperLimitAsc(Pageable pageable);

    @Query(
            value = "select * from average_mtow_factors where upper_limit >= ?1 order by upper_limit asc limit 1",
            nativeQuery = true
    )
    AverageMtowFactor findAverageMtowFactorByUpperLimit(Double upperLimit);

    @Query(
        value = "select * from average_mtow_factors where upper_limit >= ?1 and factor_class = ?2 order by upper_limit asc limit 1",
        nativeQuery = true
    )
    AverageMtowFactor findAverageMtowFactorByUpperLimitAndFactorClass(Double upperLimit, String factorClass);
}
