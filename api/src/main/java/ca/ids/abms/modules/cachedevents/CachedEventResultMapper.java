package ca.ids.abms.modules.cachedevents;

import ca.ids.abms.modules.common.mappers.ClassMapper;
import ca.ids.spring.cache.CacheableExceptionResult;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(uses = { ClassMapper.class })
public interface CachedEventResultMapper {

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    CachedEventResult toCachedEventResult(CacheableExceptionResult cacheableExceptionResult);

    List<CachedEventResult> toCachedEventResult(List<CacheableExceptionResult> cacheableExceptionResults);

    @Mapping(target = "thrown", constant = "true")
    CacheableExceptionResult toCacheableExceptionResult(CachedEventResult cachedEventResult) throws ClassNotFoundException;

    List<CacheableExceptionResult> toCacheableExceptionResult(List<CachedEventResult> cachedEventResults) throws ClassNotFoundException;
}
