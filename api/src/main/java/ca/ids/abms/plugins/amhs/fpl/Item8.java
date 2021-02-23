package ca.ids.abms.plugins.amhs.fpl;

class Item8 extends ItemBase {
    public String flightRules;
    public String flightType;
    
    public Item8(final String text, final String flightRules, final String flightType) {
        super (8, text);
        this.flightRules = flightRules;
        this.flightType = flightType;
    }
    
    @Override
    public String toString() {
        return newStringHelper()
                .add ("flightRules", flightRules)
                .add ("flightType", flightType)
                .toString();
    }

}
