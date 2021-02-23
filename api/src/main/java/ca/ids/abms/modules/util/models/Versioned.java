package ca.ids.abms.modules.util.models;

public interface Versioned<T extends Comparable<T>> {

    T getVersion();

}
