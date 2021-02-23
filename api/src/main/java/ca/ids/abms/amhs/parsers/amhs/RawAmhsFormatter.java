package ca.ids.abms.amhs.parsers.amhs;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import com.google.common.base.Strings;

import ca.ids.abms.amhs.AmhsException;

/**
 * Copied/adapted from CRONOS
 *
 */
@SuppressWarnings ({ "squid:CommentedOutCodeLine", "squid:S1134", "squid:S1199", "squid:S135" })
public class RawAmhsFormatter {

    private static final String NEW_LINE = "\r\n";

    public enum BodyPartType {
        IA5_TEXT, GENERAL_TEXT,
    }

    public byte[] format(final RawAmhsMessage src) {
        return format(src, BodyPartType.IA5_TEXT);
    }

    public byte[] format(final RawAmhsMessage src, final BodyPartType bodyPartType) {
        this.now = new Date();
        this.src = src;
        this.bodyPartType = bodyPartType;
        this.out = new UbimexMessage();
        this.out.AmhsMte = new UbimexMessage.AmhsMteType();
        this.out.AmhsIpm = new UbimexMessage.AmhsIpmType();

        try {
            do_setMte();
            do_setIpm();
            final byte[] serializedForm = do_serialize();
            final String xmlText = do_createRawMessageText(serializedForm);
            src.setRawMessageText(xmlText);
            return xmlText.getBytes(encoding);
        } catch (Exception x) {
            throw new AmhsException(x);
        }
    }

    private String do_getPriority() {
        final String p = src.getPriority();
        if (p == null)
            return "GG";
        if (p.length() > 2) {
            return p.substring(0, 2);
        }
        return p;
    }

    private void do_setMte() {
        out.AmhsMte.Type = "0";
        out.AmhsMte.CrtdHere = "T";
        out.AmhsMte.Intrcpt = "F";
        out.AmhsMte.SkipXmit = src.getSkipTransmit() ? "T" : "F";
        out.AmhsMte.EvntSts = "SEND";
        out.AmhsMte.EvntTm = do_formatDate(this.now);
        out.AmhsMte.CrtnTm = do_formatDate(src.getCreationDate());
        out.AmhsMte.MsgType = "0";
        out.AmhsMte.MsgFmt = "0";
        out.AmhsMte.AftnPri = do_getPriority();
        out.AmhsMte.DlExpPrhbtd = "F";
        out.AmhsMte.AltRcptAlwd = "T";
        out.AmhsMte.RsgnmntPrhbtd = "F";
        out.AmhsMte.DsclrOthr = "T";
        out.AmhsMte.ImplctCnvrsnPrhbtd = "F";
        out.AmhsMte.CnvrsnWthLsPrhbtd = "F";
        out.AmhsMte.LtstDlvryTm = do_formatDate(now);
        out.AmhsMte.CntntRtrnRqst = "F";
        out.AmhsMte.CntntType = "22";
        out.AmhsMte.DlvryNtc = "0";

        /*
         * out.AmhsMte.SrcPort = new UbimexMessage.AmhsMteType.PortType();
         * out.AmhsMte.SrcPort.PortNbr = "1"; // hard-coded in aim-ubimta config files
         * (file AmhsMtaLineMap.conf) out.AmhsMte.SrcPort.PortType = "AMHS";
         * out.AmhsMte.SrcPort.ExchngType = "X400"; out.AmhsMte.SrcPort.Prmtrs =
         * "REMOTE_OUT"; // hard-coded in aim-ubimta config files (file
         * AmhsMtaLineMap.conf) out.AmhsMte.SrcPort.RxTxTm = do_formatDate (this.now);
         */

        out.AmhsMte.DstPort = new UbimexMessage.AmhsMteType.PortType();
        // out.AmhsMte.DstPort.PortNbr = "1"; // hard-coded in aim-ubimta config files
        // (file AmhsMtaLineMap.conf)
        // out.AmhsMte.DstPort.PortType = "AMHS";
        // out.AmhsMte.DstPort.ExchngType = "X400";
        // out.AmhsMte.DstPort.Prmtrs = "REMOTE_OUT"; // hard-coded in aim-ubimta config
        // files (file AmhsMtaLineMap.conf)
        out.AmhsMte.DstPort.Prmtrs = calculateOutgoingChannelName(src.getOriginatorAddr());
        // out.AmhsMte.DstPort.RxTxTm = do_formatDate (this.now);

        out.AmhsMte.Orgntr = src.getOriginatorAddr();

        // FIXME: set to remark
        out.AmhsMte.CntntCrltr = new UbimexMessage.AmhsMteType.CntntCrltrType();
        out.AmhsMte.CntntCrltr.CntntCrltrText = "";
        out.AmhsMte.CntntCrltr.CntntCrltrLen = (Integer.valueOf(out.AmhsMte.CntntCrltr.CntntCrltrText.length()))
                .toString();
        out.AmhsMte.CntntCrltr.CntntCrltrType = "1";

        // All recipients
        out.AmhsMte.AllRcpt = new ArrayList<UbimexMessage.AmhsMteType.AllRcptType>(src.getDestAddrList().size());
        Integer count = 0;
        for (final String addr : src.getDestAddrList()) {
            final UbimexMessage.AmhsMteType.AllRcptType rec = new UbimexMessage.AmhsMteType.AllRcptType();
            rec.Rcpt = addr;
            rec.Nbr = count.toString();
            rec.RqstAltRcpt = "F";
            rec.DlvryMthd = "0";
            rec.RdrctnRsn = "0";
            rec.Rspnsblty = "1";
            rec.RjctSts = "0";
            rec.RtrnRqst = "F";
            rec.ExplCnvrsn = "-1"; // Werner's suggestion 1 based on Spain complaints
            rec.RprtRqst = "16"; // Werner's suggestion 2 based on Austria complaints
            out.AmhsMte.AllRcpt.add(rec);
            ++count;
        }

        // FIXME: set to something meaningful
        // this.out.AmhsMte.RltdMsgId = 0;

        // FIXME: set to file name
        this.out.AmhsMte.OrgnlFlNm = src.getFilename();

        // FIXME: set to char set
        out.AmhsMte.OrgnlInfoType = getOrgnlInfoType();

    }

    private static String calculateOutgoingChannelName(String originatorAddr) {
        String toReturn = "REMOTE";
        if (!Strings.isNullOrEmpty(originatorAddr)) {
            toReturn = originatorAddr.substring(originatorAddr.lastIndexOf ('=') + 1);
        }
        return toReturn;
    }

    private void do_setIpm() throws UnknownHostException {
        // FIXME: set to message type
        // out.AmhsIpm.BltnType = src.getPayloadType() == null ? null :
        // src.getPayloadType().toString();
        out.AmhsIpm.BltnType = null;

        out.AmhsIpm.IpmId = new UbimexMessage.AmhsIpmType.IpmIdType();
        out.AmhsIpm.IpmId.Orgntr = src.getOriginatorAddr();
        out.AmhsIpm.IpmId.UsrRltvId = do_getShortHostname();

        out.AmhsIpm.Orgntr = src.getOriginatorAddr();

        out.AmhsIpm.ToRcpt = new ArrayList<UbimexMessage.AmhsIpmType.RcpntType>(src.getDestAddrList().size());
        for (final String addr : src.getDestAddrList()) {
            final UbimexMessage.AmhsIpmType.RcpntType rec = new UbimexMessage.AmhsIpmType.RcpntType();
            rec.Rcpt = addr;
            rec.NtfctnRqsts = "0";
            rec.RplyRqstd = "0";
            rec.Prcdnc = "0";
            out.AmhsIpm.ToRcpt.add(rec);
        }

        // FIXME: set to subject
        out.AmhsIpm.Sbjct = "";

        out.AmhsIpm.ImprtncId = do_getAmhsImportanceCode(src.getPriority());

        out.AmhsIpm.SnstvityId = "0";
        out.AmhsIpm.AutoFwd = "0";

        out.AmhsIpm.BdyPrt = new ArrayList<UbimexMessage.AmhsIpmType.BdyPrtType>(1);
        {
            final UbimexMessage.AmhsIpmType.BdyPrtType rec = new UbimexMessage.AmhsIpmType.BdyPrtType();
            rec.MsgId = src.getBodyId();
            rec.Nbr = "1";
            rec.Frmt = "0";
            rec.Cntnt = do_getBody();

            // see http://ubidev01.idscorporation.ca/bugzilla/show_bug.cgi?id=11090

            // Latin-1
            if (this.bodyPartType.equals(BodyPartType.GENERAL_TEXT)) {
                rec.Frmt = "15";
                rec.FrmtExt = "2.6.1.4.11";
                rec.PrmsGnrl = new UbimexMessage.AmhsIpmType.BdyPrtType.PrmsGnrlType();
                rec.PrmsGnrl.RgstrtnNbr = new ArrayList<String>(3);
                rec.PrmsGnrl.RgstrtnNbr.add("1");
                rec.PrmsGnrl.RgstrtnNbr.add("6");
                rec.PrmsGnrl.RgstrtnNbr.add("100");
            }

            rec.PrmsIa5 = new UbimexMessage.AmhsIpmType.BdyPrtType.PrmsIa5Type();
            rec.PrmsIa5.Rprtr = "5";

            out.AmhsIpm.BdyPrt.add(rec);
        }
    }

    private byte[] do_serialize() throws JAXBException {
        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        final JAXBContext context = JAXBContext.newInstance(UbimexMessage.class);
        final Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
        marshaller.setProperty(Marshaller.JAXB_ENCODING, this.encoding);
        marshaller.marshal(out, stream);
        final byte[] bytes = stream.toByteArray();
        // remove leading whiute space
        int i;
        for (i = 0; i < bytes.length; ++i) {
            final int code = bytes[i];
            if (code == 9 // HT
                    || code == 10 // LF
                    || code == 11 // VT
                    || code == 12 // FF
                    || code == 13 // CR
                    || code == 32 // SP
            ) {
                continue;
            }
            break;
        }
        return Arrays.copyOfRange(bytes, i, bytes.length);
    }

    private String do_formatDate(final Date date) {
        final SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US);
        formatter.setTimeZone(UTC_TIMEZONE);
        return formatter.format(date);
    }

    private String do_formatAftnDate(final Date date) {
        final SimpleDateFormat formatter = new SimpleDateFormat("ddHHmm", Locale.US);
        formatter.setTimeZone(UTC_TIMEZONE);
        return formatter.format(date);
    }

    private String do_getAmhsImportanceCode(final String aftnPriority) {
        if (aftnPriority != null) {
            if (aftnPriority.equals("SS")) {
                return "2";
            }
            if (aftnPriority.equals("DD")) {
                return "0";
            }
            if (aftnPriority.equals("FF")) {
                return "0";
            }
            if (aftnPriority.equals("GG")) {
                return "1";
            }
            if (aftnPriority.equals("KK")) {
                return "1";
            }
        }
        return "0";
    }

    private String do_getShortHostname() throws UnknownHostException {
        String name = InetAddress.getLocalHost().getHostName();
        if (name != null) {
            name = name.replaceAll(".*$", "").trim();
            if (name.isEmpty())
                name = null;
        }
        return name;
    }

    private String do_getBody() {
        final String body = src.getBody();
        if (body == null) {
            return "";
        }
        final StringBuilder buf = new StringBuilder(body.length() + 64);
        buf.append("\u0001");
        buf.append("PRI: ").append(do_getPriority()).append(NEW_LINE);
        buf.append("FT: ").append(do_formatAftnDate(src.getCreationDate())).append(NEW_LINE);
        this.appendOHILine(buf);
        buf.append("\u0002");

        // remove non-ASCII characters; see also bug 11103
        for (int i = 0; i < body.length(); ++i) {
            final char c = body.charAt(i);
            if (c > 255) {
                buf.append('.');
                continue;
            }
            buf.append(c);
        }
        return buf.toString();
    }

    private void appendOHILine(StringBuilder buf) {
        if (!Strings.isNullOrEmpty(this.src.getOptionalHeaderInformation())) {
            buf.append("OHI: ").append(this.src.getOptionalHeaderInformation()).append(NEW_LINE);
        }
    }

    private static final Pattern RE_BODY_START_TAG = Pattern.compile("<BdyPrt>.*?<Cntnt>", Pattern.DOTALL);
    private static final Pattern RE_BODY_END_TAG = Pattern.compile("</Cntnt>");

    private String do_createRawMessageText(final byte[] serializedForm) throws UnsupportedEncodingException {
        String rawMessageText = new String(serializedForm, encoding);

        // FIXME: remove this after bug 11103 is fixed.
        //
        // UBIATN can't properly parse XML; we need to replace the body content between
        // tags <Cntnt>...</Cntnt> with the non-escaped "raw" body
        //

        final Matcher startMatcher = RE_BODY_START_TAG.matcher(rawMessageText);
        if (startMatcher.find()) {
            int bodyStart = startMatcher.end();
            final String suffix = rawMessageText.substring(bodyStart);
            final Matcher endMatcher = RE_BODY_END_TAG.matcher(suffix);
            if (endMatcher.find()) {
                final int bodyEnd = bodyStart + endMatcher.start();
                final String unescapedBody = out.AmhsIpm.BdyPrt.get(0).Cntnt;
                final String unescapedBodyOrEmptyString = unescapedBody == null ? "" : unescapedBody;
                rawMessageText = rawMessageText.substring(0, bodyStart) + unescapedBodyOrEmptyString
                        + rawMessageText.substring(bodyEnd);
            }
        }

        return rawMessageText;
    }

    private String getOrgnlInfoType() {
        switch (bodyPartType) {
        case IA5_TEXT:
            return "IA5_TEXT";
        case GENERAL_TEXT:
            return "1.0.10021.7.1.0.100";
        default:
            throw new AmhsException("unreachable code");
        }
    }

    private static final TimeZone UTC_TIMEZONE = TimeZone.getTimeZone("UTC");
    private String encoding = "ISO-8859-1";
    private Date now;
    private RawAmhsMessage src;
    private BodyPartType bodyPartType;
    private UbimexMessage out;
}
