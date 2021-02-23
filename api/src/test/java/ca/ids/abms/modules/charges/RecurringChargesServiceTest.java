package ca.ids.abms.modules.charges;

import ca.ids.abms.config.db.FiltersSpecification;
import ca.ids.abms.modules.accounts.Account;
import ca.ids.abms.modules.accounts.AccountRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class RecurringChargesServiceTest {

    private RecurringChargeService recurringChargeService;
    private RecurringChargeRepository recurringChargeRepository;
    private AccountRepository accountRepository;
    private ServiceChargeCatalogueRepository serviceChargeCatalogueRepository;

    @Before
    public void setup() {
        recurringChargeRepository = mock(RecurringChargeRepository.class);
        accountRepository = mock(AccountRepository.class);
        serviceChargeCatalogueRepository = mock(ServiceChargeCatalogueRepository.class);
        recurringChargeService = new RecurringChargeService(recurringChargeRepository, accountRepository,
            serviceChargeCatalogueRepository);
    }

    private Account buildAccount(Integer id) {
        Account account = new Account();
        account.setId(id);
        return account;
    }

    private ServiceChargeCatalogue buildServiceChargeCatalogue(Integer id) {
        ServiceChargeCatalogue serviceChargeCatalogue = new ServiceChargeCatalogue();
        serviceChargeCatalogue.setId(id);
        return serviceChargeCatalogue;
    }

    @Test
    public void createRecurringCharge() {
        when(accountRepository.getOne(eq(1))).thenReturn(buildAccount(1));
        when(serviceChargeCatalogueRepository.getOne(2)).thenReturn(buildServiceChargeCatalogue(2));


        RecurringCharge recurringCharge = new RecurringCharge();
        recurringCharge.setAccount(buildAccount(1));
        recurringCharge.setServiceChargeCatalogue(buildServiceChargeCatalogue(2));

        when(recurringChargeRepository.save(recurringCharge))
            .thenReturn(recurringCharge);

        RecurringCharge results = recurringChargeService.create(recurringCharge);

        assertThat(results).isEqualTo(recurringCharge);
    }

    @Test
    public void updateRecurringCharge() {
        when(accountRepository.getOne(eq(1))).thenReturn(buildAccount(1));
        when(serviceChargeCatalogueRepository.getOne(2)).thenReturn(buildServiceChargeCatalogue(2));

        RecurringCharge recurringCharge = new RecurringCharge();
        recurringCharge.setAccount(buildAccount(1));
        recurringCharge.setServiceChargeCatalogue(buildServiceChargeCatalogue(2));
        recurringCharge.setId(1);

        when(recurringChargeRepository.save(recurringCharge))
            .thenReturn(recurringCharge);
        when(recurringChargeRepository.getOne(recurringCharge.getId()))
            .thenReturn(recurringCharge);

        RecurringCharge results = recurringChargeService.update(recurringCharge.getId(), recurringCharge);

        assertThat(results).isEqualTo(recurringCharge);
    }

    @Test
    public void deleteRecurringCharge() {

        RecurringCharge recurringCharge = new RecurringCharge();
        recurringCharge.setId(1);
        recurringChargeService.delete(recurringCharge.getId());
        verify(recurringChargeRepository).delete(any(Integer.class));
    }

    @Test
    public void getAllRecurringCharges() {

        Account account = new Account();
        account.setId(1);

        List<RecurringCharge> recurringCharges = new ArrayList<>(1);
        RecurringCharge recurringCharge = new RecurringCharge();
        recurringCharge.setId(1);
        recurringCharge.setAccount(account);

        recurringCharges.add(recurringCharge);

        when(recurringChargeRepository.findAll(any(FiltersSpecification.class), any(Pageable.class)))
            .thenReturn(new PageImpl<>(recurringCharges));

        Page<RecurringCharge> results = recurringChargeService.findAllByFilters("test", "all", 1, mock(Pageable.class));

        assertThat(results.getTotalElements()).isEqualTo(recurringCharges.size());
    }

    @Test
    public void getAllRecurringChargesByAccount() {

        Account account1 = new Account();
        account1.setId(1);

        List<RecurringCharge> recurringCharges = new ArrayList<>();

        RecurringCharge recurringCharge1 = new RecurringCharge();
        recurringCharge1.setId(1);
        recurringCharge1.setAccount(account1);
        recurringCharges.add(recurringCharge1);

        when(recurringChargeRepository.findAll(any(FiltersSpecification.class), any(Pageable.class)))
            .thenReturn(new PageImpl<>(new ArrayList<>()));

        Page<RecurringCharge> results = recurringChargeService.findAllByFilters("test", null, 8, mock(Pageable.class));
        assertThat(results.getTotalElements()).isEqualTo(0);


        when(recurringChargeRepository.findAll(any(FiltersSpecification.class), any(Pageable.class)))
            .thenReturn(new PageImpl<>(recurringCharges));

        results = recurringChargeService.findAllByFilters("test", null, 1, mock(Pageable.class));
        assertThat(results.getTotalElements()).isEqualTo(1);
    }

    @Test
    public void getAllRecurringChargesWithBadStatus() throws Exception {

        List<RecurringCharge> recurringCharges = new ArrayList<>(1);
        String status = "bad_filter";

        RecurringCharge recurringCharge = new RecurringCharge();
        recurringCharge.setId(1);

        recurringCharges.add(recurringCharge);

        Page<RecurringCharge> recurringChargesPage = new PageImpl<>(recurringCharges);

        when(recurringChargeRepository.findAll(any(FiltersSpecification.class), any(Pageable.class)))
            .thenReturn(recurringChargesPage);

        Page<RecurringCharge> results = recurringChargeService.findAllByFilters("", status, null, mock(Pageable.class));
        assertThat(results.getTotalElements()).isEqualTo(recurringChargesPage.getContent().size());
    }

}
