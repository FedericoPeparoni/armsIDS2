package ca.ids.abms.modules.plugins.utility;

import ca.ids.abms.config.db.FiltersSpecification;
import ca.ids.abms.modules.plugins.Plugin;

import javax.persistence.criteria.*;

public class PluginFiltersSpecification extends FiltersSpecification<Plugin> {

    private static final String VISIBLE_FIELD_NAME = "visible";

    private static final String SITE_FIELD_NAME = "site";

    private final String site;

    private final Boolean visible;

    public PluginFiltersSpecification(final Builder builder, final Boolean visible, final String site) {
        super(builder);
        this.site = site;
        this.visible = visible;
    }

    @Override
    public Predicate toPredicate(Root<Plugin> root, CriteriaQuery<?> query, CriteriaBuilder builder) {

        Path<Boolean> visiblePath = root.get(VISIBLE_FIELD_NAME);
        Path<String> sitePath = root.get(SITE_FIELD_NAME);

        // add visibility filter if visibility param set
        // filter out client specific plugins on visible only
        // always show non client specific plugins when visible is false
        Predicate pluginFilter = null;
        if (visible != null && visible) {
            pluginFilter = builder.and(builder.isTrue(visiblePath), builder.or(
                builder.isNull(sitePath), builder.equal(sitePath, site)));
        } else if (visible != null) {
            pluginFilter = builder.or(builder.isFalse(root.get(VISIBLE_FIELD_NAME)), builder.and(
                builder.isTrue(root.get(VISIBLE_FIELD_NAME)), builder.notEqual(root.get(SITE_FIELD_NAME), site)));
        }

        // general filter useful for search text and any other generic filter logic
        Predicate genericFilter = super.toPredicate(root, query, builder);

        // return combination of generic and plugin filter if generic not null
        // else return only plugin filter predicate
        if (genericFilter != null && pluginFilter != null)
            return builder.and(genericFilter, pluginFilter);
        else
            return genericFilter != null ? genericFilter : pluginFilter;
    }
}
