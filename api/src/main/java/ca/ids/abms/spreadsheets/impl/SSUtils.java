package ca.ids.abms.spreadsheets.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;

import com.google.common.base.Preconditions;

import ca.ids.abms.spreadsheets.InvalidSpreadsheetLayoutException;
import ca.ids.abms.spreadsheets.SSException;

class SSUtils {

    public static String formatMessage (final String format, final Object... args) {
        return MessageFormatter.arrayFormat (format, args).getMessage();
    }
    
    public static String formatMessageWithPrefix (final String prefix, final String format, final Object... args) {
        if (prefix == null || prefix.isEmpty()) {
            return formatMessage (format, args);
        }
        return prefix + ": " + formatMessage (format, args);
    }
    
    public static <X extends RuntimeException> void check (final boolean condition, final Function <String, X> runtimeExceptionCreator, final String prefix, final String format, Object... args) {
        if (!condition) {
            final X x = runtimeExceptionCreator.apply (formatMessageWithPrefix (prefix, format, args));
            logException (x);
            throw x;
        }
    }
    
    public static void check (final boolean condition, final String prefix, final String format, Object... args) {
        check (condition, m->new SSException (m), prefix, format, args);
    }
    
    public static void checkLayout (final boolean condition, final String prefix, final String format, Object... args) {
        check (condition, m->new InvalidSpreadsheetLayoutException (m), prefix, format, args);
    }
    
    public static SSException wrapException (final Exception x) {
        final SSException xx = new SSException (x);
        logException (xx);
        return xx;
    }
    
    public static List <Cell> cellListLeftRight (final Sheet sheet, final CellRangeAddress range) {
        final int rowIndex = range.getFirstRow();
        final Row row = sheet.getRow (rowIndex);
        Preconditions.checkNotNull (row);
        final int firstCellIndex = range.getFirstColumn();
        final int lastCellIndex = range.getLastColumn();
        final List <Cell> res = new ArrayList <> (lastCellIndex - firstCellIndex + 1);
        for (int cellIndex = firstCellIndex; cellIndex <= lastCellIndex; ++cellIndex) {
            final Cell cell = row.getCell (cellIndex);
            Preconditions.checkNotNull (cell);
            res.add (cell);
        }
        return res;
    }
    
    public static List <Cell> cellListTopDown (final Sheet sheet, final CellRangeAddress range) {
        final int cellIndex = range.getFirstColumn();
        final int firstRowIndex = range.getFirstRow();
        final int lastRowIndex = range.getLastRow();
        final List <Cell> res = new ArrayList <> (lastRowIndex - firstRowIndex + 1);
        for (int rowIndex = firstRowIndex; rowIndex <= lastRowIndex; ++rowIndex) {
            final Row row = sheet.getRow (rowIndex);
            final Cell cell = row.getCell (cellIndex);
            Preconditions.checkNotNull (cell);
            res.add (cell);
        }
        return res;
    }
    
    private static final Logger logger = LoggerFactory.getLogger (SSUtils.class);
    private static void logException (final Exception x) {
        logger.trace (x.getMessage(), x);
    }
    
}
