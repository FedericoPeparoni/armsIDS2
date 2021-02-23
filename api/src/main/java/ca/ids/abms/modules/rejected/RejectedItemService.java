package ca.ids.abms.modules.rejected;

import ca.ids.abms.config.db.Filter;
import ca.ids.abms.config.db.FiltersSpecification;
import ca.ids.abms.config.error.RejectedException;
import ca.ids.abms.modules.util.models.ModelUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;

@Service
@Transactional
public class RejectedItemService {

    private final Logger log = LoggerFactory.getLogger(RejectedItemService.class);

    private static final String KEY_RECORD_TYPE = "recordType";

    private static final String KEY_STATUS = "status";

    private static final String KEY_FILE_NAME = "fileName";

    private static final String KEY_ORIGINATOR = "originator";

    private static final String KEY_REJECTED_DATE_TIME = "rejectedDateTime";

    private RejectedItemRepository rejectedItemRepository;

    public RejectedItemService(RejectedItemRepository rejectedItemRepository) {
        this.rejectedItemRepository = rejectedItemRepository;
    }

    @Transactional(readOnly = true)
    public RejectedItem getOne(final Integer id) {
        assert id != null;
        return rejectedItemRepository.getOne(id);
    }

    @Transactional(readOnly = true)
    public Page<RejectedItem> findAllRejectedItemsByFilters(
        final String textSearch,
        final String filterByRecordType,
        final LocalDate filterByRejectedStartDate,
        final LocalDate filterByRejectedEndDate,
        final String filterByStatus,
        final String filterByOriginator,
        final String filterByFileName,
        final Pageable pageable) {

        final FiltersSpecification.Builder filterBuilder = new FiltersSpecification.Builder().lookFor(textSearch);

        if (filterByRecordType != null) {
            filterBuilder.restrictOn(Filter.equals(KEY_RECORD_TYPE, filterByRecordType));
        }

        if (filterByStatus != null) {
            filterBuilder.restrictOn(Filter.equals(KEY_STATUS, filterByStatus));
        }

        if (filterByOriginator != null) {
            filterBuilder.restrictOn(Filter.equals(KEY_ORIGINATOR, filterByOriginator));
        }

        if (filterByFileName != null) {
            filterBuilder.restrictOn(Filter.equals(KEY_FILE_NAME, filterByFileName));
        }

        LocalDateTime startAt = null;
        LocalDateTime endAt = null;
        if (filterByRejectedStartDate != null || filterByRejectedEndDate != null) {

            if (filterByRejectedStartDate == null) {
                startAt = (LocalDateTime.now()).minusYears(1000);
            } else {
                startAt = filterByRejectedStartDate.atStartOfDay();
            }
            if (filterByRejectedEndDate == null) {
                endAt = (LocalDateTime.now()).plusYears(1000);
            } else {
                endAt = filterByRejectedEndDate.atTime(LocalTime.MAX);
            }
            filterBuilder.restrictOn(Filter.included(KEY_REJECTED_DATE_TIME, startAt, endAt));
        }

        log.debug("Attempting to find rejected items by filters. Search: {}, Record Type: {}, Status: {} Originator: {} File Name: {} and Dates: {}, {}",
            textSearch, filterByRecordType, filterByStatus, filterByOriginator, filterByFileName, startAt, endAt);
        return rejectedItemRepository.findAll(filterBuilder.build(), pageable);
    }

    public RejectedItem create (final RejectedItemType recordType,
                                final RejectedException rejectedException,
                                final byte[] jsonText) {
        return create(RejectedItemType.FLIGHT_MOVEMENTS, rejectedException, null,
            null, null, null, jsonText);
    }

    public RejectedItem create (final RejectedItemType recordType,
                                final RejectedException rejectedException,
                                final String originator,
                                final String fileName,
                                final String rawText,
                                final String header) {
        return this.create(recordType, rejectedException, originator, fileName, rawText, header, null);
    }

    public RejectedItem create (final RejectedItemType recordType,
                                final RejectedException rejectedException,
                                final String originator,
                                final String fileName,
                                final String rawText,
                                final String header,
                                final byte[] jsonText) {
        final RejectedItem rejectedItem = new RejectedItem();
        rejectedItem.setRecordType(recordType.toValue());
        rejectedItem.setFileName(StringUtils.abbreviate(fileName, RejectedItem.FILE_NAME_SIZE));
        rejectedItem.setStatus(RejectedItemStatus.UNCORRECTED.toValue());
        rejectedItem.setRejectedReason(
            StringUtils.abbreviate(rejectedException.getReason(), RejectedItem.REJ_REASON_SIZE));
        rejectedItem.setErrorMessage(
            StringUtils.abbreviate(rejectedException.getLocalizedMessage(), RejectedItem.ERR_MESSAGE_SIZE));
        rejectedItem.setErrorDetails(
            StringUtils.abbreviate(rejectedException.getDetails(), RejectedItem.ERR_DETAILS_SIZE));
        rejectedItem.setOriginator(originator);
        rejectedItem.setRejectedDateTime(LocalDateTime.now(ZoneId.of("UTC")));
        rejectedItem.setRawText(StringUtils.abbreviate(rawText, RejectedItem.RAW_TEXT_SIZE));
        rejectedItem.setJsonText(jsonText);
        rejectedItem.setHeader(StringUtils.abbreviate(header, RejectedItem.HEADER_SIZE));

        // only save rejected item if it does not exist already, US 83967
        if (!itemExists(rejectedItem))
            return rejectedItemRepository.save(rejectedItem);
        else
            return null;
    }

    public RejectedItem update (final Integer id, final RejectedItem rejectedItem) {
        assert id != null && rejectedItem != null;
        final RejectedItem existingItem = rejectedItemRepository.getOne(id);
        ModelUtils.merge(rejectedItem, existingItem, "id");
        return rejectedItemRepository.save(existingItem);
    }

    public void delete (final Integer id) {
        assert id != null;
        rejectedItemRepository.delete(id);
    }

    private Boolean itemExists(RejectedItem rejectedItem) {
        long existingCount = rejectedItemRepository.countByRecordTypeAndRawTextAndJsonTextAndHeaderAndStatus(
            rejectedItem.getRecordType(), rejectedItem.getRawText(), rejectedItem.getJsonText(),
            rejectedItem.getHeader(), rejectedItem.getStatus());
        return existingCount > 0;
    }

    public long countAll() {
        return rejectedItemRepository.count();
    }
}
