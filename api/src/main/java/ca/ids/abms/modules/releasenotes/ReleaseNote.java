package ca.ids.abms.modules.releasenotes;

import ca.ids.abms.config.db.SearchableEntity;
import ca.ids.abms.config.db.SearchableText;
import ca.ids.abms.modules.util.models.VersionedAuditedEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "release_notes")
public class ReleaseNote extends VersionedAuditedEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "title")
    @NotNull
    @SearchableText
    private String title;

    @Column(name = "number", length = 16)
    @NotNull
    private String number;

    @Column(name = "reopened")
    @NotNull
    private Boolean reopened;

    @ManyToOne
    @JoinColumn(name = "release_category_id")
    @NotNull
    @SearchableEntity
    private ReleaseCategory releaseCategory;

    @Column(name = "release_version", length = 16)
    @NotNull
    @SearchableText
    private String releaseVersion;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Boolean getReopened() {
        return reopened;
    }

    public void setReopened(Boolean reopened) {
        this.reopened = reopened;
    }

    public ReleaseCategory getReleaseCategory() {
        return releaseCategory;
    }

    public void setReleaseCategory(ReleaseCategory releaseCategory) {
        this.releaseCategory = releaseCategory;
    }

    public String getReleaseVersion() {
        return releaseVersion;
    }

    public void setReleaseVersion(String releaseVersion) {
        this.releaseVersion = releaseVersion;
    }
}
