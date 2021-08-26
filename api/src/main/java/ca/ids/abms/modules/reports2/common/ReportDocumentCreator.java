package ca.ids.abms.modules.reports2.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import ca.ids.abms.modules.common.dto.MediaDocument;
import ca.ids.abms.modules.system.SystemConfigurationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;

import ca.ids.abms.config.error.CustomParametrizedException;
import ca.ids.abms.modules.reports2.InvoiceTemplateCategory;
import ca.ids.abms.util.csv.CsvWriter;
import ca.ids.abms.util.xml.XmlDocumentSerializer;

@Component
public class ReportDocumentCreator {

    private static final Logger LOG = LoggerFactory.getLogger(ReportDocumentCreator.class);
    private final CsvWriter csvWriter;

    public ReportDocumentCreator (final XmlDocumentSerializer xmlDocumentSerializer,
                                  final Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder,
                                  final DocumentBuilderFactory documentBuilderFactory,
                                  final BirtReportCreator birtReportCreator,
                                  final SystemConfigurationService systemConfigurationService) {
        this.xmlDocumentSerializer = xmlDocumentSerializer;
        this.jackson2ObjectMapperBuilder = jackson2ObjectMapperBuilder;
        this.documentBuilderFactory = documentBuilderFactory;
        this.birtReportCreator = birtReportCreator;
        this.csvWriter = new CsvWriter(systemConfigurationService);
    }

    /**
     * Save an XML document to an output stream
     */
    private void writeXml(final Document xmlDoc, final OutputStream outputStream) {
        xmlDocumentSerializer.write (xmlDoc, outputStream);
    }

    /**
     * Format a report data object as any of the formats supported by BIRT (pdf, xlsx, docx). The document
     * will be generated using the specified BIRT report by first converting the data object to XML. BIRT report
     * template must contain a single XML-based data source called "xml".
     */
    public <T> ReportDocument createXmlBasedInvoiceDocument(final String name, final T data, final ReportFormat format,
            final InvoiceTemplateCategory invoiceTemplateCategory) {
        switch (format) {
        case xml:
        case pdf:
        case xlsx:
        case docx:
            try {
                final Document xmlDoc = documentBuilderFactory.newDocumentBuilder().newDocument();
                final JAXBContext context = JAXBContext.newInstance(data.getClass());
                final Marshaller marshaller = context.createMarshaller();
                marshaller.marshal(data, xmlDoc);
                LOG.debug("Creating report for name {} and format {} ", name, format);
                if (!format.equals(ReportFormat.xml)) {
                    return do_createReportDocument(name, format, outputStream -> {
                        try {
                            final byte[] binaryData = birtReportCreator.createInvoiceReportFromXml(invoiceTemplateCategory,
                                    format, xmlDoc, null, "xml");
                            outputStream.write(binaryData);
                        } catch (final RuntimeException x) {
                            // Catch any runtime exceptions separately and throw as-is we DO NOT want them lumped together with
                            // non-runtime exceptions. This must be done as calls within do not handle exceptions appropriately.
                            throw x;
                        } catch (final Exception x) {
                            LOG.error("Error creating report --> {}", x.getMessage());
                            throw new IllegalStateException(x);
                        }
                    });
                } else {
                    return do_createReportDocument(name, format, outputStream -> writeXml(xmlDoc, outputStream));
                }
            } catch (ParserConfigurationException | JAXBException x) {
                LOG.error("Error creating report --> {}", x.getMessage());
                throw new IllegalStateException(x);
            }
        default:
            throw new IllegalArgumentException("format is invalid");
        }
    }

    /**
     * Format a list of objects as CSV
     */

    private static final char character1 = '\ufeef'; // emits 0xef
    private static final char character2 = '\ufebb'; // emits 0xbb
    private static final char character3 = '\ufebf'; // emits 0xbf
    private <T> void writeCsv(final List<T> data, final Class<T> clazz, final OutputStream outputStream, boolean exportFromTable) {
        try (final InputStream inputStream = csvWriter.createStream (data, clazz, exportFromTable)){

            //outputStream.write('\ufeef'); // emits 0xef
            //outputStream.write('\ufebb'); // emits 0xbb
            //outputStream.write('\ufebf'); // emits 0xbf

            outputStream.write(character1); // emits 0xef
            outputStream.write(character2); // emits 0xbb
            outputStream.write(character3); // emits 0xbf

            IOUtils.copy (inputStream, outputStream);
        } catch (final RuntimeException x) {
            x.printStackTrace();
            // Catch any runtime exceptions separately and throw as-is we DO NOT want them lumped together with
            // non-runtime exceptions. This must be done as calls within do not handle exceptions appropriately.
            throw x;
        } catch (final Exception x) {
            x.printStackTrace();
            throw new IllegalStateException(x);
        }
    }

    private <T> void appendCsv(final List<T> data, final Class<T> clazz, final OutputStream outputStream, boolean exportFromTable, boolean header) {
        try (final InputStream inputStream = csvWriter.createStream (data, clazz, exportFromTable, header)){
            IOUtils.copy (inputStream, outputStream);
        } catch (final RuntimeException x) {
            x.printStackTrace();
            // Catch any runtime exceptions separately and throw as-is we DO NOT want them lumped together with
            // non-runtime exceptions. This must be done as calls within do not handle exceptions appropriately.
            throw x;
        } catch (final Exception x) {
            x.printStackTrace();
            throw new IllegalStateException(x);
        }
    }

    /**
     * Format a list of objects as a CSV file
     */
    public <T> ReportDocument createCsvDocument (final String name, final List <T> data, final Class<T> clazz, boolean exportFromTable) {
        return do_createReportDocument (name, ReportFormat.csv, outputStream -> writeCsv(data, clazz, outputStream, exportFromTable));
    }

    public <T> void appendToCsvDocument(ReportDocument reportDocument,final List <T> data,final Class<T> clazz, boolean exportFromTable, boolean header ) throws IOException {

     /*  try(ByteArrayOutputStream stream =	new ByteArrayOutputStream()){
    	    //converto data to outputstream
    	    appendCsv(data, clazz, stream, exportFromTable);
            reportDocument.appendData(stream.toByteArray());
        }catch (IOException ex){
    	    ex.printStackTrace();
        }*/

        //Oppure Salto un passaggio
        //Attenzione Crea multiple Header
        appendCsv(data, clazz, reportDocument.getOutputStream(), exportFromTable, header);


    }

    /**
     * Format a report data object as JSON
     */
    private <T> void writeJson(final T data, final OutputStream outputStream) {
        final ObjectMapper objectMapper = this.jackson2ObjectMapperBuilder.build();
        objectMapper.enable (SerializationFeature.INDENT_OUTPUT);
        try {
            objectMapper.writer().writeValue (outputStream, data);
        } catch (final IOException x) {
            throw new IllegalStateException(x);
        }
    }

    /**
     * Format a report data object as a JSON file
     */
    public <T> ReportDocument createJsonDocument (final String name, final T data) {
        return do_createReportDocument (name, ReportFormat.json, outputStream -> writeJson(data, outputStream));
    }

    /**
     * Combine multiple PDF files into one
     */
    public ReportDocument combinePdfFiles(String bundleName, List<ReportDocument> values) {
        final int approCombinedSize = values.stream().map (MediaDocument::contentLength).reduce (0, Integer::sum);
        final ByteArrayOutputStream out = new ByteArrayOutputStream (approCombinedSize);
        PDFMergerUtility ut = new PDFMergerUtility();
        ut.addSources(values.stream().map(d->(InputStream)new ByteArrayInputStream (d.data())).collect (Collectors.toList()));
        ut.setDestinationStream (out);
        try {
            ut.mergeDocuments(MemoryUsageSetting.setupMixed(1024L * 512L));
            return do_createReportDocument (bundleName, ReportFormat.pdf, out.toByteArray());
        } catch (final IOException x) {
            throw new CustomParametrizedException("Failed to combine multiple PDF files into one" + ": " + x.getMessage(), x);
        }
    }

    // ---------------------- private ----------------------------

    private ReportDocument do_createReportDocument (
        final String fileName, @SuppressWarnings("SameParameterValue") final ReportFormat format, final byte[] data
    ) {
        return new ReportDocument (do_createFileName (fileName, format.fileNameSuffix()), format.contentType(), data);
    }

    private ReportDocument do_createReportDocument (final String fileName, final ReportFormat format, final Consumer <OutputStream> callback) {
        return do_createReportDocument (fileName, format.fileNameSuffix(), format.contentType(), callback);
    }

    private ReportDocument do_createReportDocument (final String fileName, final String fileSuffix, final String contentType, final Consumer <OutputStream> callback) {
        final String fullFileName = do_createFileName (fileName, fileSuffix);
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        callback.accept (outputStream);
        LOG.debug("ReportDocument is ready with fullFileName {} and contentType {} ", fullFileName, contentType);
        return new ReportDocument (fullFileName, contentType, outputStream.toByteArray());
    }

    private static String do_createFileName (final String fileName, final String fileSuffix) {
        final String sanitized = (fileName + fileSuffix).replaceAll ("[^\\x20-\\xfe:/$|%\"'=]+", "_");
        if (sanitized.length() > 100) {
            return sanitized.substring (0, 100);
        }
        return sanitized;
    }

    private final XmlDocumentSerializer xmlDocumentSerializer;
    private final Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder;
    private final DocumentBuilderFactory documentBuilderFactory;
    private final BirtReportCreator birtReportCreator;
}
