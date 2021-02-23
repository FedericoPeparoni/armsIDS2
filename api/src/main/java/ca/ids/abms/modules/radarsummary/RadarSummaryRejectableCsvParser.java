package ca.ids.abms.modules.radarsummary;

import com.google.common.base.Preconditions;

import ca.ids.abms.modules.dataimport.RejectableCsvParser;
import ca.ids.abms.modules.radarsummary.enumerators.RadarSummaryFormat;

public abstract class RadarSummaryRejectableCsvParser extends RejectableCsvParser<RadarSummaryCsvViewModel> {
    
    private final RadarSummaryFormat radarSummaryFormat;

    /**
     * Use to parse radar summary rejectable csv line items from a file.
     *
     * @param valueDelimiter line value separator as regex
     */
    public RadarSummaryRejectableCsvParser(final RadarSummaryFormat radarSummaryFormat, final String valueDelimiter) {
        this (radarSummaryFormat, valueDelimiter, null);
    }

    /**
     * Use to parse radar summary rejectable csv line items from a file.
     *
     * @param valueDelimiter line value separator as regex
     * @param lineIndicator line indicator as regex, null if unused
     */
    public RadarSummaryRejectableCsvParser(final RadarSummaryFormat radarSummaryFormat, final String valueDelimiter, final String lineIndicator) {
        super(valueDelimiter, lineIndicator, radarSummaryFormat);
        Preconditions.checkNotNull (radarSummaryFormat);
        this.radarSummaryFormat = radarSummaryFormat;
    }

    /**
     * Radar summary format that this class applies to.
     */
    public final RadarSummaryFormat getRadarSummaryFormat() {
        return radarSummaryFormat;
    }

    /**
     * This will be used to populate reject error, line number, and raw text.
     */
    protected RadarSummaryCsvViewModel newRejectableInstance(final String line, final Integer lineNo) {
        return new RadarSummaryCsvViewModel();
    }
    
    @Override
    protected RadarSummaryCsvViewModel parseLine(final String line, final Integer lineNo) {
        final RadarSummaryCsvViewModel result = super.parseLine (line, lineNo);
        result.setFormat (radarSummaryFormat);
        return result;
    }
}
