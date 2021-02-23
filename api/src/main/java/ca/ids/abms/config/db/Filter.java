package ca.ids.abms.config.db;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;

public class Filter<T> {

    private String fieldName = null;

    private String secondName = null;

    private LocalDateTime date = null;

    private final CriteriaType criteriaType;

    private final T[] arguments;

    public static Filter<Boolean> isTrue(final String fieldName) {
        return new Filter<>(fieldName, CriteriaType.EQUAL, Boolean.TRUE);
    }

    public static Filter<Boolean> isFalse(final String fieldName) {
        return new Filter<>(fieldName, CriteriaType.NOT_EQUAL, Boolean.TRUE);
    }

    public static Filter<String> like(final String fieldName, final String argument) {
        return new Filter<>(fieldName, CriteriaType.LIKE, argument);
    }

    public static Filter<String> equals(final String fieldName, final String argument) {
        return new Filter<>(fieldName, CriteriaType.EQUAL, argument);
    }

    public static Filter<String> orEqual(final String fieldName, final String firstArgument, final String secondArgument) {
        return new Filter<>(fieldName, CriteriaType.OR_EQUAL, firstArgument, secondArgument);
    }

    public static Filter<String> in(final String fieldName, final String[] strings) {
        return new Filter<>(fieldName, CriteriaType.IN, strings);
    }

    public static Filter<Number> equalOr(final String fieldName, final String secondName, final Number argument) {
        return new Filter<>(fieldName, secondName, CriteriaType.OR, argument);
    }

    public static Filter<Enum> orEqual(final String fieldName, final Enum firstArgument, final Enum secondArgument) {
        return new Filter<>(fieldName, CriteriaType.OR_EQUAL, firstArgument, secondArgument);
    }

    public static Filter<String> notEqual(final String fieldName, final String argument) {
        return new Filter<>(fieldName, CriteriaType.NOT_EQUAL, argument);
    }

    public static Filter<Enum> like(final String fieldName, final Enum argument) {
        return new Filter<>(fieldName, CriteriaType.LIKE, argument);
    }

    public static Filter<Enum> equals(final String fieldName, final Enum argument) {
        return new Filter<>(fieldName, CriteriaType.EQUAL, argument);
    }

    public static Filter<Enum> notEqual(final String fieldName, final Enum argument) {
        return new Filter<>(fieldName, CriteriaType.NOT_EQUAL, argument);
    }

    public static Filter<Number> like(final String fieldName, final Number argument) {
        return new Filter<>(fieldName, CriteriaType.LIKE, argument);
    }

    public static Filter<Number> equals(final String fieldName, final Number argument) {
        return new Filter<>(fieldName, CriteriaType.EQUAL, argument);
    }

    public static Filter<Number> notEqual(final String fieldName, final Number argument) {
        return new Filter<>(fieldName, CriteriaType.NOT_EQUAL, argument);
    }

    public static Filter<LocalDateTime> equals(final String fieldName, final LocalDateTime argument) {
        return new Filter<>(fieldName, CriteriaType.EQUAL, argument);
    }

    public static Filter<Boolean> equals(final String fieldName, final Boolean argument) {
        return new Filter<>(fieldName, CriteriaType.EQUAL, argument);
    }

    public static Filter<LocalDateTime> notEqual(final String fieldName, final LocalDateTime argument) {
        return new Filter<>(fieldName, CriteriaType.NOT_EQUAL, argument);
    }

    public static Filter included(final String fieldName, final LocalDateTime startAt, final LocalDateTime endAt) {
        return new Filter<>(fieldName, CriteriaType.BETWEEN, startAt, endAt);
    }

    public static Filter included(final LocalDateTime date, final String fieldName, final String fieldName2) {
        return new Filter<>(date, CriteriaType.BETWEEN_COLUMNS, fieldName, fieldName2);
    }

    public static Filter notIncluded(final LocalDateTime date, final String fieldName, final String fieldName2) {
        return new Filter<>(date, CriteriaType.NOT_BETWEEN_COLUMNS, fieldName, fieldName2);
    }

    public static Filter<Double> greaterThan(final String fieldName, final double number) {
        return new Filter<>(fieldName, CriteriaType.GREATER_THAN, number);
    }

    public static Filter<Double> greaterThanOrEqualTo(final String fieldName, final double number) {
        return new Filter<>(fieldName, CriteriaType.GREATER_OR_EQUAL_THAN, number);
    }

    public static Filter<LocalDateTime> greaterThanOrEqualTo(final String fieldName, final LocalDateTime number) {
        return new Filter<>(fieldName, CriteriaType.GREATER_OR_EQUAL_THAN_TIME, number);
    }

    public static Filter<LocalDateTime> greaterThan(final String fieldName, final LocalDateTime argument) {
        return new Filter<>(fieldName, CriteriaType.GREATER_THAN_DATE_TIME, argument);
    }

    public static Filter<Double> lessThan(final String fieldName, final double number) {
        return new Filter<>(fieldName, CriteriaType.LESS_THAN, number);
    }

    public static Filter<LocalDateTime> lessThan(final String fieldName, final LocalDateTime argument) {
        return new Filter<>(fieldName, CriteriaType.LESS_THAN_DATE_TIME, argument);
    }

    public static Filter<Double> lessThanOrEqualTo(final String fieldName, final double number) {
        return new Filter<>(fieldName, CriteriaType.LESS_OR_EQUAL_THAN, number);
    }

    public static Filter<LocalDateTime> lessThanOrEqualTo(final String fieldName, final LocalDateTime number) {
        return new Filter<>(fieldName, CriteriaType.LESS_OR_EQUAL_THAN_TIME, number);
    }

    public static Filter<Collection> notEmpty(final String fieldName) {
        return new Filter<>(fieldName, CriteriaType.IS_NOT_EMPTY);
    }

    public static Filter<Number> make(final String fieldName, final CriteriaType criteriaType, final Number ... arguments) {
        return new Filter<>(fieldName, criteriaType, arguments);
    }

    public static Filter<String> make(final String fieldName, final CriteriaType criteriaType, final String ... arguments) {
        return new Filter<>(fieldName, criteriaType, arguments);
    }

    public static Filter<LocalDateTime> make(final String fieldName, final CriteriaType criteriaType, final LocalDateTime ... arguments) {
        return new Filter<>(fieldName, criteriaType, arguments);
    }

    public static Filter<Enum> make(final String fieldName, final CriteriaType criteriaType, final Enum ... arguments) {
        return new Filter<>(fieldName, criteriaType, arguments);
    }

    public static Filter<Boolean> isNotNull(final String fieldName) {
        return new Filter<>(fieldName, CriteriaType.NOT_NULL, true);
    }

    public static Filter<Boolean> isNull(final String fieldName) {
        return new Filter<>(fieldName, CriteriaType.IS_NULL, true);
    }

    private Filter(final String fieldName, final CriteriaType criteriaType, final T ... arguments)
        throws IllegalArgumentException {
        assert (fieldName != null && criteriaType != null && arguments != null && arguments.length > 0);

        this.fieldName = fieldName;
        this.criteriaType = criteriaType;
        this.arguments = Arrays.copyOf(arguments, arguments.length);

        this.validate();
    }

    private Filter(final String fieldName, final String secondName, final CriteriaType criteriaType, final T ... arguments) {
        assert (fieldName != null && criteriaType != null && arguments != null && arguments.length > 0);

        this.fieldName = fieldName;
        this.secondName = secondName;
        this.criteriaType = criteriaType;
        this.arguments = Arrays.copyOf(arguments, arguments.length);

        this.validate();
    }

    private Filter(final LocalDateTime date, final CriteriaType criteriaType, final T ... arguments) {
        assert (date != null && criteriaType != null && arguments != null && arguments.length > 0);

        this.date = date;
        this.criteriaType = criteriaType;
        this.arguments = Arrays.copyOf(arguments, arguments.length);

        this.validate();
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getSecondName() {
        return secondName;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public CriteriaType getCriteriaType() {
        return criteriaType;
    }

    public T getFirstArgument() {
        return arguments[0];
    }

    public T getSecondArgument() {
        assert arguments.length > 1;
        return arguments[1];
    }

    public T[] getArgumetns() {
        return this.arguments;
    }

    private void validate() {
        if (criteriaType.equals(CriteriaType.LIKE) && !(arguments[0] instanceof String)) {
            throw new IllegalArgumentException ("The argument must be a String object for the \"LIKE\" criteria");
        } else if (criteriaType.equals(CriteriaType.BETWEEN)) {
            if (arguments.length < 2) {
                throw new IllegalArgumentException ("Two arguments must be for the \"BETWEEN\" criteria");
            } else if (!(arguments[0] instanceof LocalDateTime && arguments[1] instanceof  LocalDateTime)) {
                throw new IllegalArgumentException ("The arguments must be LocalDateTime objects for the \"BETWEEN\" criteria");
            }
        }
    }
}
