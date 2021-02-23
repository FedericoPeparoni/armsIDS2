package ca.ids.abms.modules.reports2.common;

import ca.ids.abms.modules.currencies.Currency;

import java.util.Objects;

class CachedCurrencyKey {

    private final Currency source;

    private final Currency target;

    CachedCurrencyKey(final Currency source, final Currency target) {
        this.source = source;
        this.target = target;
    }

    public Currency getSource() {
        return source;
    }

    public Currency getTarget() {
        return target;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CachedCurrencyKey)) return false;
        CachedCurrencyKey key = (CachedCurrencyKey) o;
        return source == key.source && target == key.target;
    }

    @Override
    public int hashCode() {
        return Objects.hash(source, target);
    }
}
