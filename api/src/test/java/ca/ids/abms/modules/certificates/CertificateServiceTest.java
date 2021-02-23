package ca.ids.abms.modules.certificates;

import ca.ids.abms.modules.certificates.templates.CertificateTemplateService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.*;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Created by c.talpa on 14/12/2016.
 */
public class CertificateServiceTest {

    private CertificateRepository certificateRepository;
    private CertificateTemplateService certificateTemplateService;
    private CertificateService certificateService;

    @Before
    public void setup() {
        this.certificateRepository = mock(CertificateRepository.class);
        this.certificateTemplateService=mock(CertificateTemplateService.class);
        this.certificateService = new CertificateService(certificateRepository);
    }

    @Test
    public void create() throws Exception {
        Certificate item = new Certificate();
        item.setCertifiedOrganizationOrPerson("Organization");

        when(certificateRepository.saveAndFlush(any(Certificate.class))).thenReturn(item);

        Certificate result = certificateService.create(item);
        assertThat(result.getCertifiedOrganizationOrPerson().equals(item.getCertifiedOrganizationOrPerson()));
    }

    @Test
    public void update() throws Exception {
        Certificate item = new Certificate();
        item.setId(1);
        item.setCertifiedOrganizationOrPerson("Organization");

        when(certificateRepository.getOne(any(Integer.class))).thenReturn(item);

        Certificate itemUpdated = new Certificate();
        itemUpdated.setId(1);
        itemUpdated.setCertifiedOrganizationOrPerson("person");

        when(certificateRepository.saveAndFlush(any(Certificate.class))).thenReturn(itemUpdated);

        Certificate result = certificateService.update(1, item);
        assertThat(result.getId() == 1);
        assertThat(result.getCertifiedOrganizationOrPerson().equals(itemUpdated.getCertifiedOrganizationOrPerson()));
    }

    @Test
    public void findAll() throws Exception {
        Certificate item = new Certificate();
        item.setId(1);
        item.setCertifiedOrganizationOrPerson("person");

        List<Certificate> items = Collections.singletonList(item);

        when(certificateRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(items));

        final Sort sortingOpts = new Sort(new Sort.Order(Sort.Direction.DESC, "dateOfIssue"));
        Pageable pageable = new PageRequest(1, 20, sortingOpts);
        Page<Certificate> results = certificateService.findAll(pageable,"no-filter");
        assertThat(items.size() == results.getTotalElements());
        assertThat(items.get(0).getId() == results.getContent().get(0).getId());
        assertThat(items.get(0).getCertifiedOrganizationOrPerson() == results.getContent().get(0).getCertifiedOrganizationOrPerson());
    }

    @Test
    public void findOne() throws Exception {
        Certificate item = new Certificate();
        item.setId(1);
        item.setCertifiedOrganizationOrPerson("template");

        when(certificateRepository.findOne(1)).thenReturn(item);
        Certificate result = certificateService.findOne(1);
        assertThat(result.getId() == item.getId());
        assertThat(result.getCertifiedOrganizationOrPerson().equals(item.getCertifiedOrganizationOrPerson()));
    }

    @Test
    public void delete() throws Exception {
        Certificate item = new Certificate();
        item.setId(1);
        when(certificateRepository.getOne(any())).thenReturn(item);
        certificateService.delete(1);
        verify(certificateRepository).delete(any(Integer.class));
    }

}
