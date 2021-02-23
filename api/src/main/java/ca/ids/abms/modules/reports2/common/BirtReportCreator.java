package ca.ids.abms.modules.reports2.common;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.function.BiConsumer;

import javax.persistence.EntityManager;
import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.report.engine.api.DocxRenderOption;
import org.eclipse.birt.report.engine.api.EXCELRenderOption;
import org.eclipse.birt.report.engine.api.EngineException;
import org.eclipse.birt.report.engine.api.IGetParameterDefinitionTask;
import org.eclipse.birt.report.engine.api.IParameterDefnBase;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportRunnable;
import org.eclipse.birt.report.engine.api.IRunAndRenderTask;
import org.eclipse.birt.report.engine.api.PDFRenderOption;
import org.eclipse.birt.report.engine.api.RenderOption;
import org.eclipse.birt.report.engine.api.impl.ScalarParameterDefn;
import org.eclipse.birt.report.model.api.OdaDataSourceHandle;
import org.eclipse.birt.report.model.api.ReportDesignHandle;
import org.eclipse.birt.report.model.api.activity.SemanticException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;

import ca.ids.abms.config.error.CustomParametrizedException;
import ca.ids.abms.modules.reports2.InvoiceTemplateCategory;
import ca.ids.abms.util.xml.XmlDocumentSerializer;

@Component
public class BirtReportCreator {

    private static final Logger LOG = LoggerFactory.getLogger(BirtReportCreator.class);

    private final IReportEngine reportEngine;
    private final XmlDocumentSerializer xmlWriter;
    private final EntityManager entityManager;
    private final DataSource dataSource;
    private final ReportHelper reportHelper;

    public BirtReportCreator(
        final IReportEngine reportEngine,
        final XmlDocumentSerializer xmlWriter,
        final EntityManager entityManager,
        final DataSource dataSource,
        final ReportHelper reportHelper
    ) {
        this.reportEngine = reportEngine;
        this.xmlWriter = xmlWriter;
        this.entityManager = entityManager;
        this.dataSource = dataSource;
        this.reportHelper = reportHelper;
    }

    /**
     * Render an invoice document. This method will look for the .rptdesign file named
     * 
     * <code><pre>(src/main/resources/)reports/invoices/{{billingOrgCode}}/{{invoiceTemplateCategory}}.rptdesign</pre></code>
     */
    public byte[] createInvoiceReportFromXml (final InvoiceTemplateCategory invoiceTemplateCategory, ReportFormat format, final Document xmlDoc, final Map <String, Object> params, final String dataSourceName) throws Exception {
        LOG.debug("Getting InputStream for invoiceTemplateCategory {}", invoiceTemplateCategory);
        InputStream reportBirtTemplateInputStream = reportHelper.openInvoiceReportTemplate(invoiceTemplateCategory);

    	return do_createReportFromXml(reportBirtTemplateInputStream,format,xmlDoc,params,dataSourceName);
    }

    /**
     * Render an "other"-type (non-invoice) report based on a self-contained BIRT template.
     * The template is expected to fetch its own data if necessary by executing SQL etc.
     * This method will look for the .rptdesign file named
     * 
     * <code><pre>(src/main/resources/)reports/other/{{billingOrgCode}}/{{pathToDesignFile}}.rptdesign</pre></code>
     */
    @Transactional(readOnly = true)
    public byte[] createOtherReport(String pathToDesignFile, Map<String, Object> parameters, ReportFormat format) throws Exception {
        try (
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                final BirtCloseableRunAndRenderTaskWrapper taskWrapper = doCreateOtherRenderTask(format, pathToDesignFile, outputStream)
            ) {

            final IGetParameterDefinitionTask taskGetParameters = reportEngine.createGetParameterDefinitionTask(taskWrapper.task().getReportRunnable());

            @SuppressWarnings("rawtypes")
            final Collection params = taskGetParameters.getParameterDefns(true);
            @SuppressWarnings("rawtypes")
            final Iterator iter = params.iterator();

            while (iter.hasNext()) {
                IParameterDefnBase param = (IParameterDefnBase) iter.next();

                if (parameters.containsKey(param.getName())) {
                    final Object inputParameter = parameters.get(param.getName());
                    LOG.debug("Template {}: added inputParameter with name={}, data type={}, value={}", pathToDesignFile,
                    param.getName(), ((ScalarParameterDefn) param).getDataType(), inputParameter.toString());
                    if (((ScalarParameterDefn) param).getScalarParameterType().equals("multi-value")) {
                        final String[] parametersArray = StringUtils.split((String)inputParameter, ';');
                        if ((parametersArray == null || parametersArray.length == 0) && (!((ScalarParameterDefn)param).allowBlank())) {
                            throw new CustomParametrizedException("Parameter required is empty", param.getName());
                        }
                        setParameter(taskWrapper.task(), param.getName(), parametersArray, ((ScalarParameterDefn) param).getDataType());
                    } else {
                        setParameter(taskWrapper.task(), param.getName(), inputParameter, ((ScalarParameterDefn) param).getDataType());
                    }
                } else {
                    if (!((ScalarParameterDefn)param).allowNull()) {
                        LOG.debug("Parameter required isn't provided, {}", param.getName());
                        throw new CustomParametrizedException("Parameter required isn't provided", param.getName());
                    }
                }
            }
            taskWrapper.task().run();
            return outputStream.toByteArray();
        }
    }
    
    
    // -------------------------------- private -------------------------

    /**
     * Render a BIRT document given some XML data and report parameters. The BIRT report
     * template must be already opened and passed as a stream to this method.
     */
    @SuppressWarnings("squid:S00112")
    private byte[] do_createReportFromXml (final InputStream reportBirtTemplateInputStream, ReportFormat format, final Document xmlDoc, final Map <String, Object> params, final String dataSourceName) throws Exception {
        try (
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                BirtCloseableRunAndRenderTaskWrapper taskWrapper = doCreateRenderTask(format, reportBirtTemplateInputStream, outputStream);
            ) {

            final ReportDesignHandle reportDesignHandle = (ReportDesignHandle) taskWrapper.task().getReportRunnable()
                    .getDesignHandle();
            final OdaDataSourceHandle dataSourceHandle = (OdaDataSourceHandle) reportDesignHandle
                    .findDataSource(dataSourceName);
            if (dataSourceName == null) {
                throw new RuntimeException("Data source `" + dataSourceName + "' not found in BIRT report");
            }
            if (params != null) {
                params.forEach((k, v) -> {
                    LOG.debug("Setting parameter {}: {}", k, v);
                    taskWrapper.task().setParameterValue(k, v);
                });
            }
            do_useTempXmlFile((fileName, tempFileOutputStream) -> {
                try {
                    this.xmlWriter.write(xmlDoc, new BufferedOutputStream(tempFileOutputStream));
                    dataSourceHandle.setProperty("FILELIST", fileName);
                    taskWrapper.task().run();
                } catch (final SemanticException | EngineException x) {
                    throw new RuntimeException(x);
                }
            });
            LOG.debug("Output stream has been generated");
            return outputStream.toByteArray();
        }
    }

    /**
     * Create a BIRT render task for the give "other"-type (non-invoice) report.
     * The design file is assumed to be named
     * <code><pre>(src/main/resources/)reports/other/{{billingOrgCode}}/{{pathToDesignFile}}.rptdesign</pre></code>
     */
    private BirtCloseableRunAndRenderTaskWrapper doCreateOtherRenderTask (final ReportFormat format, final String pathToDesignFile, OutputStream outputStream) throws Exception {
        return doCreateRenderTask(format, reportHelper.openOtherReportTemplate (pathToDesignFile), outputStream);
    }

    /**
     * Create a BIRT render task; the template file must be opened and passed as a parameter by the caller.
     */
    private BirtCloseableRunAndRenderTaskWrapper doCreateRenderTask(final ReportFormat format, final InputStream reportBirtTemplateInputStream, final OutputStream outputStream) throws Exception {
        BirtCloseableRunAndRenderTaskWrapper taskWrapper = null;
        boolean done = false;
        try {
            IReportRunnable runnable = reportEngine.openReportDesign(reportBirtTemplateInputStream);
            taskWrapper = do_createRenderTask(runnable);
            RenderOption option = null;
            //Changed for US 65037
            switch(format) {
                case docx:
                    option = new DocxRenderOption();
                    option.setOutputFormat("docx");
                    break;
                case xlsx:
                    option = new EXCELRenderOption();
                    option.setOutputFormat("xlsx");
                    break;
                default:
                    option = new PDFRenderOption();
                    option.setOutputFormat("pdf");
            }
            option.setOutputStream(outputStream);
            taskWrapper.task().setRenderOption(option);
            done = true;
            return taskWrapper;

        } finally {
        	if (reportBirtTemplateInputStream!=null) {
        		reportBirtTemplateInputStream.close();
        	}
        	if (!done && taskWrapper != null) {
        	    taskWrapper.close();
        	}
        }
    }

    /**
     * Initializes and configures a BIRT render task with a JDBC connection.
     */
    @SuppressWarnings("unchecked")
    private BirtCloseableRunAndRenderTaskWrapper do_createRenderTask(IReportRunnable runnable) throws SQLException {
        entityManager.flush();

        // Get JDBC connection
        final Connection connection = dataSource.getConnection();
        BirtCloseableRunAndRenderTaskWrapper taskWrapper = null;
        try {
            IRunAndRenderTask task = reportEngine.createRunAndRenderTask(runnable);
            task.getAppContext().put("OdaJDBCDriverPassInConnection", connection);
            taskWrapper = new BirtCloseableRunAndRenderTaskWrapper (task, connection);
        }
        finally {
            if (taskWrapper == null) {
                connection.close();
            }
        }

        return taskWrapper;
    }


    /**
     * Create a temp file, open it for writing, execute the provided callback, then delete the temp file
     */
    private void do_useTempXmlFile (final BiConsumer <String, OutputStream> callback) throws Exception {
        final File file = File.createTempFile("abms_birt_", ".xml");
        try {
            try (final FileOutputStream stream = new FileOutputStream (file)) {
                callback.accept(file.toString(), stream);
            }
        }
        finally {
            file.delete();
        }
    }

    /**
     * Set a BIRT report parameter
     */
    private void setParameter(final IRunAndRenderTask task, final String name, final String[] value, int dataType) throws BirtException {
        task.setParameterValue(name, TemplateDataType.convertValue(value, dataType));
    }

    /**
     * Set a BIRT report parameter
     */
    private void setParameter(final IRunAndRenderTask task, final String name, final Object value, int dataType) throws BirtException {
        task.setParameterValue(name, TemplateDataType.convertValue(value, dataType));
    }

}
