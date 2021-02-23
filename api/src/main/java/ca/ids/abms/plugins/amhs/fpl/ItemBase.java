package ca.ids.abms.plugins.amhs.fpl;

import java.util.ArrayList;
import java.util.List;

import ca.ids.abms.util.StringUtils;


abstract class ItemBase {
    public final int id;
    public final String text;
    
    protected ItemBase (final int id, final String text) {
        this.id = id;
        this.text = text;
    }
    
    protected class StringHelper {
        private final String text;
        private List <String> parts = new ArrayList<>();
        private StringHelper (final String text) {
            this.text = text;
        }
        private <T> void add (final List <String> parts, final String key, final T value) {
            if (value != null) {
                parts.add (String.format ("%s=[%s]", key, value));
            }
        }
        public <T> StringHelper add (final String key, final T value) {
            add (parts, key, value);
            return this;
        }
        public String toString() {
            final List <String> parts = new ArrayList<> (this.parts);
            add (parts, "text", StringUtils.abbrev(text));
            return "[" + org.apache.commons.lang3.StringUtils.join(parts, ", ") + "]";
        }
    }
    protected StringHelper newStringHelper() {
        return new StringHelper (text);
    }
}
