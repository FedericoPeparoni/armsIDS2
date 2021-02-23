package ca.ids.abms.modules.reports2.common;

import java.util.function.Function;
import java.util.function.Supplier;

import ca.ids.abms.modules.common.controllers.MediaDocumentComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

public abstract class ReportControllerBase extends MediaDocumentComponent {

    private static final Logger LOG = LoggerFactory.getLogger (ReportControllerBase.class);

    /**
     * Create a response containing the report document file as a named attachment.
     *
     * @param preview If true rollback current transaction at the end.
     * @param callback A function that will be passed a boolean flag that indicates whether
     *                 we are called in preview mode. It should return the generated document(s)
     *                 as a ReportData object.
     */
    protected final ResponseEntity<ByteArrayResource> doCreateBinaryResponse (final Boolean preview, final Function<Boolean, ReportDocument> callback) {
        boolean fakeInvoiceNumber = false;
        if (preview != null && preview) {
            LOG.info("Current transaction will be rolled back because we are in preview mode");
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            fakeInvoiceNumber = true;
        }
        return doCreateBinaryResponse(callback.apply(fakeInvoiceNumber));
    }

    /**
     * Create a response containing the report document file as a named attachment.
     *
     * @param callback A function that will be passed a boolean flag that indicates whether
     *                 we are called in preview mode. It should return the generated document(s)
     *                 as a ReportData object.
     */
    protected final <T> ResponseEntity<T> do_createResponse(final Supplier <T> callback) {
        final T res = callback.get();
        ResponseEntity <T> responseEntity;
        if (res != null) {
            responseEntity = ResponseEntity.ok().body(res);
        } else {
            responseEntity = new ResponseEntity(HttpStatus.OK);
        }
        return responseEntity;
    }
}
