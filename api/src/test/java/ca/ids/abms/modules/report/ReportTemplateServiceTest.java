package ca.ids.abms.modules.report;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import ca.ids.abms.modules.reports2.template.ReportTemplate;
import ca.ids.abms.modules.reports2.template.ReportTemplateRepository;
import ca.ids.abms.modules.reports2.template.ReportTemplateService;

public class ReportTemplateServiceTest {
    
    private ReportTemplateRepository reportTemplateRepository;
    private ReportTemplateService reportTemplateService;

    @Test
    public void createReportTemplate() throws Exception {
        ReportTemplate reportTemplate = new ReportTemplate();
        reportTemplate.setSqlQuery("sqlquery");
        when(reportTemplateRepository.save(any(ReportTemplate.class))).thenReturn(reportTemplate);

        ReportTemplate result = reportTemplateService.create(reportTemplate);
        assertThat(result.getSqlQuery()).isEqualTo(reportTemplate.getSqlQuery());
    }

    @Test
    public void deleteUser() throws Exception {
        reportTemplateService.delete(1);
        verify(reportTemplateRepository).delete(any(Integer.class));
    }

    @Test
    public void getAllReportTemplates() throws Exception {
        List<ReportTemplate> reportTemplates = Collections.singletonList(new ReportTemplate());

        when(reportTemplateRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(reportTemplates));

        Page<ReportTemplate> results = reportTemplateService.findAll(mock(Pageable.class));

        assertThat(results.getTotalElements()).isEqualTo(reportTemplates.size());
    }

    @Test
    public void getReportTemplateById() throws Exception {
        ReportTemplate reportTemplate = new ReportTemplate();
        reportTemplate.setId(1);

        when(reportTemplateRepository.getOne(any())).thenReturn(reportTemplate);

        ReportTemplate result = reportTemplateService.getOne(1);
        assertThat(result).isEqualTo(reportTemplate);
    }

    @Before
    public void setup() {
        reportTemplateRepository = mock(ReportTemplateRepository.class);
        reportTemplateService = new ReportTemplateService(reportTemplateRepository);
    }

    @Test
    public void updateReportTemplate() throws Exception {
        ReportTemplate existingReportTemplate = new ReportTemplate();
        existingReportTemplate.setSqlQuery("sqlquery");

        MultipartFile file = mock(MultipartFile.class);
        
        ReportTemplate reportTemplate = new ReportTemplate();
        reportTemplate.setSqlQuery("new_sqlquery");

        when(reportTemplateRepository.getOne(any())).thenReturn(existingReportTemplate);

        when(reportTemplateRepository.saveAndFlush(any(ReportTemplate.class))).thenReturn(existingReportTemplate);

        ReportTemplate result = reportTemplateService.uploadAndUpdate(1, file, "", "new_sqlquery", "");

        assertThat(result.getSqlQuery()).isEqualTo("new_sqlquery");
    }
}
