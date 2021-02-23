package ca.ids.abms.modules.common.entities;

import java.io.Serializable;

public interface AbmsCrudEntity<I> extends Serializable {

    I getId();

    void setId(final I id);
}
