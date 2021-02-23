package ca.ids.abms.modules.charges;

import ca.ids.abms.config.db.FiltersSpecification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class OrphanPassengerServiceChargeReturnFilter extends FiltersSpecification<PassengerServiceChargeReturn>{

    public static final String QUERY_STRING_ATTRIBUTE = "filter-orphan-returns";
    private final List<Integer> list;
    private final boolean orphanFilter;

    static final String QUERY = "select service.id from passenger_service_charge_returns as service " +
        "left outer join flight_movements as movement on service.flight_id = movement.flight_id " +
        "and movement.date_of_flight >= service.day_of_flight\\:\\:date " +
        "and movement.date_of_flight < (service.day_of_flight\\:\\:date + '1 day'\\:\\:interval) " +
        "where movement.id is null";

    OrphanPassengerServiceChargeReturnFilter(final FiltersSpecification.Builder builder, List<Integer> list, boolean orphanFilter) {
        super(builder);
        this.list = list;
        this.orphanFilter = orphanFilter;
    }

    @Override
    public Predicate toPredicate(Root<PassengerServiceChargeReturn> root, CriteriaQuery<?> query, CriteriaBuilder builder) {

        // List of top-level predicates that will be AND-ed together at the end
        final List <Predicate> andList = new ArrayList<>();

        // general filter useful for search text and any other generic filter logic
        Predicate genericFilter = super.toPredicate(root, query, builder);
        if (genericFilter != null) {
            andList.add (genericFilter);
        }

        if (this.orphanFilter && !this.list.isEmpty()) {
            andList.add (builder.isTrue(root.get("id").in(list)));
        }

        // Combine all filters with AND
        if (andList.isEmpty())
            return null;
        return builder.and (andList.toArray(new Predicate[0]));
    }
}
