package ca.ids.abms.modules.charges;

import java.util.List;

import ca.ids.abms.config.db.ABMSRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ServiceChargeCatalogueRepository extends ABMSRepository<ServiceChargeCatalogue, Integer> {

    @Query (nativeQuery = true, value =
            "SELECT * from service_charge_catalogues where invoice_category in (:categories)")
    List<ServiceChargeCatalogue> findChargesOfCategories (final @Param("categories") List <String> categories);

}
