package ca.ids.abms.modules.dataimport;

import ca.ids.abms.config.error.ExceptionFactory;
import ca.ids.abms.modules.radarsummary.enumerators.RadarSummaryFormat;
import ca.ids.abms.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public abstract class RejectableCsvParser<T extends RejectableCsvModel> {

    private static final Logger LOG = LoggerFactory.getLogger(RejectableCsvParser.class);

    private static final String RAW_TEXT_DELIMITER = ",";

    private final String lineIndicator;

    private final String valueDelimiter;

    private final String format;

    /**
     * Use to parse rejectable csv line items from a file.
     *
     * @param valueDelimiter line value separator
     */
    public RejectableCsvParser(final String valueDelimiter) {
        this(valueDelimiter, null, null);
    }

    /**
     * Use to parse rejectable csv line items from a file.
     *
     * @param valueDelimiter line value separator
     * @param lineIndicator beginning of line indicator, null if unused
     */
    public RejectableCsvParser(final String valueDelimiter, final String lineIndicator, final RadarSummaryFormat radarSummaryFormat) {
        this.valueDelimiter = valueDelimiter;
        this.lineIndicator = lineIndicator;
        this.format = radarSummaryFormat != null ? radarSummaryFormat.getName() : null;
    }

    /**
     * Parse rejectable CSV file into usable rows of data.
     *
     * @param file multipart file to parse
     * @return list of `T` models
     */
    public List<T> parseFile(final MultipartFile file) throws IOException {
        InputStream inputStream = file.getInputStream();
        return this.parseFile(new BufferedReader(new InputStreamReader(inputStream)));
    }

    /**
     * Parse rejectable CSV file into usable rows of data.
     *
     * @param file file to parse
     * @return list of `T` models
     */
    public List<T> parseFile(final File file) throws IOException {
        return this.parseFile(new BufferedReader(new FileReader(file)));
    }
    
    /**
     * Parse rejectable CSV file into usable rows of data.
     *
     * @param inputStream the stream object to be parsed
     * @return list of `T` models
     */
    public List<T> parseStream (final InputStream inputStream) throws IOException {
        return this.parseFile(new BufferedReader(new InputStreamReader (inputStream)));
    }
    
    /**
     * Generate new rejectable instance of the model `T`. This will be used to populate reject error,
     * line number, and raw text. Any additional information must be provided by the concrete class.
     */
    protected abstract T newRejectableInstance(final String line, final Integer lineNo);

    /**
     * Parse line of fields and map to type `T` to be added to list of parsed lines. Any exceptions will be
     * added as a rejected item.
     */
    protected abstract T parseFields(final String[] fields);

    private List<T> parseFile(final BufferedReader bufferedReader) throws IOException {
        try {
            String line;
            Integer lineProcessing = 0;
            List<T> list = new ArrayList<>();
            while ((line = bufferedReader.readLine()) != null) {
                // only parse lines starting with lineIndicator if defined
                // only parse lines greater then 1 character in length to prevents StringIndexOutOfBoundsException
                // and IllegalArgumentException
                // also check that line is not a header or last empty line (Radar Summary Leonardo format only)
                if (line.length() > 0 && (lineIndicator == null || line.matches(lineIndicator)) &&
                    !isRadarSummaryLeonardoFormatExtraLine(line, lineProcessing)) {
                    lineProcessing++;
                    line = checkIfIntelcanFormat(line);
                    list.add(parseLine(line, lineProcessing));
                }
            }
            return list;
        } catch (Exception ex) {
            LOG.error("Could not parse the INDRA file because: {}", ex);
            throw ex;
        }
    }
    
    protected T parseLine(final String line, final Integer lineNo) {

        // split line by column separated
        String[] fields = line.split(valueDelimiter);

        // parse line and set result based on exceptions or not
        T result;
        try {
            result = parseFields(fields);
            result.setParsed(true);
        } catch (Exception ex) {
            result = newRejectableInstance(line, lineNo);
            result.setParsed(false);
            result.setErrorMessage(ExceptionFactory.resolveManagedErrors(ex, " at row #" + lineNo));
        }

        // add line number and raw text values
        result.setLine(lineNo);
        result.setRawText(String.join(RAW_TEXT_DELIMITER, Arrays.copyOfRange(fields, 1, fields.length)));

        // return result of line parsing
        return result;
    }

    private String checkIfIntelcanFormat(String line) {
        if (StringUtils.isNotBlank(format) && format.equals(RadarSummaryFormat.INTELCAN_A.getName())) {
            return line.trim().replaceAll(" +", " ");
        }

        return line;
    }

    private boolean isRadarSummaryLeonardoFormatExtraLine(final String line, final int lineNo) {

        if (StringUtils.isBlank(format) || (StringUtils.isNotBlank(format) && !format.equals(RadarSummaryFormat.LEONARDO.getName()))) {
            return false;
        }

        List<String> fields = Arrays.asList(line.split(valueDelimiter));

        //check if the line is a header
        if (lineNo == 0) {
            final Pattern pattern = Pattern.compile ("^\\D");
            return fields.stream().allMatch(item -> pattern.matcher(item).lookingAt());
        }

        //check for total rows line at the end of the file
        return fields.isEmpty() || fields.size() == 1;

    }
}
