package ca.ids.abms.modules.common.mappers;

import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

import java.util.List;

public interface AbmsCrudMapper<T, V, E> {

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    T toModel(final V viewModel);

    V toViewModel(final T model);

    List<V> toViewModel(Page<T> page);

    E toCsvModel(T item);

    List<E> toCsvModel(Iterable<T> items);
}
