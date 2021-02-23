package ca.ids.abms.modules.languages;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/languages")
public class LanguagesController {

    private final Logger log = LoggerFactory.getLogger(LanguagesController.class);
    private final LanguagesService languagesService;

    public LanguagesController(LanguagesService languagesService) {
        this.languagesService = languagesService;
    }

    /**
     * Get all tags for selected language
     */
    private List<Languages> findAllByProvidedLang(String lang) {
        return languagesService.findAllByLang(lang);
    }

    /**
     * Get all language fields
     */
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<Page<Languages>> findAll(@SortDefault(sort = {"token"}, direction = Sort.Direction.ASC) Pageable pageable) {
        log.debug("REST request to get all language tags");

        return ResponseEntity.ok().body(languagesService.findAll(pageable));
    }

    /**
     * Get single field/language from language table
     */
    @RequestMapping(value = "/{lang}", method = RequestMethod.GET)
    public ResponseEntity<List<Languages>> findAllByLang(@SortDefault(sort = {"token"}, direction = Sort.Direction.ASC) String lang) {
        log.debug("REST request to get all Languages");

        return ResponseEntity.ok().body(findAllByProvidedLang(lang));
    }

    /**
     * Update single language tag
     */
    @PreAuthorize("hasAuthority('language_modify')")
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<Languages> updateLanguageTag(@RequestBody Languages languagesDto, @PathVariable Integer id) {
        log.debug("REST request to update Languages: {}", languagesDto);

        final Languages languages = languagesDto;
        Languages result = languagesService.update(id, languages);
        final Languages resultDto = result;

        return ResponseEntity.ok().body(resultDto);
    }

    /**
     * Create new language tag
     */
    @PreAuthorize("hasAuthority('language_modify')")
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Languages> createLanguageTag(@Valid @RequestBody Languages languagesDto) throws URISyntaxException {
        log.debug("REST request to create Languages : {}", languagesDto);

        final Languages languages = languagesDto;
        Languages result = languagesService.create(languages);
        final Languages resultDto = result;

        return ResponseEntity.created(new URI("/api/languages-management/" + resultDto.getId())).body(resultDto);
    }

    /**
     * Delete single language tag
     */
    @PreAuthorize("hasAuthority('language_modify')")
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteLanguageTag(@PathVariable Integer id) {
        log.debug("REST request to delete Languages : {}", id);

        languagesService.delete(id);

        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/json", method = RequestMethod.GET)
    public ResponseEntity<String> createLanguageFileForPart(@RequestParam(required = true) String lang) {
        log.debug("REST request to create language files for language : {}", lang);

        String langs = languagesService.getLanguageTagsByLang(lang);

        return ResponseEntity.ok().body(langs);
    }


}
