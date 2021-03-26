package ca.ids.abms.modules.unifiedtaxes;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ca.ids.abms.config.db.ABMSRepository;

@Repository
public interface UnifiedTaxRepository extends ABMSRepository<UnifiedTax, Integer> {

    @Query(nativeQuery = true, value = "SELECT u.* FROM abms.unified_tax u"
            + " WHERE (u.from_manufacture_year <= :yearManufacture or u.from_manufacture_year IS NULL) and (u.to_manufacture_year >= :yearManufacture  or u.to_manufacture_year IS NULL) and "
            + "u.validity_id = :validityId")
    UnifiedTax findByValidityAndManifactureYear(@Param("validityId") Integer validityId,
            @Param("yearManufacture") Timestamp yearManufacture);

    @Query(nativeQuery = true, value = "SELECT u.* FROM abms.unified_tax u WHERE u.validity_id = :validityId Order by u.from_manufacture_year Asc")
    List<UnifiedTax> findAllByValidityId(@Param("validityId") Integer validityId);

    @Query(nativeQuery = true, value = "SELECT COUNT(*) FROM abms.unified_tax u "
            + "where u.validity_id = :validityId and "
            + "((:toManufactureYear >= u.from_manufacture_year AND u.to_manufacture_year >= :fromManufactureYear)"
            + " OR (u.from_manufacture_year IS NULL AND u.to_manufacture_year >= :fromManufactureYear)"
            + " OR (u.to_manufacture_year IS NULL AND :toManufactureYear >= u.from_manufacture_year))")
    Integer countManifactureOverlappingFromAndToDatesOnTheSameValidityPeriod(@Param("fromManufactureYear") LocalDateTime fromManufactureYear,
            @Param("toManufactureYear") LocalDateTime toManufactureYear,@Param("validityId") Integer validityId);

    @Query(nativeQuery = true, value = "SELECT COUNT(*) FROM abms.unified_tax u "
            + "where u.validity_id = :validityId and (u.from_manufacture_year IS NULL OR "
            + ":toManufactureYear >= u.from_manufacture_year"
            + ")")
    Integer countManifactureOverlappingToDateOnTheSameValidityPeriod(@Param("toManufactureYear") LocalDateTime toManufactureYear, @Param("validityId") Integer validityId);

    @Query(nativeQuery = true, value = "SELECT COUNT(*) FROM abms.unified_tax u "
            + "where u.validity_id = :validityId and (u.to_manufacture_year IS NULL OR "
            + "u.to_manufacture_year >= :fromManufactureYear"
            + ")")
    Integer countManifactureOverlappingFromDateOnTheSameValidityPeriod(@Param("fromManufactureYear") LocalDateTime fromManufactureYear, @Param("validityId") Integer validityId);
    
    @Query(nativeQuery = true, value = "SELECT COUNT(*) FROM abms.unified_tax u "
            + "where u.validity_id = :validityId and u.id != :utId and "
            + "((:toManufactureYear >= u.from_manufacture_year AND u.to_manufacture_year >= :fromManufactureYear)"
            + " OR (u.from_manufacture_year IS NULL AND u.to_manufacture_year >= :fromManufactureYear)"
            + " OR (u.to_manufacture_year IS NULL AND :toManufactureYear >= u.from_manufacture_year))")
    Integer countManifactureOverlappingFromAndToDatesOnTheSameValidityPeriodExcludingCurrentId(
            @Param("fromManufactureYear") LocalDateTime fromManufactureYear, @Param("toManufactureYear")LocalDateTime toManufactureYear, @Param("validityId") Integer validityId, @Param("utId") Integer utId);

    @Query(nativeQuery = true, value = "SELECT COUNT(*) FROM abms.unified_tax u "
            + "where u.validity_id = :validityId and u.id != :utId and (u.from_manufacture_year IS NULL OR "
            + ":toManufactureYear >= u.from_manufacture_year"
            + ")")
    Integer countManifactureOverlappingToDateOnTheSameValidityPeriodExcludingCurrentId(@Param("toManufactureYear") LocalDateTime toManufactureYear, @Param("validityId") Integer validityId, @Param("utId") Integer utId);

    @Query(nativeQuery = true, value = "SELECT COUNT(*) FROM abms.unified_tax u "
            + "where u.validity_id = :validityId and u.id != :utId and (u.to_manufacture_year IS NULL OR "
            + "u.to_manufacture_year >= :fromManufactureYear"
            + ")")
    Integer countManifactureOverlappingFromDateOnTheSameValidityPeriodExcludingCurrentId(@Param("fromManufactureYear") LocalDateTime fromManufactureYear, @Param("validityId") Integer validityId, @Param("utId") Integer utId);

}
