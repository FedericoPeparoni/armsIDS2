package ca.ids.abms.plugins.amhs.fpl;

class Item15 extends ItemBase {
    public String cruisingSpeed;
    public String flightLevel;
    public String route;
    
    public Item15(final String text, final String cruisingSpeed, final String flightLevel, final String route) {
        super (15, text);
        this.cruisingSpeed = cruisingSpeed;
        this.flightLevel = flightLevel;
        this.route = route;
    }

    @Override
    public String toString() {
        return newStringHelper()
                .add ("cruisingSpeed", cruisingSpeed)
                .add ("flightLevel", flightLevel)
                .add ("route", route)
                .toString();
    }

}
