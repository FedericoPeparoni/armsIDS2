package ca.ids.abms.modules.reports2.common;

import ca.ids.abms.modules.flightmovements.enumerate.FlightmovementCategoryNationality;
import ca.ids.abms.modules.flightmovements.enumerate.FlightmovementCategoryScope;

import java.util.Objects;

public class CachedScopeNationalityKey {

    private final FlightmovementCategoryScope scope;

    private final FlightmovementCategoryNationality nationality;

    CachedScopeNationalityKey(
        final FlightmovementCategoryScope scope, final FlightmovementCategoryNationality nationality
    ) {
        this.scope = scope;
        this.nationality = nationality;
    }

    public FlightmovementCategoryScope getScope() {
        return scope;
    }

    public FlightmovementCategoryNationality getNationality() {
        return nationality;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CachedScopeNationalityKey)) return false;
        CachedScopeNationalityKey key = (CachedScopeNationalityKey) o;
        return scope == key.scope && nationality == key.nationality;
    }

    @Override
    public int hashCode() {
        return Objects.hash(scope, nationality);
    }
}
