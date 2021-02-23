package ca.ids.abms.modules.certificates;

import ca.ids.abms.config.error.CustomParametrizedException;
import ca.ids.abms.config.error.ErrorConstants;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

/**
 * Created by c.talpa on 14/12/2016.
 */
@RestController
@RequestMapping("/api/certificates")
public class CertificateController {

    private static final Logger LOG = LoggerFactory.getLogger(CertificateController.class);

    private CertificateService certificateService;

    private CertificateMapper certificateMapper;

    public CertificateController(CertificateService certificateService, CertificateMapper certificateMapper){
        this.certificateService=certificateService;
        this.certificateMapper=certificateMapper;
    }

    @GetMapping
    public ResponseEntity<Page<CertificateViewModel>> findAll (
        @RequestParam(name ="filter", required = false) String filter,
        @SortDefault(sort = {"dateOfIssue"}, direction = Sort.Direction.DESC) Pageable pageable) {
        final Page<Certificate> page = certificateService.findAll(pageable, filter);
        final Page<CertificateViewModel> result = new PageImpl<>(
            certificateMapper.toViewModel(page), pageable, page.getTotalElements()
        );
        return ResponseEntity.ok().body(result);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<CertificateViewModel> findOne(@PathVariable Integer id) {
        final Certificate item = certificateService.findOne(id);
        final CertificateViewModel dto = certificateMapper.toViewModel(item);
        return Optional.ofNullable(dto).map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PreAuthorize("hasAuthority('certificates_modify')")
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        certificateService.delete(id);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAuthority('certificates_modify')")
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<CertificateViewModel> create(@Valid @RequestBody CertificateViewModel dto) throws URISyntaxException {
        if (dto.getId() != null) {
            throw new CustomParametrizedException(ErrorConstants.ERR_ID_MAY_BE_NULL, "id");
        }
        final Certificate itemToCreate = certificateMapper.toModel(dto);
        final Certificate createdItem = certificateService.create(itemToCreate);
        final CertificateViewModel resultDto = certificateMapper.toViewModel(createdItem);
        return ResponseEntity.created(new URI("/api/certificate/" + createdItem.getId()))
            .body(resultDto);
    }

    @PreAuthorize("hasAuthority('certificates_modify')")
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<CertificateViewModel> update(@PathVariable Integer id,
                                                               @RequestBody CertificateViewModel dto) {
        final Certificate itemToUpdate = certificateMapper.toModel(dto);
        final Certificate updatedItem = certificateService.update(id, itemToUpdate);
        final CertificateViewModel updatedDto = certificateMapper.toViewModel(updatedItem);
        return ResponseEntity.ok().body(updatedDto);
    }

    @PreAuthorize("hasAuthority('certificates_modify')")
    @RequestMapping(value = "/{id}/upload", headers = ("content-type=multipart/*"), method = RequestMethod.PUT)
    public ResponseEntity<CertificateViewModel> uploadImage (@PathVariable final Integer id, @RequestParam("file") final MultipartFile file)
        throws URISyntaxException, IOException {
        if (file.getContentType() == null || !file.getContentType().startsWith("image/")) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Certificate certificate;
        try {
            certificate = certificateService.uploadImage(id, file);
        } catch (IOException ioe) {
            LOG.error("Cannot upload the image for the passenger manifest with ID {}", id);
            throw ioe;
        }
        return ResponseEntity.created(new URI("/api/passenger-manifests/" + certificate.getId()+ "/image"))
            .body(certificateMapper.toViewModel(certificate));
    }

    @RequestMapping(value = "/{id}/download", method = RequestMethod.GET)
    public void downloadImage (@PathVariable final Integer id, final HttpServletResponse response) throws IOException {
        final Certificate certificate = certificateService.findOne(id);
        try (ByteArrayInputStream is = new ByteArrayInputStream(certificate.getCertificateImage())) {
            response.addHeader("Content-disposition", "attachment;filename=" + certificate.getId());
            response.setContentType(certificate.getCertificateImageType());
            IOUtils.copy(is, response.getOutputStream());
            response.flushBuffer();
        } catch (IOException ioe) {
            LOG.error("Cannot download the image for passenger manifest with ID {}", id);
            throw ioe;
        }
    }
}
