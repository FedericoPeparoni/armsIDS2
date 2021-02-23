package ca.ids.abms.modules.cachedevents;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.ids.abms.modules.common.mappers.ArrayMapper;
import ca.ids.abms.modules.common.mappers.ClassMapper;
import ca.ids.spring.cache.CacheableException;

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    uses = { ArrayMapper.class, CachedEventResultMapper.class, ClassMapper.class })
abstract class CachedEventMapper {

    private static final Logger LOG = LoggerFactory.getLogger(CachedEventMapper.class);
    private static final String REGEX = "[\\[\\]]";

    @Mapping(target = "exceptionsInverted", source = "exclude")
    @Mapping(target = "args", ignore = true)
    @Mapping(target = "metadata", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    abstract CachedEvent toCachedEvent(CacheableException cacheableException) throws IOException;

    abstract List<CachedEvent> toCachedEvent(List<CacheableException> cacheableExceptions);

    @Mapping(target = "exclude", source = "exceptionsInverted")
    @Mapping(target = "args", ignore = true)
    @Mapping(target = "metadata", ignore = true)
    abstract CacheableException toCacheableException(CachedEvent cachedEvent) throws IOException, ClassNotFoundException;

    abstract List<CacheableException> toCacheableException(List<CachedEvent> cachedEvents) throws IOException, ClassNotFoundException;

    @Mapping(target = "type", ignore = true)
    @Mapping(target = "resource", ignore = true)
    @Mapping(target = "action", ignore = true)
    abstract CachedEventCsvExportModel toCsvModel(CachedEvent item);

    abstract List<CachedEventCsvExportModel> toCsvModel(Iterable<CachedEvent> items);

    @AfterMapping
    void setCsvModel(final CachedEvent source, @MappingTarget CachedEventCsvExportModel target) {
        List<String> type = new ArrayList<>();
        List<String> resource = new ArrayList<>();
        List<String> action = new ArrayList<>();
        source.getMetadata().stream().limit(2).forEach(r -> {
            type.add(r.getType().toString());
            resource.add(r.getResource());
            action.add(r.getAction().toString());
        });
        target.setType(!type.isEmpty() ? type.toString().replaceAll(REGEX, "") : "OTHER");
        target.setResource(!resource.isEmpty() ? resource.toString().replaceAll(REGEX, "") : source.getTarget());
        target.setAction(!action.isEmpty() ? action.toString().replaceAll(REGEX, "") : source.getMethodName());
    }

    @AfterMapping
    void toCachedEventMetadata(final CacheableException source, @MappingTarget CachedEvent target) {
        if ( source.getMetadata() == null )
            return;

        List<CachedEventMetadata> list = new ArrayList<>();
        for ( Object object : source.getMetadata() ) {
            if (object instanceof CachedEventMetadata)
                list.add( (CachedEventMetadata) object );
            else
                throw new IllegalArgumentException("Expected instance of" + " " + CachedEventMetadata.class + ".");
        }

        target.setMetadata(list);
    }

    @AfterMapping
    void fromCachedEventMetadata(final CachedEvent source, @MappingTarget CacheableException target) {
        if ( source.getMetadata() == null )
            return;

        Object[] array = new Object[source.getMetadata().size()];
        int i = 0;
        for ( CachedEventMetadata cachedEventMetadata : source.getMetadata() ) {
            array[i] = cachedEventMetadata;
            i++;
        }

        target.setMetadata(array);
    }

    @AfterMapping
    void toByteArray(final CacheableException source, @MappingTarget CachedEvent target) throws IOException {
        List<byte[]> targetArgs = new ArrayList<>();
        for(Object arg: source.getArgs()) {

            // make sure all arguments are serializable
            if (!(arg instanceof Serializable)) {
                LOG.error("CacheableException parameter '" + source.getTarget().getName() + ":" +
                    source.getMethodName() + "(..., " + arg.getClass().getName() + ", ...)' MUST be serializable!!");
                throw new IllegalArgumentException("Argument(s) MUST be serializable!!");
            }

            // add argument as byte array
            targetArgs.add(this.toByteArray(arg));
        }
        target.setArgs(targetArgs);
    }

    @AfterMapping
    void toObject(final CachedEvent source, @MappingTarget CacheableException target) throws IOException, ClassNotFoundException {
        List<Object> targetArgs = new ArrayList<>();
        for (byte[] arg : source.getArgs()) {
            targetArgs.add(this.toObject(arg));
        }
        target.setArgs(targetArgs.toArray());
    }

    private byte[] toByteArray(Object object) throws IOException {
        byte[] bytes;
        try (
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)
        ) {
            objectOutputStream.writeObject(object);
            objectOutputStream.flush();
            bytes = byteArrayOutputStream.toByteArray();
        }
        return bytes;
    }

    private Object toObject(byte[] bytes) throws IOException, ClassNotFoundException {
        Object object;
        try (
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)
        ) {
            object = objectInputStream.readObject();
        }
        return object;
    }
}
