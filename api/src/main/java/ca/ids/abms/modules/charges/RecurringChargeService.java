package ca.ids.abms.modules.charges;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import ca.ids.abms.config.db.Filter;
import ca.ids.abms.config.db.FiltersSpecification;
import ca.ids.abms.config.db.JoinFilter;
import ca.ids.abms.modules.accounts.Account;
import ca.ids.abms.modules.accounts.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.ids.abms.config.error.ErrorConstants;
import ca.ids.abms.config.error.ExceptionFactory;
import ca.ids.abms.modules.common.enumerators.InvoiceCategory;
import ca.ids.abms.modules.util.models.ModelUtils;

@Service
@Transactional
public class RecurringChargeService {

    private static final Logger LOG = LoggerFactory.getLogger(RecurringChargeService.class);

    private final RecurringChargeRepository recurringChargeRepository;
    private final AccountRepository accountRepository;
    private final ServiceChargeCatalogueRepository serviceChargeCatalogueRepository;

    private static final String END_DATE = "endDate";

    public RecurringChargeService(final RecurringChargeRepository recurringChargeRepository,
                                  final AccountRepository accountRepository,
                                  final ServiceChargeCatalogueRepository serviceChargeCatalogueRepository) {
        this.accountRepository = accountRepository;
        this.recurringChargeRepository = recurringChargeRepository;
        this.serviceChargeCatalogueRepository = serviceChargeCatalogueRepository;
    }

    public RecurringCharge create(RecurringCharge recurringCharge) {
        LOG.debug("Request to save Recurring Charge : {}", recurringCharge);
        final Account account = accountRepository.getOne(recurringCharge.getAccount().getId());
        recurringCharge.setAccount(account);
        final ServiceChargeCatalogue serviceChargeCatalogue = serviceChargeCatalogueRepository.getOne(recurringCharge.getServiceChargeCatalogue().getId());
        recurringCharge.setServiceChargeCatalogue(serviceChargeCatalogue);
        return recurringChargeRepository.save(recurringCharge);
    }

    public RecurringCharge update(Integer id, RecurringCharge recurringCharge) {
        LOG.debug("Request to update Recurring Charge : {}", recurringCharge);
        RecurringCharge rc;
        try {
            final RecurringCharge existingItem = recurringChargeRepository.getOne(id);
            ModelUtils.merge(recurringCharge, existingItem, "id");
            final Account account = accountRepository.getOne(recurringCharge.getAccount().getId());
            existingItem.setAccount(account);
            final ServiceChargeCatalogue serviceChargeCatalogue = serviceChargeCatalogueRepository.getOne(recurringCharge.getServiceChargeCatalogue().getId());
            existingItem.setServiceChargeCatalogue(serviceChargeCatalogue);
            rc = recurringChargeRepository.save(existingItem);
        } catch (RuntimeException e) {
            throw ExceptionFactory.persistenceDataManagement(e, ErrorConstants.ERR_UPDATE_NO_LONGER_EXISTS);
        }
        return rc;
    }

    @Transactional(readOnly = true)
    public Page<RecurringCharge> findAllByFilters(final String textSearch, final String status, final Integer account, final Pageable pageable) {
        LOG.debug("Request to find all Recurring Charge by filters: textSearch: {}, account id: {}, status: {}", textSearch, account, status);
        final FiltersSpecification.Builder filterBuilder = new FiltersSpecification.Builder().lookFor(textSearch);

        if (status != null) {
            if (status.equals("near_end_date")) {
                filterBuilder.restrictOn(Filter.greaterThanOrEqualTo(END_DATE, LocalDateTime.now()));
                filterBuilder.restrictOn(Filter.lessThanOrEqualTo(END_DATE, LocalDateTime.now().plusDays(30)));
            }
            else if (status.equals("out_of_date")) {
                filterBuilder.restrictOn(Filter.lessThan(END_DATE, LocalDateTime.now()));
            }
        }

        if (account != null) {
            filterBuilder.restrictOn(JoinFilter.equal("account", "id", account));
        }
        return recurringChargeRepository.findAll(filterBuilder.build(), pageable);
    }

    @Transactional(readOnly = true)
    public RecurringCharge getOne (final Integer id) {
        LOG.debug("Request to get one Recurring Charge by id : {}", id);
        return recurringChargeRepository.getOne (id);
    }

    public Set<RecurringCharge> findChargesIncludedInAccountInvoiceForPeriod (
           Integer accountId,
           LocalDateTime invoicedFromInclusive,
           LocalDateTime invoicedToExclusive) {
        return recurringChargeRepository.findChargesIncludedInAccountInvoiceForPeriod (accountId, invoicedFromInclusive, invoicedToExclusive);
    }

    public List<RecurringCharge> findChargesNotIncludedInAccountInvoiceForPeriod (
            Integer accountId,
            LocalDateTime invoicedFromInclusive,
            LocalDateTime invoicedToExclusive,
            Integer externalChargeCategoryId,
            final List <InvoiceCategory> invoiceCategories) {
        final List <String> catList = invoiceCategories == null ? null : invoiceCategories.stream()
                .filter (x->x != null)
                .map (x->x.toValue())
                .collect(Collectors.toList());
        if (catList != null && !catList.isEmpty() && externalChargeCategoryId != null)
            return recurringChargeRepository.findChargesNotIncludedInAccountInvoiceForPeriodByExternalChargeCategory(
                accountId, invoicedFromInclusive, invoicedToExclusive, externalChargeCategoryId, catList);
        else if (catList != null && !catList.isEmpty())
            return recurringChargeRepository.findChargesNotIncludedInAccountInvoiceForPeriod(
                accountId, invoicedFromInclusive, invoicedToExclusive, catList);
        else
            return new ArrayList<>();
    }



    public void delete(Integer id) {
        LOG.debug("Request to delete Recurring Charge id : {}", id);
        try {
            recurringChargeRepository.delete(id);
        } catch (RuntimeException e) {
            throw ExceptionFactory.persistenceDataManagement(e, ErrorConstants.ERR_DELETE_NO_LONGER_EXISTS);
        }
    }

    public long countAll() {
        return recurringChargeRepository.count();
    }
}
