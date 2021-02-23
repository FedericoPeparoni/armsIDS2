package ca.ids.abms.spreadsheets.impl;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ca.ids.abms.spreadsheets.dto.CellTitleDto;
import ca.ids.abms.spreadsheets.dto.WorkbookDto;
import com.google.common.base.Preconditions;

import ca.ids.abms.spreadsheets.AerodromeCharges;
import ca.ids.abms.spreadsheets.FlightChargeType;
import org.apache.commons.lang.StringUtils;

class AerodromeChargesImpl extends SSViewBase implements AerodromeCharges {

    public static final class TimeCharges {
        public final int endSecond;
        public final List <Double> charges;
        public TimeCharges (final int endSecond, final List <Double> charges) {
            this.endSecond = endSecond;
            this.charges = charges;
        }
    }

    @Override
    public double getCharge (double mtow, final LocalTime timeOfDay, final FlightChargeType flightChargeType) throws IllegalArgumentException, NullPointerException {
        Preconditions.checkArgument (mtow >= 0);
        Preconditions.checkNotNull (timeOfDay);
        Preconditions.checkNotNull (flightChargeType);
        final int secondOfDay = timeOfDay.toSecondOfDay();
        final int mtowIndex = do_getMtowIndex (mtow);
        return do_getCharge (mtowIndex, secondOfDay, flightChargeType);
    }

    public AerodromeChargesImpl (final WorkbookHelper helper, final String sheetName, final List <Double> mtows, 
                                 final Map <FlightChargeType, List <TimeCharges>> charges, final String title) {
        super (helper.loader().data, helper.loader().baseName, sheetName, helper.mimeType());
        this.mtows = mtows;
        this.charges = charges;
        this.title = title;
    }

    @Override
    public WorkbookDto toModel() {
        final WorkbookDto workbook = new WorkbookDto();

        final ArrayList<CellTitleDto> regionHeaders = new ArrayList<>();
        regionHeaders.add(new CellTitleDto(StringUtils.EMPTY));

        final ArrayList<CellTitleDto> timeSlotHeaders = new ArrayList<>();
        timeSlotHeaders.add(new CellTitleDto("MTOW"));

        int valueRowsNumber = mtows.size();

        for (int y=0; y<valueRowsNumber; y++) {
            final List<Double> row = new ArrayList<>();
            row.add(mtows.get(y));
            workbook.getData().getData().add(row);
        }

        int tableWidth = 1;

        /* Browse into the flight charges, read from the spreadsheet */
        for (final Map.Entry<FlightChargeType, List <TimeCharges>> entry : charges.entrySet()) {

            int timeSlots = entry.getValue().size();
            tableWidth += timeSlots;
            regionHeaders.add(new CellTitleDto(entry.getKey().name(), timeSlots));

            /* Update the time charges matrix per row */
            final List<TimeCharges> charges = entry.getValue();
            int startInterval = 0;

            for (int x=0; x<timeSlots; x++) {

                for (int y = 0; y < valueRowsNumber; y++) {
                    List<Double> row = workbook.getData().getData().get(y);
                    row.add(charges.get(x).charges.get(y));
                }

                int endInterval = charges.get(x).endSecond;
                final StringBuilder timeInterval = new StringBuilder()
                    .append(SimpleTimeRange.formatSecondsOfDay2400(startInterval, "%02d%02d")).append('-')
                    .append(SimpleTimeRange.formatSecondsOfDay2400(endInterval, "%02d%02d"));
                timeSlotHeaders.add(new CellTitleDto(timeInterval.toString()));
                startInterval = endInterval;
            }
        }
        workbook.getData().addRowHeaders().add(new CellTitleDto(title, tableWidth));
        workbook.getData().addRowHeaders(regionHeaders);
        workbook.getData().addRowHeaders(timeSlotHeaders);
        return workbook;
    }

    @Override
    public String toHtml () {
        int valueRowsNumber = mtows.size();

        /* Set up the table with the headers */
        final StringBuilder table = new StringBuilder()
            .append(TABLE_OPEN_1).append("aerodrome-fees-table").append(TABLE_OPEN_2)
            .append(CAPTION_OPEN).append(title).append(CAPTION_CLOSE);

        final StringBuilder line2nd = new StringBuilder().append(HEAD_OPEN).append(HEAD_CLOSE);
        final StringBuilder line3rd = new StringBuilder().append(HEAD_OPEN).append("MTOW").append(HEAD_CLOSE);

        /* Setup the time charges matrix */
        final StringBuilder values[] = new StringBuilder[valueRowsNumber];
        for (int y=0; y<valueRowsNumber; y++) {
            values[y] = new StringBuilder().append(ROW_OPEN).append(CELL_OPEN).append(mtows.get(y)).append(CELL_CLOSE);
        }

        /* Browse into the flight charges, read from the spreadsheet */
        for (final Map.Entry<FlightChargeType, List <TimeCharges>> entry : charges.entrySet()) {
            int timeSlots = entry.getValue().size();

            /* Set up the header, 2nd row */
            line2nd
                .append(HEAD_SPAN_OPEN).append(timeSlots).append(HEAD_SPAN_CLOSE)
                .append(entry.getKey().name()).append(HEAD_CLOSE);

            /* Update the time charges matrix per row */
            final List<TimeCharges> charges = entry.getValue();
            int startInterval = 0;
            for (int x=0; x<timeSlots; x++) {
                for (int y = 0; y < valueRowsNumber && y < charges.size(); y++) {
                    values[y].append(CELL_OPEN).append(charges.get(x).charges.get(y)).append(CELL_CLOSE);
                }
                int endInterval = charges.get(x).endSecond;
                line3rd
                    .append(HEAD_OPEN)
                    .append(SimpleTimeRange.formatSecondsOfDay2400(startInterval, "%02d%02d")).append('-')
                    .append(SimpleTimeRange.formatSecondsOfDay2400(endInterval, "%02d%02d"))
                    .append(HEAD_CLOSE);
                startInterval = endInterval;
            }
        }
        /* Merge the headers */
        table
            .append(ROW_OPEN).append(line2nd).append(ROW_CLOSE)
            .append(ROW_OPEN).append(line3rd).append(ROW_CLOSE);

        /* Merge the values into the table */
        for (int y = 0; y < valueRowsNumber; y++) {
            values[y].append(ROW_CLOSE);
            table.append(values[y]);
        }
        table.append(TABLE_CLOSE);
        return table.toString();
    }

    // ----------------- private -----------------

    private int do_getMtowIndex (final double mtow) {
        int mtowIndex = mtows.size() - 1;
        for (int i = mtowIndex - 1; i >= 0; --i) {
            if (mtow > mtows.get(i)) {
                break;
            }
            mtowIndex = i;
        }
        return mtowIndex;
    }

    private double do_getCharge (final int mtowIndex, final int secondOfDay, final FlightChargeType flightChargeType) {
        final List <TimeCharges> timeChargesList = charges.get (flightChargeType);
        if (timeChargesList == null) {
            return 0;
        }
        int index = timeChargesList.size() - 1;
        for (int i = index; i >= 0; --i) {
            if (secondOfDay >= timeChargesList.get(i).endSecond) {
                break;
            }
            index = i;
        }
        return timeChargesList.get(index).charges.get (mtowIndex);
    }

    private final List <Double> mtows;
    private final Map <FlightChargeType, List <TimeCharges>> charges;
    private final String title;
}
