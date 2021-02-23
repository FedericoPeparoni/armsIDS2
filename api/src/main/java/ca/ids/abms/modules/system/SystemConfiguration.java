package ca.ids.abms.modules.system;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import ca.ids.abms.modules.util.models.VersionedAuditedEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class SystemConfiguration extends VersionedAuditedEntity {

    private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Size(max = 100)
    @Column(unique = true)
    @NotNull
    private String itemName;

    @ManyToOne
    @JoinColumn(name = "item_class")
    @NotNull
    private SystemItemType itemClass;

    @ManyToOne
    @JoinColumn(name = "data_type")
    @NotNull
    private SystemDataType dataType;

    @Size(max = 100)
    private String units;

    @Size(max = 100)
    private String range;

    @Size(max = 100)
    private String defaultValue;

    @Size(max = 100)
    private String currentValue;

    @NotNull
    private Boolean clientStorageForbidden;

    @Enumerated(EnumType.STRING)
    private SystemValidationType systemValidationType;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        SystemConfiguration that = (SystemConfiguration) o;

        return id != null ? id.equals(that.id) : that.id == null;
    }

    public String getCurrentValue() {
        return currentValue;
    }

    /**
     * Return current value as a list of strings by splitting it on commas
     */
    @Transient
    @JsonIgnore
    public List <String> getCurrentValueList() {
        return do_split (getCurrentValue());
    }

    /**
     * Return current value as a list of objects by converting each comma-separated string to some other object type
     */
    @Transient
    @JsonIgnore
    public <T> List <T> getCurrentValueList (final Function <String, T> converter) {
        return do_split (converter, getCurrentValue());
    }

    public SystemDataType getDataType() {
        return dataType;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    /**
     * Return default value as a list of strings by splitting it on commas
     */
    @Transient
    public List <String> getDefaultValueList() {
        return do_split (getDefaultValue());
    }

    /**
     * Return default value as a list of objects by converting each comma-separated string to some other object type
     */
    @Transient
    public <T> List <T> getDefaultValueList (final Function <String, T> converter) {
        return do_split (converter, getDefaultValue());
    }

    public Integer getId() {
        return id;
    }

    public SystemItemType getItemClass() {
        return itemClass;
    }

    public String getItemName() {
        return itemName;
    }

    public String getRange() {
        return range;
    }

    public String getUnits() {
        return units;
    }

    public Boolean getClientStorageForbidden() {
        return clientStorageForbidden;
    }

    public SystemValidationType getSystemValidationType() {
        return systemValidationType;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public void setCurrentValue(String aCurrentValue) {
        currentValue = aCurrentValue;
    }

    public void setDataType(SystemDataType aDataType) {
        dataType = aDataType;
    }

    public void setDefaultValue(String aDefaultValue) {
        defaultValue = aDefaultValue;
    }

    public void setId(Integer aId) {
        id = aId;
    }

    public void setItemClass(SystemItemType aItemClass) {
        itemClass = aItemClass;
    }

    public void setItemName(String aItemName) {
        itemName = aItemName;
    }

    public void setRange(String aRange) {
        range = aRange;
    }

    public void setUnits(String aUnits) {
        units = aUnits;
    }

    public void setClientStorageForbidden(Boolean clientStorageForbidden) {
        this.clientStorageForbidden = clientStorageForbidden;
    }

    public void setSystemValidationType(SystemValidationType systemValidationType) {
        this.systemValidationType = systemValidationType;
    }

    @Override
    public String toString() {
        return "SystemConfiguration [id=" + id + ", itemName=" + itemName + ", itemClass=" + itemClass + ", dataType="
                + dataType + ", units=" + units + ", range=" + range + ", defaultValue=" + defaultValue
                + ", currentValue=" + currentValue + "]";
    }

    // ----------------------- private ------------------------

    final static Pattern RE_COMMA = Pattern.compile ("\\s*,+\\s*");

    private List <String> do_split (final String strValue) {
        return do_split (strValue, RE_COMMA);
    }
    private List <String> do_split (final String strValue, final Pattern p) {
        if (strValue != null) {
            final String[] parts = p.split (strValue, 0);
            if (parts != null) {
                final ArrayList<String> result = new ArrayList<> (parts.length);
                for (final String part: parts) {
                    if (part != null) {
                        final String t = part.trim();
                        if (!t.isEmpty()) {
                            result.add (t);
                        }
                    }
                }
                return result;
            }
        }
        return null;
    }

    private <T> List <T> do_split (final Function <String, T> converter, final String strValue, final Pattern p) {
        final List <String> strList = do_split (strValue, p);
        if (strList != null) {
            return strList.stream().map (converter).collect(Collectors.toList());
        }
        return null;
    }

    private <T> List <T> do_split (final Function <String, T> converter, final String strValue) {
        return do_split (converter, strValue, RE_COMMA);
    }
}
