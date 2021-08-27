package ca.ids.abms.modules.invoices;

import ca.ids.abms.config.error.CustomParametrizedException;
import ca.ids.abms.modules.reports2.InvoiceTemplateCategory;
import ca.ids.abms.modules.reports2.common.ReportHelper;
import ca.ids.abms.modules.transactions.Transaction;
import ca.ids.abms.modules.util.models.PageImplCustom;
import ca.ids.abms.util.StringUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Transactional
public class InvoiceTemplateService {

	private final Logger log = LoggerFactory.getLogger(InvoiceTemplateService.class);

    @PersistenceContext
    private EntityManager em;

    private final InvoiceTemplateRepository invoiceTemplateRepository;
    private final InvoiceTemplateMapper invoiceTemplateMapper;
    private final ReportHelper reportHelper;

    public InvoiceTemplateService(final InvoiceTemplateRepository anInvoiceTemplateRepository,
                                  final InvoiceTemplateMapper invoiceTemplateMapper,
                                  final ReportHelper reportHelper) {
        this.invoiceTemplateRepository = anInvoiceTemplateRepository;
        this.invoiceTemplateMapper = invoiceTemplateMapper;
        this.reportHelper = reportHelper;
    }

    public Page<InvoiceTemplateViewModel> findAll(Pageable pageable, String searchFilter) {

        List<InvoiceTemplate> invoiceTemplateList = new ArrayList<>(InvoiceTemplateCategory.values().length);

        for (InvoiceTemplateCategory invoiceTemplateCategory : InvoiceTemplateCategory.values()) {
        	InvoiceTemplate invoiceTemplate = getInvoiceTemplateFromDbOrDefaultByCategory(invoiceTemplateCategory, false);
        	if (invoiceTemplate != null) {
        		invoiceTemplateList.add(invoiceTemplate);
        	}
        }
        int totalTemplates = invoiceTemplateList.size();

        if (StringUtils.isNotBlank(searchFilter)) {
            List<InvoiceTemplate> filteredList = invoiceTemplateList.stream().filter(t ->
                t.getInvoiceCategory().toLowerCase().contains(searchFilter.toLowerCase())
                    || t.getInvoiceTemplateName().toLowerCase().contains(searchFilter.toLowerCase())).collect(Collectors.toList());

            return new PageImplCustom<>(
                invoiceTemplateMapper.toViewModel(filteredList), pageable, filteredList.size(), totalTemplates);
        }
        return new PageImplCustom<>(
            invoiceTemplateMapper.toViewModel(invoiceTemplateList), pageable, invoiceTemplateList.size(), totalTemplates);

    }

    public Page<InvoiceTemplateViewModel> findAllForDownload(Pageable pageable, String searchFilter) {
        Page<InvoiceTemplateViewModel> invoiceTemplateViewModel = findAll(pageable, searchFilter);
        /*****************************
         The reason to flush is send the update SQL to DB .
         Otherwise ,the update will lost if we clear the entity manager
         afterward.
         ******************************/
        em.flush();
        em.clear();
        return invoiceTemplateViewModel;


    }

    protected InvoiceTemplate getInvoiceTemplateFromDbOrDefaultByCategory(final InvoiceTemplateCategory invoiceTemplateCategory, final boolean includeTemplateFile) {

    	InvoiceTemplate result = null;

    	if (invoiceTemplateCategory!=null) {

    		result = invoiceTemplateRepository.findInvoiceTemplateByCategory(invoiceTemplateCategory.name());

    		if (result==null) {
    			//return default
    			result = new InvoiceTemplate();
    			result.setInvoiceTemplateName(invoiceTemplateCategory.getReadableValue());
    			result.setInvoiceCategory(invoiceTemplateCategory.name());

    			result.setInvoiceFilename(invoiceTemplateCategory.name() + ".rptdesign"); //default file name
    			if (includeTemplateFile) {
	    			byte[] defaultReportTemplateDocument = reportHelper.getInvoiceTemplateFile(invoiceTemplateCategory);
	    			result.setTemplateDocument(defaultReportTemplateDocument);
	    			result.setMimeType("application/xml");
    			}
    		}
    	}

    	return result;
    }

    @Transactional(readOnly = true)
    private Page<InvoiceTemplate> findAllFromDb(Pageable pageable) {
        return invoiceTemplateRepository.findAll(pageable);
    }


    /**
     * Get from DB or return default one
     *
     * @param invoiceTemplateCategory
     * @return
     */
    public InvoiceTemplateViewModel getOne(InvoiceTemplateCategory invoiceTemplateCategory) {

        final InvoiceTemplate invoiceTemplate = getInvoiceTemplateFromDbOrDefaultByCategory(invoiceTemplateCategory, false);
        final InvoiceTemplateViewModel invoiceTemplateDto = invoiceTemplateMapper.toViewModel(invoiceTemplate);

        return invoiceTemplateDto;
    }

    /**
     * Delete any exisiting record in DB and return default template
     *
     * @param invoiceTemplateCategory
     * @return
     */
    public InvoiceTemplateViewModel resetReportTemplateToDefault(InvoiceTemplateCategory invoiceTemplateCategory) {

    	//delete a record from DB by category
    	invoiceTemplateRepository.removeByInvoiceCategory(invoiceTemplateCategory.name());

        return getOne(invoiceTemplateCategory); //becuase no record in DB, a default will be returned
    }

    public void downloadInvoiceTemplate(final InvoiceTemplateCategory invoiceTemplateCategory, HttpServletResponse response) throws IOException {
        final InvoiceTemplate template = getInvoiceTemplateFromDbOrDefaultByCategory(invoiceTemplateCategory, true);

        try (ByteArrayInputStream is = new ByteArrayInputStream(template.getTemplateDocument())) {
            response.addHeader("Content-disposition", "attachment;filename=\"" + template.getInvoiceFilename() + '\"');
            response.setContentType(template.getMimeType());
            IOUtils.copy(is, response.getOutputStream());
            response.flushBuffer();
        } catch (IOException ioe) {
            log.error("Cannot download the template with ID {}", invoiceTemplateCategory);
            throw ioe;
        }
    }

    public void downloadInvoiceTemplateExampleXml(final InvoiceTemplateCategory invoiceTemplateCategory, HttpServletResponse response) throws IOException {

    	byte[] invoiceTemplateExampleXml = reportHelper.getInvoiceTemplateExampleXmlFile(invoiceTemplateCategory);

        try (ByteArrayInputStream is = new ByteArrayInputStream(invoiceTemplateExampleXml)) {

            response.addHeader("Content-disposition", "attachment;filename=\"" + invoiceTemplateCategory.name() + ".example.xml" + '\"');
            response.setContentType("application/xml");

            IOUtils.copy(is, response.getOutputStream());

            response.flushBuffer();
        } catch (IOException ioe) {
            log.error("Cannot download the template example xml file for category {}", invoiceTemplateCategory);
            throw ioe;
        }
    }

    public InvoiceTemplate uploadAndCreateOrUpdate(final InvoiceTemplateCategory invoiceTemplateCategory, final MultipartFile rawTemplateFile, final String name)
        throws IOException {

        if (invoiceTemplateCategory==null) {
        	throw new CustomParametrizedException("Report template category can't be empty", "category");
        }

        InvoiceTemplate existingTemplate = invoiceTemplateRepository.findInvoiceTemplateByCategory(invoiceTemplateCategory.name());

        if (existingTemplate==null) {
        	existingTemplate = new InvoiceTemplate();
        }

        doValidateReportTemplateFile(invoiceTemplateCategory,rawTemplateFile); //validate template file

        //if template file is valid then proceed
        existingTemplate.setTemplateDocument(rawTemplateFile.getBytes());
        existingTemplate.setInvoiceFilename(rawTemplateFile.getOriginalFilename());
        existingTemplate.setMimeType(rawTemplateFile.getContentType());

        if (name != null) {
            existingTemplate.setInvoiceTemplateName(name);
        }
         existingTemplate.setInvoiceCategory(invoiceTemplateCategory.name());

        return invoiceTemplateRepository.saveAndFlush(existingTemplate);
    }

    /**
     * Validate report template file
     *
     * @param invoiceTemplateCategory
     * @param rawTemplateFile
     * @throws IOException
     */
    private void doValidateReportTemplateFile(final InvoiceTemplateCategory invoiceTemplateCategory, final MultipartFile rawTemplateFile) throws IOException {
        if (rawTemplateFile != null && rawTemplateFile.getBytes().length > 0) {
            final byte[] fileBytes = rawTemplateFile.getBytes();

            	//validate template file
            	//validate file extension
            	if (rawTemplateFile.getOriginalFilename()==null || rawTemplateFile.getOriginalFilename().isEmpty()) {
            		throw new CustomParametrizedException("Report template file name can't be empty", "file");
            	}

            	String templateFileExtension =  FilenameUtils.getExtension(rawTemplateFile.getOriginalFilename());
            	if (templateFileExtension==null || templateFileExtension.isEmpty() || !templateFileExtension.equals("rptdesign")) {
            		throw new CustomParametrizedException("Report template file extension should be .rptdesign", "file");
            	}

            	//validate required element
            	String templateValidationRequiredStr = String.format("<text-property name=\"title\" key=\"%s\"></text-property>",invoiceTemplateCategory.name());
            	String templateFileContent = new String(fileBytes,"UTF-8");
            	Pattern p = Pattern.compile(templateValidationRequiredStr);
            	Matcher m = p.matcher(templateFileContent);
            	if (!m.find()) {
            		throw new CustomParametrizedException(
                        String.format(
                            "Invalid report template file for category" + " %s", invoiceTemplateCategory.name()), "file");
            	}

        } else {
        	throw new CustomParametrizedException("Report template file can't be empty", "file");
        }
    }

    public long countAll() {
        return invoiceTemplateRepository.count();
    }
}
