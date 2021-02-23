package ca.ids.abms.modules.certificates.templates;

import java.io.IOException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import ca.ids.abms.modules.util.models.ModelUtils;

@Service
@Transactional
public class CertificateTemplateService {

    private CertificateTemplateRepository certificateTemplateRepository;

    public CertificateTemplateService (CertificateTemplateRepository certificateTemplateRepository) {
        this.certificateTemplateRepository = certificateTemplateRepository;
    }

    @Transactional(readOnly = true)
    public CertificateTemplate getOne (final Integer id) {
        return certificateTemplateRepository.getOne(id);
    }

    @Transactional(readOnly = true)
    public Page<CertificateTemplate> findAll (final Pageable pageable) {
        return certificateTemplateRepository.findAll(pageable);
    }

    public CertificateTemplate upload (final Integer id, final MultipartFile template)  throws IOException {
        final CertificateTemplate certificateTemplate = certificateTemplateRepository.getOne(id);
        final byte[] file = template.getBytes();
        certificateTemplate.setTemplateDocument(file);
        certificateTemplate.setTemplateDocumentType(template.getContentType());
        return certificateTemplateRepository.saveAndFlush(certificateTemplate);
    }

    public CertificateTemplate create (final CertificateTemplate certificateTemplate) {
        return certificateTemplateRepository.saveAndFlush(certificateTemplate);
    }

    public CertificateTemplate update (final Integer id, final CertificateTemplate item) {
        final CertificateTemplate existingItem = certificateTemplateRepository.getOne(id);
        ModelUtils.merge(item, existingItem, "id", "templateDocument", "createdAt", "createdBy",
            "updatedAt", "updatedBy");
        return certificateTemplateRepository.saveAndFlush(existingItem);
    }

    public void delete(final Integer id) {
        certificateTemplateRepository.delete(id);
    }
}
