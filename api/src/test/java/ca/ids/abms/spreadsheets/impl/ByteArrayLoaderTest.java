package ca.ids.abms.spreadsheets.impl;

import org.junit.Test;

import ca.ids.abms.spreadsheets.SSException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.Charset;

public class ByteArrayLoaderTest {
    
    private static final String createTempFile() throws Exception {
        return File.createTempFile("test", ".tmp").toString();
    }
    private static void writeFile (final String fileName, final String data) throws Exception {
        try (final FileOutputStream stream = new FileOutputStream (fileName)) {
            stream.write (data.getBytes(Charset.forName("UTF-8")));
        }
    }

    @Test
    public void testBasic() throws Exception {
        final String fileName = createTempFile();
        try {
            writeFile (fileName, "01234");

            {
                final ByteArrayLoader x = ByteArrayLoader.loadFile(fileName, 999);
                assertThat (x.sourceName).isEqualTo (fileName);
                assertThat (new String (x.data, "UTF-8")).isEqualTo ("01234");
            };

            {
                final ByteArrayLoader x = ByteArrayLoader.loadFile(fileName, 5);
                assertThat (new String (x.data, "UTF-8")).isEqualTo ("01234");
            };

            {
                final Throwable thrown = catchThrowable(()->{
                    ByteArrayLoader.loadFile(fileName, 4);
                });
                assertThat (thrown).isInstanceOf (SSException.class).hasMessageContaining("file too large");
            };
        }
        finally {
            new File (fileName).delete();
        }
    }
}
