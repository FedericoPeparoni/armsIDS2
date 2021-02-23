/**
 * 
 */
package ca.ids.abms.amhs.parsers.aftn;

import java.util.List;

@SuppressWarnings ("squid:CommentedOutCodeLine")
public class RawAftnMessage {

    private String tsi; // 3 - 5 latin letters
    private Integer csn; // 1 - 5 digits
    private String relayDateTime; // 6 digits
    private String aftnExtra; // optional text on first line
    private String priority;

    private List<String> destAddrList;

    private String originator;
    private String filingDatetime;
    private String origExtra; // optional text on originator line

    private String msgBody;

    private String dupe; // dupe found in message

//	AtnPayloadType type;

//	public AtnPayloadType getType() {
//		return type;
//	}
//	public void setType(AtnPayloadType type) {
//		this.type = type;
//	}

    public String getTsi() {
        return tsi;
    }

    public void setTsi(String tsi) {
        this.tsi = tsi;
    }

    public Integer getCsn() {
        return csn;
    }

    public void setCsn(Integer csn) {
        this.csn = csn;
    }

    public String getRelayDateTime() {
        return relayDateTime;
    }

    public void setRelayDateTime(String relayDateTime) {
        this.relayDateTime = relayDateTime;
    }

    public String getAftnExtra() {
        return aftnExtra;
    }

    public void setAftnExtra(String aftnExtra) {
        this.aftnExtra = aftnExtra;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public List<String> getDestAddrList() {
        return destAddrList;
    }

    public void setDestAddrList(List<String> destAddrList) {
        this.destAddrList = destAddrList;
    }

    public String getOriginator() {
        return originator;
    }

    public void setOriginator(String originator) {
        this.originator = originator;
    }

    public String getFilingDatetime() {
        return filingDatetime;
    }

    public void setFilingDatetime(String filingDatetime) {
        this.filingDatetime = filingDatetime;
    }

    public String getOrigExtra() {
        return origExtra;
    }

    public void setOrigExtra(String origExtra) {
        this.origExtra = origExtra;
    }

    public String getMsgBody() {
        return msgBody;
    }

    public void setMsgBody(String msgBody) {
        this.msgBody = msgBody;
    }

    public String getDupe() {
        return dupe;
    }

    public void setDupe(String dupe) {
        this.dupe = dupe;
    }

}
