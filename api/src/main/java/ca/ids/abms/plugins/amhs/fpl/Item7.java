package ca.ids.abms.plugins.amhs.fpl;

class Item7 extends ItemBase {
    public String callsign;
    
    public Item7(final String text, final String callsign) {
        super (7, text);
        this.callsign = callsign;
    }
    
    @Override
    public String toString() {
        return newStringHelper()
                .add ("callsign", callsign)
                .toString();
    }
}
