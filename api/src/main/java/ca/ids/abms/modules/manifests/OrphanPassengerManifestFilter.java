package ca.ids.abms.modules.manifests;

import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.List;

public class OrphanPassengerManifestFilter {

    public static final String QUERY_STRING_ATTRIBUTE = "filter-orphan-manifests";

    public static final String QUERY = "select manifest.document_number from passenger_manifests as manifest " +
        "left outer join flight_movements as movement on manifest.flight_id = movement.flight_id " +
        "and movement.date_of_flight >= manifest.date_of_flight\\:\\:date " +
        "and movement.date_of_flight < (manifest.date_of_flight\\:\\:date + '1 day'\\:\\:interval) " +
        "where movement.id is null";

    public static Specification<PassengerManifest> isIn(List<Integer> filtered) {
        return new Specification<PassengerManifest>() {
            @Override
            public Predicate toPredicate(Root<PassengerManifest> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                return cb.isTrue(root.get("documentNumber").in(filtered));

            }
        };
    }
}
