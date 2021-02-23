package ca.ids.abms.plugins.amhs.fpl;

import org.apache.commons.lang3.StringUtils;

class Item17 extends ItemBase {
    public String arrivalAirport;
    public String arrivalTime;
    public String arrivalAirportName;
    
    public Item17 (final String text, final String arrivalAirport, final String arrivalTime, final String arrivalAirportName) {
        super (17, text);
        this.arrivalAirport = arrivalAirport;
        this.arrivalTime = arrivalTime;
        this.arrivalAirportName = arrivalAirportName;
    }
    
    public boolean isEmpty() {
        return StringUtils.isEmpty(arrivalAirport) && 
                StringUtils.isEmpty(arrivalAirportName) && 
                StringUtils.isEmpty(arrivalTime);
    }
    
    @Override
    public String toString() {
        return newStringHelper()
                .add ("arrivalAirport", arrivalAirport)
                .add ("arrivalTime", arrivalTime)
                .add ("arrivalAirportName", arrivalAirportName)
                .toString();
    }
}
