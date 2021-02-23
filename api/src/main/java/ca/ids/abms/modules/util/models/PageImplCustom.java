package ca.ids.abms.modules.util.models;

import java.util.List;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

public class PageImplCustom<T> extends PageImpl<T>{

    private long totalItems;

    private static final long serialVersionUID = 1L;

    public PageImplCustom(List<T> aContent) {
        super(aContent);
    }
    
    public PageImplCustom(List<T> aContent, Pageable pageable, long totalFiltered, long aTotalItems) {
        super(aContent, pageable, totalFiltered);
        totalItems = aTotalItems;
    }
    
    public long getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(long aTotalItems) {
        totalItems = aTotalItems;
    }
}
