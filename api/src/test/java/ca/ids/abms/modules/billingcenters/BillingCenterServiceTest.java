package ca.ids.abms.modules.billingcenters;

import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by s.menotti on 10/01/2017.
 */
public class BillingCenterServiceTest {
    private BillingCenterRepository billingCenterRepository;
    private BillingCenterService billingCenterService;

    @Before
    public void setup() {
        billingCenterRepository = mock(BillingCenterRepository.class);
        billingCenterService = new BillingCenterService(billingCenterRepository);
    }

    @Test
    public void createBillingCenter() throws Exception {
        BillingCenter billingCenter = new BillingCenter();
        billingCenter.setName("name");
        billingCenter.setPrefixInvoiceNumber("prefix");
        billingCenter.setInvoiceSequenceNumber(1);

        when(billingCenterRepository.save(any(BillingCenter.class))).thenReturn(billingCenter);

        BillingCenter result = billingCenterService.create(billingCenter);
        assertThat(result.getName()).isEqualTo(billingCenter.getName());
    }

    // test to see if creating a record, setting `hq` to true will render the old `hq` record as false
    @Test
    public void createBillingCenterWithHq() throws Exception {

        List<BillingCenter> billingCenterList = new ArrayList<BillingCenter>();

        BillingCenter billingCenterFormerHq = new BillingCenter();
        billingCenterFormerHq.setId(1);
        billingCenterFormerHq.setHq(true);

        BillingCenter billingCenterNewHq = new BillingCenter();
        billingCenterNewHq.setHq(true);

        billingCenterList.add(billingCenterFormerHq);
        billingCenterList.add(billingCenterNewHq);

        when(billingCenterRepository.save(any(BillingCenter.class))).thenReturn(billingCenterNewHq);
        when(billingCenterRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(billingCenterList));
        when(billingCenterRepository.findOne(1)).thenReturn(billingCenterFormerHq);
        when(billingCenterRepository.findOne(2)).thenReturn(billingCenterNewHq);
        when(billingCenterRepository.findHq()).thenReturn(billingCenterFormerHq);

        assertThat(billingCenterService.findOne(1).getHq()).isTrue();

        billingCenterService.create(billingCenterNewHq);

        assertThat(billingCenterService.findOne(2).getHq()).isTrue();
        assertThat(billingCenterService.findOne(billingCenterFormerHq.getId()).getHq()).isFalse();
    }

    // test to see if updating a record, setting `hq` to true will render the old `hq` record as false
    @Test
    public void updateBillingCenterWithHq() throws Exception {

        List<BillingCenter> billingCenterList = new ArrayList<BillingCenter>();

        BillingCenter billingCenterFormerHq = new BillingCenter();
        billingCenterFormerHq.setId(1);
        billingCenterFormerHq.setHq(true);

        BillingCenter billingCenterNewHq = new BillingCenter();
        billingCenterNewHq.setId(2);
        billingCenterNewHq.setHq(false);

        billingCenterList.add(billingCenterFormerHq);
        billingCenterList.add(billingCenterNewHq);

        when(billingCenterRepository.save(any(BillingCenter.class))).thenReturn(billingCenterNewHq);
        when(billingCenterRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(billingCenterList));
        when(billingCenterRepository.findOne(1)).thenReturn(billingCenterFormerHq);
        when(billingCenterRepository.findOne(2)).thenReturn(billingCenterNewHq);

        assertThat(billingCenterService.findOne(2).getHq()).isFalse();
        assertThat(billingCenterService.findOne(1).getHq()).isTrue();

        billingCenterNewHq.setHq(true);
        billingCenterService.update(2, billingCenterNewHq);

        assertThat(billingCenterService.findOne(2).getHq()).isTrue();
        assertThat(billingCenterService.findOne(1).getHq()).isTrue();
    }

    @Test
    public void getAllRadarSummaries() throws Exception {

        List<BillingCenter> billingCenterList = Collections.singletonList(new BillingCenter());

        when(billingCenterRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(billingCenterList));

        Page<BillingCenter> results = billingCenterService.findAll(mock(Pageable.class));

        assertThat(results.getTotalElements()).isEqualTo(billingCenterList.size());
    }

    @Test
    public void getBillingCenterById() throws Exception {
        BillingCenter billingCenter = new BillingCenter();
        billingCenter.setId(1);

        when(billingCenterRepository.findOne(1)).thenReturn(billingCenter);

        BillingCenter result = billingCenterService.findOne(1);
        assertThat(result).isEqualTo(billingCenter);
    }


    @Test
    public void updateBillingCenter() throws Exception {
        BillingCenter existingBillingCenter = new BillingCenter();
        existingBillingCenter.setName("name");

        BillingCenter updateBillingCenter = new BillingCenter();
        updateBillingCenter.setName("name1");

        when(billingCenterService.findOne(1)).thenReturn(existingBillingCenter);

        when(billingCenterRepository.save(any(BillingCenter.class))).thenReturn(existingBillingCenter);

        BillingCenter result = billingCenterService.update(1, updateBillingCenter);

        assertThat(result.getName()).isEqualTo(updateBillingCenter.getName());
    }

    @Test
    public void deleteUnspecifiedAircraftType() throws Exception {
        billingCenterService.delete(1);
        verify(billingCenterRepository).delete(any(Integer.class));
    }
}

