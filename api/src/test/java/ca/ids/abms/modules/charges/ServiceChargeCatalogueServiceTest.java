package ca.ids.abms.modules.charges;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import ca.ids.abms.modules.charges.ServiceChargeCatalogue;
import ca.ids.abms.modules.charges.ServiceChargeCatalogueRepository;
import ca.ids.abms.modules.charges.ServiceChargeCatalogueService;

import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

public class ServiceChargeCatalogueServiceTest {

    private ServiceChargeCatalogueRepository serviceChargeCatalogueRepository;
    private ServiceChargeCatalogueService serviceChargeCatalogueService;

    @Test
    public void createServiceChargeCatalogue() throws Exception {
        ServiceChargeCatalogue serviceChargeCatalogue = new ServiceChargeCatalogue();
        serviceChargeCatalogue.setCategory("category");
        when(serviceChargeCatalogueRepository.save(any(ServiceChargeCatalogue.class)))
                .thenReturn(serviceChargeCatalogue);

        ServiceChargeCatalogue result = serviceChargeCatalogueService.create(serviceChargeCatalogue);
        assertThat(result.getCategory()).isEqualTo(serviceChargeCatalogue.getCategory());
    }

    @Test
    public void deleteUser() throws Exception {
        serviceChargeCatalogueService.delete(1);
        verify(serviceChargeCatalogueRepository).delete(any(Integer.class));
    }

    @Test
    public void getAllServiceChargeCatalogues() throws Exception {
        List<ServiceChargeCatalogue> serviceChargeCatalogues = Collections.singletonList(new ServiceChargeCatalogue());

        when(serviceChargeCatalogueRepository.findAll(any(Pageable.class)))
                        .thenReturn(new PageImpl<>(serviceChargeCatalogues));

        Page<ServiceChargeCatalogue> results = serviceChargeCatalogueService.findAll(mock(Pageable.class));

        assertThat(results.getTotalElements()).isEqualTo(serviceChargeCatalogues.size());
    }

    @Test
    public void getServiceChargeCatalogueById() throws Exception {
        ServiceChargeCatalogue serviceChargeCatalogue = new ServiceChargeCatalogue();
        serviceChargeCatalogue.setId(1);

        when(serviceChargeCatalogueRepository.getOne(any())).thenReturn(serviceChargeCatalogue);

        ServiceChargeCatalogue result = serviceChargeCatalogueService.getOne(1);
        assertThat(result).isEqualTo(serviceChargeCatalogue);
    }

    @Before
    public void setup() {
        serviceChargeCatalogueRepository = mock(ServiceChargeCatalogueRepository.class);
        serviceChargeCatalogueService = new ServiceChargeCatalogueService(serviceChargeCatalogueRepository);
    }

    @Test
    public void updateServiceChargeCatalogue() throws Exception {
        ServiceChargeCatalogue existingServiceChargeCatalogue = new ServiceChargeCatalogue();
        existingServiceChargeCatalogue.setCategory("category");

        ServiceChargeCatalogue serviceChargeCatalogue = new ServiceChargeCatalogue();
        serviceChargeCatalogue.setCategory("new_category");

        when(serviceChargeCatalogueRepository.getOne(any())).thenReturn(existingServiceChargeCatalogue);

        when(serviceChargeCatalogueRepository.save(any(ServiceChargeCatalogue.class)))
                .thenReturn(existingServiceChargeCatalogue);

        ServiceChargeCatalogue result = serviceChargeCatalogueService.update(1, serviceChargeCatalogue);

        assertThat(result.getCategory()).isEqualTo("new_category");
    }
}
