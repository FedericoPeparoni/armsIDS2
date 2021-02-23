package ca.ids.abms.modules.common.mappers;

import ca.ids.abms.modules.dataimport.RejectableCsvModel;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

public interface CustomDataMapper<T extends RejectableCsvModel> {

    T convertStringArrayIntoCsvModel (String[] record) throws CsvRequiredFieldEmptyException,
        CsvConstraintViolationException, CsvDataTypeMismatchException;

}
