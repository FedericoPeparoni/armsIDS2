package ca.ids.abms.modules.transactions.utility;

import ca.ids.abms.config.db.FiltersSpecification;
import ca.ids.abms.modules.transactions.Transaction;
import ca.ids.abms.modules.transactions.TransactionExportSupport;
import ca.ids.abms.modules.transactions.TransactionPaymentMechanism;

import javax.persistence.criteria.*;
import java.util.List;

public class TransactionExportFilterSpecification extends FiltersSpecification<Transaction> {

    private final Boolean exported;

    private final TransactionExportSupport exportSupport;

    private final List<TransactionPaymentMechanism> mechanismSupport;

    public TransactionExportFilterSpecification(
        final Builder builder, final Boolean exported, final TransactionExportSupport exportSupport,
        final List<TransactionPaymentMechanism> mechanismSupport
    ) {
        super(builder);
        this.exported = exported;
        this.exportSupport = exportSupport;
        this.mechanismSupport = mechanismSupport;
    }

    @Override
    public Predicate toPredicate(Root<Transaction> root, CriteriaQuery<?> query, CriteriaBuilder builder) {

        // add exported filter if exported param set
        Predicate exportedFilter = exported != null ? exportPredicate(root, query, builder) : null;

        // general filter useful for search text and any other generic filter logic
        Predicate genericFilter = super.toPredicate(root, query, builder);

        // return combination of generic and exported filter if generic not null
        // else return only exported filter predicate
        if (genericFilter != null && exportedFilter != null)
            return builder.and(genericFilter, exportedFilter);
        else
            return genericFilter != null ? genericFilter : exportedFilter;
    }

    private Predicate exportPredicate(Root<Transaction> root, CriteriaQuery<?> query, CriteriaBuilder builder) {

        // set distinct true to prevent duplicates from payments join
        query.distinct(true);

        // define filter for adjustment or non-adjustment as different logic for each
        Predicate filter = builder.or(isAdjustmentExported(root, builder), isNonAdjustmentExported(root, builder));

        // must limit to transaction credit types only and supported payment mechanism
        filter = builder.and(filter, isCredit(root, builder), isMechanismSupport(root, builder));

        return filter;
    }

    private Predicate isCredit(Root<Transaction> root, CriteriaBuilder builder) {
        Path<String> path = root.join(ATTRIBUTE.TRANSACTION_TYPE).get(ATTRIBUTE.TRANSACTION_TYPE_NAME);
        return builder.equal(builder.lower(path), "credit");
    }

    private Predicate isMechanismSupport(Root<Transaction> root, CriteriaBuilder builder) {
        Path<TransactionPaymentMechanism> path = root.get(ATTRIBUTE.PAYMENT_MECHANISM);
        return builder.isTrue(path.in(mechanismSupport));
    }

    private Predicate isAdjustmentExported(Root<Transaction> root, CriteriaBuilder builder) {

        // determine if adjustment is exported depending on export support provided
        Predicate isAdjustmentExported;
        if (exportSupport.getCreditNotes() && exportSupport.getPayments()) {
            isAdjustmentExported = exported
                ? builder.and(isTransactionExported(root, builder), arePaymentsExported(root, builder))
                : builder.or(isTransactionExported(root, builder), arePaymentsExported(root, builder));
        } else if (exportSupport.getCreditNotes()) {
            isAdjustmentExported = isTransactionExported(root, builder);
        } else if (exportSupport.getPayments()) {
            isAdjustmentExported = arePaymentsExported(root, builder);
        } else {
            isAdjustmentExported = builder.disjunction();
        }

        return builder.and(isAdjustmentExported,
            builder.equal(root.get(ATTRIBUTE.PAYMENT_MECHANISM), TransactionPaymentMechanism.adjustment));
    }

    private Predicate isNonAdjustmentExported(Root<Transaction> root, CriteriaBuilder builder) {

        // determine if adjustment is exported depending on export support provided
        Predicate isNonAdjustmentExported;
        if (exportSupport.getPayments()) {
            isNonAdjustmentExported = arePaymentsExported(root, builder);
        } else {
            isNonAdjustmentExported = builder.disjunction();
        }

        return builder.and(
            isNonAdjustmentExported,
            builder.notEqual(root.get(ATTRIBUTE.PAYMENT_MECHANISM), TransactionPaymentMechanism.adjustment));
    }

    private Predicate isTransactionExported(Root<Transaction> root, CriteriaBuilder builder) {
        Path<Boolean> path = root.get(ATTRIBUTE.EXPORTED);
        return exported
            ? builder.isTrue(path)
            : builder.isFalse(path);
    }

    private Predicate arePaymentsExported(Root<Transaction> root, CriteriaBuilder builder) {
        Path<Boolean> path = root.joinSet(ATTRIBUTE.TRANSACTION_PAYMENTS)
            .get(ATTRIBUTE.TRANSACTION_PAYMENTS_EXPORTED);

        return exported
            ? builder.isTrue(path)
            : builder.isFalse(path);
    }

    private class ATTRIBUTE {

        private static final String EXPORTED = "exported";

        private static final String PAYMENT_MECHANISM = "paymentMechanism";

        private static final String TRANSACTION_PAYMENTS = "transactionPayments";

        private static final String TRANSACTION_PAYMENTS_EXPORTED = "exported";

        private static final String TRANSACTION_TYPE = "transactionType";

        private static final String TRANSACTION_TYPE_NAME = "name";
    }
}
