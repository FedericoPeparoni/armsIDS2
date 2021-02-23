package ca.ids.abms.modules.charges;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ExternalChargeCategoryService {

    private final ExternalChargeCategoryRepository externalChargeCategoryRepository;

    public ExternalChargeCategoryService(final ExternalChargeCategoryRepository externalChargeCategoryRepository) {
        this.externalChargeCategoryRepository = externalChargeCategoryRepository;
    }

    @Transactional(readOnly = true)
    public List<ExternalChargeCategory> findAll() {
        return externalChargeCategoryRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<ExternalChargeCategory> findAllNonAviation() {
        return externalChargeCategoryRepository.findAllNonAviation();
    }

    @Transactional(readOnly = true)
    public ExternalChargeCategory findByName(final String name) {
        return externalChargeCategoryRepository.findByName(name);
    }
}
