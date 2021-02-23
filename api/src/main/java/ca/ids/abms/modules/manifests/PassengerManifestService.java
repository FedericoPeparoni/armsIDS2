package ca.ids.abms.modules.manifests;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import ca.ids.abms.config.error.ErrorConstants;
import ca.ids.abms.config.error.ExceptionFactory;
import ca.ids.abms.modules.aircraft.AircraftType;
import ca.ids.abms.modules.aircraft.AircraftTypeRepository;
import ca.ids.abms.modules.util.models.ModelUtils;

@Service
@Transactional
public class PassengerManifestService {

    private PassengerManifestRepository passengerManifestRepository;
    private AircraftTypeRepository aircraftTypeRepository;

    public PassengerManifestService(PassengerManifestRepository passengerManifestRepository,
                                    AircraftTypeRepository aircraftTypeRepository) {
        this.passengerManifestRepository = passengerManifestRepository;
        this.aircraftTypeRepository = aircraftTypeRepository;
    }

    public PassengerManifest create(PassengerManifest item) {
        return passengerManifestRepository.saveAndFlush(item);
    }

    public PassengerManifest update(Integer id, PassengerManifest item) {
        PassengerManifest pm = null;
        try {
            final PassengerManifest existingItem = passengerManifestRepository.getOne(id);
            final AircraftType aircraftTypeRequiredDto = item.getAircraftType();
            if (aircraftTypeRequiredDto != null) {
                final AircraftType aircraftType = aircraftTypeRepository.getOne(aircraftTypeRequiredDto.getId());
                existingItem.setAircraftType(aircraftType);
            }
            ModelUtils.merge(item, existingItem, "documentNumber", "aircraftType", "passengerManifestImage");
            pm = passengerManifestRepository.saveAndFlush(existingItem);
        } catch (RuntimeException e) {
            throw ExceptionFactory.persistenceDataManagement(e,ErrorConstants.ERR_UPDATE_NO_LONGER_EXISTS);
        }
        return pm;
    }

    public PassengerManifest uploadImage(final Integer id, final MultipartFile image) throws IOException {
        final PassengerManifest existingItem = passengerManifestRepository.getOne(id);
        final byte[] file = Base64.getDecoder().decode(image.getBytes());
        existingItem.setPassengerManifestImage(file);
        existingItem.setPassengerManifestImageType(image.getContentType());
        passengerManifestRepository.saveAndFlush(existingItem);
        return passengerManifestRepository.getOne(id);
    }

    @Transactional(readOnly = true)
    public PassengerManifest getOne(Integer id) {
        return passengerManifestRepository.getOne(id);
    }

    @Transactional(readOnly = true)
    public List<Integer> getOrphanManifests() {
        return passengerManifestRepository.getOrphanPassengerManifests();
    }

    @Transactional(readOnly = true)
    public Page<PassengerManifest> findAll(Pageable pageable, boolean filtered) {
        Page<PassengerManifest> manifests;

        /* Set the default sorting */
        if (pageable.getSort() == null) {
            final Sort sortingOpts = new Sort(
                new Sort.Order(Sort.Direction.DESC, "dateOfFlight"),
                new Sort.Order(Sort.Direction.ASC, "flightId"));
            pageable = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), sortingOpts);
        }
        if (filtered) {
            /* Retrieve a list of manifest id to filter the orphan manifests through a native query because Hibernate
                doesn't support the join between two tables without a path of relationship, and Spring doesn't support
                any pageable entity with native queries yet.
             */
            final List<Integer> idList = passengerManifestRepository.getOrphanPassengerManifests();
             manifests = passengerManifestRepository
                .findAll(OrphanPassengerManifestFilter.isIn(idList), pageable);
        } else {
            manifests = passengerManifestRepository
                .findAll(pageable);
        }
        return manifests;
    }

    public void delete(Integer id) {
        try {
            passengerManifestRepository.delete(id);
        } catch (RuntimeException e) {
            throw ExceptionFactory.persistenceDataManagement(e,ErrorConstants.ERR_DELETE_NO_LONGER_EXISTS);
        }
    }
}
