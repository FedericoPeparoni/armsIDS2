package ca.ids.abms.modules.revenueprojection;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Arrays;
import java.util.List;

@XmlRootElement(name = "revenueProjection")
public class RevenueProjectionData {

    public static final class Global {

        // applied percentage increase/descrease to revenue charges (current, projected, % change, included)
        public @XmlElement(nillable = true) List<Double> chargesAerodrome = Arrays.asList(0.0d, 0.0d, 0.0d, 0.0d);
        public @XmlElement(nillable = true) List<Double> chargesApproach = Arrays.asList(0.0d, 0.0d, 0.0d, 0.0d);
        public @XmlElement(nillable = true) List<Double> chargesLateArrival = Arrays.asList(0.0d, 0.0d, 0.0d, 0.0d);
        public @XmlElement(nillable = true) List<Double> chargesLateDeparture = Arrays.asList(0.0d, 0.0d, 0.0d, 0.0d);
        public @XmlElement(nillable = true) List<Double> chargesPassenger = Arrays.asList(0.0d, 0.0d, 0.0d, 0.0d);

        // applied formula to charges (current, projected, % change, included)
        public @XmlElement(nillable = true) List<Double> chargesEnrouteDom = Arrays.asList(0.0d, 0.0d, 0.0d, 0.0d);
        public @XmlElement(nillable = true) List<Double> chargesEnrouteDomD = Arrays.asList(0.0d, 0.0d, 0.0d, 0.0d);
        public @XmlElement(nillable = true) List<Double> chargesEnrouteIntArr = Arrays.asList(0.0d, 0.0d, 0.0d, 0.0d);
        public @XmlElement(nillable = true) List<Double> chargesEnrouteIntArrD = Arrays.asList(0.0d, 0.0d, 0.0d, 0.0d);
        public @XmlElement(nillable = true) List<Double> chargesEnrouteIntDep = Arrays.asList(0.0d, 0.0d, 0.0d, 0.0d);
        public @XmlElement(nillable = true) List<Double> chargesEnrouteIntDepD = Arrays.asList(0.0d, 0.0d, 0.0d, 0.0d);
        public @XmlElement(nillable = true) List<Double> chargesEnrouteIntOvf = Arrays.asList(0.0d, 0.0d, 0.0d, 0.0d);
        public @XmlElement(nillable = true) List<Double> chargesEnrouteIntOvfD = Arrays.asList(0.0d, 0.0d, 0.0d, 0.0d);
        public @XmlElement(nillable = true) List<Double> chargesEnrouteRegArr = Arrays.asList(0.0d, 0.0d, 0.0d, 0.0d);
        public @XmlElement(nillable = true) List<Double> chargesEnrouteRegArrD = Arrays.asList(0.0d, 0.0d, 0.0d, 0.0d);
        public @XmlElement(nillable = true) List<Double> chargesEnrouteRegDep = Arrays.asList(0.0d, 0.0d, 0.0d, 0.0d);
        public @XmlElement(nillable = true) List<Double> chargesEnrouteRegDepD = Arrays.asList(0.0d, 0.0d, 0.0d, 0.0d);
        public @XmlElement(nillable = true) List<Double> chargesEnrouteRegOvf = Arrays.asList(0.0d, 0.0d, 0.0d, 0.0d);
        public @XmlElement(nillable = true) List<Double> chargesEnrouteRegOvfD = Arrays.asList(0.0d, 0.0d, 0.0d, 0.0d);
        public @XmlElement(nillable = true) List<Double> chargesEnrouteW = Arrays.asList(0.0d, 0.0d, 0.0d, 0.0d);

        //totals (current, projected, % change, included)
        public @XmlElement(nillable = true) List<Double> totalPassengers = Arrays.asList(0.0d, 0.0d, 0.0d, 1.0d);
        public @XmlElement(nillable = true) List<Double> totalFlights = Arrays.asList(0.0d, 0.0d, 0.0d, 1.0d);
        public @XmlElement(nillable = true) List<Double> totalRevenue = Arrays.asList(0.0d, 0.0d, 0.0d, 1.0d);

    }

    public Global global;

}
