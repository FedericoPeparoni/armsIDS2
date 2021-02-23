package ca.ids.spring.cache;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class CacheableExceptionProviderImpl implements CacheableExceptionProvider {

    private final HashMap<Integer, CacheableException> cacheableExceptions;

    public CacheableExceptionProviderImpl() {
        this.cacheableExceptions = new HashMap<>();
    }

    public List<CacheableException> findAll() {
        return new ArrayList<>(cacheableExceptions.values());
    }

    public void create(CacheableException cacheableException) {
        cacheableException.setId(cacheableException.hashCode());
        this.cacheableExceptions.put(cacheableException.getId(), cacheableException);
    }

    public void update(CacheableException cacheableException) {
        this.cacheableExceptions.put(cacheableException.getId(), cacheableException);
    }

    public void remove(CacheableException cacheableException) {
        this.cacheableExceptions.remove(cacheableException.getId());
    }
}
