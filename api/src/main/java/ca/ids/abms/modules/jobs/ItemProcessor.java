package ca.ids.abms.modules.jobs;

public interface ItemProcessor<T> {

    T processItem(T item) throws Exception;
}
