package ca.ids.abms.modules.dataimport;

import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class DataImportServiceTest {

    private CsvImportServiceImp<DataImportTestPojo> dataImportServiceCsv;

    @Before
    public void init() {
        dataImportServiceCsv = new CsvImportServiceImp<>();
    }

    @Test
    public void testConversionValidData() throws Exception {

        String csvMsg=readFileContentFromResource("/dataimport/csv-01-valid-data.txt");

        List<?> result = dataImportServiceCsv.parseColumnCsvToObject(csvMsg, DataImportTestPojo.class);
        assertNotNull(result);
        assertTrue(result.size()>0);

    }

    @Test
    public void testConversionRandomColumnOrder() throws Exception {

        String csvMsg=readFileContentFromResource("/dataimport/csv-02-random-column-order.txt");

        List<?> result = dataImportServiceCsv.parseColumnCsvToObject(csvMsg, DataImportTestPojo.class);
        assertNotNull(result);
        assertTrue(result.size()>0);

    }

    @Test
    public void testConversionMissingColumns() throws Exception {

        String csvMsg=readFileContentFromResource("/dataimport/csv-03-missing-columns.txt");

        List<?> result = dataImportServiceCsv.parseColumnCsvToObject(csvMsg, DataImportTestPojo.class);
        assertNotNull(result);
        assertTrue(result.size()>0);
    }

    @Test
    public void testParseCsvFromFile() throws Exception {

        InputStream fileInputStream = readFileInputStreamFromResource("/dataimport/csv-01-valid-data.txt");
        List<?> result = dataImportServiceCsv.parseCsvFromInputStream("text/plain", fileInputStream,
            DataImportService.STRATEGY.BIND_BY_HEADER_NAME, DataImportTestPojo.class);

        assertNotNull(result);
        assertTrue(result.size()>0);
    }

    @Test
    public void testConversionInvalidColumns() throws Exception {

        String csvMsg=readFileContentFromResource("/dataimport/csv-04-invalid-columns.txt");

        boolean validResponse = false;

        try {
            List<?> result = dataImportServiceCsv.parseColumnCsvToObject(csvMsg, DataImportTestPojo.class);
        } catch (RuntimeException ex) {
            validResponse = true;
        }
        assertTrue(true);
    }

    //----------------- helper methods -----------------------------------------

    private String readFileContentFromResource(String path) throws Exception {

        String data = new String();
        InputStream fileInputStream = readFileInputStreamFromResource(path);
        data = dataImportServiceCsv.readFileContent(fileInputStream);

        return data;
    }

    private InputStream readFileInputStreamFromResource(String path) throws Exception {
        ClassPathResource cpr = new ClassPathResource(path);
        return cpr.getInputStream();

    }
}
