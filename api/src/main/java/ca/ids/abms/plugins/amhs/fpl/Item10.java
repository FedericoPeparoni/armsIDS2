package ca.ids.abms.plugins.amhs.fpl;

class Item10 extends ItemBase {
    
    public String equip;
    
    public Item10(final String text, final String equip) {
        super (10, text);
        this.equip = equip;
    }
    
    @Override
    public String toString() {
        return newStringHelper().add ("equip", equip).toString();
    }

}
