package ca.ids.abms.amhs.parsers.aftn;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.ids.abms.amhs.AmhsMessageParseError;

/**
 * Copied & adapted from CRONOS
 */

@SuppressWarnings({
    "squid:S135",
    "squid:S1126",
    "squid:S1168",
    "squid:S1192",
    "squid:S1643",
    "squid:S1854",
    "squid:S2589",
    "squid:S3626",
    "squid:S3776",
    "squid:S4165",
    "squid:CommentedOutCodeLine",
})
public class RawAftnParser {

    private static final Logger LOG = LoggerFactory.getLogger(RawAftnParser.class);
    private Pattern emptyLine = Pattern.compile("^\\s*$");
    private Pattern line1 = Pattern
            .compile("^\\s*[(ZCZC)||(\\s)|(\\u0001)|(\\r)|(\\n)]*[a-zA-Z]{3,5}\\s*[0-9]{1,5}\\s+[0-9]{6}\\s*[\\w\\s]*");
    private Pattern line2 = Pattern.compile("^\\s*[(GG)|(FF)|(DD)|(SS)|(KK)]{2}\\s+([a-zA-Z]{8})(\\s+[a-zA-Z]{8})*");
    private Pattern addtDestLine = Pattern.compile("^\\s*([a-zA-Z]{8}\\s)+");
    private Pattern originatorLine = Pattern.compile("^\\s*[0-9]{6}\\s+[a-zA-Z]{8}\\s*[\\w\\s]*");
    private Pattern endOfHeader = Pattern.compile("[\\u0002]");
    private Pattern dupeLine = Pattern.compile("^\\s*(DUPE)+\\s*$");
    private Pattern startOfBody = Pattern.compile("\\s*(\\u0002)");
    private Pattern endOfBody = Pattern.compile("(^\\s*(NNNN))");
    private Pattern endOfBodyText = Pattern.compile("(\\u0003)");
    private static final int NUM_EMPTY_LINES = 4; // consecutive number of lines to indicate end of message.

    // field length for database tables
    private static final int TSI_LEN = 4;
    private static final int CSN_LEN = 5;
    private static final int CSN_MAX_VAL = 32767;
    private static final int PRIORITY_LEN = 2;
    private static final int ORIG_ADDR_LEN = 8;
    private static final int ORIG_COMMENT_LEN = 100;
    private static final int BODY_LEN = 4000;

    public RawAftnMessage parseMsg(String atnMsgIn) {
        try {
            return do_parseMsg(atnMsgIn);
        } catch (Exception x) {
            throw new AmhsMessageParseError(x);
        }
    }

    /******************* PRIVATE METHODS **************************************/

    private RawAftnMessage do_parseMsg(final String atnMsgIn) {

        RawAftnMessage parsedMsg = new RawAftnMessage();

        boolean msgStart = false;
        boolean line1Start = false;
        boolean line2Start = false;
        boolean orgLineStart = false;
        boolean messageBodyStart = false;
        boolean headerParseFailed = true;
        Integer lineNumber = -1;

        if (null == atnMsgIn || atnMsgIn.isEmpty()) {
            throw error("Invalid empty AFTN message");
        } else {

            LOG.trace("Parsing AFTN message:\n{}\n", atnMsgIn);

            String[] lines = getLines(atnMsgIn);

            for (String line : lines) {
                lineNumber++;
                LOG.debug(line);

                // end of Header found
                Matcher m = endOfHeader.matcher(line);
                if (m.find()) {
                    if (msgStart && line1Start && line2Start && orgLineStart) {
                        headerParseFailed = false;
                        break;
                    } else {
                        break;
                    }
                }

                // if all 3 required lines are filled, should be message body
                if (line1Start && line2Start && orgLineStart && msgStart) {
                    headerParseFailed = false;
                    break;
                }

                m = emptyLine.matcher(line);
                if (m.find())
                    continue; // skip empty lines

                // skip ZCZC, <WS>, <EOL>, and <SOH> line
                /*
                 * m = start.matcher(line); if (m.find()) { if (!msgStart) { msgStart = true;
                 * //continue; } else { validator.error().tr
                 * ("Duplicated start of AFTN message").emit(); } }
                 */

                // should only be 1 line that matches TSI,CSN, and relay time
                m = line1.matcher(line);
                if (m.find()) {
                    if (!line1Start) {
                        // if line 1 detected and message does not have ZCZC, flag as start
                        if (!msgStart)
                            msgStart = true;
                        line1Start = true;
                        parsedMsg = parseLine1(line, parsedMsg); // parse line 1
                    } else {
                        throw error("Duplicated AFTN header line 1");
                    }
                    continue;
                }

                // should only be 1 line that matches Priority and Destination Address only
                m = line2.matcher(line);
                if (m.find()) {
                    if (!line2Start && line1Start) {
                        line2Start = true;
                        parsedMsg = parseLine2(line, parsedMsg); // parse line 1
                    } else {
                        throw error("Invalid start of Priority line");
                    }
                    continue;
                }

                // will support multiple destination address lines as long as Priority line has
                // started
                m = addtDestLine.matcher(line);
                if (m.find()) {
                    if (line2Start && line1Start) {
                        parsedMsg = parseAddtDestAddr(line, parsedMsg); // parse line 1
                    } else {
                        throw error("Invalid start of Destination line");
                    }
                    continue;
                }

                // parse originator line only if
                m = originatorLine.matcher(line);
                if (m.find()) {
                    if (line1Start && line2Start) {
                        orgLineStart = true;
                        parsedMsg = parseOrginatorLine(line, parsedMsg); // parse originator line
                    } else {
                        throw error("Invalid start of Originator line");
                    }
                    continue;
                }

                // if it doesn't match any pattern, error
                throw error("Failed to parse line {0}", line);

            }

            if (!headerParseFailed) {
                String messageBody = "";
                int numEmptyLines = 0;
                for (int x = lineNumber; x < lines.length; x++) {
                    // if dupe line found, end of message body
                    Matcher m = dupeLine.matcher(lines[x]);
                    if (m.find()) {
                        parsedMsg.setDupe("DUPE");
                        break;
                    }

                    // end of body reached with minimum
                    m = endOfBody.matcher(lines[x]);
                    if (m.find() && (numEmptyLines > NUM_EMPTY_LINES)) {
                        break;
                    }

                    m = endOfBodyText.matcher(lines[x]);
                    if (m.find()) {
                        messageBody += lines[x].substring(0, m.start());
                        break;
                    }

                    // find consecutive empty lines
                    m = emptyLine.matcher(lines[x]);
                    if (m.find()) {
                        numEmptyLines++;
                    } else {
                        numEmptyLines = 0;
                    }

                    String line = "";
                    m = startOfBody.matcher(lines[x]);
                    if (m.find()) {
                        if (!messageBodyStart)
                            messageBodyStart = true;
                        // replace all STX
                        line = lines[x].replaceAll("(\\u0002)", "");
                    } else {
                        line = lines[x];
                    }

                    messageBody += line + System.getProperty("line.separator");
                }
                if (messageBody.length() <= BODY_LEN)
                    parsedMsg.setMsgBody(messageBody.trim());
                else
                    throw error("Message Body is too long");
            } else {
                throw error("Invalid AFTN header");
            }

            // final validation of required elements
            if (parsedMsg.getTsi() == null)
                throw error("Missing TSI");
            if (parsedMsg.getCsn() == null)
                throw error("Missing CSN");
            if (parsedMsg.getRelayDateTime() == null)
                throw error("Missing relay date/time");
            if (parsedMsg.getPriority() == null)
                throw error("Missing priority");
            if (parsedMsg.getDestAddrList() == null)
                throw error("Missing destination address");
            if (parsedMsg.getFilingDatetime() == null)
                throw error("Missing filing date/time");
            if (parsedMsg.getOriginator() == null)
                throw error("Missing originator address");
            if (parsedMsg.getMsgBody() == null)
                throw error("Missing message body");
        }

        return parsedMsg;
    }

    private RawAftnMessage parseLine1(String line, RawAftnMessage input) {
        Pattern requiredLine1 = Pattern.compile("[a-zA-Z]{3,5}\\s?[0-9]{1,5}\\s+[0-9]{6}\\s*");
        Matcher req = requiredLine1.matcher(line);
        req.find();

        String required = line.substring(req.start(), req.end()).trim();
        if (line.length() > req.end()) {
            String aftnExtra = line.substring(req.end()).trim();
            input.setAftnExtra(aftnExtra);
        }

        Pattern tsi = Pattern.compile("^[a-zA-Z]{3,5}$");
        Pattern csn = Pattern.compile("^[0-9]{1,5}$");
        Pattern relayDate = Pattern.compile("^[0-9]{6}$");
        Pattern tsicsn = Pattern.compile("^[a-zA-Z]{3,5}[0-9]{1,5}$");

        String[] tokens = required.trim().split("\\s"); // remove leading and ending whitespace
        for (String token : tokens) {
            Matcher m = tsi.matcher(token);
            if (m.find()) {
                if (token.length() <= TSI_LEN)
                    input.setTsi(token);
                else
                    throw error("TSI value too long {0}", token);
                continue;
            }

            m = csn.matcher(token);
            if (m.find()) {
                if ((token.length() <= CSN_LEN) && (Integer.parseInt(token) <= CSN_MAX_VAL))
                    input.setCsn(Integer.parseInt(token));
                else
                    throw error("Invalid CSN value {0}", token);
                continue;
            }

            m = tsicsn.matcher(token);
            if (m.find()) {
                Matcher matcher = Pattern.compile("\\d+").matcher(token);
                matcher.find();
                if (token.substring(0, matcher.start()).length() <= TSI_LEN)
                    input.setTsi(token.substring(0, matcher.start()));
                else
                    throw error("TSI value too long {0}", token.substring(0, matcher.start()).length());
                if ((token.substring(matcher.start(), matcher.end()).length() <= CSN_LEN)
                        && (Integer.parseInt(token.substring(matcher.start(), matcher.end())) <= CSN_MAX_VAL))
                    input.setCsn(Integer.parseInt(token.substring(matcher.start(), matcher.end())));
                else
                    throw error("Invalid CSN value {0}", token.substring(0, matcher.start()).length());
                continue;
            }

            m = relayDate.matcher(token);
            if (m.find()) {
                if (validateDateTime(token)) {
                    input.setRelayDateTime(token);
                } else {
                    throw error("Invalid relay date time value {0}", token);
                }

                continue;
            }

        }

        return input;
    }

    private RawAftnMessage parseLine2(String line, RawAftnMessage input) {
        List<String> destAddrList = new ArrayList<>();

        Pattern priority = Pattern.compile("^[(GG)|(FF)|(DD)|(SS)|(KK)]{2}$");
        Pattern destination = Pattern.compile("^[a-zA-Z]{8}$");

        String[] tokens = line.trim().split("\\s"); // remove leading and ending whitespace

        for (String token : tokens) {
            Matcher m = priority.matcher(token);
            if (m.find()) {
                if (token.length() == PRIORITY_LEN)
                    input.setPriority(token);
                else
                    throw error("Invalid priority value {0}", token);
                continue;
            }

            m = destination.matcher(token);
            if (m.find()) {
                destAddrList.add(token);
                continue;
            }
        }

        if (!destAddrList.isEmpty())
            input.setDestAddrList(destAddrList);

        return input;
    }

    private RawAftnMessage parseAddtDestAddr(String line, RawAftnMessage input) {
        List<String> destAddrList = input.getDestAddrList();

        Pattern destination = Pattern.compile("^[a-zA-Z]{8}$");

        String[] tokens = line.trim().split("\\s"); // remove leading and ending whitespace

        for (String token : tokens) {
            Matcher m = destination.matcher(token);
            if (m.find()) {
                destAddrList.add(token);
                continue;
            }
        }

        input.setDestAddrList(destAddrList);

        return input;
    }

    private RawAftnMessage parseOrginatorLine(String line, RawAftnMessage input) {
        Pattern requiredLine = Pattern.compile("[0-9]{6}\\s+[a-zA-Z]{8}\\s*");
        Matcher req = requiredLine.matcher(line);
        req.find();

        String required = line.substring(0, req.end()).trim();
        if (line.length() > req.end()) {
            String origExtra = line.substring(req.end()).trim();
            if (origExtra.length() <= ORIG_COMMENT_LEN)
                input.setOrigExtra(origExtra);
            else
                throw error("Originator extra too long {0}", origExtra);
        }

        Pattern filingDate = Pattern.compile("^[0-9]{6}$");
        Pattern originator = Pattern.compile("^[a-zA-Z]{8}$");

        String[] tokens = required.trim().split("\\s"); // remove leading and ending whitespace
        for (String token : tokens) {
            Matcher m = filingDate.matcher(token);
            if (m.find()) {
                if (validateDateTime(token)) {
                    input.setFilingDatetime(token);
                } else {
                    throw error("Invalid filing date time value {0}", token);
                }
                continue;
            }

            m = originator.matcher(token);
            if (m.find()) {
                if (token.length() == ORIG_ADDR_LEN)
                    input.setOriginator(token);
                else
                    throw error("Invalid originator value {0}", token);
                continue;
            }
        }

        return input;
    }

    private boolean validateDateTime(String input) {

        return validateDtgTime(input);
    }

    public static boolean validateDtgTime(String input) {
        if (input.length() == 6) {
            try {
                Integer day = Integer.parseInt(input.substring(0, 2));
                Integer hour = Integer.parseInt(input.substring(2, 4));
                Integer min = Integer.parseInt(input.substring(4, 6));

                if (day < 0 || day > 31)
                    return false;

                if (hour < 0 || hour > 23)
                    return false;

                if (min < 0 || min > 59)
                    return false;

                return true;

            } catch (NumberFormatException e) {
                return false;
            }
        } else
            return false;
    }

    private static String[] getLines(String in) {
        String delims = "\\r?\\n";

        if (in == null || 0 == in.length()) {
            return null;
        }
        in = in.replaceAll("\\r", "");
        return in.split(delims);
    }

    private AmhsMessageParseError error(final String format, final Object... args) {
        return new AmhsMessageParseError(MessageFormat.format(format, args));
    }

}
