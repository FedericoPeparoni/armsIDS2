package ca.ids.abms.spreadsheets.impl;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.base.Preconditions;

import org.apache.commons.lang.StringUtils;

import ca.ids.abms.modules.translation.Translation;
import ca.ids.abms.spreadsheets.FlightChargeType;
import ca.ids.abms.spreadsheets.ParkingCharges;
import ca.ids.abms.spreadsheets.dto.CellTitleDto;
import ca.ids.abms.spreadsheets.dto.WorkbookDto;

class ParkingChargesImpl extends SSViewBase implements ParkingCharges {

    public static final class TimeCharges {
        final FlightChargeBasis basis;
        final double freeHours;
        final List <Double> charges;
        public TimeCharges (final FlightChargeBasis basis, final double freeHours, final List <Double> charges) {
            this.basis = basis;
            this.freeHours = freeHours;
            this.charges = charges;
        }
    }

    public ParkingChargesImpl (final WorkbookHelper helper, final String sheetName, final List <Double> mtows,
                                final Map <FlightChargeType, TimeCharges> charges, String title) {
        super (helper.loader().data, helper.loader().baseName, sheetName, helper.mimeType());
        this.mtows = mtows;
        this.charges = charges;
        this.title = title;
    }

    @Override
    public double getCharge (final double mtow, LocalDateTime arrivalTimeUtc, LocalDateTime departureTimeUtc, FlightChargeType flightChargeType) {
        Preconditions.checkArgument (mtow >= 0);
        Preconditions.checkNotNull (arrivalTimeUtc);
        Preconditions.checkNotNull (departureTimeUtc);
        Preconditions.checkArgument (arrivalTimeUtc.compareTo (departureTimeUtc) <= 0);
        Preconditions.checkNotNull (flightChargeType);
        final int mtowIndex = do_getMtowIndex (mtow);
        final double charge = do_getCharge (mtowIndex, arrivalTimeUtc, departureTimeUtc, flightChargeType);
        return charge;
    }

    @Override
    public WorkbookDto toModel() {
        final WorkbookDto workbook = new WorkbookDto();

        final ArrayList<CellTitleDto> regionHeaders = new ArrayList<>();
        regionHeaders.add(new CellTitleDto(StringUtils.EMPTY));

        final ArrayList<CellTitleDto> freeSlotHeaders = new ArrayList<>();
        freeSlotHeaders.add(new CellTitleDto(Translation.getLangByToken("Free")));

        final ArrayList<CellTitleDto> timeSlotHeaders = new ArrayList<>();
        timeSlotHeaders.add(new CellTitleDto(Translation.getLangByToken("MTOW")));

        int valueRowsNumber = mtows.size();

        /* Create data row */
        for (int y=0; y<valueRowsNumber; y++) {
            final List<Double> row = new ArrayList<>();
            row.add(mtows.get(y));
            workbook.getData().getData().add(row);
        }

        int tableWidth = 1;

        /* Browse into the flight charges, read from the spreadsheet */
        for (final Map.Entry<FlightChargeType, ParkingChargesImpl.TimeCharges> entry : charges.entrySet()) {
            tableWidth += 1;

            final ParkingChargesImpl.TimeCharges charges = entry.getValue();
            regionHeaders.add(new CellTitleDto(entry.getKey().name()));
            freeSlotHeaders.add(new CellTitleDto(Double.toString(charges.freeHours)));
            timeSlotHeaders.add(new CellTitleDto(charges.basis.label()));

            /* Update the time charges matrix per row */
            for (int y = 0; y < valueRowsNumber && y < charges.charges.size(); y++) {
                final List<Double> row = workbook.getData().getData().get(y);
                row.add(charges.charges.get(y));
            }
        }
        workbook.getData().addRowHeaders().add(new CellTitleDto(title, tableWidth));
        workbook.getData().addRowHeaders(regionHeaders);
        workbook.getData().addRowHeaders(freeSlotHeaders);
        workbook.getData().addRowHeaders(timeSlotHeaders);
        return workbook;
    }

    @Override
    public String toHtml () {
        int valueRowsNumber = mtows.size();

        /* Set up the table with the headers */
        final StringBuilder table = new StringBuilder()
            .append(TABLE_OPEN_1).append("parking-fees-table").append(TABLE_OPEN_2)
            .append(CAPTION_OPEN).append("Parking Fees").append(CAPTION_CLOSE);

        final StringBuilder line2nd = new StringBuilder().append(HEAD_OPEN).append(HEAD_CLOSE);
        final StringBuilder line3rd = new StringBuilder().append(HEAD_OPEN).append(Translation.getLangByToken("Free")).append(HEAD_CLOSE);
        final StringBuilder line4rd = new StringBuilder().append(HEAD_OPEN).append(Translation.getLangByToken("MTOW")).append(HEAD_CLOSE)
                                                        .append(HEAD_OPEN).append(Translation.getLangByToken("HOUR")).append(HEAD_CLOSE)
                                                        .append(HEAD_OPEN).append(Translation.getLangByToken("DAY")).append(HEAD_CLOSE)
                                                        .append(HEAD_OPEN).append(Translation.getLangByToken("24HR")).append(HEAD_CLOSE);

        /* Setup the time charges matrix */
        final StringBuilder values[] = new StringBuilder[valueRowsNumber];
        for (int y=0; y<valueRowsNumber; y++) {
            values[y] = new StringBuilder().append(ROW_OPEN).append(CELL_OPEN).append(mtows.get(y)).append(CELL_CLOSE);
        }

        /* Browse into the flight charges, read from the spreadsheet */
        for (final Map.Entry<FlightChargeType, ParkingChargesImpl.TimeCharges> entry : charges.entrySet()) {
            final ParkingChargesImpl.TimeCharges charges = entry.getValue();

            /* Set up the header, 2nd row */
            line2nd.append(HEAD_OPEN).append(entry.getKey().name()).append(HEAD_CLOSE);
            line3rd.append(HEAD_OPEN).append(charges.freeHours).append(HEAD_CLOSE);

            /* Update the time charges matrix per row */
            for (int y = 0; y < valueRowsNumber && y < charges.charges.size(); y++) {
                    values[y].append(CELL_OPEN).append(charges.charges.get(y)).append(CELL_CLOSE);
            }
        }

        /* Merge the headers */
        table
            .append(ROW_OPEN).append(line2nd).append(ROW_CLOSE)
            .append(ROW_OPEN).append(line3rd).append(ROW_CLOSE)
            .append(ROW_OPEN).append(line4rd).append(ROW_CLOSE);

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

    private double do_getCharge (final int mtowIndex, LocalDateTime arrivalTimeUtc, LocalDateTime departureTimeUtc, final FlightChargeType flightChargeType) {
        final TimeCharges timeCharges = this.charges.get (flightChargeType);
        if (timeCharges == null) {
            return 0;
        }
        final double durationSeconds = ChronoUnit.SECONDS.between (arrivalTimeUtc, departureTimeUtc);
        final double durationHours = durationSeconds / 60 / 60;
        double freeDurationHours = durationHours - timeCharges.freeHours;
        if (freeDurationHours <= 0)
            return 0;
        double factor = 0;
        switch (timeCharges.basis) {
        case HOUR:
            factor = Math.ceil (freeDurationHours);
            break;
        case TWENTY_FOUR_HOUR:
            if (timeCharges.freeHours < 24) {
                factor = Math.ceil(durationHours / 24);
                double remain = durationHours % 24;
                if (remain < timeCharges.freeHours ) {
                    factor = factor - 1;
                }
            }
            break;
        case DAY:
            {
                final LocalDateTime start = arrivalTimeUtc.truncatedTo(ChronoUnit.DAYS);
                final LocalDateTime end = departureTimeUtc.truncatedTo(ChronoUnit.DAYS);
                final long durationDays = ChronoUnit.DAYS.between (start, end) + 1;
                if (durationDays > 1) {
                    factor = 0;
                    for (int i = 0; i < durationDays; i++) {
                        if (i==0) {
                            double secondOffset = ChronoUnit.SECONDS.between (arrivalTimeUtc, start.plusDays(1));
                            double houtOffSet = secondOffset / 60 / 60;
                            if (houtOffSet > timeCharges.freeHours) {
                                factor = factor + 1;
                            }
                        } else if (i == durationDays - 1) {
                            double secondOffset = ChronoUnit.SECONDS.between (end, departureTimeUtc);
                            double houtOffSet = secondOffset / 60 / 60;
                            if (houtOffSet > timeCharges.freeHours) {
                                factor = factor + 1;
                            }
                        } else {
                            factor = factor + 1;
                        }
                    }
                } else {
                    factor = durationDays;
                }
            };
            break;
        }
        return timeCharges.charges.get (mtowIndex) * factor;
    }


    private final List <Double> mtows;
    private final Map <FlightChargeType, TimeCharges> charges;
    private final String title;
}
