package ca.ids.abms.modules.revenueprojection;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "revenueProjection")
public class RevenueProjectionReport {

    public static final class Global {

        // applied percentage increase/descrease to revenue charges (current, projected,% change, included)
        public @XmlElement(nillable = true) String chargesAerodrome_old = "0";
        public @XmlElement(nillable = true) String chargesApproach_old = "0";
        public @XmlElement(nillable = true) String chargesLateArrival_old = "0";
        public @XmlElement(nillable = true) String chargesLateDeparture_old = "0";
        public @XmlElement(nillable = true) String chargesPassenger_old = "0";

        public @XmlElement(nillable = true) String chargesAerodrome_new = "0";
        public @XmlElement(nillable = true) String chargesApproach_new = "0";
        public @XmlElement(nillable = true) String chargesLateArrival_new = "0";
        public @XmlElement(nillable = true) String chargesLateDeparture_new = "0";
        public @XmlElement(nillable = true) String chargesPassenger_new = "0";

        public @XmlElement(nillable = true) String chargesAerodrome_prc = "0%";
        public @XmlElement(nillable = true) String chargesApproach_prc = "0%";
        public @XmlElement(nillable = true) String chargesLateArrival_prc = "0%";
        public @XmlElement(nillable = true) String chargesLateDeparture_prc = "0%";
        public @XmlElement(nillable = true) String chargesPassenger_prc = "0%";

        public @XmlElement(nillable = true) String chargesAerodrome_inc = "0";
        public @XmlElement(nillable = true) String chargesApproach_inc = "0";
        public @XmlElement(nillable = true) String chargesLateArrival_inc = "0";
        public @XmlElement(nillable = true) String chargesLateDeparture_inc = "0";
        public @XmlElement(nillable = true) String chargesPassenger_inc = "0";

        // applied formula to charges (current, projected, % change, included)
        public @XmlElement(nillable = true) String chargesEnrouteDom_old = "0";
        public @XmlElement(nillable = true) String chargesEnrouteDomD_old = "0";
        public @XmlElement(nillable = true) String chargesEnrouteIntArr_old = "0";
        public @XmlElement(nillable = true) String chargesEnrouteIntArrD_old = "0";
        public @XmlElement(nillable = true) String chargesEnrouteIntDep_old = "0";
        public @XmlElement(nillable = true) String chargesEnrouteIntDepD_old = "0";
        public @XmlElement(nillable = true) String chargesEnrouteIntOvf_old = "0";
        public @XmlElement(nillable = true) String chargesEnrouteIntOvfD_old = "0";
        public @XmlElement(nillable = true) String chargesEnrouteRegArr_old = "0";
        public @XmlElement(nillable = true) String chargesEnrouteRegArrD_old = "0";
        public @XmlElement(nillable = true) String chargesEnrouteRegDep_old = "0";
        public @XmlElement(nillable = true) String chargesEnrouteRegDepD_old = "0";
        public @XmlElement(nillable = true) String chargesEnrouteRegOvf_old = "0";
        public @XmlElement(nillable = true) String chargesEnrouteRegOvfD_old = "0";
        public @XmlElement(nillable = true) String chargesEnrouteW_old = "0";

        public @XmlElement(nillable = true) String chargesEnrouteDom_new = "0";
        public @XmlElement(nillable = true) String chargesEnrouteDomD_new = "0";
        public @XmlElement(nillable = true) String chargesEnrouteIntArr_new = "0";
        public @XmlElement(nillable = true) String chargesEnrouteIntArrD_new = "0";
        public @XmlElement(nillable = true) String chargesEnrouteIntDep_new = "0";
        public @XmlElement(nillable = true) String chargesEnrouteIntDepD_new = "0";
        public @XmlElement(nillable = true) String chargesEnrouteIntOvf_new = "0";
        public @XmlElement(nillable = true) String chargesEnrouteIntOvfD_new = "0";
        public @XmlElement(nillable = true) String chargesEnrouteRegArr_new = "0";
        public @XmlElement(nillable = true) String chargesEnrouteRegArrD_new = "0";
        public @XmlElement(nillable = true) String chargesEnrouteRegDep_new = "0";
        public @XmlElement(nillable = true) String chargesEnrouteRegDepD_new = "0";
        public @XmlElement(nillable = true) String chargesEnrouteRegOvf_new = "0";
        public @XmlElement(nillable = true) String chargesEnrouteRegOvfD_new = "0";
        public @XmlElement(nillable = true) String chargesEnrouteW_new = "0";

        public @XmlElement(nillable = true) String chargesEnrouteDom_prc = "0%";
        public @XmlElement(nillable = true) String chargesEnrouteDomD_prc = "0%";
        public @XmlElement(nillable = true) String chargesEnrouteIntArr_prc = "0%";
        public @XmlElement(nillable = true) String chargesEnrouteIntArrD_prc = "0%";
        public @XmlElement(nillable = true) String chargesEnrouteIntDep_prc = "0%";
        public @XmlElement(nillable = true) String chargesEnrouteIntDepD_prc = "0%";
        public @XmlElement(nillable = true) String chargesEnrouteIntOvf_prc = "0%";
        public @XmlElement(nillable = true) String chargesEnrouteIntOvfD_prc = "0%";
        public @XmlElement(nillable = true) String chargesEnrouteRegArr_prc = "0%";
        public @XmlElement(nillable = true) String chargesEnrouteRegArrD_prc = "0%";
        public @XmlElement(nillable = true) String chargesEnrouteRegDep_prc = "0%";
        public @XmlElement(nillable = true) String chargesEnrouteRegDepD_prc = "0%";
        public @XmlElement(nillable = true) String chargesEnrouteRegOvf_prc = "0%";
        public @XmlElement(nillable = true) String chargesEnrouteRegOvfD_prc = "0%";
        public @XmlElement(nillable = true) String chargesEnrouteW_prc = "0%";

        public @XmlElement(nillable = true) String chargesEnrouteDom_inc = "0";
        public @XmlElement(nillable = true) String chargesEnrouteDomD_inc = "0";
        public @XmlElement(nillable = true) String chargesEnrouteIntArr_inc = "0";
        public @XmlElement(nillable = true) String chargesEnrouteIntArrD_inc = "0";
        public @XmlElement(nillable = true) String chargesEnrouteIntDep_inc = "0";
        public @XmlElement(nillable = true) String chargesEnrouteIntDepD_inc = "0";
        public @XmlElement(nillable = true) String chargesEnrouteIntOvf_inc = "0";
        public @XmlElement(nillable = true) String chargesEnrouteIntOvfD_inc = "0";
        public @XmlElement(nillable = true) String chargesEnrouteRegArr_inc = "0";
        public @XmlElement(nillable = true) String chargesEnrouteRegArrD_inc = "0";
        public @XmlElement(nillable = true) String chargesEnrouteRegDep_inc = "0";
        public @XmlElement(nillable = true) String chargesEnrouteRegDepD_inc = "0";
        public @XmlElement(nillable = true) String chargesEnrouteRegOvf_inc = "0";
        public @XmlElement(nillable = true) String chargesEnrouteRegOvfD_inc = "0";
        public @XmlElement(nillable = true) String chargesEnrouteW_inc = "0";

        // totals (current, projected, % change, included)
        public @XmlElement(nillable = true) String totalPassengers_old = "0";
        public @XmlElement(nillable = true) String totalFlights_old = "0";
        public @XmlElement(nillable = true) String totalRevenue_old = "0";

        public @XmlElement(nillable = true) String totalPassengers_new = "0";
        public @XmlElement(nillable = true) String totalFlights_new = "0";
        public @XmlElement(nillable = true) String totalRevenue_new = "0";

        public @XmlElement(nillable = true) String totalPassengers_prc = "0%";
        public @XmlElement(nillable = true) String totalFlights_prc = "0%";
        public @XmlElement(nillable = true) String totalRevenue_prc = "0%";

        public @XmlElement(nillable = true) String totalPassengers_inc = "0";
        public @XmlElement(nillable = true) String totalFlights_inc = "0";
        public @XmlElement(nillable = true) String totalRevenue_inc = "0";
    }

    public Global global;

}
