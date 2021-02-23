package ca.ids.spring.cache;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class CacheableException  {

    private Integer id;

    private Class target;

    private String methodName;

    private Class[] paramTypes;

    private Object[] args;

    private Class[] exceptions;

    private String[] caches;

    private Boolean exclude;

    private Object[] metadata;

    private List<CacheableExceptionResult> results;
    
    private int retryCount = 0;

    public CacheableException() {
        this.results = new ArrayList<>();
    }

    public CacheableException(Class target, String methodName, Class[] paramTypes, Object[] args, Class[] exceptions,
                              String[] caches, Boolean exclude, Object[] metadata, Throwable result) {
        this.target = target;
        this.methodName = methodName;
        this.paramTypes = paramTypes;
        this.args = args;
        this.exceptions = exceptions;
        this.caches = caches;
        this.exclude = exclude;
        this.metadata = metadata;
        this.results = new ArrayList<>();
        this.addThrownResult(result);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Class getTarget() {
        return target;
    }

    public void setTarget(Class target) {
        this.target = target;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class[] getParamTypes() {
        return paramTypes;
    }

    public void setParamTypes(Class[] paramTypes) {
        this.paramTypes = paramTypes;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    public Class[] getExceptions() {
        return exceptions;
    }

    public void setExceptions(Class[] exceptions) {
        this.exceptions = exceptions;
    }

    public String[] getCaches() {
        return caches;
    }

    public void setCaches(String[] caches) {
        this.caches = caches;
    }

    public Boolean getExclude() {
        return exclude;
    }

    public void setExclude(Boolean exclude) {
        this.exclude = exclude;
    }

    public Object[] getMetadata() {
        return metadata;
    }

    public void setMetadata(Object[] metadata) {
        this.metadata = metadata;
    }

    public List<CacheableExceptionResult> getResults() {
        return results;
    }

    public void setResults(List<CacheableExceptionResult> results) {
        if (results == null)
            this.results = new ArrayList<>();
        else
            this.results = results;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    public void addReturnedResult(Object object) {
        if (this.results == null)
            this.results = new ArrayList<>();

        if (object != null)
            this.results.add(new CacheableExceptionResult(object.getClass(),
                object.toString() != null && !object.toString().isEmpty() ? object.toString() : null,
                false));
        else
            this.results.add(new CacheableExceptionResult(null, null, false));
    }

    public void addThrownResult(Throwable throwable) {
        if (this.results == null && throwable != null)
            this.results = new ArrayList<>();

        if (throwable != null)
            this.results.add(new CacheableExceptionResult(throwable.getClass(), throwable.getLocalizedMessage(), true));
    }

    @Override
    public String toString() {
        return "CacheableException [id=" + id + ", retryCount=" + retryCount + ", target=" + target + ", methodName=" + methodName
            + ", paramTypes=" + Arrays.toString(paramTypes) + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        else if (!(o instanceof CacheableException))
            return false;

        CacheableException that = (CacheableException) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
