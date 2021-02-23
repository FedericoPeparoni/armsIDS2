package ca.ids.abms.config.db;

import ca.ids.abms.modules.translation.Translation;
import ca.ids.abms.util.EnumUtils;
import ca.ids.abms.util.EnumWithLabels;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import javax.persistence.criteria.CriteriaBuilder.In;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.time.LocalDateTime;
import java.util.*;

public class FiltersSpecification<T> implements Specification<T> {

    private final ArrayList<Filter<?>> filters;

    private final ArrayList<JoinFilter<?>> joinFilters;

    private final String textToSearch;

    // True if @SearchableEntity is a Collection
    private Boolean isCollection;

    protected FiltersSpecification(final Builder builder) {
        this.filters = builder.filters;
        this.joinFilters = builder.joinFilters;
        this.textToSearch = builder.textToSearch;
        this.isCollection = false;
    }

    public boolean hasFilters () {
        return (CollectionUtils.isNotEmpty(this.filters) || CollectionUtils.isNotEmpty(this.joinFilters) || this.textToSearch != null);
    }

    public static class Builder {

        private final ArrayList<Filter<?>> filters;

        private ArrayList<JoinFilter<?>> joinFilters;

        private String textToSearch;

        public Builder() {
            this.filters = new ArrayList<>();
            this.joinFilters = new ArrayList<>();
        }

        public Builder(int howManyFilters) {
            this.filters = new ArrayList<>(howManyFilters);
        }

        public Builder(final String textToSearch) {
            this.filters = new ArrayList<>();
            this.textToSearch = textToSearch;
        }

        public Builder(final Filter<?> filter) {
            this.filters = new ArrayList<>(1);
            this.filters.add(filter);
        }

        public Builder(final FiltersList filters) {
            this.filters = filters.list();
        }

        public Builder(final String textToSearch, final Filter<?> filter) {
            this(filter);
            this.textToSearch = textToSearch;
        }

        public Builder(final String textToSearch, final FiltersList filters) {
            this.filters = filters.list();
            this.textToSearch = textToSearch;
        }

        public Builder restrictOn (final Filter<?> filter) {
            assert filter != null;
            this.filters.add(filter);
            return this;
        }

        public Builder restrictOn (final JoinFilter<?> joinFilter) {
            assert joinFilter != null;
            this.joinFilters.add(joinFilter);
            return this;
        }

        public Builder lookFor (final String textToSearch) {
            this.textToSearch = textToSearch;
            return this;
        }

        public <T> FiltersSpecification<T> build() {
            return new FiltersSpecification<>(this);
        }
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {

        ArrayList<Predicate> predicatesToBuild = new ArrayList<>();

        // add search predicate to list if exists
        Predicate searchPredicate = toPredicateSearch(root, query, builder);
        if (searchPredicate != null)
            predicatesToBuild.add(searchPredicate);

        // add filter predicate to list if exists
        Predicate filterPredicate = toPredicateFilters(root, builder);
        if (filterPredicate != null)
            predicatesToBuild.add(filterPredicate);

        // add join filter predicate to list if exists
        Predicate joinFilterPredicate = toPredicateJoinFilters(root, query, builder);
        if (joinFilterPredicate != null)
            predicatesToBuild.add(joinFilterPredicate);

        // return list of predicates as AND predicate if exists
        if (CollectionUtils.isNotEmpty(predicatesToBuild))
            return builder.and(predicatesToBuild.toArray(new Predicate[predicatesToBuild.size()]));
        else
            return null;
    }

    /**
     * Build and return all searchable predicates for `textToSearch` value.
     */
    private Predicate toPredicateSearch(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {

        if (textToSearch == null || textToSearch.isEmpty())
            return null;

        List<Predicate> predicates = this.searchableBuilder(builder, root.getJavaType().getDeclaredFields(), root);

        // @SearchableEntity is a collection so must be distinct
        if (isCollection) {
            query.distinct(true);
        }

        if (CollectionUtils.isNotEmpty(predicates))
            return builder.or(predicates.toArray(new Predicate[predicates.size()]));
        else
            return null;
    }

    /**
     * Build and return all filter predicates.
     */
    private Predicate toPredicateFilters(Root<T> root, CriteriaBuilder builder) {

        if (CollectionUtils.isEmpty(filters))
            return null;

        final ArrayList<Predicate> predicates = new ArrayList<>(filters.size());
        for (final Filter filter : filters) {
            switch (filter.getCriteriaType()) {
                case NOT_EQUAL:
                    predicates.add(notEqualOrIsNotNull(builder, root.get(filter.getFieldName()), filter.getFirstArgument()));
                    break;
                case LIKE:
                    final String argument = "%" + filter.getFirstArgument() + '%';
                    predicates.add(builder.like(root.get(filter.getFieldName()), argument));
                    break;
                case EQUAL:
                    predicates.add(equalOrIsNull(builder, root.get(filter.getFieldName()), filter.getFirstArgument()));
                    break;
                case OR_EQUAL:
                    predicates.add(builder.or(
                        equalOrIsNull(builder, root.get(filter.getFieldName()), filter.getFirstArgument()),
                        equalOrIsNull(builder, root.get(filter.getFieldName()), filter.getSecondArgument())));
                    break;
                case OR:
                    predicates.add(builder.or(
                        equalOrIsNull(builder, root.get(filter.getFieldName()), filter.getFirstArgument()),
                        equalOrIsNull(builder, root.get(filter.getSecondName()), filter.getFirstArgument())));
                    break;
                case BETWEEN:
                    predicates.add(builder.between(root.get(filter.getFieldName()),
                        (LocalDateTime)filter.getFirstArgument(), (LocalDateTime)filter.getSecondArgument()));
                    break;
                case GREATER_THAN:
                    predicates.add(builder.greaterThan(root.get(filter.getFieldName()), (double)filter.getFirstArgument()));
                    break;
                case GREATER_THAN_DATE_TIME:
                    predicates.add(builder.greaterThan(root.get(filter.getFieldName()), (LocalDateTime)filter.getFirstArgument()));
                    break;
                case LESS_THAN:
                    predicates.add(builder.lessThan(root.get(filter.getFieldName()), (double)filter.getFirstArgument()));
                    break;
                case LESS_THAN_DATE_TIME:
                    predicates.add(builder.lessThan(root.get(filter.getFieldName()), (LocalDateTime)filter.getFirstArgument()));
                    break;
                case BETWEEN_COLUMNS:
                    predicates.add(builder.between(builder.literal(filter.getDate()),
                        root.get((String) filter.getFirstArgument()), root.get((String) filter.getSecondArgument())));
                    break;
                case NOT_BETWEEN_COLUMNS:
                    predicates.add(builder.not(builder.between(builder.literal(filter.getDate()),
                        root.get((String) filter.getFirstArgument()), root.get((String) filter.getSecondArgument()))));
                    break;
                case NOT_NULL:
                    predicates.add(builder.isNotNull(root.get(filter.getFieldName())));
                    break;
                case IS_NULL:
                    predicates.add(builder.isNull(root.get(filter.getFieldName())));
                    break;
                case IS_NOT_EMPTY:
                    predicates.add(builder.isNotEmpty(root.get(filter.getFieldName())));
                    break;
                case GREATER_OR_EQUAL_THAN:
                    predicates.add(builder.greaterThanOrEqualTo(root.get(filter.getFieldName()), (double)filter.getFirstArgument()));
                    break;
                case GREATER_OR_EQUAL_THAN_TIME:
                    predicates.add(builder.greaterThanOrEqualTo(root.get(filter.getFieldName()), (LocalDateTime)filter.getFirstArgument()));
                    break;
                case LESS_OR_EQUAL_THAN:
                    predicates.add(builder.lessThanOrEqualTo(root.get(filter.getFieldName()), (double)filter.getFirstArgument()));
                    break;
                case LESS_OR_EQUAL_THAN_TIME:
                    predicates.add(builder.lessThanOrEqualTo(root.get(filter.getFieldName()), (LocalDateTime)filter.getFirstArgument()));
                    break;
                case IN:
                    predicates.add(root.get(filter.getFieldName()).in(filter.getArgumetns()));
                default: // ignored
            }
        }

        if (CollectionUtils.isNotEmpty(predicates))
            return builder.and(predicates.toArray(new Predicate[predicates.size()]));
        else
            return null;
    }

    /**
     * Build and return all join filter predicates.
     */
    private Predicate toPredicateJoinFilters(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {

        if (CollectionUtils.isEmpty(joinFilters))
            return null;

        final ArrayList<Predicate> predicates = new ArrayList<>(joinFilters.size());
        for (final JoinFilter joinFilter : joinFilters) {
            switch (joinFilter.getCriteriaType()) {
                case EQUAL:
                    predicates.add(builder.equal(
                        root.join(joinFilter.getJoinName()).get(joinFilter.getFieldName()), joinFilter.getFirstArgument())
                    );
                    break;
                case EQUAL_SECOND:
                    predicates.add(builder.equal(
                        root.join(joinFilter.getJoinName()).join(joinFilter.getFieldName()).get(joinFilter.getSecondFieldName()), joinFilter.getFirstArgument())
                    );
                    break;
                case NOT_EQUAL:
                    predicates.add(builder.notEqual(
                        root.join(joinFilter.getJoinName()).get(joinFilter.getFieldName()), joinFilter.getFirstArgument())
                    );
                    break;
                case GREATER_THAN:
                    predicates.add(builder.greaterThan(
                        root.join(joinFilter.getJoinName()).get(joinFilter.getFieldName()), (double)joinFilter.getFirstArgument())
                    );
                    break;
                case GREATER_THAN_DISTINCT:
                    query.distinct(true);
                    predicates.add(builder.greaterThan(
                        root.join(joinFilter.getJoinName()).get(joinFilter.getFieldName()), (double)joinFilter.getFirstArgument())
                    );
                    break;
                case LESS_THAN_DISTINCT:
                    query.distinct(true);
                    predicates.add(builder.lessThan(
                        root.join(joinFilter.getJoinName()).get(joinFilter.getFieldName()), (LocalDateTime)joinFilter.getFirstArgument())
                    );
                    break;
                case NOT_NULL:
                    predicates.add(builder.isNotNull(
                        root.join(joinFilter.getJoinName()).get(joinFilter.getFieldName()))
                    );
                    break;
                case NOT_NULL_DISTINCT:
                    query.distinct(true);
                    predicates.add(builder.isNotNull(
                        root.join(joinFilter.getJoinName()).get(joinFilter.getFieldName()))
                    );
                    break;
                case IS_NOT_EMPTY:
                    predicates.add(builder.isNotEmpty(
                        root.join(joinFilter.getJoinName()).get(joinFilter.getFieldName())));
                    break;
                case IN:
                    predicates.add(root.join(joinFilter.getJoinName()).get(joinFilter.getFieldName()).in((ArrayList)joinFilter.getFirstArgument()));
                    break;
                case LESS_OR_EQUAL_THAN_TIME:
                    predicates.add(builder.lessThanOrEqualTo(
                        root.join(joinFilter.getJoinName()).get(joinFilter.getFieldName()), (LocalDateTime)joinFilter.getFirstArgument())
                    );
                    break;
                case GREATER_OR_EQUAL_THAN_TIME:
                    predicates.add(builder.greaterThanOrEqualTo(
                        root.join(joinFilter.getJoinName()).get(joinFilter.getFieldName()), (LocalDateTime)joinFilter.getFirstArgument())
                    );
                    break;
                default: // ignored
            }
        }

        if (CollectionUtils.isNotEmpty(predicates))
            return builder.and(predicates.toArray(new Predicate[predicates.size()]));
        else
            return null;
    }

    /**
     * Build predicates for searchable fields.
     *
     * @param builder criteria builder to pass onto `searchableTextBuilder`
     * @param fields fields to check for `@SearchableText` or `@SearchableEntity`
     * @param from table to search
     * @return predicates for `textToSearch`
     */
    private List<Predicate> searchableBuilder(CriteriaBuilder builder, Field[] fields, From from) {
        final List<Predicate> predicates = new ArrayList<>();
        for (final Field field : fields) {
            if (field.isAnnotationPresent(SearchableEntity.class) && Iterable.class.isAssignableFrom(field.getType())) {
                predicates.addAll(this.searchableIterableBuilder(builder, field, from));
                // @SearchableEntity is a Collection
                isCollection = true;
            }
            else if (field.isAnnotationPresent(SearchableEntity.class)) {
                SearchableEntity annotation = field.getDeclaredAnnotation(SearchableEntity.class);
                if (!annotation.searchableField().isEmpty()) {
                    predicates.addAll(this.searchableEntityBuilder(builder, field, from, annotation.searchableField()));
                } else {
                    predicates.addAll(this.searchableEntityBuilder(builder, field, from));
                }
            }
            else if (field.isAnnotationPresent(SearchableText.class)) {
                predicates.add(this.searchablePropertyBuilder(builder, from.get(field.getName()),
                    field.getAnnotation(SearchableText.class).exactMatch()));
            }
        }
        return predicates;
    }

    /**
     * Build predicates for a searchable entity. Should not be accessed directly and only called
     * from `searchableBuilder`.
     * Join type is specified as LEFT for cases when fields are NULL
     *
     * @param builder criteria builder to pass onto `searchableBuilder`
     * @param field field annotated with `@SearchableEntity`
     * @param from table to join entity too
     * @return predicates for `textToSearch`
     */
    private List<Predicate> searchableEntityBuilder(CriteriaBuilder builder, Field field, From from) {
        return this.searchableBuilder(builder, field.getType().getDeclaredFields(), from.join(field.getName(), JoinType.LEFT));
    }

    /**
     * Build predicates for a searchable field in a searchable entity. Should not be accessed directly and only called
     * from `searchableBuilder`.
     * Join type is specified as LEFT for cases when fields are NULL
     *
     * @param builder criteria builder to pass onto `searchableBuilder`
     * @param field field annotated with `@SearchableEntity`
     * @param from table to join entity too
     * @param searchableField field that should be searched in a searchable entity
     * @return predicates for `textToSearch`
     */
    private List<Predicate> searchableEntityBuilder(CriteriaBuilder builder, Field field, From from, String searchableField) {
        Field[] searchableFields = Arrays.stream(field.getType().getDeclaredFields()).filter(f -> f.getName().equals(searchableField)).toArray(Field[]::new);
        return this.searchableBuilder(builder, searchableFields, from.join(field.getName(), JoinType.LEFT));
    }

    /**
     * Build predicates for searchable entity list. Should not be accessed directly and only called
     * from `searchableBuilder`.
     *
     * @param builder criteria builder to pass onto `searchableBuilder`
     * @param field `Iterable.class` field annotated with `@SearchableEntity`
     * @param from table to join iterable too
     * @return predicates for `textToSearch`
     */
    private List<Predicate> searchableIterableBuilder(CriteriaBuilder builder, Field field, From from) {

        // get join based on field type
        Join join;
        if (field.getType() == Set.class)
            join = from.joinSet(field.getName(), JoinType.LEFT);
        else if (field.getType() == Collection.class)
            join = from.joinCollection(field.getName(), JoinType.LEFT);
        else if (field.getType() == List.class)
            join = from.joinList(field.getName(), JoinType.LEFT);
        else
            join = from.join(field.getName(), JoinType.LEFT);

        // get generic type from field,
        // this assume of type Iterable<?> where only one type argument possible
        ParameterizedType collectionType = (ParameterizedType) field.getGenericType();
        Class<?> genericType = (Class<?>) collectionType.getActualTypeArguments()[0];

        // return searchable builder predicates for generic type fields in join
        return this.searchableBuilder(builder, genericType.getDeclaredFields(), join);
    }

    /**
     * Build predicate for searchable property. Should not be accessed directory and only called
     * from `searchableBuilder`.
     *
     * @param builder criteria builder to use
     * @param expression expression to match
     * @param exactMatch true for exact match
     * @return predicate for `textToSearch`
     */
    private Predicate searchablePropertyBuilder(CriteriaBuilder builder, Expression<?> expression, boolean exactMatch) {
        // Handle enums specially
        if (expression.getJavaType().isEnum()) {
            @SuppressWarnings({ "unchecked", "rawtypes" })
            final Expression <? extends Enum> enumExpression = (Expression)expression;
            return this.searchableEnumPropertyBuilder(builder, enumExpression, exactMatch);
        }
        // Non-enums: match them simply as text
        if (exactMatch) {
            return builder.like(expression.as(String.class), textToSearch);
        } else {
            return builder.like(
                builder.lower(expression.as(String.class)),
                "%" + textToSearch.toLowerCase(Locale.US) + '%');
        }
    }

    /**
     * Create a predicate for an enum-type property. This will match enum label IDs, as well as their translations,
     * as well as any "labels" (plus translations) for enums that implement {@link EnumWithLabels} interface.
     */
    private <E extends Enum <?>> Predicate searchableEnumPropertyBuilder(CriteriaBuilder builder, Expression<E> expression, boolean exactMatch) {

        // Look for all enums that match our search text
        @SuppressWarnings({ "unchecked", "rawtypes" })
        final List <E> enumValues = findMatchingEnumValues ((Class)expression.getJavaType(), textToSearch, exactMatch);
        // If none found, create an empoty predicate (i.e., ignore this field)
        if (enumValues.isEmpty()) {
            return builder.or();
        }
        // Treat it as "enumProperti in ('VALUE1', 'VALUE2', ...)"
        final In<String> inPredicate = builder.in (expression.as (String.class));
        enumValues.stream().forEach(enumValue->inPredicate.value (enumValue.name()));
        return inPredicate;
    }

    /**
     * Return true if the given strings are equal, or one is contained within the other.
     */
    private static boolean matchSearchTerm (final String stringToCheck, final String searchTerm, final boolean exactMatch) {
        return (exactMatch && StringUtils.equals (stringToCheck, searchTerm))
                || (!exactMatch && StringUtils.contains (stringToCheck, searchTerm));
    }

    /**
     * Find all enum value that match the text we are looking for. We will try to match enum
     * member IDs, labels (for enums that implement the EnumWithLabels interface), and their translations.
     * Matching is not case-sensitive.
     */
    @SuppressWarnings({ "squid:S3776", "squid:S135" })
    private static <E extends Enum <E>> List <E> findMatchingEnumValues (Class<E> enumClass, final String text, boolean exactMatch) {
        final List<E> enumValues = EnumUtils.getEnumValues (enumClass);
        final List<E> result = new ArrayList<>();
        final String textLower = text.toLowerCase (Locale.US);
        for (final E enumValue: enumValues) {
            final String name = enumValue.name();
            final String nameLower = name.toLowerCase (Locale.US);
            // Match enum member ID
            if (matchSearchTerm (nameLower, textLower, exactMatch)) {
                result.add (enumValue);
                continue;
            }
            // Match translated member ID
            final String translatedName = Translation.getLangByToken(name);
            if (translatedName != null && !translatedName.equals (name)) {
                final String translatedNameLower = translatedName.toLowerCase (Locale.US);
                if (matchSearchTerm (translatedNameLower, textLower, exactMatch)) {
                    result.add (enumValue);
                    continue;
                }
            }

            // Get the labels (see EnumWithLabels interface)
            final List <String> labels = EnumUtils.getEnumLabels (enumValue);
            for (final String label: labels) {
                if (label == null) {
                    continue;
                }
                // Match label
                final String labelLower = label.toLowerCase (Locale.US);
                if (matchSearchTerm (labelLower, textLower, exactMatch)) {
                    result.add (enumValue);
                    break;
                }
                // Match translated label
                final String translatedLabel = Translation.getLangByToken (label);
                if (translatedLabel == null || label.equals (translatedLabel)) {
                    continue;
                }
                final String translatedLabelLower = translatedLabel.toLowerCase (Locale.US);
                if (matchSearchTerm (translatedLabelLower, textLower, exactMatch)) {
                    result.add (enumValue);
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Build predicate for equal or isNull for nullable arguments. This is required in situations
     * where argument is null and thus `builder.isNull` should be used in place of `builder.equal`.
     *
     * @param builder criteria builder to use
     * @param expression field that applies
     * @param argument nullable argument to validate
     * @return predicate for `equal` or `isNull
     */
    private Predicate equalOrIsNull(CriteriaBuilder builder, Expression<?> expression, Object argument) {
        if (argument == null)
            return builder.isNull(expression);
        else
            return builder.equal(expression, argument);
    }

    /**
     * Build predicate for notEqual or isNotNull for nullable arguments. This is required in situations
     * where argument is null and thus `builder.isNotNull` should be used in place of `builder.notEqual`.
     *
     * @param builder criteria builder to use
     * @param expression field that applies
     * @param argument nullable argument to validate
     * @return predicate for `notEqual` or `isNotNull`
     */
    private Predicate notEqualOrIsNotNull(CriteriaBuilder builder, Expression<?> expression, Object argument) {
        if (argument == null)
            return builder.isNotNull(expression);
        else
            return builder.notEqual(expression, argument);
    }
}
