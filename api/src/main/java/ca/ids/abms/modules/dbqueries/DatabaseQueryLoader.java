package ca.ids.abms.modules.dbqueries;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.LinkedHashSet;
import java.util.MissingResourceException;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;

/**
 * Load SQL formulas by name from SQL files.
 */
final class DatabaseQueryLoader {
    
    public DatabaseQueryLoader (final String queryFilesLocation) {
        this.queryFilesLocation = queryFilesLocation;
    }

    /**
     * Named query information
     */
    public static final class NamedQuery {
        
        /** The SQL */
        final String expr;
        
        /** List of required parameter names */
        final Set <String> requiredParameters;
        
        public NamedQuery (final String expr, final Set <String> requiredParameters) {
            this.expr = expr;
            this.requiredParameters = requiredParameters;
        }
    }

    /**
     * Load a query by name.
     * 
     * The query is loaded from a file named "classpath:/dbqueries/$NAME.sql" or similar.
     * In Linux the location is "/etc/abms/api/dbqueries/$NAME.sql" instead.
     * Names may include "/" which will map to subdirectories.
     * 
     */
    public NamedQuery load (final String name) {
        return do_compile (do_loadQueryFile (name));
    }
    
    // ---------------------- private ---------------------
    
    /** Read the query file into memory */
    private final String do_loadQueryFile (final String name) {
        final String fileLocation = queryFilesLocation + "/" + name + ".sql";
        final String classpathPrefix = "classpath:";
        InputStream inputStream;
        try {
            // classpath resource?
            if (fileLocation.startsWith (classpathPrefix)) {
                final String resourcePath = fileLocation.substring(classpathPrefix.length()).replaceAll("^/+", "");
                final URL resourceUrl = this.getClass().getClassLoader().getResource (resourcePath);
                if (resourceUrl == null) {
                    throw new MissingResourceException (fileLocation + ": missing resource", null, fileLocation);
                }
                // serving from file system: load file directly
                if (resourceUrl.getProtocol().equals("file")) {
                    inputStream = new FileInputStream (resourceUrl.getPath());
                }
                // serving from jar file: load resource
                else {
                    if ((inputStream = resourceUrl.openStream()) == null) {
                        throw new MissingResourceException (fileLocation + ": missing resource", null, fileLocation);
                    }
                }
            }
            // file system
            else {
                inputStream = new FileInputStream (fileLocation);
            }
            
            // load the file
            try (final InputStream stream = inputStream) {
                return do_loadQueryFile (fileLocation, stream);
            }
        }
        catch (final IOException x) {
            throw new RuntimeException (x);
        }
    }

    /** Read entire stream into a String */
    private final String do_loadQueryFile (final String resourcePath, final InputStream stream) throws IOException {
        // Postgres JDBC driver can't handle trailing semicolons
        return IOUtils.toString (stream, "UTF-8").replaceAll(";+$", "");
    }
    
    /** Compile SQL into a {@link NamedQuery} by extracting the ":VAR" parameters */
    private final NamedQuery do_compile (final String expr) {
        final Set <String> requiredParameters = new LinkedHashSet<> ();
        final Matcher m = RE_PARAMETERS.matcher (expr);
        while (m.find()) {
            requiredParameters.add (m.group(1));
        }
        return new NamedQuery (expr, requiredParameters);
    }
    
    private String queryFilesLocation;
    
    private final Pattern RE_PARAMETERS = Pattern.compile("(?<!:):([a-zA-Z_][a-zA-Z_0-9]*)");

}
