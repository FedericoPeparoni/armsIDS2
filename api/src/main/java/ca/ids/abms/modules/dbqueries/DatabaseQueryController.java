package ca.ids.abms.modules.dbqueries;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.HandlerMapping;

/**
 * Database queries controller.
 * <p>
 * An HTTP request such as <code>GET /api/dbqueries/SOME/NAME</code> will load
 * and execute an SQL query from the file <code>/etc/abms/api/dbqueries/SOME/NAME.sql</code> in Linux
 * or <code>$PROJECT_DIR/dbqueries/SOME/NAME.sql</code> in Eclipse.
 *
 */
@RestController
@RequestMapping(DatabaseQueryController.CONTROLLER_PATH)
public class DatabaseQueryController {

    public static final String CONTROLLER_PATH = "/api/dbqueries";

    public DatabaseQueryController (final DatabaseQueryService databaseQueryService) {
        this.databaseQueryService = databaseQueryService;
    }
    
    // TODO: add permission annotations

    // This will match: /api/dbqueries/any/path/suffix
    @RequestMapping (value = "/**", method = { RequestMethod.GET, RequestMethod.POST })
    public ResponseEntity <List <Map <String, Object>>> query (
            final @RequestParam (required = false) Map <String, Object> queryParams,
            final @RequestBody (required = false) Map <String, Object> bodyParams,
            final HttpServletRequest req
    ) {
        // See here: http://stackoverflow.com/questions/3686808/spring-3-requestmapping-get-path-value
        final String name = ((String)req.getAttribute (HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE)).substring (CONTROLLER_PATH.length()).replaceAll("^/+", "");
        final Map <String, Object> params = new HashMap <>();
        if (bodyParams != null) {
            params.putAll (bodyParams);
        }
        if (queryParams != null) {
            params.putAll (queryParams);
        }
        final List <Map <String, Object>> result = databaseQueryService.query (name, params).data();
        return ResponseEntity.ok().body (result);
    }

    private final DatabaseQueryService databaseQueryService;

}
