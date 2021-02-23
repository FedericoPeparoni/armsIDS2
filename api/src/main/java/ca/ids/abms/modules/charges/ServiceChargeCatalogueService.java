package ca.ids.abms.modules.charges;

import java.util.ArrayList;
import java.util.List;

import ca.ids.abms.config.db.FiltersSpecification;
import ca.ids.abms.config.error.ErrorConstants;
import ca.ids.abms.config.error.ExceptionFactory;
import ca.ids.abms.modules.common.enumerators.InvoiceCategory;
import ca.ids.abms.modules.util.models.ModelUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ServiceChargeCatalogueService {

    private static final Logger LOG = LoggerFactory.getLogger(ServiceChargeCatalogueService.class);
    private ServiceChargeCatalogueRepository serviceChargeCatalogueRepository;
    
    public ServiceChargeCatalogueService(ServiceChargeCatalogueRepository aServiceChargeCatalogueRepository) {
        serviceChargeCatalogueRepository = aServiceChargeCatalogueRepository;
    }

    public ServiceChargeCatalogue create(ServiceChargeCatalogue item) {
        LOG.debug("Request to save Service Charge Catalogue : {}", item);
        return serviceChargeCatalogueRepository.save(item);
    }

    public void delete(Integer id) {
        LOG.debug("Request to delete Service Charge Catalogue : {}", id);
        try {
            serviceChargeCatalogueRepository.delete(id);
        } catch (RuntimeException e) {
            throw ExceptionFactory.persistenceDataManagement(e, ErrorConstants.ERR_DELETE_NO_LONGER_EXISTS);
        }
    }

    @Transactional(readOnly = true)
    public Page<ServiceChargeCatalogue> findAll(Pageable pageable) {
        LOG.debug("Request to get all Service Charge Catalogue");
        return serviceChargeCatalogueRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<ServiceChargeCatalogue> findAll(String textFilter, Pageable pageable) {
        LOG.debug("Request to get Service Charge Catalogue by text search. Search: {}", textFilter);
        final FiltersSpecification.Builder filterBuilder = new FiltersSpecification.Builder().lookFor(textFilter);
        return serviceChargeCatalogueRepository.findAll(filterBuilder.build(), pageable);
    }

    @Transactional(readOnly = true)
    public ServiceChargeCatalogue getOne(Integer id) {
        LOG.debug("Request to get Service Charge Catalogue by id : {}", id);
        return serviceChargeCatalogueRepository.getOne(id);
    }

    public ServiceChargeCatalogue update(Integer id, ServiceChargeCatalogue item) {
        LOG.debug("Request to update Service Charge Catalogue : {}", item);
        ServiceChargeCatalogue scc;
        try {
            final ServiceChargeCatalogue existingItem = serviceChargeCatalogueRepository.getOne(id);
            ModelUtils.merge(item, existingItem, "id");
            scc = serviceChargeCatalogueRepository.save(existingItem);
        } catch (RuntimeException e) {
            throw ExceptionFactory.persistenceDataManagement(e, ErrorConstants.ERR_UPDATE_NO_LONGER_EXISTS);
        }
        return scc;
    }

    public List<ServiceChargeCatalogue> findChargesForCategories (final List <InvoiceCategory> categories) {
        final List <String> categoryStrList = new ArrayList<>();
        if (categories != null && !categories.isEmpty()) {
            for (final InvoiceCategory cat: categories) {
                if (cat != null) {
                    categoryStrList.add (cat.toValue());
                }
            }
            if (!categoryStrList.isEmpty()) {
                return serviceChargeCatalogueRepository.findChargesOfCategories (categoryStrList);
            }
        }
        return new ArrayList<>();
    }


    public long countAll() {
        return serviceChargeCatalogueRepository.count();
    }
}
