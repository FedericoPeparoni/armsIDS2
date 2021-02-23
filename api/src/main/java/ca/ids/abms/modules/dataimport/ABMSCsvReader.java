package ca.ids.abms.modules.dataimport;

import com.opencsv.CSVParser;
import com.opencsv.CSVReader;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;

public class ABMSCsvReader extends CSVReader {

    /**
     * Constructs CSVReader using a comma for the separator.
     *
     * @param reader The reader to an underlying CSV source.
     */
    public ABMSCsvReader(Reader reader) {
        super(reader);
    }

    /**
     * Constructs CSVReader with supplied separator.
     *
     * @param reader    The reader to an underlying CSV source.
     * @param separator The delimiter to use for separating entries.
     */
    public ABMSCsvReader(Reader reader, char separator) {
        super(reader, separator);
    }

    /**
     * Constructs CSVReader with supplied separator and quote char.
     *
     * @param reader    The reader to an underlying CSV source.
     * @param separator The delimiter to use for separating entries
     * @param quotechar The character to use for quoted elements
     */
    public ABMSCsvReader(Reader reader, char separator, char quotechar) {
        super(reader, separator, quotechar);
    }

    /**
     * Constructs CSVReader with supplied separator, quote char, and quote handling
     * behavior.
     *
     * @param reader       The reader to an underlying CSV source.
     * @param separator    The delimiter to use for separating entries
     * @param quotechar    The character to use for quoted elements
     * @param strictQuotes Sets if characters outside the quotes are ignored
     */
    public ABMSCsvReader(Reader reader, char separator, char quotechar, boolean strictQuotes) {
        super(reader, separator, quotechar,strictQuotes);
    }

    /**
     * Constructs CSVReader.
     *
     * @param reader    The reader to an underlying CSV source.
     * @param separator The delimiter to use for separating entries
     * @param quotechar The character to use for quoted elements
     * @param escape    The character to use for escaping a separator or quote
     */

    public ABMSCsvReader(Reader reader, char separator,
                     char quotechar, char escape) {
        super(reader, separator, quotechar, escape);
    }

    /**
     * Constructs CSVReader.
     *
     * @param reader    The reader to an underlying CSV source.
     * @param separator The delimiter to use for separating entries
     * @param quotechar The character to use for quoted elements
     * @param line      The number of lines to skip before reading
     */
    public ABMSCsvReader(Reader reader, char separator, char quotechar, int line) {
        super (reader, separator, quotechar, line);
    }

    /**
     * Constructs CSVReader.
     *
     * @param reader    The reader to an underlying CSV source.
     * @param separator The delimiter to use for separating entries
     * @param quotechar The character to use for quoted elements
     * @param escape    The character to use for escaping a separator or quote
     * @param line      The number of lines to skip before reading
     */
    public ABMSCsvReader(Reader reader, char separator, char quotechar, char escape, int line) {
        super(reader, separator, quotechar,escape, line);
    }

    /**
     * Constructs CSVReader.
     *
     * @param reader       The reader to an underlying CSV source.
     * @param separator    The delimiter to use for separating entries
     * @param quotechar    The character to use for quoted elements
     * @param escape       The character to use for escaping a separator or quote
     * @param line      The number of lines to skip before reading
     * @param strictQuotes Sets if characters outside the quotes are ignored
     */
    public ABMSCsvReader(Reader reader, char separator, char quotechar, char escape, int line, boolean strictQuotes) {
        super(reader, separator, quotechar, escape, line, strictQuotes);
    }

    /**
     * Constructs CSVReader with all data entered.
     *
     * @param reader                  The reader to an underlying CSV source.
     * @param separator               The delimiter to use for separating entries
     * @param quotechar               The character to use for quoted elements
     * @param escape                  The character to use for escaping a separator or quote
     * @param line                    The number of lines to skip before reading
     * @param strictQuotes            Sets if characters outside the quotes are ignored
     * @param ignoreLeadingWhiteSpace If true, parser should ignore white space before a quote in a field
     */
    public ABMSCsvReader(Reader reader, char separator, char quotechar, char escape, int line, boolean strictQuotes, boolean ignoreLeadingWhiteSpace) {
        super(reader, separator, quotechar, escape, line, strictQuotes, ignoreLeadingWhiteSpace);
    }

    /**
     * Constructs CSVReader with all data entered.
     *
     * @param reader                  The reader to an underlying CSV source.
     * @param separator               The delimiter to use for separating entries
     * @param quotechar               The character to use for quoted elements
     * @param escape                  The character to use for escaping a separator or quote
     * @param line                    The number of lines to skip before reading
     * @param strictQuotes            Sets if characters outside the quotes are ignored
     * @param ignoreLeadingWhiteSpace If true, parser should ignore white space before a quote in a field
     * @param keepCR                  If true the reader will keep carriage returns, otherwise it will discard them.
     */
    public ABMSCsvReader(Reader reader, char separator, char quotechar, char escape, int line, boolean strictQuotes,
                     boolean ignoreLeadingWhiteSpace, boolean keepCR) {
        super(reader, separator, quotechar, escape, line, strictQuotes, ignoreLeadingWhiteSpace, keepCR);
    }

    /**
     * Constructs CSVReader with supplied CSVParser.
     *
     * @param reader    The reader to an underlying CSV source.
     * @param line      The number of lines to skip before reading
     * @param csvParser The parser to use to parse input
     */
    public ABMSCsvReader(Reader reader, int line, CSVParser csvParser) {
        super(reader, line, csvParser);
    }

    public RawData readNextSingleLine() throws IOException {
        return readNextSingleLine(StringUtils.EMPTY, false);
    }

    /**
     * Reads the next line from the buffer and converts to a string array.
     *
     * @return A string array with each comma-separated element as a separate
     * entry.
     * @throws IOException If bad things happen during the read
     */
    @SuppressWarnings("squid:S3776")
    public RawData readNextSingleLine (final String stopToken, final boolean readAndDiscard) throws IOException {
    	RawData rawData = null;
    	final StringBuilder rawText = new StringBuilder();
    	final String[] row = this.readNext (rawText);
    	if (row != null) {
	    	if (!readAndDiscard && !isRowEmpty (row)) {
	    		for (final String item: row) {
	    			if (StringUtils.isNotEmpty(item)) {
	                    if (item.equals(stopToken)) {
	                        rawData = RawData.createTheLastRow();
	                    } else {
	                        rawData = RawData.createRow(validateResult(row), rawText.toString());
	                    }
                        break;
	    			}
	    		}
	    	}
	    	if (rawData == null) {
	    		rawData = RawData.createAnEmptyRow();
	    	}
	    	logger.trace ("Read CSV item: {}", rawData);
    	}
    	return rawData;
    }
    
    /**
     * Reads the next CSV record and converts to a string array.
     * 
     * <p>
     * It handles multi-line records correctly.
     * 
     * <p>
     * It also appends the raw line(s) it consumed to the provided StringBuilder,
     * using "\n" as the line separator (in case of multi-line records). Note that
     * this potentially differs from line separators in the original CSV file,
     * which could be "\r\n" -- we have no way of knowing what they were without
     * re-writing a lot of code here. This should be harmless because the "\n"
     * style is slightly more common, and a lot of the software accepts either
     * style.
     * 
     * <p>
     * <B>NOTE</b>: this code here was copy/pasted (and modified a bit) from the
     *              readNext() function of the (3rd-party) base class.
     *              
     * @param 	rawText - buffer to append raw unparsed text to
     * @return	parsed CSV fields as an array of strings
     */
    @SuppressWarnings("squid:S3776")
    private String[] readNext (StringBuilder rawText) throws IOException {
        String[] result = null;
        do {
            String nextLine = getNextLine();
            if (!hasNext) {
            	if (parser.isPending()) {
            		throw new IOException("Un-terminated quoted field at end of CSV line");
            	}
                return validateResult(result);
            }
            if (nextLine != null) {
            	if (rawText.length() != 0) {
            		rawText.append("\n");
            	}
            	rawText.append (nextLine);
            }
            String[] r = parser.parseLineMulti(nextLine);
            if (r.length > 0) {
                if (result == null) {
                    result = r;
                } else {
                    result = combineResultsFromMultipleReads(result, r);
                }
            }
        } while (parser.isPending());
        return validateResult(result);
    }

    /**
     * Check whether the given CSV row represents an empty line
     */
    @SuppressWarnings("squid:S1066")
    private static boolean isRowEmpty (final String[] row) {
    	// Empty or NULL array
    	if (row == null || row.length == 0) {
    		return true;
    	}
    	// Array with 1 element that contains only white space
    	if (row.length == 1) {
    		if (row[0] == null || row[0].isEmpty() || StringUtils.isBlank(row[0])) {
    			return true;
    		}
    	}
    	return false;
    }

    private final Logger logger = LoggerFactory.getLogger(ABMSCsvReader.class);
    
}
