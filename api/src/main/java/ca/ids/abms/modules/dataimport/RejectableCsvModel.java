package ca.ids.abms.modules.dataimport;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.opencsv.bean.CsvBindByPosition;
import com.opencsv.bean.CsvCustomBindByPosition;

import ca.ids.abms.config.error.ErrorDTO;
import ca.ids.abms.modules.common.mappers.UseCustomDataMapper;

public interface RejectableCsvModel {

    void setRawText(String rawText);

    @JsonIgnore
    String getRawText();

    void setParsed(boolean parsed);

    @JsonIgnore
    boolean isParsed();

    void setLine(long line);

    @JsonIgnore
    long getLine();

    @JsonIgnore
    ErrorDTO getErrorMessage();

    void setErrorMessage(ErrorDTO errorMessage);

    static String getHeader(Class<? extends RejectableCsvModel> cls) {
        String header = null;
        if (cls.isAnnotationPresent(UseCustomDataMapper.class)) {
            if (cls.isAnnotationPresent(UseCustomDataMapper.class)) {
                final UseCustomDataMapper annotation = cls.getAnnotation(UseCustomDataMapper.class);
                header = annotation.header();
            }
        } else {
            final Map<Integer, String> columns = new HashMap<>();
            int maxPos = 0;
            for (Field field : cls.getDeclaredFields()) {
                if (field.isAnnotationPresent(CsvCustomBindByPosition.class)) {
                    CsvCustomBindByPosition binding = field.getAnnotation(CsvCustomBindByPosition.class);
                    if (binding.position() > maxPos) {
                        maxPos = binding.position();
                    }
                    columns.put(binding.position(), field.getName());
                } else if (field.isAnnotationPresent(CsvBindByPosition.class)) {
                    CsvBindByPosition binding = field.getAnnotation(CsvBindByPosition.class);
                    if (binding.position() > maxPos) {
                        maxPos = binding.position();
                    }
                    columns.put(binding.position(), field.getName());
                }
            }
            final StringBuilder headerBuiler = new StringBuilder();
            for (int i = 0; i <= maxPos; i++) {
                final String columnName = columns.get(i);
                if (columnName != null) {
                    headerBuiler.append(columnName);
                }
                if (i < maxPos) {
                    headerBuiler.append(',');
                }
            }
            header = headerBuiler.toString();
        }
        return header;
    }
}
