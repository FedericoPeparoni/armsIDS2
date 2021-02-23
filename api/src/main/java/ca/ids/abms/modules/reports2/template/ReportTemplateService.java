package ca.ids.abms.modules.reports2.template;

import java.io.IOException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
public class ReportTemplateService {

    private ReportTemplateRepository reportTemplateRepository;

    public ReportTemplateService(ReportTemplateRepository anReportTemplateRepository) {
        reportTemplateRepository = anReportTemplateRepository;
    }

    public ReportTemplate create(ReportTemplate item) {
        return reportTemplateRepository.save(item);
    }

    public void delete(Integer id) {
        reportTemplateRepository.delete(id);
    }

    @Transactional(readOnly = true)
    public Page<ReportTemplate> findAll(Pageable pageable) {
        return reportTemplateRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public ReportTemplate getOne(Integer id) {
        return reportTemplateRepository.getOne(id);
    }

    public ReportTemplate uploadAndCreate(final MultipartFile rawTemplate, final String name, final String sqlQuery, final String parameters)
        throws IOException {
        assert (rawTemplate != null);
        final ReportTemplate reportTemplate = new ReportTemplate();
        final byte[] file = rawTemplate.getBytes();
        reportTemplate.setTemplateDocument(file);
        reportTemplate.setReportTemplateName(name);
        reportTemplate.setSqlQuery(sqlQuery);
        reportTemplate.setParameters(parameters);
        reportTemplate.setReportFilename(rawTemplate.getOriginalFilename());
        reportTemplate.setMimeType(rawTemplate.getContentType());
        return reportTemplateRepository.saveAndFlush(reportTemplate);
    }

    public ReportTemplate uploadAndUpdate(final Integer id, final MultipartFile rawTemplate, final String name, final String sqlQuery, final String parameters)
        throws IOException {
        assert (id != null);
        final ReportTemplate existingTemplate = reportTemplateRepository.getOne(id);
        if (rawTemplate != null) {
            final byte[] file = rawTemplate.getBytes();
            if (file != null && file.length > 0) {
                existingTemplate.setTemplateDocument(file);
                existingTemplate.setReportFilename(rawTemplate.getOriginalFilename());
                existingTemplate.setMimeType(rawTemplate.getContentType());
            }
        }
        if (name != null) {
            existingTemplate.setReportTemplateName(name);
        }
        if (sqlQuery != null) {
            existingTemplate.setSqlQuery(sqlQuery);
        }
        if (parameters != null) {
            existingTemplate.setParameters(parameters);
        }
        return reportTemplateRepository.saveAndFlush(existingTemplate);
    }

    public long countAll() {
        return reportTemplateRepository.count();
    }
}
