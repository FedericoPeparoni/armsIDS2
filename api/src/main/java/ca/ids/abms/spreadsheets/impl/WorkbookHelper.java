package ca.ids.abms.spreadsheets.impl;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

import ca.ids.abms.spreadsheets.InvalidSpreadsheetLayoutException;

class WorkbookHelper {
    
    public WorkbookHelper (final ByteArrayLoader loader) {
        Preconditions.checkNotNull (loader);
        this.loader = loader;
        this.wb = loadWorkbook();
        this.mimeType = guessMimeType();
    }
    
    public ByteArrayLoader loader() {
        return loader;
    }
    
    public Workbook wb() {
        return wb;
    }
    
    public String mimeType() {
        return mimeType;
    }
    
    public byte[] data() {
        return loader.data;
    }
    
    public static String loc (final Sheet sheet, final String cellRef) {
        final StringBuilder buf = new StringBuilder();
        if (sheet.getSheetName() != null && !sheet.getSheetName().isEmpty()) {
            buf.append (sheet.getSheetName()).append (": ");
        }
        buf.append (cellRef);
        return buf.toString();
    }
    public String loc (final Sheet sheet, final CellReference cellRef) {
        return loc (sheet, cellRef.formatAsString());
    }
    public String loc (final Sheet sheet, final int rowIndex, final int columnIndex) {
        return loc (sheet, new CellReference (rowIndex, columnIndex).formatAsString());
    }
    public String loc (final Sheet sheet, final Cell cell) {
        return loc (sheet, new CellReference (cell.getRowIndex(), cell.getColumnIndex()));
    }
    public String loc (final Sheet sheet, final CellRangeAddress range) {
        return loc (sheet, range.formatAsString());
    }
    
    public InvalidSpreadsheetLayoutException badLayout (final String location, final String format, final Object... args) {
        return new InvalidSpreadsheetLayoutException (SSUtils.formatMessageWithPrefix(location + ": ", format, args));
    }
    
    public void checkLayout (final boolean ok, final String location, final String format, final Object... args) {
        if (!ok) {
            throw badLayout (location, format, args);
        }
    }
    
    /** Return true if the provided cell is empty */
    public boolean isCellEmpty (final Cell cell) {
        return getCellStringValue (cell) == null;
    }
    
    /** Return true if the provided cell contains a double value */
    public boolean isCellNumeric (final Cell cell) {
        return getCellDoubleValue (cell) != null;
    }
    
    /** Get the cell given its coordinates */
    public Cell getCell (final Sheet sheet, int rowIndex, int columnIndex) {
        if (rowIndex >= sheet.getFirstRowNum() && rowIndex <= sheet.getLastRowNum()) {
            final Row row = sheet.getRow (rowIndex);
            if (row != null) {
                if (columnIndex >= row.getFirstCellNum() && columnIndex <= row.getLastCellNum())
                    return row.getCell (columnIndex);
            }
        }
        return null;
    }
    
    /**
     * Return cell value as a number. This will parse/convert text values to double if necessary.
     * Returns null if the cell is not numeric and can't be parsed as a number.
     */
    public Double getCellDoubleValue (final Cell cell) {
        if (cell != null) {
            if (cell.getCellType() == Cell.CELL_TYPE_BLANK) {
                return null;
            }
            if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                return cell.getNumericCellValue();
            }
            if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
                try {
                    return Double.parseDouble(cell.getStringCellValue());
                }
                catch (final NumberFormatException x) {
                }
            }
        }
        return null;
    }
    
    /** Return cell value as a string, or null if the cell is not a text cell. */
    public String getCellStringValue (final Cell cell) {
        if (cell != null) {
            switch (cell.getCellType()) {
            case Cell.CELL_TYPE_BLANK:
            case Cell.CELL_TYPE_ERROR:
            case Cell.CELL_TYPE_BOOLEAN:
            case Cell.CELL_TYPE_FORMULA:
                break;
            case Cell.CELL_TYPE_NUMERIC:
                return cell.getNumericCellValue() + "";
            case Cell.CELL_TYPE_STRING:
                return cell.getStringCellValue();
            }
        }
        return null;
    }
    
    /**
     * Find all text cells whose values match the given pattern.
     */
    public List <Cell> findMatchingCells (final Sheet sheet, final Pattern valuePattern) {
        final List <Cell> resultList = new ArrayList <Cell> (64);
        final int firstRowIndex = sheet.getFirstRowNum();
        final int lastRowIndex = sheet.getLastRowNum();
        for (int rowIndex = firstRowIndex; rowIndex <= lastRowIndex; ++rowIndex) {
            final Row row = sheet.getRow (rowIndex);
            if (row != null) {
                final int firstCellIndex = row.getFirstCellNum();
                if (firstCellIndex >= 0) {
                    final int lastCellIndex = row.getLastCellNum();
                    for (int cellIndex = firstCellIndex; cellIndex <= lastCellIndex; ++cellIndex) {
                        final Cell cell = row.getCell (cellIndex);
                        if (cell != null) {
                            if (!isCellEmpty (cell)) {
                                final String value = getCellStringValue (cell);
                                if (value != null && valuePattern.matcher (value).matches()) {
                                    resultList.add (cell);
                                }
                            }
                        }
                    }
                }
            }
        }
        return resultList;
    }
    
    /**
     * Convert a cell to a range address. If the given cell overlaps with any merge regions,
     * return that. Otherwise return a range address containing the cell itself.
     */
    public CellRangeAddress findCellRegion (final Cell cell) {
        final Sheet sheet = cell.getSheet();
        final CellReference ref = new CellReference (cell);
        for (int i = 0; i < sheet.getNumMergedRegions(); ++i) {
            final CellRangeAddress reg = sheet.getMergedRegion (i);
            if (reg.getFirstRow() == ref.getRow() && reg.getFirstColumn() == ref.getCol()) {
                return reg;
            }
        }
        return CellRangeAddress.valueOf (ref.formatAsString());
    }
    
    public String findTitle(final Sheet sheet) {
        int startRowInx = sheet.getFirstRowNum();
        int endRowInx = sheet.getLastRowNum();
        String title = null;
        for (int i=startRowInx;i<=endRowInx;i++) {
            final Row row = sheet.getRow (i);
            int startColInx = row.getFirstCellNum();
            int endColInx = row.getLastCellNum();
            for (int j=startColInx;j<=endColInx;j++) {
                if (!isCellEmpty(row.getCell(j))) {
                    title = row.getCell(j).getStringCellValue();
                    return title;
                }
            }
        }
        return title;
    }
    
    public int scanColumnDown (final Sheet sheet, int startRowIndex, int colIndex, final Predicate <Cell> matcher) {
        int endRowIndex = startRowIndex;
        while (endRowIndex <= sheet.getLastRowNum()) {
            final Row row = sheet.getRow (endRowIndex);
            if (row == null || !matcher.test(row.getCell (colIndex)))
                break;
            ++endRowIndex;
        }
        return endRowIndex - startRowIndex;
    }
    
    
    // --------------------------- private ------------------------
    
    private static final Logger logger = LoggerFactory.getLogger (WorkbookHelper.class);
    
    private final ByteArrayLoader loader;
    private final Workbook wb;
    private final String mimeType;
    
    private Workbook loadWorkbook () {
        logger.debug ("creating Excel work book instance: sourceName={} rawData.length={}",
                loader.sourceName, loader.data.length);
        try (final ByteArrayInputStream stream = new ByteArrayInputStream (loader.data)) {
            return WorkbookFactory.create (stream);
        }
        catch (final Exception x) {
            throw SSUtils.wrapException(x);
        }
    }
    
    // See also: https://blogs.msdn.microsoft.com/vsofficedeveloper/2008/05/08/office-2007-file-format-mime-types-for-http-content-streaming-2/
    private String guessMimeType() {
        
        // .xls files
        if (wb instanceof HSSFWorkbook) {
            return "application/vnd.ms-excel";
        }
        
        // .xlsx
        if (wb instanceof XSSFWorkbook) {
            return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        }
        
        return "application/octet-stream";
    }

}
