package ca.ids.abms.modules.unifiedtaxes;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ca.ids.abms.config.db.ABMSRepository;

@Repository
public interface UnifiedTaxValidityRepository extends ABMSRepository<UnifiedTaxValidity, Integer> {
    @Query(nativeQuery = true, value = "select u.* from abms.unified_tax_validity u "
            + "where (u.from_validity_year <= :yearValidity  or u.from_validity_year IS NULL) and (u.to_validity_year >= :yearValidity or u.to_validity_year IS NULL)")
    UnifiedTaxValidity findByValidityYear(@Param("yearValidity") Timestamp yearValidity);

    @Query(nativeQuery = true, value = "SELECT COUNT(*) FROM abms.unified_tax_validity u "
            + "where ((:toValidityYear >= u.from_validity_year AND u.to_validity_year >= :fromValidityYear)"
            + " OR (u.from_validity_year IS NULL AND u.to_validity_year >= :fromValidityYear)"
            + " OR (u.to_validity_year IS NULL AND :toValidityYear >= u.from_validity_year))")
    Integer countValiditiesOverlappingFromAndToDates(@Param("fromValidityYear")LocalDateTime fromValidityYear,@Param("toValidityYear") LocalDateTime toValidityYear);
    
    @Query(nativeQuery = true, value = "SELECT COUNT(*) FROM abms.unified_tax_validity u "
            + "where (u.from_validity_year IS NULL OR "
            + ":toValidityYear >= u.from_validity_year"
            + ")")
    Integer countValiditiesOverlappingToDate(@Param("toValidityYear") LocalDateTime toValidityYear);

    @Query(nativeQuery = true, value = "SELECT COUNT(*) FROM abms.unified_tax_validity u "
            + "where (u.to_validity_year IS NULL OR "
            + "u.to_validity_year >= :fromValidityYear"
            + ")")
    Integer countValiditiesOverlappingFromDate(@Param("fromValidityYear") LocalDateTime fromValidityYear);
    
    @Query(nativeQuery = true, value = "SELECT COUNT(*) FROM abms.unified_tax_validity u "
            + "where u.id != :utvId and ((:toValidityYear >= u.from_validity_year AND u.to_validity_year >= :fromValidityYear)"
            + " OR (u.from_validity_year IS NULL AND u.to_validity_year >= :fromValidityYear)"
            + " OR (u.to_validity_year IS NULL AND :toValidityYear >= u.from_validity_year))")
    Integer countValiditiesOverlappingFromAndToDatesExcludingCurrentId(@Param("fromValidityYear")LocalDateTime fromValidityYear,
            @Param("toValidityYear") LocalDateTime toValidityYear,@Param("utvId") Integer utvId);
    
    @Query(nativeQuery = true, value = "SELECT COUNT(*) FROM abms.unified_tax_validity u "
            + "where u.id != :utvId and (u.from_validity_year IS NULL OR "
            + ":toValidityYear >= u.from_validity_year"
            + ")")
    Integer countValiditiesOverlappingToDateExcludingCurrentId(@Param("toValidityYear") LocalDateTime toValidityYear, @Param("utvId") Integer utvId);

    @Query(nativeQuery = true, value = "SELECT COUNT(*) FROM abms.unified_tax_validity u "
            + "where u.id != :utvId and (u.to_validity_year IS NULL OR "
            + "u.to_validity_year >= :fromValidityYear"
            + ")")
    Integer countValiditiesOverlappingFromDateExcludingCurrentId(@Param("fromValidityYear") LocalDateTime fromValidityYear, @Param("utvId") Integer utvId);


}
