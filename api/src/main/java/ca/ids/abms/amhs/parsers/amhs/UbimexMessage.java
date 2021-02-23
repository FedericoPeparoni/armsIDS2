package ca.ids.abms.amhs.parsers.amhs;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "AmhsMsg")
@SuppressWarnings({ "squid:S00116", "squid:S1700" })
public class UbimexMessage {

    public static class AmhsMteType {
        public static class PortType {
            public @XmlElement String PortNbr;
            public @XmlElement String PortType;
            public @XmlElement String ExchngType;
            public @XmlElement String Prmtrs;
            public @XmlElement String RxTxTm;
        }

        public static class MtsIdType {
            public @XmlElement String GlblDmn;
            public @XmlElement String LclId;
        }

        public static class AllRcptType {
            public @XmlElement String Rcpt;
            public @XmlElement String Nbr;
            public @XmlElement String RqstAltRcpt;
            public @XmlElement String ExplCnvrsn;
            public @XmlElement String DlvryMthd;
            public @XmlElement String RprtRqst;
            public @XmlElement String DlvryTm;
            public @XmlElement String RdrctnRsn;
            public @XmlElement String Rspnsblty;
            public @XmlElement String RjctSts;
            public @XmlElement String RtrnRqst;
        }

        public static class CntntCrltrType {
            public @XmlElement String CntntCrltrText;
            public @XmlElement String CntntCrltrType;
            public @XmlElement String CntntCrltrLen;
        }

        public @XmlElement String Vrsn;
        public @XmlElement String Type;
        public @XmlElement String MsgId;
        public @XmlElement String CrtnTm;
        public @XmlElement String CrtnMs;
        public @XmlElement String CrtdHere;
        public @XmlElement String Intrcpt;
        public @XmlElement String PrntTsiCsn;
        public @XmlElement String TsiCsn;
        public @XmlElement String SkipXmit;
        public @XmlElement String EvntSts;
        public @XmlElement String EvntTm;
        public @XmlElement String RltdMsgId;
        public @XmlElement String GpRltdMsgId;

        public @XmlElement PortType SrcPort;
        public @XmlElement PortType DstPort;

        public @XmlElement String MsgType;
        public @XmlElement String MsgFmt;
        public @XmlElement String OrgnlFlNm;
        public @XmlElement String AftnPri;

        public @XmlElement MtsIdType MtsId;

        public @XmlElement String Orgntr;

        public @XmlElement List<AllRcptType> AllRcpt;

        public @XmlElement String ImprtncId;
        public @XmlElement String DlExpPrhbtd;
        public @XmlElement String AltRcptAlwd;
        public @XmlElement String RsgnmntPrhbtd;
        public @XmlElement String DsclrOthr;
        public @XmlElement String ImplctCnvrsnPrhbtd;
        public @XmlElement String CnvrsnWthLsPrhbtd;
        public @XmlElement String DfrdDlvryTm;
        public @XmlElement String LtstDlvryTm;
        public @XmlElement String CntntRtrnRqst;
        public @XmlElement String OrgnlInfoType;
        public @XmlElement String CntntType;
        public @XmlElement String CntntId;

        public @XmlElement CntntCrltrType CntntCrltr;

        public @XmlElement String SbmsnTm;
        public @XmlElement String DlvryNtc;
        public @XmlElement String BltrlInfo;
    }

    public static class AmhsIpmType {
        public static class IpmIdType {
            public @XmlElement String Orgntr;
            public @XmlElement String UsrRltvId;
        }

        public static class RcpntType {
            public @XmlElement String Rcpt;
            public @XmlElement String NtfctnRqsts;
            public @XmlElement String RplyRqstd;
            public @XmlElement String Prcdnc;
        }

        public static class BdyPrtType {
            public static class PrmsIa5Type {
                public @XmlElement String Rprtr;
            }

            public static class PrmsGnrlType {
                public @XmlElement List<String> RgstrtnNbr;
            }

            public @XmlElement String MsgId;
            public @XmlElement String Nbr;
            public @XmlElement String Frmt;
            public @XmlElement String FrmtExt;
            public @XmlElement String Size;
            public @XmlElement PrmsGnrlType PrmsGnrl;
            public @XmlElement PrmsIa5Type PrmsIa5;
            public @XmlElement String Cntnt;
        }

        public @XmlElement String BltnType;
        public @XmlElement IpmIdType IpmId;
        public @XmlElement String Orgntr;
        public @XmlElement List<RcpntType> ToRcpt;
        public @XmlElement List<RcpntType> CcRcpt;
        public @XmlElement List<RcpntType> BccRcpt;
        public @XmlElement String Sbjct;
        public @XmlElement String OptHdrInfo;
        public @XmlElement String ExpryTm;
        public @XmlElement String RplyTm;
        public @XmlElement String ImprtncId;
        public @XmlElement String SnstvityId;
        public @XmlElement String AutoFwd;
        public @XmlElement List<BdyPrtType> BdyPrt;
    }

    public @XmlElement AmhsMteType AmhsMte;
    public @XmlElement AmhsIpmType AmhsIpm;

}
