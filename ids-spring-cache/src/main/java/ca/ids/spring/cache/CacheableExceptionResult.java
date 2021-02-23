package ca.ids.spring.cache;

import java.util.Objects;

@SuppressWarnings("WeakerAccess")
public class CacheableExceptionResult {

    private Class clazz;

    private String result;

    private Boolean thrown;

    public CacheableExceptionResult() {
        this(null, null, null);
    }

    public CacheableExceptionResult(Class clazz, String result, Boolean thrown) {
        this.clazz = clazz;
        this.result = result;
        this.thrown = thrown;
    }

    public Class getClazz() {
        return clazz;
    }

    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Boolean getThrown() {
        return thrown;
    }

    public void setThrown(Boolean thrown) {
        this.thrown = thrown;
    }

    @Override
    public String toString() {
        return "CacheableExceptionResult [clazz=" + clazz + ", result=" + result + ", thrown=" + thrown + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        else if (!(o instanceof CacheableExceptionResult))
            return false;

        CacheableExceptionResult that = (CacheableExceptionResult) o;
        return Objects.equals(clazz, that.clazz)
            && Objects.equals(result, that.result)
            && Objects.equals(thrown, that.thrown);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clazz, result, thrown);
    }
}
