package ca.ids.abms.spreadsheets.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.ids.abms.modules.translation.Translation;
import ca.ids.abms.spreadsheets.FlightChargeType;

public class MtowChargesFinder {

    public static final class Result {
        public final WorkbookHelper helper;
        public final Sheet sheet;
        public final CellRangeAddress mtow;
        public final CellRangeAddress mtowData;
        public final List <CellData <Double>> mtowDataCells;
        public final Map <FlightChargeType, CellRangeAddress> charges;
        public Result (final WorkbookHelper helper, final Sheet sheet, final CellRangeAddress mtow, final CellRangeAddress mtowData, final List <CellData <Double>> mtowDataCells, Map <FlightChargeType, CellRangeAddress> charges) {
            this.helper = helper;
            this.sheet = sheet;
            this.mtow = mtow;
            this.mtowData = mtowData;
            this.mtowDataCells = mtowDataCells;
            this.charges = charges;
        }
    }

    public static Result find (final WorkbookHelper helper) {
        return new MtowChargesFinder (helper).do_find (null);
    }

    public static Result find (final WorkbookHelper helper, final String sheetName) {
        return new MtowChargesFinder (helper).do_find (sheetName);
    }

    public static Result find (final WorkbookHelper helper, final Sheet sheet) {
        return new MtowChargesFinder (helper).do_findOnSheet (sheet);
    }

    // ----------------- private ---------------

    private MtowChargesFinder (final WorkbookHelper helper) {
        this.helper = helper;
    }

    private Result do_find (final String sheetName) {
        if (sheetName == null) {
            for ( int i = 0; i < helper.wb().getNumberOfSheets(); ++i) {
                final Sheet sheet = helper.wb().getSheetAt (i);
                Result res = do_findOnSheet (sheet);
                if (res != null) {
                    return res;
                }
            }
            return null;
        }
        return do_findOnSheet (helper.wb().getSheet (sheetName));
    }

    private Result do_findOnSheet (final Sheet sheet) {
        if (sheet != null) {
            for (final Cell mtowCell: helper.findMatchingCells (sheet, RE_MTOW)) {
                final CellRangeAddress mtowCellRegion = helper.findCellRegion (mtowCell);
                final int firstRowIndex = mtowCellRegion.getFirstRow();
                final int firstColIndex = mtowCellRegion.getFirstColumn();
                final int firstDataRowIndex = mtowCellRegion.getLastRow() + 1;
                final int dataRowCount = helper.scanColumnDown (sheet, firstDataRowIndex, firstColIndex, cell->helper.isCellNumeric (cell));
                if (dataRowCount > 0) {
                    final int lastDataRowIndex = firstDataRowIndex + dataRowCount - 1;
                    for (int rowIndex = firstRowIndex - 1; rowIndex >= sheet.getFirstRowNum(); --rowIndex) {
                        final Row row = sheet.getRow (rowIndex);
                        final Map <FlightChargeType, CellRangeAddress> flightChargeHeaderMap = do_checkFlightChargeTypeRow (row, mtowCellRegion.getLastColumn() + 1);
                        if (flightChargeHeaderMap != null) {
                            final Map <FlightChargeType, CellRangeAddress> charges = new HashMap<>();
                            flightChargeHeaderMap.keySet().stream().forEach(flightChargeType->{
                                final CellRangeAddress flightChargeHeaderRegion = flightChargeHeaderMap.get (flightChargeType);
                                final CellRangeAddress dataRegion = new CellRangeAddress(
                                        firstRowIndex,
                                        lastDataRowIndex,
                                        flightChargeHeaderRegion.getFirstColumn(),
                                        flightChargeHeaderRegion.getLastColumn());
                                charges.put (flightChargeType, dataRegion);
                            });
                            final CellRangeAddress mtowDataAddr = new CellRangeAddress (
                                    mtowCellRegion.getLastRow() + 1,
                                    mtowCellRegion.getLastRow() + dataRowCount,
                                    mtowCellRegion.getFirstColumn(),
                                    mtowCellRegion.getLastColumn());
                            final List <CellData <Double>> mtowDataCells = do_findMtowDataCells (sheet, mtowDataAddr);
                            final Result res = new Result (helper, sheet, mtowCellRegion, mtowDataAddr, mtowDataCells, charges);
                            do_validateMtow (res);
                            do_validateCharges (res);
                            logger.debug ("found MTOW data in sheet" + " \"{}\"", sheet.getSheetName());
                            return res;
                        }
                    }
                }
            };
        }
        return null;
    }

    private List <CellData <Double>> do_findMtowDataCells (final Sheet sheet, final CellRangeAddress mtowDataAddr) {
        List <CellData <Double>> list = SSUtils.cellListTopDown (sheet, mtowDataAddr)
                .stream()
                .map (cell->{
                    final String location = helper.loc(sheet, cell);
                    final Double value = helper.getCellDoubleValue(cell);
                    helper.checkLayout(value != null, location, Translation.getLangByToken("invalid or empty MTOW"));
                    helper.checkLayout(value >= 0, location, Translation.getLangByToken("invalid negative MTOW"));
                    return CellData.create (value, cell);
                })
                .sorted((a,b)->a.value.compareTo(b.value))
                .collect(Collectors.toList());
        final List <CellData <Double>> uqList = new ArrayList <> (list.size());
        final Set <Double> set = new HashSet<>();
        list.stream().forEach(x->{
            final String location = helper.loc(sheet, x.cell);
            helper.checkLayout(!set.contains(x.value), location, "MTOW \"{}\" " + Translation.getLangByToken("occurs more than once"), x.value);
            uqList.add (x);
            set.add (x.value);
        });
        return uqList;
    }


    private Map <FlightChargeType, CellRangeAddress> do_checkFlightChargeTypeRow (final Row row, final int firstCellIndex) {
        if (row == null) {
            return null;
        }
        final Map <FlightChargeType, CellRangeAddress> addrMap = new HashMap<>();
        final int lastCellIndex = row.getLastCellNum();
        for (int cellIndex = firstCellIndex; cellIndex <= lastCellIndex; ++cellIndex) {
            final Cell cell = row.getCell(cellIndex);
            final String value = helper.getCellStringValue (cell);
            if (value != null && !value.isEmpty()) {
                final FlightChargeType flightChargeType = do_getFlightChargeTypeFromLabel (value);
                if (flightChargeType == null)
                    return null;
                if (addrMap.containsKey (flightChargeType))
                    return null;
                addrMap.put (flightChargeType, helper.findCellRegion (cell));
            }
        }
        if (!addrMap.containsKey (FlightChargeType.INTERNATIONAL) || !addrMap.containsKey (FlightChargeType.DOMESTIC)) {
            return null;
        }
        return addrMap;
    }

    private FlightChargeType do_getFlightChargeTypeFromLabel (final String label) {
        if (label != null) {
            if (RE_FCT_INTERNATIONAL.matcher(label).matches()) {
                return FlightChargeType.INTERNATIONAL;
            }
            if (RE_FCT_DOMESTIC.matcher(label).matches()) {
                return FlightChargeType.DOMESTIC;
            }
            if (RE_FCT_REGIONAL.matcher(label).matches()) {
                return FlightChargeType.REGIONAL;
            }
        }
        return null;
    }

    private void do_validateMtow (final Result res) {
        final int cellIndex = res.mtowData.getFirstColumn();
        for (int rowIndex = res.mtowData.getFirstRow(); rowIndex <= res.mtowData.getLastRow(); ++rowIndex) {
            final Row row = res.sheet.getRow (rowIndex);
            final Cell cell = row == null ? null : row.getCell (cellIndex);
            final CellReference ref = new CellReference (rowIndex, cellIndex);
            final String location = helper.loc (res.sheet, ref);
            final Double value = cell == null ? null : helper.getCellDoubleValue (cell);
            helper.checkLayout (value != null, location, Translation.getLangByToken("invalid empty or non-numeric MTOW"));
            helper.checkLayout (value >= 0, location, Translation.getLangByToken("invalid negative MTOW") + " \"{}\"", value);
        }
    }

    private void do_validateCharges (final Result res) {
        final int firstRowIndex = res.mtowData.getFirstRow();
        final int lastRowIndex = res.mtowData.getLastRow();
        for (final FlightChargeType fct: FlightChargeType.values()) {
            final CellRangeAddress range = res.charges.get (fct);
            if (range != null) {
                final int firstCellIndex = range.getFirstColumn();
                final int lastCellIndex = range.getLastColumn();
                for (int rowIndex = firstRowIndex; rowIndex <= lastRowIndex; ++rowIndex) {
                    for (int cellIndex = firstCellIndex; cellIndex <= lastCellIndex; ++cellIndex) {
                        final Row row = res.sheet.getRow (rowIndex);
                        final Cell cell = row == null ? null : row.getCell (cellIndex);
                        final CellReference ref = new CellReference (rowIndex, cellIndex);
                        final String location = helper.loc (res.sheet, ref);
                        final Double value = row == null ? null : helper.getCellDoubleValue(cell);
                        helper.checkLayout (value != null, location, Translation.getLangByToken("invalid empty or non-numeric charge"));
                        helper.checkLayout (value >= 0, location, Translation.getLangByToken("invalid negative charge") + " \"{}\"", value);
                    }
                }
            }
        }
    }

    private final WorkbookHelper helper;

    private static final Logger logger = LoggerFactory.getLogger (MtowChargesFinder.class);

    private final static String internationalRegex = "^\\s*" + "International" + "\\s*$";
    private final static String domesticRegex = "^\\s*" + "Domestic" + "\\s*$";
    private final static String regionalRegex = "^\\s*" + "Regional" + "\\s*$";

    private static final Pattern RE_MTOW = Pattern.compile("^\\s*M\\s*\\.?\\s*T\\s*\\.?\\s*O\\s*\\.?\\s*W\\s*\\.?\\s*$");
    private static final Pattern RE_FCT_INTERNATIONAL = Pattern.compile (internationalRegex, Pattern.CASE_INSENSITIVE);
    private static final Pattern RE_FCT_DOMESTIC = Pattern.compile (domesticRegex, Pattern.CASE_INSENSITIVE);
    private static final Pattern RE_FCT_REGIONAL = Pattern.compile (regionalRegex, Pattern.CASE_INSENSITIVE);

}
