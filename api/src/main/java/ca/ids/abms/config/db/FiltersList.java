package ca.ids.abms.config.db;

import java.util.ArrayList;

public class FiltersList {

    private final ArrayList<Filter<?>> filters;

    private FiltersList (final Builder builder) {
        this.filters = builder.filters;
    }

    public static class Builder {

        private final ArrayList<Filter<?>> filters;

        public Builder () {
            this.filters = new ArrayList<>();
        }

        public Builder (final Filter<?> filter) {
            this.filters = new ArrayList<>(1);
            assert filter != null;
            this.filters.add(filter);
        }

        public Builder add (final Filter<?> filter) {
            assert filter != null;
            this.filters.add(filter);
            return this;
        }

        public FiltersList build() {
            return new FiltersList(this);
        }
    }

    ArrayList<Filter<?>> list() {
        return this.filters;
    }

    int lenght() {
        return this.filters.size();
    }
}
