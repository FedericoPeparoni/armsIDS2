package ca.ids.abms.plugins.amhs.fpl;

class Item9 extends ItemBase {
    public String aircraftNumber;
    public String aircraftType;
    public String wakeTurb;

    public Item9(final String text, final String aircraftNumber, final String aircraftType, final String wakeTurb) {
        super (9, text);
        this.aircraftNumber = aircraftNumber;
        this.aircraftType = aircraftType;
        this.wakeTurb = wakeTurb;
    }

    @Override
    public String toString() {
        return newStringHelper()
                .add ("aircraftNumber", aircraftNumber)
                .add ("aircraftType", aircraftType)
                .add ("wakeTurb", wakeTurb)
                .toString();
    }
}
