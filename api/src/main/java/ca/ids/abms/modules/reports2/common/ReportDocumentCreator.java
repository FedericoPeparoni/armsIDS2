package ca.ids.abms.modules.reports2.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import ca.ids.abms.modules.common.dto.MediaDocument;
import ca.ids.abms.modules.system.SystemConfigurationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.apache.commons.io.FilenameUtils;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.multipdf.PDFMergerUtility.DocumentMergeMode;
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

	public ReportDocumentCreator(final XmlDocumentSerializer xmlDocumentSerializer,
			final Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder,
			final DocumentBuilderFactory documentBuilderFactory, final BirtReportCreator birtReportCreator,
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
		xmlDocumentSerializer.write(xmlDoc, outputStream);
	}

	/**
	 * Format a report data object as any of the formats supported by BIRT (pdf,
	 * xlsx, docx). The document will be generated using the specified BIRT report
	 * by first converting the data object to XML. BIRT report template must contain
	 * a single XML-based data source called "xml".
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
							final byte[] binaryData = birtReportCreator
									.createInvoiceReportFromXml(invoiceTemplateCategory, format, xmlDoc, null, "xml");
							outputStream.write(binaryData);
						} catch (final RuntimeException x) {
							// Catch any runtime exceptions separately and throw as-is we DO NOT want them
							// lumped together with
							// non-runtime exceptions. This must be done as calls within do not handle
							// exceptions appropriately.
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

	private <T> void writeCsv(final List<T> data, final Class<T> clazz, final OutputStream outputStream,
			boolean exportFromTable) {
		try (final InputStream inputStream = csvWriter.createStream(data, clazz, exportFromTable)) {

			// outputStream.write('\ufeef'); // emits 0xef
			// outputStream.write('\ufebb'); // emits 0xbb
			// outputStream.write('\ufebf'); // emits 0xbf

			outputStream.write(character1); // emits 0xef
			outputStream.write(character2); // emits 0xbb
			outputStream.write(character3); // emits 0xbf

			IOUtils.copy(inputStream, outputStream);
		} catch (final RuntimeException x) {
			x.printStackTrace();
			// Catch any runtime exceptions separately and throw as-is we DO NOT want them
			// lumped together with
			// non-runtime exceptions. This must be done as calls within do not handle
			// exceptions appropriately.
			throw x;
		} catch (final Exception x) {
			x.printStackTrace();
			throw new IllegalStateException(x);
		}
	}

	private <T> void appendCsv(final List<T> data, final Class<T> clazz, final OutputStream outputStream,
			boolean exportFromTable, boolean header) {
		try (final InputStream inputStream = csvWriter.createStream(data, clazz, exportFromTable, header)) {
			IOUtils.copy(inputStream, outputStream);
		} catch (final RuntimeException x) {
			x.printStackTrace();
			// Catch any runtime exceptions separately and throw as-is we DO NOT want them
			// lumped together with
			// non-runtime exceptions. This must be done as calls within do not handle
			// exceptions appropriately.
			throw x;
		} catch (final Exception x) {
			x.printStackTrace();
			throw new IllegalStateException(x);
		}
	}

	/**
	 * Format a list of objects as a CSV file
	 */
	public <T> ReportDocument createCsvDocument(final String name, final List<T> data, final Class<T> clazz,
			boolean exportFromTable) {
		return do_createReportDocument(name, ReportFormat.csv,
				outputStream -> writeCsv(data, clazz, outputStream, exportFromTable));
	}

	public <T> void appendToCsvDocument(ReportDocument reportDocument, final List<T> data, final Class<T> clazz,
			boolean exportFromTable, boolean header) throws IOException {

		/*
		 * try(ByteArrayOutputStream stream = new ByteArrayOutputStream()){ //converto
		 * data to outputstream appendCsv(data, clazz, stream, exportFromTable);
		 * reportDocument.appendData(stream.toByteArray()); }catch (IOException ex){
		 * ex.printStackTrace(); }
		 */

		// Attenzione Crea multiple Header
		appendCsv(data, clazz, reportDocument.getOutputStream(), exportFromTable, header);

	}

	private static File createTempFile(Optional<String> suffix) throws IOException {
		Path tempPathFile = Files.createTempFile("arms", suffix.orElse(null));
		File tempFile = tempPathFile.toFile();
		tempFile.deleteOnExit();
		return tempFile;
	}

	/**
	 * Format a report data object as JSON
	 */
	private <T> void writeJson(final T data, final OutputStream outputStream) {
		final ObjectMapper objectMapper = this.jackson2ObjectMapperBuilder.build();
		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
		try {
			objectMapper.writer().writeValue(outputStream, data);
		} catch (final IOException x) {
			throw new IllegalStateException(x);
		}
	}

	/**
	 * Format a report data object as a JSON file
	 */
	public <T> ReportDocument createJsonDocument(final String name, final T data) {
		return do_createReportDocument(name, ReportFormat.json, outputStream -> writeJson(data, outputStream));
	}

	private void buildZipOutputStream(List<ReportDocument> values, OutputStream outputStream) throws Exception {
		try (ZipOutputStream zipOut = new ZipOutputStream(outputStream)) {
			for (ReportDocument fileToZip : values) {
				try (FileInputStream fis = new FileInputStream(fileToZip.getFile())) {

					ZipEntry zipEntry = new ZipEntry(buildFilenameEntryZip(fileToZip));
					zipOut.putNextEntry(zipEntry); // casting of length fails if file is bigger than 2147483647 bytes.
					byte[] bytes = new byte[(int) fileToZip.getFile().length()];
					int length;
					while ((length = fis.read(bytes)) >= 0) { // NOPMD
						zipOut.write(bytes, 0, length);
					}
				}
			}
		}
	}


	  private static String buildFilenameEntryZip( ReportDocument reportDocument){
		  if(!reportDocument.fileName().contains(".pdf")) {
			  return new StringBuilder(FilenameUtils.getBaseName(reportDocument.fileName()))
					  .append(".pdf").toString();
		  }else {
			  return reportDocument.fileName();
		  }
	  }



	public ReportDocument combinePdfFilesInZip(String bundleName, List<ReportDocument> values) {
		System.gc();
		FileOutputStream out = null;
		try {
			if(!values.isEmpty()) {
				File tempFile = createTempFile(Optional.empty());
				out = new FileOutputStream(tempFile);
				buildZipOutputStream(values, out);

				return do_createReportDocument(bundleName, ReportFormat.zip, out, tempFile);
			}else {
				return do_createReportDocument(bundleName, ReportFormat.zip, new byte[0]);
			}
		} catch (final IOException x) {
			throw new CustomParametrizedException("Failed to combine multiple PDF files into one" + ": " + x.getMessage(), x);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new CustomParametrizedException("Failed to create ZIP files into one" + ": " + e.getMessage(), e);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public ReportDocument combinePdfFiles(String bundleName, List<ReportDocument> values, Boolean preview) {
		if (preview) {
			return combinePdfFilesInOne(bundleName, values);
		} else {
			return combinePdfFilesInZip(bundleName, values);
		}
	}

	/**
	 * Combine multiple PDF files into one
	 *
	 * @throws IOException
	 */
	public ReportDocument combinePdfFilesInOne(String bundleName, List<ReportDocument> values) {
		System.gc();
		FileOutputStream out = null;
		try {
			if (!values.isEmpty()) {
				File tempFile = createTempFile(Optional.empty());
				out = new FileOutputStream(tempFile);
				PDFMergerUtility ut = new PDFMergerUtility();
				ut.addSources(values.stream().map(d -> (InputStream) new ByteArrayInputStream(d.data())).collect(Collectors.toList()));
				ut.setDestinationStream(out);
				ut.setDocumentMergeMode(DocumentMergeMode.OPTIMIZE_RESOURCES_MODE);
				ut.mergeDocuments(MemoryUsageSetting.setupMixed(1024L * 512L));

				return do_createReportDocument(bundleName, ReportFormat.pdf, out, tempFile);
			} else {
				return do_createReportDocument(bundleName, ReportFormat.pdf, new byte[0]);
			}
		} catch (final IOException x) {
			throw new CustomParametrizedException("Failed to combine multiple PDF files into one" + ": " + x.getMessage(), x);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	// ---------------------- private ----------------------------

	private ReportDocument do_createReportDocument(final String fileName,
			@SuppressWarnings("SameParameterValue") final ReportFormat format, final byte[] data) {
		return new ReportDocument(do_createFileName(fileName, format.fileNameSuffix()), format.contentType(), data);
	}

	private ReportDocument do_createReportDocument(final String fileName,
			@SuppressWarnings("SameParameterValue") final ReportFormat format, final FileOutputStream outputStream,
			File file) {
		return new ReportDocument(do_createFileName(fileName, format.fileNameSuffix()), format.contentType(),
				outputStream, file);
	}

	private ReportDocument do_createReportDocument(final String fileName, final ReportFormat format,
			final Consumer<OutputStream> callback) {
		return do_createReportDocument(fileName, format.fileNameSuffix(), format.contentType(), callback);
	}

	private ReportDocument do_createReportDocument(final String fileName, final String fileSuffix,
			final String contentType, final Consumer<OutputStream> callback) {
		final String fullFileName = do_createFileName(fileName, fileSuffix);
		final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		callback.accept(outputStream);
		LOG.debug("ReportDocument is ready with fullFileName {} and contentType {} ", fullFileName, contentType);
		return new ReportDocument(fullFileName, contentType, outputStream.toByteArray());
	}

	private static String do_createFileName(final String fileName, final String fileSuffix) {
		final String sanitized = (fileName + fileSuffix).replaceAll("[^\\x20-\\xfe:/$|%\"'=]+", "_");
		if (sanitized.length() > 100) {
			return sanitized.substring(0, 100);
		}
		return sanitized;
	}

	private final XmlDocumentSerializer xmlDocumentSerializer;
	private final Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder;
	private final DocumentBuilderFactory documentBuilderFactory;
	private final BirtReportCreator birtReportCreator;
}
