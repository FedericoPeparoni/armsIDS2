package ca.ids.abms.config.db;

import java.util.ArrayList;

public class JoinFilterList {

    private final ArrayList<JoinFilter<?>> joinFilters;

    private JoinFilterList (final Builder builder) {
        this.joinFilters = builder.joinFilters;
    }

    public static class Builder {

        private final ArrayList<JoinFilter<?>> joinFilters;

        public Builder () {
            this.joinFilters = new ArrayList<>();
        }

        public Builder (final JoinFilter<?> joinFilter) {
            this.joinFilters = new ArrayList<>(1);
            assert joinFilter != null;
            this.joinFilters.add(joinFilter);
        }

        public Builder add (final JoinFilter<?> joinFilter) {
            assert joinFilter != null;
            this.joinFilters.add(joinFilter);
            return this;
        }

        public JoinFilterList build() {
            return new JoinFilterList(this);
        }
    }

    ArrayList<JoinFilter<?>> list() {
        return this.joinFilters;
    }
}
