package ca.ids.abms.modules.certificates.templates;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.poi.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import ca.ids.abms.config.AttachmentsConfig;
import ca.ids.abms.config.error.CustomParametrizedException;
import ca.ids.abms.config.error.ErrorConstants;
import ca.ids.abms.config.error.ExceptionFactory;

@RestController
@RequestMapping("/api/certificate-templates")
public class CertificateTemplatesController {

    private final Logger log = LoggerFactory.getLogger(CertificateTemplatesController.class);

    private CertificateTemplateService certificateTemplateService;
    private CertificateTemplateMapper certificateTemplateMapper;
    private AttachmentsConfig attachmentsConfig;

    public CertificateTemplatesController (CertificateTemplateService certificateTemplateService,
                                           CertificateTemplateMapper certificateTemplateMapper,
                                           AttachmentsConfig attachmentsConfig) {
        this.certificateTemplateMapper = certificateTemplateMapper;
        this.certificateTemplateService = certificateTemplateService;
        this.attachmentsConfig = attachmentsConfig;
    }

    @GetMapping
    public ResponseEntity<Page<CertificateTemplateViewModel>> findAll (Pageable pageable) {
        final Page<CertificateTemplate> page = certificateTemplateService.findAll(pageable);
        final Page<CertificateTemplateViewModel> result = new PageImpl<>(
            certificateTemplateMapper.toViewModel(page), pageable, page.getTotalElements()
        );
        return ResponseEntity.ok().body(result);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<CertificateTemplateViewModel> getOne(@PathVariable Integer id) {
        final CertificateTemplate item = certificateTemplateService.getOne(id);
        final CertificateTemplateViewModel dto = certificateTemplateMapper.toViewModel(item);
        return Optional.ofNullable(dto)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PreAuthorize("hasAuthority('certificate_template_modify')")
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<CertificateTemplateViewModel> create(
        @Valid @RequestBody CertificateTemplateViewModel dto) throws URISyntaxException {
        if (dto.getId() != null) {
            throw new CustomParametrizedException(ErrorConstants.ERR_ID_MAY_BE_NULL, "id");
        }
        final CertificateTemplate itemToCreate = certificateTemplateMapper.toModel(dto);
        final CertificateTemplate createdItem = certificateTemplateService.create(itemToCreate);
        final CertificateTemplateViewModel resultDto = certificateTemplateMapper.toViewModel(createdItem);
        return ResponseEntity.created(new URI("/api/certificate-templates/" + createdItem.getId()))
            .body(resultDto);
    }

    @PreAuthorize("hasAuthority('certificate_template_modify')")
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<CertificateTemplateViewModel> update(@PathVariable Integer id,
                                                               @RequestBody CertificateTemplateViewModel dto) {
        final CertificateTemplate itemToUpdate = certificateTemplateMapper.toModel(dto);
        final CertificateTemplate updatedItem = certificateTemplateService.update(id, itemToUpdate);
        final CertificateTemplateViewModel updatedDto = certificateTemplateMapper.toViewModel(updatedItem);
        return ResponseEntity.ok().body(updatedDto);
    }

    @PreAuthorize("hasAuthority('certificate_template_modify')")
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        certificateTemplateService.delete(id);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAuthority('certificate_template_modify')")
    @RequestMapping(value = "/{id}/upload", headers = ("content-type=multipart/*"), method = RequestMethod.PUT)
    public ResponseEntity<CertificateTemplateViewModel> upload (@PathVariable final Integer id,
                                                                @RequestParam("file") final MultipartFile file)
        throws URISyntaxException, IOException
    {
        if (file.getContentType() == null
            || !attachmentsConfig.getCertificateTemplates().containsKey(file.getContentType())) {
            throw ExceptionFactory.getInvalidFileException(CertificateTemplate.class);
        }
        final CertificateTemplate template = certificateTemplateService.upload(id, file);
        return ResponseEntity.created(new URI("/api/certificate-templates/" + template.getId() + "/document")).body(
            certificateTemplateMapper.toViewModel(template));
    }

    @RequestMapping(value = "/{id}/download", method = RequestMethod.GET)
    public void download (@PathVariable final Integer id, HttpServletResponse response) throws IOException {
        final CertificateTemplate template = certificateTemplateService.getOne(id);
        try (ByteArrayInputStream is = new ByteArrayInputStream(template.getTemplateDocument())) {
            response.addHeader("Content-disposition", "attachment;filename=" + template.getCertificateTemplateName() +
                attachmentsConfig.getCertificateTemplates().get(template.getTemplateDocumentType()));
            response.setContentType(template.getTemplateDocumentType());
            IOUtils.copy(is, response.getOutputStream());
            response.flushBuffer();
        } catch (IOException ioe) {
            log.error("Cannot download the template with ID {}", id);
            throw ioe;
        }
    }

    @Deprecated
    @RequestMapping(value = "/{id}/document/json", method = RequestMethod.GET)
    public ResponseEntity<CertificateTemplateViewModel> downloadEmbedded (@PathVariable final Integer id) throws IOException {
        final CertificateTemplate item = certificateTemplateService.getOne(id);
        final CertificateTemplateViewModel result = certificateTemplateMapper.toViewModel(item);
        result.setDocumentContents(item.getTemplateDocument());
        return ResponseEntity.ok().body(result);

    }
}
