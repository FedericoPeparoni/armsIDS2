package ca.ids.abms.modules.billings.utility;

import ca.ids.abms.config.db.FiltersSpecification;
import ca.ids.abms.modules.billings.BillingLedger;
import ca.ids.abms.modules.common.enumerators.InvoiceType;
import edu.emory.mathcs.backport.java.util.Collections;

import javax.persistence.criteria.*;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BillingLedgerExportFilterSpecification extends FiltersSpecification<BillingLedger> {

    private final Boolean exported;

    private final List<String> typeSupport;
    
    private final boolean onlyAccountsWithAnyUsers;
    
    private final Integer onlyAccountsOfUserId;
    
    private final String flightIdOrRegistration;

    public BillingLedgerExportFilterSpecification(final Builder builder, final Boolean exported,
                                                  final List<InvoiceType> typeSupport, boolean onlyAccountsWithAnyUsers, Integer onlyAccountsOfUserId,
                                                  String flightIdOrRegistration) {
        super(builder);
        this.exported = exported;
        this.typeSupport = resolveTypeSupport(typeSupport);
        this.onlyAccountsWithAnyUsers = onlyAccountsWithAnyUsers;
        this.onlyAccountsOfUserId = onlyAccountsOfUserId;
        this.flightIdOrRegistration = flightIdOrRegistration;
    }
    
    public BillingLedgerExportFilterSpecification(final Builder builder, final Boolean exported,
            final List<InvoiceType> typeSupport) {
    	this (builder, exported, typeSupport, false, null,null);
    }

    @Override
    public Predicate toPredicate(Root<BillingLedger> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
    	
    	// List of top-level predicates that will be AND-ed together at the end
    	final List <Predicate> andList = new ArrayList<>(3);
    	
        // general filter useful for search text and any other generic filter logic
        Predicate genericFilter = super.toPredicate(root, query, builder);
        if (genericFilter != null) {
        	andList.add (genericFilter);
        }
        
        // add exported filter if exported param set
    	if (this.exported != null) {
    		andList.add (isExported (root, builder));
    		andList.add (isPublishedOrPaid (root, builder));
    		andList.add (isTypeSupport (root, builder));
    	}
    	
    	// add self-care user/account filters:
    	//   billing_ledgers of accounts associated with a specific user
    	if (this.onlyAccountsOfUserId != null) {
    		Path<Integer> path = root.join("account").join ("accountUsers").get ("id");
    		andList.add (builder.equal (path, onlyAccountsOfUserId));
    	}
    	//   billing_ledgers of accounts associated with any user at all - at least one, but it doesn't matter which
    	else if (this.onlyAccountsWithAnyUsers) {
    		Path<Integer> path = root.join("account").join ("accountUsers").get ("id");
    		andList.add (builder.isNotNull (path));
    		// We may get multiple hits for the same billing ledger record, because its account may
    		// be associated with multiple users -- "distinct" will remove the duplicates
    		query.distinct (true);
    	}
    	
    	// add filter by flightId or registration number
    	if(StringUtils.isNotBlank(this.flightIdOrRegistration)) {
    		String filter = flightIdOrRegistration + "%";
      		Path<Integer> pathEnroute = root.joinSet("enrouteFlightMovements", JoinType.LEFT);
    		Path<Integer> pathPassenger = root.joinSet("passengerFlightMovements", JoinType.LEFT);
    		Path<Integer> pathOther = root.joinSet("otherFlightMovements", JoinType.LEFT);
    		
    		andList.add (builder.or(isFlightIdOrRegistration(pathEnroute, builder, filter),
    				isFlightIdOrRegistration(pathPassenger, builder, filter),
    				isFlightIdOrRegistration(pathOther, builder, filter)));
    		query.distinct (true);
    	}
    	
        // Combine all filters with AND
        if (andList.isEmpty())
        	return null;
        return builder.and (andList.toArray(new Predicate[0]));
    }

    private Predicate isFlightIdOrRegistration(Path<Integer> path,CriteriaBuilder builder,String flightIdOrRegistration) {   	    
    	return builder.or(
    			builder.like(path.get("flightId"), flightIdOrRegistration),
    			builder.like(path.get("item18RegNum"), flightIdOrRegistration)
        		 ); 
    }
    private Predicate isExported(Root<BillingLedger> root, CriteriaBuilder builder) {
        Path<Boolean> path = root.get(ATTRIBUTE.EXPORTED);
        return exported
            ? builder.isTrue(path)
            : builder.isFalse(path);
    }

    private Predicate isTypeSupport(Root<BillingLedger> root, CriteriaBuilder builder) {
        Path<String> path = root.get(ATTRIBUTE.INVOICE_TYPE);
        return builder.isTrue(path.in(typeSupport));
    }

    private Predicate isPublishedOrPaid(Root<BillingLedger> root, CriteriaBuilder builder) {
        Path<String> path = root.get(ATTRIBUTE.INVOICE_STATE_TYPE);
        return builder.or(
            builder.equal(path, "PUBLISHED"),
            builder.equal(path, "PAID"));
    }

    @SuppressWarnings("unchecked")
	private List<String> resolveTypeSupport(List<InvoiceType> types) {
        return types == null ? Collections.emptyList() : types.stream()
            .map(InvoiceType::toValue)
            .collect(Collectors.toList());
    }

    private class ATTRIBUTE {

        private static final String EXPORTED = "exported";

        private static final String INVOICE_TYPE = "invoiceType";

        private static final String INVOICE_STATE_TYPE = "invoiceStateType";
        
    }
}
