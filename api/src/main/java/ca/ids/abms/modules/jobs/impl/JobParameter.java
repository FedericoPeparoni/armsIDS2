package ca.ids.abms.modules.jobs.impl;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class JobParameter<T> {

    JobParameter (final String name, final T value, boolean isIdentifier) {
        this.name = name;
        this.value = value;
        this.isIdentifier = isIdentifier;
    }

    private String name;

    private T value;

    @JsonIgnore
    private Boolean isIdentifier;

    String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    public T getValue() {
        return value;
    }

    void setValue(T value) {
        this.value = value;
    }

    Boolean getIdentifier() {
        return isIdentifier;
    }

    void setIdentifier(Boolean identifier) {
        isIdentifier = identifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JobParameter that = (JobParameter) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        return value != null ? value.equals(that.value) : that.value == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return new StringBuilder(name).append('=').append(value).toString();
    }
}
