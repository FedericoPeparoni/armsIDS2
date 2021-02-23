package ca.ids.abms.modules.languages;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LanguagesRepository extends JpaRepository<Languages, Integer> {

    @Query(
        value = "SELECT l FROM Languages l WHERE l.code = :lang"
    )
    List<Languages> findAllByLang(@Param("lang") String lang);

    @Query(
        value = "SELECT l FROM Languages l WHERE l.code = :lang AND l.part = 'frontend'"
    )
    List<Languages> getLanguageTagsByLang(@Param("lang") String lang);

    @Query(
        value = "SELECT l FROM Languages l WHERE l.code = :lang AND l.part = 'backend'"
    )
    List<Languages> getLanguageForBackend(@Param("lang") String lang);

}
