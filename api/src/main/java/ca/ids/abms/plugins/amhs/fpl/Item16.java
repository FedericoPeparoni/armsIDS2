package ca.ids.abms.plugins.amhs.fpl;

import org.apache.commons.lang3.StringUtils;

class Item16 extends ItemBase {
    public String destinationAirport;
    public String altDestinationAirport;
    public String altDestinationAirport2;
    public String totalEet;
    
    public Item16(
            final String text,
            final String destinationAirport,
            final String altDestinationAirport,
            final String altDestinationAirport2,
            final String totalEet) {
        super (16, text);
        this.destinationAirport = destinationAirport;
        this.altDestinationAirport = altDestinationAirport;
        this.altDestinationAirport2 = altDestinationAirport2;
        this.totalEet = totalEet;
    }

    public boolean isEmpty() {
        return
                StringUtils.isEmpty (destinationAirport) &&
                StringUtils.isEmpty (altDestinationAirport) &&
                StringUtils.isEmpty (altDestinationAirport2) &&
                StringUtils.isEmpty (totalEet);
    }

    @Override
    public String toString() {
        return newStringHelper()
                .add ("destinationAirport", destinationAirport)
                .add ("altDestinationAirport", altDestinationAirport)
                .add ("altDestinationAirport2", altDestinationAirport2)
                .add ("totalEet", totalEet)
                .toString();
    }

}
