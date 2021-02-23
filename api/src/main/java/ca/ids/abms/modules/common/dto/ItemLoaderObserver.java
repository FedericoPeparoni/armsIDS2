package ca.ids.abms.modules.common.dto;

import ca.ids.abms.modules.common.enumerators.ItemLoaderResult;

public interface ItemLoaderObserver {
    void update (Object o, ItemLoaderResult result);
}
