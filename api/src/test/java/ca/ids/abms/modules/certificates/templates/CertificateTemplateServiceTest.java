package ca.ids.abms.modules.certificates.templates;

import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class CertificateTemplateServiceTest {

    private CertificateTemplateRepository certificateTemplateRepository;
    private CertificateTemplateService certificateTemplateService;

    @Before
    public void setup() {
        this.certificateTemplateRepository = mock(CertificateTemplateRepository.class);
        this.certificateTemplateService = new CertificateTemplateService(certificateTemplateRepository);
    }

    @Test
    public void create() throws Exception {
        CertificateTemplate item = new CertificateTemplate();
        item.setCertificateTemplateName("template");

        when(certificateTemplateRepository.saveAndFlush(any(CertificateTemplate.class))).thenReturn(item);

        CertificateTemplate result = certificateTemplateService.create(item);
        assertThat(result.getCertificateTemplateName().equals(item.getCertificateTemplateName()));
    }

    @Test
    public void update() throws Exception {
        CertificateTemplate item = new CertificateTemplate();
        item.setId(1);
        item.setCertificateTemplateName("template");

        when(certificateTemplateRepository.getOne(any(Integer.class))).thenReturn(item);

        CertificateTemplate itemUpdated = new CertificateTemplate();
        itemUpdated.setId(1);
        itemUpdated.setCertificateTemplateName("changed");

        when(certificateTemplateRepository.saveAndFlush(any(CertificateTemplate.class))).thenReturn(itemUpdated);

        CertificateTemplate result = certificateTemplateService.update(1, item);
        assertThat(result.getId() == 1);
        assertThat(result.getCertificateTemplateName().equals(itemUpdated.getCertificateTemplateName()));
    }

    @Test
    public void findAll() throws Exception {
        CertificateTemplate item = new CertificateTemplate();
        item.setId(1);
        item.setCertificateTemplateName("template");

        List<CertificateTemplate> items = Collections.singletonList(item);

        when(certificateTemplateRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(items));

        Page<CertificateTemplate> results = certificateTemplateService.findAll(mock(Pageable.class));
        assertThat(items.size() == results.getTotalElements());
        assertThat(items.get(0).getId() == results.getContent().get(0).getId());
        assertThat(items.get(0).getCertificateTemplateName() == results.getContent().get(0).getCertificateTemplateName());
    }

    @Test
    public void getOne() throws  Exception {
        CertificateTemplate item = new CertificateTemplate();
        item.setId(1);
        item.setCertificateTemplateName("template");

        when(certificateTemplateRepository.getOne(any())).thenReturn(item);
        CertificateTemplate result = certificateTemplateService.getOne(1);
        assertThat(result.getId() == item.getId());
        assertThat(result.getCertificateTemplateName().equals(item.getCertificateTemplateName()));
    }

    @Test
    public void delete() throws Exception {
        CertificateTemplate item = new CertificateTemplate();
        item.setId(1);
        when(certificateTemplateRepository.getOne(any())).thenReturn(item);
        certificateTemplateService.delete(1);
        verify(certificateTemplateRepository).delete(any(Integer.class));
    }
}
