package ca.ids.abms.plugins.amhs.fpl;

class Item13 extends ItemBase {

    public String departureAirport;
    public String departureTime;
    
    public Item13(final String text, final String departureAirport, final String departureTime) {
        super (13, text);
        this.departureAirport = departureAirport;
        this.departureTime = departureTime;
    }

    @Override
    public String toString() {
        return newStringHelper()
                .add ("departureAirport", departureAirport)
                .add ("departureTime", departureTime)
                .toString();
    }

}
