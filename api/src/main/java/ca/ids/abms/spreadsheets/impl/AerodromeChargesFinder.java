package ca.ids.abms.spreadsheets.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

import ca.ids.abms.modules.translation.Translation;
import ca.ids.abms.spreadsheets.FlightChargeType;

class AerodromeChargesFinder {

    public static final class TimeCharges {
        public final CellData <SimpleTimeRange> timeRange;
        public final List <CellData <Double>> charges;
        public TimeCharges (final CellData <SimpleTimeRange> timeRange, final List <CellData <Double>> charges) {
            this.timeRange = timeRange;
            this.charges = charges;
        }
    }

    public static final class Result {
        public final WorkbookHelper helper;
        public final Sheet sheet;
        public final List <CellData <Double>> mtows;
        public final Map <FlightChargeType, List <TimeCharges>> timeCharges;
        public final String title;
        public Result (
                final WorkbookHelper helper,
                final Sheet sheet,
                final List <CellData <Double>> mtows,
                final Map <FlightChargeType, List <TimeCharges>> timeCharges,
                final String title) {
            this.helper = helper;
            this.sheet = sheet;
            this.mtows = mtows;
            this.timeCharges = timeCharges;
            this.title = title;
        }
    }

    public static Result find (
            final WorkbookHelper helper,
            final Sheet sheet,
            final CellRangeAddress mtowAddr,
            final List <CellData <Double>> mtowDataCells,
            final Map <FlightChargeType, CellRangeAddress> chargeAddrMap) {
        return new AerodromeChargesFinder (helper, sheet, mtowAddr, mtowDataCells, chargeAddrMap).do_find();
    }

    // ----------------------- private -------------------------

    private Result do_find() {
        final Map <FlightChargeType, List <TimeCharges>> timeCharges = do_findTimeCharges();
        final String title = helper.findTitle(sheet);
        final Result res = new Result (helper, sheet, mtowDataCells, timeCharges, title);
        return res;
    }

    private Map <FlightChargeType, List <TimeCharges>> do_findTimeCharges () {
        final Map <FlightChargeType, List <TimeCharges>> result = new HashMap<>();
        final int headerRowIndex = this.mtowAddr.getFirstRow();
        for (final FlightChargeType type: this.chargeAddrMap.keySet()) {
            final CellRangeAddress typeBlockAddr = this.chargeAddrMap.get (type);
            final int firstColumnIndex = typeBlockAddr.getFirstColumn();
            final int lastColumnIndex = typeBlockAddr.getLastColumn();
            final List <TimeCharges> timeChargesList = new ArrayList<> (lastColumnIndex - firstColumnIndex + 1);
            for (int columnIndex = firstColumnIndex; columnIndex <= lastColumnIndex; ++columnIndex) {
                final CellData <SimpleTimeRange> timeRange = do_parseTimeRange (headerRowIndex, columnIndex);
                final int finalColumnIndex = columnIndex;
                final List <CellData <Double>> charges = mtowDataCells.stream().map (mtow->do_parseCharge (mtow.cell.getRowIndex(), finalColumnIndex)).collect(Collectors.toList());
                final TimeCharges timeCharges = new TimeCharges (timeRange, charges);
                timeChargesList.add (timeCharges);
            }
            result.put (type, do_sortTimeCharges (timeChargesList));
        }
        return result;
    }

    private List <TimeCharges> do_sortTimeCharges (final List <TimeCharges> list) {
        List <TimeCharges> sortedList = list
                .stream()
                .sorted ((a, b)->a.timeRange.value.endSecond.compareTo (b.timeRange.value.endSecond))
                .collect(Collectors.toList());
        final Set <Integer> set = new HashSet <>();
        sortedList.forEach(x->{
            final String location = helper.loc (sheet, x.timeRange.cell);
            final String strValue = helper.getCellStringValue(x.timeRange.cell);
            helper.checkLayout (!set.contains (x.timeRange.value.endSecond), location, Translation.getLangByToken("end time \"{}\" occurs more than once"), strValue);
            set.add (x.timeRange.value.endSecond);
        });

        int expectedStartSecond, endSecond = 0;
        for (int i = 0; i < sortedList.size(); ++i) {
            final TimeCharges x = sortedList.get (i);
            expectedStartSecond = endSecond;
            endSecond = x.timeRange.value.endSecond;
            if (x.timeRange.value.startSecond != null && x.timeRange.value.startSecond != expectedStartSecond) {
                final String location = helper.loc (sheet, x.timeRange.cell);
                final String expectedStartTime = SimpleTimeRange.formatSecondsOfDay (expectedStartSecond);
                helper.checkLayout (false, location, Translation.getLangByToken("invalid start time, expecting") + " {}", expectedStartTime);
            }
        }
        return sortedList;
    }

    private CellData <SimpleTimeRange> do_parseTimeRange (final int rowIndex, final int columnIndex) {
        final String loc = helper.loc (sheet, rowIndex, columnIndex);
        final Row row = sheet.getRow (rowIndex);
        final Cell cell = row == null ? null : row.getCell (columnIndex);
        String strValue = null;
        if (cell != null) {
            if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                final double value = cell.getNumericCellValue();
                if (value == Math.floor (value)) {
                    strValue = Long.toString ((long)value);
                }
            }
            else if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
                strValue = cell.getStringCellValue();
            }
        }
        helper.checkLayout (strValue != null, loc, Translation.getLangByToken("missing or invalid time"));
        final SimpleTimeRange timeRange = SimpleTimeRange.parseRange (loc, strValue);
        return CellData.create(timeRange, cell);
    }

    private CellData <Double> do_parseCharge (final int rowIndex, final int columnIndex) {
        final String loc = helper.loc (sheet, rowIndex, columnIndex);
        final Row row = sheet.getRow (rowIndex);
        final Cell cell = row == null ? null : row.getCell (columnIndex);
        final Double value = cell == null ? null : helper.getCellDoubleValue (cell);
        helper.checkLayout (value != null, loc, Translation.getLangByToken("missing or invalid charge"));
        helper.checkLayout (value >= 0, loc, Translation.getLangByToken("invalid negative charge"));
        return CellData.create (value, cell);
    }

    private AerodromeChargesFinder (
            final WorkbookHelper helper,
            final Sheet sheet,
            final CellRangeAddress mtowAddr,
            final List <CellData <Double>> mtowDataCells,
            final Map <FlightChargeType, CellRangeAddress> chargeAddrMap) {
        this.helper = helper;
        this.sheet = sheet;
        this.mtowAddr = mtowAddr;
        this.mtowDataCells = mtowDataCells;
        this.chargeAddrMap = chargeAddrMap;
    }

    private final WorkbookHelper helper;
    private final Sheet sheet;
    private final CellRangeAddress mtowAddr;
    private final List <CellData <Double>> mtowDataCells;
    private final Map <FlightChargeType, CellRangeAddress> chargeAddrMap;

}
