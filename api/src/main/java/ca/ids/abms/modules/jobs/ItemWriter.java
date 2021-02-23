package ca.ids.abms.modules.jobs;

import java.io.IOException;

public interface ItemWriter<T> {

    void open();

    void write(final T item) throws IOException;

    void close();
}
