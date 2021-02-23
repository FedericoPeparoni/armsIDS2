package ca.ids.abms.modules.languages;

import java.util.List;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LanguagesService {

    private final Logger log = LoggerFactory.getLogger(LanguagesService.class);

    private LanguagesRepository languagesRepository;

    public LanguagesService(LanguagesRepository languagesRepository) {
        this.languagesRepository = languagesRepository;
    }

    @Transactional(readOnly = true)
    public Page<Languages> findAll(Pageable pageable) {
        log.debug("Request to get languages");
        return languagesRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public List<Languages> findAllByLang(String lang) {
        log.debug("Request to get languages");
        return languagesRepository.findAllByLang(lang);
    }

    public Languages update(Integer id, Languages lang) {
        log.debug("Request to update language tag : {}", lang);
        Languages existingLanguages = languagesRepository.getOne(id);

        return languagesRepository.save(existingLanguages);
    }

    public Languages create(Languages country) {
        log.debug("Request to create Languages : {}", country);

        return languagesRepository.save(country);
    }

    public void delete(Integer id) {
        log.debug("Request to delete Language: {}", id);

        languagesRepository.delete(id);
    }

    public String getLanguageTagsByLang(String lang) {
        List<Languages> data = languagesRepository.getLanguageTagsByLang(lang);
        String jsonString = createJSONString(data);

        return jsonString;
    }

    public String getLanguageForBackend(String lang) {
        List<Languages> data = languagesRepository.getLanguageForBackend(lang);
        String jsonString = createJSONString(data);

        return jsonString;
    }

    private String createJSONString(List<Languages> data) {
        JSONObject obj = new JSONObject();

        for (Languages row : data) {
            obj.put(row.getToken(), row.getVal());
        }

        return obj.toJSONString();
    }

}
