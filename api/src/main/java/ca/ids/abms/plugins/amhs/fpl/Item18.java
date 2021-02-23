package ca.ids.abms.plugins.amhs.fpl;

import java.time.LocalDate;

class Item18 extends ItemBase {
    public LocalDate dayOfFlight;
    public String otherInfo;
    
    public Item18(final String text, final LocalDate dayOfFlight, final String otherInfo) {
        super (18, text);
        this.dayOfFlight = dayOfFlight;
        this.otherInfo = otherInfo;
    }
    
    @Override
    public String toString() {
        return newStringHelper()
                .add ("dayOfFlight", dayOfFlight)
                .add ("otherInfo", otherInfo)
                .toString();
    }
}
