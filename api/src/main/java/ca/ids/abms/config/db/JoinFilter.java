package ca.ids.abms.config.db;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;

public class JoinFilter<T> {

    private final String joinName;

    private final String fieldName;

    private String secondFieldName;

    private final CriteriaType criteriaType;

    private final T[] arguments;

    public static JoinFilter<String> equal(final String joinName, final String fieldName, final String argument) {
        return new JoinFilter<>(joinName, fieldName, CriteriaType.EQUAL, argument);
    }

    public static JoinFilter<String> notEqual(final String joinName, final String fieldName, final String argument) {
        return new JoinFilter<>(joinName, fieldName, CriteriaType.NOT_EQUAL, argument);
    }

    public static JoinFilter<Boolean> equal(final String joinName, final String fieldName, final Boolean argument) {
        return new JoinFilter<>(joinName, fieldName, CriteriaType.EQUAL, argument);
    }

    public static JoinFilter<Boolean> notEqual(final String joinName, final String fieldName, final Boolean argument) {
        return new JoinFilter<>(joinName, fieldName, CriteriaType.NOT_EQUAL, argument);
    }

    public static JoinFilter<Integer> equal(final String joinName, final String fieldName, final int number) {
        return new JoinFilter<>(joinName, fieldName, CriteriaType.EQUAL, number);
    }

    public static JoinFilter<Integer> equal(final String joinName, final String fieldName, final String secondFieldName, final int number) {
        return new JoinFilter<>(joinName, fieldName, secondFieldName, CriteriaType.EQUAL_SECOND, number);
    }

    public static JoinFilter<Integer> notEqual(final String joinName, final String fieldName, final int number) {
        return new JoinFilter<>(joinName, fieldName, CriteriaType.NOT_EQUAL, number);
    }

    public static JoinFilter<Double> greaterThan(final String joinName, final String fieldName, final double number) {
        return new JoinFilter<>(joinName, fieldName, CriteriaType.GREATER_THAN, number);
    }

    public static JoinFilter<Double> greaterThanDistinct(final String joinName, final String fieldName, final double number) {
        return new JoinFilter<>(joinName, fieldName, CriteriaType.GREATER_THAN_DISTINCT, number);
    }

    public static JoinFilter<LocalDateTime> greaterThanOrEqualTo(final String joinName, final String fieldName, final LocalDateTime dateTime) {
        return new JoinFilter<>(joinName, fieldName, CriteriaType.GREATER_OR_EQUAL_THAN_TIME, dateTime);
    }

    public static JoinFilter<LocalDateTime> lessThanOrEqualTo(final String joinName, final String fieldName, final LocalDateTime dateTime) {
        return new JoinFilter<>(joinName, fieldName, CriteriaType.LESS_OR_EQUAL_THAN_TIME, dateTime);
    }

    public static JoinFilter<LocalDateTime> lessThanDistinct(final String joinName, final String fieldName, final LocalDateTime date) {
        return new JoinFilter<>(joinName, fieldName, CriteriaType.LESS_THAN_DISTINCT, date);
    }

    public static JoinFilter<Boolean> isNotNull(final String joinName, final String fieldName) {
        return new JoinFilter<>(joinName, fieldName, CriteriaType.NOT_NULL, true);
    }

    public static JoinFilter<Boolean> isNotNullDistinct(final String joinName, final String fieldName) {
        return new JoinFilter<>(joinName, fieldName, CriteriaType.NOT_NULL_DISTINCT, true);
    }

    public static JoinFilter<Collection> notEmpty(final String joinName, final String fieldName) {
        return new JoinFilter<>(joinName, fieldName, CriteriaType.IS_NOT_EMPTY);
    }

    public static JoinFilter<Collection> in(final String joinName, final String fieldName, final Collection list) {
        return new JoinFilter<>(joinName, fieldName, CriteriaType.IN, list);
    }

    private JoinFilter(final String joinName, final String fieldName, final CriteriaType criteriaType, final T ... arguments)
        throws IllegalArgumentException {
        assert (joinName != null && fieldName != null && criteriaType != null && arguments != null && arguments.length > 0);

        this.joinName = joinName;
        this.fieldName = fieldName;
        this.criteriaType = criteriaType;
        this.arguments = Arrays.copyOf(arguments, arguments.length);
    }

    private JoinFilter(final String joinName, final String fieldName, final String secondFieldName, final CriteriaType criteriaType, final T ... arguments)
        throws IllegalArgumentException {
        assert (joinName != null && fieldName != null && criteriaType != null && arguments != null && arguments.length > 0);

        this.joinName = joinName;
        this.fieldName = fieldName;
        this.secondFieldName = secondFieldName;
        this.criteriaType = criteriaType;
        this.arguments = Arrays.copyOf(arguments, arguments.length);
    }

    public String getJoinName() {
        return joinName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getSecondFieldName() {
        return secondFieldName;
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
}
