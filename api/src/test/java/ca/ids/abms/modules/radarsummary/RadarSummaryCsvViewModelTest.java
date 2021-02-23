package ca.ids.abms.modules.radarsummary;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ca.ids.abms.util.converter.JSR310DateConverters;

/**
 * Created by s.craymer on 21/08/2017.
 */
public class RadarSummaryCsvViewModelTest {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(JSR310DateConverters.DEFAULT_PATTERN_DATE_TIME);

    private RadarSummaryCsvViewModel radarSummaryCsvViewModel;

    @Before
    public void setup() {
        this.radarSummaryCsvViewModel = new RadarSummaryCsvViewModel();
    }

    @Test
    public void setDayOfFlightTest() {

        final RadarSummaryMapper testMapper = getTestMapper();
        final RadarSummary target = testMapper.toModel(radarSummaryCsvViewModel);

        // set date value to use when setting DayOfFlight
        LocalDateTime date = LocalDateTime.parse("2017-08-21 00:00:00", DATE_TIME_FORMATTER);

        // assert that DayOfFlight is equal to date value when departureTime is less than firEntryTime
        this.radarSummaryCsvViewModel.setDate("170821");
        this.radarSummaryCsvViewModel.setDepartureTime("2209");
        this.radarSummaryCsvViewModel.setFirEntryTime("2232");

        testMapper.parseDateTimeFromCSV(radarSummaryCsvViewModel, target);
        assertThat(target.getDayOfFlight()).isEqualTo(date);

        // assert that DayOfFlight is equal to date minus one day when departureTime is greater than firEntryTime by 4 horus

        this.radarSummaryCsvViewModel.setDepartureTime("2232");
        this.radarSummaryCsvViewModel.setFirEntryTime("1809");

        testMapper.parseDateTimeFromCSV(radarSummaryCsvViewModel, target);
        assertThat(target.getDayOfFlight()).isEqualTo(date.minusDays(1));

        // assert that DayOfFlight is equal to date value when departureTime is equal to firEntryTime

        this.radarSummaryCsvViewModel.setDepartureTime("2209");
        this.radarSummaryCsvViewModel.setFirEntryTime("2209");

        testMapper.parseDateTimeFromCSV(radarSummaryCsvViewModel, target);
        assertThat(target.getDayOfFlight()).isEqualTo(date);

        // assert that DayOfFlight is equal to date value when departureTime and firEntryTime are null or empty

        this.radarSummaryCsvViewModel.setDepartureTime("");
        this.radarSummaryCsvViewModel.setFirEntryTime(null);

        testMapper.parseDateTimeFromCSV(radarSummaryCsvViewModel, target);
        assertThat(target.getDayOfFlight()).isEqualTo(date);
    }

    private RadarSummaryMapper getTestMapper() {
        return new RadarSummaryMapper() {
            @Override
            public List<RadarSummaryViewModel> toViewModel(Iterable<RadarSummary> radarSummaries) {
                return null;
            }

            @Override
            public RadarSummaryViewModel toViewModel(RadarSummary radarSummary) {
                return null;
            }

            @Override
            public RadarSummary toModel(RadarSummaryViewModel dto) {
                return null;
            }

            @Override
            public RadarSummaryCsvViewModel toCsvViewModel(RadarSummary radarSummary) {
                return null;
            }

            @Override
            public RadarSummary toModel(RadarSummaryCsvViewModel dto) {
                return new RadarSummary();
            }

            @Override
            public RadarSummaryCsvExportModel toCsvModel(RadarSummary radarSummary) {
                return null;
            }

            @Override
            public List<RadarSummaryCsvExportModel> toCsvModel(Iterable<RadarSummary> radarSummary) {
                return null;
            }
        };
    }

}
