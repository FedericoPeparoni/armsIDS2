package ca.ids.abms.modules.common.controllers;

import ca.ids.abms.modules.common.entities.AbmsCrudEntity;
import ca.ids.abms.modules.common.mappers.AbmsCrudMapper;
import ca.ids.abms.modules.common.services.AbmsCrudService;
import ca.ids.abms.modules.reports2.common.ReportDocument;
import ca.ids.abms.modules.reports2.common.ReportDocumentCreator;
import ca.ids.abms.modules.util.models.PageImplCustom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@SuppressWarnings("squid:S1452")
public abstract class AbmsViewController<T extends AbmsCrudEntity<I>, V, E, I extends Serializable> extends MediaDocumentComponent {

    private final String endpoint;
    private final AbmsCrudMapper<T, V, E> mapper;
    private final AbmsCrudService<T, I> service;
    private final ReportDocumentCreator reportDocumentCreator;
    private final String fileName;
    private final Class<E> clazz;

    public AbmsViewController(final String endpoint,
                              final AbmsCrudMapper<T, V, E> mapper,
                              final AbmsCrudService<T, I> service,
                              final ReportDocumentCreator reportDocumentCreator,
                              final String fileName,
                              final Class<E> clazz) {
        this.endpoint = endpoint;
        this.mapper = mapper;
        this.service = service;
        this.reportDocumentCreator = reportDocumentCreator;
        this.fileName = fileName;
        this.clazz = clazz;
    }

    public abstract ResponseEntity<?> getPage(final String search, final Pageable pageable, final Boolean csvExport);

    public abstract ResponseEntity<V> getOne(final I id);

    protected ResponseEntity<?> doGetPage(final String search, final Pageable pageable, final Boolean csvExport) {
        Page<T> page = service.findAll(search, pageable);

        if (csvExport != null && csvExport) {
            final List<T> list = page.getContent();
            final List<E> csvExportModel = mapper.toCsvModel(list);
            ReportDocument report = reportDocumentCreator.createCsvDocument(fileName, csvExportModel, clazz, true);
            return doCreateBinaryResponse(report);
        } else {
            return ResponseEntity.ok().body(new PageImplCustom<>(
                mapper.toViewModel(page), pageable, page.getTotalElements(), service.countAll()));
        }
    }

    protected ResponseEntity<V> doGetOne(final I id) {
        return ResponseEntity.ok().body(mapper.toViewModel(service.findOne(id)));
    }

    protected ResponseEntity<V> doCreate(final V viewModel) throws URISyntaxException {
        final T result = service.create(mapper.toModel(viewModel));
        return ResponseEntity.created(new URI(endpoint + result.getId()))
            .body(mapper.toViewModel(result));
    }
}
