package ca.ids.abms.plugins.amhs.fpl;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Function;

public class RefMessageResource {

    public final String name;
    public <T> T read (final Function <InputStream, T> callback) {
        try (final InputStream stream = open()) {
            return callback.apply (stream);
        }
        catch (final IOException x) {
            throw new RuntimeException (x);
        }
        
    }
    @Override
    public String toString() {
        return name;
    }
    protected InputStream open() throws IOException {
        return new FileInputStream (name);
    }
    public RefMessageResource (final String filename) {
        this.name = filename;
    }
}
