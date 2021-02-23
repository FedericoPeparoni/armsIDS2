package ca.ids.abms.modules.common.services;

import ca.ids.abms.config.db.ABMSRepository;
import ca.ids.abms.config.db.FiltersSpecification;
import ca.ids.abms.config.error.CustomParametrizedException;
import ca.ids.abms.config.error.ErrorConstants;
import ca.ids.abms.modules.common.entities.AbmsCrudEntity;
import ca.ids.abms.modules.common.entities.AbmsCrudEntityTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@SuppressWarnings("unchecked")
public class AbmsCrudServiceTest {

    private AbmsCrudEntity<Integer> entity;
    private ABMSRepository<AbmsCrudEntity<Integer>, Integer> repository;
    private AbmsCrudService<AbmsCrudEntity<Integer>, Integer> service;

    @Before
    public void setup() {
        entity = AbmsCrudEntityTest.mockEntity();
        repository = mock(ABMSRepository.class);
        service = new AbmsCrudService(repository) {};
    }

    @Test
    public void findAllTest() {

        // mock single entity result when find all with any parameters
        when(repository.findAll(any(FiltersSpecification.class), any(Pageable.class)))
            .thenReturn(new PageImpl(Collections.singletonList(entity)));

        // find all entities by page
        Page<AbmsCrudEntity<Integer>> result = service
            .findAll(null, mock(Pageable.class));

        // verify that not null and one entity is returned
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0)).isEqualTo(entity);
    }

    @Test
    public void findOneTest() {

        // mock entity result when find one with specific id parameter
        when(repository.findOne(entity.getId()))
            .thenReturn(entity);

        // find one entity
        AbmsCrudEntity<Integer> result = service
            .findOne(entity.getId());

        // verify that not null and equal to entity
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(entity);
    }

    @Test
    public void createTest() {

        // mock entity save result when supplied object
        when(repository.save(entity))
            .thenReturn(entity);

        // save entity
        AbmsCrudEntity<Integer> result = service
            .create(entity);

        // verify that not null and equal to entity
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(result);
    }

    @Test
    public void updateTest() {

        // mock entity save result when supplied object
        when(repository.save(entity))
            .thenReturn(entity);

        // mock entity getOne result when supplied id
        when(repository.getOne(entity.getId()))
            .thenReturn(entity);

        // update entity
        AbmsCrudEntity<Integer> result = service
            .update(entity.getId(), entity);

        // verify that not null and equal to entity
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(result);
    }

    @Test
    public void updateNotFoundTest() {

        // mock entity getOne result when supplied id
        when(repository.getOne(entity.getId()))
            .thenReturn(null);

        // update entity, fail test if successful
        try {
            service.update(entity.getId(), entity);
            expectedCustomParametrizedException(ErrorConstants.ERR_UPDATE_NO_LONGER_EXISTS);
        } catch (CustomParametrizedException ex) {
            if (!ex.getDescription().equals(ErrorConstants.ERR_UPDATE_NO_LONGER_EXISTS.toValue()))
                expectedCustomParametrizedException(ErrorConstants.ERR_UPDATE_NO_LONGER_EXISTS);
            // else ignore, this is the correct behaviour
        }
    }

    @Test
    public void removeTest() {

        // mock entity getOne result when supplied id
        when(repository.getOne(entity.getId()))
            .thenReturn(entity);

        // remove entity by id
        service.remove(entity.getId());

        // verify that repository delete method was called once with entity id
        verify(repository, times(1))
            .delete(entity.getId());
    }

    @Test
    public void removeNotFoundTest() {

        // mock entity getOne result when supplied id
        when(repository.getOne(entity.getId()))
            .thenReturn(null);

        // update entity, fail test if successful
        try {
            service.remove(entity.getId());
            expectedCustomParametrizedException(ErrorConstants.ERR_DELETE_NO_LONGER_EXISTS);
        } catch (CustomParametrizedException ex) {
            if (!ex.getDescription().equals(ErrorConstants.ERR_DELETE_NO_LONGER_EXISTS.toValue()))
                expectedCustomParametrizedException(ErrorConstants.ERR_DELETE_NO_LONGER_EXISTS);
            // else ignore, this is the correct behaviour
        }
    }

    private void expectedCustomParametrizedException(ErrorConstants description) {
        fail("Expected " + CustomParametrizedException.class + " thrown with description : "
            + description);
    }
}
