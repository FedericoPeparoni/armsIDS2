package ca.ids.abms.spreadsheets.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

import ca.ids.abms.modules.translation.Translation;
import ca.ids.abms.spreadsheets.FlightChargeType;

class ParkingChargesFinder {

    public static final class TimeCharges {
        public final FlightChargeBasis basis;
        public final double freeHours;
        public final List <Double> charges;
        public TimeCharges (final FlightChargeBasis basis, final double freeHours, final List <Double> charges) {
            this.basis = basis;
            this.freeHours = freeHours;
            this.charges = charges;
        }
    }

    public static final class Result {
        public final WorkbookHelper helper;
        public final Sheet sheet;
        public final List <Double> mtows;
        public final Map <FlightChargeType, TimeCharges> charges;
        public final String title;
        public Result (
                final WorkbookHelper helper,
                final Sheet sheet,
                final List <Double> mtows,
                final Map <FlightChargeType, TimeCharges> charges,
                final String title) {
            this.helper = helper;
            this.sheet = sheet;
            this.mtows = mtows;
            this.charges = charges;
            this.title = title;
        }
    }

    public static Result find (
            final WorkbookHelper helper,
            final Sheet sheet,
            final CellRangeAddress mtowAddr,
            final List <CellData <Double>> mtowDataCells,
            final Map <FlightChargeType, CellRangeAddress> chargeAddrMap) {
        return new ParkingChargesFinder (helper, sheet, mtowAddr, mtowDataCells, chargeAddrMap).do_find();
    }

    // ----------------------- private -------------------------

    private Result do_find() {
        final Result res = new Result (
                helper,
                sheet,
                mtowDataCells.stream().map(x->x.value).collect (Collectors.toList()),
                do_findTimeCharges(),
                helper.findTitle(sheet)
        );
        return res;
    }

    private Map <FlightChargeType, TimeCharges> do_findTimeCharges() {
        final Map <FlightChargeType, TimeCharges> result = new HashMap<>();
        final int headerRowIndex = this.mtowAddr.getFirstRow();
        for (final FlightChargeType type: this.chargeAddrMap.keySet()) {
            final CellRangeAddress typeBlockAddr = this.chargeAddrMap.get (type);
            final int columnIndex = typeBlockAddr.getFirstColumn();
            final FlightChargeBasis basis = do_parseBasis (headerRowIndex, columnIndex);
            final int finalColumnIndex = columnIndex;
            final double freeHours = do_parseFreeHours (headerRowIndex - 1, columnIndex);
            final List <Double> charges = mtowDataCells.stream().map (mtow->do_parseCharge (mtow.cell.getRowIndex(), finalColumnIndex)).collect(Collectors.toList());
            final TimeCharges timeCharges = new TimeCharges (basis, freeHours, charges);
            result.put (type, timeCharges);
        }
        return result;
    }

    private FlightChargeBasis do_parseBasis (final int rowIndex, final int columnIndex) {
        final String location = helper.loc (sheet, rowIndex, columnIndex);
        final Cell cell = helper.getCell (sheet, rowIndex, columnIndex);
        final String s = helper.getCellStringValue (cell);
        final FlightChargeBasis basis = s == null ? null : FlightChargeBasis.matchingValue(s);
        helper.checkLayout (basis != null, location, Translation.getLangByToken("invalid or missing charges basis, expecting one of:") + " {}", FlightChargeBasis.allDescr());
        return basis;
    }

    private double do_parseFreeHours (final int rowIndex, final int columnIndex) {
        final String location = helper.loc (sheet, rowIndex, columnIndex);
        final Cell cell = helper.getCell (sheet, rowIndex, columnIndex);
        final Double value = helper.getCellDoubleValue (cell);
        helper.checkLayout (value != null && value >= 0, location, Translation.getLangByToken("invalid or missing free parking duration"));
        return value;
    }

    private double do_parseCharge (final int rowIndex, final int columnIndex) {
        final String loc = helper.loc (sheet, rowIndex, columnIndex);
        final Row row = sheet.getRow (rowIndex);
        final Cell cell = row == null ? null : row.getCell (columnIndex);
        final Double value = cell == null ? null : helper.getCellDoubleValue (cell);
        helper.checkLayout (value != null, loc, Translation.getLangByToken("missing or invalid charge"));
        helper.checkLayout (value >= 0, loc, Translation.getLangByToken("invalid negative charge"));
        return value;
    }

    private ParkingChargesFinder (
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
