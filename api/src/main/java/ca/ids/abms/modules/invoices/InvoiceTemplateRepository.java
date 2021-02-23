/**
 *
 */
package ca.ids.abms.modules.invoices;

import ca.ids.abms.config.db.ABMSRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InvoiceTemplateRepository extends ABMSRepository<InvoiceTemplate, Integer> {

	@Query(value="select * from " +
	"invoice_templates " +
	"where invoice_category=:invoiceCategory " +
	"order by created_at desc limit 1",
	nativeQuery = true)
    InvoiceTemplate findInvoiceTemplateByCategory(@Param("invoiceCategory") String invoiceCategory);

	Integer removeByInvoiceCategory(String invoiceCategory);
}
