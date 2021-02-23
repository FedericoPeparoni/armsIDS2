package ca.ids.abms.modules.dataimport;

import java.io.File;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

/**
 * This interface describes generic file import
 *
 * @author i.protasov
 *
 * @param <T>
 */
public interface DataImportService<T> {

    enum STRATEGY {
        BIND_BY_HEADER_NAME,
        BIND_BY_POSITION
    }

    List<T> parseFromFile(File file, Class<T> targetClass);

    List<T> parseFromFile(File file, STRATEGY strategy, Class<T> targetClass);

    List<T> parseFromMultipartFile(MultipartFile file, Class<T> targetClass);

    List<T> parseFromMultipartFile(MultipartFile file, STRATEGY strategy, Class<T> targetClass);

}
