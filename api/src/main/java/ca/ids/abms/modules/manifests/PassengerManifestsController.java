package ca.ids.abms.modules.manifests;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.data.web.SortDefault.SortDefaults;
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

import ca.ids.abms.config.error.CustomParametrizedException;
import ca.ids.abms.config.error.ErrorConstants;
import ca.ids.abms.modules.common.controllers.BulkLoaderComponent;
import ca.ids.abms.modules.common.dto.BulkLoaderSummary;
import ca.ids.abms.modules.dataimport.CsvImportServiceImp;
import ca.ids.abms.modules.dataimport.DataImportService;

@RestController
@RequestMapping("/api/passenger-manifests")
public class PassengerManifestsController implements BulkLoaderComponent {

    private final Logger log = LoggerFactory.getLogger(PassengerManifestsController.class);

    private PassengerManifestService passengerManifestService;
    private PassengerManifestMapper passengerManifestMapper;

    @Autowired
    @Qualifier(DATA_IMPORT_SERVICE)
    private CsvImportServiceImp<PassengerManifestCsvViewModel> dataImportService;

    @Qualifier(PASSENGER_MANIFEST_LOADER)
    private PassengerManifestLoader loaderService;

    public PassengerManifestsController(PassengerManifestService passengerManifestService,
                                        PassengerManifestMapper passengerManifestMapper,
                                        PassengerManifestLoader loaderService) {
        this.passengerManifestService = passengerManifestService;
        this.passengerManifestMapper = passengerManifestMapper;
        this.loaderService = loaderService;
    }

    @GetMapping
    public ResponseEntity<Page<PassengerManifestViewModel>> findAll (
        @RequestParam(value = OrphanPassengerManifestFilter.QUERY_STRING_ATTRIBUTE, required = false) String value,
        @SortDefaults({@SortDefault(sort = {"dateOfFlight"}, direction = Sort.Direction.DESC),
            @SortDefault(sort = {"flightId"}, direction = Sort.Direction.ASC)}
        ) Pageable pageable) {
        log.debug("REST request to get all passenger manifests, filter: {}", value);
        final Page<PassengerManifest> page = passengerManifestService.findAll(pageable, Boolean.parseBoolean(value));
        final Page<PassengerManifestViewModel> resultPage = new PageImpl<>(
            passengerManifestMapper.toViewModel(page), pageable, page.getTotalElements()
        );
        return ResponseEntity.ok().body(resultPage);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<PassengerManifestViewModel> getOne(@PathVariable Integer id) {
        final PassengerManifest item = passengerManifestService.getOne(id);
        final PassengerManifestViewModel dto = passengerManifestMapper.toViewModel(item);
        return Optional.ofNullable(dto)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PreAuthorize("hasAuthority('flight_log_modify')")
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<PassengerManifestViewModel> create(
        @RequestBody PassengerManifestViewModel dto) throws URISyntaxException {
        if (dto.getDocumentNumber() != null) {
            throw new CustomParametrizedException(ErrorConstants.ERR_ID_MAY_BE_NULL, "document_number");
        }
        final PassengerManifest itemToCreate = passengerManifestMapper.toModel(dto);
        final PassengerManifest createdItem = passengerManifestService.create(itemToCreate);
        final PassengerManifestViewModel resultDto = passengerManifestMapper.toViewModel(createdItem);
        return ResponseEntity.created(new URI("/api/passenger-manifests/" + createdItem.getDocumentNumber()))
            .body(resultDto);
    }

    @PreAuthorize("hasAuthority('flight_log_modify')")
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<PassengerManifestViewModel> update(@PathVariable Integer id,
                                                                    @RequestBody PassengerManifestViewModel dto){
        final PassengerManifest itemToUpdate = passengerManifestMapper.toModel(dto);
        final PassengerManifest updatedItem = passengerManifestService.update(id, itemToUpdate);
        final PassengerManifestViewModel updatedDto = passengerManifestMapper.toViewModel(updatedItem);
        return ResponseEntity.ok().body(updatedDto);
    }

    @PreAuthorize("hasAuthority('flight_log_modify')")
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        passengerManifestService.delete(id);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAuthority('flight_log_modify')")
    @RequestMapping(value = "/{id}/upload", headers = ("content-type=multipart/*"), method = RequestMethod.PUT)
    public ResponseEntity<PassengerManifestViewModel> uploadImage (@PathVariable final Integer id,
                                                              @RequestParam("file") final MultipartFile file)
        throws URISyntaxException, IOException {
        if (file.getContentType() == null || !file.getContentType().startsWith("image/")) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        PassengerManifest manifest;
        try {
            manifest = passengerManifestService.uploadImage(id, file);
        } catch (IOException ioe) {
            log.error("Cannot upload the image for the passenger manifest with ID {}", id);
            throw ioe;
        }
        return ResponseEntity.created(new URI("/api/passenger-manifests/" + manifest.getDocumentNumber()
            + "/image"))
            .body(passengerManifestMapper.toViewModel(manifest));
    }

     /**
     * Upload of a CSV file
     * @param file
     * @return
     */
    @PreAuthorize("hasAuthority('flight_log_modify')")
    @RequestMapping (method=RequestMethod.PUT, value="/upload", consumes = { "multipart/form-data" })
    public ResponseEntity <BulkLoaderSummary> upload (
        final @RequestParam("file") MultipartFile file, @RequestParam(required = false) Map<String, String> params
    ) {
        List<PassengerManifestCsvViewModel> mappingResult;
        try {
            mappingResult = dataImportService.parseFromMultipartFile(file,
                DataImportService.STRATEGY.BIND_BY_HEADER_NAME, PassengerManifestCsvViewModel.class);
        } catch (Exception ex) {
            log.error("Cannot read the file {} because: {}", file.getOriginalFilename(), ex.getMessage());
            throw new CustomParametrizedException("Invalid file", ex, file.getOriginalFilename());
        }
        final BulkLoaderSummary summary = loaderService.bulkLoad(mappingResult, file);
        return ResponseEntity.ok().body(summary);
    }

    @RequestMapping(value = "/{id}/download", method = RequestMethod.GET)
    public void downloadImage (@PathVariable final Integer id, final HttpServletResponse response) throws IOException {
        final PassengerManifest manifest = passengerManifestService.getOne(id);
        final byte[] image = manifest.getPassengerManifestImage();
        if (image != null) {
            try (ByteArrayInputStream is = new ByteArrayInputStream(image)) {
                response.addHeader("Content-disposition", "attachment;filename=" + manifest.getDocumentNumber());
                response.setContentType(manifest.getPassengerManifestImageType());
                IOUtils.copy(is, response.getOutputStream());
                response.flushBuffer();
            } catch (IOException ioe) {
                log.error("Cannot download the image for passenger manifest with ID {}", id);
                throw ioe;
            }
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}
