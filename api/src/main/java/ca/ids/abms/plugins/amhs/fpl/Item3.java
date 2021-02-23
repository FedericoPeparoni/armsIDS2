package ca.ids.abms.plugins.amhs.fpl;

import ca.ids.abms.plugins.amhs.AmhsMessageType;

public class Item3 extends ItemBase {
    public AmhsMessageType type;
    public String ref;

    public Item3 (final String text, final AmhsMessageType type, final String ref) {
        super (3, text);
        this.type = type;
        this.ref = ref;
    }
    
    @Override
    public String toString() {
        return newStringHelper()
                .add ("type", type)
                .add ("ref", ref)
                .toString();
    }
}
