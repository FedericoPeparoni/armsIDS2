package ca.ids.abms.modules.reports2.invoices.iata;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * Raw data that represents an IATA invoice document.
 * This should be further formatted into a PDF, CSV, etc.
 * <p>
 * The correspoinding BIRT report template expects this class to be formatted
 * as XML using the Java 8 JAXB library; see the example file in the BIRT report directory.
 *
 */
@XmlRootElement (name = "iataInvoiceReport")
public class IataInvoiceData {

    /** Information about each flight */
    public static final class FlightMovementInfo {
        public @XmlElement(nillable = true) Integer flightMovementId;
        public @XmlElement(nillable = true) String billingDateStr;
        public @XmlElement(nillable = true) String operatorName;
        public @XmlElement(nillable = true) String icaoCode;
        public @XmlElement(nillable = true) String flightId;
        public @XmlElement(nillable = true) String aircraftType;
        public @XmlElement(nillable = true) String regNum;
        public @XmlElement(nillable = true) String depAd;
        public @XmlElement(nillable = true) String destAd;
        public @XmlElement(nillable = true) String routing;
        public @XmlElement(nillable = true) Double crossDist;
        public @XmlElement(nillable = true) Double mtow;
        public @XmlElement(nillable = true) Double wFactor;
        public @XmlElement(nillable = true) Double dFactor;
        public @XmlElement(nillable = true) Double enrouteChargesUSD;
        public @XmlElement(nillable = true) String distanceUnitOfMeasure;
        public @XmlElement(nillable = true) String mtowUnitOfMeasure;
    };

    /** Information for currency exchange rate sheet */
    public static final class ExchangeRateInfo {
        public @XmlElement(nillable = true)
        Double exchangeRate;

        public @XmlElement(nillable = true)
        String dayOfMonth;
    }

    /** Global properties */
    public static final class Props {

        /** Invoice number to be used within the PDF, etc. document and in file names */
        public String invoiceNumber;

        /** Actual invoice number (differs from invoiceNumber in preview mode) */
        public String realInvoiceNumber;

        /** Report name */
        public String name;

        /** Billing interval description, e.g., "MARCH OF 2017" */
        public @XmlElement(nillable = true) String intervalDescr;

        /** Total charges */
        public double totalEnrouteChargesUSD;

        /** Total charges */
        public double totalEnrouteChargesANSP;
    };

    /** Global/singleton properties */
    public Props props;

    /** Information about each individual flight in this invoice */
    @XmlElementWrapper
    @XmlElement (name="flightMovementInfo", nillable = true)
    public List <FlightMovementInfo> flightMovements;

    /** Information about each individual exchange rate */
    @XmlElementWrapper
    @XmlElement (name="exchangeRateInfo", nillable = true)
    public List <ExchangeRateInfo> exchangeRates;
}
