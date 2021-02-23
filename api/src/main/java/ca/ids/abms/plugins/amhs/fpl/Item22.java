package ca.ids.abms.plugins.amhs.fpl;

import java.util.List;

public class Item22 extends ItemBase {
    
    List <ItemBase> amendments;
    
    public Item22 (final String text, final List <ItemBase> amendments) {
        super (22, text);
        this.amendments = amendments;
    }

    @Override
    public String toString() {
        return newStringHelper()
                .add ("amendments", amendments)
                .toString();
    }


}
