package ca.ids.abms.modules.util.models;

public abstract class VersionedViewModel implements Versioned<Long> {

    private Long version;

    @Override
    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
