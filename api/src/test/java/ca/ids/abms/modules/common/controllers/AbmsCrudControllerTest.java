package ca.ids.abms.modules.common.controllers;

import ca.ids.abms.modules.common.entities.AbmsCrudEntity;
import ca.ids.abms.modules.common.entities.AbmsCrudEntityTest;
import ca.ids.abms.modules.common.mappers.AbmsCrudMapper;
import ca.ids.abms.modules.common.services.AbmsCrudService;
import ca.ids.abms.modules.reports2.common.ReportDocumentCreator;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URISyntaxException;
import java.util.Collections;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@SuppressWarnings("unchecked")
public class AbmsCrudControllerTest {

    private AbmsCrudEntity<Integer> entity;
    private AbmsCrudMapper<AbmsCrudEntity<Integer>, AbmsCrudEntity<Integer>, AbmsCrudEntity<Integer>> mapper;
    private AbmsCrudService<AbmsCrudEntity<Integer>, Integer> service;
    private AbmsCrudController<AbmsCrudEntity<Integer>, AbmsCrudEntity<Integer>, AbmsCrudEntity<Integer>, Integer> controller;
    private final ReportDocumentCreator reportDocumentCreator;
    private final String fileName;
    private final Class clazz;

    public AbmsCrudControllerTest() {
        this.reportDocumentCreator = null;
        this.fileName = null;
        this.clazz = null;
    }

    @Before
    public void setup() {
        entity = AbmsCrudEntityTest.mockEntity();
        mapper = mock(AbmsCrudMapper.class);
        service = mock(AbmsCrudService.class);
        controller = mockController(mapper, service, reportDocumentCreator, fileName, clazz);

        // general mock entity to view when mapping
        when(mapper.toViewModel(entity)).thenReturn(entity);
        when(mapper.toModel(entity)).thenReturn(entity);
    }

    @Test
    public void getPageTest() {

        Page<AbmsCrudEntity<Integer>> page = new PageImpl(Collections.singletonList(entity));

        // mock single entity result when find all with any parameters
        when(service.findAll(isNull(String.class), any(Pageable.class)))
            .thenReturn(page);

        // mock single entity result when mapping page to list
        when(mapper.toViewModel(page))
            .thenReturn(Collections.singletonList(entity));

        // get page by search and peageable
        ResponseEntity<?> result = controller
            .getPage(null, mock(Pageable.class), false );

        // verify that result is not null, status OK, and content body has one entity
        assertThat(result).isNotNull();
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isNotNull();
        assertThat(page.getContent()).isNotNull();
        assertThat(page.getContent().size()).isEqualTo(1);
        assertThat(page.getContent().get(0)).isEqualTo(entity);
    }

    @Test
    public void getOneTest() {

        // mock entity result when find one with specific id parameter
        when(service.findOne(entity.getId())).thenReturn(entity);

        // get one by entity id
        ResponseEntity<AbmsCrudEntity<Integer>> result = controller.getOne(entity.getId());

        // verify that result is not null, status OK, and body equal to entity
        assertThat(result).isNotNull();
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody()).isEqualTo(entity);
    }

    @Test
    public void createTest() throws URISyntaxException {

        // mock entity result when create entity
        when(service.create(entity)).thenReturn(entity);

        // create entity by object
        ResponseEntity<AbmsCrudEntity<Integer>> result = controller.create(entity);

        // verify that result is not null, status CREATED, and body equal to entity
        assertThat(result).isNotNull();
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody()).isEqualTo(entity);
    }

    @Test
    public void updateTest() {

        // mock entity result when update entity
        when(service.update(entity.getId(), entity)).thenReturn(entity);

        // update entity by id and object
        ResponseEntity<AbmsCrudEntity<Integer>> result = controller.update(entity.getId(), entity);

        // verify that result is not null, status OK, and body equal to entity
        assertThat(result).isNotNull();
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody()).isEqualTo(entity);
    }

    @Test
    public void deleteTest() {

        // delete entity by id
        ResponseEntity result = controller.delete(entity.getId());

        // assert that service delete method called once with appropriate id
        verify(service, times(1)).remove(entity.getId());

        // verify that result is not null, status OK, and body is null
        assertThat(result).isNotNull();
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isNull();
    }

    private static AbmsCrudController<AbmsCrudEntity<Integer>, AbmsCrudEntity<Integer>, AbmsCrudEntity<Integer>, Integer> mockController(
        final AbmsCrudMapper<AbmsCrudEntity<Integer>, AbmsCrudEntity<Integer>, AbmsCrudEntity<Integer>> mapper,
        final AbmsCrudService<AbmsCrudEntity<Integer>, Integer> service,
        final ReportDocumentCreator reportDocumentCreator,
        final String fileName,
        final Class clazz) {

        return new AbmsCrudController<AbmsCrudEntity<Integer>, AbmsCrudEntity<Integer>, AbmsCrudEntity<Integer>, Integer>(
            "/api/mock-endpoint", mapper, service, reportDocumentCreator, fileName, clazz) {

            @Override
            public ResponseEntity<?> getPage(String search, Pageable pageable, Boolean csvExport) {
                return super.doGetPage(search, pageable, false);
            }

            @Override
            public ResponseEntity<AbmsCrudEntity<Integer>> getOne(Integer id) {
                return super.doGetOne(id);
            }

            @Override
            public ResponseEntity<AbmsCrudEntity<Integer>> create(AbmsCrudEntity<Integer> viewModel) throws URISyntaxException {
                return super.doCreate(viewModel);
            }

            @Override
            public ResponseEntity<AbmsCrudEntity<Integer>> update(Integer id, AbmsCrudEntity<Integer> viewModel) {
                return super.doUpdate(id, viewModel);
            }

            @Override
            public ResponseEntity delete(Integer id) {
                return super.doDelete(id);
            }
        };
    }
}
