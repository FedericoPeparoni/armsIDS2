package ca.ids.abms.modules.dataimport;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

/**
 * This interface should be used by controllers that implement file import
 * @author i.protasov
 *
 */
@Deprecated
public interface DataImportController {
    /**
     * Request example:
     * POST http://localhost:8080/api/passenger-service-charge-return/upload
     *
     * multipart/form-data
     *
     * Body:
     * file=demo.csv
     *
     */
    public ResponseEntity <String> upload (final @RequestParam("file") MultipartFile file);
}
