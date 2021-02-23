package ca.ids.abms.modules.dataimport;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import ca.ids.abms.util.converter.JSR310DateConverters;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class XLSXToCSVConverter {
    public static String APPLICATION_XLXS = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    private static String seperator = ",";
    public static String convert(InputStream fileInputStream) throws IOException {
        XSSFWorkbook wBook = new XSSFWorkbook(fileInputStream);
        XSSFSheet sheet = wBook.getSheetAt(0);
        StringBuffer data = new StringBuffer();
        Row row;
        Cell cell;
        Iterator<Row> rowIterator = sheet.iterator();
        while (rowIterator.hasNext()) {
            row = rowIterator.next();
            Iterator<Cell> cellIterator = row.cellIterator();
            while (cellIterator.hasNext()) {
                cell = cellIterator.next();
                switch (cell.getCellType()) {
                    case Cell.CELL_TYPE_BOOLEAN:
                            data.append(cell.getBooleanCellValue() + seperator);
                            break;
                    case Cell.CELL_TYPE_NUMERIC:
                            double numericValue = cell.getNumericCellValue();
                            if(HSSFDateUtil.isCellDateFormatted(cell)) {

                                /*
                                    If the date is 31-12-1899, we can assume this is a time only value.

                                    This is a limitation of Excel and POI so we should only use dates of
                                    01-01-1900 and up - https://stackoverflow.com/a/15747134/3502564.

                                    This is okay for ABMS as the first plane didn't fly until 12-17-1903.
                                    Even then, we can safely assume ABMS isn't going to invoice the Wright
                                    brothers anytime soon ;)

                                    Note: This does not handle date + time values... currently that
                                    functionality is not required.
                                 */

                                // Java Date object from numeric cell value
                                Date dateValue = HSSFDateUtil.getJavaDate(numericValue);

                                // if date is 31-12-1899, set formatter as time only
                                // else default to date only formatter
                                SimpleDateFormat formatter;
                                if (dateValue.before(new Date(-2208988800000L))
                                    && dateValue.after(new Date(-2209161600000L))) {
                                    formatter = new SimpleDateFormat(JSR310DateConverters.DEFAULT_PATTERN_TIME);
                                } else {

                                    /*
                                        Unfortunately, the expected date format is dependent on csv record mappers.

                                        Currently only ATC Movement Log allows XLSX file uploads and it expects
                                        the date format of d-MMM-yy where year is LAST.

                                        This will also work with Tower Movement Logs but NOT Radar Summary which
                                        expects a different format of yyMMdd where year is FIRST in their uploaded
                                        csv records.

                                        If Radar Summary XLSX file upload is added, this difference will have to
                                        be accounted for.
                                     */
                                    formatter = new SimpleDateFormat("d-MMM-yy");
                                }

                                // format as date or time depending on formatter setting above
                                data.append(formatter.format(dateValue) + seperator);

                            } else {
                                data.append(numericValue + seperator);
                            }
                            break;
                    case Cell.CELL_TYPE_STRING:
                            data.append(cell.getStringCellValue() + seperator);
                            break;
                    case Cell.CELL_TYPE_BLANK:
                            data.append("" + seperator);
                            break;
                    default:
                            data.append(cell + seperator);
                }
            }
            data.append("\r\n");
        }
        return data.toString();
    }
}
